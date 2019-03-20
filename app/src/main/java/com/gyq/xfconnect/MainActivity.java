package com.gyq.xfconnect;

import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import android.accessibilityservice.AccessibilityService;

import com.jflavio1.wificonnector.WifiConnector;
import com.jflavio1.wificonnector.interfaces.ConnectionResultListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public void msg(String text){
        AlertDialog.Builder  builder=new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setIcon(android.R.drawable.btn_star);
        builder.setMessage(text);

        //为builder对象添加确定按钮，不过这里嵌套了一个函数
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
        );
        //builder创建对话框对象AlertDialog
        AlertDialog simpledialog=builder.create();
        simpledialog.show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        msg("开始连接小方");
     //   openMijia();
        WifiConnector connector = new WifiConnector(this,
                "isa-camera-isc5_miap9EA6",
                "NEW_BSSID",
                "WPA",
                "20110331");

        connector.enableWifi();

        connector.connectToWifi(new ConnectionResultListener() {
            @Override
            public void successfulConnect(String SSID) {
                msg("连接成功！");
                openMijia();
            }

            @Override
            public void errorConnect(int codeReason) {
                msg("连接出错！");

            }

            @Override
            public void onStateChange(SupplicantState supplicantState) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickXF(int x1,int y1){
        OutputStream os = null;
        String cmd = "input tap "+x1+" "+y1;
        try {
            if (os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            msg(e.getMessage());
        }
        Instrumentation mInst = new Instrumentation();
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x1, y1, 0));    //x,y 即是事件的坐标
        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x1, y1, 0));

    }
    public void openMijia(){
        Intent intent = new Intent();
        PackageManager packageManager = getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.xiaomi.smarthome");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
        startActivity(intent);
//        int x0 = 200;
//        int y0 = 500;
//        for(int i=0; i<5; i++) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//
//            }
//            clickXF(x0, y0);
//            msg("click "+i);
//        }

    }

}

