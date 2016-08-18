package com.ihateflyingbugs.hsmd.lock;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ihateflyingbugs.hsmd.R;

import java.util.List;

public class WebViewActivity extends Activity {

	SharedPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		WebView mWebView = (WebView)findViewById(R.id.webview);
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setWebViewClient(new MyWebViewClient());

		String uri = getIntent().getStringExtra("uri");


		WebSettings settings = mWebView.getSettings();
		settings.setBuiltInZoomControls(true); // 안드로이드에서 제공하는 줌 아이콘을 사용할 수 있도록 설정
		settings.setSupportZoom(true); // 확대,축소 기능을 사용할 수 있도록 설정
		settings.setJavaScriptEnabled(true);
		mWebView.loadUrl(""+uri);
		mWebView.reload();
	}


	@Override
	protected void onStop() {
		super.onStop();

		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName componentInfo = taskInfo.get(0).topActivity;

		if (!componentInfo.getPackageName().equals("com.ihateflyingbugs.hsmd")) {
			for (int i = 0; i < taskInfo.size(); i++) {
				finish();
			}
		}
	}


	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
