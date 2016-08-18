/**
 * Title       : Learning Status - 'Memory' Part - Coach ment : get Coach announcment of Memory part
 * Programmer  : Kang Il Gu
 * Date        : '14.12.15(Mon)
 * Description : '14.12.15(Mon) 1. 시간에 따른 망각률 / 2. 평균 복습 시간 / 3. 줄어든 복습 시간에 따른 줄어든 망각률
 * Cooperated  : 'GetMemoryCoachMent.java', 'DBPool.java'
 */

package com.ihateflyingbugs.hsmd;

import android.util.Log;

import com.ihateflyingbugs.hsmd.data.DBPool;

public class GetMemoryCoachMent{

	public DBPool db;
	public int min_study_count = 100;

	public GetMemoryCoachMent(DBPool _db){
		db=_db;
		Log.e("Memory part", "GetMemeoryCoachMent()");
	}

	/*
	 * get remember rate as time
	 */
	public double[] getForgettingRateAsTime(int state){
		double[] forgetting_rate = {-1, -1, -1, -1};
		int studyCount = db.getStudyCountOnState(state);
		if(studyCount >= min_study_count){
			int[] forgetting_time = {3, 24, 24*7, 24*7*30};	//{3hours, 1day, 1week, 1month}
			for(int i=0;i<forgetting_time.length;i++){
				forgetting_rate[i] = 1 - db.getProbabilityAsTime(state, forgetting_time[i]);
			}
		}else{
			;
		}

		return forgetting_rate;	//{remember rate after 3hours, 1day, 1week, 1month}
	}

	/*
	 * get average review time to get memory coach ment
	 */
	//transform (hour) to (hour and min)
	public int[] transformToHourAndMin(double hourTime){
		int transformedTime[] = {-1, -1};	//{hour, minute}
		transformedTime[0] = (int)hourTime;
		transformedTime[1] = (int) (hourTime*60)%60;

		return transformedTime;

	}

	//get average review hour to get memory coach ment
	public double getAverageReviewTimeByHour(int state){
		double averageReviewHour = db.getReviewTime(state);

		return averageReviewHour;
	}

	//get average review time(transformed to hour and minute) to get memory coach ment
	public int[] getAverageReviewTimeByHourAndMinute(int state){
		int[] averageReviewTime = {-1, -1};	//{hour, minute}
		int studyCount = db.getStudyCountOnState(state);
		if(studyCount >= min_study_count){
			double averageReviewHour = getAverageReviewTimeByHour(state);
			averageReviewTime = transformToHourAndMin(averageReviewHour);
		}else{
			;
		}

		return averageReviewTime;	//{hour, minute}
	}

	/*
	 * get reduced forgetting rate as reduced time
	 */
	public double[] getReducedProbabilityAsReducedTime(int state){
		double[] reducedValue = {-1, -1, -1};	//{hour, minute, percentage}
		int studyCount = db.getStudyCountOnState(state);
		if(studyCount >= min_study_count){
			double time[] = {-1, getAverageReviewTimeByHour(state)};	//time[0] <= time[1]
			double prob[] = {db.getScoreCriterion(), -1};	//prob[0] >= prob[1]
			time[0] = db.getTimeAsProbability(state, prob[0]);
			prob[1] = db.getProbabilityAsTime(state, time[1]);
			int[] reducedTime = transformToHourAndMin(time[1] - time[0]);
			double reducedProb = prob[0] - prob[1];

			reducedValue[0] = reducedTime[0];	//hour
			reducedValue[1] = reducedTime[1];	//minute
			reducedValue[2] = reducedProb;		//percentage
		}else{
			;
		}

		return reducedValue;	//{hour, minute, percentage}
	}
}

















