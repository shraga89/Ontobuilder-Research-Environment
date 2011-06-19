

package ac.technion.schemamatching.permanent;

import java.io.File;


import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.topk.wrapper.SchemaMatchingsWrapper;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;

import ac.technion.schemamatching.util.RandomMatchingProblemGenerator;
import ac.technion.schemamatching.util.RandomMatchingProblemInstance;

import com.modica.ontology.Ontology;
import com.modica.ontology.Term;
import com.modica.ontology.algorithm.TermAlgorithm;
import com.modica.ontology.match.MatchInformation;


public class PermanentExperiment{
	
	public static double[][] extractMatrix(int size, MatchMatrix mm, MatchedAttributePair[] exact, double threshold){
		double[][] matrix = new double[size][size];
		Term cand, target;
		for (int i=0;i<size;i++){
			cand = mm.getTermByName(exact[i].getAttribute1(), mm.getCandidateTerms());
			target = mm.getTermByName(exact[i].getAttribute2(), mm.getTargetTerms());
			matrix[i][i] = (mm.getMatchConfidence(cand, target) >= threshold ? 1.0 : 0.0);
			for (int j=0;j<size;j++){
				if (j==i) continue;
				target = mm.getTermByName(exact[j].getAttribute2(), mm.getTargetTerms());
				matrix[i][j] = (mm.getMatchConfidence(cand, target) >= threshold ? 1.0 : 0.0);
			}
		}
		
		return matrix;
	}
	
	public static void main(String[] args){
		String workDirName = args[0];
		int size = Integer.parseInt(args[1]);
		String candidateOntology;
		String targetOntology;
		String exactMapping;
		RandomMatchingProblemGenerator generator;
		
		OntoBuilderWrapper ob = new OntoBuilderWrapper();
		try {
			File workDir = new File(workDirName); 
			File[] triplets = workDir.listFiles();
			File[] triplet;
			for (int k=0;k<triplets.length;k++){
				try{
					triplet = triplets[k].listFiles();
					String filePath;
					candidateOntology = null;
					targetOntology = null;
					exactMapping = null;
					for (int q=0;q<triplet.length;q++){
						filePath = triplet[q].getAbsolutePath();
						if (filePath.indexOf("_EXACT.xml") != -1){
							exactMapping = filePath;
						}else if (filePath.indexOf("Thumbs.db") != -1){
							continue;//skip
						}else if (candidateOntology == null){
							candidateOntology = filePath;
						}else {
							targetOntology = filePath; 
						}
					}
					Ontology candidate = ob.readOntologyXMLFile(candidateOntology,false);
					Ontology target = ob.readOntologyXMLFile(targetOntology,false);
					SchemaTranslator exact = SchemaMatchingsUtilities.readXMLBestMatchingFile(exactMapping);
					
					generator = new RandomMatchingProblemGenerator(exact, candidate, target);
					RandomMatchingProblemInstance instance = generator.generateProblem(Math.min(size,exact.getMatchedPairs().length));
					
					TermAlgorithm term = (TermAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
					
					MatchInformation matchInfo = term.match(instance.getTargetOntology(), instance.getCandOntology());
					SchemaMatchingsWrapper best = new SchemaMatchingsWrapper(matchInfo.getMatrix());
					SchemaTranslator match = best.getBestMatching();
					//match.printTranslations();
					exact = instance.getExactMatching();
					//exact.printTranslations();
					
					//statistics
					double precision = SchemaMatchingsUtilities.calculatePrecision(exact, match);
					double recall = SchemaMatchingsUtilities.calculateRecall(exact, match);
					matchInfo.getMatrix().normalize();
					double[][] matrix = extractMatrix(matchInfo.getMatchMatrix()[0].length, matchInfo.getMatrix(), instance.getExactPairs(), 0.0);
					int dim = matrix[0].length;
					
					double permanent = SchemaMatchingsUtilities.calcPermanentValue(matrix);
					System.out.println(precision+"\t"+recall+"\t"+permanent);
				}catch(Exception ignore){}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
