package org.janussistemas.wsssimulator.procesos.lector;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class LectorPNML {
	private RedDePetri red;
	private String error;
	
	public RedDePetri getRed() {
		return red;
	}
	public void setRed(RedDePetri red) {
		this.red = red;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public boolean leerRed(String archivo) {

		String nombre;
		
		// Lectura de fichero_origen.xml
		try {
		File fXmlFile = new File(archivo);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();

		
		// Ahora documento es el XML leido en memoria.

		// Escritura de fichero_destino.xml
		NodeList nRedes = doc.getElementsByTagName("net");
		if(nRedes.getLength() == 1) {
			red = new RedDePetri();
			red.setNombre(archivo);
			red.inicializar();
			Node net = nRedes.item(0);
			red.setNombre(archivo.substring(archivo.lastIndexOf("/")+1, archivo.length()));
			
			// obtener la lista de nodos en la red.
			
			NodeList netNodeList = net.getChildNodes();		
			
			for (int temp = 0; temp < netNodeList.getLength(); temp++) {
				Node nNode = netNodeList.item(temp);
				nombre = nNode.getNodeName();
				if(nombre == "place") {
					Lugar lugar = new Lugar();
					NodeList listaNodo = nNode.getChildNodes();
					String identificador = ((Element) nNode).getAttribute("id");
					lugar.setIdentificador(identificador);
					lugar.setMarcacionInicial(0);
					for(int i=0; i < listaNodo.getLength(); i++){
						Node nodo = listaNodo.item(i);
						if(nodo.getNodeName() == "name") {
							int lgn = nodo.getChildNodes().getLength();
							for(int j= 0; j< lgn;j++){
								if(nodo.getChildNodes().item(j).getNodeType()==1 && nodo.getChildNodes().item(j).getNodeName().contentEquals("text")) {
									lugar.setNombre(nodo.getChildNodes().item(j).getFirstChild().getNodeValue().trim());
								}
							}
						}	
						if(nodo.getNodeName() == "initialMarking") {
							int lgn = nodo.getChildNodes().getLength();
							
							for(int j= 0; j< lgn;j++){
								if(nodo.getChildNodes().item(j).getNodeType()==1) {
									String valor = nodo.getChildNodes().item(j).getFirstChild().getNodeValue().trim();
									lugar.setMarcacionInicial((Integer.parseInt(valor))); 
								}
							}
						}
					}
					red.getListaLugar().add(lugar);
					
				}
				if(nombre == "transition") {
					Transicion transicion = new Transicion();
					NodeList listaNodo = nNode.getChildNodes();
					String identificador = ((Element) nNode).getAttribute("id");
					transicion.setIdentificador(identificador);
					transicion.setTipo(0);
					for(int i=0; i < listaNodo.getLength(); i++){
						Node nodo = listaNodo.item(i) ;
						if(nodo.getNodeName() == "name") {
							int lgn = nodo.getChildNodes().getLength();
							for(int j= 0; j< lgn;j++){
								if(nodo.getChildNodes().item(j).getNodeType()==1 && nodo.getChildNodes().item(j).getNodeName().contentEquals("text")) 
									transicion.setNombre(nodo.getChildNodes().item(j).getFirstChild().getNodeValue().trim());
							}
						}								
						if(nodo.getNodeName() == "toolspecific") {
							NodeList listaWoPeD = nodo.getChildNodes();
							for(int iListaWoPeD = 0; iListaWoPeD < listaWoPeD.getLength(); iListaWoPeD++) {
								Node nodoListaWoPeD = listaWoPeD.item(iListaWoPeD);
								if(nodoListaWoPeD.getNodeName() == "trigger") {
									String tipoString = ((Element) nodoListaWoPeD).getAttribute("type");
									tipoString = tipoString.trim();
									transicion.setTipo(Integer.parseInt(tipoString));
								}
								if(nodoListaWoPeD.getNodeName() == "transitionResource"){
									String recurso = ((Element) nodoListaWoPeD).getAttribute("organizationalUnitName");
									String rol = ((Element) nodoListaWoPeD).getAttribute("roleName");
									transicion.setRecurso(recurso);
									transicion.setRol(rol);
								}
							}

						}
						
					}
					red.getListaTransicion().add(transicion);
				}
				if(nombre == "arc") {
					String idOrigen = ((Element) nNode).getAttribute("source");
					idOrigen = idOrigen.trim();
					String idDestino = ((Element) nNode).getAttribute("target");
					idDestino=idDestino.trim();
					String idNombre =  ((Element) nNode).getAttribute("id");
					red.getListaArco().add(determinar(idNombre, idOrigen, idDestino));
				}
				if(nombre == "toolspecific"){
					NodeList listaNodo = nNode.getChildNodes();
					for(int i=0; i<listaNodo.getLength();i++){
						Node nodo = listaNodo.item(i);
						if(nodo.getNodeName() == "resources") {
							NodeList listaResources = nodo.getChildNodes();
							for(int j=0;j<listaResources.getLength();j++){
								Node rec = listaResources.item(j);
								if(rec.getNodeName().contentEquals("role")) {
									String nomRole = ((Element) rec).getAttribute("Name");
									red.getListaRol().add(nomRole);
								}
								if(rec.getNodeName().contentEquals("organizationUnit")) {
									String nomRecurso = ((Element) rec).getAttribute("Name");
									red.getListaRecurso().add(nomRecurso);
								}

							}
						}
					}
				}
			}
		}
	
	}
	catch (Exception e) {
		e.printStackTrace();
		error = "Archivo no leido";
		return false;
		}
		
		return true;
	}
	private Arco determinar(String nombre, String origen, String destino){
		Arco arco = new Arco();
		if(origen.startsWith("p")){
			arco.setEntrada(true);
			arco.setLugar(buscarLugar(origen));
			arco.setTransicion(buscarTransicion(destino));
		}
		else {
			arco.setEntrada(false);
			arco.setLugar(buscarLugar(destino));
			arco.setTransicion(buscarTransicion(origen));
		}
		return arco;
	}
	private Lugar buscarLugar(String nLugar){
		Lugar lugar = null;
		Iterator<Lugar> itLugar = red.getListaLugar().iterator();
		while (lugar == null && itLugar.hasNext()){
			Lugar tmp = itLugar.next();
			if(tmp.getIdentificador().contentEquals(nLugar)) {
				lugar = tmp;
			}
		}
		return lugar;
	}
	private Transicion buscarTransicion(String nTrans) {
		Transicion trans = null;
		Iterator<Transicion> itTrans = red.getListaTransicion().iterator();
		while(trans == null && itTrans.hasNext()){
			Transicion tmp = itTrans.next();
			if(tmp.getIdentificador().contentEquals(nTrans)){
				trans = tmp;
			}
		}
		return trans;
	}
}
