

package ac.technion.schemamatchings.test;

import java.util.ArrayList;

import schemamatchings.topk.algorithms.MaxWeightBipartiteMatchingAlgorithm;
import schemamatchings.topk.graphs.BipartiteGraph;
import schemamatchings.topk.graphs.EdgesSet;
import schemamatchings.topk.graphs.Graph;
import schemamatchings.topk.graphs.GraphFactory;
import schemamatchings.topk.graphs.util.EdgeArray;
import schemamatchings.topk.graphs.util.VertexArray;

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
        ArrayList leftVertexNames = new ArrayList();
        //labels for right vertexes
        ArrayList rightVertexNames = new ArrayList();
        
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
