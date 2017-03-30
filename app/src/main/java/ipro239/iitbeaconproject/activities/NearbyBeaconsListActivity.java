package ipro239.iitbeaconproject.activities;

import java.util.List;

import ipro239.iitbeaconproject.beacon.Beacon;

/**
 * Created by shuao23 on 3/30/2017.
 */

public class NearbyBeaconsListActivity extends BeaconListActivity {
    @Override
    protected List<Beacon> getData() {
        return getIntent().getExtras().getParcelableArrayList(BEACON_LIST_KEY);
    }
}
