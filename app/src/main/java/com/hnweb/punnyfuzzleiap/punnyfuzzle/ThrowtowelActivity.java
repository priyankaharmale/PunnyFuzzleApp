package com.hnweb.punnyfuzzleiap.punnyfuzzle;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.multiplayer.activity.CreditListActivity;
import com.hnweb.punnyfuzzleiap.punnyfuzzle.sixtysecondchallge.SixtySecondCreditListActivity;

public class ThrowtowelActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 10000; //splash screen will be shown for 2 seconds
    ImageView iv_gif;
    String callfrom = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throwtowel);
        iv_gif = findViewById(R.id.iv_gif);

        Intent intent = getIntent();
        callfrom = intent.getStringExtra("callfrom");
        /** Called when the activity is first created. */
        Glide.with(this)
                .load(R.raw.thowingtowel)
                .into(new GlideDrawableImageViewTarget(
                        iv_gif));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (callfrom.equalsIgnoreCase("1")) {
                    Intent mainIntent = new Intent(ThrowtowelActivity.this, CreditListActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else if (callfrom.equalsIgnoreCase("2")) {
                    Intent mainIntent = new Intent(ThrowtowelActivity.this, SixtySecondCreditListActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
