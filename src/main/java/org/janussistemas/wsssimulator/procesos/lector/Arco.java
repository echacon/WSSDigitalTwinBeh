package org.janussistemas.wsssimulator.procesos.lector;

public class Arco {
	private boolean entrada;
	private String nombre;
	private Lugar lugar;
	private Transicion transicion;
	public boolean isEntrada() {
		return entrada;
	}
	public void setEntrada(boolean entrada) {
		this.entrada = entrada;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Lugar getLugar() {
		return lugar;
	}
	public void setLugar(Lugar lugar) {
		this.lugar = lugar;
	}
	public Transicion getTransicion() {
		return transicion;
	}
	public void setTransicion(Transicion transicion) {
		this.transicion = transicion;
	}
}
