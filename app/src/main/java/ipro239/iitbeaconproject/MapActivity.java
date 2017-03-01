package ipro239.iitbeaconproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.qozix.tileview.TileView;
import android.support.v7.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TileView tileView = new TileView(this);
        tileView.setSize(6823,13866);
        tileView.addDetailLevel(1f, "map_org_sliced/map_org_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.69998534369f, "map_70_sliced/map70_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.39997068738f, "map_40_sliced/map40_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.0999560311f, "map_10_sliced/map10_tile-%d_%d.png", 256, 256);

        ImageView downSample = new ImageView( this );
        downSample.setImageResource( R.drawable.map_tiny );
        tileView.addView( downSample, 0 );

        setContentView(tileView);
    }

}
