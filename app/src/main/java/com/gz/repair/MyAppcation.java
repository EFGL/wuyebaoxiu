package com.gz.repair;

import android.content.Context;

import com.gz.repair.bean.Login;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Endeavor on 2016/8/26.
 */
public class MyAppcation extends DaemonApplication {

    public static String baseUrl = "http://ceshi.sx-soft.net/android_oa";
    public static int userId = -1 ;
    public static int rootId = -1 ;
    public static String userName  ;
//    public static boolean isSuccess = true  ;
    public static String clientid   ;
    public static ArrayList<Login.Result.Privileges>  pro= new ArrayList<Login.Result.Privileges>();
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
    }

    // 以下为守护进程
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.marswin89.marsdaemon.demo:process1",
                Service1.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.marswin89.marsdaemon.demo:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
}
