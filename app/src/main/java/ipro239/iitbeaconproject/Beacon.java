package ipro239.iitbeaconproject;

import android.content.res.Resources;

import java.net.URL;

/**
 * Created by shuao23 on 3/24/2017.
 */

public class Beacon {

    private int instance;
    private String name;
    private String description;
    private String url;
    private Coord location;

    public Beacon(int instance, String name){
        this.instance = instance;
        this.name = name;
    }

    public int getInstance() {
        return instance;
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

    public void setInstance(int instance) {
        this.instance = instance;
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
}
