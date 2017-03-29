package ipro239.iitbeaconproject.beacon;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconConnection{
    public BeaconConnection(String id, ConnectionStatus status){
        this.id = id;
        this.status = status;
    }

    private String id;
    private ConnectionStatus status;

    public ConnectionStatus getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
