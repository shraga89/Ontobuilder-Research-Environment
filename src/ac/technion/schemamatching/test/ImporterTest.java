/**
 * The schemamatchings.test package houses a variety of tests for the various components of Ontobuilder
 */
package ac.technion.schemamatching.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.io.imports.ImportException;
import ac.technion.iem.ontobuilder.io.imports.XSDImporterUsingXSOM;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;



/**
 * Class tests importers
 * 
 * @author Tomer Sagi 
 *
 */
public class ImporterTest
{
	
	private File inFolder;
	private File outFolder;
	/**
	 * Class constructor
	 * @param schemaFile rdf/xsd/wsdl/ontobuilder_dtd file of schema to be decomposed
	 * @param conceptFiles array of rdf/xsd/wsdl/ontobuilder_dtd files of  concepts to be used for schema decomposition
	 * 
	 */
	public ImporterTest(File inputFolder, File outputFolder)
	{
		inFolder = inputFolder;
		outFolder = outputFolder;
	}
	/**
	 * Uses the importer to create an ontology from the file supplied
	 * @param f XSD file to import
	 * @return ontology object for ontobuilder
	 */
	private Ontology getOntologyFromFile(File f) 
	{
		@SuppressWarnings("unused")
		OntoBuilderWrapper obw = new OntoBuilderWrapper();
		Ontology o = null;
		XSDImporterUsingXSOM imp = new XSDImporterUsingXSOM();
		try {
			o = imp.importFile(f);
		} catch (ImportException e) {
			
			e.printStackTrace();
		}	
		return o;
	}

	private static void printInstructions() 
	{
		System.err.println("Missing / invalid argument");
		System.out.println("Usage: java ImporterTest <inputfolderPath> <OutputFolderPath>");
		System.exit(1);
	}
	
	/**
	 * Runs an importer on all files in supplied folder path and outputs
	 * as Ontobuilder xml files to supplied output folder path
	 * 
	 * @param args 0 - inputFolderPath (absolute), 1 - outputFolderPath (absolute) 
	 */
	public static void main(String[] args)
	{
		//Check input
		if (args.length<2) printInstructions();
		File inFolder = new File(args[0]);
		if (!inFolder.isDirectory()) printInstructions();
		File outFolder = new File(args[0]);
		if (!outFolder.isDirectory()) printInstructions();
		ImporterTest it = new ImporterTest(inFolder,outFolder);
		it.convertFiles();
	}
	
	/**
	 * Imports all XSD files in the inFolder and exports them as Ontobuilder XMLs to the outFolder  
	 */
	private void convertFiles() 
	{

		XSDFilter xsdFilter = new XSDFilter();
		for (String fileName : inFolder.list(xsdFilter))
		{
			File f = new File(inFolder,fileName);
			Ontology o = getOntologyFromFile(f);
			if (o == null)
			{
				System.err.println("Failed to import file: " + fileName);
				continue;
			}
			try {
				o.saveToXML(new File(outFolder,o.getName() + ".xml"));
			} catch (IOException e) {
				System.err.println("Failed to export ontology imported from: " + fileName);
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	 * Inline class XSD filter
	 */
	class XSDFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{
			if (name.substring(name.length()-4).equalsIgnoreCase(".xsd"))
				return true;
			return false;
		}	
	}
}
