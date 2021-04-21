package org.janussistemas.wsssimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.janussistemas.wsssimulator.model.Flocculator;
import org.janussistemas.wsssimulator.model.Mixer;
import org.janussistemas.wsssimulator.model.ProductionUnit;
import org.janussistemas.wsssimulator.model.Transition;

public class ProbadorFloc {

	public static void main(String[] args) {
		String extEvent = null;
		List<Transition> fireableTrans;
		List<ProductionUnit> system = new ArrayList<ProductionUnit>();
		ProductionUnit p = new Flocculator();
		p.initialize();
		double[] input = new double[2];
		double[] output = new double[2];
		double[] estado = new double[2];
		String commandint;
		List<String> salida = new ArrayList<String>();
		String linea = "iter,flow,turb,output flow,turb, event \n";
		salida.add(linea);
		String prevent;
		for(int i=0; i<570; i ++) {
			linea = "";
			input[0] = Math.random()*10 +150;
			input[1] = 4;
			p.setInput(input);
			p.simul();
			output = p.getOutput();
			estado = p.getState();
			System.out.print("iter " + i ); 
			System.out.print("estado activo " + p.activeState() + " salida " + output[0]+ "  " + output[1] + " estado " + estado[0]+ "  " + estado[1] + " --");

			if(i == 3)
				extEvent = "Start";
			if(i == 550)
				extEvent = "Stop";
			if(i == 560)
				extEvent = "Clean";
			if(extEvent == null) 
				prevent ="";
			else
				prevent = extEvent;
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
			linea = Integer.toString(i)+","+Double.toString(input[0])+","+Double.toString(input[1])+","+
				Double.toString(output[0])+","+Double.toString(output[1])+","+prevent + "\n";
			salida.add(linea);
		}
		File archivo = new File("salidaFloc.csv");
		try {
			FileWriter escritor = new FileWriter(archivo,true);
			for(String lin:salida) {
				escritor.write(lin);
			}
			escritor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
