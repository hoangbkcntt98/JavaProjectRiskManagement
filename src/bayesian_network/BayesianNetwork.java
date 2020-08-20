package bayesian_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bayesserver.Network;
import com.bayesserver.Node;
import com.bayesserver.State;
import com.bayesserver.Table;
import com.bayesserver.TableIterator;

public class BayesianNetwork {
	public Map<String,Integer> nodeStatusMap;
	public List<Node> nodes;
	public Network bayesianNet;
	public List<Double> listProb;
	public BayesianNetwork(String nameNet) {
		this.bayesianNet = new Network(nameNet);
		this.nodes = new ArrayList<Node>();
		this.nodeStatusMap  = new HashMap<String,Integer>();
	}
	public void createNode(String name,int isParrent) {
		this.bayesianNet.getNodes().add(new Node(name,new State("True"),new State("False")));
		this.nodeStatusMap.put(name,isParrent);
	}
	public void createCptTable(Node node,List<Double> listProb) {
		Table table = node.getDistribution().getTable();
		TableIterator iter = new TableIterator(table,this.nodes.toArray(new Node[0]));
		iter.copyFrom(listProb.stream().mapToDouble(Double::doubleValue).toArray());
		node.setDistribution(table);
	}
	public void excute() {
		
	}
	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
	
	

}
