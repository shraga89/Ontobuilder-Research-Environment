package ac.technion.schemamatching.matchers.firstline; 
import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileReader; 
import java.io.StringReader; 
import java.util.ArrayList; 
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections; 
public class MaximalCliquesWithPivot {

    int nodesCount;
    public ArrayList<ArrayList<Vertex>> cliques = new ArrayList<ArrayList<Vertex>>(); 
    HashMap<Integer, Vertex> graph = new HashMap<Integer,Vertex>(); 

    public class Vertex implements Comparable<Vertex> {
        int id; 

        int degree; 
        ArrayList<Vertex> nbrs = new ArrayList<Vertex>(); 
        
        
        
        public int getID() {
            return id; 
        } 

        public void setID(int id) {
            this.id = id; 
        } 
        
        
        public int getDegree() {
            return degree; 
        } 

        public void setDegree(int degree) {
            this.degree = degree; 
        } 

        public ArrayList<Vertex> getNbrs() { 
            return nbrs; 
        } 

        public void setNbrs(ArrayList<Vertex> nbrs) {
            this.nbrs = nbrs; 
        } 

        public void addNbr(Vertex y) {
            this.nbrs.add(y); 
            if (!y.getNbrs().contains(y)) { 
                y.getNbrs().add(this); 
                y.degree++; 
            } 
            this.degree++; 

        } 

        public void removeNbr(Vertex y) {
            this.nbrs.remove(y); 
            if (y.getNbrs().contains(y)) { 
                y.getNbrs().remove(this); 
                y.degree--; 
            } 
            this.degree--; 

        } 
        

        @Override 
        public int compareTo(Vertex o) {
            if (this.degree < o.degree) {
                return -1; 
            } 
            if (this.degree > o.degree) {
                return 1;
            } 
            return 0; 
        } 

        public String toString() { 
            return "" + id; 
        } 
    } 

    void initGraph(HashSet<Integer> vertexesSet) { 
        graph.clear(); 
        for (Integer i : vertexesSet) {
            Vertex V = new Vertex(); 
            V.setID(i); 
            graph.put(i,V); 
        } 
    } 

    int readTotalGraphCount(BufferedReader bufReader) throws Exception {

        return Integer.parseInt(bufReader.readLine()); 
    } 

    // Reads Input 
    public void readNextGraph(BufferedReader bufReader, HashSet<Integer> vertexesSet) throws Exception {
        try { 
            nodesCount = Integer.parseInt(bufReader.readLine()); 
            int edgesCount = Integer.parseInt(bufReader.readLine());
            initGraph(vertexesSet); 

            for (int k = 0; k < edgesCount; k++) {
                String[] strArr = bufReader.readLine().split(" "); 
                int u = Integer.parseInt(strArr[0]);
                int v = Integer.parseInt(strArr[1]);
                Vertex vertU = graph.get(u); 
                Vertex vertV = graph.get(v); 
                vertU.addNbr(vertV); 

            } 

        } catch (Exception e) { 
            e.printStackTrace(); 
            throw e; 
        } 
    } 

    // Finds nbrs of vertex i 
    ArrayList<Vertex> getNbrs(Vertex v) { 
        int i = v.getID(); 
        return graph.get(i).nbrs; 
    } 

    // Intersection of two sets 
    ArrayList<Vertex> intersect(ArrayList<Vertex> arlFirst, 
            ArrayList<Vertex> arlSecond) { 
        ArrayList<Vertex> arlHold = new ArrayList<Vertex>(arlFirst); 
        arlHold.retainAll(arlSecond); 
        return arlHold; 
    } 

    // Union of two sets 
    ArrayList<Vertex> union(ArrayList<Vertex> arlFirst, 
            ArrayList<Vertex> arlSecond) { 
        ArrayList<Vertex> arlHold = new ArrayList<Vertex>(arlFirst); 
        arlHold.addAll(arlSecond); 
        return arlHold; 
    } 

    // Removes the neigbours 
    ArrayList<Vertex> removeNbrs(ArrayList<Vertex> arlFirst, Vertex v) { 
        ArrayList<Vertex> arlHold = new ArrayList<Vertex>(arlFirst); 
        arlHold.removeAll(v.getNbrs()); 
        return arlHold; 
    } 

