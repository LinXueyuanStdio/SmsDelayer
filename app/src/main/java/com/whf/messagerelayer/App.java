package com.whf.messagerelayer;

import android.app.Application;
import android.content.Context;

import com.whf.messagerelayer.confing.Constant;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/12/13
 * @discription null
 * @usage null
 */
public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        RetrofitUrlManager.getInstance().setDebug(true);
        //将每个 BaseUrl 进行初始化,运行时可以随时改变 DOMAIN_NAME 对应的值,从而达到切换 BaseUrl 的效果
        RetrofitUrlManager.getInstance().putDomain(Constant.DOMAIN_NAME, Constant.APP_DEFAULT_DOMAIN);
    }

    public static Context getContext() {
        return context;
    }
}
