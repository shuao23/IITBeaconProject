package ipro239.iitbeaconproject.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Region;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.markers.MarkerLayout;
import com.qozix.tileview.widgets.ZoomPanLayout;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import ipro239.iitbeaconproject.BeaconService;
import ipro239.iitbeaconproject.activities.helper.BeaconViewHolder;
import ipro239.iitbeaconproject.beacon.Beacon;
import ipro239.iitbeaconproject.beacon.BeaconConnection;
import ipro239.iitbeaconproject.beacon.BeaconDatabase;
import ipro239.iitbeaconproject.beacon.BeaconDisplayer;
import ipro239.iitbeaconproject.beacon.BeaconFilters;
import ipro239.iitbeaconproject.beacon.BeaconIcons;
import ipro239.iitbeaconproject.beacon.ConnectionStatus;
import ipro239.iitbeaconproject.bluetooth.BLEScanCallback;
import ipro239.iitbeaconproject.bluetooth.BeaconScanResult;
import ipro239.iitbeaconproject.R;
import ipro239.iitbeaconproject.beacon.BeaconConnectionManager;
import ipro239.iitbeaconproject.bluetooth.BeaconScanner;

public class MapActivity extends AppCompatActivity  {

    //Statics
    private static final int MAP_SIZEX = 5749;
    private static final int MAP_SIZEY = 12834;
    private static final int REQUEST_PERMISSION = 273;
    private static final int REQUEST_ENABLE_BT = 842;
    private static final int REQUEST_ENABLE_LOC = 555;
    private static final int INIT_RESULT = 123;             //Used for callback when the initial usermode settings gets displayed
    private static final int SCAN_LENGTH = 2500;
    private static final int MARKER_UPDATE_WAIT = 500;
    private static final int NEXT_SCAN_DELAY = 20000;

    private TileView mapView;
    private View uiView;
    private Menu optionMenu;

    //For scanning beacons
    private BeaconScanner beaconScanner;
    private Handler autoScannerHandler = new Handler();

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

    //for markers
    private boolean markerTapped = false;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopService(new Intent(this,BeaconService.class));
        //Initialize beacon database
        if(!BeaconDatabase.isInit())
            BeaconDatabase.init(this);

