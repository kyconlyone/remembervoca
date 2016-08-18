package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ihateflyingbugs.hsmd.R;

import java.util.List;

/**
 * Created by 영철 on 2016-06-22.
 */
public class SampleTextAdapter extends ArrayAdapter<SampleText> {


    private LayoutInflater mInflater;

    public SampleTextAdapter(Context context, List<SampleText> list_sampletext) {
        super(context, 0, list_sampletext);
        mInflater = LayoutInflater.from(context);
    }

    View view;
    SampleTextViewHolder sampleTextViewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_sampletext, null);
            setViewHolder(view);
        }  else {
            view = convertView;
        }
        sampleTextViewHolder = (SampleTextViewHolder) view.getTag();
        sampleTextViewHolder.tv_sampletext_item.setText(getItem(position).getText());
        sampleTextViewHolder.tv_sampletext_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        view.setTag(sampleTextViewHolder);



        return view;
    }

    private void setViewHolder(View view) {
        SampleTextViewHolder vh = new SampleTextViewHolder();
        vh.tv_sampletext_item = (TextView) view.findViewById(R.id.tv_sampletext_item);
        view.setTag(vh);
    }

    public class SampleTextViewHolder{
        TextView tv_sampletext_item;
    }
}
