package ipro239.iitbeaconproject.beacon;

import android.content.Context;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 4/4/2017.
 */

public class BeaconDatabase {

    private static BeaconDatabase instance;
    private Context context;
    private HashMap<String, Beacon> beacons;

    private BeaconDatabase(Context context){
        this.context = context;
    }

    public static boolean isInit(){
        return instance != null && instance.context != null;
    }

    public static void init(Context context){
        instance = new BeaconDatabase(context);
        instance.readBeaconData();
    }

    public static Beacon getBeacon(String id){
        if(!isInit())
            return null;

        return instance.beacons.get(id);
    }

    public static Iterator<Beacon> getBeaconIter(){
        if(!isInit())
            return null;

        return instance.beacons.values().iterator();
    }

    public static int getBeaconCount(){
        if(!isInit())
            return -1;

        return instance.beacons.size();
    }

    private void readBeaconData(){
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

        beacons = new HashMap<String, Beacon>();
        for(int i = 0; i < result.size(); i++){
            beacons.put(result.get(i).getInstanceID(), result.get(i));
        }
    }

}
