package ipro239.iitbeaconproject.activities.helper;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/30/2017.
 */

public class BeaconViewHolder extends RecyclerView.ViewHolder {

    private CardView container;
    private TextView tittleView;
    private TextView descriptionView;
    private ImageView typeIconView;
    private String url;


    public BeaconViewHolder(View itemView) {
        super(itemView);

        container = (CardView)itemView;
        tittleView = (TextView)itemView.findViewById(R.id.beacon_name);
        descriptionView = (TextView)itemView.findViewById(R.id.beacon_description);
        typeIconView = (ImageView)itemView.findViewById(R.id.beacon_type_icon);
    }

    public CardView getContainer() {
        return container;
    }

    public TextView getTittleView() {
        return tittleView;
    }

    public TextView getDescriptionView() {
        return descriptionView;
    }

    public ImageView getTypeIconView() {
        return typeIconView;
    }

    public String getUrl() {
        return url;
    }

    public void setContainer(CardView container) {
        this.container = container;
    }

    public void setTittleView(TextView tittleView) {
        this.tittleView = tittleView;
    }

    public void setDescriptionView(TextView descriptionView) {
        this.descriptionView = descriptionView;
    }

    public void setTypeIconView(ImageView typeIconView) {
        this.typeIconView = typeIconView;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

