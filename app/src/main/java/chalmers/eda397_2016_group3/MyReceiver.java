package chalmers.eda397_2016_group3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //
        Log.v("AlertNotifiy", "on receive.....");

        Intent intentAlert = new Intent(context, AlertActivity.class);
        intentAlert.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentAlert);
    }
}
