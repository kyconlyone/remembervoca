package com.ihateflyingbugs.hsmd.struct;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.os.*;
import android.util.Log;


//-------------------------- 
//HttpPost
//--------------------------
public class HttpPost {

	public  String   rString;                         // Receive String;
	public  StringBuilder  rBuffer;                // Receive Buffer
	private ArrayList<HttpQue>  sBuffer;      // SendBuffer

	//--------------------------
	//  생성자
	//--------------------------
	public HttpPost(ArrayList<HttpQue> _sBuffer) {
		sBuffer = _sBuffer;

		rBuffer = new StringBuilder(20000);
		rString = "";
	}

	//--------------------------
	//   URL 설정하고 접속하기
	//--------------------------
	public void HttpPostData() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
		try {

			URL url = new URL(sBuffer.get(0).value);       // URL 설정
			HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속

			//--------------------------
			// 전송 모드 설정 - 기본적인 설정이다
			//--------------------------
			http.setConnectTimeout(5000);  // 5초
			http.setReadTimeout(10000) ;  // 10초
			http.setDefaultUseCaches(false);
			http.setDoInput(true);                         // 서버에서 읽기 모드 지정
			http.setDoOutput(true);                        // 서버로 쓰기 모드 지정
			http.setRequestMethod("POST");    // 전송 방식은 POST
			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
			http.setRequestProperty("content-type", "application/x-www-form-urlencoded");

			//--------------------------
			// 서버로 값 전송
			//--------------------------
			StringBuffer buffer = new StringBuffer();
			for (int i = 1; i < sBuffer.size(); i++) {
				buffer.append(sBuffer.get(i).var).append("=");
				buffer.append(sBuffer.get(i).value).append("&");
			}

			//OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "EUC-KR");
			OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");

			PrintWriter writer = new PrintWriter(outStream);
			writer.write(buffer.toString());
			writer.flush();
			// Log.v("Http post", buffer.toString());

			//--------------------------
			// 서버에서 전송받기
			//--------------------------

			//InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "EUC-KR");
			InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");

			BufferedReader reader = new BufferedReader(tmp);
			String str;
			while ((str = reader.readLine()) != null) {  // 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다
				rBuffer.append(str + "\n");
			}
			rString = rBuffer.toString().trim();        // 전송결과를 문자열로
			// Log.v("Receive String", rString);

		} catch (MalformedURLException e) {
			Log.v("MalformedURLException error", "--------------");
			rString = "N/A";
		} catch (IOException e) {
			Log.v("IOException", rString + "--------------");
			rString = "N/A";
		} // try
	} // HttpPostData
} // class
