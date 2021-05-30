package ac.technion.schemamatching.testbed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import ac.technion.schemamatching.DBInterface.DBInterface;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.ExperimentDocumenter;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.util.ConversionUtils;


/**
 * <p>Title: Schema Pair Matching Experiment</p>
 *
 * <p>Description: A Schema experiment consists of 2 schemas and an optional exact match.  
 *
 * @author Tomer Sagi (Version 1.0 by Anan Marie)
 * @version 2.1
 */

public class ExperimentSchemaPair extends ExperimentSchema {


	/**
	 * Constructor by pair ID, Loads schema ontologies and exact match if exists
	 * @param spid schema pair ID
	 * @param dsid data set ID
	 * @throws FileNotFoundException is one of the ontologies isn't found
	 */
	public ExperimentSchemaPair(int spid, int dsid) throws FileNotFoundException {
		super();
		ID = spid;
		dsEnum = OREDataSetEnum.getByDbid(dsid);
		basicMatrices = new HashMap<>();
		System.err.println("loading" + spid);
		load();
	}

	/**
	 * Constructor by two schemas, Loads exact match if exists.
	 *
	 * @param candidate schema
	 * @param target schema
	 * @param allowReverse if true will load an exact match even if the candidate and target are swapped.
	 */
	public ExperimentSchemaPair(ExperimentSchema candidate, ExperimentSchema target, boolean allowReverse) {
		super();
		Ontology o = new Ontology("A", "B");
		Term t = new Term("c");
		o.addTerm(t);
		basicMatrices = new HashMap<>();
		this.candidate = candidate.getTargetOntology();
		this.target = target.getTargetOntology();
		this.dsEnum = target.dsEnum;
		int spid;
		ExperimentDocumenter ed = OBExperimentRunner.getOER().getDoc();
		if (ed==null) {
			System.err.println("Database not avilable, cannot load schema pair info from DB");
			spid = -1;
		} else {
			spid = ed.getPairID(candidate.getID(), target.getID(), allowReverse);
		}
		String exactPath;
		if (spid != -1) {
			exactPath = ed.getSPPathBySPID(spid);
			this.loadExact(exactPath);
		}
	}

	public boolean hasExactMatch(){
		return (exactMapping == null);
	}
	
	public Ontology getTargetOntology() { 
		return target;
	}
	
	public Ontology getCandidateOntology()	{
		return candidate;
	}
	
	public MatchInformation getExact() {
		return exactMapping;
	}

	/**
	 * Loads exact match from supplied path using DSEnum defined importer
	 *
	 * @param exactMatchPath filepath to exact match
	 */
	private void loadExact(String exactMatchPath) {
		try {
			if (dsEnum.isHasExact()){
				exactMapping = dsEnum.getMatchImp().importMatch(new MatchInformation(candidate, target), new File(OBExperimentRunner.getOER().getDsurl() + exactMatchPath));
			}
			else
				exactMapping = null;
		} catch (Exception e) {
			e.printStackTrace();
			OBExperimentRunner.fatalError("XML Load failed on:" + exactMatchPath);
		}
	}

