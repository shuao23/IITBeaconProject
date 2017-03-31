package ipro239.iitbeaconproject.beacon;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconConnection{
    public BeaconConnection(String id, int rssi, ConnectionStatus status){
        this.id = id;
        this.rssi = rssi;
        this.status = status;
    }

    private String id;
    private int rssi;
    private ConnectionStatus status;

    public ConnectionStatus getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public int getRssi() {
        return rssi;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRssi(int txPower) {
        this.rssi = txPower;
    }
}
