package ipro239.iitbeaconproject.beacon;

import android.content.Context;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 4/4/2017.
 */

public class BeaconDatabase {

    private static BeaconDatabase instance;
    private Context context;
    private List<Beacon> beacons;

    private BeaconDatabase(Context context){
        this.context = context;
    }

    public static boolean isInit(){
        return instance != null && instance.context != null;
    }

    public static void init(Context context){
        instance = new BeaconDatabase(context);
        instance.beacons = instance.readBeaconData();
    }

    public static Beacon getBeacon(int idx){
        if(!isInit())
            return null;

        return instance.beacons.get(idx);
    }

    public static int getBeaconCount(){
        if(!isInit())
            return -1;

        return instance.beacons.size();
    }

    private List<Beacon> readBeaconData(){
        //Display beacons
        List<Beacon> result;
        InputStream inputStream = context.getResources().openRawResource(R.raw.beacons);
        BeaconXMLParser parser = new BeaconXMLParser();
        try {
            try {
                result = parser.parse(inputStream);

            }finally {
                inputStream.close();
            }
        }catch (Exception e){
            result = null;
            Toast.makeText(context, "Could not load beacons", Toast.LENGTH_LONG).show();
        }
        return result;
    }

}
