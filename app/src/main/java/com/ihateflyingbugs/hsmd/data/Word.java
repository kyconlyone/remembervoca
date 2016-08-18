package com.ihateflyingbugs.hsmd.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.TTS_Util;
import com.ihateflyingbugs.hsmd.VOCAconfig;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Word implements Item {

	boolean isFlipAnimation = true;

	public final static int Class_N = 1;
	public final static int Class_V = 2;
	public final static int Class_A = 3;
	public final static int Class_Ad = 4;
	public final static int Class_Conj = 5;

	private int _id;
	private String word;

	public Timer timer;
	public TimerTask timer_task;
	public View view;
	public int index;


	private int p_class;
	private int difficulty;
	private int priority;

	private double score;
	private int state;
	private int time;
	private int frequency;

	private int known_flag;

	private boolean isRight = false;
	private boolean isWrong = false;


	private int wrongCount = 0;


	boolean isShow_wc = false;

	private List<Mean> mean_list;
	private HashMap<Integer, Boolean> total_class;

	private int exState;

	private double times;

	Context mContext;
	Activity mActivity;

	String reg_time;
	String local_time;
	int pre_state;
	int word_id;
	int student_id;
	int word_log_id;

	public int getPre_state() {
		return pre_state;
	}

	public void setPre_state(int pre_state) {
		this.pre_state = pre_state;
	}

	public int getWord_id() {
		return word_id;
	}

	public void setWord_id(int word_id) {
		this._id = word_id;
		this.word_id = word_id;
	}

	public int getStudent_id() {
		return student_id;
	}

	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}

	public int getWord_log_id() {
		return word_log_id;
	}

	public void setWord_log_id(int word_log_id) {
		this.word_log_id = word_log_id;
	}

	public String getLocal_time() {
		return local_time;
	}

	public void setLocal_time(String local_time) {
		this.local_time = local_time;
	}

	public String getReg_time() {
		return reg_time;
	}

	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}

	public Word(){

	}
	public Word(int _id, double score, int state, double time, int frequency){
		this._id = _id;
		this.score = score;
		this.state = state;
		this.times = time;
		this.frequency = frequency;
	}

	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency,boolean isThread, Context context, Activity activity){
		mActivity = activity;
		mContext = context;

		this._id = _id;
		this.word = word;

		//		try {
		//			this.word = SimpleCrypto.decrypt("hott", word);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;

		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;

		mean_list = new ArrayList<Mean>();


	}

	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency, Context context, Activity activity){
		mActivity = activity;
		mContext = context;

		this._id = _id;

		this.word = word;

		//		try {
		//			this.word = SimpleCrypto.decrypt("hott", word);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;

		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;

		mean_list = new ArrayList<Mean>();

		timer = new Timer();


	}

	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency,
				boolean isRight, boolean isWrong, int wrongCount, Context context, Activity activity){
		mActivity = activity;
		mContext = context;
		this._id = _id;

		this.word = word;

		//		try {
		//			this.word = SimpleCrypto.decrypt("hott", word);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;

		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;

		this.isRight = isRight;
		this.isWrong = isWrong;
		this.wrongCount = wrongCount;

		mean_list = new ArrayList<Mean>();
		timer = new Timer();
	}

	public Word(int _id, String word,int p_class,int difficulty, int priority, double score, int state, int time, int frequency,
				boolean isRight, boolean isWrong, int wrongCount, int exState, Context context, Activity activity){
		mActivity = activity;
		mContext = context;

		this._id = _id;

		this.word = word;

		//		try {
		//			this.word = SimpleCrypto.decrypt("hott", word);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		this.p_class = p_class;
		this.difficulty = difficulty;
		this.priority = priority;

		this.score = score;
		this.state = state;
		this.time = time;
		this.frequency = frequency;

		this.isRight = isRight;
		this.isWrong = isWrong;
		this.wrongCount = wrongCount;

		mean_list = new ArrayList<Mean>();
		timer = new Timer();
		this.exState = exState;
	}


	/**
	 * Title      : Upload State0 Flag
	 * Programmer : Kang Il Gu
	 * Date       : 14.09.22(MON) ~ 14.09.24(WED), 14.10.13(MON)
	 */

	public Word(int word_code, String word, double score, int state, int time, Context context, Activity activity){
		mActivity = activity;
		mContext = context;//used on upgrading algorithm
		this._id = word_code;

		this.word = word;

		//		try {
		//			this.word = SimpleCrypto.decrypt("hott", word);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		this.score = score;
		this.state = state;
		this.time = time;
	}

	public Word(int word_code, int flag, Context context, Activity activity){
		mActivity = activity;
		mContext = context;	//used to upload state0 flag
		this._id = word_code;
		this.known_flag = flag;
	}

	public int getKnownFlag(){
		return this.known_flag;
	}

	/**
	 *
	 */


	public int get_id(){
		return _id;
	}

	public int get(){
		return _id;
	}

	public String getWord(){
		String word = null;
		String chiper= "hi";
		if(Config.Word_chiper.length()>2){
			chiper = Config.Word_chiper;
		}else{
			SharedPreferences mpPreferences = VOCAconfig.context.getSharedPreferences(MainActivitys.preName, Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
			chiper= mpPreferences.getString("Word_Pass", "hi");
		}

		try {
			if(Config.Word_chiper.length()>0){
				Crypter crypter = new Crypter(Config.Word_chiper);
				String res = new String(crypter.decrypt( this.word ), "UTF-8" );
				res = URLDecoder.decode(res,"UTF-8");
				word = res;
			}else{
				if(!chiper.equals("hott")){
					Config.Word_chiper =chiper;
					word = SimpleCrypto.decrypt(Config.Word_chiper, this.word);
				}else{
					new RetrofitService().getAuthorizationService().retroCheckVersion("10")
							.enqueue(new Callback<AuthorizationData>() {

								@Override
								public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
									Log.e("word_pass", "async pass : "+ response.body().getPassword());
									Config.Word_chiper  = response.body().getPassword();

								}

								@Override
								public void onFailure(Throwable t) {
									Config.Word_chiper = "hi";
								}
							});


				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Crypter crypter = new Crypter(Config.Word_chiper);
			String res;
			try {
				res = new String(crypter.decrypt( this.word ), "UTF-8" );
				res = URLDecoder.decode(res,"UTF-8");
				word = res;

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			word.isEmpty();
		} catch (NullPointerException e) {
			// TODO: handle exception
			String res;
			try {
				res = new String(SimpleCrypto.decrypt( "hott" , this.word ));
				word = res;
			} catch (Exception e1) {
				word = "단어 불러오기에 실패하였습니다.";
				Log.e("word_pass", "pass:"+chiper+" second Exception : "+ e.toString());
			}
		}

		return word;
	}


	public int getDifficulty(){
		return difficulty;
	}

	public int getP_class(){
		return p_class;
	}

	public double getScore(){
		return score;
	}

	public void setState(int state)
	{
		this.state = state;
	}

	public int getState(){
		return state;
	}

	public int getTime(int i){
		return time + i;
	}



	public int getNextTime(){
		if(time == 0)
			return 0;

		return ++time;
	}

	public int getFrequency(){

		//5_16 주석처리함
		//		if(frequency == 0)
		//			frequency = 1;

		return frequency;
	}



	public void setRight(boolean isRight)
	{
		this.isRight = isRight;
	}

	public void setWrong(boolean isWrong)
	{
		this.isWrong = isWrong;
	}

	public void setShow(boolean isShow_wc)
	{
		this.isShow_wc = isShow_wc;
	}

	public void increaseWrongCount()
	{
		wrongCount = wrongCount+1;
	}

	public int getWrongCount()
	{
		return wrongCount;
	}
	public void setWrongCount(int count){
		wrongCount = count;
	}

	public boolean isWrong()
	{
		return isWrong;
	}

	public boolean isRight()
	{
		return isRight;
	}

	public boolean isShow()
	{
		return isShow_wc;
	}

	public List<Mean> getMeanList(){
		return mean_list;

	}

	public void setMeanList(List<Mean> MeanList)
	{


		Collections.sort(MeanList, new Comparator<Mean>() {

			public int compare(Mean o1, Mean o2) {
				return String.valueOf(o2.getMpriority()).compareTo(String.valueOf(o1.getMpriority()));
			}
		});

		//Collections.reverse(MeanList);

		this.mean_list = MeanList;
		setmClassList(mean_list);
	}



	private void setmClassList(List<Mean> meanList){
		total_class = new HashMap<Integer, Boolean>();
		for(int i =0; i < meanList.size();i++){
			switch (meanList.get(i).getMClass()) {
				case Class_N:
					total_class.put(Class_N, true);
					break;
				case Class_V:
					total_class.put(Class_V, true);
					break;
				case Class_Ad:
					total_class.put(Class_Ad, true);
					break;
				case Class_Conj:
					total_class.put(Class_Conj, true);
					break;
				case Class_A:
					total_class.put(Class_A, true);
					break;
				default:
					//total_class.put("N", Class_N);
					break;
			}

		}

	}

	public Mean getMean(int position){
		return mean_list.get(position);
	}

	public HashMap<Integer, Boolean> getmClassList(){
		return total_class;
	}

	public void setTimerTask(View v, int index){
		view = v;
		this.index = index;
	}

	@JsonIgnore
	public View getView(){
		return view;

	}
	public int getIndex(){
		return index;

	}
	public int getExState(){
		return exState;
	}
	public void setExState(int exState){
		this.exState = exState;
	}
	public void deleteTimer(){
		this.timer=null;
		this.timer_task=null;
	}
	public double getTimes(){
		return this.times;
	}

	TTS_Util tts_util;

	WordItemViewHolder mViewHolder;


	String mSpecialRate;

	public String getmSpecialRate() {
		return mSpecialRate;
	}

	public void setmSpecialRate(String mSpecialRate) {
		this.mSpecialRate = mSpecialRate;
	}

	@Override
	public View getView(LayoutInflater inflater, View convertView) {

		View view ;
		if(convertView == null || ((WordItemViewHolder)convertView.getTag()).needInflate) {
			convertView = (View)inflater.inflate(R.layout.itemlist_main_word, null);
			view = convertView;
		}else{
			view = convertView;
		}


		return view;
	}

	@Override
	public int getViewType() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isFlipAnimation() {
		return isFlipAnimation;
	}

	public void setFlipAnimation(boolean isFlipAnimation) {
		this.isFlipAnimation = isFlipAnimation;
	}


	public void setWord(String word) {
		this.word = word;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public static int getClass_N() {
		return Class_N;
	}

	public static int getClass_V() {
		return Class_V;
	}

	public static int getClass_A() {
		return Class_A;
	}

	public static int getClass_Ad() {
		return Class_Ad;
	}

	public static int getClass_Conj() {
		return Class_Conj;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public TimerTask getTimer_task() {
		return timer_task;
	}

	public void setTimer_task(TimerTask timer_task) {
		this.timer_task = timer_task;
	}

	@JsonIgnore
	public void setView(View view) {
		this.view = view;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setP_class(int p_class) {
		this.p_class = p_class;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getKnown_flag() {
		return known_flag;
	}

	public void setKnown_flag(int known_flag) {
		this.known_flag = known_flag;
	}

	public boolean isShow_wc() {
		return isShow_wc;
	}

	public void setShow_wc(boolean show_wc) {
		isShow_wc = show_wc;
	}

	public List<Mean> getMean_list() {
		return mean_list;
	}

	public void setMean_list(List<Mean> mean_list) {
		this.mean_list = mean_list;
	}

	public HashMap<Integer, Boolean> getTotal_class() {
		return total_class;
	}

	public void setTotal_class(HashMap<Integer, Boolean> total_class) {
		this.total_class = total_class;
	}

	public void setTimes(double times) {
		this.times = times;
	}
	@JsonIgnore
	public Context getmContext() {
		return mContext;
	}

	@JsonIgnore
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	@JsonIgnore
	public Activity getmActivity() {
		return mActivity;
	}

	@JsonIgnore
	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	@JsonIgnore
	public TTS_Util getTts_util() {
		return tts_util;
	}

	@JsonIgnore
	public void setTts_util(TTS_Util tts_util) {
		this.tts_util = tts_util;
	}

	@JsonIgnore
	public WordItemViewHolder getmViewHolder() {
		return mViewHolder;
	}

	@JsonIgnore
	public void setmViewHolder(WordItemViewHolder mViewHolder) {
		this.mViewHolder = mViewHolder;
	}
}
