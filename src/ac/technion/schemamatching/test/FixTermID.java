package ac.technion.schemamatching.test;


import java.io.File;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

/**
 * This class fixes schema pairs from the WebForm dataset 
 * with missing termIDs by calculating and completing all termIDs and 
 * then updating the schema files 
 * @author Tomer Sagi
 *
 */
public class FixTermID {
	public static void main(String[] args) throws Exception
	{
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		XmlFileHandler xhf = new XmlFileHandler();
		String folder = "C:\\Users\\Tomer\\Dropbox\\workspace\\OntobuilderResearchEnvironment\\schema\\WebForm\\www2.inmail24.com.xml_Gmail.xml_EXACT";
		Ontology o1 = xhf.readOntologyXMLFile(folder+"\\"+"www2.inmail24.com.xml",true);
		o1.save(new File(folder+"\\"+"www2.inmail24.com.xml"));
		Ontology o2 = xhf.readOntologyXMLFile(folder+"\\"+"Gmail.xml",true);
		o2.save(new File(folder+"\\"+"Gmail.xml"));
	}
}
