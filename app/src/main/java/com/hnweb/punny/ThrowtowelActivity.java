package com.hnweb.punny;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class ThrowtowelActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 10000; //splash screen will be shown for 2 seconds
    ImageView iv_gif;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throwtowel);
        iv_gif = findViewById(R.id.iv_gif);
        /** Called when the activity is first created. */
        Glide.with(this)
                .load(R.raw.thowingtowel)
                .into(new GlideDrawableImageViewTarget(
                        iv_gif));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent mainIntent = new Intent(ThrowtowelActivity.this, CreditListActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
