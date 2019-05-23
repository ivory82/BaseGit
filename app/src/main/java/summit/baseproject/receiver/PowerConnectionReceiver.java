package summit.baseproject.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

public class PowerConnectionReceiver extends BroadcastReceiver {

    String TAG = "PowerConnectionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        Log.d(TAG, "isCharging: " + isCharging);
        Log.d(TAG, "chargePlug: " + chargePlug);
        Log.d(TAG, "usbCharge: " + usbCharge);
        Log.d(TAG, "acCharge: " + acCharge);

        if( intent.getAction() == Intent.ACTION_POWER_CONNECTED ){
//            String message = ("서비스 : 충전 중");
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else if( intent.getAction() == Intent.ACTION_POWER_DISCONNECTED ){
//            String message = ("서비스 : 충전 해제");
//            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }



    }
}
