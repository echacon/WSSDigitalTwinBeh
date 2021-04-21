package org.janussistemas.wsssimulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.janussistemas.wsssimulator.model.ProductionUnit;
import org.janussistemas.wsssimulator.model.Settler;
import org.janussistemas.wsssimulator.model.Transition;
import org.janussistemas.wsssimulator.simulator.WaterInput;

public class ProbadorSettler {

	public static void main(String[] args) {
		String extEvent = null;
		WaterInput wi = new WaterInput();
		List<Transition> fireableTrans;
		List<ProductionUnit> system = new ArrayList<ProductionUnit>();
		ProductionUnit p = new Settler();
		p.initialize();
		double[] input = new double[2];
		double[] output = new double[2];
		double[] estado = new double[2];
		String commandint;
		List<String> salida = new ArrayList<String>();
		salida.add("iter,flow in,turb,flow out,turb out,water level,sludge level,act state \n");
		try {
	      File myObj = new File("salida.csv");
	      Scanner myReader = new Scanner(myObj);
	      int i = 0;
	      myReader.nextLine();
	      while (myReader.hasNextLine()) {
	        String data = myReader.nextLine();
	        String[] arrSplit = data.split(",");
	        input[0] = Double.parseDouble(arrSplit[3])/4;
	        input[1] = Double.parseDouble(arrSplit[4]);
	        p.setInput(input);
			p.simul();
			output = p.getOutput();
			estado = p.getState();
			System.out.print("iter " + i ); 
			System.out.print("estado activo " + p.activeState() + " salida " + output[0]+ "  " + output[1] + 
					" estado " + estado[0]+ "  " + estado[1] + "  " + estado[2]+ "  " + estado[3] + " --");
	
			
			if(i == 3)
				extEvent = "Start";
			if(i == 500)
				extEvent = "Stop";
			if(i== 520)
				extEvent = "Clean";
			String linea = Integer.toString(i) + ","+Double.toString(input[0])+","+Double.toString(input[1])+","+
					Double.toString(output[0])+","+Double.toString(output[0])+","+Double.toString(estado[0])+","+
					Double.toString(estado[2])+","+p.activeState()+"\n";
			salida.add(linea);
			i = i+1;
	
			String event = p.eventDetection();
			System.out.print(" event "+ event);
			fireableTrans = p.disparables();
			System.out.print(" fireable size " + fireableTrans.size() );
			for(Transition t : fireableTrans) {
				System.out.print(" t " + t.getName() );
			}
			if(event != null) {
				 commandint = p.supervisor(event);
				 System.out.print(" commandint " + commandint);
				 p.executeOrder(commandint);
			}
			if(extEvent != null) {
				commandint = p.supervisor(extEvent);
				if(commandint != null) {
					p.executeOrder(commandint);
				}			 
				 extEvent = null;
			}	
			System.out.println();
			
	      }
	    myReader.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
		File archivo = new File("salidaSettler.csv");
		try {
			FileWriter escritor = new FileWriter(archivo,true);
			for(String linea:salida) {
				escritor.write(linea);
			}
			escritor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
