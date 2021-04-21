package org.janussistemas.wsssimulator.model;

import java.util.Vector;

public interface ProductionUnit {
	public void initialize();
	public Vector<Double> simul();
	public Vector<Transition> disparables();
	public boolean evolucion(int trans);
	public String eventDetection();
	public String supervisor(String event);
	public boolean executeOrder(String commandint);
	public void imprimirVector();
	public int activeState();
	// Entradas salidas
	public double[] getInput();
	public void setInput(double[] input);
	public double[] getState(); 
	public void setState(double[] state);
	public double[] getOutput();
	public void setOutput(double[] output);
	public int[] getMarkingVector();
	public void setMarkingVector(int[] markingVector);

	
}
