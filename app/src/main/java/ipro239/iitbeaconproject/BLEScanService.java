package ipro239.iitbeaconproject;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by shuao23 on 3/1/2017.
 */

public class BLEScanService extends IntentService {

    public BLEScanService(){
        super("BLEScanService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
