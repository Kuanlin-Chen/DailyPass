package chen.kuanlin.dailypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private EditText editText_input;
    private TextView textView_password;
    private Button button_rule, button_start, button_check;

    public static DevicePolicyManager devicePolicyManager;
    private static ComponentName DAN;
    private final int REQUEST_CODE = 100;

    public static String PassWord = null;
    private static String TimeStamp = null;
    public static String MODE = null;
    private static ArrayList<String> ruleList;

    //Set up SharedPreference
    private SharedPreferences settings;
    private static final String DATA = "DATA";
    private static final String WORD = "WORD";
    private static final String RULE = "RULE";

    private Openssl openssl = new Openssl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponent();
        initDevicePolicyManager();
        initData();
        eventListener();
    }

    private void initComponent(){
        editText_input = (EditText) findViewById(R.id.editText_input);
        textView_password = (TextView)findViewById(R.id.textView_password);
        button_rule = (Button) findViewById(R.id.button_rule);
        button_start = (Button) findViewById(R.id.button_strat);
        button_check = (Button)findViewById(R.id.button_check);
    }

    private void initDevicePolicyManager(){
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
                                MODE = ruleList.get(which);
                                Toast.makeText(getApplicationContext(), MODE, Toast.LENGTH_SHORT).show();
                            }
                        });
                rule.show();
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassWord = editText_input.getText().toString();
                /*
                == handles null strings fine,
                but calling .equals() from a null string will cause an exception.
                */
                if (PassWord==null || PassWord.equals("")) {
                    AlertDialog.Builder passwordIsEmpty = new AlertDialog.Builder(MainActivity.this);
                    passwordIsEmpty.setTitle("尚未輸入密碼");
                    passwordIsEmpty.setMessage("請在密碼欄位裡輸入您的密碼！");
                    passwordIsEmpty.setNegativeButton("了解", null);
                    passwordIsEmpty.show();
                }else if (MODE==null){
                    AlertDialog.Builder modeIsEmpty = new AlertDialog.Builder(MainActivity.this);
                    modeIsEmpty.setTitle("尚未選擇模式");
                    modeIsEmpty.setMessage("請選擇您喜歡的密碼規則！");
                    modeIsEmpty.setNegativeButton("了解", null);
                    modeIsEmpty.show();
                }else {
                    getTimeStamp(MODE);
                    textView_password.setText("新密碼："+PassWord+TimeStamp);
                    //SetupPassword
                    devicePolicyManager.resetPassword(PassWord + TimeStamp, 0);
                    //Save data into SharedPreference
                    saveData(PassWord, MODE);
                    //Register update broadcast
                    //update();
                }
            }
        });

        button_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });
    }

    private void getTimeStamp(String mode){
        if (mode.equals(getString(R.string.rule1))){
            TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        }
        else if (mode.equals(getString(R.string.rule2))){
            TimeStamp = new SimpleDateFormat("MMdd").format(Calendar.getInstance().getTime());
        }
        else if (mode.equals(getString(R.string.rule3))){
            TimeStamp = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
        }
    }

    private void update(){
        //執行完後同時註冊下一次執行的時間
        Calendar calendar = Calendar.getInstance();
        //每天00點(24小時制)執行
        calendar.add(Calendar.HOUR_OF_DAY, 00);
        Intent intent = new Intent(MainActivity.this, MyReceiver.class);
        intent.putExtra("msg", "updatepassword");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void saveData(String password, String rule){
        settings = getSharedPreferences(DATA, 0);
        settings.edit()
                .putString(WORD, openssl.Encrypt(password))
                .putString(RULE, openssl.Encrypt(rule))
                .commit();
    }

    private void readData(){
        settings = getSharedPreferences(DATA, 0);
        textView_password.setText(openssl.Decrypt(settings.getString(RULE, "Rule")));
    }
}
