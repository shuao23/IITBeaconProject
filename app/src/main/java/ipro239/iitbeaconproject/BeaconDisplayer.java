package ipro239.iitbeaconproject;

import android.app.Activity;
import android.util.Log;
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

    private int displayTag = BeaconDisplay.TAG_ALL;
    private Activity activity;
    private TileView tileView;
    private HashMap<String,BeaconDisplay> beacons = new HashMap<>();

    public BeaconDisplayer(Activity activity, TileView tileView){
        this.activity = activity;
        this.tileView = tileView;
    }

    public void updateDisplay(){
        removeAllDisplayedBeacons();
        Iterator<BeaconDisplay> it = beacons.values().iterator();
        while (it.hasNext()){
            BeaconDisplay beacon = it.next();
            if(beacon.getTags() == 0 || (beacon.getTags() & displayTag) != 0){
                createBeaconMarker(beacon);
            }
        }
    }

    public void changeBeaconState(String id, boolean isOn){
        if(!beacons.containsKey(id))
            return;

        if(isOn)
            beacons.get(id).getMarker().setImageResource(R.mipmap.ic_b_active);
        else
            beacons.get(id).getMarker().setImageResource(R.mipmap.ic_b_inactive);

    }

    public void addBeacon(BeaconDisplay beaconDisplay){
        beacons.put(beaconDisplay.getInstanceID(), beaconDisplay);
    }

    public void removeBeacon(BeaconDisplay beaconDisplay){
        removeBeaconMarker(beaconDisplay);
        beacons.remove(beaconDisplay.getInstanceID());
    }

    public void removeAllBeacons(){
        removeAllDisplayedBeacons();
        beacons.clear();
    }

    public void setDisplayTag(int displayTag) {
        this.displayTag = displayTag;
    }

    public int getDisplayTag() {
        return displayTag;
    }

    public BeaconDisplay getBeacon(String instanceID){
        return beacons.get(instanceID);
    }

    public int getBeaconCount(){
        return beacons.size();
    }

    private void removeAllDisplayedBeacons(){
        Iterator<BeaconDisplay> itr = beacons.values().iterator();
        while(itr.hasNext()){
            BeaconDisplay beacon = itr.next();
            removeBeaconMarker(beacon);
        }
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
