package ipro239.iitbeaconproject;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;

import ipro239.iitbeaconproject.activities.MapActivity;
import ipro239.iitbeaconproject.activities.OptionsActivity;
import ipro239.iitbeaconproject.activities.UserModeActivity;
import ipro239.iitbeaconproject.activities.WebActivity;
import ipro239.iitbeaconproject.beacon.Beacon;
import ipro239.iitbeaconproject.beacon.BeaconDatabase;
import ipro239.iitbeaconproject.beacon.BeaconFilters;
import ipro239.iitbeaconproject.beacon.BeaconIcons;
import ipro239.iitbeaconproject.bluetooth.BLEScanCallback;
import ipro239.iitbeaconproject.bluetooth.BeaconScanResult;
import ipro239.iitbeaconproject.bluetooth.BeaconScanner;

/**
 * Created by Ethan on 3/28/2017.
 */

public class BeaconService extends Service {

    public static final String ENABLE_KEY = "enable";
    private static final int NOTIFICATION_ID = 113;
    private static final int SCAN_LENGTH = 1000; //Scan for 1 seconds
    private static final int SCAN_PERIOD = 7000; //Wait for 7 seconds
    private static final int MINIMUM_NOTIFICATION_TIME = 900000; //900sec or 15 minutes

    private BeaconScanner beaconScanner;
    private Handler scannerHandler = new Handler();
    private static HashMap<String, Long> connectedBeacons = new HashMap<>();

    @Override
    public void onCreate() {
        if(!BeaconDatabase.isInit())
            BeaconDatabase.init(this);

        if(!checkAllPermisions())
            stopSelf();
    }

    private boolean checkAllPermisions(){
        if(!haveLocPermissions())
            return false;

        if(!isLocOn())
            return false;

        if(!haveBLEPermissions())
            return false;

        if(!initBeaconScanner())
            return false;

        return  true;
    }

    private boolean haveLocPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
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

    private boolean haveBLEPermissions(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean initBeaconScanner(){
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
            return false;

        beaconScanner = new BeaconScanner(bluetoothAdapter);
        beaconScanner.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        beaconScanner.setBLEScanCallbackListener(new BLEScanCallback() {

            @Override
            public void onScanResult(BeaconScanResult result) {
                int filterFlag = getSharedPreferences(OptionsActivity.BEACON_PREF_NAME,MODE_PRIVATE).
                        getInt(UserModeActivity.SELECTED_FLAG, UserModeActivity.STUDENT_FLAG);

                Beacon beacon = BeaconDatabase.getBeacon(result.getInstanceID());
                if(beacon == null)
                    return;

                if(beacon.getTags() != 0 && (beacon.getTags() & filterFlag) == 0)
                    return;

                if(!connectedBeacons.containsKey(result.getInstanceID())
                        || System.currentTimeMillis() - connectedBeacons.get(result.getInstanceID()) > MINIMUM_NOTIFICATION_TIME){
                    Intent intent = new Intent(BeaconService.this, WebActivity.class);
                    intent.putExtra(WebActivity.URL_KEY, beacon.getUrl());
                    intent.putExtra(WebActivity.TITTLE_KEY, beacon.getName());
                    PendingIntent pendingIntent = PendingIntent.getActivity(BeaconService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification.Builder notiBuilder = new Notification.Builder(BeaconService.this);
                    notiBuilder
                            .setContentTitle("IIT Beacon")
                            .setSmallIcon(R.mipmap.ic_selected_marker)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentText(beacon.getName() + " is nearby!");
                    NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
                    connectedBeacons.put(result.getInstanceID(), System.currentTimeMillis());
                }

            }

            @Override
            public void onScanFailed(String message) {
                stopSelf();
            }
        });
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startBackgroundTask(intent, startId);
        return Service.START_STICKY;
    }

    private void  startBackgroundTask(Intent intent, int startId) {
        if(intent == null  || intent.getExtras() == null || intent.getExtras().getBoolean(ENABLE_KEY, true)) {
            scannerHandler.removeCallbacksAndMessages(null);
            scannerHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (beaconScanner != null && beaconScanner.isScannable()) {
                        beaconScanner.startScan(SCAN_LENGTH);
                        scannerHandler.postDelayed(this, SCAN_PERIOD);
                    } else {
                        if(beaconScanner != null)
                            beaconScanner.stopScan();
                        scannerHandler.removeCallbacksAndMessages(null);
                        connectedBeacons.clear();
                        stopSelf();
                    }
                }
            });
        }else {
            if(beaconScanner != null) {
                beaconScanner.stopScan();
                scannerHandler.removeCallbacksAndMessages(null);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(beaconScanner != null) {
            beaconScanner.stopScan();
            scannerHandler.removeCallbacksAndMessages(null);
            connectedBeacons.clear();
        }
    }
}