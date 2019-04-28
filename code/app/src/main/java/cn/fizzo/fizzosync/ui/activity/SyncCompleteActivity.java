package cn.fizzo.fizzosync.ui.activity;

import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.fizzosync.R;

/**
 * Created by Raul.fan on 2017/6/27 0027.
 */

public class SyncCompleteActivity extends BaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sync_complete;
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
    protected void causeGC() {

    }


    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }
}
