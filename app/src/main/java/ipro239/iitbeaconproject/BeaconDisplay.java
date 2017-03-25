package ipro239.iitbeaconproject;

import android.widget.ImageView;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class BeaconDisplay {

    public enum Tag{
        TAG1 (1<<0),
        TAG2 (2<<0),
        NONE (0),
        EVERY (~0);

        private final int value;
        Tag(int value){
            this.value = value;
        }
        public int getValue(){ return value; }
    }

    public class Builder{
        private BeaconDisplay beaconDisplay;

        public BeaconDisplay build(){
            return beaconDisplay;
        }

        public Builder setTags(Tag tags) {
            beaconDisplay.setTags(tags);
            return this;
        }

        public Builder setDescription(String description){
            beaconDisplay.setDescription(description);
            return this;
        }

        public Builder setUrl(String url) {
            beaconDisplay.setUrl(url);
            return this;
        }

        public Builder setLocation(Coord location) {
            beaconDisplay.setLocation(location);
            return this;
        }

        public Builder setLocation(int x, int y) {
            beaconDisplay.setLocation(new Coord(x, y));
            return this;
        }

        public Builder setMarker(ImageView marker){
            beaconDisplay.setMarker(marker);
            return this;
        }
    }

    private byte[] instanceID;
    private String name;
    private int txPower;
    private Tag tags;
    private String description;
    private String url;
    private Coord location;
    private ImageView marker;

    public BeaconDisplay(byte[] instanceID, String name, int txPower){
        this.instanceID = instanceID;
        this.name = name;
        this.txPower = txPower;
    }

    public byte[] getInstanceID() {
        return instanceID;
    }

    public Tag getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public int getTxPower() {
        return txPower;
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

    public void setInstanceID(byte[] instanceID) {
        this.instanceID = instanceID;
    }

    public void setTags(Tag tags) {
        this.tags = tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTxPower(int txPower) {
        this.txPower = txPower;
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
}
