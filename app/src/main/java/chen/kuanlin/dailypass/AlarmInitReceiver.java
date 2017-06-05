package chen.kuanlin.dailypass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by kuanlin on 2016/11/28.
 */
public class AlarmInitReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        //重開機時註冊並執行(因為關機會將先前註冊的刪除)
        if(bundle.equals("android.intent.action.BOOT_COMPLETED")){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, 00);
            Intent it = new Intent(context, MyReceiver.class);
            it.putExtra("msg", "updatepassword");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, it, PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
