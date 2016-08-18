/** 
 * Title       : Learning Status - 'WILL' Part : upload use time of user
 * Programmer  : Kang Il Gu
 * Date        : 14.10.02(Thu) ~ 14.10.06(Mon), '14.12.09(Tue)
 * Description : '14.12.09(Tue) Add 'use_time_sec' column and change name of columns
 * Cooperated  : 'DBPool.java', 'UseTime.java', 'UploadUseTime.java', 
 * 				 'Async_upload_use_time.java', 'upload_use_time.php'
 */

package com.ihateflyingbugs.hsmd.data;


public class UseTime {
	
	public String id;		//id
	public int[] grade;		//grade
	public int[][] use_time;//use_time[][0] : second, use_time[][1] : date   
	public int count;		//count of data
	public int study_word;
	public int review_word;
	public int will_review_word;
	public int goal_time;
	public int total_count;
	public MildangDate local_time;
	
	public UseTime(int num){
		grade = new int[num];		//grade
		use_time = new int[num][];	//time
		for(int i=0;i<use_time.length;i++){
			use_time[i]=new int[2];	//{second, date}
		}
		count = num;	//count
		
	}
	

}
