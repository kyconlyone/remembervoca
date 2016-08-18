/**
 * Title      : Learning Status - 'Forgetting Curves' Part
 * Programmer : Kang Il Gu
 * Date       : 14.10.07 ~ 14.10.08
 * Cooperated : 'DBPool.java', 'GetReviewTime.java'
 */

package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.login.MainActivitys;

public class GetReviewTime{

	private static Context context;
	public DBPool db;
	SharedPreferences time;

	String reviewTime[] = {"-1", "-1", "-1"};	//{0 : hour, 1 : minute, 2 : count}

	public GetReviewTime(DBPool _db, Context _context){	//constuctor
		db=_db;
		context = _context;
		time = context.getSharedPreferences(MainActivitys.preName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
	}

	public String[] getReviewTime(){
		//		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		//		String str = dayTime.format(new Date(Long.valueOf(time.getString(MainActivitys.GpreTime, ""))));

		int[] maxTimeOfWords = db.getMaxTimeOnEachStates();	 //max time of words on states respectively
		int[] startTimeOfReview = db.getStartTimeOfReview(); //start time of review on states respectively

		//get review time
		//hourInterval = startTimeOfReview - maxTimeOfWords
		int hourInterval[] = minimumTimeDifference(maxTimeOfWords, startTimeOfReview);
		//minuteInterval = current time - last calculated time
		try{

			long millisecondInterval = System.currentTimeMillis() - Long.valueOf(time.getString(MainActivitys.GpreTime, ""));
			int minuteInterval = (int)millisecondInterval / (1000 * 60);	//minute

			//review time as minute = hourInterval * 60(min) - minuteInterval
			int reviewTimeMinute = hourInterval[0] * 60 - (minuteInterval);
			if(reviewTimeMinute >= 0){	//review will be started after reviewTime[0](hour) reviewTime[1](min)
				if((reviewTimeMinute/60)<10){
					reviewTime[0] = "0"+reviewTimeMinute/60;	//hour
				}else{
					reviewTime[0] = ""+reviewTimeMinute/60;	//hour
				}
				if((reviewTimeMinute%60)<10){

					reviewTime[1] = "0"+reviewTimeMinute%60;	//minute
				}else{
					reviewTime[1] = ""+reviewTimeMinute%60;	//minute
				}
				Log.e("getCountOfReviewWord", "  "+hourInterval[1]+1+"  "+ maxTimeOfWords[hourInterval[1]]+"  "+hourInterval[0]);
				reviewTime[2] = ""+db.getCountOfReviewWord(hourInterval[1]+1, maxTimeOfWords[hourInterval[1]], hourInterval[0]);	//count of review word
			}else{	//review is proceeding
				Log.e("reviewtime", ""+reviewTimeMinute+"   "+minuteInterval+"   "+hourInterval[0]+"   "+maxTimeOfWords+"    "+startTimeOfReview);
				reviewTime[0] = "0";
				reviewTime[1] = "00";
				Log.e("getCountOfReviewWord", "  "+hourInterval[1]+1+"  "+ maxTimeOfWords[hourInterval[1]]+"  "+hourInterval[0]);
				reviewTime[2] = ""+db.getCountOfReviewWord(hourInterval[1]+1, maxTimeOfWords[hourInterval[1]], hourInterval[0]);
			}
		}catch(Exception e){
			Log.e("getCountOfReviewWord", "  "+e.toString());
			Toast.makeText(context, "DBService Error", Toast.LENGTH_LONG);
			e.printStackTrace();
			//'측정할 수 없습니다' 잡아야 해요!
		}

		Log.e("Forgetting Curves part", "getReviewTime()");
		return reviewTime;
	}

	//minimum value of time difference
	public int[] minimumTimeDifference(int[] maxTimeOfWords, int[] startTimeOfReview){
		int[] minDif = new int[2];
		minDif[0]=db.infinite;	//minimum of time difference
		minDif[1]=-1;
		// state 1,2가 모두 5시간 후 복습단어 출현하는 경우가 고려되어 있지 않음.
		for(int i=0;i<maxTimeOfWords.length;i++){
			int dif = startTimeOfReview[i] - maxTimeOfWords[i];
			if(minDif[0] > dif){
				minDif[0] = dif;
				minDif[1]=i;
			}
		}
		return minDif;
	}
}

















