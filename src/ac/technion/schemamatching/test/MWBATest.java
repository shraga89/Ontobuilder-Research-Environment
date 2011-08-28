

package ac.technion.schemamatching.test;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.EdgesSet;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.GraphFactory;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.EdgeArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.MaxWeightBipartiteMatchingAlgorithm;


/**
 *  
 */
public class MWBATest {

    public static void main(String[] args) {
        
        try {
        
        int lSize = 2000;
        int rSize = 2000;
        
        double[][] adjMatrix = new double[lSize+rSize][lSize+rSize];
        //labels for left vertexes
        ArrayList<String> leftVertexNames = new ArrayList<String>();
        //labels for right vertexes
        ArrayList<String> rightVertexNames = new ArrayList<String>();
        
        //init graph
        for (int i=0;i<lSize;i++) {
            leftVertexNames.add(Integer.toString(i));
            rightVertexNames.add(Integer.toString(i));         
        }
        //generate randow bipartite graph
        for (int i=0;i<lSize;i++)
            for (int j=0;j<lSize;j++){
               if (i==j) adjMatrix[i][j] = 0;
               if (i!=j) adjMatrix[i][j] = Graph.INF;
            }
          for (int i=0;i<lSize;i++)
            for (int j=lSize;j<lSize+rSize;j++)
                adjMatrix[i][j] = Math.random();
          for (int i=lSize;i<lSize+rSize;i++)
            for (int j=0;j<lSize+rSize;j++){
               if (i==j) adjMatrix[i][j] = 0;
               if (i!=j) adjMatrix[i][j] = Graph.INF;
            }


            BipartiteGraph bg = GraphFactory.buildBipartiteGraph(adjMatrix,
                    rightVertexNames, leftVertexNames, rightVertexNames.size(),
                    leftVertexNames.size(), false);
            EdgeArray c = null;
            VertexArray pot = null;
            c = new EdgeArray(bg);
            pot = new VertexArray(bg, new Double(0));
            MaxWeightBipartiteMatchingAlgorithm aBest = new MaxWeightBipartiteMatchingAlgorithm(
                    bg, c, pot);
            EdgesSet matching = aBest.runAlgorithm();
            System.out.println(matching.printEdgesSet());

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
