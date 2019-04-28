package cn.fizzo.fizzosync.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSON;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.config.UrlConfig;
import cn.fizzo.fizzosync.entity.net.BaseRE;
import cn.fizzo.fizzosync.entity.net.GetCoachListRE;
import cn.fizzo.fizzosync.network.BaseResponseParser;
import cn.fizzo.fizzosync.network.HttpExceptionHelper;
import cn.fizzo.fizzosync.network.RequestParamsBuilder;
import cn.fizzo.fizzosync.ui.adapter.SelectCoachListAdapter;
import cn.fizzo.fizzosync.ui.widget.common.MyLoadingView;
import cn.fizzo.fizzosync.ui.widget.pulltorefresh.PullToRefreshLayout;
import cn.fizzo.fizzosync.ui.widget.pulltorefresh.PullableListView;
import cn.fizzo.fizzosync.ui.widget.toast.Toasty;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class SelectCoachListActivity extends BaseActivity {

    private static final int MSG_POST_ERROR = 0x01;
    private static final int MSG_POST_OK = 0x02;

    @BindView(R.id.list)
    PullableListView list;//列表
    @BindView(R.id.ptr)
    PullToRefreshLayout ptr;//刷新控件

    @BindView(R.id.v_loading)
    MyLoadingView vLoading;//缓冲

    private SelectCoachListAdapter mAdapter;
    private List<GetCoachListRE.CoachesBean> mCoachList = new ArrayList<>();

    private boolean mIsFirstIn = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_coach;
    }

    Handler mLocalHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POST_ERROR:
                    Toasty.error(SelectCoachListActivity.this, (String) msg.obj).show();
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


    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        vLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postGetCoachList();
            }
        });

        ptr.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                postGetCoachList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                ptr.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });

        mAdapter = new SelectCoachListAdapter(SelectCoachListActivity.this, mCoachList);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("coach",mCoachList.get(i));
                startActivity(SelectTraineeListActivity.class,bundle);
            }
        });
    }

    @Override
    protected void doMyCreate() {
        postGetCoachList();
    }

    @Override
    protected void causeGC() {

    }

    /**
     * 获取订单列表
     */
    private void postGetCoachList() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                RequestParams params = RequestParamsBuilder.buildGetCoachListRP(SelectCoachListActivity.this, UrlConfig.GET_COACH_LIST, 7);
                mNetCancelable = x.http().post(params, new Callback.CommonCallback<BaseRE>() {
                    @Override
                    public void onSuccess(BaseRE result) {
                        if (result.code == BaseResponseParser.ERROR_CODE_NONE) {
                            GetCoachListRE re = JSON.parseObject(result.result, GetCoachListRE.class);
                            if (re.getCoaches() != null) {
                                mCoachList.clear();
                                mCoachList.addAll(re.getCoaches());
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
}
