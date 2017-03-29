package ipro239.iitbeaconproject.beacon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;

import ipro239.iitbeaconproject.beacon.BeaconConnection;
import ipro239.iitbeaconproject.beacon.ConnectionStatus;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconConnectionManager {

    private HashMap<String, Boolean> connectedBeacons = new HashMap<>();
    private Queue<BeaconConnection> connectionQue = new LinkedList<>();

    public void connect(String instanceID){
        if(!connectedBeacons.containsKey(instanceID))
            connectionQue.add(new BeaconConnection(instanceID, ConnectionStatus.CONNECTED));
        connectedBeacons.put(instanceID,true);
    }

    public BeaconConnection popConnectionQueue(){
        return connectionQue.poll();
    }

    public void checkConnections(){
        Iterator<String> iterator =  connectedBeacons.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (!connectedBeacons.get(next)) {
                connectionQue.add(new BeaconConnection(next, ConnectionStatus.DISCONNECTED));
                iterator.remove();
            }else{
                connectedBeacons.put(next,false);
            }
        }

    }
}
