package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.model.NotificationData;
import com.ihateflyingbugs.hsmd.model.WordData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.math.BigDecimal;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * 복습구간 진입시 복습단어중 최소 10% 이상이 수능에 출제될 확률을 구한다.<br>
 * 2015.02.06 최초작성
 * @author Daehyeon
 *
 */
public class GetExpectRateInReviewWords {
	int gradeWordsNum[] = {0, 0, 0, 0, 0};	// 각 단어장 레벨별 복습단어 갯수
	BigDecimal gradeRate[];					// 각 단어장 레벨별 수능에 나올 확률
	BigDecimal gradeRate2[];				// 각 단어장 레벨별 수능에 나오지 않을 확률
	double examRate = 0.0;
	double bcArr[][] = new double[5][];//new BigDecimal[5][];[][];
	int N = 0;

	int sort;

	DBPool db;
	/**
	 * @param wordCodes 복습단어의 단어 코드 배열
	 */
	public GetExpectRateInReviewWords(int wordCodes[], int wordCount[], int sort) {
		gradeWordsNum = wordCount;
		this.sort = sort;

		gradeRate = new BigDecimal[5];
		gradeRate2 = new BigDecimal[5];

		db = DBPool.getInstance(VOCAconfig.context);


		examRate = 0.0;

//		gradeRate[0] = new BigDecimal("");
//		gradeRate[1] = new BigDecimal("");
//		gradeRate[2] = new BigDecimal("");
//		gradeRate[3] = new BigDecimal("");
//		gradeRate[4] = new BigDecimal("");
//
//		gradeRate2[0] = new BigDecimal(""+(1-0.1));
//		gradeRate2[1] = new BigDecimal(""+(1-0.0));
//		gradeRate2[2] = new BigDecimal(""+(1-0.0));
//		gradeRate2[3] = new BigDecimal(""+(1-0.0));
//		gradeRate2[4] = new BigDecimal(""+(1-0.0));

		Gson gson = new Gson();
		String word_json = gson.toJson(wordCodes);

		new RetrofitService().getWordService().retroGetWordExamRate(word_json)
				.enqueue(new Callback<WordData>() {
					@Override
					public void onResponse(Response<WordData> response, Retrofit retrofit) {

						gradeRate[0] = new BigDecimal(response.body().getGrade1()+"");
						gradeRate2[0] = new BigDecimal((1-response.body().getGrade1())+"");
						gradeRate[1] = new BigDecimal(response.body().getGrade2()+"");
						gradeRate2[1] = new BigDecimal((1-response.body().getGrade2())+"");
						gradeRate[2] = new BigDecimal(response.body().getGrade3()+"");
						gradeRate2[2] = new BigDecimal((1-response.body().getGrade3())+"");
						gradeRate[3] = new BigDecimal(response.body().getGrade4()+"");
						gradeRate2[3] = new BigDecimal((1-response.body().getGrade4())+"");
						gradeRate[4] = new BigDecimal(response.body().getGrade5()+"");
						gradeRate2[4] = new BigDecimal((1-response.body().getGrade5())+"");

						int maxN = (int) Math.ceil((gradeWordsNum[0] + gradeWordsNum[1] + gradeWordsNum[2] + gradeWordsNum[3] + gradeWordsNum[4]) / 10.0);
						if (maxN > 50) {
							maxN = 50;
						}
//				BigDecimal bc = new BigDecimal("0.2");

						for (int i = 0;i<5;i++){
							bcArr[i] = new double[maxN+1];
							for(int j = 0;j<=maxN;j++){
								bcArr[i][j] = (gradeRate[i].pow(j)).multiply(gradeRate2[i].pow((gradeWordsNum[i] - j) >= 0 ? gradeWordsNum[i] - j : 0)).multiply(combinationCalculate(gradeWordsNum[i] ,j)).doubleValue();
								//Log.d("RateLog", bcArr[i][j].doubleValue() + " " + bcArr[i][j]);
							}
						}
						//(cursor.getCount()/20 > 5) ? 5 : cursor.getCount()/20;
//				Log.d("RateTest", "before " + bc.pow(2000));

						for (N = 0; N <= maxN; N++) {
							Log.d("RateTest", "mid " +N + " : " + examRate);
							int i1 = 0;
							int i2 = 0;
							int i3 = 0;
							int i4 = 0;
							int i5 = 0;

							for (i1 = 0; i1 <= ((gradeWordsNum[0] > N) ? N : gradeWordsNum[0]) ; i1++) {
								if (i1 == N) {
									examRate = examRate + bcArr[0][i1]*bcArr[1][i2]*bcArr[2][i3]*bcArr[3][i4]*bcArr[4][i5];
									i1=0;
									break;
								}
								for (i2 = 0; i2 <= ((gradeWordsNum[1] > N) ? N : gradeWordsNum[1]); i2++) {
									if (i1 + i2 == N) {
										examRate = examRate + bcArr[0][i1]*bcArr[1][i2]*bcArr[2][i3]*bcArr[3][i4]*bcArr[4][i5];
										i2=0;
										break;
									}
									for (i3 = 0; i3 <=((gradeWordsNum[2] > N) ? N : gradeWordsNum[2]); i3++) {
										if (i1 + i2 + i3 == N) {
											examRate = examRate + bcArr[0][i1]*bcArr[1][i2]*bcArr[2][i3]*bcArr[3][i4]*bcArr[4][i5];
											i3=0;
											break;
										}
										for (i4 = 0; i4 <= ((gradeWordsNum[3] > N) ? N : gradeWordsNum[3]); i4++) {
											if (i1 + i2 + i3 + i4 == N) {
												examRate = examRate + bcArr[0][i1]*bcArr[1][i2]*bcArr[2][i3]*bcArr[3][i4]*bcArr[4][i5];
												i4=0;
												break;
											}
											for (i5 = 0; i5 <= ((gradeWordsNum[4] > N) ? N : gradeWordsNum[4]); i5++) {
												if (i1 + i2 + i3 + i4 + i5 == N) {
													examRate = examRate + bcArr[0][i1]*bcArr[1][i2]*bcArr[2][i3]*bcArr[3][i4]*bcArr[4][i5];
													i5=0;
													break;
												}
											}
										}
									}
								}
							}
						}
						Log.d("RateTest", "after " + examRate);
						pushSend((1.0f-examRate)*100);
					}

					@Override
					public void onFailure(Throwable t) {
						Log.d("ExpectRateInReviewWords", "retroGetWordExamRate onFailure : " + t.toString());
					}
				});
	}

