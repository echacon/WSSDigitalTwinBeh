package org.janussistemas.wsssimulator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Filter implements Serializable, ProductionUnit{
	private static double[] state = new double[4];
	/*
	 * tank level
	 * resistence
	 * colmat level
	 * elapsed time
	 * 
	 */
	double volmax = 10000.0;
	double colmat = 40.0;
	double cleanPeriod = 100;
	double cleantime = 10;
	double prepartime = 5;
	double discharge = 3;
	double deltatime = 1;
	
	private static int[] markingVector = new int[6];
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
	
	private static int[] initIntMark = {1,0,0,0,0,0};
	private static int[] initExtMark = {1,0};
	int lugInt = 6;
	int transInt = 7;
	int stateDim = 2;
	int outputSize = 2;
	
	private static int[][] matOutInt = {{0,0,0,0,0,0,1},{1,0,0,0,1,0,0},
			{0,1,0,0,0,0,0},{0,0,1,0,0,0,0},
			{0,0,0,1,0,0,0}, {0,0,0,0,0,1,0}};
	private static int[][] matIntInt = {{1,0,0,0,0,0,0,0},{0,1,0,0,0,0,0},
			{0,0,1,0,0,1,0},{0,0,0,1,0,0,0,0},
			{0,0,0,0,1,0,0},{0,0,0,0,0,0,1}};
	
	private static int[][] matOutExt = {{0,0,1,1},{1,1,0,0}};

	private static int[][] matIntExt = {{1,1,0,0},{0,0,1,1}};
	
	private static List<Transition> transitionList;
	private static String[] name = {"t1","t2","t3","t4","t5","t6","t7"};
	private static String[] event = {"","time","colmat","time","time","","time"};
	private static String[] order = {"open gate","operate", "discharge", "clean" ,"open gate", "close gate","ready"};


	@Override
	public void initialize() {
		state[0] = 0; // water level
		state[1] = 0; // mean turb
		state[2] = 0; // colmat
		state[3] = 0; // elapsed time
		
		output[0] = 0;
		output[1] = 0;
		transitionList = new ArrayList<Transition>();
		for(int i = 0; i< transInt; i++) {
			Transition t = new Transition();
			t.setName(name[i]);
			t.setIntEvent(event[i]);
			t.setCommand(order[i]);
			transitionList.add(t);
		}
		for(int i=0;i<initIntMark.length; i++) {
			markingVector[i]=initIntMark[i];
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
			res.add(0.0);
			res.add(0.0);
			output[0] = 0;
			output[1] = 0;
			break;
		case 1:
			// agua que entra sale durante un tiempo como periodo de asentamiento
			res.add(0.0);
			res.add(0.0);
			output[0] = 0;
			output[1] = 0;
			state[3] = state[3]+1;
			break;
		case 2:
			res.add(input[0]);
			res.add(input[1]);
			// incremento de resistencia del agua
			state[1]=state[1] + input[1]/1000;
			// incremento del volumen en el filtro
			state[0] = state[0] + state[1]*input[0];
			state[3] = state[3] + deltatime;
			output[0] = input[0] - state[1]*input[0];
			if(output[0]<0) 
				output[0] = 0;
			output[1] = 0;
			break;
		case 3:
			output[0] = 0.0;
			output[1] = 0.0;
			state[3] = state[3] +1;
			break;
		case 4:
			output[0] = 0.0;
			output[1] = 0.0;
			state[3] = state[3] +1;
			break;
		case 5:
			output[0] = 0.0;
			output[1] = 0.0;
			state[3] = state[3] +1;
			break;

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
				System.out.println(" marca " + markingVector[i] + " - " + matIntInt[i][trans] + " + "+ matOutInt[i][trans]);
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
			if(state[3] > prepartime)
				res = event[1];
			break;
		case 2:
			if((state[1] > colmat) || (state[3] > cleanPeriod) ) 
				res = event[2];
			break;
		case 3:
			if(state[3] > discharge)
				res = event[3];
			break;
		case 4: 
			if(state[3] > cleantime)
				res = event[4];
		default:
		case 5: 
			if(state[3] > discharge)
				res = event[6];
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
				res = "t1";
			}
			break;
		case 1:
			System.out.println("estado 1 evento " + event);
			if(event.contentEquals("time")) {
				res = "t2";
			}
			break;
		case 2:
			System.out.println("estado 2 evento " + event );
			if(event.contentEquals("colmat")) {
				res = "t3";
				break;
			};	
			if(event.contentEquals("Stop")) {
				res = "t6";
			};
			break;			
		case 3:
			System.out.println("estado 3 evento " + event);
			if(event.contentEquals("time")) {
				res = "t4";
				System.out.println("res = " + res);
			}
			break;
		case 4:
			System.out.println("estado 4 evento " + event);
			if(event.contentEquals("time")) {
				res = "t5";
			}
			break;
		default:
		case 5:
			System.out.println("estado 4 evento " + event);
			if(event.contentEquals("time")) {
				res = "t7";
			}
			
			break;
		}
		return res;
	}

	@Override
	public boolean executeOrder(String commandint) {
		boolean res = false;
		Vector<Transition> fireable = disparables();
		
		System.out.println("disparables " + fireable.size() + " comandint " + commandint);
		
		for(Transition t : fireable) {
			System.out.println("t. command " + t.getName() + " comandint " + commandint);
			if(t.getName().contentEquals(commandint) ) {
				System.out.println("Disparar " + t.getName());
				res = true;
				fire(t, input);
				break;
			}
		}
		
		return res;
	}
	private void fire(Transition t, double[] input) {
		String name = t.getName();
		switch(name) {
		case "t1":
			evolucion(0);
			// reset
			state[0]=0;
			state[1] = input[1];
			state[2] = 0;
			state[3] = 0;
			
			break;
		case "t2":
			evolucion(1);
			state[0] = 0;
			state[1] = 0;
			state[2] = 0;
			state[3] = 0;
			break;
		case "t3":
			evolucion(2);
			state[0] = 0;
			state[1] = 0;
			state[2] = 0;
			state[3] = 0;
			break;
		case "t4":
			evolucion(3);
			// reset
			state[0]=0;
			state[1] = 0;
			state[2] = 0;
			state[3] = 0;
			break;
		case "t5":
			evolucion(4);
			// reset
			state[0]=  0;
			state[1] = 0;
			state[2] = 0;
			state[3] = 0;
			break;
		case "t6":
			evolucion(5);
			// reset
			state[0]=0;
			state[1] = 0;
			state[2] = 0;
			state[3] = 0;
			break;
		case "t7":
			evolucion(6);
			// reset
			state[0]=0;
			state[1] = 0;
			state[2] = 0;
			state[3] = 0;
			break;
		}	
	}

	@Override
	public void imprimirVector() {
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
