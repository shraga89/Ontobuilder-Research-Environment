package ac.technion.schemamatching.experiments.pairwise.topkranking;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FeaturesFrequencyChecker {
 
	public static void main(String[] args) throws Exception {
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/term + dominants webForms/models", "ndcg_err_3_5");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/AMC Token Path + Ontobuilder Threshold webForms/models", "ndcg_err_4_5");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/term + maxdelta webForms/models", "ndcg_err_5_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/term + mwbg webForms/models", "err3");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/term + sm webForms/models", "err_err_5_5");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/term + th webForms/models", "ndcg_err_3_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/tokenPath + dominants webForms/models", "err_err_5_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/tokenPath + maxdelta webForms/models", "err_err_5_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/tokenPath + mwbg webForms/models", "err_err_5_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/tokenPath + sm webForms/models", "err_err_5_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/wordNet + maxdelta webForms/models", "ndcg_err_3_5");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/wordNet + mwbg webForms/models", "ndcg_err_3_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/wordNet + th webForms/models", "err_err_5_5");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/WordNet Jiang Conrath + Ontobuilder Dominants webForms/models", "err_err_5_10");
//		calcFeatures("C:/ORE/ontobuilder-research-environment/topkRanking/New predictors/webForms/WordNet Jiang Conrath + Ontobuilder Stable Marriage webForms/models", "err_err_5_5");
		File[] files = new File("C:\\ORE\\ontobuilder-research-environment\\topkRanking\\New predictors\\OAEI\\MORE\\").listFiles();
		for (File f : files){
			if (f.isDirectory()) {
				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(f + "/features.csv")), true));
				String model = "";
				File[] ifFiles = f.listFiles();
				for (File fIn : ifFiles){
					if (fIn.toString().contains("L2R Binary Golden")){
						int begin = fIn.toString().length()-17;
						if (fIn.toString().contains("ndcg")){
							begin --;
							if (fIn.toString().contains("ndcg_ndcg")){
								begin --;
							}
						}
						model = fIn.toString().substring(begin, fIn.toString().length()-4);
//						System.out.println(model);
						calcFeatures(f.getAbsolutePath()+ "/models/", model);
					}
				}
			}
		}
	}
	
	private static void calcFeatures(String fileName, String modelName) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException{
		File modelsLoc =  new File(fileName);
		File [] models = modelsLoc.listFiles();
		String modelsStr = "<ensemble>\n";
	    for (int i = 0; i < models.length; i++){
	        if ((models[i].isFile()) && (models[i].toString().endsWith(modelName))){ 
	        	String tempStr = new String(Files.readAllBytes(Paths.get(models[i].getPath())));
	        	tempStr = tempStr.substring(tempStr.indexOf("<tree"),tempStr.indexOf("</ensemble>"));
	        	modelsStr += tempStr;
	        }
	    }
	    modelsStr += "\n</ensemble>";
		InputStream in = new ByteArrayInputStream(modelsStr.getBytes());					
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(in);
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression expr = xpath.compile("/ensemble/tree");		
		NodeList trees = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		Map<String, Integer> totalFeature2count = new HashMap<>();
		for (int i = 0; i < trees.getLength(); ++i)
		{
			Map<String, Integer> feature2count = new HashMap<>();
			Node treeNode = trees.item(i);
			XPath featuresXpath = xPathfactory.newXPath();
			XPathExpression features = featuresXpath.compile("/ensemble/tree[" + i+ "]//feature/text()");
			NodeList featureNodes = (NodeList) features.evaluate(treeNode, XPathConstants.NODESET);
			for (int j = 0; j < featureNodes.getLength(); ++j)
			{
				if (featureNodes.item(j).getNodeValue().isEmpty())
				{
					continue;
				}
				putValue(feature2count, featureNodes.item(j));
				putValue(totalFeature2count, featureNodes.item(j));
			}
//			System.out.println("Tree #" + i);
//			System.out.println("---------------");
//			printFeaturesCount(feature2count);
		}
		System.out.println("-------" + fileName + "-" + modelName + "--------");
		printFeaturesCount(totalFeature2count);
	}
	
	private static void printFeaturesCount(Map<String, Integer> feature2count) {
//		System.out.println("feature,count");
		Double sumFeatures = 0.0;
		for (Map.Entry<String, Integer> entry : feature2count.entrySet())
		{
//			System.out.println(entry.getKey() + "," + entry.getValue());
			sumFeatures += entry.getValue();
		}
		System.out.println("feature,count");
		for (Map.Entry<String, Integer> entry : feature2count.entrySet())
		{
			System.out.println(entry.getKey() + "," + (entry.getValue()/sumFeatures));
		}
		
	}

	private static void putValue(Map<String, Integer> feature2count, Node node) {
		Integer counter = feature2count.putIfAbsent(node.getNodeValue().trim(), 0);
		if (counter == null)
		{
			counter = 0;
		}
		feature2count.put(node.getNodeValue().trim(), ++counter);
	}
}
