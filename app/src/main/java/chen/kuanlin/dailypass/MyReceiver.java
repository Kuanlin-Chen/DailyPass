package chen.kuanlin.dailypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.R.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kuanlin on 2016/11/28.
 */
public class MyReceiver extends BroadcastReceiver {

    private static String PassWord;
    private static String TimeStamp;
    private static String MODE;
    private SharedPreferences settings;
    private Openssl openssl = new Openssl();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if(bundle.get("msg").equals("updatepassword")){
            settings = context.getSharedPreferences("DATA", 0);
            PassWord = openssl.Decrypt(settings.getString("WORD", ""));
            MODE = openssl.Decrypt(settings.getString("RULE", ""));
            //get new TimeStamp
            getTimeStamp(context, MODE);
            MainActivity.devicePolicyManager.resetPassword(PassWord+TimeStamp, 0);

            update(context);
        }
    }

    private void getTimeStamp(Context context, String mode){
        //In BroadcastReceiver, need to import android.R.*
        if (mode.equals(context.getResources().getString(R.string.rule1))){
            TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        }
        else if (mode.equals(context.getResources().getString(R.string.rule2))){
            TimeStamp = new SimpleDateFormat("MMdd").format(Calendar.getInstance().getTime());
        }
        else if (mode.equals(context.getResources().getString(R.string.rule3))){
            TimeStamp = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
        }
    }

    private void update(Context context){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 00);
        Intent intent = new Intent(context, MyReceiver.class);
        intent.putExtra("msg", "updatepassword");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