    // Version with a Pivot 
    void Bron_KerboschWithPivot(ArrayList<Vertex> R, ArrayList<Vertex> P,
            ArrayList<Vertex> X, String pre) { 

     //   System.out.print(pre + " " + printSet(R) + ", " + printSet(P) + ", " 
     //           + printSet(X)); 
        if ((P.size() == 0) && (X.size() == 0)) {
        //    printClique(R);  
            maxCliques(R);
            return; 
        } 
     //   System.out.println(); 
        ArrayList<Vertex> P1 = new ArrayList<Vertex>(P); 
        // Find Pivot 
        Vertex u = getMaxDegreeVertex(union(P, X)); 

    //    System.out.println("" + pre + " Pivot is " + (u.id)); 
        // P = P / Nbrs(u) 
        P = removeNbrs(P, u); 

        for (Vertex v : P) { 
            R.add(v); 
            Bron_KerboschWithPivot(R, intersect(P1, getNbrs(v)), 
                    intersect(X, getNbrs(v)), pre + "\t"); 
            R.remove(v); 
            P1.remove(v); 
            X.add(v); 
        } 
    } 

    Vertex getMaxDegreeVertex(ArrayList<Vertex> g) { 
        Collections.sort(g);
        return g.get(g.size() - 1);
    } 

    public void Bron_KerboschPivotExecute() { 

        ArrayList<Vertex> X = new ArrayList<Vertex>(); 
        ArrayList<Vertex> R = new ArrayList<Vertex>(); 
        ArrayList<Vertex> P = new ArrayList<Vertex>(graph.values()); 
        Bron_KerboschWithPivot(R, P, X, "");
      //  printCliques(cliques);
    } 

    void printClique(ArrayList<Vertex> R) { 
        System.out.print("  --- Maximal Clique : "); 
        for (Vertex v : R) { 
            System.out.print(" " + (v.getID())); 
        } 
        System.out.println(); 
    } 
    public void maxCliques(ArrayList<Vertex> R) {
    	ArrayList<Vertex> x= new ArrayList<Vertex>(R);
    	cliques.add(x);
    }
    
   public void printCliques(ArrayList<ArrayList<Vertex>> x){
	   for (ArrayList<Vertex> L : x) {
       	for (Vertex	v : L)
       	{
       		System.out.print(" " + (v.getID())); 
       	}
       	System.out.print("\n " );
       }
   }

    String printSet(ArrayList<Vertex> Y) { 
        StringBuilder strBuild = new StringBuilder(); 

        strBuild.append("{"); 
        for (Vertex v : Y) { 
            strBuild.append("" + (v.getID()) + ","); 
        } 
        if (strBuild.length() != 1) {
            strBuild.setLength(strBuild.length() - 1); 
        } 
        strBuild.append("}"); 
        return strBuild.toString(); 
    } 


    public static void main(String[] args) {
      /*  BufferedReader bufReader = null; 
        if (args.length > 0) {
            // Unit Test Mode 
            bufReader = new BufferedReader(new StringReader(
                    "1\n5\n7\n0 1\n0 2\n0 3\n0 4\n1 2\n2 3\n3 4\n")); 
        } else { 
            File file = new File("C:\\graphAlgos\\mc1.txt"); 
            try { 
                bufReader = new BufferedReader(new FileReader(file));
            } catch (Exception e) { 
                e.printStackTrace(); 
                return; 
            } 
        } 
        MaximalCliquesWithPivot ff = new MaximalCliquesWithPivot();
        try { 
            int totalGraphs = ff.readTotalGraphCount(bufReader);
            System.out.println("Max Cliques with Pivot"); 
            for (int i = 0; i < totalGraphs; i++) {
                System.out.println("************** Start Graph " + (i + 1) 
                        + "******************************");
                ff.readNextGraph(bufReader, ); 
                ff.Bron_KerboschPivotExecute(); 

            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
            System.err.println("Exiting : " + e); 
        } finally { 
            try { 
                bufReader.close(); 
            } catch (Exception f) { 

            } 
        }*/ 
    } 
} 

//http://algos.org/maximal-cliquesbron-kerbosch-without-pivot-java/