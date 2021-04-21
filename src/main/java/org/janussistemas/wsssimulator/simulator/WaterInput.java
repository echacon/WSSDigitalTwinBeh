package org.janussistemas.wsssimulator.simulator;

public class WaterInput {
	private static double[] turbidityPatern = {
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			5.1, 5.5, 5.1, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			5.1, 5.5, 1000.0, 5000.0, 30000.0, 16000.0, 12000.0, 1000.0, 15.0, 10.0, 7.0, 5.0,
			5.1, 5.5, 5.1, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,};
	private static double[] waterDemand = {
			7, 6.5,6,5,  5,4,4,4,  4,4,4,4, 4,4,4,4, 4.5,5,6,7,  8,10,12,14,
			15,15,16,16, 16,16,16.5,17.5, 17,17,17,15, 14,10,10,11, 10,10,10,11, 11, 11.5, 12,13,
			14,15,15,15, 14,13.5,13,12, 12,12,11,11, 10,10,9,8, 8,9,11,11, 12,14,15,15,
			15,15,15.5,15, 14,13.5,13,12, 11,11,10,10, 10,10,9,8, 8,9,9,9, 9,8.6,8.5,8.4
	};
	
	
	public double turbidity(int pos) {
		double res = 0.0;
		int index = pos/12;
		int resid = pos%12;
		int index1 = index%48;
		int index2 = (index+1)%48;
		
		res = (turbidityPatern[index1]*(12-resid) + turbidityPatern[index2]*resid)/12;
		return res;
	}
	public double waterDemand(int pos) {
		double res = 0.0;
		
		int index = pos/3;
		int resid = pos%3;
		int index1 = index%96;
		int index2 = (index+1)%96;
		res = (waterDemand[index1]*(3-resid) + waterDemand[index2]*resid)/3;
		return res;
	}
	public void taman() {
		System.out.println(" tam demanda" + waterDemand.length + " tam turb " + turbidityPatern.length);
	}
}
