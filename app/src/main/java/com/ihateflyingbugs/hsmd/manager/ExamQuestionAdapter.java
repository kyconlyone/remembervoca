package com.ihateflyingbugs.hsmd.manager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;
import com.ihateflyingbugs.hsmd.model.ManagerData;
import com.ihateflyingbugs.hsmd.retrofitservice.RetrofitService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class ExamQuestionAdapter extends BaseAdapter {
	String TAG = "ExamQuestionAdapter";

	Context mContext;
	View v;


	ArrayList<ExamQuestion> mExamQuestion_ArrayList;

	ViewHolder mViewHolder;
	ViewHolder mClicked_ViewHolder;
	int mClicked_Position;


	int mQuestionNumber;
	int mWordCode;
	String mWord_String;
	String mComplete_QuestionWord_String;

	String[] mExampleWord_Strings = new String[4];

	int mStudentClickedNumber;

	String mSentence_String;


	public ExamQuestionAdapter(Context context) {
		mExamQuestion_ArrayList = new ArrayList<ExamQuestion>();
		mContext = context;
	}

	@Override
	public int getCount() {
		return mExamQuestion_ArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return mExamQuestion_ArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		v = convertView;

		if(v == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_examresult_question, parent, false);

			mViewHolder = new ViewHolder();

			mViewHolder.mExamQuestionWord_TextView = (TextView)v.findViewById(R.id.text_examquestion_question);

			mViewHolder.mSentence_TextView = (TextView)v.findViewById(R.id.text_examquestion_sentence);

			mViewHolder.mExampleWord_TextView[0] = (TextView)v.findViewById(R.id.text_examquestion_example1);
			mViewHolder.mExampleWord_TextView[1] = (TextView)v.findViewById(R.id.text_examquestion_example2);
			mViewHolder.mExampleWord_TextView[2] = (TextView)v.findViewById(R.id.text_examquestion_example3);
			mViewHolder.mExampleWord_TextView[3] = (TextView)v.findViewById(R.id.text_examquestion_example4);


			for(int i=0; i<4; i++) {
				mViewHolder.mExampleWord_TextView[i].setTag(mViewHolder);
			}

			v.setTag(mViewHolder);
		}
		else {
			mViewHolder = (ViewHolder) v.getTag();
		}

		mViewHolder.position = position;


		mQuestionNumber = mExamQuestion_ArrayList.get(position).getQuestionNum();
		mWordCode = mExamQuestion_ArrayList.get(position).getWordCode();
		mWord_String = mExamQuestion_ArrayList.get(position).getWord_String();

		mSentence_String = mExamQuestion_ArrayList.get(position).getSentence();

		mExampleWord_Strings[0] = mExamQuestion_ArrayList.get(position).getExample1_String();
		mExampleWord_Strings[1] = mExamQuestion_ArrayList.get(position).getExample2_String();
		mExampleWord_Strings[2] = mExamQuestion_ArrayList.get(position).getExample3_String();
		mExampleWord_Strings[3] = mExamQuestion_ArrayList.get(position).getExample4_String();


		// Question Word
		mComplete_QuestionWord_String = mQuestionNumber + ". " + mWord_String;
		mViewHolder.mExamQuestionWord_TextView.setText(mComplete_QuestionWord_String);

		try{
			if(mSentence_String.equals("null")) {
				mViewHolder.mSentence_TextView.setVisibility(View.GONE);
			}
			else {
				mViewHolder.mSentence_TextView.setVisibility(View.VISIBLE);
				mViewHolder.mSentence_TextView.setText(mSentence_String);
			}
		}catch (NullPointerException e){
			mViewHolder.mSentence_TextView.setVisibility(View.GONE);
		}



		// Examples  
		for(int i=0; i<4; i++) {
			mViewHolder.mExampleWord_TextView[i].setText(mExampleWord_Strings[i]);
			mViewHolder.mExampleWord_TextView[i].setBackgroundResource(R.drawable.border_example_default);
			mViewHolder.mExampleWord_TextView[i].setTextColor(Color.parseColor("#316F9B"));

			mViewHolder.mExampleWord_TextView[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch(v.getId()) {
						case R.id.text_examquestion_example1: {
							mClicked_ViewHolder = (ViewHolder) v.getTag();
							mClicked_Position = mClicked_ViewHolder.position;

							initExample(mClicked_ViewHolder);

							mClicked_ViewHolder.mExampleWord_TextView[0].setBackgroundResource(R.drawable.border_example_selected);
							mClicked_ViewHolder.mExampleWord_TextView[0].setTextColor(Color.parseColor("#FFFFFF"));

							mStudentClickedNumber = 1;
							mExamQuestion_ArrayList.get(mClicked_Position).setSelectedNum(mStudentClickedNumber);
							sendRealTimeUploadTest(1, mExamQuestion_ArrayList.get(position).getRowID());

						} break;

						case R.id.text_examquestion_example2: {
							mClicked_ViewHolder = (ViewHolder) v.getTag();
							mClicked_Position = mClicked_ViewHolder.position;

							initExample(mClicked_ViewHolder);

							mClicked_ViewHolder.mExampleWord_TextView[1].setBackgroundResource(R.drawable.border_example_selected);
							mClicked_ViewHolder.mExampleWord_TextView[1].setTextColor(Color.parseColor("#FFFFFF"));

							mStudentClickedNumber = 2;
							mExamQuestion_ArrayList.get(mClicked_Position).setSelectedNum(mStudentClickedNumber);
							sendRealTimeUploadTest(2, mExamQuestion_ArrayList.get(position).getRowID());
						} break;
						case R.id.text_examquestion_example3: {
							mClicked_ViewHolder = (ViewHolder) v.getTag();
							mClicked_Position = mClicked_ViewHolder.position;

							initExample(mClicked_ViewHolder);

							mClicked_ViewHolder.mExampleWord_TextView[2].setBackgroundResource(R.drawable.border_example_selected);
							mClicked_ViewHolder.mExampleWord_TextView[2].setTextColor(Color.parseColor("#FFFFFF"));

							mStudentClickedNumber = 3;
							mExamQuestion_ArrayList.get(mClicked_Position).setSelectedNum(mStudentClickedNumber);
							sendRealTimeUploadTest(3, mExamQuestion_ArrayList.get(position).getRowID());
						} break;
						case R.id.text_examquestion_example4: {
							mClicked_ViewHolder = (ViewHolder) v.getTag();
							mClicked_Position = mClicked_ViewHolder.position;

							initExample(mClicked_ViewHolder);

							mClicked_ViewHolder.mExampleWord_TextView[3].setBackgroundResource(R.drawable.border_example_selected);
							mClicked_ViewHolder.mExampleWord_TextView[3].setTextColor(Color.parseColor("#FFFFFF"));

							mStudentClickedNumber = 4;
							mExamQuestion_ArrayList.get(mClicked_Position).setSelectedNum(mStudentClickedNumber);
							sendRealTimeUploadTest(4, mExamQuestion_ArrayList.get(position).getRowID());
						} break;
					}
				}
			});
		}

		int mSelecedNum = mExamQuestion_ArrayList.get(position).getSelectedNum();
		if(mSelecedNum != 0) {
			mViewHolder.mExampleWord_TextView[mSelecedNum-1].setBackgroundResource(R.drawable.border_example_selected);
			mViewHolder.mExampleWord_TextView[mSelecedNum-1].setTextColor(Color.parseColor("#FFFFFF"));
		}

		return v;
	}

	void initExample(ViewHolder vh) {
		for(int i=0; i<4; i++) {
			vh.mExampleWord_TextView[i].setBackgroundResource(R.drawable.border_example_default);
			vh.mExampleWord_TextView[i].setTextColor(Color.parseColor("#316F9B"));
		}
	}
	private void sendRealTimeUploadTest(int choice_num, int row_id) {
		if(ManagerInfo.getInstance(mContext).ExamType==10){
			new RetrofitService().getManagerService().retroUpdateTestPaper(row_id, choice_num)
					.enqueue(new Callback<ManagerData>() {
						@Override
						public void onResponse(Response<ManagerData> response, Retrofit retrofit) {
							//워드로그를 오릴껀지 말껀지 결정해야함.
						}

						@Override
						public void onFailure(Throwable t) {

						}
					});
		}
	}

	public void addListItem(ExamQuestion _msg) {
		mExamQuestion_ArrayList.add(_msg);
	}

	public void removeListItem(int _position) {
		mExamQuestion_ArrayList.remove(_position);
	}

	static class ViewHolder {
		TextView mExamQuestionWord_TextView;

		TextView mSentence_TextView;

		TextView[] mExampleWord_TextView = new TextView[4];

		int position;

		int ClickedAnswer;
	}
}
