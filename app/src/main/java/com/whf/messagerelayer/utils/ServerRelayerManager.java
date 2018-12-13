package com.whf.messagerelayer.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by WHF on 2017/3/26.
 */

public class ServerRelayerManager {


    /**
     * 发送短信至目标手机号
     *
     * @param dataManager
     * @param content     短信内容
     */
    public static void relaySms(NativeDataManager dataManager, String content) {
        String server = dataManager.getObjectServer();
        HttpUrl httpUrl = RetrofitUrlManager.getInstance().getGlobalDomain();
        if (null == httpUrl || !httpUrl.toString().equals(server)) {
            RetrofitUrlManager.getInstance().setGlobalDomain(server);
        }

        JSONObject json = getJsonForContent(content);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Log.w(server, json.toString());
        NetWorkManager
                .getInstance()
                .getApi()
                .getResult(requestBody)
                .compose(getDefaultTransformer())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("ServerRelayerManager", "onSubscribe");
                    }

                    @Override
                    public void onNext(ResponseBody o) {
                        Log.i("ServerRelayerManager", "onNext");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ServerRelayerManager", e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.i("ServerRelayerManager", "onComplete");

                    }
                });
    }

    /**
     * 发送短信至目标手机号
     *
     * @param dataManager
     * @param json        短信内容,json format
     */
    public static void relaySms(NativeDataManager dataManager, JSONObject json) {
        String server = dataManager.getObjectServer();
        HttpUrl httpUrl = RetrofitUrlManager.getInstance().getGlobalDomain();
        if (null == httpUrl || !httpUrl.toString().equals(server)) {
            RetrofitUrlManager.getInstance().setGlobalDomain(server);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        Log.w(server, json.toString());
        NetWorkManager
                .getInstance()
                .getApi()
                .getResult(requestBody)
                .compose(getDefaultTransformer())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i("ServerRelayerManager", "onSubscribe");
                    }

                    @Override
                    public void onNext(ResponseBody o) {
                        Log.i("ServerRelayerManager", "onNext");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ServerRelayerManager", e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.i("ServerRelayerManager", "onComplete");

                    }
                });
    }


    public static JSONObject getJsonForContent(String content) {
        JSONObject root = new JSONObject();
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("sms", content);
            root.put("data", requestData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    private static ObservableTransformer<ResponseBody, ResponseBody> getDefaultTransformer() {
        return new ObservableTransformer<ResponseBody, ResponseBody>() {
            @Override
            public ObservableSource<ResponseBody> apply(Observable<ResponseBody> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doAfterTerminate(new Action() {
                            @Override
                            public void run() throws Exception {
                            }
                        });
            }
        };
    }

}
