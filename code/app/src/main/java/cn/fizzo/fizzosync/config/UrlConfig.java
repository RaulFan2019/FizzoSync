package cn.fizzo.fizzosync.config;

/**
 * Created by Raul.Fan on 2017/5/16.
 */

public class UrlConfig {

    /**
     * HTTP 头信息
     */
    public static final String USER_ANGENT = "Android " + android.os.Build.VERSION.RELEASE + ";"
            + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL + ";" + MyBuildConfig.Version;


    /**
     * 根据编译版本获取Host Ip 地址
     *
     * @return
     */
    public static String getHostIp() {
        return "http://www.123yd.cn/fitness/V2/";
    }


    //获取教练列表
    public static final String GET_COACH_LIST = getHostIp()+ "Club/getStoreCoachList";

    //获取学员列表
    public static final String GET_TRAINEE_LIST = getHostIp()+ "Club/getCoachTraineeList";

    //上传心率数据
    public static final String UPLOAD_MINUTE_EFFORT_ARRAY = getHostIp() + "Effort/uploadMinuteEffortArray";


    public static final String GET_DEVICE_LIST = getHostIp() + "School/getHrpackageHrdeviceList";
}
