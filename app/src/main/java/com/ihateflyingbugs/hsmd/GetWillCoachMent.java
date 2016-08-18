/**
 * Title       : Learning Status - 'WILL' Part : get average use time
 * Programmer  : Kang Il Gu
 * Date        : 14.10.6 ~ 14.10.06, '14.12.09(Tue) - '14.12.10(Wed)
 * Description : '14.12.09(Tue) Change getting average use time to will coach ment
 * 				 '14.12.10(Wed) get will coach ment
 * Cooperated  : 'GetWillCoachMent.java', 'Async_get_will_coach_ment.java', 'get_will_coach_ment.php'
 */

package com.ihateflyingbugs.hsmd;

import android.util.Log;

import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GetWillCoachMent{

	public GetWillCoachMent(String id, String birth, int grade){
		try {
			int sum = 0;
			if (Integer.valueOf((String) birth.subSequence(0, 2)) > 50) {
				sum = 1900;
				int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
				sum += years;
			} else {
				sum = 2000;
				int years = Integer.valueOf((String) birth.subSequence(0, 2)) - 1;
				sum += years;
			}
			birth =""+sum+"0101";
		} catch (Exception e) {
			// TODO: handle exception
			birth= "19970301";	//send just year of birth
		}

		Log.e("CoachMent", "0  : " + birth+"  "+ grade);

		new RetrofitService().getStudyInfoService().retroGetWillCoachMent(id,
																			birth,
																			""+grade)
				.enqueue(new Callback<StudyInfoData>() {
					@Override
					public void onResponse(Response<StudyInfoData> response, Retrofit retrofit) {
						double averageUseTimeOfUser = response.body().getAverageUseTimeOfUser(); // ������ ��� ��� �ð�
						Log.e("CoachMent", "1 " + averageUseTimeOfUser);
						double averageUseTimeAsAge = response.body().getAverageUseTimeAsAge(); // ���� ����(�г�)�� ��� ���ð�
						Log.e("CoachMent", "2 " + averageUseTimeAsAge);
						int theNumberOfPeople = response.body().getTheNumberOfPeople(); // �� ��� ��
						Log.e("CoachMent", "3 " + theNumberOfPeople);
						int userRankAsUseTime = response.body().getUserRankAsUseTime(); // �� ���
						Log.e("CoachMent", "4 " + userRankAsUseTime);
						int goneUpRankAsUseTime = response.body().getGoneUpRankAsUseTime(); // �ö� ���
						Log.e("CoachMent", "5 " + goneUpRankAsUseTime);
						double averageUseTimeAsAgeMonth = response.body().getAverageUseTimeAsAgeMonth();
						Log.e("CoachMent", "6 " + averageUseTimeAsAgeMonth);
						double averageUseTimeAsAgeAndGrade = response.body().getAverageUseTimeAsAgeAndGrade();
						Log.e("CoachMent", "7 " + averageUseTimeAsAgeAndGrade);
						double averageUseTimeAsAgeAndGradeMonth = response.body().getAverageUseTimeAsAgeAndGradeMonth();
						Log.e("CoachMent", "8 " + averageUseTimeAsAgeAndGradeMonth);

						setWillCoachMentValue(averageUseTimeOfUser, averageUseTimeAsAge, theNumberOfPeople, userRankAsUseTime, goneUpRankAsUseTime, averageUseTimeAsAgeMonth, averageUseTimeAsAgeAndGrade, averageUseTimeAsAgeAndGradeMonth);

					}

					@Override
					public void onFailure(Throwable t) {

						Log.e("CoachMent", "onFailure " + t.toString());
					}
				});
	}

	public void setWillCoachMentValue(double _averageUseTimeOfUser, double _averageUseTimeAsAge,
									  int _theNumberOfPeople, int _userRankAsUseTime, int _goneUpRankAsUseTime,
									  double _averageUseTimeAsAgeMonth ,double _averageUseTimeAsAgeAndGrade ,double _averageUseTimeAsAgeAndGradeMonth ){
		Config.averageUseTimeOfUser = _averageUseTimeOfUser;
		Config.averageUseTimeAsAge  = _averageUseTimeAsAge;
		Config.theNumberOfPeople    = _theNumberOfPeople;
		Config.userRankAsUseTime    = _userRankAsUseTime;
		Config.goneUpRankAsUseTime  = _goneUpRankAsUseTime;
		Config.averageUseTimeAsAgeMonth = _averageUseTimeAsAgeMonth;
		Config.averageUseTimeAsAgeAndGrade = _averageUseTimeAsAgeAndGrade;
		Config.averageUseTimeAsAgeAndGradeMonth = _averageUseTimeAsAgeAndGradeMonth;
	}

}
