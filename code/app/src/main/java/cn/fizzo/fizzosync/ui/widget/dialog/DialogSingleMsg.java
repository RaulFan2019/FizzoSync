package cn.fizzo.fizzosync.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import cn.fizzo.fizzosync.R;

/**
 * Created by Raul.fan on 2017/6/18 0018.
 *  简单消息的对话框
 */
public class DialogSingleMsg {

    public Dialog mDialog;//对话框

    TextView tvTitle;
    TextView tvMsg;


    /**
     * 初始化
     * @param context
     */
    public DialogSingleMsg(Context context){
        mDialog = new Dialog(context, R.style.DialogTheme);
        mDialog.setContentView(R.layout.dlg_msg);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);

        tvTitle = (TextView) mDialog.findViewById(R.id.tv_title);
        tvMsg = (TextView) mDialog.findViewById(R.id.tv_msg);
    }


    /**
     * 显示
     * @param title
     * @param msg
     */
    public void show(final String title, final String msg){
        tvMsg.setText(msg);
        tvTitle.setText(title);

        mDialog.show();
    }


    /**
     * 销毁
     */
    public void destroy(){
        if (mDialog != null){
            mDialog.dismiss();
        }
    }
}
