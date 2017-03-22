package smartapp.mortgagecalculator;

/**
 * Created by aashayshah on 3/21/17.
 */

public class LocationInfoObject {
    String propertyPrice;
    String downPayment;
    String apr;
    String terms;
    String loanAmount;
    String monthlyPayment;


    String address;
    String city;
    String zip;
    String state;
    String propertyType;

    String latitude;
    String longitude;

    String id;

    public LocationInfoObject(String propertyPrice, String downPayment, String apr, String terms, String loanAmount, String monthlyPayment, String address, String propertyType, String city, String zip, String state, String latitude, String longitude) {
        this.propertyPrice = propertyPrice;
        this.downPayment = downPayment;
        this.apr = apr;
        this.terms = terms;
        this.loanAmount = loanAmount;
        this.monthlyPayment = monthlyPayment;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.propertyType = propertyType;
    }

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(String downPayment) {
        this.downPayment = downPayment;
    }

    public String getApr() {
        return apr;
    }

    public void setApr(String apr) {
        this.apr = apr;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(String monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getpropertyType() {
        return propertyType;
    }

    public void setpropertyType(String propertyType) {
        this.propertyType = propertyType;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
