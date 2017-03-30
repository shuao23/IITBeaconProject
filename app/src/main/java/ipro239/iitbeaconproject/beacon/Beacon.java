package ipro239.iitbeaconproject.beacon;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class Beacon implements Parcelable{

    public static final int TAG_NONE = 0;
    public static final int TAG_ALL = ~0;

    public static class Builder{
        private Beacon beacon = new Beacon();

        public Beacon build(){
            return beacon;
        }

        public Builder setInstanceID(String id){
            beacon.setInstanceID(id);
            return this;
        }

        public Builder setName(String name){
            beacon.setName(name);
            return this;
        }

        public Builder setTags(int tags) {
            beacon.setTags(tags);
            return this;
        }

        public Builder setDescription(String description){
            beacon.setDescription(description);
            return this;
        }

        public Builder setUrl(String url) {
            beacon.setUrl(url);
            return this;
        }

        public Builder setLocation(Coord location) {
            beacon.setLocation(location);
            return this;
        }

        public Builder setLocation(int x, int y) {
            beacon.setLocation(new Coord(x, y));
            return this;
        }

        public Builder setMarker(ImageView marker){
            beacon.setMarker(marker);
            return this;
        }
    }

    private String instanceID;
    private String name;
    private int tags;
    private String description;
    private String url;
    private Coord location;
    private ImageView marker;

    public Beacon(){}

    public Beacon(Parcel in ) {
        readFromParcel(in);
    }

    public String getInstanceID() {
        return instanceID;
    }

    public int getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public Coord getLocation() {
        return location;
    }
    public int getLocationX() {
        return location.x;
    }
    public int getLocationY() {
        return location.y;
    }

    public ImageView getMarker() {
        return marker;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocation(Coord location) {
        this.location = location;
    }

    public void setLocation(int x, int y) {
        this.location = new Coord(x, y);
    }

    public void setMarker(ImageView marker) {
        this.marker = marker;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Beacon createFromParcel(Parcel in ) {
            return new Beacon( in );
        }

        public Beacon[] newArray(int size) {
            return new Beacon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getDescription());
        dest.writeString(getUrl());
        dest.writeInt(getTags());
    }

    public void readFromParcel(Parcel in){
        setName(in.readString());
        setDescription(in.readString());
        setUrl(in.readString());
        setTags(in.readInt());
    }
}
