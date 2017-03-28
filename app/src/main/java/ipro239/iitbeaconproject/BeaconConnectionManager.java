package ipro239.iitbeaconproject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconConnectionManager {

    private static final float CONNECTION_TIMEOUT = 3000;

    public enum Status{
        CONNECTED, DISCONNECTED
    }

    public class BeaconConnection{
        public BeaconConnection(String id, Status status){
            this.id = id;
            this.status = status;
        }

        String id;
        Status status;
    }

    private Timer timer = new Timer();
    private HashMap<String, Boolean> connectedBeacons = new HashMap<>();
    private Queue<BeaconConnection> connectionQue = new LinkedList<>();

    public void connect(String instanceID){
        if(!connectedBeacons.containsKey(instanceID))
            connectionQue.add(new BeaconConnection(instanceID, Status.CONNECTED));
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
                connectionQue.add(new BeaconConnection(next, Status.DISCONNECTED));
                iterator.remove();
            }else{
                connectedBeacons.put(next,false);
            }
        }

    }
}
