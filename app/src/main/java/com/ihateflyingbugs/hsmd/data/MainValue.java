package com.ihateflyingbugs.hsmd.data;

import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;

public class MainValue {

	public static SharedPreferences mPreference;
	public static final String preName = "remember_voca";

	public static String LockScreenOn="";
	
	public static String FpreToken = "V_UserToken";

	public static String GpreReviewFlag = "review_tutorial_flag";

	public static String GprePrecentageLv = "percentage_lv";


	public static final String GpreDate = "date";
	//public static final String GpreID = "v_id";
	public static final String GpreEmail = "v_email";
	public static final String GpreCul = "v_cul";
	public static final String GprePass = "v_pass";
	//public static final String GpreLevel = "v_level";
	public static final String GpreLevelCounting = "v_level_count";
	//public static final String GpreTutorial = "tutofinish";
	//public static final String GpreTutorial_Will = "tutowill";
	//public static final String GpreTutorial_Hit = "tutohit";
	//public static final String GpreTutorial_Memory = "tutomemory";
	public static final String GpreReviewTutorial = "reviewtutorial";
	public static final String GpreTime = "time";
	public static final String GpreFinishTime = "finishtime";
	public static final String GpreGCM = "gcm";
	public static final String GpreTopic = "topic";
	public static final String GpreGoalTime = "studygoaltime";
	
	public static final String GpreReviewPeriod = "reviewperiod";
	public static final String GpreContactLog = "contactlog";
	public static final String GpreLastLoginDate = "lastlogindate";

	public static final String GpreJoinPushFlag = "joinpushflag";
	public static final String GpreLast1PushFlag = "lastpush1flag";
	public static final String GpreLast2PushFlag = "lastpush2flag";
	

	public static final String GpreFreePush = "freepushflag";
	public static final String GpreFreePushDate = "freepushdateflag";
	

	public static final String GpreCheckCurve = "checkcurve";
	

	public static final String GpreSystemName = "systemname";


	public static final String GpreName = "name";
	public static final String GpreGender = "gender";
	public static final String GpreBirth = "birth";
	public static final String GpreAge = "age";
	
	public static final String GpreSchool = "school";
	public static final String GpreProfileImage = "profileimage";

	public static final String GpreKakaoPush = "kakaopush";
	public static final String GpreWillPush = "willpush";
	public static final String GpreHitPush = "hitpush";
	public static final String GpreMemoryPush = "memorypush";
	public static final String GpreBlogPush ="blogpush";

	public static final String GpreUseMin = "use_min";
	public static final String GpreUseSec = "use_sec";

	public static int GpreAccessDuration = 0;
	public static final String GpreEndTime = "endTime";
	public static final String GpreTotalReviewCnt = "totalReviewCnt"; // 占쎄쑴猿�癰귣벊�울옙占썲첎占쎈땾
	public static final String GpreTodayReviewCnt = "todayReviewCnt"; // 占썬끇��癰귣벊�울옙占썲첎占쎈땾
	public static final String GpreTodayLearnCnt = "todayLearnCnt"; // 占쎈뜄以�占쎈챷��占썩뫁堉�揶쏉옙��
	public static boolean GpreFlag = true;
	public static final String M_DATE = "mdate";

	public static final String GpreBackUpDB = "backupdb";
	public static final String GpreBackUpDate = "backupdate";
	public static final String GpreFirstStart = "firstInput";

	public static String pre_email;
	public static String pre_pass;
	public static String pre_level;

	public final static int Sign_requestCode = 1;
	final static int Fsign_requestCode = 2;

	public static UUID savedUuid;
	private static int check_login = 0;
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
	public static final String GpreUserYears = "useryears";
	public static final String GpreSatGrade = "satgrade";
	public static final String GpreUserGrade = "usergrade";
	
	public static final String GpreErrorSQLSend = "error_sql_send";
	
	static boolean flag_activity = false;

	static SharedPreferences settings;
	public static String GpreSchoolName = "schoolname";
	public static String GpreSchoolId = "schoolid";
	
	public static boolean isLowDensity(Context context){
	
		int density= context.getResources().getDisplayMetrics().densityDpi;

		boolean isLow = false;
		
		switch(density)
		{
		case DisplayMetrics.DENSITY_LOW:
		case DisplayMetrics.DENSITY_MEDIUM:
		case DisplayMetrics.DENSITY_HIGH:
			isLow = true;
			break;
		default:
			isLow = false;
			break;
		}
		return isLow;
	}

}