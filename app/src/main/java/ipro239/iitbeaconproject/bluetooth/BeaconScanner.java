package ipro239.iitbeaconproject.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconScanner {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bleScanner;
    private Handler bluetoothHandler = new Handler();
    private ScanCallback leScanCallback;
    private BluetoothAdapter.LeScanCallback old_leScanCallback;
    private BLEScanCallbackInterface BLEScanCallbackListener;
    private int scanMode = ScanSettings.SCAN_MODE_BALANCED;
    private boolean isScanning;

    public BeaconScanner(BluetoothAdapter bluetoothAdapter){
        if(bluetoothAdapter == null)
            return;

        this.bluetoothAdapter = bluetoothAdapter;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        initBLECallback();
    }

    public boolean initialized(){
        return bluetoothAdapter != null;
    }

    public boolean isScanning(){
        return isScanning;
    }

    public void setScanMode(int scanMode){
        this.scanMode = scanMode;
    }

    public void setBluetoothAdapter(BluetoothAdapter adapter){
        if(bluetoothAdapter == null)
            return;

        this.bluetoothAdapter = adapter;
        initBLECallback();
    }

    public void setBLEScanCallbackListener(BLEScanCallbackInterface BLEScanCallbackListener) {
        this.BLEScanCallbackListener = BLEScanCallbackListener;
    }

    public int getScanMode() {
        return scanMode;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startScan(int scanLength){
        if(bluetoothAdapter == null)
            return;

        if(isScanning)
            return;

        bluetoothHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
            }
        }, scanLength);

        if(bleScanner == null)
            bluetoothAdapter.startLeScan(old_leScanCallback);
        else{
            ScanSettings.Builder settingBuilder = new ScanSettings.Builder();
            settingBuilder.setScanMode(scanMode);
            bleScanner.startScan(null,settingBuilder.build(),leScanCallback);
        }

        isScanning = true;
        if(BLEScanCallbackListener != null)
            BLEScanCallbackListener.onScanStart();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScan(){
        if(bluetoothAdapter == null)
            return;

        if(!isScanning)
            return;

        if(bleScanner == null)
            bluetoothAdapter.stopLeScan(old_leScanCallback);
        else
            bleScanner.stopScan(leScanCallback);
        bluetoothHandler.removeCallbacksAndMessages(null);

        isScanning = false;
        if(BLEScanCallbackListener != null)
            BLEScanCallbackListener.onScanEnd();
    }

    private boolean BLECallbackInitDone(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return leScanCallback != null;
        }else{
            return old_leScanCallback != null;
        }
    }

    private void parseResultAndCallback(byte[] scanRecord, int rssi){
        BeaconScanResultParser parser = new BeaconScanResultParser();
        BeaconScanResult parsedResult = parser.parse(scanRecord,
                BeaconScannerHelper.PDU, BeaconScannerHelper.UUID);
        if(parsedResult != null){
            parsedResult.setRssi(rssi);
            if (parsedResult.getRssi() >= parsedResult.getTxPower()) {
                BLEScanCallbackListener.onScanResult(parsedResult);
            }
            BLEScanCallbackListener.onRawScanResult(parsedResult);
        }
    }

    private void initBLECallback(){
        if(bluetoothAdapter == null)
            return;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            leScanCallback = new ScanCallback() {
                @Override
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public void onScanResult(int callbackType, ScanResult scanResult) {
                    if(BLEScanCallbackListener == null)
                        return;

                    parseResultAndCallback(scanResult.getScanRecord().getBytes(), scanResult.getRssi());
                }

                //IDK what this is but never gets called
                /*@Override
                public void onBatchScanResults(List<ScanResult> results) { }*/

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

                    if(BLEScanCallbackListener != null)
                        BLEScanCallbackListener.onScanFailed(message);
                }
            };
        }else{
            old_leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(BLEScanCallbackListener == null)
                        return;

                    parseResultAndCallback(scanRecord, rssi);
                }
            };
        }
    }
}
