package project;

import java.util.List;

import pert.Pert;

public class Task {
	int id;
	String name;
	double expectedTime;
	double optimistic;
	double mostlikely;
	double pessimistic;
	double variance;
	double standardDeviation;
	double es;
	double ls;
	double ef;
	double lf;
	double slack=-1;
	List<String> predecessor;
	List<String> successor;
	public Task(int id, String name,double optimistic, double mostlikely,
			double pessimistic,List<String> predecessor) {
		super();
		this.id = id;
		this.name = name;
		this.optimistic = optimistic;
		this.mostlikely = mostlikely;
		this.pessimistic = pessimistic;
		this.predecessor = predecessor;
		this.expectedTime = Pert.estimateDuration(this.optimistic, this.mostlikely, this.pessimistic);
		this.variance = Pert.variance(this.optimistic, this.mostlikely, this.pessimistic);
		this.standardDeviation = Pert.standardDeviation(this.optimistic, this.mostlikely, this.pessimistic);
	}
	public void update() {
		this.ef = this.es + this.mostlikely;
		this.ls = this.lf - this.mostlikely;
		this.slack = this.ls - this.es;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getOptimistic() {
		return optimistic;
	}
	public void setOptimistic(double optimistic) {
		this.optimistic = optimistic;
	}
	public double getMostlikely() {
		return mostlikely;
	}
	public void setMostlikely(double mostlikely) {
		this.mostlikely = mostlikely;
	}
	public double getPessimistic() {
		return pessimistic;
	}
	public void setPessimistic(double pessimistic) {
		this.pessimistic = pessimistic;
	}
	public List<String> getPredecessor() {
		return predecessor;
	}
	public void setPredecessor(List<String> predecessor) {
		this.predecessor = predecessor;
	}
	public List<String> getSuccessor() {
		return successor;
	}
	public void setSuccessor(List<String> successor) {
		this.successor = successor;
	}
	public double getVariance() {
		return variance;
	}
	public double getStandardDeviation() {
		return standardDeviation;
	}
}
