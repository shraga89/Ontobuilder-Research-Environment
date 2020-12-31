package ac.technion.schemamatching.matchers.firstline;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.cmu.lti.jawjaw.pobj.POS;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

public class OurWordnet implements FirstLineMatcher {
	private double[][] syn_matrix;
	private double[][] hypon_matrix;
	private double[][] hyper_matrix;
	

	@Override
	public String getName() {
		return "Our Wordnet";
	}

	@Override
	public boolean hasBinary() {
		return true;
	}

	@Override
	/*
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#match(ac.technion.iem.ontobuilder.core.ontology.Ontology, ac.technion.iem.ontobuilder.core.ontology.Ontology, boolean)
	 * @param candidate-the first schema ontologies 
	 * @param target- the second schema ontologies
	 * @param binary
	 * @return MatchInformation object that represents the effectiveness of the synonyms that was found by get_Synonys function
	 */
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		Vector<Term> cTerms = candidate.getTerms(true);
		Vector<Term> tTerms = target.getTerms(true);
		int cLen=cTerms.size();
		int tLen=tTerms.size();
		ArrayList<String> templist_1=new ArrayList<String>() ; 
		ArrayList<String> templist_2=new ArrayList<String>() ;
		
		for(Term c:cTerms){
			templist_1.add(c.getName());
		}
		for(Term t:tTerms){
			templist_2.add(t.getName());
		}
		ArrayList<String> list_1=cleanAttributes(templist_1);
		ArrayList<String> list_2=cleanAttributes(templist_2);
		ArrayList <ArrayList<String[]>> match_strings_1=new ArrayList<ArrayList<String[]>>();
		ArrayList <ArrayList<String[]>> match_strings_2=new ArrayList<ArrayList<String[]>>();
 		match_strings_1=get_Synonyms_Hypernyms_Hyponyms(list_1);
		match_strings_2=get_Synonyms_Hypernyms_Hyponyms(list_2);
		
		syn_matrix=new double[cLen][tLen];
		hypon_matrix=new double[cLen][tLen];
		hyper_matrix=new double[cLen][tLen];
		syn_matrix=get_Synonyms(match_strings_1,match_strings_2,cLen,tLen);
		hypon_matrix=get_Hyponyms(match_strings_1,match_strings_2,cLen,tLen);
		hyper_matrix=get_Hypernyms(match_strings_1,match_strings_2,cLen,tLen);          
		ArrayList<ArrayList<Integer>> all_null_indexes=new ArrayList<ArrayList<Integer>>();
		all_null_indexes=find_all_null(syn_matrix,hypon_matrix,hyper_matrix);
		ArrayList<double[][]> match_after=new ArrayList<double[][]>();
		match_after=null_attribute_change_and_match(all_null_indexes,list_1,list_2);
		// change the schema features and find match according the updated attributes
		syn_matrix=match_after.get(0);
		hypon_matrix=match_after.get(1);
		hyper_matrix=match_after.get(2);
		
		
		ArrayList<Match> matches=new ArrayList<Match>();
		int iC=0;
		for(Term c:cTerms){
			int jT=0;
			for(Term t:tTerms){
				matches.add(new Match(c,t,syn_matrix[iC][jT]))	;
				jT++;
			}
			iC++;
		}
		MatchInformation mi=new MatchInformation(candidate, target);
		for (Match match : matches) {
			mi.updateMatch(match.getCandidateTerm(), match.getTargetTerm(),
					match.getEffectiveness());
			
		}
		
