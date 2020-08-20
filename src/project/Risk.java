package project;

import java.util.List;
import com.bayesserver.Node;

public class Risk {
	public List<Risk> parentRisk;
	public String Id;
	public List<Double> probability;
	public List<Node> nodes;
	public Risk(String Id,List<Risk> parentRisk,List<Node> nodes) {
		this.Id = Id;
		this.parentRisk = parentRisk;
		this.nodes = nodes;
	}
	public Risk(String Id,List<Risk> parentRisk) {
		this.Id = Id;
		this.parentRisk = parentRisk;
	}
	public Risk(String Id) {
		this.Id = Id;
	}
	public double caculateProbability()
	{
		
		return -1;
	}
	public List<Risk> getParentRisk() {
		return parentRisk;
	}
	public void setParentRisk(List<Risk> parentRisk) {
		this.parentRisk = parentRisk;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public List<Double> getProbability() {
		return probability;
	}
	public void setProbability(List<Double> probability) {
		this.probability = probability;
	}
	

}
