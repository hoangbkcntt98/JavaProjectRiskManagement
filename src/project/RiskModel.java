package project;

import java.util.ArrayList;
import java.util.List;

import bayesian_network.BayesianNetwork;
import utils.Utils;

public class RiskModel {
	public List<Risk> risks;
	public RiskModel(List<Risk> risks) {
		this.risks = risks;
	}
	public void calcProb() {
		this.risks = getOrder();
		double [] listProb ;
		for(Risk r:risks) {
			
			BayesianNetwork bayesNet = new BayesianNetwork("bayesNet of risk " + r.getId());
			
			if(r.getParentRisk()!=null) {
				List<Risk> bayesRiskList = new ArrayList<Risk>();
				List<Double> bayesDis = r.getProbabilityList();
				bayesRiskList.addAll(r.getParentRisk());
				bayesRiskList.add(r);
				bayesNet.excute(r);
				
			}else {
				r.setProbability(r.getProbabilityList().get(0));
			}
//			
//			System.out.println(BayesListRisk);
//			System.out.println("\\");
		}
		
	}
	public List<Risk> getOrder(){
		List<Risk> riskListOrdered = new ArrayList<Risk>();
		
		int size = this.risks.size();
		int [] prob = new int [size];// The array which contain prob of all row in matOrder if prob[i] =1 then risk ith hasn't parent risk
		int [][] matOrder = Utils.matrix(size, size, 1);// The matrix which contain order of all risks 
		//update order matrix 
		for(int i=0;i<size;i++) {
			List<Risk> parent = risks.get(i).getParentRisk();
			if(parent!=null) {
				for(Risk risk : parent) {
					matOrder[i][Integer.parseInt(risk.getId())-1] = 0;
				}
			}
		}
		
		// get ordered list of risks
		while(riskListOrdered.size()!=size) {
			prob = Utils.getProb(matOrder);
			List<Risk> riskUpdates = Utils.riskNeedUpdateBefore(this.risks, prob);
			riskListOrdered.addAll(riskUpdates);
			Utils.updateMatrix(riskUpdates, matOrder,prob);
		}
		return riskListOrdered;
	}

}
