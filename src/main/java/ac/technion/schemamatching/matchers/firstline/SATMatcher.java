package ac.technion.schemamatching.matchers.firstline;

import java.util.ArrayList;
import java.util.Vector;

import edu.cmu.lti.jawjaw.pobj.Synset;
import rita.RiWordNet;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

public class SATMatcher implements FirstLineMatcher {
	public SATMatcher() {

	}

	/**
	 * Matcher Name
	 * 
	 * @return String representing the matcher name
	 */
	public String getName() {
		return "SAT Matcher";
	}

	/**
	 * Should return true if the algorithm can be set to return a binary
	 * similarity matrix rather than a real valued one.
	 * 
	 * @return
	 */
	public boolean hasBinary() {
		return false;
	}

	// Match mt=new Match(tTerms.get(j+2),cTerms.get(i+2),M[i][j]);
	public class SatMatch {
		private Vector<Term> cTerms;
		private Vector<Term> tTerms;
		private ArrayList<Match> matches;

		private double synPercent = 0.5;
		private double ancestorPercent = 0.2;
		private double decendantPercent = 0.2;
		private double brotherPercent = 0.1;

		public RiWordNet wordnet;

		private class ExpandedTerm {
			public Term t;
			public ArrayList<String> syn;
			private ArrayList<String> ancestorNames;
			private ArrayList<String> decendantNames;
			private ArrayList<String> brothers;

			private int wordCountToTake = 2;

			ExpandedTerm(Term t) {
				this.t = t;
				syn = new ArrayList<String>();
				ancestorNames = new ArrayList<String>();
				decendantNames = new ArrayList<String>();
				brothers = new ArrayList<String>();
				fillSyns(syn, t);
				fillAncestorNames();
				fillDecendantNames();
				fillBrothers();
			}
			
			public void print() {
				System.out.println("Term: " + t.getName());
				System.out.println("Syn: ");
				for(int i = 0; i < syn.size(); i++)
					System.out.print(syn.get(i) + ", ");
				System.out.println("anc: ");
				for(int i = 0; i < ancestorNames.size(); i++)
					System.out.print(ancestorNames.get(i) + ", ");
				System.out.println("dec: ");
				for(int i = 0; i < decendantNames.size(); i++)
					System.out.print(decendantNames.get(i) + ", ");
				System.out.println("Brothers: ");
				for(int i = 0; i < brothers.size(); i++)
					System.out.print(brothers.get(i) + ", ");
			}
			
			private ArrayList<String> splitTerm(String word) {
				ArrayList<String> finalWords = new ArrayList<String>();
				word=word.replaceAll("-", "_");
				word=word.replaceAll(" ", "_");
				String[] words = word.split("_");
				String[] upperWords = {};
				for(String k : words) {
					upperWords = k.split("(?<=[a-z])(?=[A-Z])");
					for(String p : upperWords) {
						if(p.length() == 1) {
							if(!finalWords.contains(k)) {
								finalWords.add(k.toLowerCase());
							}
						} else {
							if(!finalWords.contains(p)) {
								finalWords.add(p.toLowerCase());
							}
						}
					}
				}
				return finalWords;
			}

			private void fillSyns(ArrayList<String> list, Term term) {
				String word = term.getName();
				; // use t.toString() or t.getName();
				ArrayList<String> finalWords = splitTerm(word);
				for(String p : finalWords)	{
					list.add(p.toLowerCase());
				}
					
				if (wordnet != null) {
					for(String k : finalWords) {
						String[] poss = wordnet.getPos(k.toLowerCase());
						if (poss.length == 0){
							return;
						}
						for(int j = 0; j < poss.length; j++) {
							String[] synonyms = wordnet.getAllSynonyms(k.toLowerCase(),
									poss[j], 10);
							int len;
							if (synonyms.length > wordCountToTake) {
								len = wordCountToTake;
							} else {
								len = synonyms.length;
							}

							for (int i = 0; i < len; i++) {
								list.add(synonyms[i].toLowerCase());
							}	

						}
					}
				} 
			}

			private void fillAncestorNames() {
				Term pTermIt = t;
				String word = pTermIt.getName();
				ArrayList<String> finalWords = splitTerm(word);
				if (wordnet != null) {
					for(String k : finalWords) {
						String pos = wordnet.getBestPos(k.toLowerCase());
						if (pos == null)
							return;
						String[] hypernyms = wordnet.getAllHypernyms(k.toLowerCase(), pos);
						int len;
						if (hypernyms.length > wordCountToTake)
							len = wordCountToTake;
						else
							len = hypernyms.length;
						for (int i = 0; i < len; i++) {
							ancestorNames.add(hypernyms[i].toLowerCase());
						}
					}
					for (int i = 0; i < wordCountToTake; i++) {
						pTermIt = pTermIt.getParent();
						if (pTermIt == null)
							break;
						fillSyns(ancestorNames, pTermIt);
					}
				}
			}

