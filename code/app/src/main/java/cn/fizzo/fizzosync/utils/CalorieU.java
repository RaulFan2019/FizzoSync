package cn.fizzo.fizzosync.utils;

import java.math.BigDecimal;

import cn.fizzo.fizzosync.config.UserEnum;
import cn.fizzo.fizzosync.entity.net.GetTraineeListRE;

/**
 * Created by Administrator on 2016/8/8.
 */
public class CalorieU {

    private static final String TAG = "CalorieU";

    /**
     * 计算一分钟消耗的卡路里
     *
     * @param avgHr
     * @return
     */
    public static float getCalorie(final int avgHr) {
        float coefficient;
        coefficient = (((float) avgHr - (float) 80) / ((float) 200 - (float) 80) * 9 + 1);

        return (coefficient > 0) ? (float) (coefficient * 35 * 3.5 / 200) : 0;
    }


    /**
     * 计算一分钟消耗的卡路里
     *
     * @param user
     * @param avgHr
     * @return
     */
    public static float getMinutesCalorie(final GetTraineeListRE.TraineesBean user, final int avgHr) {
//        Log.v(TAG, "user.restHr:" + user.restHr + "user.maxHr:" + user.maxHr + ",avgHr:" + avgHr + "user.weight:" + user.weight);
        float coefficient;
        if (user.gender == UserEnum.GENDER_MAN) {
            coefficient = (((float) avgHr - (float) user.rest_hr) / ((float) user.max_hr - (float) user.rest_hr) * 9 + 1);
        } else {
            coefficient = (((float) avgHr - (float) user.rest_hr) / ((float) user.max_hr - (float) user.rest_hr) * 9 + 1);
        }
//        Log.v(TAG, "cal:" + ((coefficient > 0) ? (float) (coefficient * user.weight * 3.5 / 200) : 0));
        return (coefficient > 0) ? (float) (coefficient * user.weight * 3.5 / 200) : 0;
    }

    /**
     * 获取显示的卡路里值
     *
     * @param calorie
     * @return
     */
    public static float getShowCalorie(final float calorie) {
        BigDecimal b = new BigDecimal(calorie);
        b.setScale(2, BigDecimal.ROUND_HALF_UP);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

}
