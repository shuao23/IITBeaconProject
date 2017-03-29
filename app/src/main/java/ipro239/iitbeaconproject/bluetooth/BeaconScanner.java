package ipro239.iitbeaconproject.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

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

    public void setBluetoothAdapter(BluetoothAdapter adapter){
        if(bluetoothAdapter == null)
            return;

        this.bluetoothAdapter = adapter;
        initBLECallback();
    }

    public void setBLEScanCallbackListener(BLEScanCallbackInterface BLEScanCallbackListener) {
        this.BLEScanCallbackListener = BLEScanCallbackListener;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startScan(int scanLength){
        if(bluetoothAdapter == null)
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
            bleScanner.startScan(leScanCallback);
        }

        if(BLEScanCallbackListener != null)
            BLEScanCallbackListener.onScanStart();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScan(){
        if(bluetoothAdapter == null)
            return;

        if(bleScanner == null)
            bluetoothAdapter.stopLeScan(old_leScanCallback);
        else
            bleScanner.stopScan(leScanCallback);

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

                    BeaconScanResultParser parser = new BeaconScanResultParser();
                    BeaconScanResult parsedResult = parser.parse(scanResult.getScanRecord().getBytes(),
                            BeaconScannerHelper.PDU, BeaconScannerHelper.UUID);
                    if(parsedResult != null){
                        if(scanResult.getRssi() >= parsedResult.getTxPower()) {
                            BLEScanCallbackListener.onScanResult(parsedResult);
                        }
                        BLEScanCallbackListener.onRawScanResult(parsedResult);
                    }
                }

                //IDK what this is but never gets called
                /*@Override
                public void onBatchScanResults(List<ScanResult> results) {}*/

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

                    BeaconScanResult result = new BeaconScanResultParser().
                            parse(scanRecord, BeaconScannerHelper.PDU, BeaconScannerHelper.UUID);
                    if(result != null && rssi >= result.getTxPower())
                            BLEScanCallbackListener.onScanResult(result);
                    BLEScanCallbackListener.onRawScanResult(result);
                }
            };
        }
    }
}
