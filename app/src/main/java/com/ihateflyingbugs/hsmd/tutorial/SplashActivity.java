package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.Get_my_uuid;
import com.ihateflyingbugs.hsmd.KakaoLoginActivity;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.data.AsyncResultType;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Contact;
import com.ihateflyingbugs.hsmd.data.Crypter;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.data.Word;
import com.ihateflyingbugs.hsmd.indicator.SampleCirclesSnap;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.manager.OnlineLessonApplyPopup;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.PaymentData;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.model.WordUpdateData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

public class SplashActivity extends Activity {


    private static SharedPreferences mPreference;
    private MySessionStatusCallback callback;
    static boolean isStart = false;
    static Context mContext;
    static Activity activity;
    Handler handler;
    ProgressBar pb_splash;

    static boolean isActivity = false;
    static int flurry_time = 30;
    static final int FLURRY_SUCCESS = 1;

    String check_firtst = "0";
    boolean isPass = false;

    DBPool db;

    int isWithdrawnResult;

    IInAppBillingService mService;

    IabHelper mHelper;

    private String profileUrl;

    final String TAG = "SplashActivity";


    ServiceConnection mServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };


    TextView tv_splash_loading;

    boolean isFriendPush = false;
    boolean isKakaoStoryPush = false;
    boolean isPushResponse = false;
    boolean isFreePush = false;
    //	Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.d("activitygg", "splash oncreate");

        VOCAconfig application = (VOCAconfig) getApplication();
        //		mTracker = application.getDefaultTracker();
        //		mTracker.setSessionTimeout(10000);


        setContentView(R.layout.activity_splash);
        checkRouteWithPush();

        mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        mContext = getApplicationContext();
        activity = SplashActivity.this;

        pb_splash = (ProgressBar) findViewById(R.id.pb_splash);
        tv_splash_loading = (TextView) findViewById(R.id.tv_splash_loading);




        handler = new Handler();

        setDbTbl();


        checkAndroidSdkVersion();


        if (!isAvailableNetwork()) {
            return;
        }

		/*
         * check_code
		 */
        Intent service_intent = new Intent().setAction("com.android.vending.billing.InAppBillingService.BIND");
        service_intent.setPackage("com.ihateflyingbugs.hsmd");
        bindService(service_intent, mServiceConn, Context.BIND_AUTO_CREATE);

        if (mPreference.getString("Word_Pass", "hi").length() < 5) {

            new RetrofitService().getAuthorizationService().retroCheckID("", "", "", "", "", "12")
                    .enqueue(new Callback<AuthorizationData>() {
                        @Override
                        public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
                            isPass = true;
                            Config.Word_chiper = response.body().getPassword();
                            Log.e("retroCheckID", "" + response.body().getPassword());
                            mPreference.edit().putString("Word_Pass", Config.Word_chiper).commit();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e("retroCheckID", "onFailure" + t.toString());
                            Config.Word_chiper = "hi";
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                    Toast.makeText(getApplicationContext(), "서버상태가 좋지 않습니다. 잡시후에 다시 접속해주세요!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            finish();
                        }
                    });
        }


		/*
		 * check_code
		 */


        if (!(mPreference.getString("Word_Pass", "hi").length() < 5)) {
            try {
            } catch (Exception e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
            Crypter crypter = new Crypter(mPreference.getString("Word_Pass", "hi"));
            String res = null;
            try {
                res = new String(crypter.decrypt(Config.base64EncodedPublicKey), "UTF-8");
            } catch (Exception e) {
                //무슨처리를 해야하지 음 ...
            }

            mHelper = new IabHelper(this, res);
            mHelper.enableDebugLogging(true);

            new RetrofitService().getPaymentService().retroGetProductInfoList().enqueue(new Callback<PaymentData>() {
                @Override
                public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
                    int count = response.body().getCount();
                    if (count != 0) {
                        Config.google_product_id = new String[count][];
                        JSONObject json[] = new JSONObject[count];
                        List<PaymentData.Product> ProductList = response.body().getUsable_product_list();
                        for (int i = 0; i < Config.google_product_id.length; i++) {
                            Config.google_product_id[i] = new String[2];
                            Config.google_product_id[i][Config.TYPE_ID] = "" + ProductList.get(i).getProduct_type_id();
                            Config.google_product_id[i][Config.GOOGLE_PRODUCT_ID] = ProductList.get(i).getGoogle_product_id();
                            Log.d(TAG, "GOOGLE_PRODUCT_ID : " + Config.google_product_id[i][Config.GOOGLE_PRODUCT_ID]);
                        }
                        if (db.getStudentId().equals("0")) {
                            return;
                        }


                        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                            @Override
                            public void onIabSetupFinished(IabResult result) {
                                // TODO Auto-generated method stub
                                if (!result.isSuccess()) {
                                    // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면 되겠습니다 )
                                    return;
                                }

                                if (mHelper == null) {
                                    return;
                                }

                                Log.d(TAG, "Setup successful. Querying inventory.");
                                mHelper.queryInventoryAsync(mGotInventoryListener);
                            }

                        });
                    } else if (count == 0) {
                        Config.google_product_id = new String[3][];
                        Config.google_product_id[0] = new String[2];
                        Config.google_product_id[0][Config.TYPE_ID] = "101";
                        Config.google_product_id[0][Config.GOOGLE_PRODUCT_ID] = "ihfb_subs_2";

                        Config.google_product_id[1] = new String[2];
                        Config.google_product_id[1][Config.TYPE_ID] = "102";
                        Config.google_product_id[1][Config.GOOGLE_PRODUCT_ID] = "ihfb_inapp_1";

                        Config.google_product_id[2] = new String[2];
                        Config.google_product_id[2][Config.TYPE_ID] = "103";
                        Config.google_product_id[2][Config.GOOGLE_PRODUCT_ID] = "ihfb_inapp_2";


                        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                            @Override
                            public void onIabSetupFinished(IabResult result) {
                                // TODO Auto-generated method stub
                                if (!result.isSuccess()) {
                                    // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면
                                    // 되겠습니다 )
                                    return;
                                }

                                if (mHelper == null) {
                                    return;
                                }

                                Log.d(TAG, "Setup successful. Querying inventory.");
                                mHelper.queryInventoryAsync(mGotInventoryListener);
                            }

                        });
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("onFailure", "getProduct_Fail : " + t.toString());
                    Config.google_product_id = new String[3][];
                    Config.google_product_id[0] = new String[2];
                    Config.google_product_id[0][Config.TYPE_ID] = "101";
                    Config.google_product_id[0][Config.GOOGLE_PRODUCT_ID] = "ihfb_subs_2";

                    Config.google_product_id[1] = new String[2];
                    Config.google_product_id[1][Config.TYPE_ID] = "102";
                    Config.google_product_id[1][Config.GOOGLE_PRODUCT_ID] = "ihfb_inapp_1";

                    Config.google_product_id[2] = new String[2];
                    Config.google_product_id[2][Config.TYPE_ID] = "103";
                    Config.google_product_id[2][Config.GOOGLE_PRODUCT_ID] = "ihfb_inapp_2";


                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            // TODO Auto-generated method stub
                            if (!result.isSuccess()) {
                                // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면
                                // 되겠습니다 )
                                return;
                            }

                            if (mHelper == null) {
                                return;
                            }

                            Log.d(TAG, "Setup successful. Querying inventory.");
                            mHelper.queryInventoryAsync(mGotInventoryListener);
                        }

                    });


                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(SplashActivity.this, "아이템 리스트를 가져오지 못하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }

        if (isUpdating == false) {
            final Animation anim = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            anim.setRepeatCount(0);
            anim.setRepeatMode(Animation.REVERSE);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_splash_loading.startAnimation(anim);
                }
            });

            Log.e("dbversion", "retroUpdateWord start ");
            new RetrofitService().getWordUpdateService().retroUpdateWord("" + db.getDBversion())
                    .enqueue(new Callback<WordUpdateData>() {
                        @Override
                        public void onResponse(final Response<WordUpdateData> response, Retrofit retrofit) {
                            Log.e("word_update", "finish");


                            Log.e("dbversion", "response : " + response.body().getCurrent_word_version());

                            if (response.body().getResult() == AsyncResultType.RESULT_FAIL) {
                                Thread thread= new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                tv_splash_loading.setVisibility(View.VISIBLE);

                                            }
                                        });

                                        db.sendQuery(response.body());

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                callback = new MySessionStatusCallback();
                                                Session.getCurrentSession().addCallback(callback);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        tv_splash_loading.setVisibility(View.GONE);
                                                        anim.cancel();
                                                    }
                                                });

                                                if (Session.getCurrentSession().isOpened()) {
                                                    pb_splash.setVisibility(View.VISIBLE);
                                                    onSessionOpened();
                                                } else {


                                                    Log.e("activityggg", "check 2 sam");
                                                    Log.e("activitygg", "!check_firtst.equals");
                                                    Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
                                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    Session.getCurrentSession().removeCallback(callback);
                                                    finish();
                                                    startActivity(intent);
                                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                    return;
                                                }
                                            }
                                        });
                                    }
                                });

                                thread.setDaemon(true);
                                thread.start();




                            } else if (response.body().getResult() == AsyncResultType.RESULT_SUCCESS) {

                                callback = new MySessionStatusCallback();
                                Session.getCurrentSession().addCallback(callback);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_splash_loading.setVisibility(View.GONE);
                                        anim.cancel();
                                    }
                                });


                                if (Session.getCurrentSession().isOpened()) {
                                    pb_splash.setVisibility(View.VISIBLE);
                                    onSessionOpened();
                                } else {


                                    Log.e("activityggg", "check 2 sam");
                                    Log.e("activitygg", "!check_firtst.equals");
                                    Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    Session.getCurrentSession().removeCallback(callback);
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    return;
                                }


                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, "retroUpdateWord onFailure : " + t.toString());
                        }
                    });
        }

        Log.d("GOOGLE_PRODUCT_ID1", "after");



    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //		mTracker.setScreenName("SplashActivity");
        //		mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        try {
            Class service = Class.forName("com.ihateflyingbugs.hsmd.service.ChatHeadService");
            stopService(new Intent(this, service));
        } catch (ClassNotFoundException e) {
        }


        check_firtst = mPreference.getString(MainActivitys.GpreFirtst, "0");
        isActivity = false;


        pb_splash.setVisibility(View.VISIBLE);

    }


    private void checkAndroidSdkVersion() {
        // TODO Auto-generated method stub
        final String versionSDK = Build.VERSION.SDK;

        Log.e("os_version", versionSDK);


        if (Integer.parseInt(versionSDK) < 14) {
            new AlertDialog.Builder(SplashActivity.this)
                    .setMessage("현재 안드로이드 버전이 낮아 접속하실수 없습니다. 버전 업그레이드후 다시 실행해주시기 바랍니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                            FlurryAgent.logEvent("SplashActivity:HaveLowSDKVersion_" + versionSDK);
                            finish();

                        }
                    }).show();
        }


    }


    /**
     * check possbile use network
     *
     * @return true = available<pre>false = not available
     */
    private boolean isAvailableNetwork() {
        // TODO Auto-generated method stub
        boolean is_available_network = Config.isNetworkAvailable(getApplicationContext());

        if (!is_available_network) {
            FlurryAgent.logEvent("SplashActivity:NotAccessInternet");
            isFinish = true;
            AlertDialog alertDialog = new AlertDialog.Builder(SplashActivity.this).create();
            // Setting Dialog Title
            alertDialog.setTitle("인터넷을 연결할 수 없습니다.");

            // Setting Dialog Message
            alertDialog.setMessage("연결 상태를 확인한 후 다시 시도해 주세요.");


            alertDialog.setIcon(R.drawable.launcher_mdpi);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } else {
			/*
			 * check_code
			 */

            flurry_time = 20;

        }
        return is_available_network;
    }


    /**
     * make basic db structure
     */
    private void setDbTbl() {
        // TODO Auto-generated method stub
        db = DBPool.getInstance(getApplicationContext());

        db.makePushTable();
        db.calcMaxCnt();
        db.wordModify();
        db.makeFreeTable();
        db.makeLockScreenTable();
        db.injectionStudentIdColumn();
    }


    private void checkRouteWithPush() {
        // TODO Auto-generated method stub
        try {

            isFriendPush = getIntent().getExtras().getBoolean("friend");
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        try {
            isKakaoStoryPush = getIntent().getExtras().getBoolean("kakaostory");
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            isPushResponse = getIntent().getExtras().getBoolean("pushresponse");
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            isFreePush = getIntent().getExtras().getBoolean("1freeday");
        } catch (Exception e) {
            // TODO: handle exception
        }

		/*
		 * check_code
		 */
        if (getIntent().hasExtra("idx")) {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            new RetrofitService().getUseAppInfoService().retroUpdateUserPushLog(getIntent().getLongExtra("idx", 0000000) + "",
                    "3421",
                    pm.isScreenOn() + "")
                    .enqueue(new Callback<UseAppInfoData>() {
                        @Override
                        public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {

                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, "onFailure : " + t.toString());
                        }
                    });
        }

    }


    private void getIsWithdrawn() {
        new RetrofitService().getAuthorizationService().retroIsLeaveStudent(db.getStudentId())
                .enqueue(new Callback<AuthorizationData>() {
                    @Override
                    public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
                        isWithdrawnResult = response.body().getResult();

                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (isWithdrawnResult == 10 && !isFinishing()) {

                                    //db.initailizeInfo();

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                                    dialog.setTitle("밀당 영단어");
                                    dialog.setMessage("이미 탈퇴한 사용자입니다. 다시 사용을 원하는 경우 cs@lnslab.com으로 연락바랍니다.");
                                    dialog.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    dialog.create().show();
                                } else {
                                    get_Version();
                                }
                            }

                            ;
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                                    dialog.setTitle("밀당 영단어");
                                    dialog.setMessage("잠시 후 다시 시도해 주세요.");
                                    dialog.setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    dialog.create().show();
                                }


                            }

                            ;
                        });
                    }
                });
    }


    boolean isFinish = false;


    private class MySessionStatusCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // 프로그레스바를 보이고 있었다면 중지하고 세션 오픈후 보일 페이지로 이동
            pb_splash.setVisibility(View.GONE);
            Log.e("activitygg", "onSessionOpened");
            SplashActivity.this.onSessionOpened();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("activitygg", "onSessionClosed");
            pb_splash.setVisibility(View.GONE);
            if (check_firtst.equals("0")) {
                Log.e("activityggg", "check 3 sam");
                Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            } else {

                Intent intent = new Intent(SplashActivity.this, KakaoLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }


    private boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }


    protected void onSessionOpened() {


        UserManagement.requestMe(new MeResponseCallback() {


            @Override
            public void onSuccess(UserProfile userProfile) {
                // TODO Auto-generated method stub
                Log.e("activitygg", "onSessionOpened onSuccess");
                if (userProfile != null)
                    userProfile.saveUserToCache();
                Config.USER_ID = String.valueOf(userProfile.getId());
                db.insertLoginId(Config.USER_ID);
                if (mPreference.getString(MainValue.GpreName, "0").equals("0")) {
                    mPreference.edit().putString(MainValue.GpreName, userProfile.getNickname()).commit();
                    Config.NAME = userProfile.getNickname();
                }

                profileUrl = userProfile.getThumbnailImagePath();
                mPreference.edit().putString(MainValue.GpreProfileImage, profileUrl).commit();

                FlurryAgent.setUserId(Config.USER_ID);
                if (!mPreference.getString(MainValue.GpreGender, "-1").equals("-1")) {
                    byte gender = com.flurry.android.Constants.UNKNOWN;
                    String getGender = mPreference.getString(MainValue.GpreGender, "-1");
                    if (getGender.equals("2")) {
                        gender = com.flurry.android.Constants.FEMALE;
                    } else if (getGender.equals("1")) {
                        gender = com.flurry.android.Constants.MALE;
                    }
                    FlurryAgent.setGender(gender);
                    String Birth = mPreference.getString(MainValue.GpreBirth, "990405");
                    FlurryAgent.setAge(Config.getAge(Birth));

                }


                Log.e("activitygg", "get_Version start " + db.isExistStudentId());


                if (db.isExistStudentId()) {
                    getIsWithdrawn();
                } else {
                    Log.e("activityggg", "check 4 sam");
                    Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            }

            @Override
            public void onNotSignedUp() {
                // TODO Auto-generated method stub
                Log.e(TAG, "onNotSignedUp : RedirectActivity");
                Log.e("activityggg", "check 5 sam");
                Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
                Log.e("UserProfile", "onFailure");

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    Log.e(TAG, "onFailure : CLIENT_ERROR_CODE");
                    finish();
                } else {
                    Log.e(TAG, "onFailure : " + errorResult.toString());
                    Log.e("activityggg", "check 6 sam");
                    Intent intent = new Intent(SplashActivity.this, SampleCirclesSnap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e(TAG, "onSessionClosed");
            }
        });

    }

    String startTime = "";
    String endTime = "";

    protected void get_Version() {
        // TODO Auto-generated method stub
        new RetrofitService().getAuthorizationService().retroCheckVersion("10")
                .enqueue(new Callback<AuthorizationData>() {
                    @Override
                    public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
                        String isUpdate = "false";
                        String version = "";
                        String isInspect = "false";

                        Log.e("activitygg", "get_Version Resonponse try");
                        isUpdate = response.body().is_update();
                        version = response.body().getVersion();
                        isInspect = "" + response.body().getIs_inspect();
                        startTime = response.body().getStart_time();
                        endTime = response.body().getEnd_time();
                        Config.SERVER_VERSION = version;


                        Log.e("activitygg", "get_Version Resonponse finally");
                        PackageManager packageManager = getPackageManager();
                        PackageInfo infor = null;
                        try {
                            infor = packageManager.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
                        } catch (NameNotFoundException e) {
                            // TODO Auto-generated catch block
                        }

                        String cur_version = infor.versionName;
                        int code = infor.versionCode;
                        Config.VERSION = cur_version;
                        Log.e("versioninfo", "" + cur_version + "      " + version);

                        Date dated = new Date();

                        String currentVerArr[] = cur_version.trim().split("\\.");
                        String verArr[] = version.trim().split("\\.");


                        if (Integer.parseInt(currentVerArr[0]) >= Integer.parseInt(verArr[0])
                                && Integer.parseInt(currentVerArr[1]) >= Integer.parseInt(verArr[1])
                                && Integer.parseInt(currentVerArr[2]) >= Integer.parseInt(verArr[2])) {

                            Log.e("activitygg", "check_id   :     Call check_id");
                            //check_id(db.getStudentId(), Config.VERSION, mContext);
                            updateDBtbl();
                        } else {

                            if (isUpdate.equals("true")) {
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            // TODO Auto-generated method stub
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                                            dialog.setTitle("VersionUpdate");
                                            dialog.setMessage("현재 App의 버전이 낮아 마켓에서의 업그레이드가 필요합니다.");
                                            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method
                                                    // stub
                                                    FlurryAgent.logEvent("SplashActivity:NeedVersionUpdate");
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                                                            .parse("market://details?id=com.ihateflyingbugs.hsmd"));
                                                    activity.startActivity(browserIntent);
                                                    activity.finish();
                                                }
                                            });
                                            dialog.create().show();
                                        }
                                    }
                                });


                            } else if (isInspect.equals("true")) {
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            // TODO Auto-generated method stub
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                                            dialog.setTitle("서버 점검중");
                                            dialog.setMessage("현재 서버 점검중입니다. \n불편을 끼쳐드려 죄송합니다.\n\n점검시간 : " + startTime + " ~ " + endTime);
                                            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // TODO Auto-generated method
                                                    // stub

                                                    activity.finish();
                                                }
                                            });
                                            dialog.create().show();
                                        }
                                    }
                                });
                            } else {

                                Log.e("activitygg", "get_version");
                                updateDBtbl();
                                //check_id(db.getStudentId(), Config.VERSION, mContext);
                            }
                        }

                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e("activitygg", "get_Version Exception");
                        FlurryAgent.logEvent("SplashActivity:NoGetVersionReponse");
                    }
                });
    }

    boolean isUpdating = false;

    public void updateDBtbl() {
        if (isUpdating == false) {
            final Animation anim = new AnimationUtils().loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            anim.setRepeatCount(0);
            anim.setRepeatMode(Animation.REVERSE);
            tv_splash_loading.startAnimation(anim);
            isUpdating = true;
            Log.e("word_update", "updateDBtbl" + db.getDBversion());

            new RetrofitService().getStudyInfoService().retroUpdateWordState("" + db.getStudentId())
                    .enqueue(new Callback<WordUpdateData>() {
                        @Override
                        public void onResponse(Response<WordUpdateData> response, Retrofit retrofit) {
                            if (response.isSuccess()) {
                                for (int i = 0; i < response.body().getWord_list().size(); i++) {
                                    try {
                                        Word word = db.getWord(response.body().getWord_list().get(i).getWord_id());
                                        Config.unknow_count++;

                                        int ex_state = word.getState();
                                        word.increaseWrongCount();

                                        if (!word.isWrong()) {
                                            word.setWrong(true);
                                            word.setRight(false);
                                            db.updateRightWrong(false, word.get_id());
                                        }

                                        db.insertState0FlagTableElement(word, false);
                                        db.updateForgettingCurvesByNewInputs(word, Config.MAINWORDBOOK, false);
                                        db.insertReviewTimeToGetMemoryCoachMent(word);
                                        db.insertLevel(word, false);


                                        Word word_for_write = db.getWord(word.get_id());
                                        word.setState(word_for_write.getState());


                                        if (ex_state > 0) {
                                            mPreference.edit().putInt(MainValue.GpreCheckCurve, mPreference.getInt(MainValue.GpreCheckCurve, 0) + 1).commit();
                                        }
                                    }catch (NullPointerException e){

                                    }

                                }
                            } else {
                                Log.e(TAG, "retroUpdateWordState not success");
                            }


                            check_id(db.getStudentId(), Config.VERSION, mContext);


                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.e(TAG, "retroUpdateWordState onFailure : " + t.toString());
                        }
                    });

        } else {

            check_id(db.getStudentId(), Config.VERSION, mContext);
        }

    }

    private void setMySharedPreferences(String _key, String _value) {
        if (mPreference == null) {
            mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        }
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(_key, _value);
        editor.commit();
    }


    public String getVersionName(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void check_id(String id, String version, Context context) {
        Date date = new Date();
        String time = date.get_currentTime();

        Log.e("activitygg", "check_id   : " + id + "   " + version + "   " + time + "   " + Get_my_uuid.get_Device_id(context) + "   " + GCMRegistrar.getRegistrationId(context) + "   " + 18);
        new RetrofitService().getAuthorizationService().retroCheckID(id,
                version,
                time,
                "" + Get_my_uuid.get_Device_id(context),
                "" + GCMRegistrar.getRegistrationId(context),
                "" + 18).enqueue(new Callback<AuthorizationData>() {
            @Override
            public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
                Log.e("activitygg", "check_id   : Resonponse");
                FlurryAgent.setUserId(Config.USER_ID);

                int success = 0;
                int is_friend_phone_exist = 0;
                success = response.body().getResult();
                is_friend_phone_exist = response.body().getIs_friend_phone();

                if (!isActivity) {

                    if (success == 0) {
                        Log.e("activitygg", "check_id   : ID_DUPLICATION");

                        if (is_friend_phone_exist == 1) {
                            Cursor cursor = null;
                            JsonArray myCustomArray = null;
                            List<Contact> contactss = new ArrayList<Contact>();
                            ;
                            try {
                                cursor = getApplicationContext().getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
                                int contactIdIdx = cursor.getColumnIndex(Phone._ID);
                                int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
                                int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
                                int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
                                int i = 0;
                                int count = 0;
                                count = cursor.getCount();

                                cursor.moveToFirst();
                                do {

                                    String name = cursor.getString(nameIdx);
                                    String phoneNumber = cursor.getString(phoneNumberIdx);

                                    contactss.add(new Contact(name, phoneNumber));

                                    i++;

                                } while (cursor.moveToNext());

                            } catch (Exception e) {
                                Log.e("contactss", "" + e.toString());
                                contactss.add(new Contact("없음", "01000000000"));
                            } finally {

                                Gson gson = new GsonBuilder().create();
                                myCustomArray = gson.toJsonTree(contactss).getAsJsonArray();

                                if (cursor != null) {
                                    cursor.close();
                                }
                            }

                            String contacts = myCustomArray.toString();
                            Log.e(TAG, "contacts : " + contacts);

                            new RetrofitService().getAuthorizationService().retroInsertFriendPhone(db.getStudentId(), contacts)
                                    .enqueue(new Callback<AuthorizationData>() {
                                        @Override
                                        public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {

                                        }

                                        @Override
                                        public void onFailure(Throwable t) {


                                            Log.e(TAG, "onFailure : " + t.toString());
                                        }
                                    });

                            new RetrofitService().getAuthorizationService().retroUpdateUserInfo(db.getStudentId(),
                                    mPreference.getString(MainValue.GpreName, ""),
                                    mPreference.getString(MainValue.GpreBirth, ""),
                                    mPreference.getString(MainValue.GpreSchoolId, ""),
                                    mPreference.getString(MainValue.GpreEmail, ""),
                                    profileUrl,
                                    "" + db.getWordLevel(),
                                    "" + mPreference.getInt(MainValue.GpreSatGrade, 3))
                                    .enqueue(new Callback<AuthorizationData>() {
                                        @Override
                                        public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {

                                            Log.e(TAG, "" + response.body().toString());
                                        }

                                        @Override
                                        public void onFailure(Throwable t) {

                                            Log.e(TAG, "onFailure : " + t.toString());
                                        }
                                    });
                        }


                        //final Intent intent = new Intent(mContext, MainActivity.class);
                        if (db.getTutorial()) {
                            Log.e("kyc", "ip : " + getWifiIpAddress());

                            // KARAM : Online Lesson
                            Log.e("KARAM", "   Show Online Lesson Popup");

                            String mDoNotShow = mPreference.getString("onlinelesson_donot", "");

                            if (mDoNotShow.equals("yes")) {
                                final SharedPreferences.Editor mShared_Editor;
                                mShared_Editor = mPreference.edit();

                                final Long mDonotShowTime = mPreference.getLong("onlinelesson_time", 0);

                                Calendar mm = Calendar.getInstance();
                                mm.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);

                                final Long mTime = mDonotShowTime - mm.getTimeInMillis();
                                Log.e("KARAM", " -> Time : " + mTime);
                                new RetrofitService().getUseAppInfoService().retroIsApplyBetaService(db.getStudentId())
                                        .enqueue(new Callback<UseAppInfoData>() {
                                            @Override
                                            public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
                                                if (response.body().getResult() == 1) {
                                                    startWordMainActivity();
                                                } else {
                                                    if (mDonotShowTime == 0) {
                                                        mShared_Editor.putString("onlinelesson_donot", "no");
                                                        mShared_Editor.commit();

                                                        Intent intent = new Intent(SplashActivity.this, OnlineLessonApplyPopup.class);
                                                        startActivityForResult(intent, 111);
                                                    } else if (mTime < 0) {
                                                        mShared_Editor.putString("onlinelesson_donot", "no");
                                                        mShared_Editor.commit();

                                                        Intent intent = new Intent(SplashActivity.this, OnlineLessonApplyPopup.class);
                                                        startActivityForResult(intent, 111);
                                                    } else {
                                                        startWordMainActivity();
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                Log.e(TAG, "onFailure : " + t.toString());
                                                startWordMainActivity();
                                            }
                                        });


                            } else {
                                new RetrofitService().getUseAppInfoService().retroIsApplyBetaService(db.getStudentId())
                                        .enqueue(new Callback<UseAppInfoData>() {
                                            @Override
                                            public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
                                                if (response.body().getResult() == 1) {
                                                    startWordMainActivity();
                                                } else {
                                                    Intent intent = new Intent(SplashActivity.this, OnlineLessonApplyPopup.class);
                                                    startActivityForResult(intent, 111);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                Log.e(TAG, "onFailure : " + t.toString());
                                                startWordMainActivity();
                                            }
                                        });
                            }

                        } else {
                            final Intent intent = new Intent(mContext, com.ihateflyingbugs.hsmd.animtuto.MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("friend", isFriendPush);
                            intent.putExtra("kakaostory", isKakaoStoryPush);
                            intent.putExtra("pushresponse", isPushResponse);
                            intent.putExtra("1freeday", isFreePush);
                            activity.startActivity(intent);
                            activity.finish();
                            isActivity = true;
                        }

                    } else {
                        Log.e("activitygg", "check_id   : Not ID_DUPLICATION");
                        final Intent intent = new Intent(mContext, InstallActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                        isActivity = true;
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("activitygg", "check_id   : Exception  " + t.toString());
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111) {
            startWordMainActivity();
        }
    }

    void startWordMainActivity() {

        Log.d("tutorialFlag", db.getTutorial() + "");
        final Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("friend", isFriendPush);
        intent.putExtra("kakaostory", isKakaoStoryPush);
        intent.putExtra("pushresponse", isPushResponse);
        intent.putExtra("1freeday", isFreePush);
        activity.startActivity(intent);
        activity.finish();
        isActivity = true;
    }


    public static String getWifiIpAddress() {
        Log.e("activitygg", "check_id   : start");
        WifiManager wifiMgr = (WifiManager) VOCAconfig.context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip);
        return ipAddress;
    }


//	public boolean geturl(){
//		String[] proj = new String[] { Browser.BookmarkColumns. TITLE, Browser.BookmarkColumns.URL, BookmarkColumns.DATE };
//		//android.net.Uri uriCustom = Browser.BOOKMARKS_URI;
//		boolean isExist = false;
//
//		android.net.Uri[] uriCustom = {Browser.BOOKMARKS_URI, android.net.Uri.parse("content://com.android.chrome.browser/bookmarks")};
//
//
//		String sel = Browser.BookmarkColumns.BOOKMARK + " = 0"; // 0 = history, 1 = bookmark
//
//		String browser = null;
//		for(int i =0; i<uriCustom.length;i++){
//			switch (i) {
//				case 0:
//					browser = "Default";
//					break;
//				case 1:
//					browser = "Chrome";
//					break;
//				default:
//					browser = "Default";
//					break;
//			}
//			Cursor mCur = getContentResolver().query(uriCustom[i], proj, sel, null, Browser.BookmarkColumns.DATE + " DESC");
//			try {
//				mCur.getCount();
//			} catch (NullPointerException e) {
//				// TODO: handle exception
//				Log.e("bookmark", "NullPointerException            " +i);
//				continue;
//			}
//
//			mCur.moveToFirst();
//			@SuppressWarnings("unused")
//			String title = "";
//			@SuppressWarnings("unused")
//			String url = "";
//
//			if (mCur.moveToFirst() && mCur.getCount() > 0) {
//				boolean cont = true;
//				while (mCur.isAfterLast() == false && cont) {
//					title = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.TITLE));
//
//					url = mCur.getString(mCur.getColumnIndex(Browser.BookmarkColumns.URL));
//
//					Log.e("bookmark", ""+url);
//					if(url.contains("hott.kr/appdown/indexs.php")){
//						try {
//							String sub = url.split("Uid=")[1];
//
//							isExist = true;
//
//							Log.e("bookmark", ""+isExist);
////							new Async_send_adpath(sub, browser ,getApplicationContext()).execute();
//
//						} catch (ArrayIndexOutOfBoundsException e) {
//							// TODO: handle exception
//						}
//
//					}
//					// Do something with title and url
//					if(isExist){
//						break;
//					}
//					mCur.moveToNext();
//				}
//			}
//			mCur.close();
//		}
//		Log.e("bookmark", ""+isExist);
//		return isExist;
//	}


    String starttime;
    String startdate;
    Date date = new Date();
    Map<String, String> articleParams;

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
        Log.d("activitygg", "onStart         " + flurry_time);
        FlurryAgent.setContinueSessionMillis(flurry_time * 1000); // Set session timeout to 60 seconds
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.setLogLevel(Log.VERBOSE);
        articleParams = new HashMap<String, String>();
        startdate = date.get_currentTime();
        starttime = String.valueOf(System.currentTimeMillis());
        FlurryAgent.logEvent("SplashActivity", articleParams);
        Log.e("activitygg", "SplashActivity onstart");

    }


    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (isFinish) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("activitygg", "SplashActivity onstop");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            articleParams.put("WIFI", "On");
        } else {
            articleParams.put("WIFI", "Off");
        }

        articleParams.put("Start", startdate);
        articleParams.put("End", date.get_currentTime());
        articleParams.put("Duration", "" + ((Long.valueOf(System.currentTimeMillis()) - Long.valueOf(starttime))) / 1000);

        FlurryAgent.endTimedEvent("SplashActivity");
        FlurryAgent.onEndSession(this);
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // mHelper가 소거되었다면 종료
            if (mHelper == null)
                return;

            // getPurchases()가 실패하였다면 종료
            if (result.isFailure()) {
                // complain("Failed to query inventory: " + result);
                return;
            }

            if (!db.isExistStudentId()) {
                Log.e(TAG, "not exist.");
                return;
            } else {
                Log.e(TAG, "exist.");
            }

            Log.d(TAG, "Query inventory was successful.");

			/*
			 * 유저가 보유중인 각각의 아이템을 체크합니다. 여기서 developerPayload가 정상인지 여부를 확인합니다.
			 * 자세한 사항은 verifyDeveloperPayload()를 참고하세요.
			 */

            // 월 자동 결제를 구독중입니까 ?

            for (int i = 0; i < Config.google_product_id.length; i++) {
                Log.d(TAG, "product_type : " + Config.google_product_id[i][Config.GOOGLE_PRODUCT_ID]);
                Purchase Purchase = inventory.getPurchase(Config.google_product_id[i][Config.GOOGLE_PRODUCT_ID]);
                if (Purchase != null) {
                    verify_Nomal_DeveloperPayload(Purchase);
                }
            }

            // updateUi();
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            return;
        }
    };

    public void verify_Nomal_DeveloperPayload(final Purchase p) {

        String payload = p.getDeveloperPayload();

        Log.v(TAG, "payload      : " + payload);
        Log.v(TAG, "getItemType  : " + p.getItemType());
        Log.v(TAG, "getOrderId   : " + p.getOrderId());
        Log.v(TAG, "getSignature : " + p.getSignature());


        new RetrofitService().getPaymentService().retroInsertPaymentInfo(p.getDeveloperPayload(),
                p.getSignature(),
                p.getOrderId()).enqueue(new Callback<PaymentData>() {
            @Override
            public void onResponse(Response<PaymentData> response, Retrofit retrofit) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        if (!p.getItemType().equals(IabHelper.ITEM_TYPE_SUBS)) {

                            mHelper.consumeAsync(p, mConsumeFinishedListener);
                        }

                        new RetrofitService().getPaymentService().retroInsertGoogleItemConsume(db.getStudentId(), p.getDeveloperPayload())
                                .enqueue(new Callback<PaymentData>() {
                                    @Override
                                    public void onResponse(Response<PaymentData> response, Retrofit retrofit) {

                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Log.e("InsertGoogleItemConsume", "onFailure : " + t.toString());

                                    }
                                });

                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "PayLoad is not exist");

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
            }
        });
    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // mHelper가 소거되었다면 종료
            if (mHelper == null)
                return;

            // 이 샘플에서는 "관리되지 않는 제품"은 "가스" 한가지뿐이므로 상품에 대한 체크를 하지 않습니다.
            // 하지만 다수의 제품이 있을 경우 상품 아이디를 비교하여 처리할 필요가 있습니다.
            if (result.isSuccess()) {
                String month = null;
                // 성공적으로 소진되었다면 상품의 효과를 게임상에 적용합니다. 여기서는 가스를 충전합니다.

                new RetrofitService().getPaymentService().retroInsertGoogleItemConsume(db.getStudentId(), purchase.getDeveloperPayload());

                // 앱안에서의 데이터를 변경 ()

            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("activitygg", "splash onDestroy");
        super.onDestroy();
        unbindService(mServiceConn);


    }

}