	public void pushSend(double examRate){
		SharedPreferences settings = VOCAconfig.context.getSharedPreferences(MainValue.preName,
				Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

		String word_count = ""+(gradeWordsNum[0] + gradeWordsNum[1] + gradeWordsNum[2] + gradeWordsNum[3] + gradeWordsNum[4]);

		new RetrofitService().getNotificationService().retroFirstReviewPush(db.getStudentId(),
																			""+examRate,
																			word_count,
																			""+ NotificationData.PUSH_MEMORY_REVIEW_FIRST_TIME)
				.enqueue(new Callback<NotificationData>() {
					@Override
					public void onResponse(Response<NotificationData> response, Retrofit retrofit) {
						Log.d("ReviewPushLog", response.toString());
					}

					@Override
					public void onFailure(Throwable t) {
						Log.d("retroFirstReviewPush", "onFailure : "+t.toString());
					}
				});

	}

	public BigDecimal combinationCalculate(int n, int r){
		int k;
		if(n-r > r){
			k=r;
		}else {
			k=n-r;
		}

		double upperC = 1;
		for(int i=0;i<k;i++){
			upperC=upperC*(n-i)/(k-i);
		}
//		double lowerC = 1;
//		for(int i=0;i<k;i++){
//			lowerC=lowerC*(k-i);
//		}

		Log.d("combination", n + " " + r + " " + upperC);
		return new BigDecimal("" + (upperC));
	}

}
