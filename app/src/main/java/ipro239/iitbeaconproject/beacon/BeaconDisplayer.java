package ipro239.iitbeaconproject.beacon;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qozix.tileview.TileView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconDisplayer {

    private int displayTag = Beacon.TAG_ALL;
    private Activity activity;
    private TileView tileView;
    private HashMap<String,Beacon> beacons = new HashMap<>();
    private HashMap<ImageView,String> beaconIcons = new HashMap<>();

    public BeaconDisplayer(Activity activity, TileView tileView){
        this.activity = activity;
        this.tileView = tileView;
    }

    public void updateDisplay(){
        removeAllDisplayedBeacons();
        Iterator<Beacon> it = beacons.values().iterator();
        while (it.hasNext()){
            Beacon beacon = it.next();
            if(validFlag(beacon.getTags())){
                createBeaconMarker(beacon);
            }
        }
    }

    public void changeBeaconState(String id, boolean isOn){
        if(!beacons.containsKey(id))
            return;

        Beacon beacon = beacons.get(id);

        //If flag is currently getting shown
        if(validFlag(beacon.getTags())) {
            if (isOn)
                beacons.get(id).getMarker().setImageResource(R.mipmap.ic_b_active);
            else
                beacons.get(id).getMarker().setImageResource(R.mipmap.ic_b_inactive);
        }

    }

    public void addBeacon(Beacon beacon){
        if(beacon == null)
            return;
        beacons.put(beacon.getInstanceID(), beacon);
    }

    public void addBeacons(List<Beacon> beacons){
        if(beacons != null){
            for(int i = 0; i < beacons.size(); i++){
                Beacon beacon = beacons.get(i);
                if(beacon != null)
                    this.beacons.put(beacon.getInstanceID(), beacon);
            }
        }
    }

    public void removeBeacon(Beacon beaconDisplay){
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

    public Beacon getBeacon(String instanceID){
        return beacons.get(instanceID);
    }

    public int getBeaconCount(){
        return beacons.size();
    }

    public boolean isDisplayedBeacon(String instanceID){
        if(instanceID == null)
            return false;

        if(!beacons.containsKey(instanceID))
            return false;

        if(!validFlag(beacons.get(instanceID).getTags()))
            return false;

        return true;
    }

    private boolean validFlag(int beaconFlag){
        return (beaconFlag == 0 || (beaconFlag & displayTag) != 0);
    }

    private void removeAllDisplayedBeacons(){
        Iterator<Beacon> itr = beacons.values().iterator();
        while(itr.hasNext()){
            Beacon beacon = itr.next();
            removeBeaconMarker(beacon);
        }
    }

    private void createBeaconMarker(Beacon beacon){
        ImageView beaconMarker = new ImageView(activity);
        beaconMarker.setImageResource(R.mipmap.ic_b_inactive);
        int beaconIconSize = activity.getResources().getDimensionPixelSize(R.dimen.beacon_icon_size);
        beaconMarker.setLayoutParams(new RelativeLayout.LayoutParams(beaconIconSize, beaconIconSize));
        tileView.addMarker(beaconMarker, beacon.getLocationX(), beacon.getLocationY(), -0.5f, -0.5f);
        beacon.setMarker(beaconMarker);
    }

    private void removeBeaconMarker(Beacon beacon){
        if(beacon != null && beacon.getMarker() != null) {
            tileView.removeMarker(beacon.getMarker());
            beacon.setMarker(null);
        }
    }
}
