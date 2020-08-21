package bayesian_network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.nd4j.shade.guava.collect.Sets;
import org.nd4j.shade.guava.primitives.Doubles;

import com.bayesserver.Link;
import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.State;
import com.bayesserver.Table;
import com.bayesserver.TableIterator;
import com.bayesserver.inference.InconsistentEvidenceException;
import com.bayesserver.inference.Inference;
import com.bayesserver.inference.InferenceFactory;
import com.bayesserver.inference.QueryOptions;
import com.bayesserver.inference.QueryOutput;
import com.bayesserver.inference.RelevanceTreeInferenceFactory;

import project.Risk;

public class BayesianNetwork {
	public Network bayesianNet;
	public List<Double> listProb;

	public BayesianNetwork(String nameNet) {
		this.bayesianNet = new Network(nameNet);
	}

	public void addNode(String name) {
		this.bayesianNet.getNodes().add(new Node(name, new State("True"), new State("False")));
	}
	public void addNodes(List<String> names) {
		for(String n:names) {
			addNode(n);
		}
	}
	public void addCptTable(Node node, List<Double> probList) {
		Table table = node.newDistribution().getTable();
		TableIterator iter = new TableIterator(table, bayesianNet.getNodes().toArray(new Node[0]));
		iter.copyFrom(Doubles.toArray(probList));
		node.setDistribution(table);
	}
	public void addCptTableParent(Node node,double prob) {
		Table table = node.newDistribution().getTable();
		TableIterator iter = new TableIterator(table,new Node[] {node});
		iter.copyFrom(new double[] {prob,1-prob});
		node.setDistribution(table);
	}
	public Node getNode(String name) {
		return bayesianNet.getNodes().get(name);
	}
	public State getNodeState(Node node,String state) {
		return node.getVariables().get(0).getStates().get(state);
	}
	public double excute(Risk risk) {
		List<String> nodes = new ArrayList<String>();
		List<Double> probList = risk.getProbabilityList();
		risk.getParentRisk().forEach(r->{
			nodes.add(r.getId());
		});
		nodes.add(risk.getId());
		addNodes(nodes);
		String child = risk.getId();
		nodes.stream().filter(node -> node != child).forEach(entry -> {
			bayesianNet.getLinks().add(new Link(getNode(entry), getNode(child)));
		});
		//set CPT Table for parent nodes
		for(Risk r: risk.getParentRisk()) {
			
			addCptTableParent(getNode(r.getId()),r.getProbability());
		}
		// set CPT table for child node
		addCptTable(getNode(child),probList);
		// calc probability
		List<Set<State>> itertoolsSet = new ArrayList<Set<State>>();
		for(Risk r:risk.getParentRisk()) {
			Set<State> set = new HashSet<>();
			set.add(getNodeState(getNode(r.getId()), "True"));
			set.add(getNodeState(getNode(r.getId()), "False"));
			itertoolsSet.add(set);
		}
		State [][] matTF = new State [(int)Math.pow(2, nodes.size()-1)][nodes.size()-1];
		Set<List<State>> result = Sets.cartesianProduct(itertoolsSet);
		Iterator<List<State>> iterator = result.iterator();
		System.out.println(risk.getId());
		int i=0,j=0;
		while(iterator.hasNext()) {
			List<State> temp = iterator.next();
			j=0;
			for(State str:temp) {
				
				
				matTF[i][j] = str;
				j++;
			}
			
			i++;
		}
		InferenceFactory factory = new RelevanceTreeInferenceFactory();
		Inference inference = factory.createInferenceEngine(bayesianNet);
		QueryOptions queryOptions = factory.createQueryOptions();
		QueryOutput queryOutput = factory.createQueryOutput();
		Table queryChild = new Table(getNode(child));
		inference.getQueryDistributions().add(queryChild);
		double probParent = 1;
		System.out.println("length [0]"+matTF.length);
		for(int a=0;a<matTF.length;a++)
		{
			for(int b=0;b<matTF[1].length;b++) {
				inference.getEvidence().setState(matTF[a][b]);
				if(matTF[a][b].getName()=="True") { 
					probParent*=risk.getParentRisk().get(b).getProbability();
				}else {
					probParent*=(1-risk.getParentRisk().get(b).getProbability());
				}
	
				
			}
			try {
				inference.query(queryOptions, queryOutput);
				return queryChild.get(getNodeState(getNode(child), "True"))*probParent;
			} catch (InconsistentEvidenceException e) {
				e.printStackTrace();
			}
			
		}
		return 0;
	}

}
