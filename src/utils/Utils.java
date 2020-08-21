package utils;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.math3.special.Erf;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import config.Configuaration;
import project.Risk;
import project.RiskModel;
import project.Task;
public class Utils {
	List<String> getListFromString(String str) {
			return Arrays.asList(str.split(","));
	}
	public static double gauss(double givenTime, double expectedTime,double standardDeviation) {
		double z = (givenTime - expectedTime)/standardDeviation;
		return (1+Erf.erf(z/Math.sqrt(2)))/2;
	}
	public static List<Task> readTaskListInfo(String path){
		List<Task> tasks = new ArrayList<Task>();
		Map<String,List<String>> predecessorMap = new HashMap<String,List<String>>();
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader) 
                    .withSkipLines(1) 
                    .build(); 
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) { 
	            tasks.add(Utils.getTaskInfoFromString(nextRecord));
	            String predecessor = nextRecord[nextRecord.length-1];
	            predecessorMap.put(nextRecord[1], convertStringToList(predecessor)); 
	        } 
			//update predecessorList
			for(Task task: tasks) {
				List<String> predecessorList = predecessorMap.get(task.getName());
				if(predecessorList!=null) task.setPredecessor(convertToPredecessorList(predecessorList, tasks));
			}
			//update successorList
			for(Task task:tasks) {
				List<Task> successor= new ArrayList<Task>();
				for(Task task1:tasks) {
					if(task1.getPredecessor()!=null) {
						if(task1.getPredecessor().contains(task)) {
							successor.add(task1);
						}
					}
				}
			
				if(successor.size()!=0)
				task.setSuccessor(successor);
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		return tasks;
	}
	public static List<Task> convertToPredecessorList(List<String> strListPredecessor,List<Task> tasks) {
		return tasks.stream().filter(task-> strListPredecessor.contains(task.getName())).collect(Collectors.toList());
	}
	public static List<String> convertStringToList(String str) {
		return (str.split(" ").length!=0)?Arrays.asList(str.split(" ")):null;
	}
	public static Task getTaskInfoFromString(String[] str) {
			return new Task(Integer.parseInt(str[0]),str[1],Double.parseDouble(str[2]),Double.parseDouble(str[3]),Double.parseDouble(str[4]));
	}
	public static Task findTaskByName(String name,List<Task> taskList) {
		for(Task t : taskList) {
			if(t.getName()==name) {
				return t;
			}
		}
		return null;
	}
	
	public static List<Task> updateTasks(List<Task> givenTasks){
		List<Task>tasks = givenTasks;
		// set early start for first task = 0
		tasks.get(0).setEs(0);
		//set early start for all task
		for(Task t: tasks) {
			if(t.getPredecessor()==null) {
				t.setEs(0);
			}else {
				double taskEs = 0;
				for(Task t1: t.getPredecessor()) {
					if(taskEs<t1.getMostlikely()+t1.getEs()) {
						taskEs = t1.getMostlikely()+t1.getEs();
					}
				}
				t.setEs(taskEs);
				
			}
			t.setEf(t.getEs()+t.getMostlikely());
		}
		Task finalTask = tasks.get(tasks.size()-1);
		finalTask.setLf(finalTask.getEs()+ finalTask.getMostlikely());		
		// set late finish for all task 
		for(Task t: tasks) {
			if(t.getSuccessor()==null) {
				t.setLf(t.getEs()+t.getMostlikely());
			}else {
				System.out.println(t.getName());
				double taskLf = Configuaration.INFINITIVE;
				for(Task suc : t.getSuccessor()) {
					System.out.println("->"+suc.getName());
					if(suc.getLf()- suc.getMostlikely()<taskLf) {
						taskLf = suc.getLf()-suc.getMostlikely();
					}
					System.out.println(" "+taskLf);
				}
				t.setLf(taskLf);
				
			}
			t.setLs(t.getLf()-t.getMostlikely());
			t.setSlack(t.getLs()-t.getEs());
			
		}		
		return tasks;
	}
	public static List<Risk> readRiskRelationInfo(String path) {
		List<Risk> allRisks = new ArrayList<Risk>();
		Map<String,List<String>> parentRiskMap = new HashMap<String,List<String>>();
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader) 
                    .withSkipLines(1) 
                    .build(); 
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) { 
	            parentRiskMap.put(nextRecord[0], convertStringToList(nextRecord[1]));
	        } 
			for(Entry<String, List<String>> entry: parentRiskMap.entrySet()) {
				allRisks.add(new Risk(entry.getKey()));
			}
			Collections.sort(allRisks,(r1,r2)->{
				return Integer.parseInt(r1.getId())-Integer.parseInt(r2.getId());
			});
			for(Risk risk:allRisks) {
				List<String>strParent = parentRiskMap.get(risk.getId());
				if(Integer.parseInt(strParent.get(0))!=0) {
					risk.setParentRisk(convertToParentRiskList(strParent, allRisks));
				}
			}
			
			return allRisks;
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
	}
	public static List<Risk> convertToParentRiskList(List<String> strParentList,List<Risk> risks){
		return risks.stream().filter(risk -> strParentList.contains(risk.getId())).collect(Collectors.toList());
	}
	public static void readRiskDistribution(String path,List<Risk> risks){
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .build(); 
			String [] nextRecord;
			Map<String,List<Double>> probMap = new HashMap<String,List<Double>>();
			while ((nextRecord = csvReader.readNext()) != null) { 
				ArrayList<Double> childList = new ArrayList<Double>();
	            for(int i=1;i<nextRecord.length;i++) {
	            	childList.add(Double.parseDouble(nextRecord[i]));
	            	
	            }
	            probMap.put(nextRecord[0], childList);
	            } 
			for(Risk risk:risks) {
				risk.setProbabilityList(probMap.get(risk.getId()));
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	public static int [][] matrix(int row,int column,int value){
		int [][] mat = new int[row][column];
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<column;j++)
			{
				mat[i][j] = value;
			}
		}
		return mat;
	}
	public static int[] getProb(int [][] mat) {
		int [] prob = new int [mat[0].length];
		Arrays.fill(prob, 1);
		for(int i=0;i<mat[0].length;i++) {
			int probArray =1;
			for(int j=0;j<mat[1].length;j++) {
				probArray *= mat[i][j];
			}
			prob[i] = probArray;
		}
		return prob;
	}
	public static List<Risk> riskNeedUpdateBefore(List<Risk> risks,int [] prob){
		
		return risks.stream().filter(r-> prob[Integer.parseInt(r.getId())-1]==1).collect(Collectors.toList());
	}
	public static void updateMatrix(List<Risk> nodeUpdates,int [][] matrix,int[] prob) {
		nodeUpdates.stream().filter(r->prob[Integer.parseInt(r.getId())-1]==1).forEach(entry->{
			setValueCol(matrix,Integer.parseInt(entry.getId())-1, 1);
			setValueRow(matrix,Integer.parseInt(entry.getId())-1, 0);
		});
	}
	public static void setValueRow(int [][] mat,int row,int value) {
		for(int i=0;i<mat[0].length;i++) {
			if(i==row) {
				for(int j=0;j<mat[1].length;j++) {
					mat[i][j] = value;
				}
			}
			
		}
	}
	public static void setValueCol(int [][] mat,int col,int value) {
		for(int i=0;i<mat[1].length;i++) {
			if(i==col) {
				for(int j=0;j<mat[0].length;j++) {
					mat[j][i] = value;
				}
			}
			
		}
	}
	public static void main(String args[]) {
//		List<Task> tasks = Utils.readTaskListInfo(Configuaration.inputPath+"0.csv");
//		List<Task> criticlePath = Pert.excute(tasks);
//		Pert.showCriticalPath(criticlePath);
		
		List<Risk> allRisks = Utils.readRiskRelationInfo(Configuaration.inputPath+"risk_relation.csv");
//		FakeDb.generateRiskDistribution(allRisks);
		Utils.readRiskDistribution(Configuaration.inputPath+"risk_distribution.csv", allRisks);
		RiskModel riskModel = new RiskModel(allRisks);
		riskModel.calcProb();
		
		System.out.println();
	}

}
