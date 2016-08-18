package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 * 占쎌쥙?껓옙貫踰앾옙占쏙옙醫롫짗占쎌눨?앾옙?롮굻占쏙옙占쎌쥙占쏙쭗??굲占쎌쥙猷욑옙占쏙옙醫롫뼦占쎈벊?뺧옙醫롫솂占쎈Ŋ??TextToSpeech 占쎌쥙猷욑옙?용쐻占쎌늿?? * @author mailss
 */
public class TTS_Util {
	private TextToSpeech mTTS;
	private static boolean isInit, isSupport;

	public TTS_Util(Context context){
		try {
			mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {

				@Override
				public void onInit(int status) {
					// TODO Auto-generated method stub
					isInit = status == TextToSpeech.SUCCESS;

					mTTS.setEngineByPackageName("com.google.android.tts");
					mTTS.setLanguage(Locale.US);


					mTTS.setPitch(10/10.0f);
					mTTS.setSpeechRate(10/10.0f);
				}
			});
		}
		catch (Exception e) {
			// TODO: handle exception
			isInit = false;
		}
	}


	public boolean tts_check(){

		if(!isInit){
			isSupport = false;
			return false;
		}

		try {
			int available = mTTS.isLanguageAvailable(Locale.US);
			if(available < 0) {
				isSupport = false;
			}
			else isSupport = true;

			Log.e("tts_test", String.valueOf(isSupport));
		} catch (Exception e) {
			// TODO: handle exception
			isSupport = false;
		}

		return isSupport;
	}

	public void tts_reading(String text){

		if(!isInit){
			//Toast.makeText(this, R.string.msg_fail_init, Toast.LENGTH_SHORT).show();
		}else if(!tts_check()){
			//Toast.makeText(getApplicationContext(), R.string.msg_not_support_lang, Toast.LENGTH_SHORT).show();
		}else {
			if(TextUtils.isEmpty(text)){
				//Toast.makeText(this, R.string.msg_success_init, Toast.LENGTH_SHORT).show();
			}else {

				mTTS.setLanguage(Locale.US);
				mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

				Log.e("tts_test", text);
			}
		}

	}
	public void onDestroy() {
		//Close the Text to Speech Library
		if(mTTS != null) {

			mTTS.stop();
			mTTS.shutdown();
			Log.d("tts_test", "TTS Destroyed");
		}
	}


}