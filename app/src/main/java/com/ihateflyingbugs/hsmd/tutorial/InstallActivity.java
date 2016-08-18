package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.ihateflyingbugs.hsmd.data.AsyncResultType;
import com.ihateflyingbugs.hsmd.CommonUtilities;
import com.ihateflyingbugs.hsmd.Get_my_uuid;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.animtuto.HitTutoActivity;
import com.ihateflyingbugs.hsmd.animtuto.WillTutoActivity;
import com.ihateflyingbugs.hsmd.data.Config;
import com.ihateflyingbugs.hsmd.data.Contact;
import com.ihateflyingbugs.hsmd.data.DBPool;
import com.ihateflyingbugs.hsmd.data.Date;
import com.ihateflyingbugs.hsmd.data.MainValue;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.login.TimeClass;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.model.UseAppInfoData;
import com.ihateflyingbugs.hsmd.model.WordUpdateData;
import com.ihateflyingbugs.hsmd.popup.DocPopup;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import org.andlib.ui.CircleAnimationView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;

//import com.ihateflyingbugs.hsmd.login.DocPopup;

public class InstallActivity extends Activity implements OnClickListener {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 2580:
                if (data.getBooleanExtra("isOk", false)) {
                    cb_collection.setChecked(true);
                } else {
                    cb_collection.setChecked(false);
                }
                break;
            case 3690:
                if (data.getBooleanExtra("isOk", false)) {
                    cb_utilization.setChecked(true);
                } else {
                    cb_utilization.setChecked(false);
                }
                break;
            case 6666:
                et_page5_school.setText(data.getExtras().getString("school_name"));
                school_name = data.getExtras().getString("school_name");
                school_id = data.getExtras().getInt("school_id");
                break;
            default:

