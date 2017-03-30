package ipro239.iitbeaconproject.activities.helper;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/30/2017.
 */

public class BeaconIcon {
    public static int getIconIDByTag(int tag){
        switch (tag){
            case (1<<0):
                return R.mipmap.ic_launcher;
            case (1<<1):
            default:
                return R.mipmap.ic_b_active;
        }
    }
}
