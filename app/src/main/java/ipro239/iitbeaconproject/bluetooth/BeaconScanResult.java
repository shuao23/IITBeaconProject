package ipro239.iitbeaconproject.bluetooth;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconScanResult {
    private String namespaceID;
    private int txPower;

    public BeaconScanResult(){}
    public BeaconScanResult(String namespaceID, int txPower){
        this.namespaceID = namespaceID;
        this.txPower = txPower;
    }

    public int getTxPower() {
        return txPower;
    }

    public String getNamespaceID() {
        return namespaceID;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }

    public void setNamespaceID(String namespaceID) {
        this.namespaceID = namespaceID;
    }
}
