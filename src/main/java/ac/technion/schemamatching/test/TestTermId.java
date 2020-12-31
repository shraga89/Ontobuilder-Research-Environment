package ac.technion.schemamatching.test;


import java.io.File;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;


public class TestTermId {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception
	{
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		XmlFileHandler xhf = new XmlFileHandler();
		String folder = "D:\\Ontology_Pairs_and_Exact_Mappings\\Ontology Pairs and Exact Mappings\\3-national_geographic.xml_6-people.xml_EXACT";
		Ontology o1 = xhf.readOntologyXMLFile(folder+"\\"+"3-national_geographic.xml",true);
		o1.save(new File(folder+"\\"+"3-national_geographic.xml"));
		Ontology o2 = xhf.readOntologyXMLFile(folder+"\\"+"6-people.xml",true);
		o2.save(new File(folder+"\\"+"6-people.xml"));
		MatchInformation mi = obw.matchOntologies(o1,o2,MatchingAlgorithmsNamesEnum.TERM.toString());
		SchemaTranslator st = new SchemaTranslator();
		st.importIdsFromMatchInfo(mi,true);
		MatchMatrix mm = mi.getMatrix();
		st.saveMatchToXML(0, "o1", "o2", "d:\\match.xml");
		SchemaTranslator exact = SchemaMatchingsUtilities.readXMLBestMatchingFile(folder+"\\"+"3-national_geographic.xml_6-people.xml_EXACT.xml",mm);
		exact.saveMatchToXML(0, "o1", "o2", "d:\\match3.xml");
    	System.out.println("P: "+SchemaMatchingsUtilities.calculatePrecision(st, exact)+", R: "+SchemaMatchingsUtilities.calculateRecall(st, exact));
	}
}
