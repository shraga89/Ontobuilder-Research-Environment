

package ac.technion.schemamatching.test;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.BipartiteGraph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Graph;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.GraphFactory;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.utils.VertexArray;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl.MaxWeightBipartiteMatchingAlgorithm;


/**
 *  
 */
public class MWBATest {

    @SuppressWarnings("unused")
	public static void main(String[] args) {
        
        try {
        
        int lSize = 2000;
        int rSize = 2000;
        
        double[][] adjMatrix = new double[lSize+rSize][lSize+rSize];
        //labels for left vertexes
        ArrayList<Long> leftVertexNames = new ArrayList<Long>();
        
        //labels for right vertexes
        ArrayList<Long> rightVertexNames = new ArrayList<Long>();
        
        //init graph
        for (long i=0;i<lSize;i++) {
            leftVertexNames.add(i);
            rightVertexNames.add(i);         
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

          String[] rightVertexLabels = new String[rightVertexNames.size()];
          String[] leftVertexLabels = new String[leftVertexNames.size()];
            BipartiteGraph bg = GraphFactory.buildBipartiteGraph(adjMatrix,
                    rightVertexNames, leftVertexNames, rightVertexNames.size(),
                    leftVertexNames.size(), false,rightVertexNames.toArray(rightVertexLabels),leftVertexNames.toArray(leftVertexLabels));
            
            VertexArray pot = null;
            pot = new VertexArray(bg, new Double(0));
            MaxWeightBipartiteMatchingAlgorithm aBest = new MaxWeightBipartiteMatchingAlgorithm(
                    bg, pot);
            /*EdgesSet matching = aBest.runAlgorithm();
            System.out.println(matching.printEdgesSet());
            EdgeSet deprecated by Matthias September 2012*/

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

}
