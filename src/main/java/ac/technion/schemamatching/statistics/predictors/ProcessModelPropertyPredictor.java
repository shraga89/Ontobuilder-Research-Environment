package ac.technion.schemamatching.statistics.predictors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage.LanguageCode;

import org.jbpt.algo.graph.DirectedGraphAlgorithms;
import org.jbpt.algo.graph.TransitiveClosure;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.RelSetType;
import org.jbpt.bp.construct.BPCreatorUnfolding;
import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.behavior.ConcurrencyRelation;
import org.jbpt.petri.io.PNMLSerializer;

import semanticTools.SemanticUtils;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.schemamatching.util.ProcessModelUtils;


public class ProcessModelPropertyPredictor implements Predictor {

	public enum ProcessModelProperty {
		/*
		 * Text Syntax Properties
		 */
		AvgLengthOfLabelsRelative,
		AvgLengthOfLabelsAbsolute,
		
		/*
		 * Text Semantics Properties
		 */
		NumberLabelsWithActionsInWordNet,
		NumberLabelsWithObectsInWordNet,
		AvgNumberActionSynsetsRelative,
		AvgNumberActionSynsetsAbsolute,
		AvgNumberObjectSynsetsRelative,
		AvgNumberObjectSynsetsAbsolute,
		
		/*
		 * Structural Properties
		 */
		Size,
		RPSTDepthRelative,
		RPSTWidthRelative,
		RPSTDepthAbsolute,
		RPSTWidthAbsolute,
		NumberRPSTFragmentTypesRelative,
		NumberCommonRPSTFragmentTypesRelative,
		StructurednessRelative,
		StructurednessAbsolute,
		NodesInCycle,
		NumberSourceNodes,
		NumberSinkNodes,
		AvgNodeDegreeRelative,
		AvgNodeDegreeAbsolute,
		MaxNodeDegreeRelative,
		MaxNodeDegreeAbsolute,
		
		/*
		 * Behavioural Properties
		 */
		SizeExclusivenessRelationRelative,
		SizeStrictOrderRelationRelative,
		SizeConcurrencyRelationRelative,
		SizeExclusivenessRelationAbsolute,
		SizeStrictOrderRelationAbsolute,
		SizeConcurrencyRelationAbsolute,
		
	}
	
	private ProcessModelProperty currentProperty = ProcessModelProperty.AvgLengthOfLabelsRelative;
	
	private Ontology candidate;
	private Ontology target;
	
	private LanguageCode languageCode;
	
	private PNMLSerializer pnmlSerializer = new PNMLSerializer();

	
	public ProcessModelPropertyPredictor(Ontology candidate, Ontology target, LanguageCode languageCode) {
		this.candidate = candidate;
		this.target = target;
		this.languageCode = languageCode;
	}
	
	public void setProperty(ProcessModelProperty property) {
		this.currentProperty = property;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return this.currentProperty.toString();
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#newRow()
	 */
	public void newRow() { }

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#visitColumn(double)
	 */
	public void visitColumn(double val) { }

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols) { }

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		double score = 0.0;
		
		NetSystem net1 = pnmlSerializer.parse(candidate.getFile().getPath());
		if (net1.getMarkedPlaces().isEmpty())
			for (Place p : net1.getSourcePlaces())
				net1.getMarking().put(p, 1);
		NetSystem net2 = pnmlSerializer.parse(target.getFile().getPath());
		if (net2.getMarkedPlaces().isEmpty())
			for (Place p : net2.getSourcePlaces())
				net2.getMarking().put(p, 1);
		
		DirectedGraphAlgorithms<Flow, Node> dga = new DirectedGraphAlgorithms<>();
		
		Graph sg1 = ProcessModelUtils.loadGraphFromPNML(candidate.getFile().getPath());
		Graph sg2 = ProcessModelUtils.loadGraphFromPNML(target.getFile().getPath());
		
		DirectedGraph dg1 = ProcessModelUtils.graphTransform(sg1);
		DirectedGraph dg2 = ProcessModelUtils.graphTransform(sg2);
		
