package ipro239.iitbeaconproject.bluetooth;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconScanResult {
    private String instanceID;
    private int txPower;

    public BeaconScanResult(){}
    public BeaconScanResult(String namespaceID, int txPower){
        this.instanceID = namespaceID;
        this.txPower = txPower;
    }

    public int getTxPower() {
        return txPower;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }
}
