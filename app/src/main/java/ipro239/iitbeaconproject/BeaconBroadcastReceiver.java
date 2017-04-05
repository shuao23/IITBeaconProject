package ipro239.iitbeaconproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shuao23 on 4/4/2017.
 */

public class BeaconBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, BeaconService.class);
        context.startService(startServiceIntent);
    }
}
