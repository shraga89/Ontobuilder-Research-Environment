/**
 * This package contains classes to manage datasets for schema matching experiments
 */
package ac.technion.schemamatching.testbed;

import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.iem.ontobuilder.io.imports.NativeImporter;
import ac.technion.iem.ontobuilder.io.imports.WSDLImporter;
import ac.technion.iem.ontobuilder.io.imports.XSDImporter;
import ac.technion.iem.ontobuilder.io.imports.XSDImporterUsingXSOM;
import ac.technion.iem.ontobuilder.io.matchimport.CSVMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.MatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.NativeMatchImporter;

/**
 * @author Tomer Sagi
 *
 */
public enum OREDataSetEnum 
{
	OBWebForms(1,"Ontobuilder Web Forms",new NativeImporter(),new NativeMatchImporter(),true,true),
	OBWebFormsTest(21,"Ontobuilder Web Forms",new NativeImporter(),new NativeMatchImporter(),true, true),
	Tel8(2,"Tel-8",null,null,false,false),
	NisBESW(3,"WSDL supplied by SAP",new WSDLImporter(),null,false, false),
	NisBSAPTest(4,"SAP simple test schemas in NisbRDF format",null,null,true,false), 
	OBSynthetic(5,"Synthetic concepts designed by Nimrod Busany",null,null,true, true),
	SAPSchemasRDF(6,"SAP software schemas in NisB rdf format",null,null,true, false),
	Thalia(8,"",new XSDImporter(),new CSVMatchImporter(),true, false),
	XBenchMatch(9,"",new XSDImporter(),null,true, false),
	NisBSynthetic(10,"Synthetic concepts designed by Nimrod Busany",new NativeImporter(),new NativeMatchImporter(),true, false),
	SAPSchemasXSD(11,"SAP software schemas in XSD format",new XSDImporter(),null,true, false),
	eTuner(12,"Small schemas and instances used to generate eTuner synthetic datasets",null,null,true, false),
	I3Con(13,"Ontology alignment contest ICon3 in owl format",null,null,true, false),
	OAEIConference(14,"Ontology alignment conference OAEI conference track",null,null,true, false),
	OAEIBecnhmark(15,"Ontology alignment conference OAEI benchmark track",null,null,true, false),
	NisBUBL(16,"NisB UBL mega schema vs. vendor",null,null,true, false),
	NisPO(17,"NisB Purchase Order schemas (XSD)",new XSDImporterUsingXSOM(),new MappingMatchImporter(),true, false),
	NisPOBig(18,"NisB Big Purchase Order schemas (XSD)",null,null,true, false),
	NisBHungSynthetic(19,"Synthetic schemas based on vendor and ubl (NisBRDF)",null,null,true, false),
	University(20,"University Application Forms",new XSDImporterUsingXSOM(),new MappingMatchImporter(),true, false);
	
	/**
	 * Get a OREDataSetEnum by it's dataset id in O(n)
	 * @param dsid
	 * @return matching datasetEnum or null if no such exists
	 */
	public static OREDataSetEnum getByDbid(int dsid)
	{
		for (OREDataSetEnum d : OREDataSetEnum.values())
			if (d._datasetDBid == dsid)
				return d;
		return null;
	}
	
	public int getDatasetDBid() {
		return _datasetDBid;
	}
	public String getDatasetDescription() {
		return _datasetDescription;
	}
	public Importer getImporter() {
		return _importer;
	}
	private OREDataSetEnum(int dsid,String dsDesc,Importer imp, 
			MatchImporter matchImporter, boolean hasExactMatch, boolean supportsDBLook)
	{
		_datasetDBid = dsid;
		_datasetDescription = dsDesc;
		_importer = imp;
		_matchImp = matchImporter;
		_hasExact = hasExactMatch;
		_supportsDBLookup = supportsDBLook;
	}
	public MatchImporter getMatchImp() {
		return _matchImp;
	}
	public boolean isHasExact() {
		return _hasExact;
	}
	
	/**
	 * 
	 * @return true if the dataset supports database lookup of similarity matrices to reduce runtime. 
	 */
	public boolean isSupportsDBLookUp()
	{
		return _supportsDBLookup;
	}
	private int _datasetDBid;
	private String _datasetDescription;
	private Importer _importer;
	private MatchImporter _matchImp;
	private boolean _hasExact;
	private boolean _supportsDBLookup; 
}
