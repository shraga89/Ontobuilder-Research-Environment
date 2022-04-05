package ac.technion.schemamatching.matching;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import junit.framework.TestCase;

import java.io.*;
import java.util.ArrayList;


public class MatchHandlerTest extends TestCase {
    XmlFileHandler xfh;
    OREMatchHandler ore;
    private Ontology o1;
    private Ontology o2;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ore = OREMatchHandler.getMatchHandler();
        xfh = ore.getXfh();
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

    public void testDefault() {
        MatchInformation mi = ore.match(o1,o2);
        assertNotNull(mi);
        ArrayList<Match> pairs = mi.getCopyOfMatches();
        assertEquals(12, pairs.size());
//        for (Match m : pairs){
//            System.out.println(m.toString());
//        }
    }

    public void testWordNet() {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        flmList.add(FLMList.WordNet.getFLM());
        MatchInformation mi = ore.match(o1,o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        ArrayList<Match> pairs = mi.getCopyOfMatches();
        assertEquals(32, pairs.size());
//        for (Match m : pairs){
//            System.out.println(m.toString());
//        }
    }

    public void testFlmTerm()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBTerm.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testFlmValue()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBValue.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testFlmGraphMatch()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBGraphMatch.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testFlmPrecedence()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBPrecedence.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testFlmSimilarityFlooding()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBSimilarityFlooding.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testFlmDomain()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBDomain.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testFlmTED()
    {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        ArrayList<Match> pairs;
        MatchInformation mi;

        flmList.add(FLMList.OBTED.getFLM());
        mi = ore.match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }

    public void testMultiple() {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        flmList.add(FLMList.WordNet.getFLM());
        flmList.add(FLMList.OBTerm.getFLM());
        flmList.add(FLMList.OBValue.getFLM());
        slmList.add(SLMList.OBThreshold015.getSLM());
        slmList.add(SLMList.OBMaxDelta01.getSLM());
        MatchInformation mi = ore.match(o1,o2, flmList, slmList, new SimpleWeightedEnsemble());
        assertNotNull(mi);
        ArrayList<Match> pairs = mi.getCopyOfMatches();
        assertTrue(pairs.size() > 0);
    }
}
