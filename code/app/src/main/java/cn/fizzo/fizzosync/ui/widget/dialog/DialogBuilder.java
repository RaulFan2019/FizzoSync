package cn.fizzo.fizzosync.ui.widget.dialog;

import android.content.Context;

/**
 * Created by Raul.fan on 2017/6/18 0018.
 */

public class DialogBuilder {

    public DialogSingleMsg dialogSingleMsg;

    /**
     * 显示普通消息对话框
     *
     * @param context
     * @param title
     * @param msg
     */
    public void showSingleMsgDialog(final Context context, final String title, final String msg) {
        if (dialogSingleMsg == null) {
            dialogSingleMsg = new DialogSingleMsg(context);
        }
        dialogSingleMsg.show(title, msg);
    }
}
