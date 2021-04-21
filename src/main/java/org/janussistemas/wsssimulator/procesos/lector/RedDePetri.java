package org.janussistemas.wsssimulator.procesos.lector;

import java.util.ArrayList;
import java.util.List;

public class RedDePetri {
	private String nombre;
	private List<Lugar> listaLugar;
	private List<Transicion> listaTransicion;
	private List<Arco> listaArco;
	private List<String> listaRol;
	private List<String> listaRecurso;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<Lugar> getListaLugar() {
		return listaLugar;
	}
	public void setListaLugar(List<Lugar> listaLugar) {
		this.listaLugar = listaLugar;
	}
	public List<Transicion> getListaTransicion() {
		return listaTransicion;
	}
	public void setListaTransicion(List<Transicion> listaTransicion) {
		this.listaTransicion = listaTransicion;
	}
	public List<Arco> getListaArco() {
		return listaArco;
	}
	public void setListaArco(List<Arco> listaArco) {
		this.listaArco = listaArco;
	}
	public List<String> getListaRol() {
		return listaRol;
	}
	public void setListaRol(List<String> listaRol) {
		this.listaRol = listaRol;
	}
	public List<String> getListaRecurso() {
		return listaRecurso;
	}
	public void setListaRecurso(List<String> listaRecurso) {
		this.listaRecurso = listaRecurso;
	}
	public void inicializar(){
		listaLugar = new ArrayList<Lugar>();
		listaTransicion = new ArrayList<Transicion>();
		listaArco = new ArrayList<Arco>();
		listaRecurso = new ArrayList<String>();
		listaRol = new ArrayList<String>();
	}
}
