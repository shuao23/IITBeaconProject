package ipro239.iitbeaconproject.bluetooth;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconScanResult {
    private String instanceID;
    private int txPower;
    private int rssi;

    public BeaconScanResult(){}
    public BeaconScanResult(String namespaceID, int txPower, int rssi){
        this.instanceID = namespaceID;
        this.txPower = txPower;
        this.rssi = rssi;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public int getTxPower() {
        return txPower;
    }

    public int getRssi() {
        return rssi;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
