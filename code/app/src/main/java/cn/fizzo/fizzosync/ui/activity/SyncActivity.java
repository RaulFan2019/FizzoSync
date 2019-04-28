package cn.fizzo.fizzosync.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.config.UrlConfig;
import cn.fizzo.fizzosync.entity.adapter.SyncTraineeEntity;
import cn.fizzo.fizzosync.entity.net.BaseRE;
import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;
import cn.fizzo.fizzosync.network.BaseResponseParser;
import cn.fizzo.fizzosync.network.HttpExceptionHelper;
import cn.fizzo.fizzosync.network.RequestParamsBuilder;
import cn.fizzo.fizzosync.ui.adapter.SyncTraineeListAdapter;
import cn.fizzo.fizzosync.ui.widget.circular.CircularImage;
import cn.fizzo.fizzosync.ui.widget.dialog.DialogBuilder;
import cn.fizzo.fizzosync.utils.CalorieU;
import cn.fizzo.fizzosync.utils.ImageU;
import cn.fizzo.fizzosync.utils.Log;
import cn.fizzo.fizzosync.utils.TimeU;
import cn.fizzo.fizzosync.utils.ZoneU;
import cn.fizzo.watch.Fw;
import cn.fizzo.watch.array.ConnectStates;
import cn.fizzo.watch.entity.SyncHrDataEntity;
import cn.fizzo.watch.entity.SyncHrDataProgressEntity;
import cn.fizzo.watch.entity.SyncHrItemEntity;
import cn.fizzo.watch.observer.ConnectListener;
import cn.fizzo.watch.observer.SyncHrDataListener;

/**
 * Created by Raul.fan on 2017/6/27 0027.
 */

public class SyncActivity extends BaseActivity implements ConnectListener, SyncHrDataListener {


    private static final String TAG = "SyncActivity";

    public static final int MSG_ADD_CONNECT_TIMER = 0x01;//连接计时+1
    public static final int MSG_POST_ERROR = 0x02;//上传错误
    public static final int MSG_POST_OK = 0x03;//上传成功
    public static final int MSG_ADD_SYNC_TIMER = 0x04;


    public static final int TIME_OUT_CONNECT_TIMER = 20;//连接超时时间
    public static final int TIME_OUT_SYNC_TIMER = 30;//同步超时

    @BindView(R.id.iv_curr_avatar)
    CircularImage ivCurrAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_device)
    TextView tvDevice;
    @BindView(R.id.v_anim)
    View vAnim;
    @BindView(R.id.tv_sync_state)
    TextView tvSyncState;
    @BindView(R.id.tv_un_finish_count)
    TextView tvUnFinishCount;
    @BindView(R.id.list)
    ListView list;


    /* local data */
    List<SyncTraineeEntity> mSyncList = new ArrayList<>();

    private SyncTraineeListAdapter mAdapter;
    private SyncTraineeEntity mCurrSyncEntity;
    private long mCurrSyncStartTime;

    private int mConnectTimer = 0;
    private int mSyncTimer = 0;

    private RotateAnimation mRotateAnim;
    private DialogBuilder mDialogBuilder;

    private boolean mIsFirstSync = true;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_sync;
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //连接时间++
                case MSG_ADD_CONNECT_TIMER:
                    mConnectTimer++;
                    //连接超时
                    if (mConnectTimer > TIME_OUT_CONNECT_TIMER) {
                        mConnectTimer = 0;
                        mHandler.removeMessages(MSG_ADD_CONNECT_TIMER);
                        findNextSyncTrainee(false);
                    } else {
                        mHandler.sendEmptyMessageDelayed(MSG_ADD_CONNECT_TIMER, 1000);
                    }
                    break;
                //发送历史数据成功
                case MSG_POST_OK:
                    Fw.getManager().deleteOneHrHistory(mCurrSyncStartTime);
                    mSyncTimer = 0;
                    mHandler.sendEmptyMessage(MSG_ADD_SYNC_TIMER);
                    break;
                //上传历史数据失败
                case MSG_POST_ERROR:
                    findNextSyncTrainee(false);
                    break;
                //同步时间
                case MSG_ADD_SYNC_TIMER:
