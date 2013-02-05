package ac.technion.schemamatching.matchers.firstline;

import java.io.File;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.io.matchimport.MatchImporter;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
/**
 * @author Tomer Sagi
 * A shell intended for importing similarity matrices generated by some
 * external system using OB built-in match importers. 
 */
public class SimMatrixShell implements FirstLineMatcher {
	private File f = null;
	private MatchImporter importer = null;
	private MatcherType type = MatcherType.SYNTACTIC;

	/**
	 * Sets the match file import path 
	 * @param matrixPath
	 * @return true if path supplied resolves correctly to a file and false otherwise.
	 */
	public boolean setPath(String matrixPath, String filename)
	{
		File f = new File(matrixPath.trim(),filename.trim());
		if (f.exists())
		{
			this.f = f;
			return true;
		}
		return false;
	}
	
	/**
	 * Since the matcher used to generate this matrix is unknown
	 * to ORE, you may use this method to update this information.
	 * Defaults to "Syntactic" 
	 * @param mType
	 * @return true if type is not null
	 */
	public void setType(MatcherType mType)
	{
		if (mType != null)
			type = mType;
	}
	
	public void setImporter(MatchImporter imp)
	{
		importer = imp;
	}
	
	@Override
	public String getName() {
		return "SimilarityMatrixImporterShell";
	}

	@Override
	public boolean hasBinary() {
		return false;
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		MatchInformation res = null;
		if (f != null && importer != null)
			res = importer.importMatch(new MatchInformation(candidate,target), f);
		return res;
	}

	@Override
	public String getConfig() {
		return "path: " + f ;
	}

	@Override
	public MatcherType getType() {
		return type;
	}

	@Override
	public int getDBid() {
		return 15;
	}
}