        if(!UserModeActivity.Initialized(this)){
            //Display the user mode screen if the app is used for the first time
            startActivityForResult(new Intent(this, UserModeActivity.class),INIT_RESULT);
        }else{
            //Else, if used the app before, call the callback function manually to initialize program
            onActivityResult(INIT_RESULT, 0, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences  = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,MODE_PRIVATE);

        //Get the flag settings
        if(beaconDisplayer != null) {
            beaconDisplayer.setDisplayTag(preferences.getInt(UserModeActivity.SELECTED_FLAG, BeaconFilters.ALL_FLAGS));
            beaconDisplayer.updateDisplay();
            connectionManager.resetConnections();
        }

        //Get the option settings
        //If we want to scan in the background, start scan runnable
        if(beaconScanner != null && preferences.getBoolean(OptionsActivity.BACKGROUND_SCANNING_KEY, false)){
            autoScannerHandler.post(new Runnable() {
                @Override
                public void run() {
                    startScan();
                    autoScannerHandler.postDelayed(this, NEXT_SCAN_DELAY);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        //Stop auto scans
        autoScannerHandler.removeCallbacksAndMessages(null);
        stopScan();
        super.onPause();
    }

    @Override
    protected void onStop(){
        if(getSharedPreferences(OptionsActivity.BEACON_PREF_NAME, MODE_PRIVATE).getBoolean(OptionsActivity.NOTIFICATION_KEY, false))
            startService(new Intent(this, BeaconService.class));
        super.onStop();
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
                startActivity(new Intent(this, UserModeActivity.class));
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
        SharedPreferences preferences  = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,MODE_PRIVATE);
        if(requestCode == REQUEST_ENABLE_LOC) {
            askForAllPermissions();
        }else if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            initBeaconScanner();
        }else if(requestCode == INIT_RESULT){ //This first gets called to initialize activity
            setupView();
            askForAllPermissions();
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
                if(beaconDisplayer != null &&
                        beaconDisplayer.isDisplayedBeacon(result.getInstanceID())) {
                    connectionManager.connect(result.getInstanceID(), result.getRssi());
                    displayChanges();
                }
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
        baseLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundColor));
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

        View bottomBar = uiview.findViewById(R.id.nearby_beacon_button);
        bottomBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, NearbyBeaconsListActivity.class);
                List<BeaconConnection> beaconConnections = connectionManager.getConnectedBeaconIDs();
                Collections.sort(beaconConnections, new Comparator<BeaconConnection>() {
                    @Override
                    public int compare(BeaconConnection o1, BeaconConnection o2) {
                        if(o1.getRssi() > o2.getRssi())
                            return -1;
                        else if(o1.getRssi() < o2.getRssi())
                            return 1;
                        else
                            return 0;
                    }
                });
                List<Beacon> connectedBeacons = new ArrayList<>(beaconConnections.size());
                for(int i = 0 ; i < beaconConnections.size(); i++){
                    connectedBeacons.add(beaconDisplayer.getBeacon(beaconConnections.get(i).getId()));
                }

                intent.putParcelableArrayListExtra(BeaconListActivity.BEACON_LIST_KEY, (ArrayList<? extends Parcelable>)connectedBeacons);
                startActivity(intent);
            }
        });

        //Display all beacons from xml file
        beaconDisplayer = new BeaconDisplayer(this, mapView);
        if(BeaconDatabase.isInit()) {
            Iterator<Beacon> iterator = BeaconDatabase.getBeaconIter();
            while (iterator.hasNext()){
                beaconDisplayer.addBeacon(iterator.next());
            }
        }
        beaconDisplayer.setDisplayTag(getSharedPreferences(OptionsActivity.BEACON_PREF_NAME, MODE_PRIVATE)
                .getInt(UserModeActivity.SELECTED_FLAG, BeaconFilters.ALL_FLAGS));
        beaconDisplayer.updateDisplay();
        connectionManager.resetConnections();

        cardView = (CardView) getLayoutInflater().inflate(R.layout.beacon_cardview, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        ((RelativeLayout)uiView).addView(cardView, params);
        cardView.setVisibility(View.INVISIBLE);


        //When the map is tapped
        HotSpot hotSpot = new HotSpot();
        hotSpot.set(0, 0, MAP_SIZEX, MAP_SIZEY);
        mapView.addHotSpot(hotSpot);
        mapView.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
            @Override
            public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                if(markerTapped){
                    markerTapped = false;
                    return;
                }
                beaconDisplayer.removeCurrentBeacon();
                hideSelectedBeaconCard();
            }
        });

        //When marker is clicked
        mapView.setMarkerTapListener(new MarkerLayout.MarkerTapListener() {
            @Override
            public void onMarkerTap(View view, int x, int y) {
                markerTapped = true;
                final Beacon beacon = beaconDisplayer.findBeaconByImageView(view);
                if(beacon != null) {
                    beaconDisplayer.selectCurrentBeacon(beacon.getInstanceID());
                    BeaconViewHolder viewHolder = new BeaconViewHolder(cardView);
                    viewHolder.getTittleView().setText(beacon.getName());
                    viewHolder.getDescriptionView().setText(beacon.getDescription());
                    viewHolder.getTypeIconView().setImageResource(BeaconIcons.getCardIconIDByTag(beacon.getTags()));
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MapActivity.this, WebActivity.class);
                            intent.putExtra(WebActivity.URL_KEY, beacon.getUrl());
                            intent.putExtra(WebActivity.TITTLE_KEY, beacon.getName());
                            v.getContext().startActivity(intent);
                        }
                    });
                    showSelectedBeaconCard();
                }
            }
        });
    }

    private TileView createMap(){
        TileView tileView = new TileView(this);
        tileView.setSize(MAP_SIZEX,MAP_SIZEY);
        tileView.addDetailLevel(1f, "sliced_large/large_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.6f, "sliced_medium/medium_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.2f, "sliced_small/small_tile-%d_%d.png", 256, 256);

        ImageView downSample = new ImageView(this);
        downSample.setImageResource( R.drawable.tiny );
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

    private void showSelectedBeaconCard(){
        Animation topDown = AnimationUtils.loadAnimation(MapActivity.this,
                R.anim.top_down);
        cardView.startAnimation(topDown);
        cardView.setVisibility(View.VISIBLE);
    }

    private void hideSelectedBeaconCard(){
        if(cardView.getVisibility() == View.INVISIBLE)
            return;

        Animation topUp = AnimationUtils.loadAnimation(MapActivity.this,
                R.anim.top_up);
        topUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardView.startAnimation(topUp);
    }

}