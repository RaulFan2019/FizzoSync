package cn.fizzo.fizzosync.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.entity.adapter.SelectTraineeEntity;
import cn.fizzo.fizzosync.entity.net.GetCoachListRE;
import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;
import cn.fizzo.fizzosync.ui.widget.circular.CircularImage;
import cn.fizzo.fizzosync.utils.ImageU;
import cn.fizzo.fizzosync.utils.TimeU;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class SelectTraineeListAdapter extends BaseAdapter{


    private Context mContext;
    private LayoutInflater mInflater;
    private List<SelectTraineeEntity> mData;

    public SelectTraineeListAdapter(Context context, List<SelectTraineeEntity> mList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = mList;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TraineeVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_select_trainee, null);
            holder = new TraineeVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TraineeVH) convertView.getTag();
        }

        SelectTraineeEntity traineeRE = mData.get(position);

        holder.tvName.setText(traineeRE.trainee.nickname);
        holder.tvWatchMac.setText(traineeRE.trainee.antplus_serialno);
        if (traineeRE.trainee.fizzosync_lasttime.equals("")
                || !TimeU.isToday(traineeRE.trainee.fizzosync_lasttime,TimeU.FORMAT_TYPE_1)){
            holder.tvLastTime.setText("今日尚未同步");
        }else {
            holder.tvLastTime.setText("同步到 " + TimeU.formatDateToStr(TimeU.formatStrToDate(traineeRE.trainee.fizzosync_lasttime,TimeU.FORMAT_TYPE_1),TimeU.FORMAT_TYPE_9));
        }
        ImageU.loadTraineeAvatar(traineeRE.trainee.avatar,traineeRE.trainee.gender,holder.ivAvatar);
        if (traineeRE.select){
            holder.vSelect.setBackgroundResource(R.drawable.ic_select_yes);
        }else {
            holder.vSelect.setBackgroundResource(R.drawable.ic_select_no);
        }
        return convertView;
    }

    class TraineeVH {
        CircularImage ivAvatar;//头像
        TextView tvName;//姓名
        TextView tvWatchMac;//手表地址
        TextView tvLastTime;//上次同步时间
        View vSelect;//选择状态

        public TraineeVH(View convertView) {
            ivAvatar = (CircularImage) convertView.findViewById(R.id.iv_avatar);
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvWatchMac = (TextView)convertView.findViewById(R.id.tv_watch_mac);
            tvLastTime = (TextView) convertView.findViewById(R.id.tv_sync_state);
            vSelect = convertView.findViewById(R.id.v_select_state);
        }
    }
}
