package com.ihateflyingbugs.hsmd.tutorial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.model.AuthorizationData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SearchSchoolActivity extends Activity{
	String TAG  = "SearchSchoolActivity";
	ArrayList<School> array_school;
	Handler handler;
	SchoolAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_school);

		handler = new Handler();

		array_school = new ArrayList<School>();
		Button bt_search_school = (Button)findViewById(R.id.bt_search_school);
		final EditText et_search_school = (EditText)findViewById(R.id.et_search_school);
		final ListView lv_search_school = (ListView)findViewById(R.id.lv_search_school);

		final View footerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_schoollist, null, false);

		final Button bt_footer_show_input_form = (Button)footerView.findViewById(R.id.bt_footer_show_input_form);
		final Button bt_footer_insert_school = (Button)footerView.findViewById(R.id.bt_footer_insert_school);
		final EditText et_footer_schoolname = (EditText)footerView.findViewById(R.id.et_footer_schoolname);
		final EditText et_footer_schooladdress = (EditText)footerView.findViewById(R.id.et_footer_schooladdress);
		final LinearLayout ll_footer_insert_school = (LinearLayout)footerView.findViewById(R.id.ll_footer_insert_school);

		bt_footer_show_input_form.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bt_footer_show_input_form.setVisibility(View.GONE);
				ll_footer_insert_school.setVisibility(View.VISIBLE);
				et_footer_schoolname.setText(et_search_school.getText().toString());
			}
		});

		bt_footer_insert_school.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(et_footer_schoolname.getText().length()>2&&et_footer_schooladdress.getText().length()>1){

					new RetrofitService().getAuthorizationService().retroinsertSchoolData(et_footer_schoolname.getText().toString(),
																						et_footer_schooladdress.getText().toString())
							.enqueue(new Callback<AuthorizationData>() {
								@Override
								public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
										Intent intent = new Intent();
										intent.putExtra("school_id", response.body().getSchool_id());
										intent.putExtra("school_name", et_footer_schoolname.getText().toString());
										setResult(6666, intent);
										finish();
								}

								@Override
								public void onFailure(final Throwable t) {
									handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Log.e(TAG, "onFailure : " + t.toString());
											Toast.makeText(getApplicationContext(), "학교입력에 실패하였습니다. 학교이름을 확인후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
										}
									});
								}
							});
				}else if(et_footer_schoolname.getText().equals("")||et_footer_schooladdress.getText().equals("")){
					Toast.makeText(getApplicationContext(), "학교와 학교 주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "학교는 2자이상 주소는 2자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
				}
			}
		});


		bt_search_school.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				array_school.clear();
				if(et_search_school.getText().length()<2
						|| et_search_school.getText().length()>10 ){
					Toast.makeText(getApplicationContext(), "2자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
					return;
				}

				new RetrofitService().getAuthorizationService().retroGetSchoolList(et_search_school.getText().toString())
						.enqueue(new Callback<AuthorizationData>() {
							@Override
							public void onResponse(Response<AuthorizationData> response, Retrofit retrofit) {
								List<AuthorizationData.School> school_list = new ArrayList<AuthorizationData.School>();
								school_list = response.body().getSchool_list();
								try {
									if(school_list.size()>0){
										for(int i=0; i<school_list.size();i++){
											array_school.add(new School(school_list.get(i).getSchool_origin_id(),
													school_list.get(i).getSchool_origin_name(),
													school_list.get(i).getSchool_address()));
										}

										handler.post(new Runnable() {

											@Override
											public void run() {
												adapter = new SchoolAdapter(getApplicationContext(), R.layout.itemlist_school, array_school);
												lv_search_school.setAdapter(adapter);
											}
										});
									}else{
										handler.post(new Runnable() {

											@Override
											public void run() {
												adapter = new SchoolAdapter(getApplicationContext(), R.layout.itemlist_school, array_school);
												lv_search_school.setAdapter(adapter);
												Toast.makeText(getApplicationContext(), "등록된 학교가 없습니다.", Toast.LENGTH_SHORT).show();
											}
										});
									}
								}catch (NullPointerException e ){
									handler.post(new Runnable() {

										@Override
										public void run() {
											adapter = new SchoolAdapter(getApplicationContext(), R.layout.itemlist_school, array_school);
											lv_search_school.setAdapter(adapter);
											Toast.makeText(getApplicationContext(), "등록된 학교가 없습니다.", Toast.LENGTH_SHORT).show();
										}
									});
								}


									handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											lv_search_school.addFooterView(footerView);
										}
									});
							}

							@Override
							public void onFailure(final Throwable t) {
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Log.e(TAG, "onFailure : " + t.toString());
										adapter = new SchoolAdapter(getApplicationContext(), R.layout.itemlist_school, array_school);
										lv_search_school.setAdapter(adapter);
										lv_search_school.addFooterView(footerView);
									}
								});
							}
						});
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public class SchoolAdapter extends ArrayAdapter<School>{

		public SchoolAdapter(Context context, int resource, List<School> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		private LayoutInflater inflater;



		@Override
		public void add(School object) {
			// TODO Auto-generated method stub
			super.add(object);
		}

		@Override
		public School getItem(int position) {
			// TODO Auto-generated method stub
			return super.getItem(position);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view ;
			if(convertView == null) {
				SchoolHolder schoolholder = new SchoolHolder();
				convertView = (View)inflater.inflate(R.layout.itemlist_school, null);
				schoolholder.tv_school_name = (TextView)convertView.findViewById(R.id.tv_school_name);
				schoolholder.tv_school_address = (TextView)convertView.findViewById(R.id.tv_school_address);
				schoolholder.bt_school_choice = (Button)convertView.findViewById(R.id.bt_school_choice);
				convertView.setTag(schoolholder);
				view = convertView;
			}else{
				view = convertView;
			}


			SchoolHolder schoolholder = (SchoolHolder)view.getTag();
			schoolholder.tv_school_name.setText(adapter.getItem(position).getSchool_name());
			schoolholder.tv_school_address.setText(adapter.getItem(position).getSchool_location());

			schoolholder.bt_school_choice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();

					intent.putExtra("school_id", adapter.getItem(position).getSchool_id());
					intent.putExtra("school_name", adapter.getItem(position).getSchool_name());
					setResult(6666, intent);
					finish();
				}
			});

			return view;
		}

	}



	public class School {
		int school_id;
		String school_name;
		String school_location;

		public School(int school_id, String school_name, String school_location){
			this.school_id = school_id;
			this.school_name = school_name;
			this.school_location = school_location;

		}

		public int getSchool_id() {
			return school_id;
		}

		public String getSchool_name() {
			return school_name;
		}

		public String getSchool_location() {
			return school_location;
		}

	}

	private class SchoolHolder{
		TextView tv_school_name;
		TextView tv_school_address;
		Button bt_school_choice;
	}
}
