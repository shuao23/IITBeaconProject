package ipro239.iitbeaconproject;

import android.app.Activity;
import android.view.ViewManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qozix.tileview.TileView;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconDisplayer {

    private BeaconDisplay.Tag displayTag;
    private Activity activity;
    private TileView tileView;
    private HashMap<byte[],BeaconDisplay> beacons;

    public BeaconDisplayer(Activity activity, TileView tileView){
        this.activity = activity;
        this.tileView = tileView;
    }

    public void updateDisplay(){

    }

    public void addBeacon(BeaconDisplay beaconDisplay){
        createBeaconMarker(beaconDisplay);
        beacons.put(beaconDisplay.getInstanceID(), beaconDisplay);
    }

    public void removeBeacon(BeaconDisplay beaconDisplay){
        removeBeaconMarker(beaconDisplay);
        beacons.remove(beaconDisplay.getInstanceID());
    }

    public void removeAllBeacons(){
        Iterator<BeaconDisplay> itr = beacons.values().iterator();
        while(itr.hasNext()){
            BeaconDisplay beacon = itr.next();
            removeBeaconMarker(beacon);
        }
        beacons.clear();
    }

    public void setDisplayTag(BeaconDisplay.Tag displayTag) {
        this.displayTag = displayTag;
    }

    public BeaconDisplay.Tag getDisplayTag() {
        return displayTag;
    }

    public BeaconDisplay getBeacon(byte[] instanceID){
        return beacons.get(instanceID);
    }

    public int getBeaconCount(){
        return beacons.size();
    }

    private void createBeaconMarker(BeaconDisplay beacon){
        ImageView beaconMarker = new ImageView(activity);
        beaconMarker.setImageResource(R.mipmap.ic_b_inactive);
        int beaconIconSize = activity.getResources().getDimensionPixelSize(R.dimen.beacon_icon_size);
        beaconMarker.setLayoutParams(new RelativeLayout.LayoutParams(beaconIconSize, beaconIconSize));
        tileView.addMarker(beaconMarker, beacon.getLocationX(), beacon.getLocationY(), -0.5f, -0.5f);
        beacon.setMarker(beaconMarker);
    }

    private void removeBeaconMarker(BeaconDisplay beacon){
        if(beacon != null && beacon.getMarker() != null) {
            tileView.removeMarker(beacon.getMarker());
            beacon.setMarker(null);
        }
    }
}
