package com.ihateflyingbugs.hsmd;
//package com.jinu.voca;
//
//import android.app.ActionBar.LayoutParams;
//import android.os.Bundle;
//
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class StatisticsFragment extends Fragment implements OnClickListener  {
//	int total_word_count= 30;
//	int total_lock_count;
//	int total_popup_count;
//	
//	int note_known_word_count = 15;
//	int note_unknown_word_count = 15;
//	
//	int lock_known_word_count;
//	int lock_unknown_word_count;
//	
//	int popup_known_word_count;
//	int popup_unknown_word_count;
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//
//		View view = inflater.inflate(R.layout.fragment_statistics, container, false);
//		
//		LinearLayout ll_word_graph =(LinearLayout)view.findViewById(R.id.ll_word_graph);
//		
//		LinearLayout img_1 =(LinearLayout)view.findViewById(R.id.ll_1);
//		LinearLayout img_2 =(LinearLayout)view.findViewById(R.id.ll_2);
//		
//		TextView tv_1 = (TextView)view.findViewById(R.id.tv_11);
//		TextView tv_2 = (TextView)view.findViewById(R.id.tv_22);
//		TextView tv_3 = (TextView)view.findViewById(R.id.tv_33);
//		
//		
//		ll_word_graph.setWeightSum(total_word_count);
//		note_known_word_count = 15;
//		note_unknown_word_count = 15;
//		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT,note_known_word_count);
//		img_1.setLayoutParams(param);
//		LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT,
//                LayoutParams.MATCH_PARENT, note_unknown_word_count);
//		img_2.setLayoutParams(param1);
//		
//		
//		tv_1.setText(String.valueOf(total_word_count));
//		tv_2.setText(String.valueOf(note_known_word_count));
//		tv_3.setText(String.valueOf(note_unknown_word_count));
//
//		return view;
//	}
//
//	@Override
//	public void onClick(View v) {
//
//		switch (v.getId()) {
//
//		case R.id.bt_ok:
//			Toast.makeText(getActivity(), "One Fragment", Toast.LENGTH_SHORT)
//					.show();
//			break;
//
//		}
//
//	}
//
//}
