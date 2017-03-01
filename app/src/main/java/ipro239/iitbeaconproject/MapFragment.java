package ipro239.iitbeaconproject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qozix.tileview.TileView;
import com.qozix.tileview.widgets.ZoomPanLayout;

/**
 * Created by shuao23 on 3/1/2017.
 */

public class MapFragment extends Fragment {

    private  TileView tileView;
    private ImageView testMarker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        tileView = new TileView(getActivity());
        tileView.setSize(6823,13866);
        tileView.addDetailLevel(1f, "map_org_sliced/map_org_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.69998534369f, "map_70_sliced/map70_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.39997068738f, "map_40_sliced/map40_tile-%d_%d.png", 256, 256);
        tileView.addDetailLevel(0.0999560311f, "map_10_sliced/map10_tile-%d_%d.png", 256, 256);

        ImageView downSample = new ImageView(getActivity());
        downSample.setImageResource( R.drawable.map_tiny );
        tileView.addView( downSample, 0 );

        tileView.setScale(0);
        tileView.setMinimumScaleMode(ZoomPanLayout.MinimumScaleMode.FIT);


        testMarker = new ImageView(getActivity());
        testMarker.setImageResource(R.mipmap.ic_b_inactive);
        int beaconIconSize = getActivity().getResources().getDimensionPixelSize(R.dimen.beacon_icon_size);
        testMarker.setLayoutParams(new RelativeLayout.LayoutParams(beaconIconSize, beaconIconSize));
        tileView.addMarker(testMarker, 1500, 11177, -0.5f, -0.5f );

        return tileView;
    }

    public void TurnOnTestBeacon(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testMarker.setImageResource(R.mipmap.ic_b_active);
            }
        });

    }

    public void TurnOffTestBeacon(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testMarker.setImageResource(R.mipmap.ic_b_inactive);
            }
        });
    }
}
