package com.ihateflyingbugs.hsmd.data;

public class Time {

	int Time;
	float column0;
	int column1;
	int  first,  second,  third,  fourth,  fifth, sixth,  seventh,  eighth, ninth;
	
	float one, two, three, four, five, six, seven, eight, nine;
	
	
	public Time( int Time, float column0, int column1)
	{
		this.Time=Time;
		
		this.column0=column0;
		this.column1=column1;
	}
	
	public Time(int Time, float one, int first, float two, int second, float three, int third, float four, int fourth, float five, int fifth,
			float six, int sixth, float seven, int seventh, float eight, int eighth, float nine, int ninth){
		
		this.Time=Time;

		this.one=one;
		this.first=first;
		
		this.two=two;
		this.second=second;
		
		this.three=three;
		this.third=third;

		this.four=four;
		this.fourth=fourth;
		
		this.five=five;
		this.fifth=fifth;
		
		this.six=six;
		this.sixth=sixth;

		this.seven=seven;
		this.seventh=seventh;
		
		this.eight=eight;
		this.eighth=eighth;
		
		this.nine=nine;
		this.ninth=ninth;

	}
	
	public void set_column0(float column0){
		this.column0 = column0;
	}
	
	public void set_column1(int column1){
		this.column1 = column1;
	}
	
	public float get_column0(){
		return column0;
	}
	public int get_column1(){
		return column1;
	}
	
	public void set_Time(int Time){
		this.Time = Time;
	}
	
	public int get_Time(){
		return Time;
	}
	public float get_one(){
		return one;
	}
	public float get_two(){
		return two;
	}
	public float get_three(){
		return three;
	}
	public float get_four(){
		return four;
	}
	public float get_five(){
		return five;
	}
	public float get_six(){
		return six;
	}
	public float get_seven(){
		return seven;
	}
	public float get_eight(){
		return eight;
	}
	public float get_nine(){
		return nine;
	}
	
	public float get_property(int state){
		float property;

		switch(state)
		{
		case 1: property = one; break;
		case 2: property = two; break;
		case 3: property = three; break;
		case 4: property = four; break;
		case 5: property = five; break;
		case 6: property = six; break;
		case 7: property = seven; break;
		case 8: property = eight; break;
		case 9: property = nine; break;
		default : property = nine; break;
		}
		
		return property;
	}
}
