package com.ihateflyingbugs.hsmd.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.login.TimeClass;

public class Config {


	public static final String PREFS_NAME = "remember_voca";
	public static final String DB_NAME = "remember_voca.sqlite";
	public static final String Log_NAME = "remember_voca_log.txt";
	public static final String SLog_NAME = "DbService_log.txt";
	public static final String Server_Url = "http://lnslab.com/mildang/";
	public static final String Server_Payment_Url = "http://lnslab.com/payment/";

	// public static final String DB_NAME = "Remember_Temp.sqlite";
	public static final String DB_USER_NAME = "local.sqlite";
	public static final String PROFILE_NAME = "profile.png";
	public static final String SYSTEM_NAME = "system_name";
	public static final String KAKAO_PUSH = "kakao_push_flag";
	public static final String WILL_PUSH = "will_push_flag";
	public static final String MEMORY_PUSH = "memory_push_flag";
	public static final String HIT_PUSH = "hit_push_flag";
	public static final String CURRENT_LEVEL = "current_level";

	public static final String MAIN_STATE = "main_state";
	public static final String WILL_STATE = "will_state";
	public static final String MEMORY_STATE = "memory_state";
	public static final String HIT_STATE = "hit_state";

	public static final int TYPE_ID = 0;
	public static final int GOOGLE_PRODUCT_ID = 1;
	public static final int NOT_DC_PRICE = 2;
	public static final int PRICE = 3;
	public static final int PRODUCT_MEANS = 4;
	public static final int PRICE_CODE = 5;
	public static final int PEROID_MONTH = 6;
	public static final int IS_SUBSCRIBED = 7;

	public static final int INAPP = 0;
	public static final int WEB = 1;
	public static final int ACCOUNT = 2;
	public static final int LOCKSCREEN = 2;
	public static final int EXAMTEST= 4;
	public static final int OFFLINELESSON= 4;
	public static final int MAINWORDBOOK = 1;

	public static boolean isKakaoStory= false;



	public static double averageUseTimeAsAgeAndSatGrade = 0;

	public static final String base64EncodedPublicKey = 	"c12c99b3191e804e945fa729efa1ede84c18f5c7fb654f9"
			+ "00277f54ca2e6b828b5a36ce936457e9ea16e3000c7c456ab"
			+ "fc576f38ae4ffa37866b6c8f03b5f9a368af292f2b0e3718ac5"
			+ "cf24e682036f861ddbce4d0afe82713ad3f79cf3589f8878a4e"
			+ "6857823bc0b9c7cea7a87b4193792c9cd3f87f69da23f866778"
			+ "2c61a298c0b026b9d7f65da2316077340a0f3e21e0adaefc70f"
			+ "60702585b14629012a100292fab5217cde751ccdbcb419f51c7"
			+ "c9978763ac4caf6536248f621c22960a080e2f9931d5bd55928"
			+ "76b9fa92604d08c07fcb48050779c006ee4f7bb5e815f96e426"
			+ "00eee9c2020cb1d4d452dc38add4b6dc85a68fa3a3783eebdc3"
			+ "047b015ade854dde62573aaff3919aaec9ebd68cb769e16eec9"
			+ "fbbaac068593b21aaaed2baa126e7e9b966aca618eab0b71c0f"
			+ "b9251ef477ecfe95dff36c1c7ae2e2fa271a49d050b89da4626"
			+ "5332b938327e484d67ff17c19300e53814db05c527fde55d37f"
			+ "27f79efbf30f2e28d60624335f378e157700fedf229413d671c"
			+ "7e7b352d482a514ed0a25e915bbad8b8c2e064d8b";

	public static String USER_ID;
	public static String USER_PASS;
	public static String USER_PHONENUM = null;

	public static String REQUEST_ID;

	public static final String ExternalDirectory = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/Remember_voca/";

	public static final String DB_FILE_DIR = ExternalDirectory + "db/";

	// public static final String ExternalDirectory =
	// "/data/data/com.ihateflyingbugs.hsmd/";
	// public static final String DB_FILE_DIR = ExternalDirectory + "";

	public static final String DIFFICULTY = "DIFFICULTY";
	public static final String M_DATE = "M_DATE";

	public static final String STUDY_GOAL_TIME = "STUDY_GOAL_TIME";

	public static final int ONCE_WORD_COUNT = 12;
	public static int Difficulty;
	public static int WORD_TOPIC;
	public static int MAX_DIFFICULTY = 5;
	public static int MIN_DIFFICULTY = 1;

	public static final int Time_ONE_HOUR = 1;
	public static int CHANGE_LEVEL_COUNT;
	public static String VERSION;
	public static String SERVER_VERSION;

	public static int FOR_DUPLICATION_CHECK = 1;
	public static int FOR_REGISTER_CHECK = 2;

