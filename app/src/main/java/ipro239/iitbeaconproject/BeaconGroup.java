package ipro239.iitbeaconproject;

import java.util.List;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconGroup {

    private String name;
    private List<Beacon> beacons;


    public void addBeacon(Beacon  beacon){
        beacons.add(beacon);
    }

    public void removeBeacon(Beacon beacon){
        beacons.remove(beacon);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Beacon getBeacon(int index){
        return beacons.get(index);
    }

    public int getBeaconCount(){
        return beacons.size();
    }
}
