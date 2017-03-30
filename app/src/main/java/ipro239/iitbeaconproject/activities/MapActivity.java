package ipro239.iitbeaconproject.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.widgets.ZoomPanLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ipro239.iitbeaconproject.beacon.Beacon;
import ipro239.iitbeaconproject.beacon.BeaconConnection;
import ipro239.iitbeaconproject.beacon.BeaconDisplayer;
import ipro239.iitbeaconproject.beacon.BeaconFilters;
import ipro239.iitbeaconproject.beacon.BeaconXMLParser;
import ipro239.iitbeaconproject.beacon.ConnectionStatus;
import ipro239.iitbeaconproject.bluetooth.BLEScanCallback;
import ipro239.iitbeaconproject.bluetooth.BeaconScanResult;
import ipro239.iitbeaconproject.R;
import ipro239.iitbeaconproject.beacon.BeaconConnectionManager;
import ipro239.iitbeaconproject.bluetooth.BeaconScanner;

public class MapActivity extends AppCompatActivity  {

    //Statics
    private static final int REQUEST_PERMISSION = 273;
    private static final int REQUEST_ENABLE_BT = 842;
    private static final int REQUEST_ENABLE_LOC = 555;
    private static final int INIT_RESULT = 123;             //Used for callback when the initial usermode settings gets displayed
    private static final int USERMODE_CALLBACK = 457;
    private static final int SCAN_LENGTH = 2500;
    private static final int MARKER_UPDATE_WAIT = 500;

    private TileView mapView;
    private View uiView;
    private Menu optionMenu;

    //For scanning beacons
    private BeaconScanner beaconScanner;

    //For beacons
    BeaconConnectionManager connectionManager = new BeaconConnectionManager();
    BeaconDisplayer beaconDisplayer;
    Handler markerUpdateHandler = new Handler();
    Runnable markerUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            displayChanges();
            markerUpdateHandler.postDelayed(markerUpdateRunnable,MARKER_UPDATE_WAIT);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!UserModeActivity.Initialized(this)){
            //Display the user mode screen if the app is used for the first time
            startActivityForResult(new Intent(this, UserModeActivity.class),INIT_RESULT);
        }else{
            //Else, if used the app before, call the callback function manually to initialize program
            onActivityResult(INIT_RESULT, 0, null);
        }
    }

    @Override
    //The option menu contains buttons such as the rescan and options button
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
                startActivity(new Intent(this, OptionsActivity.class));
                break;
            case R.id.menu_usermode:
                startActivityForResult(new Intent(this, UserModeActivity.class), USERMODE_CALLBACK);
                break;
        }
        return true;
    }

    //Callback Functions----------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            askForAllPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_LOC) {
            askForAllPermissions();
        }else if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            initBeaconScanner();
        }else if(requestCode == INIT_RESULT){ //This first gets called to initialize activity
            setupView();
            askForAllPermissions();

            //Display all beacons from xml file
            beaconDisplayer = new BeaconDisplayer(this, mapView);
            beaconDisplayer.addBeacons(getBeaconData());
            SharedPreferences preferences  = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,MODE_PRIVATE);
            beaconDisplayer.setDisplayTag(preferences.getInt(UserModeActivity.SELECTED_FLAG, BeaconFilters.ALL_FLAGS));
            beaconDisplayer.updateDisplay();
        }else if(requestCode == USERMODE_CALLBACK){
            SharedPreferences preferences  = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,MODE_PRIVATE);
            beaconDisplayer.setDisplayTag(preferences.getInt(UserModeActivity.SELECTED_FLAG, BeaconFilters.ALL_FLAGS));
            beaconDisplayer.updateDisplay();
        }
    }

    //Permissions-----------------------------------------------------------------------------------
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
        if(beaconScanner == null) {
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

    private void initBeaconScanner(){
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        beaconScanner = new BeaconScanner(bluetoothAdapter);
        beaconScanner.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        beaconScanner.setBLEScanCallbackListener(new BLEScanCallback() {
            @Override
            public void onScanStart() {
                animateScanButton();
                markerUpdateHandler.postDelayed(markerUpdateRunnable,MARKER_UPDATE_WAIT);
            }

            @Override
            public void onScanEnd() {
                stopanimationScanButton();
                markerUpdateHandler.removeCallbacks(markerUpdateRunnable);
                connectionManager.checkConnections();
                displayChanges();
            }

            @Override
            public void onScanResult(BeaconScanResult result) {
                connectionManager.connect(result.getInstanceID());
                displayChanges();
            }

            @Override
            public void onScanFailed(String message) {
                Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
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

        uiview.findViewById(R.id.bottom_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, NearbyBeaconsListActivity.class);
                intent.putParcelableArrayListExtra(BeaconListActivity.BEACON_LIST_KEY, (ArrayList<? extends Parcelable>)beaconDisplayer.getOnBeacons());
                startActivity(intent);
            }
        });
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

    private void startScan(){
        if(!askForAllPermissions())
            return;

        if(beaconScanner == null)
            return;

        beaconScanner.startScan(SCAN_LENGTH);
    }

    private void stopScan() {
        if (beaconScanner == null)
            return;

        beaconScanner.stopScan();
    }

    private void displayChanges(){
        BeaconConnection connection = connectionManager.popConnectionQueue();
        while (connection != null) {
            if (connection.getStatus() == ConnectionStatus.CONNECTED)
                beaconDisplayer.changeBeaconState(connection.getId(), true);
            else
                beaconDisplayer.changeBeaconState(connection.getId(), false);
            connection = connectionManager.popConnectionQueue();
        }
    }

    private List<Beacon> getBeaconData(){
        //Display beacons
        List<Beacon> result;
        InputStream inputStream = getResources().openRawResource(R.raw.beacons);
        BeaconXMLParser parser = new BeaconXMLParser();
        try {
            try {
                result = parser.parse(inputStream);

            }finally {
                inputStream.close();
            }
        }catch (Exception e){
            result = null;
            Toast.makeText(this, "Could not load beacons", Toast.LENGTH_LONG).show();
        }
        return result;
    }

    private void showBottomBar(){
        Animation bottomUp = AnimationUtils.loadAnimation(MapActivity.this,
                R.anim.bottom_up);
        TextView hiddenPanel = (TextView)findViewById(R.id.bottom_bar);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
    }

    private void hideBottomBar(){
        Animation bottomUp = AnimationUtils.loadAnimation(MapActivity.this,
                R.anim.bottom_down);
        TextView hiddenPanel = (TextView)findViewById(R.id.bottom_bar);
        hiddenPanel.startAnimation(bottomUp);
    }
}


    /*@Override
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
                                .setContentTitle("IIT Beacon")
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
    }*/