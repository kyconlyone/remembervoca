package com.ihateflyingbugs.hsmd.manager;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

import java.util.ArrayList;

public class ExamQuestion_Result_Adapter extends BaseAdapter {
	String TAG = "ExamQuestionAdapter";

	Context mContext;
	View v;


	ArrayList<ExamResult_Questions> mExamQuestion_ArrayList;

	ViewHolder mViewHolder;
	ViewHolder mClicked_ViewHolder;


	int mQuestionNumber;
	int mQuestionWordCode;
	String mQuestionWord_String;
	int mCorrectWordNumber;

	int mEBSCount;
	int mSNCount;
	int mPGWCount;
	int mFrequencyCount;
	double mForgetRate;

	String[] mExampleWord_Strings = new String[4];

	int mStudentClickedNumber;


	String mComplete_QuestionWord_String;
	String mComplete_Frequency_String;
	String mComplete_ForgetRate_String;

	String mSentence_String;
	String mSentenceMean_String;
	String mSentenceInfo_String;

	public ExamQuestion_Result_Adapter(Context context) {
		mExamQuestion_ArrayList = new ArrayList<ExamResult_Questions>();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		v = convertView;

		if(v == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.item_examresult_result_question, parent, false);

			mViewHolder = new ViewHolder();

			mViewHolder.mExamQuestion_Layout = (LinearLayout)v.findViewById(R.id.layout_examquestion);

			mViewHolder.mQuestionFrequency_TextView = (TextView)v.findViewById(R.id.text_examquestion_frequency);
			mViewHolder.mExamQuestionWord_TextView = (TextView)v.findViewById(R.id.text_examquestion_question);
			mViewHolder.mQuestionForgetRate_TextView = (TextView)v.findViewById(R.id.text_examquestion_forget);

			mViewHolder.mSentence_TextView = (TextView)v.findViewById(R.id.text_examquestion_sentence);
			mViewHolder.mSentenceMean_TextView = (TextView)v.findViewById(R.id.text_examquestion_sentencemean);
			mViewHolder.mSentenceInfo_TextView = (TextView)v.findViewById(R.id.text_examquestion_sentenceinfo);

			mViewHolder.mExampleWord_Layout[0] = (RelativeLayout)v.findViewById(R.id.layout_examquestion_example1);
			mViewHolder.mExampleWord_Layout[1] = (RelativeLayout)v.findViewById(R.id.layout_examquestion_example2);
			mViewHolder.mExampleWord_Layout[2] = (RelativeLayout)v.findViewById(R.id.layout_examquestion_example3);
			mViewHolder.mExampleWord_Layout[3] = (RelativeLayout)v.findViewById(R.id.layout_examquestion_example4);

			mViewHolder.mExampleWord_TextView[0] = (TextView)v.findViewById(R.id.text_examquestion_example1);
			mViewHolder.mExampleWord_TextView[1] = (TextView)v.findViewById(R.id.text_examquestion_example2);
			mViewHolder.mExampleWord_TextView[2] = (TextView)v.findViewById(R.id.text_examquestion_example3);
			mViewHolder.mExampleWord_TextView[3] = (TextView)v.findViewById(R.id.text_examquestion_example4);

			mViewHolder.mExampleOX_ImageView[0] = (ImageView)v.findViewById(R.id.img_examquestion_ox1);
			mViewHolder.mExampleOX_ImageView[1] = (ImageView)v.findViewById(R.id.img_examquestion_ox2);
			mViewHolder.mExampleOX_ImageView[2] = (ImageView)v.findViewById(R.id.img_examquestion_ox3);
			mViewHolder.mExampleOX_ImageView[3] = (ImageView)v.findViewById(R.id.img_examquestion_ox4);

			v.setTag(mViewHolder);
		}
		else {
			mViewHolder = (ViewHolder) v.getTag();
		}

		mViewHolder.position = position;


		mQuestionNumber = mExamQuestion_ArrayList.get(position).getQuestionNumber();
		mQuestionWordCode = mExamQuestion_ArrayList.get(position).getQuestionWordCode();
		mQuestionWord_String = mExamQuestion_ArrayList.get(position).getQuestionWord_String();
		mCorrectWordNumber = mExamQuestion_ArrayList.get(position).getCorrectNumber();

		mEBSCount = mExamQuestion_ArrayList.get(position).getFrequencyCount_EBS();
		mSNCount = mExamQuestion_ArrayList.get(position).getFrequencyCount_SN();
		mPGWCount = mExamQuestion_ArrayList.get(position).getFrequencyCount_PGW();
		mForgetRate = mExamQuestion_ArrayList.get(position).getForgetRate();

		mSentence_String = mExamQuestion_ArrayList.get(position).getSentence();
		mSentenceMean_String = mExamQuestion_ArrayList.get(position).getSentence_Mean();
		mSentenceInfo_String = mExamQuestion_ArrayList.get(position).getSentence_Info();

		mExampleWord_Strings[0] = mExamQuestion_ArrayList.get(position).getExampleWord1();
		mExampleWord_Strings[1] = mExamQuestion_ArrayList.get(position).getExampleWord2();
		mExampleWord_Strings[2] = mExamQuestion_ArrayList.get(position).getExampleWord3();
		mExampleWord_Strings[3] = mExamQuestion_ArrayList.get(position).getExampleWord4();

		mStudentClickedNumber = mExamQuestion_ArrayList.get(position).getStudentCheckedNumber();


