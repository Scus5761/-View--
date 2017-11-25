package com.example.scus.inputtext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WmsInputView inputViewF;
    private WmsInputView inputViewT;
    private static Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputViewF = (WmsInputView) findViewById(R.id.inputViewF);
        inputViewT = (WmsInputView) findViewById(R.id.inputViewT);

        Toast.makeText(MainActivity.this, "默认状态是输入状态", Toast.LENGTH_SHORT).show();
        inputViewT.setInputState(WmsInputView.STATE_DISABLE);

        //设置点击键盘确认键监听
        inputViewF.setOnCompleteListener(new WmsInputView.OnCompleteListener() {
            @Override
            public void onComplete(String inputText) {
                if (!TextUtils.isEmpty(inputText)) {
                    inputViewF.setInputState(WmsInputView.STATE_LOADING);
                    Toast.makeText(MainActivity.this, "网络加载，正在校验输入内容", Toast.LENGTH_SHORT).show();
                    requestHttpData("inputViewF");
                } else {
                    inputViewF.setInputState(WmsInputView.STATE_ERROR);
                    Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });


        inputViewT.setOnCompleteListener(new WmsInputView.OnCompleteListener() {
            @Override
            public void onComplete(String inputText) {
                inputViewF.setInputState(WmsInputView.STATE_DISABLE);
                if (!TextUtils.isEmpty(inputText)) {
                    inputViewT.setInputState(WmsInputView.STATE_LOADING);
                    Toast.makeText(MainActivity.this, "网络加载，正在校验输入内容", Toast.LENGTH_SHORT).show();
                    requestHttpData("inputViewT");
                } else {
                    inputViewT.setInputState(WmsInputView.STATE_ERROR);
                    Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0:
                        inputViewF.setInputState(WmsInputView.STATE_ERROR);
                        Toast.makeText(MainActivity.this, "校验错误", Toast.LENGTH_SHORT).show();
                        inputViewT.setInputState(WmsInputView.STATE_INPUT);
                        break;

                    case 1:
                        inputViewT.setInputState(WmsInputView.STATE_COMPLETE);
                        Toast.makeText(MainActivity.this, "校验成功", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                }
            }
        };
    }


    //模拟对接口访问，校验数据（省略网络请求相关操作）
    private void requestHttpData(String name) {

        if (name.equals("inputViewF")) {
            new Thread((new Runnable() {
                @Override
                public void run() {

                    SystemClock.sleep(3000);
                    Message message = handler.obtainMessage();
                    message.what = 0;
                    message.sendToTarget();
                }
            })).start();
        }


        if (name.equals("inputViewT")) {
            new Thread((new Runnable() {
                @Override
                public void run() {

                    SystemClock.sleep(3000);
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.sendToTarget();
                }
            })).start();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
