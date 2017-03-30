package ipro239.iitbeaconproject.activities.helper;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ipro239.iitbeaconproject.R;
import ipro239.iitbeaconproject.beacon.Beacon;

/**
 * Created by shuao23 on 3/30/2017.
 */

public class BeaconRVAdapter extends RecyclerView.Adapter<BeaconViewHolder> {

    List<Beacon> beacons;

    public BeaconRVAdapter(List<Beacon> beacons){
        this.beacons = beacons;
    }

    @Override
    public BeaconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beacon_cardview, parent, false);
        BeaconViewHolder holder = new BeaconViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(BeaconViewHolder holder, int position) {
        holder.getTittleView().setText(beacons.get(position).getName());
        holder.getDescriptionView().setText(beacons.get(position).getDescription());
        holder.getTypeIconView().setImageResource(BeaconIcon.getIconIDByTag(beacons.get(position).getTags()));
    }

    @Override
    public int getItemCount() {
        return beacons.size();
    }
}
