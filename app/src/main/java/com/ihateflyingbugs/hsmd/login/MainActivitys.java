package com.ihateflyingbugs.hsmd.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class MainActivitys  {

	public static SharedPreferences mPreference;
	public static final String preName = "remember_voca";

	public static String FpreToken = "V_UserToken";

	//public static final String GpreID	= "v_id";
	public static final String GpreEmail = "v_email";
	public static final String GpreCul = "v_cul";
	public static final String GprePass = "v_pass";
	public static final String GpreLevel = "v_level";
	public static final String GpreLevelCounting = "v_level_count";
	public static final String GpreTutorial = "tutorial";
	public static final String GpreReviewTutorial = "reviewtutorial";
	public static final String GpreTime = "time";
	public static final String GpreFinishTime = "finishtime";
	public static final String GpreGCM = "gcm";
	public static final String GpreTopic = "topic";
	public static final String GpreFirtst = "firtst";

	public static final String GpreName = "name";
	public static final String GpreGender = "gender";
	public static final String GpreBirth = "birth";
	public static final String GpreSchool = "school";

	public static int GpreAccessDuration=0;
	public static final String GpreEndTime = "endTime";
	public static final String GpreTotalReviewCnt = "totalReviewCnt";	//�꾩껜 蹂듭뒿��媛�닔
	public static final String GpreTodayReviewCnt = "todayReviewCnt";	//�ㅻ뒛 蹂듭뒿��媛�닔
	public static final String GpreTodayLearnCnt = "todayLearnCnt";		//�덈줈 �몄슫 �⑥뼱 媛�닔
	public static boolean GpreFlag=true;
	public static final String M_DATE = "mdate";

	public static String pre_email;
	public static String pre_pass;
	public static String pre_level;

	public final static int Sign_requestCode = 1;
	final static int Fsign_requestCode = 2;


	public static UUID savedUuid;
	private static int check_login =0;
	EditText UserEmail;
	EditText UserPassword;

	boolean namecard = false;
	Intent intent;

	static ProgressDialog mProgress;
	static boolean loadingFinished = true;
	AlertDialog.Builder alert;
	Button SignIn;
	private static Handler mhandler;

	private static Activity thisActivity;
	private static Context context;

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	static boolean flag_activity = false;

	static SharedPreferences settings;



}