		return mi;
	}
	/*
	 * The function receive a list of schema fields and makes them consensus to WordNet dictionary, so WordNet can identify them in the dictionary, 
	 * so for example if a field contains several words listed sequentially, the function separate them into two separated words, 
	 * another example, if the field contains characters like apostrophe, exclamation or question symbol etc., the function remove that characters.
	 * @param list of schema fields
	 * @return list of fields consistent to WordNet dictionary
	 */
	private ArrayList<String> cleanAttributes(ArrayList<String> list) {
		int len=list.size();
		ArrayList<String> cleanRes=new ArrayList<String>();
		for(int i=0;i<len;i++){
			String temp=list.get(i);
			int temp_size=temp.length();
			int index=0;
			char temp_char;
			for(int j=0;j<temp_size;j++)
			{
				temp_char=temp.charAt(j);
				if(temp_char>='A' && temp_char<='Z')
				{
					if(j>0)
					{
						char _char=temp.charAt(j-1);
						if(_char>='a' && _char<='z')
						{
							String sub_1=temp.substring(0, j);
							String sub_2=temp.substring(j);
							temp=sub_1+"_"+sub_2;
						}
					}
				}
			}
			while(temp.contains(":") && (!temp.contains("http")))
			{
				index=temp.indexOf(':');
				if(index>=0)
					temp=temp.substring(0,index);
			}
			while(temp.contains("!"))
			{
				index=temp.indexOf('!');
				if(index>=0)
					temp=temp.substring(0,index);
			}
			while(temp.contains("?"))
			{
				index=temp.indexOf('?');
				if(index>=0)
					temp=temp.substring(0,index);
			}
			while(temp.contains(".") && (!temp.contains("http")))
			{
				index=temp.indexOf('.');
				if(index>=0)
					temp=temp.substring(0,index);
			}
			while(temp.contains(" "))
			{
				String sub_st="";
				index=temp.indexOf(" ");
				sub_st=temp.substring(0, index);
				temp=sub_st+"_"+temp.substring(index+1);
			}
			temp=temp.toLowerCase();
			cleanRes.add(temp);
		}
	
	return cleanRes;
	}
	
	/*
	 * Function accepts three matrices:
	 * one contains the match as synonyms, 
	 * the second matrix contains hypernym match of each field in the first schema to each field in the second schema, 
	 * the third matrix contains hyponym match of each field in the first schema to each field in second. 
	 * Function finds and returns the first schema's fields for which no match was found to any second schema's field, and vice versa.
	 * @param syn_matrix- synonym match 
	 * @param hypon_matrix- hyponym match
	 * @param hyper_matrix- hypernym match
	 * @return ArrayList the indexes of fields, that all the matches was null.  
	 */
	private ArrayList<ArrayList<Integer>> find_all_null(double[][] syn_matrix,double[][] hypon_matrix,double[][] hyper_matrix) 
	{
		ArrayList<ArrayList<Integer>> all_nulls_attributes=new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> attrib_1=new ArrayList<Integer>();
		ArrayList<Integer> attrib_2=new ArrayList<Integer>();
		double count_prob_1=0.0;
		double count_prob_2=0.0;
		int size_1=syn_matrix.length;
		int size_2=syn_matrix[0].length;
		for(int i=0;i<size_1;i++)
		{
			for(int j=0;j<size_2;j++)
			{
				count_prob_1=count_prob_1+syn_matrix[i][j]+hypon_matrix[i][j]+hyper_matrix[i][j];
			}
			if(count_prob_1==0.0)
			{
				attrib_1.add(i);
			}
			count_prob_1=0;
		}
		for(int i=0;i<size_2;i++)
		{
			for(int j=0;j<size_1;j++)
			{
				count_prob_2=count_prob_1+syn_matrix[j][i]+hypon_matrix[j][i]+hyper_matrix[j][i];
			}
			if(count_prob_2==0.0)
			{
				attrib_2.add(i);
			}
			count_prob_2=0;
		}
		all_nulls_attributes.add(0,attrib_1);
		all_nulls_attributes.add(1,attrib_2);
		return all_nulls_attributes;
	}
	/*
	 * The function receives two tables of word lists, the first/second table contains for each feature of the first/second schema, respectively, the list of synonyms.
	 * For each word in the first table function calculates match to each word in a second table, and returns the maximum among these values.
	 * @param string_mat_1- array of lists contain first schema feature synonyms
	 * @param string_mat_2- array of lists contain second schema feature synonyms
	 * @param list_1_size number of features in first schema
	 * @param list_2_size number of features in second schema
	 * @return synonym-match matrix
	 */
	private double[][] get_Synonyms(ArrayList <ArrayList<String[]>> string_mat_1,ArrayList <ArrayList<String[]>> string_mat_2,int list_1_size,int list_2_size)
	{
		double[][] matrix=new double[list_1_size][list_2_size];
		edu.cmu.lti.ws4j.WS4J word_rel=new edu.cmu.lti.ws4j.WS4J();
		int matrix_index_1=0;
		for(Iterator<ArrayList<String[]>> i = string_mat_1.iterator(); i.hasNext();)
		{
			double prob=0.0;
			int matrix_index_2=0;
			ArrayList<String[]> temp_1 = i.next();
			String word_1_0=temp_1.get(0)[0];
			String[] vector_1=temp_1.get(3);
			int vec_lengh_1=vector_1.length;
			for(Iterator<ArrayList<String[]>> j = string_mat_2.iterator(); j.hasNext();)
			{
				prob=0.0;
				ArrayList<String[]> temp_2=j.next();
				String word_2_0=temp_2.get(0)[0];
				if(word_1_0.endsWith(word_2_0))
					prob=1.0;
				else
					prob=word_rel.runWUP(word_1_0, word_2_0);
				int w=0;
				while(w<vec_lengh_1 && prob<1)
				{
					String word_1_1=vector_1[w];
					String[] vector_2=temp_2.get(3);
					int vec_lengh_2=vector_2.length;
					int s=0;
					while(s<vec_lengh_2 && prob==0)
					{
						String word_2_1=vector_2[s];
						prob=Math.max(Math.max(prob,word_rel.runWUP(word_1_0, word_2_1)),Math.max(word_rel.runWUP(word_1_1, word_2_0),word_rel.runWUP(word_1_1, word_2_1)));
						s++;
					}
					w++;
				}
				if(prob>1)
					prob=1;
				matrix[matrix_index_1][matrix_index_2]=prob;
				matrix_index_2++;
			}
			matrix_index_1++;			
		}
		return matrix;
	}
	/*The function receives two tables of word lists, 
	 * the first table contains for each first schemas feature list of hyponyms,
	 * the second array contains for each second schemas feature list of hypernyms,
	 * For each word in the first table function calculates match to each word in a second table, and returns the maximum among these values.
	 * @param hyper_mat- array of lists contain first schema feature hypernyms
	 * @param hypon_mat- array of lists contain second schema feature hyponyms
	 * @param list_1_size number of features in first schema
	 * @param list_2_size number of features in second schema
	 * @return hypernyms-match matrix
	 */
	private double[][] get_Hyponyms(ArrayList<ArrayList<String[]>> hyper_mat,ArrayList<ArrayList<String[]>> hypon_mat, int list_1_size,int list_2_size) {
		double[][] matrix=new double[list_1_size][list_2_size];
		edu.cmu.lti.ws4j.WS4J word_rel=new edu.cmu.lti.ws4j.WS4J();
		int matrix_index_1=0;
		for(Iterator<ArrayList<String[]>> i = hyper_mat.iterator(); i.hasNext();)
		{
			double prob=0.0;
			int matrix_index_2=0;
			ArrayList<String[]> temp_1 = i.next();
			String word_1_0=temp_1.get(0)[0];
			String[] vector_1=temp_1.get(1);
			int vec_lengh_1=vector_1.length;
			for(Iterator<ArrayList<String[]>> j = hypon_mat.iterator(); j.hasNext();)
			{
				ArrayList<String[]> temp_2=j.next();
				String[] vector_2=temp_2.get(3);
				int vec_lengh_2=vector_2.length;
				String[] vector_3=temp_2.get(2);
				int vec_lengh_3=vector_3.length;
				String word_2_0=temp_2.get(0)[0];
				prob=0.0;
				int w=0;
				while(w<vec_lengh_1 && prob<1.0)
				{
					String word_1_1=vector_1[w];
					int s=0;
					while(s<vec_lengh_2 && prob<1.0)
					{
						String word_2_1=vector_2[s]; 
						prob=Math.max(prob,word_rel.runPATH(word_1_1, word_2_1));
						//System.out.print(word_1_0+"  "+word_1_1+"  "+word_2_0+"  "+word_2_1+"  "+prob+"  \n");
						s++;
					}
					s=0;
					while(s<vec_lengh_3 && prob<1.0)
					{
						String word_3_1=vector_3[s]; 
						prob=Math.max(prob,word_rel.runPATH(word_1_1, word_3_1));
						//System.out.print(word_1_0+"  "+word_1_1+"  "+word_2_0+"  "+word_2_1+"  "+prob+"  \n");
						s++;
					}
					w++;
				}
				matrix[matrix_index_1][matrix_index_2]=prob;
				matrix_index_2++;
			}
			matrix_index_1++;
		}

	return matrix;
}
	/*
	 * The function receives two tables of word lists, 
	 * the first table contains for each first schemas feature list of hypernyms,
	 * the second array contains for each second schemas feature list of hyponyms,
	 * For each word in the first table function calculates match to each word in a second table, and returns the maximum among these values.
	 * @param hypon_mat- array of lists contain first schema feature hypernyms
	 * @param hyper_mat- array of lists contain second schema feature hyponyms
	 * @param list_1_size number of features in first schema
	 * @param list_2_size number of features in second schema
	 * @return hyponyms-match matrix
	 */
	private double[][] get_Hypernyms(ArrayList<ArrayList<String[]>> hypon_mat,ArrayList<ArrayList<String[]>> hyper_mat, int list_1_size,int list_2_size) {
		double[][] matrix=new double[list_1_size][list_2_size];
		edu.cmu.lti.ws4j.WS4J word_rel=new edu.cmu.lti.ws4j.WS4J();
		int matrix_index_1=0;
		int matrix_index_2=0;
		double prob=0.0;
		for(Iterator<ArrayList<String[]>> i = hypon_mat.iterator(); i.hasNext();)
		{
			prob=0.0;
			matrix_index_2=0;
			ArrayList<String[]> temp_1 = i.next();
			String word_1_0=temp_1.get(0)[0];
			String[] vector_1=temp_1.get(3);
			int vec_lengh_1=vector_1.length;
			String[] vector_2=temp_1.get(2);
			int vec_lengh_2=vector_2.length;
			for(Iterator<ArrayList<String[]>> j = hyper_mat.iterator(); j.hasNext();)
			{
				ArrayList<String[]> temp_2=j.next();
				String word_2_0=temp_2.get(0)[0];
				prob=0.0;
				String[] vector_3=temp_2.get(1);
				int vec_lengh_3=vector_3.length;
				int w=0;
				while(w<vec_lengh_3 && prob<1.0)
				{
					String word_3_1=vector_3[w];
					int s=0;
					while(s<vec_lengh_1 && prob<1.0)
					{
						String word_1_1=vector_1[s]; 
						prob=Math.max(prob,word_rel.runPATH(word_1_1, word_3_1));
						//System.out.print(word_1_0+"  "+word_1_1+"  "+word_2_0+"  "+word_2_1+"  "+prob+"  \n");
						s++;
					}
					s=0;
					while(s<vec_lengh_2 && prob<1.0)
					{
						String word_1_2=vector_2[s]; 
						prob=Math.max(prob,word_rel.runPATH(word_1_2, word_3_1));
						//System.out.print(word_1_0+"  "+word_1_1+"  "+word_2_0+"  "+word_2_1+"  "+prob+"  \n");
						s++;
					}
					w++;
				}
				matrix[matrix_index_1][matrix_index_2]=prob;
				matrix_index_2++;
			}
			matrix_index_1++;
		}
	
	return matrix;
}

	/*
	 * The function receive list of features and return for each feature list of synonyms, hypernym's list and hyponym's list
	 * @param list- list of schema's features
	 * @return array that consist of tree lists for each feature: list of synonyms, hypernym's list and hyponym's list 
	 */
	private ArrayList<ArrayList<String[]>> get_Synonyms_Hypernyms_Hyponyms(ArrayList<String> list)
	{
		edu.cmu.lti.ws4j.WS4J word_rel=new edu.cmu.lti.ws4j.WS4J();
		int list_size=list.size();
		Set<String> mat_hypernyms;
		Set<String> mat_hyponims;
		Set<String> mat_synonyms;
		ArrayList <ArrayList<String[]>> match_strings=new ArrayList<ArrayList<String[]>>();
		String temp="";
		for(int i=0;i<list_size;i++)
		{
			ArrayList<String[]> m_strings=new ArrayList<String[]>();
			temp=list.get(i);
			String[] m_0= new String[1];
			m_0[0]=temp;
			POS pos=POS.n;
			mat_hypernyms=word_rel.findHypernyms(temp, pos);
			String[] m_1=mat_hypernyms.toArray(new String[0]);
			mat_hyponims=word_rel.findHyponyms(temp, pos);
			String[] m_2=mat_hyponims.toArray(new String[0]);
			mat_synonyms=word_rel.findSynonyms(temp, pos);
			String[] m_3=mat_synonyms.toArray(new String[0]);
			m_strings.add(0, m_0); //attributes
			m_strings.add(1,m_1); //hypernyms
			m_strings.add(2,m_2); //hyponyms
			m_strings.add(3,m_3); //synonyms
			match_strings.add(m_strings);
		}
		return match_strings;
	}
	/*
	 * The function receive the first schema's features, the second schema's features and an array of two lists,
	 * the first list contain the first schema's feature's indexes
	 * and the second contain the second schema feature's indexes.
	 * the function assisted by another function, get_new_attributes, gets abbreviated features 
	 * and return match after the changes, synonym, hypernym and hyponym match
	 * @param all_null_indexes - the indexes of the first and the second schema fields, that all the matches was null.  
	 * @param list_1 - the first schema attributes
	 * @param list_2 - the second schema attributes
	 * @return an array of 3 tables: synonym, hypernym and hyponym match
	 */
	private ArrayList<double[][]> null_attribute_change_and_match(ArrayList<ArrayList<Integer>> all_null_indexes,ArrayList<String> list_1,ArrayList<String> list_2)
	{
		
		ArrayList<double[][]> match_after=new ArrayList<double[][]>();
		ArrayList<Integer> null_attributes_1=new ArrayList<Integer>();
		null_attributes_1=all_null_indexes.get(0);
		ArrayList<Integer> null_attributes_2=new ArrayList<Integer>();
		null_attributes_2=all_null_indexes.get(1);
		ArrayList<ArrayList<String>> new_attributes_1=new ArrayList<ArrayList<String>>();
		new_attributes_1=get_new_attributes(list_1, null_attributes_1);
		ArrayList<ArrayList<String>> new_attributes_2=new ArrayList<ArrayList<String>>();
		new_attributes_2=get_new_attributes(list_2, null_attributes_2);
		
		match_after=match(new_attributes_1,new_attributes_2);
		return match_after;
	}
	/*
	 * The function receives the attributes for which no match was found, 
	 * if the feature has more than two words, shortens it by the next rule:
	 * split according to the bottom-lines:
	 * if there tree words and the first has synonyms and if the two last words have definition then doesn't refer to the first word
	 * else split according to the first bottom-line.
	 * In that two cases remain with two substrings, now the function has to define with which one of them to refer.
	 * If the match between two substrings is above 0.5 then refer to the first word, if the match is above 0.7 then save it to the list "att_1_1" else save it to the list "att_1_2". else, refer to the second and saves it to the list "att_1_2".  
	 * After the function has changed and updated features for both schemas, it returns two lists of updated features.
	 * @param list_ - list of two schemas features
	 * @param null_attributes - the indexes of the first and the second schema fields such that all the matches was null for them
	 * @return new_attributes - array of tree lists: first list is the updated features, second is the list "att_1_1"- list of updated features, for these features we can look for synonym match too, the third list contains features for which the previous function will not look for synonym-match 
	 */
	private ArrayList<ArrayList<String>> get_new_attributes(ArrayList<String> list_,ArrayList<Integer> null_attributes)
	{
		edu.cmu.lti.ws4j.WS4J word_rel=new edu.cmu.lti.ws4j.WS4J();
		ArrayList<ArrayList<String>> new_attributes=new ArrayList<ArrayList<String>>();
		int size=null_attributes.size();
		ArrayList<String> att_1_1=new ArrayList<String>();
		ArrayList<String> att_1_2=new ArrayList<String>();
		String new_attribute="";
		boolean falgAdj=false;
		if(null_attributes.size()>0)
		{
			for(int i=0;i<size;i++)
			{
				int index=null_attributes.get(i);
				String temp_1=list_.get(index);
				String substring_1="";
				String substring_2="";
				if(temp_1.contains("_"))
				{
					String[] words=temp_1.split("_");
					if (words.length==3 && word_rel.findSynonyms(words[0], POS.n).isEmpty() && word_rel.findSynonyms(words[0], POS.v).isEmpty() && (!word_rel.findDefinitions(words[1]+"_"+words[2],POS.n).isEmpty() || !word_rel.findDefinitions(words[1]+"_"+words[2],POS.v).isEmpty())){
						substring_1=words[1];
						substring_2=words[2];
						falgAdj=true;
					}
					else{
						int index_of__=temp_1.indexOf("_");
						substring_1=temp_1.substring(0, index_of__);
						substring_2=temp_1.substring(index_of__+1);	
						
					}
					new_attribute="";
					double rel=word_rel.runWUP(substring_1, substring_2);
					if(rel>=0.7)
					{
						if (falgAdj==false)
						{
							new_attribute=substring_1;
							att_1_1.add(new_attribute);
						}//********avoiding from three word attribute is considered be synonym 
						else
						{
							new_attribute=substring_1;
							att_1_2.add(new_attribute);
						}
							
					}
					if(rel>0.5&&rel<0.7)
					{
						new_attribute=substring_1;
						att_1_2.add(new_attribute);
					}
					if(rel<=0.5)
					{
						new_attribute=substring_2;
						att_1_2.add(new_attribute);
					}
					list_.remove(index);
					list_.add(index, new_attribute);
					if(falgAdj==true){
						falgAdj=false;
					}
				}
			}
		}
		new_attributes.add(0,list_);
		new_attributes.add(1, att_1_1);
		new_attributes.add(2, att_1_2);
		return new_attributes;
	}
	/*
	 * The function receive two arrays of tree lists, first list contain the attributes, second is the list of features for which the function will look for synonym match too, the third list contains features for which the function will not look for synonym-match.
	 * @param new_attributes_1 - array of tree list of the first schema features
	 * @param new_attributes_2 - array of tree list of the second schema features
	 * @return the synonym, hypernym and hyponym match between each two features
	 */
	public ArrayList<double[][]> match(ArrayList<ArrayList<String>> new_attributes_1,ArrayList<ArrayList<String>> new_attributes_2)
	{
		ArrayList<double[][]> match_matrix=new ArrayList<double[][]>();
		ArrayList<String> list_1=new ArrayList<String>();
		list_1=new_attributes_1.get(0);
		ArrayList<String> for_hyper_hypon_1=new ArrayList<String>();
		for_hyper_hypon_1=new_attributes_1.get(2);
		ArrayList<String> list_2=new_attributes_2.get(0);
		ArrayList<String> for_hyper_hypon_2=new_attributes_2.get(2);
		ArrayList<ArrayList<String[]>> att_list_1=get_Synonyms_Hypernyms_Hyponyms(list_1);
		ArrayList<ArrayList<String[]>> att_list_2=get_Synonyms_Hypernyms_Hyponyms(list_2);
		int size_list_1=list_1.size();
		int size_list_2=list_2.size();
		double[][] syn_mat=new double[size_list_1][size_list_2];
		double[][] hyper_mat=new double[size_list_1][size_list_2];
		double[][] hypon_mat=new double[size_list_1][size_list_2];
		syn_mat=this.get_Synonyms(att_list_1, att_list_2, size_list_1, size_list_2);
		hypon_mat=this.get_Hyponyms(att_list_1, att_list_2, size_list_1, size_list_2);
		hyper_mat=this.get_Hypernyms(att_list_1, att_list_2, size_list_1, size_list_2);
		
		for(int i=0;i<size_list_1;i++)
		{
			if(for_hyper_hypon_1.contains(list_1.get(i)))
			{
				for(int j=0;j<size_list_2;j++)
				{
					syn_mat[i][j]=0;
					hyper_mat[i][j]=0;
				}
			}
		}
		for(int i=0;i<size_list_2;i++)
		{
			if(for_hyper_hypon_2.contains(list_2.get(i)))
			{
				for(int j=0;j<size_list_1;j++)
				{
					syn_mat[j][i]=0;
					hypon_mat[j][i]=0;
				}
			}
		}
		match_matrix.add(0, syn_mat);
		match_matrix.add(1, hypon_mat);
		match_matrix.add(2, hyper_mat);
		return match_matrix;
	}

	@Override
	public String getConfig() { 
		return "no configurable parameters";
	}

	@Override
	public MatcherType getType() {
		return MatcherType.SEMANTIC;
	}

	@Override
	public int getDBid() {
		return 111;
	}

}
