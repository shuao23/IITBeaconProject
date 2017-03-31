package ipro239.iitbeaconproject.beacon;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/30/2017.
 */

public class BeaconIcons {
    public static int getCardIconIDByTag(int tag){
        switch (tag){
            case (1<<0):
                return R.mipmap.ic_food;
            case (1<<1):
                return R.mipmap.ic_sightseeing;
            case (1<<2):
                return R.mipmap.ic_entertainment;
            case (1<<3):
                return R.mipmap.ic_student_res;
            default:
                return R.mipmap.ic_launcher;
        }
    }

    public static int getActiveMapIconIDByTag(int tag){
        switch (tag){
            case (1<<0):
            default:
                return R.mipmap.ic_b_active;
        }
    }

    public static int getInactiveMapIconIDByTag(int tag){
        switch (tag){
            case (1<<0):
            default:
                return R.mipmap.ic_b_inactive;
        }
    }
}
