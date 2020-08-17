package pert;

import java.util.List;

import project.Task;

public class Pert {
	public static double estimateDuration(double opt,double mos,double pes) {
		return (opt+4*mos+pes)/6;
	}
	public static double standardDeviation(double opt,double mos,double pes) {
		return (pes-mos)/6;
	}
	public static double variance(double opt,double mos,double pes) {
		return Math.pow((pes-opt), 2)/6;
	}
	public static List<Task> criticalPath(List<Task> tasks) {
		
		return null;
	}
}