//                    Log.v(TAG,"mSyncTimer:" + mSyncTimer);
                    mSyncTimer++;
                    if (mSyncTimer > TIME_OUT_SYNC_TIMER) {
                        mSyncTimer = 0;
                        mHandler.removeMessages(MSG_ADD_SYNC_TIMER);
                        findNextSyncTrainee(false);
                    } else {
                        mHandler.sendEmptyMessageDelayed(MSG_ADD_SYNC_TIMER, 1000);
                    }
                    break;
            }
        }
    };

    /**
     * 蓝牙连接状态发生改变
     *
     * @param state
     */
    @Override
    public void connectStateChange(int state) {
        switch (state) {
            //连接中
            case ConnectStates.CONNECTING:
                tvSyncState.setText("连接中..");
                break;
            //失去连接
            case ConnectStates.DISCONNECT:
                tvSyncState.setText("断开连接");
                if (mConnectTimer == 0) {
                    mHandler.sendEmptyMessageDelayed(MSG_ADD_CONNECT_TIMER, 1000);
                }
                break;
            //连接失败
            case ConnectStates.CONNECT_FAIL:
                tvSyncState.setText("连接失败");
                break;
            //已连接
            case ConnectStates.CONNECTED:
                tvSyncState.setText("连接成功");
                break;
            //已经正常工作
            case ConnectStates.NOTIFY_PRIVATE_SERVICE_OK:
                tvSyncState.setText("准备同步");
                Fw.getManager().syncHrData();
                mConnectTimer = 0;
                mHandler.removeMessages(MSG_ADD_CONNECT_TIMER);
                mHandler.sendEmptyMessage(MSG_ADD_SYNC_TIMER);
                break;
        }
    }


    /**
     * 同步状态发生变化
     *
     * @param entity
     */
    @Override
    public void syncProgressChange(SyncHrDataProgressEntity entity) {
        tvSyncState.setText("正在同步 " + entity.HistoryFinishSize + "/" + entity.HistorySize + " ...");
    }

    /**
     * 同步完成
     *
     * @param state
     */
    @Override
    public void syncFinish(int state) {
        findNextSyncTrainee(true);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 同步一条历史完成
     *
     * @param entity
     */
    @Override
    public void completeOneHrHistory(SyncHrDataEntity entity) {
        mHandler.removeMessages(MSG_ADD_SYNC_TIMER);
        postHrHistoryData(entity);
    }

    @Override
    public void onBackPressed() {
    }

    @OnClick(R.id.btn_stop)
    public void onViewClicked() {
        Fw.getManager().disConnectDevice();
        finish();
    }

    @Override
    protected void initData() {
        mRotateAnim = (RotateAnimation) AnimationUtils.loadAnimation(SyncActivity.this, R.anim.rotating);
        List<GetTraineeListRE.TraineesBean> mSelectList = (List<GetTraineeListRE.TraineesBean>)
                getIntent().getExtras().getSerializable("trainee");
        mDialogBuilder = new DialogBuilder();
        for (GetTraineeListRE.TraineesBean trainee : mSelectList) {
            mSyncList.add(new SyncTraineeEntity(trainee, SyncTraineeEntity.SYNC_NONE));
        }
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        mAdapter = new SyncTraineeListAdapter(SyncActivity.this, mSyncList);
        list.setAdapter(mAdapter);
        vAnim.setAnimation(mRotateAnim);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SyncTraineeEntity syncTraineeEntity = mSyncList.get(i);
                if (syncTraineeEntity.syncState == SyncTraineeEntity.SYNC_FAIL) {
                    showSyncFailDialog();
                }
            }
        });
    }

    @Override
    protected void doMyCreate() {
        Fw.getManager().registerConnectListener(this);
        Fw.getManager().registerSyncHrDataListener(this);
        startSync(mSyncList.get(0), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //若用户在这个页面 ，希望保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void causeGC() {
        Fw.getManager().unRegisterConnectListener(this);
        Fw.getManager().unRegisterSyncHrDataListener(this);
        Fw.getManager().disConnectDevice();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * 开始同步一个学员
     *
     * @param syncTraineeEntity
     */
    private void startSync(final SyncTraineeEntity syncTraineeEntity, boolean lastFinish) {
        if (lastFinish) {
            mSyncList.remove(mCurrSyncEntity);
            if (mSyncList.size() == 0) {
                startActivity(SyncCompleteActivity.class);
                finish();
            }
            return;
        }
        mCurrSyncEntity = syncTraineeEntity;
        ImageU.loadTraineeAvatar(mCurrSyncEntity.trainee.avatar, mCurrSyncEntity.trainee.gender, ivCurrAvatar);
        tvDevice.setText(mCurrSyncEntity.trainee.antplus_serialno);
        tvName.setText(mCurrSyncEntity.trainee.nickname);
        tvSyncState.setText("正在连接");
        tvUnFinishCount.setText("未完成（" + mSyncList.size() + ")");
        Fw.getManager().addNewConnect(syncTraineeEntity.trainee.mac_addr);
        if (mIsFirstSync) {
            Fw.getManager().recovery();
            mIsFirstSync = false;
        }
        mHandler.sendEmptyMessageDelayed(MSG_ADD_CONNECT_TIMER, 1000);
    }


    /**
     * 准备上传心率历史记录
     *
     * @param entity
     */
    private void postHrHistoryData(final SyncHrDataEntity entity) {
        //检查记录是否合理
        if (entity.itemEntities == null || entity.itemEntities.size() == 0) {
            Fw.getManager().deleteOneHrHistory(mCurrSyncStartTime);
            return;
        }
        String avg_bpms = "[";
        String avg_efforts = "[";
        String hr_zones = "[";
        String effort_points = "[";
        String calories = "[";
        String bpms = "[";

        int splitId = 0;
        int splitHrTotal = 0;
        int splitHrSize = 0;
        mCurrSyncStartTime = entity.startTime;
        for (SyncHrItemEntity item : entity.itemEntities) {
            //若是一个新的split
            if (item.timeOffSet / 60 > splitId) {
                int avgHeartbeat = 0;
                if (splitHrSize != 0) {
                    avgHeartbeat = splitHrTotal / splitHrSize;
                }
                splitHrSize = 0;
                splitHrTotal = 0;
                for (int j = 0, sizeJ = ((int) item.timeOffSet / 60) - splitId; j < sizeJ; j++) {
                    int percent = avgHeartbeat * 100 / mCurrSyncEntity.trainee.max_hr;

                    avg_bpms += avgHeartbeat + ",";
                    avg_efforts += percent + ",";
                    hr_zones += ZoneU.getZone(percent) + ",";
                    effort_points += ZoneU.getMinutesEffortPoint(avgHeartbeat, mCurrSyncEntity.trainee.max_hr) + ",";
                    calories += CalorieU.getMinutesCalorie(mCurrSyncEntity.trainee, avgHeartbeat) + ",";
                    splitId++;
                }
            }
            splitHrSize++;
            splitHrTotal += item.bmp;
            bpms += "[" + item.timeOffSet + "," + item.bmp + "," + item.reserved + "],";
        }
        int avgHeartbeat = splitHrTotal / splitHrSize;
        int percent = avgHeartbeat * 100 / mCurrSyncEntity.trainee.max_hr;
        avg_bpms += avgHeartbeat + ",";
        avg_efforts += percent + ",";
        hr_zones += ZoneU.getZone(percent) + ",";
        effort_points += ZoneU.getMinutesEffortPoint(avgHeartbeat, mCurrSyncEntity.trainee.max_hr) + ",";
        calories += CalorieU.getMinutesCalorie(mCurrSyncEntity.trainee, avgHeartbeat) + ",";

        avg_bpms = avg_bpms.substring(0, avg_bpms.length() - 1);
        avg_efforts = avg_efforts.substring(0, avg_efforts.length() - 1);
        hr_zones = hr_zones.substring(0, hr_zones.length() - 1);
        effort_points = effort_points.substring(0, effort_points.length() - 1);
        calories = calories.substring(0, calories.length() - 1);
        bpms = bpms.substring(0, bpms.length() - 1);

        final String upload_avg_bpms = avg_bpms + "]";
        final String upload_avg_efforts = avg_efforts + "]";
        final String upload_hr_zones = hr_zones + "]";
        final String upload_effort_points = effort_points + "]";
        final String upload_calories = calories + "]";
        final String upload_bpms = bpms + "]";
        final int minutes = splitId;

        //记录的时长不符合常理
        if (minutes > 24 * 60) {
            Fw.getManager().deleteOneHrHistory(mCurrSyncStartTime);
            return;
        }

        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildUploadMinuteEffortArray(SyncActivity.this, UrlConfig.UPLOAD_MINUTE_EFFORT_ARRAY,
                        TimeU.formatDateToStr(new Date(entity.startTime * 1000), TimeU.FORMAT_TYPE_1), minutes, mCurrSyncEntity.trainee.mac_addr,
                        upload_avg_bpms, upload_avg_efforts, upload_hr_zones, upload_effort_points, upload_calories, upload_bpms);
                x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        //若成功
                        if (result.code == BaseResponseParser.ERROR_CODE_NONE) {
                            mHandler.sendEmptyMessage(MSG_POST_OK);
                        } else {
                            mHandler.sendEmptyMessage(MSG_POST_ERROR);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        mHandler.sendEmptyMessage(MSG_POST_ERROR);
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
     * 查找下一个同步学员
     */
    private void findNextSyncTrainee(boolean lastFinish) {
        int index = -1;
        for (int i = 0, size = mSyncList.size(); i < size; i++) {
            if (mSyncList.get(i).trainee.mac_addr.equals(mCurrSyncEntity.trainee.mac_addr)) {
                if (!lastFinish) {
                    SyncTraineeEntity entity = mSyncList.get(i);
                    entity.syncState = SyncTraineeEntity.SYNC_FAIL;
                    mSyncList.set(i, entity);
                    mAdapter.notifyDataSetChanged();
                }
                index = i;
            }
        }
        if (index == -1 || index == mSyncList.size() - 1) {
            index = 0;
        } else {
            index++;
        }
        Fw.getManager().disConnectDevice();
        if (mSyncList.size() == 0) {
            startActivity(SyncCompleteActivity.class);
            finish();
            return;
        }
        startSync(mSyncList.get(index), lastFinish);
    }

    /**
     * 显示同步失败对话框
     */
    private void showSyncFailDialog() {
        mDialogBuilder.showSingleMsgDialog(SyncActivity.this, "同步失败!", "请确认：\n\n" +
                "1. 手环已打开或处于充电状态\n\n" +
                "2. 手环需尽量靠近手机");
    }
}
