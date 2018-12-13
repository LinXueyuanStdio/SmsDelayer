package com.whf.messagerelayer.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.google.gson.Gson;
import com.whf.messagerelayer.confing.Constant;
import com.whf.messagerelayer.service.SmsService;
import com.whf.messagerelayer.utils.FormatMobile;
import com.whf.messagerelayer.utils.NativeDataManager;

public class MessageReceiver extends BroadcastReceiver {

    private NativeDataManager mNativeDataManager;

    public MessageReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mNativeDataManager = new NativeDataManager(context);
        if (mNativeDataManager.getReceiver()) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    startSmsService(context, sms);
                }
            }
        }
    }

    private ComponentName startSmsService(Context context, SmsMessage sms) {
        String mobile = sms.getOriginatingAddress();//发送短信的手机号码

        if (FormatMobile.hasPrefix(mobile)) {
            mobile = FormatMobile.formatMobile(mobile);
        }
        String content = sms.getMessageBody();//短信内容

        Intent serviceIntent = new Intent(context, SmsService.class);
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_CONTENT, new Gson().toJson(sms));
        serviceIntent.putExtra(Constant.EXTRA_MESSAGE_MOBILE, mobile);
        return context.startService(serviceIntent);
    }


}
