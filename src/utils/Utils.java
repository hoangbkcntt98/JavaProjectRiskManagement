package utils;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.math3.special.Erf;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import config.Configuaration;
import pert.Pert;
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
	public static void main(String args[]) {
		List<Task> tasks = Utils.readTaskListInfo(Configuaration.inputPath+"0.csv");
		Pert.prepareExcute(tasks);
		List<Task> criticlePath = Pert.excute(tasks);
		Pert.showCriticalPath(criticlePath);
	}

}
