package ipro239.iitbeaconproject.bluetooth;

/**
 * Created by shuao23 on 3/28/2017.
 */

public abstract class BLEScanCallback implements BLEScanCallbackInterface {
    public void onScanStart(){ }
    public void onScanEnd(){ }
    public void onScanResult(){ }
    public void onRawScanResult(){ }
    public void onScanFailed(String message){ }
}