		// FrequencyCount
		mComplete_Frequency_String = mContext.getString(R.string.examresult_frequencycount);
		mComplete_Frequency_String = mComplete_Frequency_String.replace("frequencyEBS", Integer.toString(mEBSCount));
		mComplete_Frequency_String = mComplete_Frequency_String.replace("frequencySN", Integer.toString(mSNCount));
		mComplete_Frequency_String = mComplete_Frequency_String.replace("frequencyPGW", Integer.toString(mPGWCount));
		mViewHolder.mQuestionFrequency_TextView.setText(mComplete_Frequency_String);


		// Question Word
		mComplete_QuestionWord_String = mContext.getString(R.string.examresult_question);
		mComplete_QuestionWord_String = mComplete_QuestionWord_String.replace("number", Integer.toString(mQuestionNumber));
		mComplete_QuestionWord_String = mComplete_QuestionWord_String.replace("questionword", mQuestionWord_String);
		mViewHolder.mExamQuestionWord_TextView.setText(mComplete_QuestionWord_String);

		// ForgetRate
		mForgetRate = mForgetRate * 100;
		mComplete_ForgetRate_String = mContext.getString(R.string.examresult_forgetrate);
		mComplete_ForgetRate_String = mComplete_ForgetRate_String.replace("forgetrate", String.format("%.0f", mForgetRate));
		mViewHolder.mQuestionForgetRate_TextView.setText(mComplete_ForgetRate_String);
		try{
			if(mSentence_String=="") {
				mViewHolder.mSentence_TextView.setVisibility(View.GONE);
				mViewHolder.mSentenceMean_TextView.setVisibility(View.GONE);
				mViewHolder.mSentenceInfo_TextView.setVisibility(View.GONE);
			}
			else {
				mViewHolder.mSentence_TextView.setVisibility(View.VISIBLE);
				mViewHolder.mSentence_TextView.setText(mSentence_String);

				if(mCorrectWordNumber == mStudentClickedNumber) {
					mViewHolder.mSentenceMean_TextView.setVisibility(View.VISIBLE);
					mViewHolder.mSentenceMean_TextView.setText(mSentenceMean_String);

					mViewHolder.mSentenceInfo_TextView.setVisibility(View.VISIBLE);
					mViewHolder.mSentenceInfo_TextView.setText(mSentenceInfo_String);
				}
			}
		}catch(NullPointerException e){
			mViewHolder.mSentence_TextView.setVisibility(View.GONE);
			mViewHolder.mSentenceMean_TextView.setVisibility(View.GONE);
			mViewHolder.mSentenceInfo_TextView.setVisibility(View.GONE);
		}


		// Examples
		for(int i=0; i<4; i++) {
			mViewHolder.mExampleWord_Layout[i].setBackgroundResource(R.drawable.border_example_default);
			mViewHolder.mExampleWord_TextView[i].setText(mExampleWord_Strings[i]);
			mViewHolder.mExampleWord_TextView[i].setTextColor(Color.parseColor("#316F9B"));
			mViewHolder.mExampleOX_ImageView[i].setBackgroundResource(R.drawable.icon_wrong);
		}


		mViewHolder.mExampleOX_ImageView[mCorrectWordNumber-1].setBackgroundResource(R.drawable.icon_correct);
		Log.e("KARAM", "  Result  : " + mCorrectWordNumber + "  vs  " + mStudentClickedNumber);

		if(mCorrectWordNumber == mStudentClickedNumber) {	// 정답을 맞췄음~
			mViewHolder.mExamQuestion_Layout.setBackgroundResource(R.drawable.bg_white);
			mViewHolder.mExampleWord_Layout[mCorrectWordNumber-1].setBackgroundResource(R.drawable.border_example_selected);
			mViewHolder.mExampleWord_TextView[mCorrectWordNumber-1].setTextColor(Color.parseColor("#FFFFFF"));
			mViewHolder.mExampleOX_ImageView[mCorrectWordNumber-1].setBackgroundResource(R.drawable.icon_correct_selected);
		}
		else {	// 틀렸음 ~
			if(mStudentClickedNumber == 0)
				mStudentClickedNumber = 1;
			mViewHolder.mExamQuestion_Layout.setBackgroundResource(R.drawable.bg_wrong);
			mViewHolder.mExampleWord_Layout[mStudentClickedNumber-1].setBackgroundResource(R.drawable.border_example_selected_incorrect);
			mViewHolder.mExampleWord_TextView[mStudentClickedNumber-1].setTextColor(Color.parseColor("#FFFFFF"));
			mViewHolder.mExampleOX_ImageView[mStudentClickedNumber-1].setBackgroundResource(R.drawable.icon_wrong_selected);
		}

		return v;
	}

	public void addListItem(ExamResult_Questions _msg) {
		mExamQuestion_ArrayList.add(_msg);
	}

	public void removeListItem(int _position) {
		mExamQuestion_ArrayList.remove(_position);
	}

	static class ViewHolder {
		LinearLayout mExamQuestion_Layout;

		TextView mQuestionFrequency_TextView;
		TextView mExamQuestionWord_TextView;
		TextView mQuestionForgetRate_TextView;

		TextView mSentence_TextView;
		TextView mSentenceMean_TextView;
		TextView mSentenceInfo_TextView;

		RelativeLayout[] mExampleWord_Layout = new RelativeLayout[4];
		TextView[] mExampleWord_TextView = new TextView[4];
		ImageView[] mExampleOX_ImageView = new ImageView[4];

		int position;
	}
}
