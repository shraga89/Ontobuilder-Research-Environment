package ac.technion.schemamatching.experiments;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapperException;
import junit.framework.TestCase;

import java.io.*;
import java.util.ArrayList;


public class MatchAlgoTest extends TestCase {
    XmlFileHandler xfh;
    OntoBuilderWrapper obw;
    private Ontology o1;
    private Ontology o2;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        OBExperimentRunner oer = OBExperimentRunner.getOER();
        xfh= oer.getXfh();
        obw = oer.getOBW();
        String dirname = "1-time.xml_2-surfer.xml_EXACT/";
        File testFile1 = getFile(dirname, "1-time.xml");
        File testFile2 = getFile(dirname, "2-surfer.xml");

        o1 = xfh.readOntologyXMLFile(testFile1.getPath(),true);
        o2 = xfh.readOntologyXMLFile(testFile2.getPath(),true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getFile(String dirname, String fname) throws IOException {
        InputStream toLoad = getClass().getClassLoader().getResourceAsStream(dirname + fname);
        assertNotNull(toLoad);
        byte[] buffer = new byte[toLoad.available()];
        toLoad.read(buffer);
        String strTmp = System.getProperty("java.io.tmpdir");
        File testFile1 = new File(strTmp,fname);
        try (OutputStream outStream = new FileOutputStream(testFile1)) {
            outStream.write(buffer);
        }
        return testFile1;
    }

    public void testTerm() throws OntoBuilderWrapperException {
        OBExperimentRunner oer = OBExperimentRunner.getOER();
        MatchInformation mi = oer.getOBW().matchOntologies(o1,o2,MatchingAlgorithmsNamesEnum.TERM.getName());
        assertNotNull(mi);
        ArrayList<Match> pairs = mi.getCopyOfMatches();
        assertEquals(180, pairs.size());
//        for (Match m : pairs){
//            System.out.println(m.toString());
//        }
    }

    public void testWordNet() throws OntoBuilderWrapperException {
        OBExperimentRunner oer = OBExperimentRunner.getOER();
        MatchInformation mi = oer.getOBW().matchOntologies(o1,o2,MatchingAlgorithmsNamesEnum.WordNet.getName());
        assertNotNull(mi);
        ArrayList<Match> pairs = mi.getCopyOfMatches();
        assertEquals(1217, pairs.size());
//        for (Match m : pairs){
//            System.out.println(m.toString());
//        }
    }
}
