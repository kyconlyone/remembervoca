package com.ihateflyingbugs.hsmd.popup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ihateflyingbugs.hsmd.R;

public class ExplainBetaService extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popup_explain_betaservice);
		WebView wv_explain_betaservice = (WebView)findViewById(R.id.wv_explain_betaservice);

		wv_explain_betaservice.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		wv_explain_betaservice.setWebViewClient(new MyWebViewClient());
		String uri =  "http://www.lnslab.com/mentor/";
		try {
			uri =getIntent().getExtras().getString("uri");
			Log.e("address_beta", uri);
		} catch (Exception e) {
			// TODO: handle exception
			uri =  "http://www.lnslab.com/mentor/";
		}



		WebSettings settings = wv_explain_betaservice.getSettings();
		settings.setBuiltInZoomControls(true); // 안드로이드에서 제공하는 줌 아이콘을 사용할 수 있도록 설정
		settings.setSupportZoom(true); // 확대,축소 기능을 사용할 수 있도록 설정
		settings.setJavaScriptEnabled(true);
		wv_explain_betaservice.loadUrl(""+uri);
		wv_explain_betaservice.reload();

		setResult(RESULT_OK);
	}

	class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}


}
