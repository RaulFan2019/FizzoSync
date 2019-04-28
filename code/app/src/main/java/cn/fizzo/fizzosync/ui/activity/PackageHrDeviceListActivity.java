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
import cn.fizzo.fizzosync.entity.net.GetDeviceListRE;
import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;
import cn.fizzo.fizzosync.network.BaseResponseParser;
import cn.fizzo.fizzosync.network.HttpExceptionHelper;
import cn.fizzo.fizzosync.network.RequestParamsBuilder;
import cn.fizzo.fizzosync.ui.adapter.DeviceListAdapter;
import cn.fizzo.fizzosync.ui.widget.common.MyLoadingView;
import cn.fizzo.fizzosync.ui.widget.pulltorefresh.PullToRefreshLayout;
import cn.fizzo.fizzosync.ui.widget.pulltorefresh.PullableListView;
import cn.fizzo.fizzosync.ui.widget.toast.Toasty;
import cn.fizzo.fizzosync.utils.Log;
import cn.fizzo.fizzosync.utils.NetworkU;

/**
 * @author Raul.Fan
 * @email 35686324@qq.com
 * @date 2018/9/12 13:55
 */
public class PackageHrDeviceListActivity extends BaseActivity {


    private static final String TAG = "PackageHrDeviceListActivity";

    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;

    @BindView(R.id.list)
    PullableListView list;//列表
    @BindView(R.id.ptr)
    PullToRefreshLayout ptr;//刷新控件

    @BindView(R.id.v_loading)
    MyLoadingView vLoading;//缓冲


    private DeviceListAdapter mAdapter;
    private List<GetDeviceListRE.HrdevicesBean> mDeviceList = new ArrayList<>();

    private boolean mIsFirstIn = true;


    private ConnectionChangeReceiver myReceiver;
    private boolean mBluetoothState = true;
    private boolean mNetworkState = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_list;
    }


    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POST_ERROR:
                    Toasty.error(PackageHrDeviceListActivity.this, (String) msg.obj).show();
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



    @OnClick({R.id.btn_back, R.id.btn_sync, R.id.btn_all_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_sync:
                //检查蓝牙
                if (!mBluetoothState){
                    Toasty.error(PackageHrDeviceListActivity.this,"请检查蓝牙是否打开").show();
                    return;
                }
                //检查网络
                if (!mNetworkState){
                    Toasty.error(PackageHrDeviceListActivity.this,"请检查网络").show();
                    return;
                }
                List<GetDeviceListRE.HrdevicesBean> mSelectList = new ArrayList<>();
                for (GetDeviceListRE.HrdevicesBean entity : mDeviceList){
                    if (entity.select){
                        mSelectList.add(entity);
                    }
                }

                //准备开始同步
                Bundle bundle = new Bundle();
                bundle.putSerializable("device", (Serializable) mSelectList);
                startActivity(PkgSyncActivity.class,bundle);
                finish();
                break;
            case R.id.btn_all_select:
                for (GetDeviceListRE.HrdevicesBean entity : mDeviceList){
                    entity.select = true;
                }
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        vLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postGetDeviceList();
            }
        });

        ptr.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                postGetDeviceList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ptr.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });

        mAdapter = new DeviceListAdapter(PackageHrDeviceListActivity.this, mDeviceList);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDeviceList.get(i).select = !mDeviceList.get(i).select;
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetDeviceList();
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

    }


    private void postGetDeviceList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetDeviceListRP(PackageHrDeviceListActivity.this, UrlConfig.GET_DEVICE_LIST, 17);
                mNetCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.code == BaseResponseParser.ERROR_CODE_NONE) {
                            Log.e(TAG, "result.result:" + result.result);
                            GetDeviceListRE re = JSON.parseObject(result.result, GetDeviceListRE.class);
                            if (re.hrdevices != null) {
                                mDeviceList.clear();
                                mDeviceList.addAll(re.hrdevices);
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
