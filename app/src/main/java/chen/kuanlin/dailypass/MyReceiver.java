package chen.kuanlin.dailypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by kuanlin on 2016/11/28.
 */
public class MyReceiver extends BroadcastReceiver {

    String TimeStamp;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if(bundle.get("msg").equals("updatepassword")){
            getTimeStamp(MainActivity.MODE);
            //String TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            MainActivity.devicePolicyManager.resetPassword(MainActivity.PassWord+TimeStamp, 0);
            //執行完後同時註冊下一次執行的時間
            Calendar calendar = Calendar.getInstance();
            //每天00點(24小時制)執行
            calendar.add(Calendar.HOUR_OF_DAY, 00);
            Intent intent2 = new Intent(context, MyReceiver.class);
            intent.putExtra("msg", "updatepassword");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void getTimeStamp(int mode){
        switch (mode){
            case 1:
                TimeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
                break;
            case 2:
                TimeStamp = new SimpleDateFormat("MMdd").format(Calendar.getInstance().getTime());
                break;
            case 3:
                TimeStamp = new SimpleDateFormat("dd").format(Calendar.getInstance().getTime());
                break;
            case 4:
                TimeStamp = new SimpleDateFormat("EEE").format(Calendar.getInstance().getTime());
                TimeStamp = MainActivity.wordtonumber(TimeStamp);
                break;
            case 5:
                TimeStamp = new SimpleDateFormat("EEE").format(Calendar.getInstance().getTime());
                break;
        }
    }
}
