package ipro239.iitbeaconproject;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import com.qozix.tileview.TileView;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.security.Permission;

public class MapActivity extends AppCompatActivity implements BeaconConsumer, MonitorNotifier {

    private BackgroundPowerSaver powerSaver;
    private BeaconManager beaconManager;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
        mapFragment = new MapFragment();
        fragTrans.add(R.id.map_fragment, mapFragment);
        fragTrans.commit();
        setContentView(R.layout.activity_map);


        //Beacon Setup
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.setRegionExitPeriod(3000);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Identifier uuid = Identifier.parse(getResources().getString(R.string.eddystone_namespace));
        Region testRegion = new Region("test_region", null, null, null);
        beaconManager.addMonitorNotifier(this);
        try{
            beaconManager.startMonitoringBeaconsInRegion(testRegion);
        }catch (RemoteException e){
            e.printStackTrace();
        }
        powerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void didEnterRegion(Region region) {
        mapFragment.TurnOnTestBeacon();
    }

    @Override
    public void didExitRegion(Region region) {
        mapFragment.TurnOffTestBeacon();
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
