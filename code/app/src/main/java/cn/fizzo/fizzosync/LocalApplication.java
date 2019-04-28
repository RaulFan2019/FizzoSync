package cn.fizzo.fizzosync;

import android.app.Application;
import android.content.Context;

import org.xutils.x;

import cn.fizzo.watch.Fw;

/**
 * Created by Raul.fan on 2017/6/26 0026.
 */

public class LocalApplication extends Application{

    public static Context applicationContext;//整个APP的上下文
    private static LocalApplication instance;//Application 对象

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        x.Ext.init(this);

        Fw.getManager().init(this);
        Fw.getManager().setDebug(true);
    }

    /**
     * 获取 LocalApplication
     *
     * @return
     */
    public static LocalApplication getInstance() {
        if (instance == null) {
            instance = new LocalApplication();
        }

        return instance;
    }
}
