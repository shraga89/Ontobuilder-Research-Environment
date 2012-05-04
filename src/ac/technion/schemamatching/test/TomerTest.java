
package ac.technion.schemamatching.test;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;


public class TomerTest {

	
	public static void main(String[] args) throws Exception{
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		XmlFileHandler xhf = new XmlFileHandler();
		String folder = "D:\\Ontology_Pairs_and_Exact_Mappings\\Ontology Pairs and Exact Mappings\\1-time.xml_2-surfer.xml_EXACT";
		Ontology o1 = xhf.readOntologyXMLFile(folder+"\\"+"1-time.xml",true);
		Ontology o2 = xhf.readOntologyXMLFile(folder+"\\"+"2-surfer.xml",true);
		MatchInformation mi = obw.matchOntologies(o1,o2,MatchingAlgorithmsNamesEnum.TERM.toString());
		SchemaTranslator exact = SchemaMatchingsUtilities.readXMLBestMatchingFile(folder+"\\"+"1-time.xml_2-surfer.xml_EXACT.xml");
		MatchedAttributePair[] pairs = exact.getMatchedPairs();
		MatchMatrix mm = mi.getMatrix();
		for (int i=0;i<pairs.length;i++){
			System.out.println("----------------------");
			System.out.println(pairs[i].getAttribute1()+" --> "+mm.getTermByName(pairs[i].getAttribute1()));
			System.out.println(pairs[i].getAttribute2()+" --> "+mm.getTermByName(pairs[i].getAttribute2()));
			System.out.println("----------------------\n");
		}
		
		
	}
}
