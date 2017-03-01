package ipro239.iitbeaconproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MapActivity extends AppCompatActivity implements BeaconConsumer, MonitorNotifier {

    private static final int PERMISSION_REQUEST_BLE = 376;
    private static final int PERMISSION_REQUEST_LOCATION = 913;

    private BackgroundPowerSaver powerSaver;
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
        MapFragment mapFragment = new MapFragment();
        fragTrans.add(R.id.map_fragment, mapFragment);
        fragTrans.commit();
        setContentView(R.layout.activity_map);

        //Beacon Setup
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
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
        Toast.makeText(this, region.getId2() + " Entered", Toast.LENGTH_LONG).show();
    }

    @Override
    public void didExitRegion(Region region) {
        Toast.makeText(this, region.getId2() + " Exited", Toast.LENGTH_LONG).show();
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