	// word set log
	public static int wordSetCount = 1;
	public static int unknw_to_knw = 0;
	public static int new_to_unknw_to_knw = 0;
	public static int new_to_knw = 0;
	public static int knw_to_unknw_to_knw = 0;
	public static int knw_to_knw = 0;
	public static int knw_to_unknw = 0;
	public static int new_to_unknw = 0;
	public static int unknw_to_unknw = 0;
	public static int new_to_new = 0;
	public static int know_count = 0;
	public static int unknow_count = 0;

	public static String NAME;

	public static String Word_chiper="";

	// will coach (announce)ment
	public static double averageUseTimeOfUser = -1; // average use time of user
	public static double averageUseTimeAsAge = -1; // average use time on same
	// age of user
	public static int theNumberOfPeople = -1; // the number of people whose age
	// is same as user
	public static int userRankAsUseTime = -1; // user rank of use time on same
	// age of user
	public static int goneUpRankAsUseTime = -1; // gone up rank when average use
	// time is increase as 2minutes
	public static double averageUseTimeAsAgeMonth = -1;
	public static double averageUseTimeAsAgeAndGrade = -1;
	public static double averageUseTimeAsAgeAndGradeMonth = -1;

	// word information concerning detailed question list
	public static double setting_rate;
	public static String word;
	public static int[] category;
	public static String[] cat1;
	public static String[] cat2;//
	public static String[] cat2_sub;
	public static int[] sum;

	public static String[][] google_product_id;

	public static int[][][] usable_purchase;
	public static String [][] usable_google_product_id;

	public static boolean checkNetwork(Context context, boolean isToastOn) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// Log.d("kja", "manager = " + manager + "   " +
		// ConnectivityManager.TYPE_MOBILE + "    " +
		// manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI));

		boolean isMobile;

		try {
			isMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isConnectedOrConnecting();
		} catch (Exception e) {
			isMobile = true;
		}

		boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();

		if (isMobile == false && isWifi == false) {
			// new AlertDialog.Builder(mContext)
			// .setTitle("경고")
			// .setMessage(mContext.getResources().getString(R.string.network_error))
			// .setNeutralButton("확인", new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dlg, int sumthin) {
			//
			// }
			// }).show();
			if (isToastOn)
				Toast.makeText(context, R.string.network_error,
						Toast.LENGTH_SHORT).show();

			return false;
		} else
			return true;
	}

	static public String setFlurryKey(Context context) {
		String USE_KEY = "";

		String USER_KEY = "T56ZS7QFY8ZN22YQ6MFW"; // betaTest
		String TEST_KEY = "RR7TSFKW8632R7BCZKKC"; // TestVoca

		if (USER_PHONENUM == null) {
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			String phoneNum = manager.getLine1Number();
			if (phoneNum == null) {
				return "010";
			}

			try {
				if (phoneNum.subSequence(0, 3).equals("+82")) {
					phoneNum = phoneNum.replace("+82", "0");
				}
			} catch (StringIndexOutOfBoundsException e) {
				// TODO: handle exception
				phoneNum = "01000000000";
			}
			USER_PHONENUM = phoneNum;
		}

		/*
		 * 배재호 팀장님 01032263632 최윤정 사원님 01029460430 김용훈 사원님 01044961311 박찬용 대표님
		 * 01071017935 강일구 개발자 01036654711 안광민 사원님 01085353819 짱짱걸 01072292908
		 * 안대현 01075771015 서강석 01029460430
		 */
		if (USER_PHONENUM.equals("01071950310")
				|| USER_PHONENUM.equals("01032263632")
				|| USER_PHONENUM.equals("01029460430")
				|| USER_PHONENUM.equals("01044961311")
				|| USER_PHONENUM.equals("01088255761")
				|| USER_PHONENUM.equals("01036654711")
				|| USER_PHONENUM.equals("01085353819")
				|| USER_PHONENUM.equals("01072292908")
				|| USER_PHONENUM.equals("01044961311")
				|| USER_PHONENUM.equals("01071017935")
				|| USER_PHONENUM.equals("01075771015")
				|| USER_PHONENUM.equals("01050275881")
				|| USER_PHONENUM.equals("01065662366")
				|| USER_PHONENUM.equals("01083357170")) {
			USE_KEY = TEST_KEY;
		} else {
			USE_KEY = USER_KEY;
		}

		return USE_KEY;

	}

	static public int getAge(String birth) {
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

		int level = 0;
		int age = Integer.valueOf(TimeClass.getYear()) - sum;

		return age;
	}

	public static boolean isNetworkAvailable(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		Log.e("leveltest",
				""
						+ (activeNetworkInfo != null && activeNetworkInfo
						.isConnected()));
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}



}