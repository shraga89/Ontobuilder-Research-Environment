package schemamatchings.test;


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
		String folder = "C:\\AviTest";
		Ontology o1 = obw.readOntologyXMLFile(folder+"\\"+"dbr.xml",true);
		o1.getModel().setLight(true);
		o1.save(new File(folder+"\\"+"dbr.xml"));
		Ontology o2 = obw.readOntologyXMLFile(folder+"\\"+"dbs.xml",true);
		o2.getModel().setLight(true);
		o2.save(new File(folder+"\\"+"dbs.xml"));
		MatchInformation mi = obw.matchOntologies(o1,o2,MatchingAlgorithms.TERM);
		SchemaTranslator st = new SchemaTranslator();
		st.importIdsFromMatchInfo(mi,true);
		MatchMatrix mm = mi.getMatrix();
		st.saveMatchToXML(0, "o1", "o2", "C:\\AviTest\\Match.xml");
		SchemaTranslator exact = SchemaMatchingsUtilities.readXMLBestMatchingFile(folder+"\\"+"dbr_EXACT_dbs.xml",mm);
		exact.saveMatchToXML(0, "o1", "o2", "C:\\dbr_dbs_EXACT.xml");
	
    	System.out.println("P: "+SchemaMatchingsUtilities.calculatePrecision(st, exact)+", R: "+SchemaMatchingsUtilities.calculateRecall(st, exact));
	}
}
