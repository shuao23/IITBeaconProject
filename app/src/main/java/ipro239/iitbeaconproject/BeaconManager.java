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

public class BeaconManager {

    private static final Byte[] PDU = {
            0x2,0x1,0x6,0x3,
            0x3,(byte)0xAA,(byte)0xFE,0x17,
            0x16,(byte)0xAA,(byte)0xFE,null };
    private Byte[] UUID = {
            (byte)0xBB,(byte)0xC7,0x66,(byte)0xB6,
            0x68,0x1D,(byte)0x82,(byte)0x9D,
            0x58,0x0D};
    private static final float CONNECTION_TIMEOUT = 3000;

    private enum Status{
        CONNECTED, DISCONNECTED
    }

    private class BeaconConnection{
        public BeaconConnection(Byte[] id, Status status){
            this.id = id;
            this.status = status;
        }

        Byte[] id;
        Status status;
    }

    private OnTimeoutListener onTimeoutListener;
    private Timer timer = new Timer();
    private HashMap<Byte[], Long> connectedBeacons = new HashMap<>();
    private Queue<BeaconConnection> connectionQue = new LinkedList<>();

    public void setOnTimeoutListener(OnTimeoutListener onTimeoutListener){
        this.onTimeoutListener = onTimeoutListener;
    }

    public void connect(Byte[] instanceID){
        if(!connectedBeacons.containsKey(instanceID))
            connectionQue.add(new BeaconConnection(instanceID, Status.CONNECTED));
        connectedBeacons.put(instanceID, System.currentTimeMillis());
    }

    public Byte[] popConnectionQueue(){
        return connectionQue.poll().id;
    }

    public void checkConnection(){
        Iterator<Byte[]> iterator =  connectedBeacons.keySet().iterator();
        while (iterator.hasNext()) {
            Byte[] next = iterator.next();
            if (connectedBeacons.get(next) + CONNECTION_TIMEOUT > System.currentTimeMillis()) {
                connectionQue.add(new BeaconConnection(next, Status.DISCONNECTED));
                iterator.remove();
            }
        }

    }
}