	/**
	 * Create schemaExp objects and adds them to the dataset. Each object includes
	 * A target Schema, Candidate Schema and Exact mapping.
	 * Assumes that each sub-directory includes 2 schemas and an exact match with .xml type
	 * Assumes exact match ends with the string "xml_EXACT.xml"
	 *
	 * @throws FileNotFoundException if an ontology isn't found
	 */
	private void load() throws FileNotFoundException {

		//get paths
		String exactMatchPath;
		String candPath;
		String targPath;
		Importer imp = dsEnum.getImporter();
		String sql = "SELECT CandidateSchema, TargetSchema, DSID, path FROM schemapairs WHERE SPID = " + ID + ";";
		ArrayList<String[]> res = OBExperimentRunner.getOER().getDB().runSelectQuery(sql, 4);
		candidateID = Integer.parseInt(res.get(0)[0]);
		targetID = Integer.parseInt(res.get(0)[1]);
		int dsid = Integer.parseInt(res.get(0)[2]);
		exactMatchPath = res.get(0)[3];
		//For ontobuilder webform dataset (DSID ==1) the path of the schemas is parsed from the path of the exact match
		if (dsid == 1) {
			//For example: WebForm/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT.xml
			String pairFolder = exactMatchPath.split("/")[1];
			candPath = OBExperimentRunner.getOER().getDsurl() + exactMatchPath.split("/")[0] + File.separatorChar + pairFolder + File.separatorChar + pairFolder.split("_")[0];
			targPath = OBExperimentRunner.getOER().getDsurl() + exactMatchPath.split("/")[0] + File.separatorChar + pairFolder + File.separatorChar + pairFolder.split("_")[1];
		} else //Non ontobuilder webform dataset
		{
			//get target path from db
			sql = "SELECT filePath FROM schemata WHERE SchemaID = " + targetID + ";";
			targPath = getSchemaPath(sql);
			sql = "SELECT filePath FROM schemata WHERE SchemaID = " + candidateID + ";";
			candPath = getSchemaPath(sql);
		}
		//load schemas to ontologies
		target = loadOntologyFromPath(targPath, imp);
		candidate = loadOntologyFromPath(candPath, imp);

		//load exact match
		loadExact(exactMatchPath);
	}

  /**
   * Given an SQL statement, returns the path
   * @param sql statement
   * @return filepath
   * @throws FileNotFoundException if path documented in datrabase is not found
   */
static String getSchemaPath(String sql) throws FileNotFoundException {
	ArrayList<String[]> res = OBExperimentRunner.getOER().getDB().runSelectQuery(sql, 1);
	  if (res.isEmpty()) OBExperimentRunner.fatalError("No url recieved from the database for schema");
	  File f = new File(OBExperimentRunner.getOER().getDsurl(),res.get(0)[0]);
	  if (!f.isFile())
		  throw new FileNotFoundException(f.getAbsolutePath());
	  else
		  return f.getAbsolutePath();
}

	/**
	 * @param schemaFilePath path to schema file to load
	 * @param imp importer to use
	 * @return ontology object
	 */
	private Ontology loadOntologyFromPath(String schemaFilePath, Importer imp) {
		try {
			File schemaFile = new File(schemaFilePath);
			if (dsEnum.isHasInstances()) {
				final String substring = schemaFilePath.substring(0, schemaFilePath.length() - 4);
				File instanceFile = new File(substring + ".xml");
				if (!instanceFile.exists()) //try folder
					instanceFile = new File(substring);
				if (!instanceFile.exists()) //instances not found
				{
					System.err.println("No instances found for " + schemaFile + " loading schema only");
					return imp.importFile(schemaFile);
				}
				return imp.importFile(schemaFile, instanceFile);
			} else
				return imp.importFile(schemaFile);
		} catch (Exception e) {
			e.printStackTrace();
			OBExperimentRunner.fatalError("File Load failed on:" + schemaFilePath);
		}
		return null;
	}

