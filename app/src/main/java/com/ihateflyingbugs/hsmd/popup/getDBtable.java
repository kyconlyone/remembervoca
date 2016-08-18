package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.data.Calendar;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.ForgettingCurve;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class getDBtable extends Activity {
    static final String TAG = "getDBtable";

    Handler handler;

    TextView mContentTextView;
    Button mUseButton;

    String mStudyRoomNameString;

    String[][] json_db;

    SharedPreferences mPreferences;

    DBPool db;

    private LinearLayout ll_recovery_db;
    private LinearLayout ll_recovery_forward;
    private ProgressBar pb_recovery_db;
    int total_count = 0;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_recovery_student_db);

        handler = new Handler();

        mPreferences = getSharedPreferences(MainValue.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        db = DBPool.getInstance(getApplicationContext());

        pb_recovery_db = (ProgressBar) findViewById(R.id.pb_recovery_db);
        ll_recovery_db = (LinearLayout) findViewById(R.id.ll_recovery_db);
        ll_recovery_forward = (LinearLayout) findViewById(R.id.ll_recovery_forward);
        ll_recovery_db.setVisibility(View.GONE);

        Button bt_recovery_cancel = (Button) findViewById(R.id.bt_recovery_cancel);
        bt_recovery_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });


        Button bt = (Button) findViewById(R.id.bt_recovery);
        bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ll_recovery_db.setVisibility(View.VISIBLE);
                ll_recovery_forward.setVisibility(View.GONE);

                new RetrofitService().getUseAppInfoService().retroGetUserRestoreDb(db.getStudentId())
                        .enqueue(new Callback<UseAppInfoData>() {

                            @Override
                            public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        //thread.start();
                                        ll_recovery_db.setVisibility(View.VISIBLE);
                                    }
                                });
                                total_count = response.body().getCount();

                                List<Word> wordlist =response.body().getWord_tbl();
                                db.initialWord();



                                String time = wordlist.get(0).getReg_time();
                                if (time != null) {
                                    mPreferences.edit().putString(MainValue.GpreTime, time);
                                }


                                for (int i = 0; i < wordlist.size(); i++) {
                                    
                                    int word_result = db.updateWordtbl(wordlist.get(i));


                                    if (word_result == 0) {
                                        //시발 뭐야
                                    } else {
                                        //프로그레스 퍼센테이지 하나 증가
                                    }
                                    total_count--;

                                }

                                List<ForgettingCurve> fclist = response.body().getForgetting_curves_tbl();

                                for (int i = 0; i < fclist.size(); i++) {

                                    int fc_result = db.updateForgetting_Curve_tbl(fclist.get(i));


                                    if (fc_result == 0) {
                                        //시발 뭐야
                                    } else {
                                        //프로그레스 퍼센테이지 하나 증가

                                    }
                                    total_count--;

                                }


                                List<Calendar> calendarlist = response.body().getCalendar_data_tbl();

                                db.initialCalendarData();




                                for (int i = 0; i < calendarlist.size(); i++) {

                                    int calendar_result = db.updateCalendar_tbl(calendarlist.get(i));


                                    if (calendar_result == 0) {
                                        //시발 뭐야
                                    } else {
                                        //프로그레스 퍼센테이지 하나 증가
                                    }
                                    total_count--;

                                }

                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        ll_recovery_db.setVisibility(View.GONE);
                                        new AlertDialog.Builder(getDBtable.this)
                                                .setMessage("복구가 완료되었습니다.")
                                                .setPositiveButton("확인",
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog,
                                                                                int which) {
                                                                finish();

                                                            }
                                                        }).show();
                                    }
                                });


                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.e(TAG, "onFailure : " + t.toString());
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        ll_recovery_db.setVisibility(View.GONE);
                                        new AlertDialog.Builder(getDBtable.this)
                                                .setMessage("복구가 실패하였습니다. 잠시후에 다시 시도해주세요. 계속해서 발생시 문의 주시기 바랍니다.")
                                                .setPositiveButton("확인",
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(DialogInterface dialog,
                                                                                int which) {
                                                                finish();

                                                            }
                                                        }).show();

                                        Log.e("userdb", "Exception");

                                    }
                                });
                            }
                        });

            }
        });

    }

}
