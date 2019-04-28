package cn.fizzo.fizzosync.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.entity.net.GetCoachListRE;
import cn.fizzo.fizzosync.ui.widget.circular.CircularImage;
import cn.fizzo.fizzosync.utils.ImageU;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class SelectCoachListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<GetCoachListRE.CoachesBean> mData;

    public SelectCoachListAdapter(Context context, List<GetCoachListRE.CoachesBean> mList) {
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
        CoachVH holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_select_coach, null);
            holder = new CoachVH(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CoachVH) convertView.getTag();
        }

        GetCoachListRE.CoachesBean coachRE = mData.get(position);

        holder.tvName.setText(coachRE.nickname);
        holder.tvStuCount.setText(coachRE.traineecount + "");

        ImageU.loadCoachAvatar(coachRE.avatar,coachRE.gender,holder.ivAvatar);
        return convertView;
    }

    class CoachVH {
        CircularImage ivAvatar;//头像
        TextView tvName;//姓名
        TextView tvStuCount;//学生数量

        public CoachVH(View convertView) {
            ivAvatar = (CircularImage) convertView.findViewById(R.id.iv_avatar);
            tvName = (TextView) convertView.findViewById(R.id.tv_name);
            tvStuCount = (TextView)convertView.findViewById(R.id.tv_stu_count);
        }
    }
}
