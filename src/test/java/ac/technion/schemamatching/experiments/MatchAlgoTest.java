package ac.technion.schemamatching.experiments;

import junit.framework.TestCase;
import java.io.File;
import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.io.matchimport.NativeMatchImporter;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;


public class MatchAlgoTest extends TestCase {

    public void testTerm() {
        OBExperimentRunner oer = OBExperimentRunner.getOER();
	String folder = "1-time.xml_2-surfer.xml_EXACT"; //TODO add to dataset folder URL from properties file
        Ontology o1 = oer.getXfh().readOntologyXMLFile(folder+"\\"+"1-time.xml",true);
        Ontology o2 = oer.getXfh().readOntologyXMLFile(folder+"\\"+"2-surfer.xml",true);
        MatchInformation mi = oer.getOBW().matchOntologies(o1,o2,MatchingAlgorithmsNamesEnum.TERM.toString());
        NativeMatchImporter imp = new NativeMatchImporter();
        MatchInformation exact = imp.importMatch(mi,new File(folder+"\\"+"1-time.xml_2-surfer.xml_EXACT.xml"));
        ArrayList<Match> pairs = exact.getCopyOfMatches();
        for (Match m : pairs){
            System.out.println(m.toString());
        }
	assertNotNull(pairs);

    }
}
