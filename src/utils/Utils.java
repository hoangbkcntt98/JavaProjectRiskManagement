package utils;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.special.Erf;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import config.Configuaration;
import project.Task;
public class Utils {
	List<String> getListFromString(String str) {
			return Arrays.asList(str.split(","));
	}
	public static double gauss(double givenTime, double expectedTime,double standardDeviation) {
		double z = (givenTime - expectedTime)/standardDeviation;
		return (1+Erf.erf(z/Math.sqrt(2)))/2;
	}
	public static List<Task> readTaskInfo(String path){
		List<Task> tasks = new ArrayList<Task>();
		try {
			FileReader fileReader = new FileReader(path);
			CSVReader csvReader = new CSVReaderBuilder(fileReader) 
                    .withSkipLines(1) 
                    .build(); 
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) { 
	            tasks.add(Utils.getTaskInfoFromString(nextRecord));
	            System.out.println(); 
	        } 
		} catch (Exception e) {
			System.out.println(e);
		}
		return tasks;
	}
	public static List<String> getListPredecesorFromString(String str) {
		return (str.split(" ")!=null)?Arrays.asList(str.split(" ")):null;
	}
	public static Task getTaskInfoFromString(String[] str) {
			return new Task(Integer.parseInt(str[0]),str[1],Double.parseDouble(str[2]),Double.parseDouble(str[3]),Double.parseDouble(str[4]),Utils.getListPredecesorFromString(str[5]));
	}
	public static void main(String args[]) {
		Utils.readTaskInfo(Configuaration.inputPath+"0.csv");
	}

}
