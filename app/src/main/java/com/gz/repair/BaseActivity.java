package com.gz.repair;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;

/**
 * Created by Endeavor on 2016/8/24.
 */
public class BaseActivity extends AppCompatActivity {

    protected BaseAnimatorSet mBasIn;
    protected BaseAnimatorSet mBasOut;

    protected void setBasIn(BaseAnimatorSet bas_in) {
        this.mBasIn = bas_in;
    }

    protected void setBasOut(BaseAnimatorSet bas_out) {
        this.mBasOut = bas_out;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        // 初始化Dialog 进出屏幕动画
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
    }
}
