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
import org.janussistemas.wsssimulator.simulator.WaterInput;

public class probador {

	public static void main(String[] args) {
		
		/*
		 * Lazo de control
		 */
		
		/*
		 * medir y regular
		 * determinar eventos
		 * 		eventos internos
		 * 		eventos externos
		 * si eventos
		 *    efectuar control supervisor
		 *    si tarea completada o fuera de alcance
		 *    		enviar evento a coordinacion
		 * fin del lazo
		 */
		String[] extEvent = new String[12];
	
		List<ProductionUnit> system = new ArrayList<ProductionUnit>();
		ProductionUnit mix1 = new Mixer();
		system.add(mix1);
		WaterInput wi = new WaterInput();
		ProductionUnit floc1 = new Flocculator();
		system.add(floc1);
		
		int pos;
		pos = 0;
		for(ProductionUnit p : system){
			p.initialize();
		}
		
		double[] mix1in = new double[2];
		double[] floc1in = new double[2];
		String commandint;
		Vector<Transition> fireableTrans;
		List<String> salida = new ArrayList<String>();
		salida.add("iter,flow in,turb,flow out, turb out, act state \n");
		
		for(int i = 0; i<576; i++) {			
			
			String linea;
			/*
			 * Colocar entradas
			 */
			// Mixer
			double turb = wi.turbidity(i);
			
			mix1in[0] = 100*3 + Math.random()*5;
			mix1in[1] = turb;
			system.get(0).setInput(mix1in);
			
			// flocculator
			double[] outMix1 = mix1.getOutput();
			floc1in[0] = outMix1[0];
			floc1in[1] = outMix1[1];
			floc1.setInput(floc1in);
			

			wi.waterDemand(i);
			if(i == 3) {
				extEvent[0] = "Start";
			}
			if(i == 10) {
				extEvent[0] = "Stop";
			}
			if(i == 15) {
				extEvent[0] = "Start";
			}
			if(i == 20) {
				extEvent[1] = "Start";
			}
			int cont = 0;
			linea = Integer.toString(i) + "," +Double.toString(mix1in[0]) +"," + Double.toString(mix1in[1]) ;
			for(ProductionUnit p : system) {
				Vector<Double> mixOut= p.simul();
				for(Double db : p.getOutput())
					linea = linea +"," + Double.toString(db);
				String event = p.eventDetection();
				fireableTrans = p.disparables();
				System.out.print("iter " + i+ "cont " + cont + " fireable size " + fireableTrans.size() );
				if(event != null) {
					 commandint = p.supervisor(event);
					 p.executeOrder(commandint);
				}
				if(extEvent[cont] != null) {
					commandint = p.supervisor(extEvent[cont]);
					if(commandint != null) {
						p.executeOrder(commandint);
					}			 
					 extEvent[cont] = null;
				}
				linea = linea+"," + Integer.toString(p.activeState()) + ","; 
				System.out.println();
				cont++;
			}
			linea = linea + "\n";
			salida.add(linea);
		}
		File archivo = new File("salida.csv");
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