			private void fillDecendantNames() {
				String word = t.getName();
				ArrayList<String> finalWords = splitTerm(word);
				if (wordnet != null) {
					for(String k : finalWords) 	{
						String pos = wordnet.getBestPos(k.toLowerCase());
						if (pos == null)
							return;
						String[] hyponyms = wordnet.getAllHyponyms(k.toLowerCase(), pos);
						int len;
						if (hyponyms.length > wordCountToTake)
							len = wordCountToTake;
						else
							len = hyponyms.length;
						for (int i = 0; i < len; i++) {
							decendantNames.add(hyponyms[i].toLowerCase());
						}
					}
				}
			}

			private void fillBrothers() {
				Term pTermIt = t.getPrecede();
				if (pTermIt != null) {
					fillSyns(brothers, pTermIt);
				}
				pTermIt = t.getSucceed();
				if (pTermIt != null) {
					fillSyns(brothers, pTermIt);
				}
			}

			ArrayList<String> getSyns() {
				return syn;
			}

			ArrayList<String> getAncestors() {
				return ancestorNames;
			}

			ArrayList<String> getDecendants() {
				return decendantNames;
			}

			ArrayList<String> getBrothers() {
				return brothers;
			}
		}

		SatMatch(Vector<Term> cTerms, Vector<Term> tTerms) {
			this.cTerms = cTerms;
			this.tTerms = tTerms;
			this.matches = new ArrayList<Match>();
			this.wordnet = new RiWordNet(".\\WordNet-3.1\\WordNet-3.1",false,false); //path to wordnet
		}

		private ExpandedTerm buildExtendedData(Term t) {
			return new ExpandedTerm(t);

		}

		public double getMatchScore(ExpandedTerm eA, ExpandedTerm eB) {
			double result = 0.000;
			ArrayList<String> tmp = new ArrayList<String>();
			tmp.addAll(eA.getSyns());
			tmp.retainAll(eB.getSyns());
			double denomenator = eA.getSyns().size() + eB.getSyns().size()
					- tmp.size();
			double x = 0.0;
			if (denomenator != 0) 
				x = ((double)tmp.size() / denomenator);
			
//			if(tmp.size() != 0)
//				System.out.println(eA.getSyns().size() + " " + eB.getSyns().size() + " " + tmp.size() + " " + denomenator);	
			
				result += (synPercent * x);				
			

			tmp.clear();

			tmp.addAll(eA.getAncestors());
			tmp.retainAll(eB.getAncestors());
			denomenator = eA.getAncestors().size() + eB.getAncestors().size()
					- tmp.size();
			if (denomenator != 0)
				result += ancestorPercent * x;

			tmp.clear();

			tmp.addAll(eA.getDecendants());
			tmp.retainAll(eB.getDecendants());
			denomenator = eA.getDecendants().size() + eB.getDecendants().size()
					- tmp.size();
			x = ((double)tmp.size() / denomenator);
			if (denomenator != 0)
				result += decendantPercent * x;

			tmp.clear();

			tmp.addAll(eA.getBrothers());
			tmp.retainAll(eB.getBrothers());
			denomenator = eA.getBrothers().size() + eB.getBrothers().size()
					- tmp.size();
			x = ((double)tmp.size() / denomenator);
			if (denomenator != 0)
				result += brotherPercent * x;
			
			tmp.clear();
			return result;
		}

		private double calcScore(Term a, Term b) {
			ExpandedTerm eA = buildExtendedData(a);
			ExpandedTerm eB = buildExtendedData(b);
//			eA.print();
//			eB.print();
			double g = getMatchScore(eA, eB);
//			if(g != 0.0)
			System.out.println(g + " " + a.getName() + " " +b.getName());
			return g;
		}

		public ArrayList<Match> run() {
			for (Term cT : cTerms) {
				for (Term tT : tTerms) {
					matches.add(new Match(cT, tT, calcScore(cT, tT)));
				}
			}
			return matches;
		}

	}

	/**
	 * Main method of the matcher.
	 * 
	 * @param candidate
	 *            Ontology / Schema to be matched
	 * @param target
	 *            Ontology / Schema to be matched
	 * @param binary
	 *            If the algorithm can return a binary matrix then setting this
	 *            parameter to true will cause it to do so.
	 * @return a MatchInformation object containing the similarity matrix
	 *         created
	 */
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {

		Vector<Term> cTerms = candidate.getTerms(true);
		Vector<Term> tTerms = target.getTerms(true);

		SatMatch satMatch = new SatMatch(cTerms, tTerms);
		ArrayList<Match> algoRes = satMatch.run();

		MatchInformation res = new MatchInformation(candidate, target);
		for (Match mt : algoRes) {
			res.updateMatch(mt.getCandidateTerm(), mt.getTargetTerm(),
					mt.getEffectiveness());
		}

		return res;

	}

	/**
	 * For matchers with configuration parameters. This method returns the
	 * configuration currently set.
	 * 
	 * @return String describing the current configuration.
	 */
	public String getConfig() {
		return null;
	}

	/**
	 * Return one the matcher type best describing this matcher
	 * 
	 * @return
	 */
	public MatcherType getType() {
		return MatcherType.SEMANTIC;
	}

	/**
	 * Return the schema matching database id of this matcher.
	 * 
	 * @return integer corresponding to the SMID field in the SimilarityMeasures
	 *         table in the schema matching DB
	 */
	public int getDBid() {
		return 30;
	}
}
