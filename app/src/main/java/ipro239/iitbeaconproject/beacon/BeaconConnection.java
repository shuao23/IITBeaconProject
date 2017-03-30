package ipro239.iitbeaconproject.beacon;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconConnection{
    public BeaconConnection(String id, int txPower, ConnectionStatus status){
        this.id = id;
        this.txPower = txPower;
        this.status = status;
    }

    private String id;
    private int txPower;
    private ConnectionStatus status;

    public ConnectionStatus getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public int getTxPower() {
        return txPower;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
    }
}
