package cn.fizzo.fizzosync.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import butterknife.BindView;
import cn.fizzo.fizzosync.R;
import cn.fizzo.watch.Fw;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class WelcomeActivity extends BaseActivity {


    @BindView(R.id.v_anim)
    View mAnimV;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }

    @Override
    protected void doMyCreate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        animLaunch();
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 页面启动动画
     */
    private void animLaunch() {
        AlphaAnimation lAnim = new AlphaAnimation(0.1f, 1.0f);
        lAnim.setDuration(2000);

        if (mAnimV == null || lAnim == null) {
            launchNext();
            return;
        }
        mAnimV.startAnimation(lAnim);
        lAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                launchNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
    }


    /**
     * 根据不同的情况启动页面
     */
    private void launchNext() {
//        startActivity(SelectCoachListActivity.class);
        startActivity(PackageHrDeviceListActivity.class);
    }
}
