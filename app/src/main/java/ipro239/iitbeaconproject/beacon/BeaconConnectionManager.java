package ipro239.iitbeaconproject.beacon;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;

import ipro239.iitbeaconproject.beacon.BeaconConnection;
import ipro239.iitbeaconproject.beacon.ConnectionStatus;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconConnectionManager {

    private HashMap<String, BeaconConnection> connectedBeacons = new HashMap<>();
    private Queue<BeaconConnection> connectionQue = new LinkedList<>();

    public void connect(String instanceID, int rssi){
        if(connectedBeacons.containsKey(instanceID)) {
            BeaconConnection previousConnection = connectedBeacons.get(instanceID);
            previousConnection.setStatus(ConnectionStatus.CONNECTED);
            previousConnection.setRssi(rssi);
        }else {
            BeaconConnection beaconConnection = new BeaconConnection(instanceID, rssi, ConnectionStatus.CONNECTED);
            connectionQue.add(beaconConnection);
            connectedBeacons.put(instanceID, beaconConnection);
        }
    }

    public List<BeaconConnection> getConnectedBeaconIDs(){
        List<BeaconConnection> listToReturn = new ArrayList<>();
        Iterator<String> itr = connectedBeacons.keySet().iterator();
        while (itr.hasNext()){
            BeaconConnection beaconConnection = connectedBeacons.get(itr.next());
            ConnectionStatus status = beaconConnection.getStatus();
            if(beaconConnection.getStatus().isConnected()){
                listToReturn.add(beaconConnection);
            }
        }
        return listToReturn;
    }

    public BeaconConnection popConnectionQueue(){
        return connectionQue.poll();
    }

    public void clearConnectionQue(){
        connectionQue.clear();
    }

    public void checkConnections(){
        Iterator<String> iterator =  connectedBeacons.keySet().iterator();
        while (iterator.hasNext()) {
            BeaconConnection beaconConnection = connectedBeacons.get(iterator.next());

            switch (beaconConnection.getStatus()){
                case DISCONNECTED:
                case DISCONNECTING:
                    beaconConnection.setStatus(ConnectionStatus.DISCONNECTED);
                    beaconConnection.setRssi(-1);
                    connectionQue.add(beaconConnection);
                    iterator.remove();
                    break;
                case CONNECTED:
                    beaconConnection.setStatus(ConnectionStatus.DISCONNECTING);
                    break;
            }
        }
    }

    public void disconnectAll(){
        Iterator<String> iterator =  connectedBeacons.keySet().iterator();
        while (iterator.hasNext()) {
            BeaconConnection beaconConnection = connectedBeacons.get(iterator.next());
            beaconConnection.setStatus(ConnectionStatus.DISCONNECTED);
            beaconConnection.setRssi(-1);
            connectionQue.add(beaconConnection);
            iterator.remove();
        }
    }

    public void resetConnections(){
        connectedBeacons.clear();
        connectionQue.clear();
    }
}