                break;
        }
    }


    String TAG = "InstallActivity";

    int school_id = 0;
    String school_name = "";

    private static final int TRAY_DIM_X_DP = 48; // Width of the tray in dps
    private static final int TRAY_DIM_Y_DP = 48; // Height of the tray in dps

    RelativeLayout rl_page_backward;

    LinearLayout ll_page_backward_bg;
    LinearLayout ll_intropage;
    LinearLayout ll_page_backward_picture;
    LinearLayout ll_page1;
    LinearLayout ll_page2;
    LinearLayout ll_page3;
    LinearLayout ll_page4;
    LinearLayout ll_page5;
    RelativeLayout ll_page6;
    LinearLayout ll_progress_gauge;
    LinearLayout ll_page5_signin;
    LinearLayout ll_info_input_finish;
    LinearLayout ll_progress;
    View ll_progress_guage;
    View ll_progress_back;
    LinearLayout ll_page6_confirm;
    LinearLayout ll_intropage_not_use_cellular;

    TextView tv_progress;
    TextView tv_page1_title;
    TextView tv_page1_contents;
    TextView tv_page1_ongoing;
    ImageView iv_intropage_title;
    TextView tv_intropage_contents;
    TextView tv_page3_title;
    TextView tv_page4_title;
    TextView tv_page4_contents;
    TextView tv_page6_title;
    TextView tv_page6_contents;
    TextView tv_page5_grade_input;
    TextView tv_page2_finish;
    TextView notice_data;

    CircleAnimationView bt_page3_confirm;
    CircleAnimationView bt_page4_confirm;
    CircleAnimationView bt_page5_signin;
    CircleAnimationView bt_page6_confirm;
    CircleAnimationView bt_intropage_confirm;

    EditText et_page3_systemname;
    EditText et_page5_name;
    EditText et_page5_school;
    EditText et_page5_birth;
    EditText et_page5_email;

    CheckBox cb_utilization;
    CheckBox cb_collection;

    ImageView iv_page_backward_picture;

    View dv_page1_divider;

    Animation intropage_text1;
    Animation intropage_text2;
    Animation intropage_text3;
    Animation intropage_text4;

    Animation firstpage_card_in;
    Animation firstpage_icon_in;
    Animation firstpage_title1_in;
    Animation firstpage_title2_in;
    Animation firstpage_title3_in;
    Animation firstpage_contents3_in;
    Animation firstpage_divider_in;
    Animation firstpage_install_text1_in;
    Animation firstpage_install_text2_in;
    Animation firstpage_install_text3_in;
    Animation firstpage_title1_out;
    Animation firstpage_title2_out;
    Animation firstpage_title3_out;
    Animation firstpage_install_text_out;
    Animation firstpage_install_progress_fill;
    Animation firstpage_progress_in;
    Animation firstpage_progress_out;

    Animation secondpage_install_finish_in;
    Animation secondpage_install_finish_out;

    Animation thirdpage_text_in;
    Animation thirdpage_edittext_in;
    Animation thirdpage_btn_in;
    Animation thirdpage_text_out;
    Animation thirdpage_edittext_out;
    Animation thirdpage_btn_out;

    Animation install_card_first_expand;

    Animation fourthpage_title_in;
    Animation fourthpage_contents_in;
    Animation fourthpage_btn_in;
    Animation fourthpage_title_out;
    Animation fourthpage_contents_out;
    Animation fourthpage_btn_out;

    Animation install_card_first_move;
    Animation install_card_second_expand;
    Animation install_card_icon_first_move;

    Animation fifthpage_window_in;
    Animation fifthpage_btn_in;
    Animation fifthpage_window_out;
    Animation fifthpage_btn_out;

    Animation install_card_second_contract;
    Animation install_card_second_move;
    Animation install_card_icon_second_move;
    Animation install_card_third_expand;

    Animation sixthpage_input_finish_in;
    Animation sixthpage_title_in;
    Animation sixthpage_title_contract;
    Animation sixthpage_title_move;
    Animation sixthpage_contents_in;
    Animation sixthpage_btn_in;
    Animation sixthpage_out;

    static ProgressDialog mProgress;
    static boolean loadingFinished = true;
    AlertDialog.Builder alert;

    String students_year = "고1";

    protected static int check_id = 0;
    protected static int check_make_account = 0;
    Handler handler;

    private static SharedPreferences mPreference;

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    TextView tv_collection;
    TextView tv_utilization;

    String user_pass;
    String email;
    String name;
    String birth;
    String school;
    String phonenum;
    String device;
    String profileUrl = null;

    Activity thisActivity;

    InputMethodManager imm;

    int grade;

    int user_count;

    DBPool db;

    //	Tracker mTracker;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        //		VOCAconfig application = (VOCAconfig) getApplication();
        //		mTracker = application.getDefaultTracker();

        if (MainValue.isLowDensity(getApplicationContext())) {
            setContentView(R.layout.activity_install_small);
        } else {
            setContentView(R.layout.activity_install);
        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        db = DBPool.getInstance(getApplicationContext());

        handler = new Handler();
        thisActivity = this;

        ll_page_backward_bg = (LinearLayout) findViewById(R.id.ll_page_backward_bg);
        ll_page_backward_picture = (LinearLayout) findViewById(R.id.ll_page_backward_picture);
        ll_progress_guage = (View) findViewById(R.id.ll_progress_gauge);
        ll_progress_back = (View) findViewById(R.id.ll_progress_back);

        ll_page1 = (LinearLayout) findViewById(R.id.ll_page1);
        ll_page2 = (LinearLayout) findViewById(R.id.ll_page2);
        ll_page3 = (LinearLayout) findViewById(R.id.ll_page3);
        ll_page4 = (LinearLayout) findViewById(R.id.ll_page4);
        ll_page5 = (LinearLayout) findViewById(R.id.ll_page5);
        ll_page6 = (RelativeLayout) findViewById(R.id.ll_page6);
        ll_progress = (LinearLayout) findViewById(R.id.ll_progress);
        ll_progress_gauge = (LinearLayout) findViewById(R.id.ll_progress_gauge);
        ll_page5_signin = (LinearLayout) findViewById(R.id.ll_page5_signin);
        ll_info_input_finish = (LinearLayout) findViewById(R.id.ll_info_input_finish);

        tv_page1_title = (TextView) findViewById(R.id.tv_page1_title);
        tv_page1_contents = (TextView) findViewById(R.id.tv_page1_contents);
        tv_page1_ongoing = (TextView) findViewById(R.id.tv_page1_ongoing);
        iv_intropage_title = (ImageView) findViewById(R.id.iv_intropage_title);
        tv_intropage_contents = (TextView) findViewById(R.id.tv_intropage_contents);
        tv_page3_title = (TextView) findViewById(R.id.tv_page3_title);
        tv_page4_title = (TextView) findViewById(R.id.tv_page4_title);
        tv_page4_contents = (TextView) findViewById(R.id.tv_page4_contents);
        tv_page5_grade_input = (TextView) findViewById(R.id.tv_page5_grade_input);
        tv_collection = (TextView) findViewById(R.id.tv_collection);
        tv_utilization = (TextView) findViewById(R.id.tv_utilization);
        tv_page6_title = (TextView) findViewById(R.id.tv_page6_title);
        tv_page6_contents = (TextView) findViewById(R.id.tv_page6_contents);
        notice_data = (TextView) findViewById(R.id.notice_data);

        ll_intropage_not_use_cellular = (LinearLayout) findViewById(R.id.ll_intropage_not_use_cellular);
        tv_page2_finish = (TextView) findViewById(R.id.tv_page2_finish);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        bt_page3_confirm = (CircleAnimationView) findViewById(R.id.bt_page3_confirm);
        bt_page4_confirm = (CircleAnimationView) findViewById(R.id.bt_page4_confirm);
        bt_page5_signin = (CircleAnimationView) findViewById(R.id.bt_page5_signin);
        bt_page6_confirm = (CircleAnimationView) findViewById(R.id.bt_page6_confirm);
        bt_intropage_confirm = (CircleAnimationView) findViewById(R.id.bt_intropage_confirm);

        et_page3_systemname = (EditText) findViewById(R.id.et_page3_systemname);
        et_page5_name = (EditText) findViewById(R.id.et_page5_name);
        et_page5_school = (EditText) findViewById(R.id.et_page5_school);
        et_page5_school.setFocusable(false);

        et_page5_birth = (EditText) findViewById(R.id.et_page5_birth);
        et_page5_email = (EditText) findViewById(R.id.et_page5_email);

        cb_utilization = (CheckBox) findViewById(R.id.cb_utilization);
        cb_collection = (CheckBox) findViewById(R.id.cb_collection);

        iv_page_backward_picture = (ImageView) findViewById(R.id.iv_page_backward_picture);
        dv_page1_divider = (View) findViewById(R.id.dv_page1_divider);

        intropage_text1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_1_fade_out_anim);
        intropage_text2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_2_fade_out_anim);
        intropage_text3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_3_fade_out_anim);
        intropage_text4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_4_fade_out_anim);

        firstpage_card_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.word_card_in_anim);
        firstpage_icon_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_icon_in_anim);
        firstpage_title1_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        firstpage_title2_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        firstpage_title3_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        firstpage_divider_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_divider_in);
        firstpage_install_text1_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_text_flick);
        firstpage_install_text2_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_text_flick);
        firstpage_install_text3_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_text_flick);
        firstpage_title1_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        firstpage_title2_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        firstpage_title3_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        firstpage_install_text_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        firstpage_progress_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_progress_in);
        firstpage_progress_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_progress_out);
        firstpage_install_progress_fill = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_progress_fill);

        secondpage_install_finish_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_icon_in_anim);
        secondpage_install_finish_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

        thirdpage_text_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
        thirdpage_edittext_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
        thirdpage_btn_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);
        thirdpage_text_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_2_fade_out_anim);
        thirdpage_edittext_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_3_fade_out_anim);
        thirdpage_btn_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_4_fade_out_anim);

        install_card_first_expand = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_first_expand);

        fourthpage_title_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
        fourthpage_contents_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.set_goal_text_in);
        fourthpage_btn_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);
        fourthpage_title_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_2_fade_out_anim);
        fourthpage_contents_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_3_fade_out_anim);
        fourthpage_btn_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_4_fade_out_anim);

        install_card_second_expand = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_second_expand);
        install_card_first_move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_first_move);
        install_card_icon_first_move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_icon_first_move);

        fifthpage_window_in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        fifthpage_btn_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);
        fifthpage_window_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        fifthpage_btn_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

        install_card_second_contract = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_second_contract);
        install_card_icon_second_move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_icon_second_move);
        install_card_third_expand = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_third_expand);
        install_card_second_move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_second_move);

        sixthpage_input_finish_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_info_input_text_in);
        sixthpage_title_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_grade_text_in);
        sixthpage_title_contract = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_grade_text_contract);
        sixthpage_title_move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_grade_text_move);
        sixthpage_contents_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.install_card_grade_bottom_text_in);
        sixthpage_btn_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.btn_in);
        sixthpage_out = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);

        tv_page5_grade_input.setOnClickListener(this);
        cb_utilization.setOnClickListener(this);
        cb_collection.setOnClickListener(this);
        tv_utilization.setOnClickListener(this);
        tv_collection.setOnClickListener(this);
        et_page5_school.setOnClickListener(this);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(WillTutoActivity.spTopx(getApplicationContext(), 12));
        paint.setAntiAlias(true);
        paint.setAlpha(222);
        paint.setTextAlign(Paint.Align.CENTER);

        Paint paint_install_start = new Paint();
        paint_install_start.setStyle(Paint.Style.FILL);
        paint_install_start.setColor(Color.WHITE);
        paint_install_start.setTextSize(WillTutoActivity.spTopx(getApplicationContext(), 14));
        paint_install_start.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint_install_start.setAntiAlias(true);
        paint_install_start.setTextAlign(Paint.Align.CENTER);

        bt_page3_confirm.setText("시스템 이름 입력");
        bt_page4_confirm.setText("맞춤 정보 입력하기");
        bt_page5_signin.setText("약관 동의 및 입력완료");
        bt_page6_confirm.setText("시작하기");
        bt_intropage_confirm.setText("시스템 인스톨 시작");

        bt_page3_confirm.setColor(0xff, 0xff, 0xff);
        bt_page4_confirm.setColor(0xff, 0xff, 0xff);
        bt_page5_signin.setColor(0xff, 0xff, 0xff);
        bt_page6_confirm.setColor(0xff, 0xff, 0xff);
        bt_intropage_confirm.setColor(0xff, 0xff, 0xff);

        try {

        } catch (IllegalStateException e) {
            // TODO: handle exception

        }
        readProfile(); // 테스트위해 임시로 주석처리 20150109 양대현

        bt_page3_confirm.setAnimButton(paint_install_start, R.drawable.qwest_choice_btn, new CircleAnimationView.ClickCallbacks() {
            @Override
            public void onClick() {
                // TODO Auto-generated method stub
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bt_page3_confirm.stopAnimation();

                        Log.d("page3", et_page3_systemname.length() + "");
                        if (et_page3_systemname.length() == 0) {
                            setMySharedPreferences(MainValue.GpreSystemName, et_page3_systemname.getHint().toString());
                        } else if (et_page3_systemname.getText().length() < 2 || et_page3_systemname.getText().length() > 8) {
                            Toast.makeText(InstallActivity.this, "시스템 이름은 2~8 글자만 가능합니다.", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            setMySharedPreferences(MainValue.GpreSystemName, et_page3_systemname.getText().toString());
                        }

                        imm.hideSoftInputFromWindow(et_page3_systemname.getWindowToken(), 0);

                        tv_page3_title.startAnimation(thirdpage_text_out);
                        et_page3_systemname.startAnimation(thirdpage_edittext_out);
                        bt_page3_confirm.startAnimation(thirdpage_btn_out);
                        Log.d("BGSIZE", "first_expand_before" + ll_page_backward_bg.getHeight());
                        ll_page_backward_bg.startAnimation(install_card_first_expand);
                        Log.d("BGSIZE", "first_expand_after" + ll_page_backward_bg.getHeight());

                        tv_page4_title.setText("안녕하세요. " + mPreference.getString(MainValue.GpreName, "이름없음") + "님!\n"
                                + mPreference.getString(MainValue.GpreSystemName, "안알랴줌") + " 입니다!");
                    }
                });
            }
        });
        bt_page4_confirm.setAnimButton(paint_install_start, R.drawable.qwest_choice_btn,
                new CircleAnimationView.ClickCallbacks() {
                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bt_page4_confirm.stopAnimation();

                                tv_page4_title.startAnimation(fourthpage_title_out);
                                tv_page4_contents.startAnimation(fourthpage_contents_out);
                                bt_page4_confirm.startAnimation(fourthpage_btn_out);

                                Log.d("BGSIZE", "first_move_before" + ll_page_backward_bg.getHeight());

                                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_page_backward_bg.getLayoutParams();
                                params.height = HitTutoActivity.getDpToPixel(getApplicationContext(), 320);
                                ll_page_backward_bg.setLayoutParams(params);

                                ll_page_backward_bg.startAnimation(install_card_first_move);

                                Log.d("BGSIZE", "first_move_after" + ll_page_backward_bg.getHeight());
                                ll_page_backward_picture.startAnimation(install_card_icon_first_move);
                            }
                        });
                    }
                });

        bt_page5_signin.setAnimButton(paint_install_start, R.drawable.qwest_choice_btn,
                new CircleAnimationView.ClickCallbacks() {
                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bt_page5_signin.stopAnimation();

                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(et_page5_email.getWindowToken(), 0);

                                email = null;

                                email = et_page5_email.getText().toString();
                                phonenum = GetMyPhoneNumber();
                                device = GCMRegistrar.getRegistrationId(getApplicationContext());
                                name = et_page5_name.getText().toString();
                                birth = et_page5_birth.getText().toString();
                                school = et_page5_school.getText().toString();

                                try {

                                    grade = Integer.valueOf(tv_page5_grade_input.getText().toString());
                                } catch (NumberFormatException e) {
                                    // TODO: handle exception
                                    Toast.makeText(InstallActivity.this, "등급을 선택해 주세요 !", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Log.e("leveltest", "device  :  " + device);
                                if (device.equals("")) {
                                    // Registration is not present, register now with GCM
                                    GCMRegistrar.register(getApplicationContext(), CommonUtilities.SENDER_ID);
                                    device = GCMRegistrar.getRegistrationId(getApplicationContext());
                                    Log.e("leveltest", "device null");
                                }

                                Log.e("leveltest", "after   device  :  " + device);

                                if (name.length() < 2 || name.length() > 15) {
                                    Toast.makeText(InstallActivity.this, "이름은 2~15자 까지만 가능합니다", Toast.LENGTH_LONG).show();
                                    return;

                                }

                                if (school_id == 0) {
                                    Toast.makeText(InstallActivity.this, "학교를 입력해주세요.", Toast.LENGTH_LONG).show();
                                    return;

                                }
                                Pattern pattern = Pattern.compile(EMAIL_PATTERN);

                                if (!pattern.matcher(email).matches()) {
                                    Toast.makeText(InstallActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                    return;
                                } else if (email.length() > 40 || email.length() < 8) {
                                    Toast.makeText(InstallActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (birth.length() != 6) {
                                    Toast.makeText(InstallActivity.this, "생년월일은 년월일(ex. 880502)", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!(cb_collection.isChecked() && cb_utilization.isChecked())) {
                                    Toast.makeText(InstallActivity.this, "약관에 동의하지 않으셨습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                mProgress = ProgressDialog.show(InstallActivity.this, "Loading", "Please wait for a moment...");

                                if (name != null && birth != null && email != null) {
                                    List<String> data = new ArrayList<String>();

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

                                    if (birth.subSequence(2, 4).equals("01") || birth.subSequence(2, 4).equals("02")) {
                                        sum--;
                                    }

                                    int level = 0;
                                    int age = Integer.valueOf(TimeClass.getYear()) - sum;
                                    Log.e("age1", "나이는 :  " + age);

                                    if (age <= 16) {// && age > 13) {
                                        students_year = "중학생";
                                        tv_page6_title.setText(R.string.install_middle_title);
                                        tv_page6_contents.setText(R.string.install_middle_contents);
                                        level = 1;
                                    } else if (age == 17) {
                                        students_year = "고1";
                                        tv_page6_title.setText(R.string.install_high_1_title);
                                        tv_page6_contents.setText(R.string.install_high_1_contents);
                                        level = 1;
                                    } else if (age == 18) {
                                        students_year = "고2";
                                        tv_page6_title.setText(R.string.install_high_2_title);
                                        tv_page6_contents.setText(R.string.install_high_2_contents);
                                        level = 1;
                                    } else if (age == 19) {
                                        students_year = "고3";
                                        tv_page6_title.setText(R.string.install_high_3_title);
                                        tv_page6_contents.setText(R.string.install_high_3_contents);
                                        level = 1;
                                    } else if (age > 19) {
                                        students_year = "재수생";
                                        tv_page6_title.setText(R.string.install_n_title);
                                        tv_page6_contents.setText(R.string.install_n_contents);
                                        level = 0;
                                    } else {
                                        // 따로 멘트를 뽑아달라고해야함
                                    }

                                    Log.e("leveltest", device);
                                    data.add(db.getLoginId());
                                    data.add(Get_my_uuid.get_Device_id(getApplicationContext()));
                                    data.add(phonenum);
                                    data.add(device);
                                    data.add(name);
                                    data.add(birth);
                                    data.add(email);
                                    data.add("" + school_id);
                                    data.add(String.valueOf(grade));
                                    data.add("1");

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

                                    data.add(contacts);
                                    data.add(profileUrl);

                                    mPreference.edit().putString(MainValue.GpreBirth, birth);
                                    mPreference.edit().putString(MainValue.GpreSchoolId, "" + school_id);
                                    mPreference.edit().putString(MainValue.GpreSchoolName, school_name);
                                    mPreference.edit().putString(MainValue.GpreEmail, email);
                                    mPreference.edit().putString(MainValue.GpreProfileImage, profileUrl);
                                    mPreference.edit().putInt(MainValue.GpreSatGrade, grade);

                                    MakeAccount(data);

                                }
                            }
                        });
                    }
                });
        bt_page6_confirm.setAnimButton(paint_install_start, R.drawable.qwest_choice_btn,
                new CircleAnimationView.ClickCallbacks() {
                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bt_page6_confirm.stopAnimation();
                                //Intent intent3 = new Intent(InstallActivity.this, MainActivity.class);
                                Intent intent3 = new Intent(InstallActivity.this, InstallLastActivity.class);
                                overridePendingTransition(R.anim.tutorial_activity_in, R.anim.install_activity_out);
                                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent3);
                                finish();
                            }
                        });
                    }
                });
        bt_intropage_confirm.setAnimButton(paint_install_start, R.drawable.qwest_choice_btn,
                new CircleAnimationView.ClickCallbacks() {
                    @Override
                    public void onClick() {
                        // TODO Auto-generated method stub
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                bt_intropage_confirm.stopAnimation();
                                iv_intropage_title
                                        .startAnimation(intropage_text1);
                                tv_intropage_contents
                                        .startAnimation(intropage_text2);
                                bt_intropage_confirm
                                        .startAnimation(intropage_text3);
                                ll_intropage_not_use_cellular
                                        .startAnimation(intropage_text4);
                            }
                        });
                    }
                });

        sixthpage_contents_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page6_confirm.setVisibility(View.VISIBLE);
                bt_page6_confirm.startAnimation(sixthpage_btn_in);
                //
                // RelativeLayout.LayoutParams iconParams = (LayoutParams)
                // ll_page_backward_picture.getLayoutParams();
                // iconParams.topMargin =
                // HitTutoActivity.getDpToPixel(getApplicationContext(), 124);
                // ll_page_backward_picture.setLayoutParams(iconParams);
                //
                //
                // RelativeLayout.LayoutParams params = (LayoutParams)
                // ll_page_backward_bg.getLayoutParams();
                // params.height =
                // HitTutoActivity.getDpToPixel(getApplicationContext(), 254);
                // params.topMargin =
                // HitTutoActivity.getDpToPixel(getApplicationContext(), 156);
                // ll_page_backward_bg.setLayoutParams(params);
            }
        });

        sixthpage_title_contract.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // tv_page6_title.setTextSize(tv_page6_title.getTextSize()*0.6f);
                // RelativeLayout.LayoutParams iconParams =
                // (RelativeLayout.LayoutParams)
                // tv_page6_title.getLayoutParams();
                // iconParams.topMargin =
                // HitTutoActivity.getDpToPixel(getApplicationContext(), 74);
                // tv_page6_title.setLayoutParams(iconParams);
                tv_page6_contents.setVisibility(View.VISIBLE);
                tv_page6_contents.startAnimation(sixthpage_contents_in);
            }
        });

        sixthpage_title_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page6_title.setAnimation(sixthpage_title_contract);
            }
        });

        sixthpage_input_finish_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        install_card_icon_second_move
                .setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        ll_info_input_finish.setVisibility(View.VISIBLE);
                        ll_info_input_finish
                                .startAnimation(sixthpage_input_finish_in);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        // RelativeLayout.LayoutParams iconParams =
                        // (LayoutParams)
                        // ll_page_backward_picture.getLayoutParams();
                        // iconParams.topMargin =
                        // HitTutoActivity.getDpToPixel(getApplicationContext(),
                        // 124);
                        // ll_page_backward_picture.setLayoutParams(iconParams);
                    }
                });

        install_card_third_expand.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_page6.setVisibility(View.VISIBLE);
                //				tv_page6_title.setText(students_year+" 이시네요!");
                tv_page6_title.setVisibility(View.VISIBLE);
                tv_page6_title.startAnimation(sixthpage_title_in);
            }
        });

        install_card_second_move.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_page_backward_bg.getLayoutParams();
                params.topMargin = HitTutoActivity.getDpToPixel(getApplicationContext(), 104);
                ll_page_backward_bg.setLayoutParams(params);
                ll_page_backward_bg.startAnimation(install_card_third_expand);
            }
        });

        install_card_second_contract.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_page_backward_bg.getLayoutParams();
                params.height = HitTutoActivity.getDpToPixel(getApplicationContext(), 110);
                ll_page_backward_bg.setLayoutParams(params);
                ll_page_backward_bg.startAnimation(install_card_second_move);
            }
        });

        fifthpage_btn_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page5_signin.setVisibility(View.GONE);
            }
        });

        fifthpage_window_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ll_page5_signin.setVisibility(View.GONE);
                ll_page5.setVisibility(View.GONE);
            }
        });

        fifthpage_btn_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imm.showSoftInput(et_page5_name, 0);
            }
        });

        fifthpage_window_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page5_signin.setVisibility(View.VISIBLE);
                fifthpage_btn_in.setDuration(292);
                fifthpage_btn_in.setStartOffset(292);
                bt_page5_signin.startAnimation(fifthpage_btn_in);
            }
        });

        install_card_second_expand
                .setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_page5.setVisibility(View.VISIBLE);

                        ll_page5_signin.setVisibility(View.VISIBLE);
                        fifthpage_window_in.setDuration(292);
                        fifthpage_window_in.setStartOffset(292);
                        ll_page5_signin.startAnimation(fifthpage_window_in);
                    }
                });

        install_card_first_move.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_page_backward_bg
                        .getLayoutParams();
                params.topMargin = HitTutoActivity.getDpToPixel(
                        getApplicationContext(), 56);
                ll_page_backward_bg.setLayoutParams(params);
                ll_page_backward_bg.startAnimation(install_card_second_expand);
            }
        });

        install_card_icon_first_move
                .setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }
                });

        fourthpage_title_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page4_title.setVisibility(View.GONE);
            }
        });
        fourthpage_contents_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page4_contents.setVisibility(View.GONE);
            }
        });
        fourthpage_btn_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page4_confirm.setVisibility(View.GONE);
            }
        });

        fourthpage_contents_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page4_confirm.setVisibility(View.VISIBLE);
                fourthpage_btn_in.setStartOffset(167);
                bt_page4_confirm.startAnimation(fourthpage_btn_in);
            }
        });

        thirdpage_text_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page3_title.setVisibility(View.GONE);
            }
        });

        thirdpage_edittext_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                et_page3_systemname.setVisibility(View.GONE);
            }
        });

        thirdpage_btn_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page3_confirm.setVisibility(View.GONE);
            }
        });

        install_card_first_expand.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                ll_page4.setVisibility(View.VISIBLE);

                fourthpage_title_in.setStartOffset(542);
                tv_page4_title.setVisibility(View.VISIBLE);
                tv_page4_title.startAnimation(fourthpage_title_in);

                tv_page4_contents.setVisibility(View.VISIBLE);
                fourthpage_contents_in.setStartOffset(542);
                tv_page4_contents.startAnimation(fourthpage_contents_in);
            }
        });

        thirdpage_edittext_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imm.showSoftInput(et_page3_systemname, 0);
            }
        });

        thirdpage_text_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_page3_confirm.setVisibility(View.VISIBLE);
                thirdpage_btn_in.setStartOffset(292);
                bt_page3_confirm.startAnimation(thirdpage_btn_in);
            }
        });

        secondpage_install_finish_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page2_finish.setVisibility(View.GONE);
            }
        });

        firstpage_progress_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_progress.setVisibility(View.GONE);
                ll_page3.setVisibility(View.VISIBLE);

                thirdpage_text_in.setStartOffset(375);
                tv_page3_title.setVisibility(View.VISIBLE);
                tv_page3_title.startAnimation(thirdpage_text_in);

                et_page3_systemname.setHint(tv_page1_contents.getText());
                et_page3_systemname.setSelection(et_page3_systemname.length());
                et_page3_systemname.setVisibility(View.VISIBLE);
                thirdpage_edittext_in.setStartOffset(292);
                et_page3_systemname.startAnimation(thirdpage_edittext_in);

                notice_data.setVisibility(View.GONE);
            }
        });
        secondpage_install_finish_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_progress.startAnimation(firstpage_progress_out);
                secondpage_install_finish_out.setDuration(125);
                secondpage_install_finish_out.setStartOffset(833);
                tv_page2_finish.startAnimation(secondpage_install_finish_out);
            }
        });

        final Handler progressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                tv_progress.setText(msg.what + "%");
            }
        };

        firstpage_install_progress_fill.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Thread thread = new Thread(new Runnable() {
                    int rate = 0;

                    @Override
                    public void run() {
//						updateDBtbl();
                        while (true) {
                            progressHandler.sendEmptyMessage(rate);
                            if (rate == 100)
                                break;
                            rate++;
                            SystemClock.sleep(122L);
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page2_finish.setVisibility(View.VISIBLE);
                tv_page2_finish.startAnimation(secondpage_install_finish_in);
            }
        });

        firstpage_title3_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_title.setVisibility(View.GONE);
                tv_page1_contents.setVisibility(View.GONE);
                dv_page1_divider.setVisibility(View.GONE);
                ll_page2.setVisibility(View.VISIBLE);
            }
        });

        firstpage_install_text3_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                firstpage_title3_out.setDuration(167);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_page1_title.startAnimation(firstpage_title3_out);
                        tv_page1_contents.startAnimation(firstpage_title3_out);
                        dv_page1_divider.startAnimation(firstpage_title3_out);
                    }
                }, 3750);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_ongoing.setVisibility(View.GONE);
            }
        });

        firstpage_title2_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_title.setText("SYSTEM ID");
                setID();

                firstpage_title2_in.setDuration(167);
                tv_page1_title.startAnimation(firstpage_title2_in);
                //tv_page1_contents.setTextColor(Color.rgb(0x2d, 0x70, 0x9e));
                tv_page1_contents.startAnimation(firstpage_title2_in);
            }
        });

        firstpage_install_text2_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                firstpage_title2_out.setDuration(167);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_page1_title.startAnimation(firstpage_title2_out);
                        tv_page1_contents.startAnimation(firstpage_title2_out);
                    }
                }, 3750);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_ongoing.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_page1_ongoing.setVisibility(View.VISIBLE);
                        tv_page1_ongoing.setText("시스템 고유아이디가 생성됩니다.");
                        tv_page1_ongoing.startAnimation(firstpage_install_text3_in);
                    }
                }, 229);
            }
        });

        firstpage_title1_out.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_title.setText("사용자 순서");
                setNumber();

                firstpage_title2_in.setDuration(167);
                tv_page1_title.startAnimation(firstpage_title2_in);
                //tv_page1_contents.setTextColor(Color.rgb(0xe1, 0xab, 0x24));
                tv_page1_contents.startAnimation(firstpage_title2_in);
            }
        });

        firstpage_install_text1_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ll_progress_gauge.setVisibility(View.VISIBLE);
                ll_progress_gauge.setAnimation(firstpage_install_progress_fill);

                firstpage_title1_out.setDuration(167);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_page1_title.startAnimation(firstpage_title1_out);
                        tv_page1_contents.startAnimation(firstpage_title1_out);
                    }
                }, 3750);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_ongoing.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_page1_ongoing.setText("인공지능을 부여중입니다.");
                        tv_page1_ongoing.setVisibility(View.VISIBLE);
                        tv_page1_ongoing.startAnimation(firstpage_install_text2_in);
                    }
                }, 229);
            }
        });

        firstpage_title1_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_ongoing.setVisibility(View.VISIBLE);
                tv_page1_ongoing.startAnimation(firstpage_install_text1_in);
            }
        });

        firstpage_progress_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_page1_title.setVisibility(View.VISIBLE);
                //tv_page1_contents.setTextColor(Color.rgb(0xeb, 0x4f, 0x38));
                tv_page1_contents.setVisibility(View.VISIBLE);
                firstpage_title1_in.setDuration(167);
                firstpage_title1_in.setStartOffset(167);
                tv_page1_title.startAnimation(firstpage_title1_in);
                tv_page1_contents.startAnimation(firstpage_title1_in);
            }
        });

        firstpage_card_in.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dv_page1_divider.setVisibility(View.VISIBLE);
                dv_page1_divider.startAnimation(firstpage_divider_in);

                ll_progress.setVisibility(View.VISIBLE);
                ll_progress.startAnimation(firstpage_progress_in);

                notice_data.setVisibility(View.VISIBLE);
            }
        });

        intropage_text4.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_intropage_not_use_cellular.setVisibility(View.GONE);
            }
        });
        intropage_text3.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bt_intropage_confirm.setVisibility(View.GONE);
            }
        });
        intropage_text2.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_intropage_contents.setVisibility(View.GONE);
            }
        });
        intropage_text1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_intropage_title.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll_page_backward_picture.setVisibility(View.VISIBLE);
                        ll_page_backward_picture
                                .startAnimation(firstpage_icon_in);
                    }
                }, 458);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ll_page_backward_bg.setVisibility(View.VISIBLE);
                        ll_page_backward_bg.startAnimation(firstpage_card_in);
                    }
                }, 417);

                ll_page1.setVisibility(View.VISIBLE);
            }
        });

        mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);

        SpannableStringBuilder sb = new SpannableStringBuilder();
        String str = "이제 \'" + mPreference.getString(MainValue.GpreName, "이름없음") + "\'님 만의\n암기 시스템을\n설치하겠습니다.";
        sb.append(str);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 3, 5 + mPreference.getString(MainValue.GpreName, "이름없음").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_intropage_contents.setText(sb);

        tv_page4_contents.setText("시스템을 " + mPreference.getString(MainValue.GpreName, "이름없음") + "님께 맞추기 위해서\n몇가지 정보가 필요합니다.\n입력해 주실거죠?");
        tv_page3_title.setText("\'" + mPreference.getString(MainValue.GpreName, "이름없음") + "\'" + "님 만의\n시스템 이름을 입력해 주세요!");

        new RetrofitService().getUseAppInfoService().retroGetUseCount()
                .enqueue(new Callback<UseAppInfoData>() {
                    @Override
                    public void onResponse(Response<UseAppInfoData> response, Retrofit retrofit) {
                        user_count = response.body().getUser_count();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        user_count = 6452;
                        Log.e(TAG, "onFailure : " + t.toString());
                    }
                });

    }

    // mPreference = getSharedPreferences(MainActivitys.preName,
    // MODE_WORLD_READABLE || MODE_WORLD_WRITEABLE);
    // tv_intropage_contents.setText(mPreference.getString(MainValue.preName,

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //		mTracker.setScreenName("InstallActivity");
        //		mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        try {
            Class service = Class.forName("com.ihateflyingbugs.hsmd.service.ChatHeadService");
            stopService(new Intent(this, service));
        } catch (ClassNotFoundException e) {
        }


    }

    int progress = 0;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.tv_utilization:
                Intent intent = new Intent(InstallActivity.this, DocPopup.class);
                intent.putExtra("title", DocPopup.REQUEST_POPUP_UTIL);
                startActivityForResult(intent, 2580);
                break;
            case R.id.tv_collection:
                Intent intent2 = new Intent(InstallActivity.this, DocPopup.class);
                intent2.putExtra("title", DocPopup.REQUEST_POPUP_COLLECT);
                startActivityForResult(intent2, 3690);
                break;
            case R.id.bt_page3_confirm:
                Log.d("page3", et_page3_systemname.length() + "");
                if (et_page3_systemname.length() == 0) {
                    setMySharedPreferences(MainValue.GpreSystemName, et_page3_systemname.getHint().toString());
                } else if (et_page3_systemname.getText().length() < 2 || et_page3_systemname.getText().length() > 8) {
                    Toast.makeText(InstallActivity.this, "시스템 이름은 2~8자 까지만 가능합니다", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    setMySharedPreferences(MainValue.GpreSystemName, et_page3_systemname.getText().toString());
                }

                ll_progress.setVisibility(View.GONE);
                ll_page3.setVisibility(View.GONE);
                ll_page4.setVisibility(View.VISIBLE);

                tv_page4_contents.setText("시스템을 " + mPreference.getString(MainValue.GpreName, "이름없음") + "님께 맞추기 위해서\n몇가지 정보가 필요합니다.\n입력해주실거죠?");

                tv_page4_title.setText("안녕하세요. " + mPreference.getString(MainValue.GpreName, "이름없음") + "님!"
                        + mPreference.getString(MainValue.GpreSystemName, "ms949") + "입니다!");
                break;

            case R.id.bt_page4_confirm:

                ll_page4.setVisibility(View.GONE);
                ll_page5.setVisibility(View.VISIBLE);

                break;

            case R.id.tv_page5_grade_input:
                show();
                break;


            case R.id.bt_page6_confirm:
                break;
            case R.id.et_page5_school:
                startActivityForResult(new Intent(InstallActivity.this, SearchSchoolActivity.class), 6666);
                break;

            default:
                break;
        }

    }

    public void show() {

        final Dialog d = new Dialog(InstallActivity.this);
        d.setTitle("등급을 선택해 주세요");
        d.setContentView(R.layout.numberpickerdialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(9); // max value 100
        np.setMinValue(1); // min value 0
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
                Log.i("value is", "" + newVal);
            }
        });
        b1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_page5_grade_input.setText(String.valueOf(np.getValue())); // set
                // the
                // value
                // to
                // textview
                grade = Integer.valueOf(np.getValue());
                d.dismiss();
            }
        });
        b2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss the dialog
            }
        });
        d.show();

    }

    Bitmap bitmap;
    static String User_id = null;

    private String thumbnailURL;

    public void readProfile() {

        UserManagement.requestMe(new MeResponseCallback() {

            @Override
            public void onSuccess(UserProfile userProfile) {
                // TODO Auto-generated method stub
                if (userProfile != null)
                    userProfile.saveUserToCache();
                Log.e("kakao_info", "userid  =  " + userProfile.getId());
                User_id = String.valueOf(userProfile.getId());
                Config.USER_ID = User_id;
                db.insertLoginId(User_id);

                final String nickName = userProfile.getNickname();
                final String profileImageURL = userProfile.getProfileImagePath();
                final String thumbnailURL = userProfile.getThumbnailImagePath();

                profileUrl = thumbnailURL;

                // display

                setMySharedPreferences(MainValue.GpreName, nickName);
                setMySharedPreferences("image", thumbnailURL);
                readPicture();

            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                // TODO Auto-generated method stub

            }
        });
        // load image
    }

    public void readPicture() {
        thumbnailURL = getMySharedPreferences("image");
        Thread threadi = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                FileOutputStream out = null;
                try {
                    URL url = new URL(thumbnailURL);
                    URLConnection conn = url.openConnection();
                    conn.connect();// url?뿰寃?
                    BufferedInputStream bis = new BufferedInputStream(
                            conn.getInputStream()); // ?젒?냽?븳
                    // url濡쒕??꽣
                    // ?뜲?씠?꽣媛믪쓣
                    // ?뼸?뼱?삩?떎
                    Bitmap bm = BitmapFactory.decodeStream(bis);// ?뼸?뼱?삩 ?뜲?씠?꽣
                    // Bitmap ?뿉???옣

                    String path = Config.DB_FILE_DIR;
                    File file = new File(path);
                    if (!file.exists() && !file.mkdirs())
                        file.mkdir();
                    file = new File(path, Config.PROFILE_NAME);
                    out = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                    bis.close();// ?궗?슜?쓣 ?떎?븳 BufferedInputStream 醫낅즺
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    try {
                        out.close();
                        getPicture();
                    } catch (Throwable ignore) {
                    }
                }
            }
        });
        threadi.start();

    }

    public void getPicture() {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                String path = Config.DB_FILE_DIR;

                File directory = new File(path);

                File file = new File(directory, Config.PROFILE_NAME); // or any
                // other
                // format
                // supported

                FileInputStream streamIn;

                try {
                    streamIn = new FileInputStream(file);
                    bitmap = BitmapFactory.decodeStream(streamIn); // This gets
                    // the image
                    streamIn.close();
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    bitmap = round(bitmap);
                }

                // url = new URL(thumbnailURL);
                // URLConnection conn = url.openConnection();
                // conn.connect();// url?뿰寃?
                // bis = new BufferedInputStream(
                // conn.getInputStream()); // ?젒?냽?븳 url濡쒕??꽣 ?뜲?씠?꽣媛믪쓣 ?뼸?뼱?삩?떎
                // bm = BitmapFactory.decodeStream(bis);// ?뼸?뼱?삩 ?뜲?씠?꽣 Bitmap
                // ?뿉???옣
                // bm = roundBitmap(bm);
                // bis.close();// ?궗?슜?쓣 ?떎?븳 BufferedInputStream 醫낅즺

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!bitmap.equals(null)) {
                                iv_page_backward_picture.setImageBitmap(bitmap);
                            }
                        } catch (NullPointerException e) {
                            // TODO: handle exception
                            iv_page_backward_picture
                                    .setImageResource(R.drawable.profile);
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private Bitmap round(Bitmap source) {
        // TODO Auto-generated method stub
        Bitmap output = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, source.getWidth(),
                source.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(source.getWidth() / 2,
                source.getHeight() / 2, source.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        return output;
    }


    private String GetMyPhoneNumber() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String phoneNum = manager.getLine1Number();
        if (phoneNum == null) {
            return "";
        }
        try {
            if (phoneNum.subSequence(0, 3).equals("+82")) {
                phoneNum = phoneNum.replace("+82", "0");
            }
        } catch (StringIndexOutOfBoundsException e) {
            // TODO: handle exception
            phoneNum = "01000000000";
        }
        return phoneNum;
    }

    public static void onPageFinished() {
        if (loadingFinished) {
            if (null != mProgress) {
                if (mProgress.isShowing()) {
                    mProgress.dismiss();
                }
            }

        }
    }

    int age_text = 1;
    String student_id;

    protected void MakeAccount(List<String> data) {
        // TODO Auto-generated method stub
        String login_id = data.get(0);
        String student_devicenum=data.get(1);
        String student_phonenum=data.get(2);
        String student_gcm=data.get(3);
        String student_name = data.get(4);
        String student_birth=data.get(5);
        String student_email = data.get(6);
        String student_school_id=data.get(7);
        String student_grade = data.get(8);
        String student_gender=data.get(9);
        String student_contacts=data.get(10).toString();
        String student_profileUrl = data.get(11);

        new RetrofitService().getAuthorizationService().retroRegistAccount(""+0,
                                                                            login_id,
                                                                            student_name,
                                                                            student_birth,
                                                                            student_school_id,
                                                                            student_gender,
                                                                            student_phonenum,
                                                                            student_gcm,
                                                                            student_devicenum,
                                                                            student_email,
                                                                            student_grade,
                                                                            student_contacts,
                                                                            student_profileUrl )
                .enqueue(new Callback<AuthorizationData>() {
                    @Override
                    public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
                        int state = 0;
                        try {
                            state = response.body().getResult();
                            student_id = response.body().getStudent_id();
                        } catch (Exception e) {
                            Log.e("webtest", "Resonponse : MAKE_EXCEPTION" + e.toString());
                            onPageFinished();
                            FlurryAgent
                                    .logEvent("JoinActivity:JoinFail_NoServerResponse");
                            Handler mHandler = new Handler(Looper.getMainLooper());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 내용
                                    Toast.makeText(InstallActivity.this, "잠시후에 다시 시도해 주세요.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }, 0);
                            return;
                        }

                        check_make_account = state;
                        onPageFinished();
                        switch (check_make_account) {
                            case AsyncResultType.RESULT_SUCCESS:
                                Log.e("webtest", "MAKE_SUCCESS");

                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {

                                        db.insertStudentID(student_id);
                                        setMySharedPreferences(MainValue.GpreEmail, email);
                                        setMySharedPreferences(MainValue.GpreBirth, birth);
                                        setMySharedPreferences(MainValue.GpreSchoolId, school_name);
                                        setMySharedPreferences(MainValue.GpreSchoolName, "" + school_id);
                                        setMySharedPreferences(MainValue.GpreName, name);
                                        mPreference.edit().putInt(MainValue.GpreSatGrade, grade).commit();
                                        mPreference.edit().putString(MainValue.GpreUserYears, students_year).commit();

                                        Log.e("age1", "레벨은 " + students_year);
                                        Toast.makeText(thisActivity, "등록해 주셔서 감사합니다.", Toast.LENGTH_SHORT).show();

							/*
                             * ?뒪?뀦 6
							 */
                                        ll_page5.setVisibility(View.GONE);
                                        ll_page6.setVisibility(View.VISIBLE);

                                        fifthpage_window_out.setDuration(250);
                                        fifthpage_btn_out.setDuration(250);
                                        ll_page5_signin.startAnimation(fifthpage_window_out);
                                        bt_page5_signin.startAnimation(fifthpage_btn_out);

                                        install_card_icon_first_move.setFillAfter(false);
                                        install_card_first_expand.setFillAfter(false);

                                        Log.d("BGSIZE", "second_contract_before" + ll_page_backward_bg.getHeight());
                                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_page_backward_bg.getLayoutParams();
                                        params.height = HitTutoActivity.getDpToPixel(getApplicationContext(), 479);
                                        params.topMargin = HitTutoActivity.getDpToPixel(getApplicationContext(), 56);
                                        ll_page_backward_bg.setLayoutParams(params);

                                        ll_page_backward_bg.startAnimation(install_card_second_contract);
                                        Log.d("BGSIZE", "second_contract_after" + ll_page_backward_bg.getHeight());

                                        RelativeLayout.LayoutParams iconParams = (RelativeLayout.LayoutParams) ll_page_backward_picture.getLayoutParams();
                                        iconParams.topMargin = HitTutoActivity.getDpToPixel(getApplicationContext(), 24);
                                        ll_page_backward_picture.setLayoutParams(iconParams);

                                        ll_page_backward_picture.startAnimation(install_card_icon_second_move);
                                    }
                                });
                                break;

                            case AsyncResultType.RESULT_FAIL:
                                Log.e("webtest", "MAKE_FAIL_NOT_EXIST");
                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        FlurryAgent
                                                .logEvent("JoinActivity:JoinFail_Unmatched");
                                        // Toast.makeText(thisActivity,
                                        // "?씪移섑븯吏? ?븡?뒿?땲?떎 .. ? 萸먭? ",
                                        // Toast.LENGTH_SHORT).show();

                                    }
                                });
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // TODO Auto-generated method stub
                        Log.e("webtest", "MAKE_EXCEPTION " + t.toString());
                        onPageFinished();
                        FlurryAgent
                                .logEvent("JoinActivity:JoinFail_NoServerResponse");
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 내용
                                Toast.makeText(InstallActivity.this, "잠시후에 다시 시도해 주세요.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                    }
                });
    }

    private void setMySharedPreferences(String _key, String _value) {
        if (mPreference == null) {
            mPreference = getApplicationContext().getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        }
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(_key, _value);
        editor.commit();
    }

    private String getMySharedPreferences(String _key) {
        if (mPreference == null) {
            mPreference = getSharedPreferences(MainActivitys.preName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        }
        return mPreference.getString(_key, "");
    }

    String starttime;
    String startdate;
    Date date = new Date();
    Map<String, String> articleParams;

    public void onStart() {

        super.onStart();

        FlurryAgent.onStartSession(this, Config.setFlurryKey(getApplicationContext()));
        articleParams = new HashMap<String, String>();
        startdate = date.get_currentTime();
        starttime = String.valueOf(System.currentTimeMillis());
        FlurryAgent.logEvent("InstallActivity", articleParams);

        // your code
    }

    public void onStop() {
        super.onStop();
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            // Do whatever
            articleParams.put("WIFI", "On");
        } else {
            articleParams.put("WIFI", "Off");
        }

        FlurryAgent.endTimedEvent("InstallActivity");
        articleParams.put("Start", startdate);
        articleParams.put("End", date.get_currentTime());
        articleParams.put(
                "Duration",
                ""
                        + ((Long.valueOf(System.currentTimeMillis()) - Long
                        .valueOf(starttime))) / 1000);

        FlurryAgent.onEndSession(this);

        // your code

    }

    public String randomAlphabet() {
        String Digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int digit1 = (int) ((Math.random() * 100) % 26);
        int digit2 = (int) ((Math.random() * 100) % 26);

        return Digits.charAt(digit1) + "" + Digits.charAt(digit2);
    }

    public String randomDigit() {
        int digit = (int) ((Math.random() * 1000000) % 100000);

        String result = "";
        if (digit < 10) {
            result = "0000" + digit;
        } else if (digit < 100 && digit >= 10) {
            result = "000" + digit;
        } else if (digit < 1000 && digit >= 100) {
            result = "00" + digit;
        } else if (digit < 10000 && digit >= 1000) {
            result = "0" + digit;
        } else if (digit < 100000 && digit >= 10000) {
            result = "" + digit;
        }
        return result;
    }

    public void setNumber() {
        Handler handler = new Handler();
        for (int i = 0; i < 149; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_page1_contents.setText(randomDigit() + "번");
                }
            }, i * 10);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String result = null;
                if (user_count < 10) {
                    result = "0000" + user_count;
                } else if (user_count < 100 && user_count >= 10) {
                    result = "000" + user_count;
                } else if (user_count < 1000 && user_count >= 100) {
                    result = "00" + user_count;
                } else if (user_count < 10000 && user_count >= 1000) {
                    result = "0" + user_count;
                } else if (user_count < 100000 && user_count >= 10000) {
                    result = "" + user_count;
                }
                tv_page1_contents.setText(result + "번");
            }
        }, 1500);
    }

    public void setID() {
        Handler handler = new Handler();
        for (int i = 0; i < 150; i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_page1_contents.setText(randomAlphabet() + randomDigit());
                }
            }, i * 10);
        }

        //		et_page3_systemname.setText(tv_page1_contents.getText());
        //mPreference.getString(MainValue.GpreSystemName, "안알랴줌"));
    }

    boolean isUpdating = false;

    public void updateDBtbl() {
        if (isUpdating == false) {
            isUpdating = true;
            Log.e("word_update", "updateDBtbl");

            new RetrofitService().getWordUpdateService().retroUpdateWord("" + db.getDBversion())
                    .enqueue(new Callback<WordUpdateData>() {
                        @Override
                        public void onResponse(Response<WordUpdateData> response, Retrofit retrofit) {
                            if (response.body().getResult() == AsyncResultType.RESULT_FAIL) {

                                db.sendQuery(response.body());

                            } else if (response.body().getResult() == AsyncResultType.RESULT_SUCCESS) {


                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                            Log.e("word_update", "onFailure : " + t.toString());
                        }
                    });

        } else {

        }

    }


}