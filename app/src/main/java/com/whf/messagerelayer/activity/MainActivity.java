package com.whf.messagerelayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.whf.messagerelayer.R;
import com.whf.messagerelayer.bean.MessageBean;
import com.whf.messagerelayer.utils.MessageUtil;
import com.whf.messagerelayer.utils.NativeDataManager;
import com.whf.messagerelayer.utils.PermissionUtil;
import com.whf.messagerelayer.utils.ServerRelayerManager;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout mSmsLayout, mEmailLayout, mRuleLayout, mServerlayout;
    private NativeDataManager mNativeDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNativeDataManager = new NativeDataManager(this);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean isReceiver = mNativeDataManager.getReceiver();
        final MenuItem menuItem = menu.add("开关");
        if (isReceiver) {
            menuItem.setIcon(R.mipmap.ic_send_on);
        } else {
            menuItem.setIcon(R.mipmap.ic_send_off);
        }

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Boolean receiver = mNativeDataManager.getReceiver();
                if (receiver) {
                    mNativeDataManager.setReceiver(false);
                    menuItem.setIcon(R.mipmap.ic_send_off);
                    Toast.makeText(MainActivity.this, "总闸已关闭", Toast.LENGTH_SHORT).show();
                } else {
                    mNativeDataManager.setReceiver(true);
                    menuItem.setIcon(R.mipmap.ic_send_on);
                    Toast.makeText(MainActivity.this, "总闸已开启", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add("关于").setIcon(R.mipmap.ic_about)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        return false;
                    }
                }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        mSmsLayout = (RelativeLayout) findViewById(R.id.sms_relay_layout);
        mEmailLayout = (RelativeLayout) findViewById(R.id.email_relay_layout);
        mRuleLayout = (RelativeLayout) findViewById(R.id.rule_layout);
        mServerlayout = (RelativeLayout) findViewById(R.id.server_relay_layout);

        mSmsLayout.setOnClickListener(this);
        mEmailLayout.setOnClickListener(this);
        mRuleLayout.setOnClickListener(this);
        mServerlayout.setOnClickListener(this);

        mServerlayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean isReadMessage = checkPermission(Manifest.permission.READ_SMS);
                    if (isReadMessage) {
                        readMessage();
                    } else {
                        requestPerssion(new String[]{Manifest.permission.READ_SMS}, smsRequestCode);
                    }
                }
            }
        }, 1000);
    }
    List<MessageBean> smsInPhone = null;
    /**
     * 读取短信
     */
    private void readMessage() {
        //使用Observable.create()创建被观察者
        smsInPhone = MessageUtil.getSmsInPhone(0);
        Observable<MessageBean> observable1 = Observable.create(new ObservableOnSubscribe<MessageBean>() {
            @Override
            public void subscribe(ObservableEmitter<MessageBean> subscriber) throws Exception {
                if (smsInPhone == null || smsInPhone.size() <= 0) {
                    return;
                }

                for (MessageBean sms : smsInPhone) {
                    subscriber.onNext(sms);
                }
                subscriber.onComplete();
            }
        });
        //订阅
        observable1.subscribeOn(Schedulers.newThread())//指定 subscribe() 发生在新的线程
                .observeOn(Schedulers.io())// 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<MessageBean>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e("ServerRelayerManager", e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessageBean s) {
                        //请求成功
                        JSONObject json = ServerRelayerManager.getJsonForContent(new Gson().toJson(s));
                        ServerRelayerManager.relaySms(mNativeDataManager, json);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sms_relay_layout:
                startActivity(new Intent(this, SmsRelayerActivity.class));
                break;
            case R.id.email_relay_layout:
                startActivity(new Intent(this, EmailRelayerActivity.class));
                break;
            case R.id.server_relay_layout:
                startActivity(new Intent(this, ServerRelayerActivity.class));
                break;
            case R.id.rule_layout:
                startActivity(new Intent(this, RuleActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == smsRequestCode) {
            readMessage();
        }
    }

    private int smsRequestCode = 3;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (smsRequestCode == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //已经授权
                readMessage();
            } else {
                //点击了不再提示,拒绝权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    //跳转到设置界面
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, smsRequestCode);

                } else {
                    Toast.makeText(MainActivity.this, "权限拒绝", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /**
     * 检查权限
     *
     * @param permission
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermission(String permission) {
        boolean b = PermissionUtil.checkPermissionWrapper(this, permission);
        return b;
    }

    /**
     * 申请权限
     *
     * @param permission
     * @param requestCode
     */
    public void requestPerssion(String[] permission, int requestCode) {
        PermissionUtil.requestPermissionsWrapper(this, permission, requestCode);
    }

}
