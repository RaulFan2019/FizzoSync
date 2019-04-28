package cn.fizzo.fizzosync.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.entity.adapter.SyncTraineeEntity;
import cn.fizzo.fizzosync.ui.widget.circular.CircularImage;
import cn.fizzo.fizzosync.utils.ImageU;

/**
 * Created by Raul.fan on 2017/6/27 0027.
 */

public class SyncTraineeListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SyncTraineeEntity> mData;

    public SyncTraineeListAdapter(Context context, List<SyncTraineeEntity> mList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mList;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SyncTraineeVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_sync_trainee, null);
            holder = new SyncTraineeVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SyncTraineeVH) convertView.getTag();
        }

        SyncTraineeEntity traineeRE = mData.get(position);

        holder.tvName.setText(traineeRE.trainee.nickname);
        holder.tvWatchMac.setText(traineeRE.trainee.antplus_serialno);
        ImageU.loadTraineeAvatar(traineeRE.trainee.avatar,traineeRE.trainee.gender,holder.ivAvatar);

        if (traineeRE.syncState == SyncTraineeEntity.SYNC_NONE){
            holder.tvSyncState.setText("等待同步");
            holder.vWarning.setVisibility(View.INVISIBLE);
            holder.tvSyncState.setTextColor(Color.parseColor("#777777"));
        }else {
            holder.tvSyncState.setText("同步失败");
            holder.vWarning.setVisibility(View.VISIBLE);
            holder.tvSyncState.setTextColor(Color.parseColor("#ff4612"));
        }
        return convertView;
    }

    class SyncTraineeVH {
        CircularImage ivAvatar;//头像
        TextView tvName;//姓名
        TextView tvWatchMac;//手表地址
        TextView tvSyncState;//状态文本
        View vWarning;//选择状态

        public SyncTraineeVH(View convertView) {
            ivAvatar = (CircularImage) convertView.findViewById(R.id.iv_avatar);
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvWatchMac = (TextView)convertView.findViewById(R.id.tv_watch_mac);
            tvSyncState = (TextView) convertView.findViewById(R.id.tv_sync_state);
            vWarning = convertView.findViewById(R.id.v_sync_state);
        }
    }
}
