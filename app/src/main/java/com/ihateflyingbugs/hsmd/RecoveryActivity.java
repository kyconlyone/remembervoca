package com.ihateflyingbugs.hsmd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.data.Calendar;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.ForgettingCurve;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.data.userDB;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class RecoveryActivity extends Activity {


    List<ForgettingCurve> fclist;
    List<Calendar> calendarlist;
    List<Word> wordlist;

    String TAG = "RecoveryActivity";
    SharedPreferences mPreferences;

    ListView lv_recovery;
    DBPool db;

    ArrayList<userDB> list;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        db = DBPool.getInstance(getApplicationContext());

        mPreferences = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        lv_recovery = (ListView) findViewById(R.id.lv_recovery);

        new RetrofitService().getUseAppInfoService().retroGetUserRestoreDb(db.getStudentId())
                .enqueue(new Callback<UseAppInfoData>() {
                    @Override
                    public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
                        list = new ArrayList<userDB>();
                        count = response.body().getCount();
                        fclist = response.body().getForgetting_curves_tbl();
                        wordlist = response.body().getWord_tbl();
                        calendarlist = response.body().getCalendar_data_tbl();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "onFailure : " + t.toString());
                    }
                });


        lv_recovery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                if(count==0){
                    Toast.makeText(RecoveryActivity.this, "업데이트할 DB가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int haha = 0;
                int maxWord = 0;

                String wordtbl = "";
                String forgettingtbl = "";
                String calendartbl = "";

                Word word;
                JSONObject words = new JSONObject();


                //Log.e("recoverydb", "getWords_tbl  "+list.get(haha).getWords_tbl().length() + "  wordtbl  "+wordtbl.length()+ "  date" + list.get(haha).getTime());


                //if(list.get(haha).getWords_tbl().length()>wordtbl.length()){
                Log.e("recoverydb", "haha  " + haha + "  maxWord" + maxWord);

                wordtbl = list.get(position).getWords_tbl();
                forgettingtbl = list.get(position).getForgetting_curves_tbl();
                calendartbl = list.get(position).getCalendar_tbl();

                String time = list.get(maxWord).getTime();
                Log.e("recoverydb", "" + time);
                Date date = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    date = sdf.parse(time);
                    mPreferences.edit().putString(MainActivitys.GpreTime, "" + date.getTime());
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Log.e("recoverydb", "" + date.getTime());

                Log.e("recoverydb", "" + (System.currentTimeMillis() - date.getTime()) / 3600000 / 24);


                DBPool db = DBPool.getInstance(getApplicationContext());

                for (int i = 0; i < wordlist.size(); i++) {

                    int word_result = db.updateWordtbl(wordlist.get(i));


                    if (word_result == 0) {
                        //시발 뭐야
                    } else {
                        //프로그레스 퍼센테이지 하나 증가
                    }

                }

                for (int i = 0; i < fclist.size(); i++) {

                    int fc_result = db.updateForgetting_Curve_tbl(fclist.get(i));


                    if (fc_result == 0) {
                        //시발 뭐야
                    } else {
                        //프로그레스 퍼센테이지 하나 증가
                    }

                }

                for (int i = 0; i < calendarlist.size(); i++) {

                    int calendar_result = db.updateCalendar_tbl(calendarlist.get(i));


                    if (calendar_result == 0) {
                        //시발 뭐야
                    } else {
                        //프로그레스 퍼센테이지 하나 증가
                    }

                }

            }
        });


    }


}
