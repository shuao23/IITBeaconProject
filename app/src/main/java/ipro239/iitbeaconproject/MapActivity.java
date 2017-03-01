package ipro239.iitbeaconproject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.qozix.tileview.TileView;
import android.support.v7.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction fragTrans = getFragmentManager().beginTransaction();
        MapFragment mapFragment = new MapFragment();
        fragTrans.add(R.id.map_fragment, mapFragment);
        fragTrans.commit();
        setContentView(R.layout.activity_map);

        //Beacon Setup

    }

}
