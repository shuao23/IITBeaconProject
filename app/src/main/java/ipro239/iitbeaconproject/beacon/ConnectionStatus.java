package ipro239.iitbeaconproject.beacon;

/**
 * Created by shuao23 on 3/28/2017.
 */

public enum ConnectionStatus {
    DISCONNECTED (0),
    CONNECTED (1),
    DISCONNECTING (2);

    private final int value;

    ConnectionStatus(final int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public boolean isConnected(){
        return value != 0;
    }
}
