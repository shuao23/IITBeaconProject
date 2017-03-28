package ipro239.iitbeaconproject;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.ImageView;
import com.qozix.tileview.TileView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.security.Permission;

public class MapActivity extends AppCompatActivity implements BeaconConsumer  {

    private BackgroundPowerSaver powerSaver;
    private BeaconManager beaconManager;
    private MapFragment mapFragment;
    private static final int NOTIFICATION_ID = 1;
    private RegionBootstrap regionBootstrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
        mapFragment = new MapFragment();
        fragTrans.add(R.id.map_fragment, mapFragment);
        fragTrans.commit();
        setContentView(R.layout.activity_map);

        if (!havePermissions()) {
            requestPermissions();
        }
        //Beacon Setup
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.setRegionExitPeriod(3000);
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Identifier uuid = Identifier.parse(getResources().getString(R.string.eddystone_namespace));
        Region testRegion = new Region("test_region", null, null, null);
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                mapFragment.TurnOnTestBeacon();
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.google.com"));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //Intent launchIntent = new Intent(getApplicationContext(), MapActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                        intent,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_b_active)
                                .setContentTitle("IIT Beacon")
                                .setContentText("Find beacon nearby")
                                .setAutoCancel(true)
                                .setContentIntent(pi);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void didExitRegion(Region region) {
                mapFragment.TurnOffTestBeacon();
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        }
        catch (RemoteException e) {    }
    }
    private boolean havePermissions() {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1111);
    }
}
