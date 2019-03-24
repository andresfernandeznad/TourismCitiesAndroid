package com.example.andres.tourismcities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class SliderLoginActivity extends AppCompatActivity {

    ViewFlipper viewFlipper;
    ImageView imageView1;
    ImageView imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_login);
        viewFlipper = findViewById(R.id.simpleViewFlipper);

        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));

        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();


    }
}
