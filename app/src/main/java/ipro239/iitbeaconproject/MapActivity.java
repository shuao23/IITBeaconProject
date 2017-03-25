package ipro239.iitbeaconproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.widgets.ZoomPanLayout;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class MapActivity extends AppCompatActivity implements BeaconConsumer  {

    //Statics
    private static final int NOTIFICATION_ID = 691;
    private static final int REQUEST_PERMISSION = 273;
    private static final int REQUEST_ENABLE_BT = 842;
    private static final int REQUEST_ENABLE_LOC = 555;
    private static final int SCAN_LENGTH = 5000;

    private TileView mapView;
    private View uiView;
    private Menu optionMenu;
    private BeaconManager beaconManager;

    //For bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private boolean isScanning;
    private Handler bluetoothHandler = new Handler();
    private ScanCallback leScanCallback;
    private BluetoothAdapter.LeScanCallback old_leScanCallback;

    //For beacons
    BeaconConnectionManager connectionManager = new BeaconConnectionManager();
    BeaconDisplayer beaconDisplayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupView();
        askForAllPermissions();
        beaconDisplayer = new BeaconDisplayer(this, mapView);

        //BeaconDisplay Setup
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.setRegionExitPeriod(3000);
        beaconManager.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionMenu = menu;
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_rescan:
                startScan();
                break;
            case R.id.menu_options:
                break;
        }
        return true;
    }

    @Override
    public void onBeaconServiceConnect() {
        Identifier uuid = Identifier.parse("");
        Region testRegion = new Region("test_region", null, null, null);
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Uri webpage=Uri.parse("http://www.google.com");
                Intent intent=new Intent(Intent.ACTION_VIEW,webpage);
                //Intent launchIntent = new Intent(getApplicationContext(), MapActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_b_active)
                                .setContentTitle("IIT BeaconDisplay")
                                .setContentText("Find beacon nearby")
                                .setContentIntent(pi);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }

            @Override
            public void didExitRegion(Region region) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            askForAllPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_LOC) {
            askForAllPermissions();
        }else if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
            turnBluetoothOn();
    }

    private boolean askForAllPermissions(){
        if(!haveLocPermissions()) {
            requestLocPermissions();
            return false;
        }
        if(!isLocOn()){
            turnLocOn();
            return false;
        }

        if(!haveBLEPermissions()){
            requestBLEPermissions();
            return false;
        }
        if(!isBluetoothOn()) {
            requestBluetooth();
            return false;
        }

        return true;
    }

    private boolean haveLocPermissions() {
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);
    }

    private boolean isLocOn(){
        LocationManager locationManager = null;
        boolean gps_enabled= false, network_enabled = false;

        if(locationManager ==null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){}

        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){}

        return gps_enabled || network_enabled;
    }

    private void turnLocOn(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.dialog_loc_msg);
        alertBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent locIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(locIntent, REQUEST_ENABLE_LOC);
            }
        });
        alertBuilder.setNegativeButton(R.string.dialog_no, null);
        alertBuilder.show();
    }

    private boolean haveBLEPermissions(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBLEPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_PERMISSION);
    }

    private void turnBluetoothOn(){
        bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private boolean isBluetoothOn(){
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private void requestBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private boolean isBLECallbackInit(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return leScanCallback != null;
        }else{
            return old_leScanCallback != null;
        }
    }

    private void initBLECallback(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            leScanCallback = new ScanCallback() {
                @Override
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public void onScanResult(int callbackType, ScanResult result) {
                    IITBeaconParser parser = new IITBeaconParser(result.getScanRecord().getBytes());
                    if(parser.validBeacon() && result.getRssi() >= parser.getTxPower())
                        debugBeacons("TX Power: " + String.valueOf(parser.getTxPower()) + "ID, ",
                                parser.getInstanceID());
                }

                //IDK what this is but never gets called
                /*@Override
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public void onBatchScanResults(List<ScanResult> results) {
                    for (ScanResult result:results) {
                        debugBeacons("Batch: ", result.getScanRecord().getBytes());
                    }
                }*/

                @Override
                public void onScanFailed(int errorCode) {
                    final String message;
                    if(errorCode == SCAN_FAILED_INTERNAL_ERROR)
                        message = "Internal Error";
                    else if(errorCode == SCAN_FAILED_FEATURE_UNSUPPORTED)
                        message = "Not supported";
                    else if(errorCode == SCAN_FAILED_APPLICATION_REGISTRATION_FAILED)
                        message = "An application error occured";
                    else
                        return;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            };
        }else{
             old_leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    IITBeaconParser parser = new IITBeaconParser(scanRecord);
                    if(parser.validBeacon() && rssi >= parser.getTxPower())
                        debugBeacons("TX Power: " + String.valueOf(parser.getTxPower()) + "ID, ",
                                parser.getInstanceID());
                }
            };
        }
    }

    private void debugBeacons(String pre, byte[] record){
        StringBuilder sb = new StringBuilder();
        sb.append(pre);
        for(int i = 0; i < record.length; i++){
            sb.append(String.format("%02X", record[i]));
        }
        Log.d("TEST", sb.toString());
    }

    private void setupView(){
        //Create the base layout
        FrameLayout baseLayout = new FrameLayout(this);
        //Create the map view
        TileView tileView = createMap();
        baseLayout.addView(tileView);
        mapView = tileView;
        //Create the UI view
        View uiview = getLayoutInflater().inflate(R.layout.mapui_layout, null);
        baseLayout.addView(uiview);
        uiView = uiview;
        //Display the base view
        setContentView(baseLayout);
    }

    private TileView createMap(){
        TileView tileView = new TileView(this);
        tileView.setSize(6823,13866);
        tileView.addDetailLevel(1f, "map_org_sliced/map_org_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.69998534369f, "map_70_sliced/map70_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.39997068738f, "map_40_sliced/map40_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.0999560311f, "map_10_sliced/map10_tile-%d_%d.png", 256, 256);

        ImageView downSample = new ImageView(this);
        downSample.setImageResource( R.drawable.map_tiny );
        tileView.addView( downSample, 0 );

        tileView.setScale(0);
        tileView.setMinimumScaleMode(ZoomPanLayout.MinimumScaleMode.FIT);

        return tileView;
    }

    private void animateScanButton(){
        RotateAnimation anim = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        View rescanView = findViewById(R.id.menu_rescan);
        rescanView.startAnimation(anim);
        if(optionMenu != null)
            optionMenu.findItem(R.id.menu_rescan).setEnabled(false);
    }

    private void stopanimationScanButton(){
        findViewById(R.id.menu_rescan).clearAnimation();
        if(optionMenu != null)
            optionMenu.findItem(R.id.menu_rescan).setEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScan(){
        if(!askForAllPermissions())
            return;

        if(!isBLECallbackInit())
            initBLECallback();

        animateScanButton();
        bluetoothHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, SCAN_LENGTH);
        isScanning = true;
        if(bleScanner == null)
            bluetoothAdapter.startLeScan(old_leScanCallback);
        else{
            bleScanner.startScan(leScanCallback);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopScan(){
        isScanning = false;
        stopanimationScanButton();
        if(bleScanner == null)
            bluetoothAdapter.stopLeScan(old_leScanCallback);
        else
            bleScanner.stopScan(leScanCallback);
        connectionManager.checkConnections();
        displayChanges();
    }

    private void displayChanges(){

    }
}
