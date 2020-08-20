package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import config.Configuaration;
import project.Risk;

public class FakeDb {
	public static void generateRiskDistribution(List<Risk> relationRisk) {
		try {
			PrintWriter writer = new PrintWriter(new File(Configuaration.inputPath+"risk_distribution.csv"));
			StringBuilder sb = new StringBuilder();
			
			for(Risk risk:relationRisk) {
				sb.append(risk.getId()+",");
				if(risk.getParentRisk()==null) {
					generateDistribute(sb);
				}else {
					int size = (int) Math.pow(2,risk.getParentRisk().size());
					for(int i=0;i<size;i++) {
						generateDistribute(sb);
						if(i!=size-1)
						sb.append(",");
					}
				}
				sb.append("\n");
			}
			System.out.println(sb.toString());
			writer.write(sb.toString());
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
			// TODO: handle exception
		}
	}
	public static void generateDistribute(StringBuilder sb) {
		double dis =(double) Math.round(Math.random()*100)/100;
		double neg = (double) Math.round((1-dis)*100)/100;
		sb.append(dis+","+neg);
	}
	
	
}
