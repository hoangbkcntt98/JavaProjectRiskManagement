package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import config.Configuaration;

public class FakeDb {
	public static void main(String[] args) {
	    try (PrintWriter writer = new PrintWriter(new File(Configuaration.inputPath+"test1.csv"))) {

	      StringBuilder sb = new StringBuilder();
	      sb.append("id");
	      sb.append(',');
	      sb.append("parrent_risk");
	      sb.append('\n');
	      sb.append("1");
	      sb.append(',');
	      sb.append("Prashant Ghimire");
	      sb.append('\n');

	      writer.write(sb.toString());

	      System.out.println("done!");

	    } catch (FileNotFoundException e) {
	      System.out.println(e.getMessage());
	    }

	  }
}
