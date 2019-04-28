package cn.fizzo.fizzosync.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.config.UrlConfig;
import cn.fizzo.fizzosync.entity.adapter.SelectTraineeEntity;
import cn.fizzo.fizzosync.entity.net.BaseRE;
import cn.fizzo.fizzosync.entity.net.GetCoachListRE;
import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;
import cn.fizzo.fizzosync.network.BaseResponseParser;
import cn.fizzo.fizzosync.network.HttpExceptionHelper;
import cn.fizzo.fizzosync.network.RequestParamsBuilder;
import cn.fizzo.fizzosync.ui.adapter.SelectCoachListAdapter;
import cn.fizzo.fizzosync.ui.adapter.SelectTraineeListAdapter;
import cn.fizzo.fizzosync.ui.widget.common.MyLoadingView;
import cn.fizzo.fizzosync.ui.widget.pulltorefresh.PullToRefreshLayout;
import cn.fizzo.fizzosync.ui.widget.pulltorefresh.PullableListView;
import cn.fizzo.fizzosync.ui.widget.toast.Toasty;
import cn.fizzo.fizzosync.utils.NetworkU;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class SelectTraineeListActivity extends BaseActivity {

    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;


    @BindView(R.id.list)
    PullableListView list;
    @BindView(R.id.ptr)
    PullToRefreshLayout ptr;
    @BindView(R.id.v_loading)
    MyLoadingView vLoading;

    private boolean mIsFirstIn = true;

    private GetCoachListRE.CoachesBean mCoach;//教练
    private SelectTraineeListAdapter mAdapter;
    private List<SelectTraineeEntity> mTraineeList = new ArrayList<>();

    private ConnectionChangeReceiver myReceiver;
    private boolean mBluetoothState = true;
    private boolean mNetworkState = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_trainee;
    }

    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POST_ERROR:
                    Toasty.error(SelectTraineeListActivity.this, (String) msg.obj).show();
                    if (mIsFirstIn) {
                        vLoading.LoadError((String) msg.obj);
                    } else {
                        ptr.refreshFinish(PullToRefreshLayout.FAIL);
                    }
                    break;
                case MSG_POST_OK:
                    mAdapter.notifyDataSetChanged();
                    if (mIsFirstIn) {
                        mIsFirstIn = false;
                        vLoading.loadFinish();
                    } else {
                        ptr.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.btn_back, R.id.btn_all_select,R.id.btn_sync})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_all_select:
                for (SelectTraineeEntity entity : mTraineeList){
                    entity.select = true;
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_sync:
                checkTraineeList();
                break;
        }
    }

    @Override
    protected void initData() {
        mCoach = (GetCoachListRE.CoachesBean) getIntent().getExtras().getSerializable("coach");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        vLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postGetTraineeList();
            }
        });

        ptr.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                postGetTraineeList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ptr.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });

        mAdapter = new SelectTraineeListAdapter(SelectTraineeListActivity.this, mTraineeList);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                SelectTraineeEntity traineeEntity = mTraineeList.get(i);
//                traineeEntity.select = !traineeEntity.select;
//                mTraineeList.set(i,)
                mTraineeList.get(i).select = !mTraineeList.get(i).select;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetTraineeList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    protected void causeGC() {
        mLocalHandler.removeCallbacksAndMessages(null);
    }

    private void postGetTraineeList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetTraineeListRP(SelectTraineeListActivity.this, UrlConfig.GET_TRAINEE_LIST, mCoach.id);
                mNetCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.code == BaseResponseParser.ERROR_CODE_NONE) {
                            GetTraineeListRE re = JSON.parseObject(result.result, GetTraineeListRE.class);
                            if (re.getTrainees() != null) {
                                mTraineeList.clear();
                                for (GetTraineeListRE.TraineesBean trainee : re.getTrainees()) {
                                    mTraineeList.add(new SelectTraineeEntity(trainee, false));
                                }
                            }
                            sendLocalMsg(MSG_POST_OK, null);
                        } else {
                            sendLocalMsg(MSG_POST_ERROR, result.msg);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        sendLocalMsg(MSG_POST_ERROR, HttpExceptionHelper.getErrorMsg(ex));
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }
        });
    }

    /**
     * 检查已选中的学员
     */
    private void checkTraineeList(){
        List<GetTraineeListRE.TraineesBean> mSelectList = new ArrayList<>();
        for (SelectTraineeEntity entity : mTraineeList){
            if (entity.select){
                mSelectList.add(entity.trainee);
            }
        }
        //若没选学员
        if (mSelectList.size() == 0){
            Toasty.error(SelectTraineeListActivity.this,"请选择需要同步的学员").show();
            return;
        }
        //检查蓝牙
        if (!mBluetoothState){
            Toasty.error(SelectTraineeListActivity.this,"请检查蓝牙是否打开").show();
            return;
        }
        //检查网络
        if (!mNetworkState){
            Toasty.error(SelectTraineeListActivity.this,"请检查网络").show();
            return;
        }
        //准备开始同步
        Bundle bundle = new Bundle();
        bundle.putSerializable("trainee", (Serializable) mSelectList);
        startActivity(SyncActivity.class,bundle);
        finish();
    }

    /**
     * 发送消息
     *
     * @param what
     * @param info
     */
    protected void sendLocalMsg(final int what, final String info) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = info;
        mLocalHandler.sendMessage(msg);
    }


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        myReceiver = new ConnectionChangeReceiver();
       registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(myReceiver);
    }

    /**
     * @author Javen
     */
    public class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            boolean bluetoothState = (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
            boolean networkState = NetworkU.isNetworkConnected(context);
            if (bluetoothState && networkState) {
                mBluetoothState = true;
                mNetworkState = true;
                return;
            }
            if (!networkState) {
                mNetworkState = false;
                return;
            }

            if (!bluetoothState) {
                mBluetoothState = false;
                return;
            }
        }
    }

}
