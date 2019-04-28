package cn.fizzo.fizzosync.network;

import android.content.Context;


import org.xutils.http.RequestParams;

import cn.fizzo.fizzosync.config.MyBuildConfig;
import cn.fizzo.fizzosync.config.UrlConfig;

/**
 * Created by Administrator on 2017/5/16.
 * 创建网络交互的参数
 */
public class RequestParamsBuilder {



    /**
     * 获取站点下的订单列表
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetDeviceListRP(final Context context, final String url, final int pkgId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("hrpackageid", pkgId + "");
        return requestParams;
    }


    /**
     * 获取站点下的订单列表
     *
     * @param context
     * @param url
     * @return
     */
    public static RequestParams buildGetCoachListRP(final Context context, final String url, final int storeid) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("storeid", storeid + "");
        return requestParams;
    }


    /**
     * 获取学员列表
     *
     * @param context
     * @param url
     * @param coachId
     * @return
     */
    public static RequestParams buildGetTraineeListRP(final Context context, final String url, final int coachId) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("coachid", coachId + "");
        return requestParams;
    }


    /**
     * 上传心率数据
     * @param context
     * @param url
     * @param startTime
     * @param minutes
     * @param hr_device_mac
     * @param avg_bpms
     * @param avg_efforts
     * @param hr_zones
     * @param effort_points
     * @param calories
     * @param bpms
     * @return
     */
    public static RequestParams buildUploadMinuteEffortArray(final Context context, final String url, final String startTime,
                                                             final int minutes, final String hr_device_mac, final String avg_bpms,
                                                             final String avg_efforts, final String hr_zones, final String effort_points,
                                                             final String calories, final String bpms) {
        MyRequestParams requestParams = new MyRequestParams(context, url);
        requestParams.addBodyParameter("resource", 7 + "");
        requestParams.addBodyParameter("starttime", startTime);
        requestParams.addBodyParameter("minutes", minutes + "");
        requestParams.addBodyParameter("hr_device_mac", hr_device_mac);
        requestParams.addBodyParameter("avg_bpms", avg_bpms);
        requestParams.addBodyParameter("avg_efforts", avg_efforts);
        requestParams.addBodyParameter("hr_zones", hr_zones);
        requestParams.addBodyParameter("effort_points", effort_points);
        requestParams.addBodyParameter("calories", calories);
        requestParams.addBodyParameter("bpms", bpms);
        return requestParams;
    }
}

