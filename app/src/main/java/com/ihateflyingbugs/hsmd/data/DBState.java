package com.ihateflyingbugs.hsmd.data;

public class DBState {

	int time[];
	double prob[];
	double freq[];
	int count;
	int progress;
	
	public DBState(int num){
		time = new int[num];
		prob = new double[num];
		freq = new double[num];
		count = num;
		progress = 0;
	}
}
