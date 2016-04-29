/**
 * This package contains classes to manage datasets for schema matching experiments
 */
package ac.technion.schemamatching.testbed;

import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.iem.ontobuilder.io.imports.NativeImporter;
import ac.technion.iem.ontobuilder.io.imports.OWLImporter;
import ac.technion.iem.ontobuilder.io.imports.PNMLImporter;
import ac.technion.iem.ontobuilder.io.imports.SQLImporter;
import ac.technion.iem.ontobuilder.io.imports.WSDLImporterEasyWSDL;
import ac.technion.iem.ontobuilder.io.imports.XSDImporterUsingXSOM;
import ac.technion.iem.ontobuilder.io.matchimport.CRFMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.CSVMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.MatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.NativeMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.PNMLPairMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.RDFMatchImporter;
//import ac.technion.iem.ontobuilder.io.imports.RDFImporter;


/**
 * @author Tomer Sagi
 *
 */
public enum OREDataSetEnum 
{
	OBWebForms(1, "Ontobuilder Web Forms", new NativeImporter(), new NativeMatchImporter(), true, true, false),									// XML lookup
	Tel8(2, "Tel-8", null, null, false, false, false),
	NisBESW(3, "WSDL supplied by SAP", new WSDLImporterEasyWSDL(), null, false, false, false),
	NisBSAPTest(4, "SAP simple test schemas in NisbRDF format", null, null, true, false, false),
	OBSynthetic(5, "Synthetic concepts designed by Nimrod Busany", null, null, true, true, false),												// XML lookup
	SAPSchemasRDF(6, "SAP software schemas in NisB rdf format", null, null, true, false, false),
	Thalia(8, "University Course Descriptions in XSD format", new XSDImporterUsingXSOM(), new CSVMatchImporter(), true, true, true),			// XSD lookup
	XBenchMatch(9, "", new XSDImporterUsingXSOM(), null, true, false, false),
	NisBSynthetic(10, "Synthetic concepts designed by Nimrod Busany", new NativeImporter(), new NativeMatchImporter(), true, false, false),
	SAPSchemasXSD(11, "SAP software schemas in XSD format", new XSDImporterUsingXSOM(), null, true, true, false),								// XSD lookup
	eTuner(12, "Small schemas and instances used to generate eTuner synthetic datasets", null, null, true, false, false),
	I3Con(13, "Ontology alignment contest ICon3 in owl format", null, null, true, false, false),
	OAEIConference(14, "Ontology alignment conference OAEI conference track", null, null, true, false, false),
	OAEIBecnhmark(15, "Ontology alignment conference OAEI benchmark track", new OWLImporter(), new RDFMatchImporter(), true, false, false),
	NisBUBL(16, "NisB UBL mega schema vs. vendor", null, null, true, false, false),
	NisPO(17, "NisB Purchase Order schemas (XSD)", new XSDImporterUsingXSOM(), new MappingMatchImporter(), true, true, true),					// XSD lookup
	NisPOBig(18, "NisB Big Purchase Order schemas (XSD)", null, null, true, true, false),														// XSD lookup
	NisBHungSynthetic(19, "Synthetic schemas based on vendor and ubl (NisBRDF)", null, null, true, false, false),
	University(20, "University Application Forms", new XSDImporterUsingXSOM(), new MappingMatchImporter(), true, true, false),					// XSD lookup
	IMAP(21,"Small scale relational schemas with instances", new XSDImporterUsingXSOM(),new CSVMatchImporter(),true,false,true),
	CRF(22,"Fiat Research Contributed Schemas", new XSDImporterUsingXSOM(),new CRFMatchImporter(),true,false, false),
	GeoDataSQL(23,"GeoData - sql", new SQLImporter(), new CSVMatchImporter(),true, false, false),
	PNML(24,"Petri-Net Represented Business Processes", new PNMLImporter(), new PNMLPairMatchImporter(),true, false, false),
	RealEstate(25,"Real Estate XSD and XML with instances",new XSDImporterUsingXSOM(),new CSVMatchImporter(),true, false, true),
	Orders(26,"Orders XSD and XML with instances",new XSDImporterUsingXSOM(),new CSVMatchImporter(),true, false, true),
	Articles(27,"Articles XSD and XML with instances",new XSDImporterUsingXSOM(),new CSVMatchImporter(),true, false, true)
	;//Lod(25, "Linked open data - rdf", new RDFImporter(),new MappingMatchImporter(),true, false, false);
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
			MatchImporter matchImporter, boolean hasExactMatch, 
			boolean supportsDBLook, boolean hasInstances)
	{
		_datasetDBid = dsid;
		_datasetDescription = dsDesc;
		_importer = imp;
		_matchImp = matchImporter;
		_hasExact = hasExactMatch;
		_supportsDBLookup = supportsDBLook;
		_hasInstances = hasInstances;
	}
	public MatchImporter getMatchImp() {
		return _matchImp;
	}
	public boolean isHasExact() {
		return _hasExact;
	}
	
	public boolean isHasInstances()	{
		return _hasInstances;
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
	private boolean _hasInstances;
	
	public boolean isXSD() {
		int[] XSDdomainsArray = {8, 11, 17, 18, 20};
		int arrLength = XSDdomainsArray.length;
		for (int i = 0; i < arrLength; i++) {
			if (XSDdomainsArray[i] == _datasetDBid)
				return true;
		}
		return false;
	}
	
}