	/**
	 * Return a basic similarity matrix using the supplied @link{FirstLineMatcher}
	 * Retrieves from the db if it exists, otherwise creates it and documents it in the DB
	 * @param flm first line matcher
	 * @param isMemory if true will use database to search for existing results before matching
	 * @return matching information object representing the result of the FLM over the schema pair
	 */
	public MatchInformation getSimilarityMatrix(FirstLineMatcher flm, boolean isMemory) {

		boolean isXSD = dsEnum.isXSD();
		int smid = flm.getDBid();

		if (!basicMatrices.containsKey(smid)) {
			MatchInformation mi;
			boolean use_db = isMemory;
			boolean write_toDB = false;
			if (use_db && OBExperimentRunner.getOER().getDoc() == null) {
				System.err.println("Database not available, matching pair.");
				use_db = false;
			}
			
			if (use_db && !this.dsEnum.isSupportsDBLookUp()) {                // check if this Enum type supports look up
				System.err.println("The " + dsEnum.name() +
						" dataset does not support database lookup, matching ontologies instead.");
				use_db = false;
			}
				
			if (use_db && !OBExperimentRunner.getOER().getDoc().checkIfSchemaPairWasMatched(ID, smid, isXSD)) {            // support lookup but no data in DB
				System.out.println("no Data in DB :D");
				use_db = false;
				write_toDB = true;
			}
			if (use_db) {
				System.out.println("match data is in the DB, loading..");

				if (!isXSD) {							// 	XML format

					mi = DBInterface.createMIfromArrayList(candidate, target, getSimilarityMatrixFromDB(smid, ID, OBExperimentRunner.getOER().getDB(), isXSD));
				} else {								// XSD format

					ArrayList<String[]> res = getSimilarityMatrixFromDB(smid, ID, OBExperimentRunner.getOER().getDB(), isXSD);
					mi = createMIfromArrayListXSD(candidate, target, res);
				}

				System.out.println("finished loading matches from DB :D");
			}
			else
			{
				System.out.println("flm name is: " + flm.getName());
				System.out.println("candidate name is: " + candidate.getName());
				System.out.println("target name is: " + target.getName());
				mi = flm.match(candidate, target, false);
				System.out.println("inserting matches to DB, please wait...");

				assert (mi != null);
				//If match information dimensions are reduced, expand them
				if (mi.getCandidateOntology().getAllTermsCount() > mi.getMatrix().getColCount()) {
					ConversionUtils.fillMI(mi);
				}
			}
			basicMatrices.put(smid, mi);
			if (write_toDB) {
				//document similarity matrix
				try {
					OBExperimentRunner.getOER().getDoc().loadSMtoDB(mi, this, smid, isXSD);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return basicMatrices.get(smid);
	}

	public long getTargetID() {
		return targetID;
	}

	public long getCandidateID() {
		return candidateID;
	}

	/**
	 * This method receives  a name of an ontology and returns its ID from the DB (Schema Table)
	 *
	 * @param name - Name of the ontonolgy
	 * @param db   - an open connection to the DB
	 * @return ID, if wasn't able to find the ontology return -1
	 */
	public long getOntologyDBId(String name, DBInterface db) {
		String sql = "SELECT SchemaID From schemata WHERE SchemaName= \"" + name + "\";";
		ArrayList<String[]> SchameID = db.runSelectQuery(sql, 1);
		long Id;
		try {
			Id = Long.parseLong(SchameID.get(0)[0]);
		} catch (IndexOutOfBoundsException e) {
			System.out.println("Ontology not found:");
			return -1;
		}
		return Id;
	}

	private Ontology target;
	private Ontology candidate;
	private int targetID;
	private int candidateID;
	private MatchInformation exactMapping;
	private final HashMap<Integer, MatchInformation> basicMatrices;
	private final OREDataSetEnum dsEnum;

	/**
	 * Return the similarity matrix of the supplied schema pair using the similarity measure supplied
	 * If the matrix isn't documented in the db, returns null
	 *
	 * @param smid code of matching algorithm
	 * @param spid Schema Pair ID to look for
	 * @param db database to search for matrix in
	 * @return String array {SMID,CandidateSchemaID,CandidateTermID,TargetSchemaID,TargetTermID,confidence} or null if data isn't found
	 */
	private ArrayList<String[]> getSimilarityMatrixFromDB(int smid, long spid, DBInterface db, boolean isXSD) {
		String sql;
		ArrayList<String[]> res;

		if (isXSD) {
			sql = "SELECT `similaritymatricesXSD`.`SMID`,`similaritymatricesXSD`." +
					"`CandidateSchemaID` , `similaritymatricesXSD`.`CandidateTermID`, " +
					"`similaritymatricesXSD`.`TargetSchemaID` , `similaritymatricesXSD`." +
					"`TargetTermID` , `similaritymatricesXSD`.`confidence` " +
					"FROM `schemapairs` INNER JOIN `similaritymatricesXSD` " +
					"ON (`schemapairs`.`TargetSchema` = `similaritymatricesXSD`.`TargetSchemaID`) " +
					"AND (`schemapairs`.`CandidateSchema` = `similaritymatricesXSD`.`CandidateSchemaID`)" +
					" WHERE (`similaritymatricesXSD`.`SMID` = '" + smid + "' " +
					"AND `schemapairs`.`SPID` = " + spid + ");";
		} else {
			sql = "SELECT `similaritymatrices`.`SMID`,`similaritymatrices`." +
					"`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID`, " +
					"`similaritymatrices`.`TargetSchemaID` , `similaritymatrices`." +
					"`TargetTermID` , `similaritymatrices`.`confidence` " +
					"FROM `schemapairs` INNER JOIN `similaritymatrices` " +
					"ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) " +
					"AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
					" WHERE (`similaritymatrices`.`SMID` = '" + smid + "' " +
					"AND `schemapairs`.`SPID` = " + spid + ");";
		}
		res = db.runSelectQuery(sql, 6);
		return res;
	}

	public String toString() {
		return Integer.toString(ID);
	}

	public OREDataSetEnum getDataSetType() {
		return dsEnum;
	}


	public Vector<Term> getLeafTerms(Vector<Term> termVec) {

		ArrayList<Term> resList = new ArrayList<>();

		ArrayList<Term> termArrayList;
		termArrayList = new ArrayList<>(termVec);

		for (Term nextTerm : termArrayList)
			if (nextTerm.getTermsCount() == 0) {
				resList.add(nextTerm);
			}

		return new Vector<>(resList);
	}

	public static String getPathToRoot(Term term) {
		StringBuilder resPath = new StringBuilder();

		while (term.getParent() != null) {
			resPath.insert(0, term.getName() + ".");
			term = term.getParent();
		}
		resPath.insert(0, term.getName() + ".");
		return resPath.toString();
	}

	public static MatchInformation createMIfromArrayListXSD(Ontology candidate, Ontology target, ArrayList<String[]> matchList) {
		MatchInformation mi = new MatchInformation(candidate, target);
		ArrayList<Match> matches = new ArrayList<>();

		Vector<Term> candidateVec = candidate.getTerms(true);
		Vector<Term> targeteVec = target.getTerms(true);

		Vector<String> candidateStr = termVecToStrVec(candidateVec);
		Vector<String> targetStr = termVecToStrVec(targeteVec);

		for (String[] match : matchList) {
			Term c = getTermByString(candidateStr, candidateVec, match[2]);
			Term t = getTermByString(targetStr, targeteVec, match[4]);

			assert c != null && t != null;

			matches.add(new Match(t, c, Double.parseDouble(match[5])));
		}

		mi.setMatches(matches);
		return mi;
	}


	public static Term getTermByString(Vector<String> strVec, Vector<Term> termVec, String termPath) {
		int idx = 0;
		for (String str : strVec) {
			if (str.equals(termPath)) {
				return termVec.elementAt(idx);
			}
			idx++;
		}
		System.err.println("Didnt find the termPart: " + termPath + " within Vector<Term>, idx: " + idx);
		return null;
	}


	public static Vector<String> termVecToStrVec(Vector<Term> termVec){
		Vector<String> strVec = new Vector<>();
		Iterator<Term> i = termVec.iterator();
		int idx = 0;
		while (i.hasNext()) {
			String str = getPathToRoot(termVec.elementAt(idx));
			strVec.add(str);

			i.next();
			idx++;
		}
		return strVec;
	}
}