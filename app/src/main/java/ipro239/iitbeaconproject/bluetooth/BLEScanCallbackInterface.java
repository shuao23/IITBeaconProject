package ipro239.iitbeaconproject.bluetooth;

/**
 * Created by shuao23 on 3/28/2017.
 */

public interface BLEScanCallbackInterface {
    void onScanStart();
    void onScanEnd();
    void onScanResult();
    void onRawScanResult();
    void onScanFailed(String message);
}
