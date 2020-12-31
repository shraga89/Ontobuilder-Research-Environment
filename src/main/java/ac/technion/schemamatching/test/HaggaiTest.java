
package ac.technion.schemamatching.test;

import java.io.File;
import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.io.imports.XSDImporterUsingXSOM;
import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;


public class HaggaiTest {

	
	public static void main(String[] args) throws Exception{
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		XSDImporterUsingXSOM xsdImp = new XSDImporterUsingXSOM();
		String folder = "D:\\Ontology_Pairs_and_Exact_Mappings\\Ontology Pairs and Exact Mappings\\1-time.xml_2-surfer.xml_EXACT";
		Ontology o1  = xsdImp.importFile(new File(folder+"\\"+"1-time.xml"));
		Ontology o2 = xsdImp.importFile(new File(folder+"\\"+"1-time.xml"));
		MatchInformation mi = obw.matchOntologies(o1,o2,MatchingAlgorithmsNamesEnum.TERM.toString());
		MappingMatchImporter imp = new MappingMatchImporter();
		MatchInformation exact = imp.importMatch(mi,new File(folder+"\\"+"1-time.xml_2-surfer.xml_EXACT.xml"));
		ArrayList<Match> pairs = exact.getCopyOfMatches();
		for (Match m : pairs){
			System.out.println(m.toString());
		}
		
		
	}
}
