package ac.technion.schemamatching.util;

import java.util.HashMap;
import java.util.Map;

import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.ptnet.PTNet;
import nl.tue.tm.is.ptnet.Transition;

import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.io.PNMLSerializer;

import com.mallardsoft.tuple.Pair;

public class ProcessModelUtils {

	private static PNMLSerializer serializer = new PNMLSerializer();
	
	public static Graph loadGraphFromPNML(String filename){
		PTNet ptnet = PTNet.loadPNML(filename);
		for (nl.tue.tm.is.ptnet.Transition t : ptnet.transitions()) {
			t.setName(t.getName().replace('.', ' ').trim());
			if (t.getName().equals(Transition.SILENT_LABEL))
				t.setName("");
		}
		Graph sg = new Graph(ptnet);
		return sg;
	}
	
	public static Graph loadGraphFromPNMLIncludingjBPTNetSystem(String filename){
		NetSystem system = serializer.parse(filename);
		for (org.jbpt.petri.Transition t : system.getTransitions())
			t.setName(t.getName().replace('.', ' ').trim());
		
		if (system.getMarkedPlaces().isEmpty())
			for (Place p : system.getSourcePlaces())
				system.getMarking().put(p, 1);

		
		Graph sg = loadGraphFromPNML(filename);
		sg.setOriginalNetSystem(system);
		return sg;
	}
	
	public static DirectedGraph graphTransform(Graph g) {
		DirectedGraph result = new DirectedGraph();
		result.setId(g.getId());
		result.setName(g.getId());
		
		Map<Integer,Vertex> vertex2vertex = new HashMap<Integer,Vertex>();

		for (Integer n : g.getVertices()) {
			Vertex v = new Vertex(g.getLabel(n));
			vertex2vertex.put(n, v);
			result.addVertex(v);
		}
		
		for (Pair<Integer, Integer> e : g.getEdges()) {
			Vertex src = vertex2vertex.get(Pair.get1(e));
			Vertex tar = vertex2vertex.get(Pair.get2(e));
			result.addEdge(src, tar);
		}
		return result;
	}



}
