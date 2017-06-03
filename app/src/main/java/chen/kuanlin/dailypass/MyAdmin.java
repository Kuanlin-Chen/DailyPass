package chen.kuanlin.dailypass;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/**
 * Created by kuanlin on 2016/11/22.
 */
public class MyAdmin extends DeviceAdminReceiver {

    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "啟動裝置管理員");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        CharSequence disable_warning = "Are you sure？";
        return disable_warning;
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Stop DeviceAdministration");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, "密碼已變更");
    }

}
