package com.whf.messagerelayer.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.whf.messagerelayer.R;
import com.whf.messagerelayer.utils.NativeDataManager;


public class ServerRelayerActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch mServerSwitch;
    private RelativeLayout mServerRelative, mCenterRelative;
    private TextView mServerText, mCenterText;

    private NativeDataManager mNativeDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_relayer);
        initActionbar();

        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initActionbar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void init() {
        mNativeDataManager = new NativeDataManager(this);

        initView();
        initData();
        initListener();

    }

    private void initView() {
        mServerSwitch = (Switch) findViewById(R.id.switch_sms);
        mServerRelative = (RelativeLayout) findViewById(R.id.layout_server);
        mServerText = (TextView) findViewById(R.id.text_server);
    }

    private void initData() {
        if (mNativeDataManager.getServerRelay()) {
            mServerSwitch.setChecked(true);
        } else {
            mServerSwitch.setChecked(false);
        }
        mServerText.setText(mNativeDataManager.getObjectServer());
    }

    private void initListener() {
        mServerSwitch.setOnCheckedChangeListener(this);

        mServerRelative.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_sms:
                smsChecked(isChecked);
                break;
        }
    }

    /**
     * 使用短信转发至指定手机号的Switch的事件方法
     *
     * @param isChecked
     */
    private void smsChecked(boolean isChecked) {
        if (isChecked) {
            mNativeDataManager.setServerRelay(true);
        } else {
            mNativeDataManager.setServerRelay(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_server:
                showEditDialog();
                break;
        }
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_server, null, false);
        final EditText serverEdit = (EditText) view.findViewById(R.id.dialog_edit);

        String serverText = mServerText.getText().toString();
        if(!serverText.equals("点击设置")){
            serverEdit.setText(serverText);
        }

        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNativeDataManager.setObjectServer(serverEdit.getText().toString());
                mServerText.setText(serverEdit.getText());
            }
        });
        builder.show();
    }
}
