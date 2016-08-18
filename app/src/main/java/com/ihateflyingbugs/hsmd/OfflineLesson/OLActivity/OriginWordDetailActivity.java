package com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.OriginWord;
import com.ihateflyingbugs.hsmd.R;

/**
 * Created by 영철 on 2016-07-11.
 */
public class OriginWordDetailActivity extends Activity{
    String TAG = "OriginWordDetailActivity";
    OriginWord originWord;
    String ImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_originword);

        originWord = (OriginWord) getIntent().getExtras().getSerializable("originword");
        ImageUrl = getIntent().getExtras().getString("ImageUrl");

        final LinearLayout ll_popup_origin = (LinearLayout) findViewById(R.id.ll_popup_originword);

        ImageView iv_explain_originword = new ImageView(getApplicationContext());
        iv_explain_originword.setId((R.id.img_origin_word));
        Glide.with(this).load(ImageUrl+originWord.getOrigin_word_img1()).into(iv_explain_originword);
        ll_popup_origin.addView(iv_explain_originword);

        ImageView iv_explain_originword2 = new ImageView(getApplicationContext());
        iv_explain_originword2.setId((R.id.img_origin_word)+1);
        Glide.with(this).load(ImageUrl+originWord.getOrigin_word_img2()).into(iv_explain_originword2);
        ll_popup_origin.addView(iv_explain_originword2);
    }
}
