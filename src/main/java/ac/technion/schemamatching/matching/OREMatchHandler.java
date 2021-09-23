package ac.technion.schemamatching.matching;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.OBmwbg;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for matching schemas without the experimental overhead
 */
public class OREMatchHandler {
    private static OREMatchHandler ore;
    protected OntoBuilderWrapper obw;
    private final XmlFileHandler xfh;

    /**
     * Base constructor, private (Singleton)
     */
    private OREMatchHandler()
    {
        obw = new OntoBuilderWrapper(); //This will fail when not connected to the internet due to validation error of xml
        xfh = new XmlFileHandler();

    }

    public static OREMatchHandler getMatchHandler() {
        if (ore == null) {
            ore = new OREMatchHandler();
        }
        return ore;
    }


    /**
     *
     * @return XML file handler that enables converting XML files to Ontology objects
     */
    public XmlFileHandler getXfh() {
        return xfh;
    }

    /**
     * Default matcher, matches using a single matcher (Term)
     * @param o1 Ontolgoy 1 to be matched
     * @param o2 Ontology 2 to be matched
     *
     * @return MatchInformation object with the result
     */
    public MatchInformation match(Ontology o1, Ontology o2) {
        ArrayList<FirstLineMatcher> flmList = new ArrayList<>();
        ArrayList<SecondLineMatcher> slmList = new ArrayList<>();
        flmList.add(FLMList.OBTerm.getFLM());
        return match(o1, o2, flmList, slmList, new SimpleWeightedEnsemble());
    }

    /**
     * Matches by applying all first line matchers in parallel and then using the 2nd line matchers specified in the order they are given
     * @param o1 Ontology 1 to match
     * @param o2 Ontology 2 to match
     * @param flmList list of first line matchers to use
     * @param slmList list of second line matchers to apploy in sequence on the results of the first line matchers.
     *                If more than one FLM is requested, then the first one must be an aggregation SLM
     * @param ensembleMethod used after the 1LM and before the 2LM //TODO generalize to a matching planner
     * @return MatchInformation object with the result
     */
    public MatchInformation match(Ontology o1, Ontology o2, ArrayList<FirstLineMatcher> flmList
            , ArrayList<SecondLineMatcher> slmList, Ensemble ensembleMethod) {

        //TODO parallelize matching

        Map<String, MatchInformation> flmResults = new HashMap<>(flmList.size());
        Map<String, Double> weights = new HashMap<>(flmResults.size());
        Double uniformWeight = 1.0/flmList.size();
        Double lastWeight = 1.0-(uniformWeight*(flmList.size()-1));
        for (FirstLineMatcher flm : flmList) {
            flmResults.put(flm.getName(), flm.match(o1,o2, false));
            if (weights.size() == flmList.size()-1)
                weights.put(flm.getName(), lastWeight);
            else
                weights.put(flm.getName(), uniformWeight);
        }


        //create the ensemble TODO use provided weights
        MatchInformation res;
        if (weights.size()>1) {
            ensembleMethod.init(flmResults, weights);
            res = ensembleMethod.getWeightedMatch();
        } else
            res = flmResults.get(flmList.get(0).getName());

        //apply second line matchers in order
        if (slmList.size() == 0)
            slmList.add(new OBmwbg());
        for (SecondLineMatcher slm : slmList)
            res = slm.match(res);

        return res;
    }
}


