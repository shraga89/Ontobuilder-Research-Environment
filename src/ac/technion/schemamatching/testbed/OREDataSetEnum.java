/**
 * This package dontains classes to manage datasets for schema matching experiments
 */
package ac.technion.schemamatching.testbed;

import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.iem.ontobuilder.io.imports.NativeImporter;
import ac.technion.iem.ontobuilder.io.imports.WSDLImporter;
import ac.technion.iem.ontobuilder.io.imports.XSDImporter;
import ac.technion.iem.ontobuilder.io.imports.XSDImporterUsingXSOM;
import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.MatchImporter;
import ac.technion.iem.ontobuilder.io.matchimport.NativeMatchImporter;

/**
 * @author Tomer Sagi
 *
 */
public enum OREDataSetEnum 
{
	OBWebForms(1,"Ontobuilder Web Forms",new NativeImporter(),new NativeMatchImporter(),true),
	OBWebFormsTest(21,"Ontobuilder Web Forms",new NativeImporter(),new NativeMatchImporter(),true),
	Tel8(2,"Tel-8",null,null,false),
	NisBESW(3,"WSDL supplied by SAP",new WSDLImporter(),null,false),
	NisBSAPTest(4,"SAP simple test schemas in NisbRDF format",null,null,true), 
	OBSynthetic(5,"Synthetic concepts designed by Nimrod Busany",null,null,true),
	SAPSchemasRDF(6,"SAP software schemas in NisB rdf format",null,null,true),
	Thalia(8,"",new XSDImporter(),null,true),
	XBenchMatch(9,"",new XSDImporter(),null,true),
	NisBSynthetic(10,"Synthetic concepts designed by Nimrod Busany",new NativeImporter(),new NativeMatchImporter(),true),
	SAPSchemasXSD(11,"SAP software schemas in XSD format",new XSDImporter(),null,true),
	eTuner(12,"Small schemas and instances used to generate eTuner synthetic datasets",null,null,true),
	I3Con(13,"Ontology alignment contest ICon3 in owl format",null,null,true),
	OAEIConference(14,"Ontology alignment conference OAEI conference track",null,null,true),
	OAEIBecnhmark(15,"Ontology alignment conference OAEI benchmark track",null,null,true),
	NisBUBL(16,"NisB UBL mega schema vs. vendor",null,null,true),
	NisPO(17,"NisB Purchase Order schemas (XSD)",new XSDImporterUsingXSOM(),new MappingMatchImporter(),true),
	NisPOBig(18,"NisB Big Purchase Order schemas (XSD)",null,null,true),
	NisBHungSynthetic(19,"Synthetic schemas based on vendor and ubl (NisBRDF)",null,null,true),
	University(20,"University Application Forms",new XSDImporterUsingXSOM(),new MappingMatchImporter(),true);
	
	/**
	 * Get a OREDataSetEnum by it's dataset id in O(n)
	 * @param dsid
	 * @return matching datasetEnum or null if no such exists
	 */
	public static OREDataSetEnum getByDbid(int dsid)
	{
		for (OREDataSetEnum d : OREDataSetEnum.values())
			if (d.datasetDBid == dsid)
				return d;
		return null;
	}
	
	public int getDatasetDBid() {
		return datasetDBid;
	}
	public String getDatasetDescription() {
		return datasetDescription;
	}
	public Importer getImporter() {
		return importer;
	}
	private OREDataSetEnum(int dsid,String dsDesc,Importer imp, MatchImporter matchImporter, boolean hasExactMatch)
	{
		datasetDBid = dsid;
		datasetDescription = dsDesc;
		importer = imp;
		matchImp = matchImporter;
		hasExact = hasExactMatch;
	}
	public MatchImporter getMatchImp() {
		return matchImp;
	}
	public boolean isHasExact() {
		return hasExact;
	}
	private int datasetDBid;
	private String datasetDescription;
	private Importer importer;
	private MatchImporter matchImp;
	private boolean hasExact;
}