		switch (this.currentProperty) {
		/*
		 * Text Syntax Properties
		 */
		case AvgLengthOfLabelsRelative:
			double lengthSum1 = 0.0;
			for (Term t : candidate.getTerms(true))
				lengthSum1 += t.getName().length();
			double avg1 = lengthSum1 / (double) candidate.getTermsCount();
			double lengthSum2 = 0.0;
			for (Term t : target.getTerms(true))
				lengthSum2 += t.getName().length();
			double avg2 = lengthSum2 / (double) target.getTermsCount();
			
			score = 1.0 - (
					(double)Math.abs(avg1 - avg2) / (double)Math.max(avg1, avg2));
			break;

		case AvgLengthOfLabelsAbsolute:
			lengthSum1 = 0.0;
			for (Term t : candidate.getTerms(true))
				lengthSum1 += t.getName().length();
			avg1 = lengthSum1 / (double) candidate.getTermsCount();
			lengthSum2 = 0.0;
			for (Term t : target.getTerms(true))
				lengthSum2 += t.getName().length();
			avg2 = lengthSum2 / (double) target.getTermsCount();
			
			score = (1.0 - (double)Math.max(0,1.0-0.01*Math.pow(avg1-20,2)))/2.0 
					+ (1.0 - (double)Math.max(0,1.0-0.01*Math.pow(avg2-20,2)))/2.0;
					
			break;

			
		/*
		 * Text Semantics Properties
		 */
		case NumberLabelsWithActionsInWordNet:
			int numberLabels1 = 0;
			for (Term t : candidate.getTerms(true))
				numberLabels1 += (SemanticUtils.actionInWordNet(this.languageCode, t.getName(), sg1))?1:0;
			int numberLabels2 = 0;
			for (Term t : target.getTerms(true))
				numberLabels2 += (SemanticUtils.actionInWordNet(this.languageCode, t.getName(), sg2))?1:0;
			
			score = ((double)numberLabels1 / (double)candidate.getTerms(true).size())/2.0 
					+ ((double)numberLabels2 / (double)target.getTerms(true).size())/2.0 ;
			break;
		
		case NumberLabelsWithObectsInWordNet:
			numberLabels1 = 0;
			for (Term t : candidate.getTerms(true))
				numberLabels1 += (SemanticUtils.objectInWordNet(this.languageCode, t.getName(), sg1))?1:0;
			numberLabels2 = 0;
			for (Term t : target.getTerms(true))
				numberLabels2 += (SemanticUtils.objectInWordNet(this.languageCode, t.getName(), sg2))?1:0;
			
			score = ((double)numberLabels1 / (double)candidate.getTerms(true).size())/2.0 
					+ ((double)numberLabels2 / (double)target.getTerms(true).size())/2.0 ;
			break;

		case AvgNumberActionSynsetsRelative:
			double synSum1 = 0.0;
			for (Term t : candidate.getTerms(true)) 
				synSum1 += SemanticUtils.getNumberOfObjectSynsetsInWordNet(languageCode, t.getName(), sg1);
			double avgSyn1 = synSum1 / (double) candidate.getTermsCount();
			double synSum2 = 0.0;
			for (Term t : target.getTerms(true))
				synSum2 += SemanticUtils.getNumberOfObjectSynsetsInWordNet(languageCode, t.getName(), sg2);
			double avgSyn2 = synSum2 / (double) target.getTermsCount();
			
			score = 1.0 - (
					(double)Math.abs(avgSyn1 - avgSyn2) / (double)Math.max(avgSyn1, avgSyn2));
			break;

		case AvgNumberActionSynsetsAbsolute:
			synSum1 = 0.0;
			for (Term t : candidate.getTerms(true)) {
				int number = SemanticUtils.getNumberOfActionSynsetsInWordNet(languageCode, t.getName(), sg1);
				synSum1 += (number > 0)? number : 1;
			}
			avgSyn1 = synSum1 / (double) candidate.getTermsCount();
			synSum2 = 0.0;
			for (Term t : target.getTerms(true)){
				int number = SemanticUtils.getNumberOfActionSynsetsInWordNet(languageCode, t.getName(), sg2);
				synSum2 += (number > 0)? number : 1;
			}
			avgSyn2 = synSum2 / (double) target.getTermsCount();
			
			score = (1.0 - (double)Math.max(0,1.0-0.005*Math.pow(avgSyn1,2)))/2.0 
					+ (1.0 - (double)Math.max(0,1.0-0.005*Math.pow(avgSyn2,2)))/2.0;
			break;

		case AvgNumberObjectSynsetsRelative:
			synSum1 = 0.0;
			for (Term t : candidate.getTerms(true))
				synSum1 += SemanticUtils.getNumberOfObjectSynsetsInWordNet(languageCode, t.getName(), sg1);
			avgSyn1 = synSum1 / (double) candidate.getTermsCount();
			synSum2 = 0.0;
			for (Term t : target.getTerms(true))
				synSum2 += SemanticUtils.getNumberOfObjectSynsetsInWordNet(languageCode, t.getName(), sg2);
			avgSyn2 = synSum2 / (double) target.getTermsCount();
			
			score = 1.0 - (
					(double)Math.abs(avgSyn1 - avgSyn2) / (double)Math.max(avgSyn1, avgSyn2));
			break;

		case AvgNumberObjectSynsetsAbsolute:
			synSum1 = 0.0;
			for (Term t : candidate.getTerms(true)) {
				int number = SemanticUtils.getNumberOfActionSynsetsInWordNet(languageCode, t.getName(), sg1);
				synSum1 += (number > 0)? number : 1;
			}
			avgSyn1 = synSum1 / (double) candidate.getTermsCount();
			synSum2 = 0.0;
			for (Term t : target.getTerms(true)) {
				int number = SemanticUtils.getNumberOfActionSynsetsInWordNet(languageCode, t.getName(), sg2);
				synSum2 += (number > 0)? number : 1;
			}
			avgSyn2 = synSum2 / (double) target.getTermsCount();
			
			score = (1.0 - (double)Math.max(0,1.0-0.005*Math.pow(avgSyn1,2)))/2.0 
					+ (1.0 - (double)Math.max(0,1.0-0.005*Math.pow(avgSyn2,2)))/2.0;
			break;

			
			
		/*
		 * Structural Properties
		 */
		case Size:
			score = 1.0 - (
					(double)Math.abs(candidate.getTermsCount() - target.getTermsCount()) 
							/ (double)Math.max(candidate.getTermsCount(), target.getTermsCount()));
			break;
			
		case RPSTDepthRelative:
			RPST<DirectedEdge, Vertex> rpst1 = new RPST<>(dg1);
			int depth1 = getMaxRPSTDepth(rpst1);
			
			RPST<DirectedEdge, Vertex> rpst2 = new RPST<>(dg2);
			int depth2 = getMaxRPSTDepth(rpst2);
			
			score = 1.0 - (
					(double)Math.abs(depth1 - depth2) 
							/ (double)Math.max(depth1,depth2));
			break;

		case RPSTWidthRelative:
			rpst1 = new RPST<>(dg1);
			int width1 = getMaxRPSTWidth(rpst1);
			
			rpst2 = new RPST<>(dg2);
			int width2 = getMaxRPSTWidth(rpst2);
			
			score = 1.0 - (
					(double)Math.abs(width1 - width2) 
							/ (double)Math.max(width1,width2));
			break;

		case RPSTDepthAbsolute:
			rpst1 = new RPST<>(dg1);
			depth1 = getMaxRPSTDepth(rpst1);
			
			rpst2 = new RPST<>(dg2);
			depth2 = getMaxRPSTDepth(rpst2);
			
			score = (1.0 - (double)Math.max(0,1.0-0.02*Math.pow(depth1-1,2)))/2.0 
				+ (1.0 - (double)Math.max(0,1.0-0.02*Math.pow(depth2-1,2)))/2.0;
			break;

		case RPSTWidthAbsolute:
			rpst1 = new RPST<>(dg1);
			width1 = getMaxRPSTWidth(rpst1);
			
			rpst2 = new RPST<>(dg2);
			width2 = getMaxRPSTWidth(rpst2);
			
			score = (1.0 - (double)Math.max(0,1.0-0.001*Math.pow(width1-1,2)))/2.0 
					+ (1.0 - (double)Math.max(0,1.0-0.001*Math.pow(width2-1,2)))/2.0;
			break;

		case NumberRPSTFragmentTypesRelative:
			rpst1 = new RPST<>(dg1);
			Set<TCType> types1 = new HashSet<>();
			for (IRPSTNode<DirectedEdge, Vertex> n : rpst1.getVertices())
				types1.add(n.getType());
			
			rpst2 = new RPST<>(dg2);
			Set<TCType> types2 = new HashSet<>();
			for (IRPSTNode<DirectedEdge, Vertex> n : rpst2.getVertices())
				types2.add(n.getType());
			
			score = 1.0 - (
					(double)Math.abs(types1.size() - types2.size()) 
							/ (double)Math.max(types1.size(),types2.size()));
			break;
			
		case NumberCommonRPSTFragmentTypesRelative:
			rpst1 = new RPST<>(dg1);
			types1 = new HashSet<>();
			for (IRPSTNode<DirectedEdge, Vertex> n : rpst1.getVertices())
				types1.add(n.getType());
			
			rpst2 = new RPST<>(dg2);
			types2 = new HashSet<>();
			for (IRPSTNode<DirectedEdge, Vertex> n : rpst2.getVertices())
				types2.add(n.getType());
			
			Set<TCType> all = new HashSet<>(types1);
			all.addAll(types2);
			types1.retainAll(types2);
			
			score = ((double)types1.size()) / ((double) all.size());
			break;

		case StructurednessRelative:
			//(number of nodes in rigids) / all nodes.
			rpst1 = new RPST<>(dg1);
			int allNodes1 = net1.getTransitions().size();
			Set<Vertex> nodesInRigid1 = new HashSet<>(); 
			for (IRPSTNode<DirectedEdge, Vertex> n :rpst1.getRPSTNodes(TCType.RIGID)) {
				for (DirectedEdge f : n.getFragment()) {
					nodesInRigid1.add(f.getSource());
					nodesInRigid1.add(f.getTarget());
				}
			}
			double struct1 = ((double)nodesInRigid1.size())/((double)allNodes1);
			
			rpst2 = new RPST<>(dg2);
			int allNodes2 = net2.getTransitions().size();
			Set<Vertex> nodesInRigid2 = new HashSet<>(); 
			for (IRPSTNode<DirectedEdge, Vertex> n :rpst2.getRPSTNodes(TCType.RIGID)) {
				for (DirectedEdge f : n.getFragment()) {
					nodesInRigid2.add(f.getSource());
					nodesInRigid2.add(f.getTarget());
				}
			}
			double struct2 = ((double)nodesInRigid2.size())/((double)allNodes2);
			
			if (Math.max(struct1,struct2) == 0)
				score = 1.0;
			else 
				score = 1.0 - (
					(double)Math.abs(struct1 - struct2) 
							/ (double)Math.max(struct1,struct2));
			break;

		case StructurednessAbsolute:
			//(number of nodes in rigids) / all nodes.
			rpst1 = new RPST<>(dg1);
			allNodes1 = net1.getTransitions().size();
			nodesInRigid1 = new HashSet<>(); 
			for (IRPSTNode<DirectedEdge, Vertex> n :rpst1.getRPSTNodes(TCType.RIGID)) {
				for (DirectedEdge f : n.getFragment()) {
					nodesInRigid1.add(f.getSource());
					nodesInRigid1.add(f.getTarget());
				}
			}
			struct1 = ((double)nodesInRigid1.size())/((double)allNodes1);
			
			rpst2 = new RPST<>(dg2);
			allNodes2 = net2.getTransitions().size();
			nodesInRigid2 = new HashSet<>(); 
			for (IRPSTNode<DirectedEdge, Vertex> n :rpst2.getRPSTNodes(TCType.RIGID)) {
				for (DirectedEdge f : n.getFragment()) {
					nodesInRigid2.add(f.getSource());
					nodesInRigid2.add(f.getTarget());
				}
			}
			struct2 = ((double)nodesInRigid2.size())/((double)allNodes2);
			
			score = 1.0 - (struct1/2.0 + struct2/2.0);
			break;

			
		case NodesInCycle:
			int nodesInCycle1 = 0;
			TransitiveClosure<Flow, Node> tc1 = new TransitiveClosure<>(net1);
			for (Transition n : net1.getTransitions())
				if (tc1.isInLoop(n))
					nodesInCycle1++;
			
			int nodesInCycle2 = 0;
			TransitiveClosure<Flow, Node> tc2 = new TransitiveClosure<>(net2);
			for (Transition n : net2.getTransitions())
				if (tc2.isInLoop(n))
					nodesInCycle2++;

			if (Math.max(nodesInCycle1,nodesInCycle2) == 0)
				score = 1.0;
			else 
				score = 1.0 - (
					(double)Math.abs(nodesInCycle1 - nodesInCycle2) 
							/ (double)Math.max(nodesInCycle1,nodesInCycle2));
			break;

		case NumberSourceNodes:
			int sourceNodes1 = dga.getSources(net1).size();
			int sourceNodes2 = dga.getSources(net2).size();
			
			score = 1.0 - (
					(double)Math.abs(sourceNodes1 - sourceNodes2) 
							/ (double)Math.max(sourceNodes1,sourceNodes2));
			break;
		
		case NumberSinkNodes:
			int sinkNodes1 = dga.getSinks(net1).size();
			int sinkNodes2 = dga.getSinks(net2).size();
			
			score = 1.0 - (
					(double)Math.abs(sinkNodes1 - sinkNodes2) 
							/ (double)Math.max(sinkNodes1,sinkNodes2));
			break;

		case AvgNodeDegreeRelative:
			double degreeSum1 = 0;
			for (Node n : net1.getNodes())
				degreeSum1 += net1.getPreset(n).size() + net1.getPostset(n).size();
			double avgDegree1 = degreeSum1 / (double)  net1.getNodes().size();
			
			double degreeSum2 = 0;
			for (Node n : net2.getNodes())
				degreeSum2 += net2.getPreset(n).size() + net2.getPostset(n).size();
			double avgDegree2 = degreeSum2 / (double)  net2.getNodes().size();
			
			score = 1.0 - (
					(double)Math.abs(avgDegree1 - avgDegree2) / (double)Math.max(avgDegree1, avgDegree2));
			break;

		case AvgNodeDegreeAbsolute:
			degreeSum1 = 0;
			for (Node n : net1.getNodes())
				degreeSum1 += net1.getPreset(n).size() + net1.getPostset(n).size();
			avgDegree1 = degreeSum1 / (double)  net1.getNodes().size();
			
			degreeSum2 = 0;
			for (Node n : net2.getNodes())
				degreeSum2 += net2.getPreset(n).size() + net2.getPostset(n).size();
			avgDegree2 = degreeSum2 / (double)  net2.getNodes().size();

			score = (1.0 - (double)Math.max(0,1.0-0.4*Math.pow(avgDegree1-2,2)))/2.0 
					+ (1.0 - (double)Math.max(0,1.0-0.4*Math.pow(avgDegree2-2,2)))/2.0;
			break;

		case MaxNodeDegreeRelative:
			int maxDegree1 = 0;
			for (Node n : net1.getNodes())
				maxDegree1 = Math.max(maxDegree1, net1.getPreset(n).size() + net1.getPostset(n).size());
			
			int maxDegree2 = 0;
			for (Node n : net2.getNodes())
				maxDegree2 = Math.max(maxDegree2, net2.getPreset(n).size() + net2.getPostset(n).size());
			
			score = 1.0 - (
					(double)Math.abs(maxDegree1 - maxDegree2) / (double)Math.max(maxDegree1, maxDegree2));
			break;

		case MaxNodeDegreeAbsolute:
			maxDegree1 = 0;
			for (Node n : net1.getNodes())
				maxDegree1 = Math.max(maxDegree1, net1.getPreset(n).size() + net1.getPostset(n).size());
			
			maxDegree2 = 0;
			for (Node n : net2.getNodes())
				maxDegree2 = Math.max(maxDegree2, net2.getPreset(n).size() + net2.getPostset(n).size());
			
			score = (1.0 - (double)Math.max(0,1.0-0.04*Math.pow(maxDegree1-2,2)))/2.0 
					+ (1.0 - (double)Math.max(0,1.0-0.04*Math.pow(maxDegree2-2,2)))/2.0;
			break;
			
		/*
		 * Behavioural Properties
		 */
		case SizeExclusivenessRelationRelative:
			int sizeRelation1 = 0;
			BehaviouralProfile<NetSystem, Node> bp1 = BPCreatorUnfolding.getInstance().deriveRelationSet(net1, new HashSet<Node>(net1.getTransitions()));
			for (Node t : bp1.getEntities())
				sizeRelation1 += bp1.getEntitiesInRelation(t, RelSetType.Exclusive).size();
			
			int sizeRelation2 = 0;
			BehaviouralProfile<NetSystem, Node> bp2 = BPCreatorUnfolding.getInstance().deriveRelationSet(net2, new HashSet<Node>(net2.getTransitions()));
			for (Node t : bp2.getEntities())
				sizeRelation2 += bp2.getEntitiesInRelation(t, RelSetType.Exclusive).size();
			
			if (Math.max(sizeRelation1,sizeRelation2) == 0)
				score = 1.0;
			else 
				score = 1.0 - (
					(double)Math.abs(sizeRelation1 - sizeRelation2) / (double)Math.max(sizeRelation1, sizeRelation2));
			break;

		case SizeStrictOrderRelationRelative:
			sizeRelation1 = 0;
			bp1 = BPCreatorUnfolding.getInstance().deriveRelationSet(net1, new HashSet<Node>(net1.getTransitions()));
			for (Node t : bp1.getEntities())
				sizeRelation1 += bp1.getEntitiesInRelation(t, RelSetType.Order).size();
			
			sizeRelation2 = 0;
			bp2 = BPCreatorUnfolding.getInstance().deriveRelationSet(net2, new HashSet<Node>(net2.getTransitions()));
			for (Node t : bp2.getEntities())
				sizeRelation2 += bp2.getEntitiesInRelation(t, RelSetType.Order).size();
			
			if (Math.max(sizeRelation1,sizeRelation2) == 0)
				score = 1.0;
			else 
				score = 1.0 - (
					(double)Math.abs(sizeRelation1 - sizeRelation2) / (double)Math.max(sizeRelation1, sizeRelation2));
			break;

		case SizeConcurrencyRelationRelative:
			sizeRelation1 = 0;
			ConcurrencyRelation conc1 = new ConcurrencyRelation(net1);
			for (Transition t1 : net1.getTransitions())
				for (Transition t2 : net1.getTransitions())
					sizeRelation1 += (conc1.areConcurrent(t1, t2))?1:0;
			
			sizeRelation2 = 0;
			ConcurrencyRelation conc2 = new ConcurrencyRelation(net2);
			for (Transition t1 : net2.getTransitions())
				for (Transition t2 : net2.getTransitions())
					sizeRelation2 += (conc2.areConcurrent(t1, t2))?1:0;
			
			if (Math.max(sizeRelation1,sizeRelation2) == 0)
				score = 1.0;
			else 
				score = 1.0 - (
					(double)Math.abs(sizeRelation1 - sizeRelation2) / (double)Math.max(sizeRelation1, sizeRelation2));
			break;
			
		case SizeExclusivenessRelationAbsolute:
			sizeRelation1 = 0;
			bp1 = BPCreatorUnfolding.getInstance().deriveRelationSet(net1, new HashSet<Node>(net1.getTransitions()));
			for (Node t : bp1.getEntities())
				sizeRelation1 += bp1.getEntitiesInRelation(t, RelSetType.Exclusive).size();
			
			sizeRelation2 = 0;
			bp2 = BPCreatorUnfolding.getInstance().deriveRelationSet(net2, new HashSet<Node>(net2.getTransitions()));
			for (Node t : bp2.getEntities())
				sizeRelation2 += bp2.getEntitiesInRelation(t, RelSetType.Exclusive).size();
			
			score = ((double)sizeRelation1 / (double)(bp1.getEntities().size() * bp1.getEntities().size()))/2.0 
					+ ((double)sizeRelation2 / (double)(bp2.getEntities().size() * bp2.getEntities().size()))/2.0;
			break;

		case SizeStrictOrderRelationAbsolute:
			sizeRelation1 = 0;
			bp1 = BPCreatorUnfolding.getInstance().deriveRelationSet(net1, new HashSet<Node>(net1.getTransitions()));
			for (Node t : bp1.getEntities())
				sizeRelation1 += bp1.getEntitiesInRelation(t, RelSetType.Order).size();
			
			sizeRelation2 = 0;
			bp2 = BPCreatorUnfolding.getInstance().deriveRelationSet(net2, new HashSet<Node>(net2.getTransitions()));
			for (Node t : bp2.getEntities())
				sizeRelation2 += bp2.getEntitiesInRelation(t, RelSetType.Order).size();
			
			score = ((double)sizeRelation1 / (double)(bp1.getEntities().size() * bp1.getEntities().size()))/2.0 
					+ ((double)sizeRelation2 / (double)(bp2.getEntities().size() * bp2.getEntities().size()))/2.0;
			break;
			
		case SizeConcurrencyRelationAbsolute:
			sizeRelation1 = 0;
			conc1 = new ConcurrencyRelation(net1);
			for (Transition t1 : net1.getTransitions())
				for (Transition t2 : net1.getTransitions())
					sizeRelation1 += (conc1.areConcurrent(t1, t2))?1:0;
			
			sizeRelation2 = 0;
			conc2 = new ConcurrencyRelation(net2);
			for (Transition t1 : net2.getTransitions())
				for (Transition t2 : net2.getTransitions())
					sizeRelation2 += (conc2.areConcurrent(t1, t2))?1:0;
			
			score = ((double)sizeRelation1 / (double)(net1.getTransitions().size() * net1.getTransitions().size()))/2.0 
					+ ((double)sizeRelation2 / (double)(net2.getTransitions().size() * net2.getTransitions().size()))/2.0;
			break;

		default:
			break;
		}
		
