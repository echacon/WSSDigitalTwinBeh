package org.janussistemas.wsssimulator.model;

import java.io.Serializable;

public class Transition implements Serializable{
	private String name;
	private String intEvent;
	private String command;
	private String message;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIntEvent() {
		return intEvent;
	}
	public void setIntEvent(String intEvent) {
		this.intEvent = intEvent;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
