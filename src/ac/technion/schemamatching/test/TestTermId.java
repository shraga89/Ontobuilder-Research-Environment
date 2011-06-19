package ac.technion.schemamatching.test;


import java.io.File;

import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.Ontology;
import com.modica.ontology.match.MatchInformation;

public class TestTermId {
	public static void main(String[] args) throws Exception{
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		String folder = "D:\\Ontology_Pairs_and_Exact_Mappings\\Ontology Pairs and Exact Mappings\\3-national_geographic.xml_6-people.xml_EXACT";
		Ontology o1 = obw.readOntologyXMLFile(folder+"\\"+"3-national_geographic.xml",true);
		o1.save(new File(folder+"\\"+"3-national_geographic.xml"));
		Ontology o2 = obw.readOntologyXMLFile(folder+"\\"+"6-people.xml",true);
		o2.save(new File(folder+"\\"+"6-people.xml"));
		MatchInformation mi = obw.matchOntologies(o1,o2,MatchingAlgorithms.TERM);
		SchemaTranslator st = new SchemaTranslator();
		st.importIdsFromMatchInfo(mi,true);
		MatchMatrix mm = mi.getMatrix();
		st.saveMatchToXML(0, "o1", "o2", "d:\\match.xml");
		SchemaTranslator exact = SchemaMatchingsUtilities.readXMLBestMatchingFile(folder+"\\"+"3-national_geographic.xml_6-people.xml_EXACT.xml",mm);
		exact.saveMatchToXML(0, "o1", "o2", "d:\\match3.xml");
    	System.out.println("P: "+SchemaMatchingsUtilities.calculatePrecision(st, exact)+", R: "+SchemaMatchingsUtilities.calculateRecall(st, exact));
	}
}
