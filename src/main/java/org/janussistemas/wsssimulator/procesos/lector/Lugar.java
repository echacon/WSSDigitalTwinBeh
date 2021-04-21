package org.janussistemas.wsssimulator.procesos.lector;

public class Lugar {

	private String identificador;
	private String nombre;
	private int marcacionInicial;
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getMarcacionInicial() {
		return marcacionInicial;
	}
	public void setMarcacionInicial(int marcacionInicial) {
		this.marcacionInicial = marcacionInicial;
	}
}
