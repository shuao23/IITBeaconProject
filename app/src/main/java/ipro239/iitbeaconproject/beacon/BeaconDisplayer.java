package ipro239.iitbeaconproject.beacon;

import android.app.Activity;
import android.view.View;
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
    private ImageView currentMarker;
    private Beacon selectedBeacon;
    private HashMap<String,Beacon> beacons = new HashMap<>();

    public BeaconDisplayer(Activity activity, TileView tileView){
        this.activity = activity;
        this.tileView = tileView;

        //Initialize marker
        currentMarker = new ImageView(activity);
        currentMarker.setImageResource(R.mipmap.ic_selected_marker);
        int markerSize = activity.getResources().getDimensionPixelSize(R.dimen.selected_marker_size);
        currentMarker.setLayoutParams(new RelativeLayout.LayoutParams(markerSize, markerSize));
    }

    public void updateDisplay(){
        removeAllDisplayedBeacons();
        Beacon removedSelectedBeacon = selectedBeacon;
        removeCurrentBeacon();
        Iterator<Beacon> it = beacons.values().iterator();
        while (it.hasNext()){
            Beacon beacon = it.next();
            if(validFlag(beacon.getTags())){
                createBeaconMarker(beacon);
            }
        }
        if(removedSelectedBeacon != null)
            selectCurrentBeacon(removedSelectedBeacon.getInstanceID());
    }

    public void changeBeaconState(String id, boolean isOn){
        if(!beacons.containsKey(id))
            return;

        Beacon beacon = beacons.get(id);

        //If flag is currently getting shown
        if(validFlag(beacon.getTags())) {
            int imageID;
            if (isOn)
                imageID = BeaconIcons.getActiveMapIconIDByTag(beacon.getTags());
            else
                imageID = BeaconIcons.getInactiveMapIconIDByTag(beacon.getTags());

            beacons.get(id).getMarker().setImageResource(imageID);
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

    public void selectCurrentBeacon(Beacon beacon){
        selectCurrentBeacon(beacon.getInstanceID());
    }
    public void selectCurrentBeacon(String id){
        removeCurrentBeacon();
        if(beacons.containsKey(id)){
            Beacon beacon = beacons.get(id);
            if(validFlag(beacon.getTags())){
                tileView.addMarker(currentMarker, beacon.getLocationX(), beacon.getLocationY(), -0.5f, -1.0f);
                selectedBeacon = beacon;
            }
        }
    }

    public void removeCurrentBeacon(){
        tileView.removeMarker(currentMarker);
        selectedBeacon = null;
    }

    public Beacon findBeaconByImageView(View view){
        if(view == null || !(view instanceof ImageView))
            return null;

        Iterator<String> itr = beacons.keySet().iterator();
        while (itr.hasNext()){
            Beacon beacon = beacons.get(itr.next());
            if(beacon.getMarker() == view){
                return beacon;
            }
        }
        return null;
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
        beaconMarker.setImageResource(BeaconIcons.getInactiveMapIconIDByTag(beacon.getTags()));
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
