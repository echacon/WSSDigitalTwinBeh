package org.janussistemas.wsssimulator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.helpers.MarkerIgnoringBase;

public class Mixer implements Serializable, ProductionUnit{
	private static double[] state = new double[2];
	private static int[] markingVector = new int[4];
	private double[] output = new double[2];
	private double[] input = new double[2];

	@Override
	public double[] getInput() {
		return input;
	}
	@Override
	public void setInput(double[] input) {
		this.input = input;
	}
	@Override
	public double[] getState() {
		return state;
	}
	@Override
	public void setState(double[] state) {
		this.state = state;
	}
	@Override
	public double[] getOutput() {
		return output;
	}
	@Override
	public void setOutput(double[] output) {
		this.output = output;
	}
	@Override
	public int[] getMarkingVector() {
		return markingVector;
	}
	@Override
	public void setMarkingVector(int[] markingVector) {
		this.markingVector = markingVector;
	}
	
	
	private static int[] initIntMark = {1,0,0,0};
	private static int[] initExtMark = {1,0};
	int lugInt = 4;
	int transInt = 8;
	int stateDim = 2;
	int outputSize = 2;
	
	private static int[][] matOutInt = {{0,0,0,0,0,1,1,1},{1,0,0,0,1,0,0,0},
			{0,1,0,1,0,0,0,0},{0,0,1,0,0,0,0,0}};
	private static int[][] matIntInt = {{1,0,0,0,0,0,0,0,0},{0,1,0,0,0,1,0,0},
			{0,0,1,0,1,0,1,0},{0,0,0,1,0,0,0,1}};
	
	private static int[][] matOutExt = {{0,0,1,1},{1,1,0,0}};

	private static int[][] matIntExt = {{1,1,0,0},{0,0,1,1}};
	
	private static List<Transition> transitionList;
	private static String[] name = {"t1","t2","t3","t4","t5","t6","t7","t8"};
	private static String[] event = {"","gt5","gt15000","lt15000","lt5","","",""};
	private static String[] order = {"open gate","add flocculant", "close gate", "open gate" ,"stop flocculant", "close gate","close gate", "close gate"};
	
