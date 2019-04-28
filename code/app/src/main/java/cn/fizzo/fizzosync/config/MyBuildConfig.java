package cn.fizzo.fizzosync.config;

/**
 * Created by Raul.Fan on 2016/11/10.
 */

public class MyBuildConfig {


    /**
     * App 的版本信息
     * 用于发送给服务器版本信息
     */
    public static final String Version = "Sync Ver.1.0";


    /**
     *  app版本号
     */
    public static final int appVersionCode = 1;

    /**
     * 协议版本号(用于控制强制升级)
     */
    public static final int agreementVersionCode = 1;


    /**
     * 数据库版本号
     */
    public static final int DB_VERSION = 1;



    /**
     * 数据库名称
     */
    public static final String DB_NAME = "Sync.db";

    /**
     * 是否是DEBUG
     */
    public static final boolean DEBUG = true;

}