		return score;
	}
	
	private int getMaxRPSTDepth(RPST<DirectedEdge, Vertex> rpst) {
		Map<IRPSTNode<DirectedEdge, Vertex>, Integer> depths = getRPSTDepth(rpst);
		int result = 0;
		for (Integer i : depths.values())
			result = Math.max(result,i);
		return result;
	}
	
	private Map<IRPSTNode<DirectedEdge, Vertex>, Integer> getRPSTDepth(RPST<DirectedEdge, Vertex> rpst) {
		Map<IRPSTNode<DirectedEdge, Vertex>, Integer> depths = new HashMap<>();
		Set<IRPSTNode<DirectedEdge, Vertex>> open = new HashSet<>();
		open.addAll(rpst.getChildren(rpst.getRoot()));		
		depths.put(rpst.getRoot(), 1);
		while (!open.isEmpty()) {
			IRPSTNode<DirectedEdge, Vertex> n = open.iterator().next();
			open.remove(n);
			open.addAll(rpst.getChildren(n));
			depths.put(n,depths.get(rpst.getParent(n))+1);
		}
		return depths;
	}

	private Map<Integer, Integer> getRPSTWidth(RPST<DirectedEdge, Vertex> rpst) {
		Map<IRPSTNode<DirectedEdge, Vertex>, Integer> depths = getRPSTDepth(rpst);
		Map<Integer, Integer> widths = new HashMap<>();
		for (IRPSTNode<DirectedEdge, Vertex> n : depths.keySet()) {
			if (!widths.containsKey(depths.get(n)))
				widths.put(depths.get(n), 1);
			else 
				widths.put(depths.get(n), widths.get(depths.get(n)) + 1);
		}
		return widths;
	}

	private int getMaxRPSTWidth(RPST<DirectedEdge, Vertex> rpst) {
		Map<Integer, Integer> widths = getRPSTWidth(rpst);
		int result = 0;
		for (Integer i : widths.values())
			result = Math.max(result,i);
		return result;
	}
	
}