package org.janussistemas.wsssimulator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.janussistemas.wsssimulator.model.DisinfectionTank;
import org.janussistemas.wsssimulator.model.Filter;
import org.janussistemas.wsssimulator.model.Flocculator;
import org.janussistemas.wsssimulator.model.Mixer;
import org.janussistemas.wsssimulator.model.ProductionUnit;
import org.janussistemas.wsssimulator.model.Settler;
import org.janussistemas.wsssimulator.model.Transition;
import org.janussistemas.wsssimulator.simulator.WaterInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WsssimulatorApplication {

	
	static ApplicationContext ctx;
	
	public static void main(String[] args) {
		ApplicationContext ctx=SpringApplication.run(WsssimulatorApplication.class, args);
		String[] beans = ctx.getBeanDefinitionNames();
        Arrays.sort(beans);
        for (String bean : beans) {
      	if(bean.contains("Repository") || bean.contains("Service")) {
      		System.out.println(bean);
     		}
      	}

		WaterInput wi = new WaterInput();
		List<Transition> fireableTrans;
		List<ProductionUnit> system = new ArrayList<ProductionUnit>();
		ProductionUnit mixer = new Mixer(); //0
		ProductionUnit floc1 = new Flocculator(); //1
		ProductionUnit floc2 = new Flocculator(); // 2
		ProductionUnit set1 = new Settler(); //3
		ProductionUnit set2 = new Settler(); // 4
		ProductionUnit set3 = new Settler(); // 5
		ProductionUnit set4 = new Settler();  // 6
		ProductionUnit filt1 = new Filter(); // 7
		ProductionUnit filt2 = new Filter(); //8
		ProductionUnit filt3 = new Filter(); //9
		ProductionUnit filt4 = new Filter(); // 10
		ProductionUnit tank = new DisinfectionTank();
		
		mixer.initialize();
		floc1.initialize();
		floc2.initialize();
		set1.initialize();
		set2.initialize();
		set3.initialize();
		set4.initialize();
		filt1.initialize();
		filt2.initialize();
		filt3.initialize();
		filt4.initialize();
		tank.initialize();
		
		system.add(mixer);
		system.add(floc1);
		system.add(floc2);
		system.add(set1);
		system.add(set2);
		system.add(set3);
		system.add(set4);
		system.add(filt1);
		system.add(filt2);
		system.add(filt3);
		system.add(filt4);
		system.add(tank);
				
		double[] input = new double[2];
		double[] output = new double[2];
		double[] estado = new double[4];
		String commandint;
		String[] extOrd = {null,null,null,null,null,null,null,null,null,null,null,null};
		List<String> salida = new ArrayList<String>();
		salida.add("iter,flow in,turb,mixer out,mix st,floc1 out,floc1 ac_st,floc2 out,floc2 ac_st,set1Flow,set1Vol,set1Stm,set2Flow,set2Vol,set2Stm,set3Flow,set3Vol,set3Stm,set4Flow,set4Vol,set4Stm,clear water,f1o,f1st,f2o,f2st,f3o,f3st,f40,f4st,filtered water,tankvol,tanko,tankst,end \n");
		for(int i = 0; i<576; i++) {			
	
			String linea;
			double turb = wi.turbidity(i);			
			input[0] = 100*10 + Math.random()*5;
			input[1] = turb;
			double waterDemand = wi.waterDemand(i)*70;
			linea = Integer.toString(i)+","+
					Double.toString(input[0])+","+
					Double.toString(input[1])+",";
			mixer.setInput(input);
			mixer.simul();
			output = mixer.getOutput();
			linea += Double.toString(output[0])+ ","+ Integer.toString(mixer.activeState())+",";
			input[0] = output[0]*.47;
			input[1] = turb;
			floc1.setInput(input);
			floc1.simul();
			double[] ouf1 = floc1.getOutput();
		
			input[0] = output[0]*.53;
			input[1] = turb;
			floc2.setInput(input);
			floc2.simul();
			double[] ouf2 = floc2.getOutput();
			linea += Double.toString(ouf1[0]) +","+Integer.toString(floc1.activeState())+","+Double.toString(ouf2[0]) +","+Integer.toString(floc2.activeState())+","; 
			// setter
			input[0] = ouf1[0]*.5;
			input[1] = turb;
			set1.setInput(input);
			set1.simul();
			double[] set1out =set1.getOutput();
			double[] set1State = set1.getState();
			linea +=Double.toString(set1out[0]) +","+ Double.toString(set1State[0])+","+Integer.toString(set1.activeState())+","; 


			input[0] = ouf1[0]*.5;
			input[1] = turb;
			set2.setInput(input);
			set2.simul();
			double[] set2out =set2.getOutput();
			double[] set2State = set2.getState();
			linea += Double.toString(set2out[0]) +","+ Double.toString(set2State[0])+","+Integer.toString(set2.activeState())+","; 

			input[0] = ouf2[0]*.5;
			input[1] = turb;
			set3.setInput(input);
			set3.simul();
			double[] set3out =set3.getOutput();
			double[] set3State = set3.getState();
			linea += Double.toString(set3out[0]) +","+ Double.toString(set3State[0])+","+Integer.toString(set3.activeState())+","; 


			input[0] = ouf2[0]*.5;
			input[1] = turb;
			set4.setInput(input);
			set4.simul();
			double[] set4out =set4.getOutput();
			double[] set4State = set4.getState();
			linea += Double.toString(set4out[0]) +","+ Double.toString(set4State[0])+","+Integer.toString(set4.activeState())+ "," ; 
			Double clearWater = set1out[0]+set2out[0]+set3out[0]+set4out[0];
			linea += Double.toString(clearWater)+ ","; 
			// filters
			int countFilters = 0;
			int actSt = filt1.activeState();
			if(actSt==1 || actSt==2) 
				countFilters ++; 
			actSt = filt2.activeState();
			if(actSt==1 || actSt==2) 
				countFilters ++;
			actSt = filt3.activeState();
			if(actSt==1 || actSt==2) 
				countFilters ++;
			actSt = filt4.activeState();
			if(actSt==1 || actSt==2) 
				countFilters ++;
			double waterByFilter = clearWater/countFilters;
			input[0] = waterByFilter;
			input[1] = turb;
			filt1.setInput(input);
			filt1.simul();
			double[] filt1o = filt1.getOutput();
			linea += Double.toString(filt1o[0])+ "," + Integer.toString(filt1.activeState())+","; 
			filt2.setInput(input);
			filt2.simul();
			double[] filt2o = filt2.getOutput();
			linea += Double.toString(filt2o[0])+ "," + Integer.toString(filt2.activeState())+","; 
			filt3.setInput(input);
			filt3.simul();
			double[] filt3o = filt3.getOutput();
			linea += Double.toString(filt3o[0])+ "," + Integer.toString(filt3.activeState())+","; 
			filt4.setInput(input);
			filt4.simul();
			double[] filt4o = filt4.getOutput();
			linea += Double.toString(filt4o[0])+ "," + Integer.toString(filt4.activeState())+","; 
			
			double filteredWater = filt1o[0]+filt2o[0]+filt3o[0]+filt4o[0];
			linea += Double.toString(filteredWater)+","; 
			input[0] = filteredWater;
			input[1] = waterDemand;
			tank.setInput(input);
			tank.simul();
			double[] tankOutput = tank.getOutput();
			double[] tankState = tank.getState();
			linea += Double.toString(tankState[0])+","+Double.toString(tankOutput[0]) +"," + tank.activeState()+", end \n"; 
			
			
			

		
			salida.add(linea);
			
			
			if(i==2) {
				extOrd[0] ="Start";
			}
			if(i==5) {
				extOrd[1] ="Start";
			}
			if(i==10) {
				extOrd[2] ="Start";
			}
			if(i==11) {
				extOrd[3] ="Start";
				extOrd[5] ="Start";
			}

			if(i==100) {
				extOrd[4] ="Start";
				extOrd[6] ="Start";
			}
			if(i==20) {
				extOrd[7] ="Start";
				extOrd[8] ="Start";
			}
			if(i==50) {
				extOrd[9] ="Start";
			}
			if(i==11) {
				extOrd[11] ="Start";
			}
			
			
			
			int cont = 0;
			for(ProductionUnit p : system) {
				String event = p.eventDetection();
				fireableTrans = p.disparables();
				if(event != null) {
					 commandint = p.supervisor(event);
					 p.executeOrder(commandint);
				}
				if(extOrd[cont] != null) {
					commandint = p.supervisor(extOrd[cont]);
					if(commandint != null) {
						p.executeOrder(commandint);
					}			 
					 extOrd[cont] = null;
				}
				cont++;
			}
					
		}
		File archivo = new File("salidaIntegrada.csv");
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

        System.exit(0);
	}

}
