package cn.fizzo.fizzosync.utils;

import android.widget.ImageView;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import cn.fizzo.fizzosync.R;
import cn.fizzo.fizzosync.config.UserEnum;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class ImageU {

    /**
     * 缓冲用户头像图片
     *
     * @param avatar
     * @param imageView
     */
    public static void loadCoachAvatar(final String avatar, final int gender ,final ImageView imageView) {
        ImageOptions imageOptions;

        if (gender == UserEnum.GENDER_MAN){
            imageOptions = new ImageOptions.Builder()
                    .setCrop(true)
                    .setLoadingDrawableId(R.drawable.ic_defualt_coach_man)
                    .setFailureDrawableId(R.drawable.ic_defualt_coach_man)
                    .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .build();
        }else {
            imageOptions = new ImageOptions.Builder()
                    .setCrop(true)
                    .setLoadingDrawableId(R.drawable.ic_default_coach_women)
                    .setFailureDrawableId(R.drawable.ic_default_coach_women)
                    .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .build();
        }
        if (avatar != null && !avatar.startsWith("http://wx.qlogo.cn/") && !avatar.equals("")) {
            x.image().bind(imageView, avatar + "?imageView2/1/w/200/h/200", imageOptions);
        } else {
            x.image().bind(imageView, avatar, imageOptions);
        }
    }

    /**
     * 缓冲用户头像图片
     *
     * @param avatar
     * @param imageView
     */
    public static void loadTraineeAvatar(final String avatar, final int gender ,final ImageView imageView) {
        ImageOptions imageOptions;

        if (gender == UserEnum.GENDER_MAN){
            imageOptions = new ImageOptions.Builder()
                    .setCrop(true)
                    .setLoadingDrawableId(R.drawable.ic_default_trainee_man)
                    .setFailureDrawableId(R.drawable.ic_default_trainee_man)
                    .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .build();
        }else {
            imageOptions = new ImageOptions.Builder()
                    .setCrop(true)
                    .setLoadingDrawableId(R.drawable.ic_default_trainee_women)
                    .setFailureDrawableId(R.drawable.ic_default_trainee_women)
                    .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .build();
        }
        if (avatar != null && !avatar.startsWith("http://wx.qlogo.cn/") && !avatar.equals("")) {
            x.image().bind(imageView, avatar + "?imageView2/1/w/200/h/200", imageOptions);
        } else {
            x.image().bind(imageView, avatar, imageOptions);
        }
    }
}
