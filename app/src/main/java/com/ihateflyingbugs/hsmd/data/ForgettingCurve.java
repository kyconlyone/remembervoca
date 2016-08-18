package com.ihateflyingbugs.hsmd.data;

public class ForgettingCurve {

	private int id;
	private int state;
	private int time;
	private double probability;
	private double frequency;

	public ForgettingCurve(){

	}

	public ForgettingCurve(int id, int state, int time, double probability, double frequency){
		this.id = id;
		this.state = state;
		this.time = time;
		this.probability = probability;
		this.frequency = frequency;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	
}
