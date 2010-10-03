package smb_service;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ori Kotek <orikotek@gmail.com>
 * @version 1.0
 *
 */
public class SMBTrain {

	/**
	 * Initialize an SMB training object with a working set.
	 * The working set contains solved samples of term pairs along with
	 * solutions from an arbitrary number of matchers.
	 * @param lws The working set
	 */
	public SMBTrain(LearnWorkingSet lws)
	{
		m_lws = lws;
	}

	/**
	 * Initialize an SMB training object from a serialized working set.
	 * @param serializedFileName Filename of the serialized object on the file system
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public SMBTrain(String serializedFileName) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		FileInputStream fis = new FileInputStream(serializedFileName);
		ObjectInputStream in = new ObjectInputStream(fis);
		m_lws = (LearnWorkingSet)in.readObject();
		in.close();
	}

	/**
	 * A utility method to help serialize a LearnWorkingSet.
	 * @param lws The working set we wish to serialize
	 * @param serializedFileName File name of file system
	 * @throws IOException
	 */
	static public void SerializeLws(LearnWorkingSet lws, String serializedFileName) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(serializedFileName);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(lws);
		out.close();
	}
	
	/**
	 * This calculates an ensemble based on Schema Matching Boosting
	 * algorithm. This is the main method of this class. The data set
	 * is already initialized through one of the constructors.
	 * @return A HashMap which maps some or all of the input matchers ids
	 * to their weight in the ensemble.
	 */
	public HashMap<Long, Double> Train()
	{
		// 1. Reorganize the data structures to a more convenient form
		Preprocess();
		
		// 2. Remove outliers
		RemoveOutliers();
		
		// 3. Apply the Ada Boost algorithm
		AdaBoost();
		
		// 3. Reorganize the results in a convenient way to the caller
		HashMap<Long, Double> results = new HashMap<Long, Double>();
		int count = m_results.size();
		double sumOfAlpha = 0;
		for (int i = 0; i < count; ++i)
		{
			Long cid = m_results.get(i).classifierId;
			double alpha = m_results.get(i).alpha;
			if (results.containsKey(cid))
				results.put(cid, results.get(cid) + alpha);
			else
				results.put(cid, alpha);
			
			sumOfAlpha += alpha;
		}
		// Renormalize

		HashMap<Long, Double> resultsNormed = 
			new HashMap<Long, Double>();
		for (Map.Entry<Long, Double> item : results.entrySet())
		{
			resultsNormed.put(item.getKey(), item.getValue() / sumOfAlpha);
		}
		
		return resultsNormed;
	}
	
	/**
	 * For each schema pair we iterate over all matcher results. For each
	 * sample we decide if each matcher result is a positive true, a 
	 * positive false or a negative false an update the appropriate data
	 * structure.
	 */
	private void Preprocess() {
		// Prepare list of exact matches. A match is defined as a single term match in a single
		// schema pair.		
		ArrayList<Sample> samples = new ArrayList<Sample>();
		
		for (SchemaPair pair : m_lws.schemaPairList)
		{
			// First prepare mappings from each source row to term id
			// and from each target column to term id. This will save 
			// some searches later on in this method.
			
			Schema srcSchema = pair.candidateSchema;
			SimilarityMatrix em = pair.exactMatch;
			HashMap<Integer, Long> emTargetColToTerm =
				new HashMap<Integer, Long>();
			for (Map.Entry<Long, Integer> id2col : em.targetTermMap.entrySet())
			{
				emTargetColToTerm.put(id2col.getValue(), id2col.getKey());
			}
			HashMap<Integer, Long> emSourceRowToTerm =
				new HashMap<Integer, Long>();
			for (Map.Entry<Long, Integer> id2row : em.candidateTermMap.entrySet())
			{
				emSourceRowToTerm.put(id2row.getValue(), id2row.getKey());
			}
			
			// Now.. for each source term existing in this exact match..
			for (Long srcTerm : srcSchema.terms.keySet())
			{
				// Initialize maps of FP, TP and FNs.
				HashMap<Long, Double> falsePositives = new HashMap<Long, Double>();
				HashMap<Long, Double> truePositives = new HashMap<Long, Double>();
				HashMap<Long, Double> falseNegatives = new HashMap<Long, Double>();

				// We're looking in a specific source term in the exact match.
				// We're trying to determine which terms are matching that source
				// term. It can be zero (the corresponding row is all zeros) or 
				// more terms.
				ArrayList<Long> emPositiveTerms = new ArrayList<Long>();
				boolean emContains = em.candidateTermMap.containsKey(srcTerm);
				if (emContains)
				{
					double[] emRow = em.getRow(em.candidateTermMap.get(srcTerm));
					for (int ind = 0; ind < em.similarityM.columns(); ++ind)
					{
						if (emRow[ind] == 1)
							emPositiveTerms.add(emTargetColToTerm.get(ind));
					}
				}
				
				// Now that we have the true solution in hand, let's see which
				// of the matchers got it right, which got it only half right
				// and which were utterly wrong.
				for (Map.Entry<Long, SimilarityMatrix> matcherRes : 
					 pair.correspondenceSet.entrySet())
				{
					// We have a specific matcher in hand, its matrix is in cs.
					SimilarityMatrix cs = matcherRes.getValue();
					Long matcherId = matcherRes.getKey();
					
					// Map the columns to terms so it's easier to work..
					HashMap<Integer, Long> csTargetColToTerm =
						new HashMap<Integer, Long>();
					for (Map.Entry<Long, Integer> id2col : cs.targetTermMap.entrySet())
					{
						csTargetColToTerm.put(id2col.getValue(), id2col.getKey());
					}

					// Let's find out who our matcher thinks srcTerm matches to
					// and put the results in csPositiveTerms.
					ArrayList<Long> csPositiveTerms = new ArrayList<Long>();
					boolean csContains = cs.candidateTermMap.containsKey(srcTerm);
					if (csContains)
					{
						double[] csRow = cs.getRow(cs.candidateTermMap.get(srcTerm));
						for (int ind = 0; ind < cs.similarityM.columns(); ++ind)
						{
							if (csRow[ind] == 1)
								csPositiveTerms.add(csTargetColToTerm.get(ind));
						}
					}
					
					// Now a reminder.
					// emPositiveTerms - The ground truth. 
					// csPositiveTerms - The current matcher's solution.
					// If both are empty - this adds no information.
					if (emPositiveTerms.size() == 0 && csPositiveTerms.size() == 0)
					{
						truePositives.put(matcherId, 0.);
						falsePositives.put(matcherId, 0.);
						falseNegatives.put(matcherId, 0.);
					}
					// If em is not empty but cs is we have false negatives.
					else if (emPositiveTerms.size() > 0 && csPositiveTerms.size() == 0)
					{
						truePositives.put(matcherId, 0.);
						falsePositives.put(matcherId, 0.);
						falseNegatives.put(matcherId, 1.);
					}
					// If em didn't find anything but our matcher did, it is wrong.
					// It has what's called false positives.
					else if (emPositiveTerms.size() == 0 && csPositiveTerms.size() > 0)
					{
						truePositives.put(matcherId, 0.);
						falsePositives.put(matcherId, 1.);
						falseNegatives.put(matcherId, 0.);
					}
					// We get to this else clause iff both em and cs are not empty
					else 
					{
						// Now we check the containment relation between these 
						// two sets
						boolean emContainsCs = emPositiveTerms.containsAll(csPositiveTerms);
						boolean csContainsEm = csPositiveTerms.containsAll(emPositiveTerms);
						
						// Each set contains the other --> the sets are equal
						// We have true positives!
						if (csContainsEm && emContainsCs)
						{
							truePositives.put(matcherId, 1.);
							falsePositives.put(matcherId, 0.);
							falseNegatives.put(matcherId, 0.);
						}
						// CS has some terms EM doesn't (--> False positives!)
						else if (csContainsEm && !emContainsCs)
						{
							truePositives.put(matcherId, 0.);
							falsePositives.put(matcherId, 1.);
							falseNegatives.put(matcherId, 0.);
						}
						// EM has some terms CS doesn't (--> false negatives!)
						else if (!csContainsEm && emContainsCs)
						{
							truePositives.put(matcherId, 0.);
							falsePositives.put(matcherId, 0.);
							falseNegatives.put(matcherId, 1.);
						}
						// EM has some terms CS AND CS has some terms EM doesn't
						// --> Both false negatives and false positives
						else if (!csContainsEm && !emContainsCs)
						{
							truePositives.put(matcherId, 0.);
							falsePositives.put(matcherId, 1.);
							falseNegatives.put(matcherId, 1.);
						}
					}
				}
				// Wrap it up and add this data to the schema pair meta-data.
				samples.add(new Sample(truePositives, falsePositives, falseNegatives));
			}
		}
		
		// Convert the data structure to array (just for easier handling).
		m_matches = new Sample[samples.size()];
		samples.toArray(m_matches);
		
	}

	/**
	 * This method goes through all samples and filters those who doesn't
	 * have at least one matcher with the correct classification.
	 * This is made extremely easy after preprocess when we know for
	 * each matcher for which sample it has true positive vote.
	 */
	private void RemoveOutliers() 
	{
		ArrayList<Sample> targetSampleList = new ArrayList<Sample>();
		for (Sample currentMatch : m_matches)
		{
			for (Double trueMatch : currentMatch.getTruePositives().values())
			{
				if (trueMatch == 1.0)
				{
					targetSampleList.add(currentMatch);
					break;
				}
			}
		}

		m_matches = new Sample[targetSampleList.size()];
		targetSampleList.toArray(m_matches);
	}
	
	/**
	 * Performs the Ada Boost algorithm, using the method described in 
	 * "Boosting Shema Matchers", pp 6.
	 */
	private void AdaBoost()
	{
		// *** Algorithm line 3: D1(i) = 1/m ***
		
		// D corresponds to m_currentDistribution and is initialized to 
		// (1 / numOfSamples).
		int numOfSamples = m_matches.length;
		m_currentDistribution = new double[numOfSamples];
		for (int i = 0; i < numOfSamples; ++i)
			m_currentDistribution[i] = 1/(double)numOfSamples;
		m_results = new ArrayList<ChosenClassifier>();
		
		// *** Algorithm line 4: t = 1 ***
		// t corresponds to 'iteration' - one of the stop conditions
		// of the main algorithmic loop.
		int iteration = 1;
		
		// Currently we limit to 50 iterations. This should probably be 
		// stored in the configuration file in the future.
		int maxIterations = 50;
		m_epsilon = 0;
		
		// *** Algorithm line 5: repeat ***
		do
		{
			// *** Algorithm line 7: Find the classifier ht that minimizes the error ***
			HashMap<Long, Double> errors = new HashMap<Long, Double>(); // Initially zeroed
			HashMap<Long, Double> A = new HashMap<Long, Double>();
			HashMap<Long, Double> B = new HashMap<Long, Double>();
			HashMap<Long, Double> C = new HashMap<Long, Double>();
			
			// The error is calculated according to the method suggested at pp 8
			// of the paper.
			// First for each matcher calculate:
			// A - number of false positives
			// B - number of true positives
			// C - number of false negatives
			// 
			// Since it's possible that for a specific matcher there is no result
			// for a certain sample, make sure that it doesn't affect the result
			// by skipping that sample for that matcher.
			for (int sampleNum = 0; sampleNum < numOfSamples; ++sampleNum)
			{
				for (Long matcherKey : m_matches[sampleNum].getTruePositives().keySet())
				{
					if (!A.containsKey(matcherKey))
						A.put(matcherKey, 0.0);
					if (!B.containsKey(matcherKey))
						B.put(matcherKey, 0.0);
					if (!C.containsKey(matcherKey))
						C.put(matcherKey, 0.0);
					
					A.put(matcherKey, A.get(matcherKey) + m_matches[sampleNum].getFalseNegatives().get(matcherKey));
					B.put(matcherKey, B.get(matcherKey) + m_matches[sampleNum].getTruePositives().get(matcherKey));
					C.put(matcherKey, C.get(matcherKey) + m_matches[sampleNum].getFalsePositives().get(matcherKey));
					
				}
			}
			
			// Calculate the error measure for each matcher. At the same time
			// find the matcher with the smallest error. Store the classifier
			// (=matcher) ID and the minimum error in (minClassiferId, minEps)
			double minEps = 1e8;
			Long minClassifierId = -1L;
			
			for (Long matcherKey : A.keySet())
			{
				double F = 2 * B.get(matcherKey) / (A.get(matcherKey) + C.get(matcherKey) + 2 * B.get(matcherKey));
				double error = 1. - F;
				errors.put(matcherKey, error);
				if (error < minEps && !containsId(m_results, matcherKey))
				{
					minEps = error;
					minClassifierId = matcherKey;
				}
			}

			// Make sure we still have classifiers at this point
			if (minClassifierId == -1L) 
				break;
			
			// Housekeeping..
			m_epsilon = minEps;
			if (m_epsilon == 0)
				m_epsilon = 1e-3; // avoid division by zero
						
			// *** Algorithm line 8: If eps_t <= 0.5 then ***
			if (m_epsilon <= 0.5)
			{
				// *** Algorithm line 9: Choose alpha_t in R. 
				//     alpha_t = 1/2 ln((1-eps_t)/eps_t) ***
				ChosenClassifier currentClassifier = new ChosenClassifier();
				currentClassifier.alpha = 0.5 * Math.log((1-m_epsilon) / m_epsilon);
				currentClassifier.classifierId = minClassifierId;
				m_results.add(currentClassifier);
				
				// *** Algorithm line 10: Update Dt+1(i) ***
				double sum = 0;
				for (int sample = 0; sample < numOfSamples; ++sample)
				{
					double currentClassifierDecision;
					if (!m_matches[sample].getTruePositives().containsKey(minClassifierId))
						currentClassifierDecision = 0.; // we shouldn't get here usually
					else
						currentClassifierDecision = m_matches[sample].getTruePositives().get(minClassifierId);
					double earg = -currentClassifier.alpha * 1 * (currentClassifierDecision);
					double nominator = m_currentDistribution[sample] * Math.exp(earg);
					sum += nominator;
					m_currentDistribution[sample] = nominator;
				}
				// Divide by normalization factor
				double one_over_sum = 1/sum;
				for (int sample = 0; sample < numOfSamples; ++sample)
				{
					m_currentDistribution[sample] *= one_over_sum;
				}
			}						
			// *** Algorithm line 11: t = t + 1 ***
			++iteration;
		
		// *** Algorithm line 13: until t = T or eps_t > 0.5 ***
		// this corresponds to while t <= T and eps_t <= 0.5
		} while (iteration <= maxIterations && m_epsilon <= 0.5);
	}
	

	/**
	 * Helper method. Returns whether the given ArrayList contains a specified id.
	 * @return true if the array contains the id.
	 */
	private boolean containsId(ArrayList<ChosenClassifier> mResults, Long id) {
		for (ChosenClassifier cc : mResults)
		{
			if (cc.classifierId == id)
				return true;
		}
		return false;
	}

	/**
	 * Container holding metadata about a match.
	 * A sample is one match between a term to one or more
	 * terms, as specified in the 'exact match'. 
	 * For each classifier/matcher this data structure contains whether
	 * that classifier classified the sample correctly and how so (true
	 * positive, false positive, false negative).
	 */
	private class Sample
	{				
		public Sample(
				HashMap<Long, Double> truePositives, 
				HashMap<Long, Double> falsePositives,
				HashMap<Long, Double> falseNegatives)
		{
			m_truePositives = truePositives;
			m_falsePositives = falsePositives;
			m_falseNegatives = falseNegatives;
		}
			
		public HashMap<Long, Double> getTruePositives() 
		{
			return m_truePositives;
		}
		
		public HashMap<Long, Double> getFalsePositives()
		{
			return m_falsePositives;
		}
	
		public HashMap<Long, Double> getFalseNegatives()
		{
			return m_falseNegatives;
		}

		private HashMap<Long, Double> m_truePositives;
		private HashMap<Long, Double> m_falsePositives;
		private HashMap<Long, Double> m_falseNegatives;
	}



	/**
	 * A small tuple container for coupling classifierId and its alpha value
	 */
	private class ChosenClassifier
	{
		public double alpha;
		public Long classifierId;
	}

	// Private members
	LearnWorkingSet m_lws;
	private Sample[] m_matches;
	private double[] m_currentDistribution;
	private double   m_epsilon;
	ArrayList<ChosenClassifier> m_results;

	
	/**
	 * Test method. 
	 * This creates a synthetic working set with three matchers.
	 * A ground truth matcher, an all-zeros matcher and an all-ones
	 * matcher.
	 * @return The resulting working set
	 */
	public static LearnWorkingSet createTestingWorkingSet1()
	{
		HashMap<Long, String> termsMapSchema1000 = new HashMap<Long, String>();
		termsMapSchema1000.put(1001L, "Schema1000Term1");
		termsMapSchema1000.put(1002L, "Schema1000Term2");
		termsMapSchema1000.put(1003L, "Schema1000Term3");
		termsMapSchema1000.put(1004L, "Schema1000Term4");
		termsMapSchema1000.put(1005L, "Schema1000Term5");
		termsMapSchema1000.put(1006L, "Schema1000Term6");
		termsMapSchema1000.put(1007L, "Schema1000Term7");
		termsMapSchema1000.put(1008L, "Schema1000Term8");
		termsMapSchema1000.put(1009L, "Schema1000Term9");
		termsMapSchema1000.put(1010L, "Schema1000Term10");

		HashMap<Long, String> termsMapSchema2000 = new HashMap<Long, String>();
		termsMapSchema2000.put(2001L, "Schema2000Term1");
		termsMapSchema2000.put(2002L, "Schema2000Term2");
		termsMapSchema2000.put(2003L, "Schema2000Term3");
		termsMapSchema2000.put(2004L, "Schema2000Term4");
		termsMapSchema2000.put(2005L, "Schema2000Term5");
		termsMapSchema2000.put(2006L, "Schema2000Term6");
		termsMapSchema2000.put(2007L, "Schema2000Term7");
		termsMapSchema2000.put(2008L, "Schema2000Term8");

		HashMap<Long, Integer> termsToRowsSchema1000 = 
			new HashMap<Long, Integer>();
		for (int i = 0; i < termsMapSchema1000.size(); ++i)
		{
			termsToRowsSchema1000.put(1001L + i, i);
		}

		HashMap<Long, Integer> termsToColsSchema2000 = 
			new HashMap<Long, Integer>();
		for (int i = 0; i < termsMapSchema2000.size(); ++i)
		{
			termsToColsSchema2000.put(2001L + i, i);
		}

		double[][] exactMatchMatrix = new double[termsMapSchema1000.size()][termsMapSchema2000.size()];
		double[][] onesMatchMatrix = new double[termsMapSchema1000.size()][termsMapSchema2000.size()];
		double[][] zerosMatchMatrix = new double[termsMapSchema1000.size()][termsMapSchema2000.size()];
		
		for (int row = 0; row < termsMapSchema1000.size(); ++row)
			for (int col = 0; col < termsMapSchema2000.size(); ++col)
			{
				exactMatchMatrix[row][col] = row == col ? 1.0 : 0.0;
				onesMatchMatrix[row][col] = 1.0;
				zerosMatchMatrix[row][col] = 0.0;
			}
				
		SimilarityMatrix em = new SimilarityMatrix(1000L, 2000L, termsToRowsSchema1000, termsToColsSchema2000, exactMatchMatrix);
		SimilarityMatrix om = new SimilarityMatrix(1000L, 2000L, termsToRowsSchema1000, termsToColsSchema2000, onesMatchMatrix);
		SimilarityMatrix zm = new SimilarityMatrix(1000L, 2000L, termsToRowsSchema1000, termsToColsSchema2000, zerosMatchMatrix);
		
		Schema schema1000 = new Schema(1000L, "Schema1000", null, termsMapSchema1000);
		Schema schema2000 = new Schema(2000L, "Schema2000", null, termsMapSchema2000);
		
		SchemaPair pair = new SchemaPair(schema1000, schema2000);
		pair.exactMatch = em;
		pair.correspondenceSet.put(1L, em);
		pair.correspondenceSet.put(2L, om);
		pair.correspondenceSet.put(3L, zm);
		
		LearnWorkingSet lws = new LearnWorkingSet();		
		lws.schemata.put(1000L, schema1000);
		lws.schemata.put(2000L, schema2000);
		lws.schemaPairList.add(pair);
		
		return lws;
	}

	/** 
	 * Main test method.
	 * If called with no parameters creates a synthetic working set and runs
	 * the algorithm on it.
	 * @param args If called with an argument this program reads the specified
	 * file and treats it as a learning working set serialized file. 
	 */
	public static void main(String[] args)
	{
		SMBTrain trainer = null;
		if (args.length > 0)
		{
			try {
				trainer = new SMBTrain(args[0]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (trainer == null)
				System.exit(1);
		}
		else
		{
			LearnWorkingSet lws = SMBTrain.createTestingWorkingSet1();
			trainer = new SMBTrain(lws);
		}
		
		HashMap<Long, Double> results = trainer.Train();
		System.err.println(results.size());
		
	}
		
}

