package chen.kuanlin.dailypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DevicePolicyManager devicePolicyManager;
    private static ComponentName DAN;
    private final int REQUEST_CODE = 100;

    private EditText editText_password;
    private TextView textView_rule;
    private Button button_yyyyMMdd, button_MMdd, button_dd, button_week_number, button_week_word;

    public static String PassWord = null;
    public static int MODE = 0;

    //Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_password = (EditText) findViewById(R.id.editText_password);
        textView_rule = (TextView) findViewById(R.id.textView_rule);
        button_yyyyMMdd = (Button) findViewById(R.id.button_yyyyMMdd);
        button_MMdd = (Button) findViewById(R.id.button_MMdd);
        button_dd = (Button) findViewById(R.id.button_dd);
        button_week_number = (Button) findViewById(R.id.button_week_number);
        button_week_word = (Button) findViewById(R.id.button_week_word);

        //裝置管理員
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        //申請權限
        DAN = new ComponentName(this, MyAdmin.class);
        boolean isAdminActive = devicePolicyManager.isAdminActive(DAN);
        if (!isAdminActive) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, DAN);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "裝置政策管理員");
            startActivityForResult(intent, REQUEST_CODE);
        }

        button_yyyyMMdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = 1;
                PassWord = editText_password.getText().toString();
                String TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
                devicePolicyManager.resetPassword(PassWord + TimeStamp, 0);
                textView_rule.setText("新密碼：" + PassWord + TimeStamp);
                update();
            }
        });

        button_MMdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = 2;
                PassWord = editText_password.getText().toString();
                String TimeStamp = new SimpleDateFormat("MMdd").format(Calendar.getInstance().getTime());
                devicePolicyManager.resetPassword(PassWord + TimeStamp, 0);
                textView_rule.setText("新密碼：" + PassWord + TimeStamp);
                update();
            }
        });

        button_dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = 3;
                PassWord = editText_password.getText().toString();
                String TimeStamp = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
                devicePolicyManager.resetPassword(PassWord + TimeStamp, 0);
                textView_rule.setText("新密碼：" + PassWord + TimeStamp);
                update();
            }
        });

        button_week_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = 4;
                PassWord = editText_password.getText().toString();
                String TimeStamp = new SimpleDateFormat("EEE").format(Calendar.getInstance().getTime());
                TimeStamp = wordtonumber(TimeStamp);
                textView_rule.setText("新密碼：" + PassWord + TimeStamp);
                update();
            }
        });

        button_week_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = 5;
                PassWord = editText_password.getText().toString();
                String TimeStamp = new SimpleDateFormat("EEE").format(Calendar.getInstance().getTime());
                devicePolicyManager.resetPassword(PassWord + TimeStamp, 0);
                textView_rule.setText("新密碼：" + PassWord + TimeStamp);
                update();
            }
        });
    }

    public static String wordtonumber(String timestamp){
        switch(timestamp){
            case "Mon":
                timestamp = "01";
                break;
            case "Tue":
                timestamp = "02";
                break;
            case "Wed":
                timestamp = "03";
                break;
            case "Thu":
                timestamp = "04";
                break;
            case "Fri":
                timestamp = "05";
                break;
            case "Sat":
                timestamp = "06";
                break;
            case "Sun":
                timestamp = "07";
                break;
        }
        return timestamp;
    }

    private void update(){
        Calendar calendar = Calendar.getInstance();
        //每天00點(24小時制)執行
        calendar.add(Calendar.HOUR_OF_DAY, 00);
        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        intent.putExtra("msg", "updatepassword");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
    /*private void rule(){
        button_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder password_rule_ad = new AlertDialog.Builder(MainActivity.this);
                password_rule_ad.setTitle("設定密碼規則");
                password_rule_ad.setMessage("abcd+當天日期");
                password_rule_ad.setNegativeButton("略過", null);
                password_rule_ad.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int fix) {
                        String TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
                        devicePolicyManager.resetPassword("abcd"+TimeStamp, 0);
                    }
                });
                password_rule_ad.show();
            }
        });
    }*/

    /*private void update(){
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                //每天00點(24小時制)執行
                calendar.add(Calendar.HOUR_OF_DAY, 00);
                Intent intent = new Intent(MainActivity.this, MyReceiver.class);
                intent.putExtra("msg", "updatepassword");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });
    }*/
