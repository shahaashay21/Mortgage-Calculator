package smartapp.mortgagecalculator;

/**
 * Created by aashayshah on 3/21/17.
 */

public class LocationInfoObject {
    String address;
    String city;
    String zip;
    String state;

    public LocationInfoObject(String address, String city, String zip, String state) {
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
