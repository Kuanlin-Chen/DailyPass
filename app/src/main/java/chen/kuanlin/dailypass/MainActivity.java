package chen.kuanlin.dailypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static DevicePolicyManager devicePolicyManager;
    private static ComponentName DAN;
    private final int REQUEST_CODE = 100;

    private EditText editText_password;
    private Button button_rule, button_yyyyMMdd, button_MMdd, button_dd, button_week_number, button_week_word;

    public static String PassWord = null;
    public static int MODE = 0;
    private static ArrayList<String> ruleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_password = (EditText) findViewById(R.id.editText_password);
        button_rule = (Button) findViewById(R.id.button_rule);
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

        initData();
        eventListener();

        /*
        button_yyyyMMdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MODE = 1;
                PassWord = editText_password.getText().toString();
                String TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
                devicePolicyManager.resetPassword(PassWord + TimeStamp, 0);
                //textView_rule.setText("新密碼：" + PassWord + TimeStamp);
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
                //textView_rule.setText("新密碼：" + PassWord + TimeStamp);
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
                //textView_rule.setText("新密碼：" + PassWord + TimeStamp);
                update();
            }
        });  */
    }

    private void initData(){
        ruleList = new ArrayList<>();
        ruleList.add(getString(R.string.rule1));
        ruleList.add(getString(R.string.rule2));
        ruleList.add(getString(R.string.rule3));
    }

    private void eventListener(){
        button_rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder rule = new AlertDialog.Builder(MainActivity.this)
                        .setItems(ruleList.toArray(new String[ruleList.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String Mode = ruleList.get(which);
                                Toast.makeText(getApplicationContext(), Mode, Toast.LENGTH_LONG).show();
                            }
                        });
                rule.show();
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
