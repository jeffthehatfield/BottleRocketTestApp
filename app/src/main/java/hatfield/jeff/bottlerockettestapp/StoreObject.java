package hatfield.jeff.bottlerockettestapp;

import org.json.JSONException;
import org.json.JSONObject;

public class StoreObject {
    private static final String TAG = StoreObject.class.getSimpleName();

    private String address;
    private String city;
    private String name;
    private double latitude;
    private double longitude;
    private String zipcode;
    private String logoURL;
    private String phone;
    private int storeID;
    private String state;

    public StoreObject(Object object){

        JSONObject jsonObject = (JSONObject) object;

        try {
            address = jsonObject.getString("address");
            city = jsonObject.getString("city");
            name = jsonObject.getString("name");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            logoURL = jsonObject.getString("storeLogoURL");
            zipcode = jsonObject.getString("zipcode");
            phone = jsonObject.getString("phone");
            storeID = jsonObject.getInt("storeID");
            state = jsonObject.getString("state");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String toString(){
        return "Name: "+name+", Store ID: "+storeID;
    }

    public String getFullAddress() {
        return address + " " + city + ", " + state + " " + zipcode;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLogoURL() {
        return logoURL;
    }

    public String getPhone() {
        return phone;
    }

    public int getStoreID() {
        return storeID;
    }

    public String getState() {
        return state;
    }
}