	@Override
	public void initialize() {
		state[0]=0;
		state[1]= 0;
		output[0]=0;
		output[1]= 0;
		for(int i=0;i<initIntMark.length; i++) {
			markingVector[i]=initIntMark[i];
		}
		transitionList = new ArrayList<Transition>();
		for(int i = 0; i< transInt; i++) {
			Transition t = new Transition();
			t.setName(name[i]);
			t.setIntEvent(event[i]);
			t.setCommand(order[i]);
			transitionList.add(t);
		}
	}
	@Override
	public Vector<Double> simul() {
		Vector<Double> res = new Vector<Double>();
		int activeState = 0;
		for(int i = 1; i<lugInt; i++) {
			if(markingVector[i] != 0) {
				activeState = i;
				break;
			}
		}
		switch (activeState) {
		case 0:
		case 3:
			for(int i =0; i<outputSize; i++) {
				output[0] = 0;
				output[1] = 0;
				res.add(0.0);
				res.add(0.0);
			}
			break;
		case 1:
		case 2:
			for(int i =0; i<outputSize; i++) {
				double val;
				output[0] = input[0];
				output[1] = input[1];
				res.add(input[0]);
				res.add(input[1]);
			}
		default:
			break;
		}
		return res;
	}
	@Override
	public Vector<Transition> disparables() {
		Vector<Transition> res = new Vector(8,5);
		for(int j = 0; j<transInt; j++) {
			boolean disp = true;
			for(int i = 0; i<lugInt; i++) {
				if(markingVector[i] < matIntInt[i][j]) {
					disp = false;
					break;
				}
			}
			if(disp) {
				res.add(transitionList.get(j));
			}
		}
		return res;
	}
	@Override
	public boolean evolucion(int trans) {
		float result =0;
		boolean disp = true;
		for(int i = 0; i<lugInt; i++) {
			if(markingVector[i] < matIntInt[i][trans]) {
				disp = false;
				break;
			}
		}
		if(disp) {
			for(int i = 0; i<lugInt; i++) {
				markingVector[i] = markingVector[i]- matIntInt[i][trans];
				markingVector[i] = markingVector[i] + matOutInt[i][trans];
			}
		}
		return disp;
	}
	@Override
	public String eventDetection() {
		String res = null;
		int activeState = 0;
		for(int i = 1; i<lugInt; i++) {
			if(markingVector[i] != 0) {
				activeState = i;
				break;
			}
		}
		switch (activeState) {
		case 0:
			break;
		case 1:
			if(input[1] > (float) 5.0)
				res = event[1];
			break;
		case 2:
			if(input[1] <= (float) 5.0 )
				res = event[4];
			if(input[1] > (float) 15000.00 )
				res = event[2];
			break;
		case 3:
			if(input[1] <= (float) 15000.00 )
				res = event[3];
			break;
		default:
			break;
		}
		return res;
	}
	@Override
	public String supervisor(String event) {
		String res = null;
		int activeState = 0;
		for(int i = 1; i<lugInt; i++) {
			if(markingVector[i] != 0) {
				activeState = i;
				break;
			}
		}
		switch (activeState) {
		case 0:
			System.out.println("estado 0 evento " + event);
			if(event.contentEquals("Start")) {
				res = order[0];
			}
			break;
		case 1:
			System.out.println("estado 1 evento " + event);
			if(event.contentEquals("gt5")) {
				res = order[1];
			}
			if(event.contentEquals("Stop")) {
				res = order[6];
			}
			break;
		case 2:
			System.out.println("estado 2 evento " + event );
			if(event.contentEquals("gt15000")) {
				res = order[2];
			}
			if(event.contentEquals("lt5")) {
				res = order[4];
			}
			if(event.contentEquals("Stop")) {
				res = order[6];
			}			
			break;
		case 3:
			System.out.println("estado 3 evento " + event);
			if(event.contentEquals("lt15000")) {
				res = order[3];
			}
			if(event.contentEquals("Stop")) {
				res = order[7];
			}					
			break;
		default:
			break;
		}
		return res;
	}
	@Override
	public boolean executeOrder(String commandint) {
		// buscar dentro de las transiciones disparables si una tiene ese evento y dispararla
		// ejecutar la actualización del estado continuo
		boolean res = false;
		Vector<Transition> fireable = disparables();
		
		for(Transition t : fireable) {
			if(t.getCommand().contentEquals(commandint) ) {
				res = true;
				fire(t);
			}
		}
		
		return res;
	}
	private void fire(Transition t) {
		String name = t.getName();
		switch(name) {
		case "t1":
			evolucion(0);
			// reset
			state[0]=0;
			state[1] = input[1];
			break;
		case "t2":
			evolucion(1);
			state[0] = state[0];
			state[1] = input[1];
			break;
		case "t3":
			evolucion(2);
			state[0] = state[0];
			state[1] = input[1];
			break;
		case "t4":
			evolucion(3);
			// reset
			state[0]=0;
			state[1] = input[1];
			break;
		case "t5":
			evolucion(4);
			state[0] = state[0];
			state[1] = input[1];
			break;
		case "t6":
			evolucion(5);
			state[0] = state[0];
			state[1] = input[1];
			break;
		case "t7":
			evolucion(6);
			// reset
			state[0]=0;
			state[1] = input[1];
			break;
		case "t8":
			evolucion(7);
			state[0] = state[0];
			state[1] = input[1];
			break;
		}	
	}
	@Override
	public void imprimirVector() {
		System.out.println("Mixer");
		for(int i =0; i< lugInt; i++) {
			System.out.print("Lugar " + i + "Marcas " + markingVector[i] + " - ");
		}
		System.out.println();
	}
	@Override
	public int activeState() {
		int activeState = 0;
		for(int i = 1; i<lugInt; i++) {
			if(markingVector[i] != 0) {
				activeState = i;
				break;
			}
		}
		return activeState;
	}
}
