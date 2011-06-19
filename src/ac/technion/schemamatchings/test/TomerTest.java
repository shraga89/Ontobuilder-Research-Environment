
package ac.technion.schemamatchings.test;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.Ontology;
import com.modica.ontology.match.MatchInformation;


public class TomerTest {

	
	public static void main(String[] args) throws Exception{
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		String folder = "D:\\Ontology_Pairs_and_Exact_Mappings\\Ontology Pairs and Exact Mappings\\1-time.xml_2-surfer.xml_EXACT";
		Ontology o1 = obw.readOntologyXMLFile(folder+"\\"+"1-time.xml",true);
		Ontology o2 = obw.readOntologyXMLFile(folder+"\\"+"2-surfer.xml",true);
		MatchInformation mi = obw.matchOntologies(o1,o2,MatchingAlgorithms.TERM);
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
