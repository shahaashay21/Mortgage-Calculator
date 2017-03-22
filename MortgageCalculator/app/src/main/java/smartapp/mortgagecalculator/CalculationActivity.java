package smartapp.mortgagecalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CalculationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText propertyPrice;
    EditText downPayment;
    EditText apr;
    RadioGroup term;
    Button calculation;
    TextView monthlyPayment;
    TextView loanAmount;
    Button save;
    Button reset;
    Integer years;
    EditText address, city, state, zip;
    private Spinner propertyspinner, statespinner;

    String addressString;
    String cityString;
    String zipString;
    String stateSpinnerString;
    String propertyTypeString;

    String finalAddress;
    double[] latlng;

    BigDecimal propertyPriceValue;
    BigDecimal downPaymentValue;
    BigDecimal aprValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        propertyPrice = (EditText) findViewById(R.id.price);
        downPayment = (EditText) findViewById(R.id.downpayment);
        apr = (EditText) findViewById(R.id.apr);
        term = (RadioGroup) findViewById(R.id.radioTerm);

        calculation = (Button) findViewById(R.id.calculate);

        loanAmount = (TextView) findViewById(R.id.loanAmount);
        monthlyPayment = (TextView) findViewById(R.id.monthlyPayment);

        save = (Button) findViewById(R.id.save);
        reset = (Button) findViewById(R.id.reset);

        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        zip = (EditText) findViewById(R.id.zip);
        statespinner = (Spinner) findViewById(R.id.stateSpinner);
        propertyspinner = (Spinner) findViewById(R.id.propertySpinner);




        //Click on calculation button
        calculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true) {
                    Intent i = new Intent(getApplicationContext(), MapLocation.class);
                    startActivity(i);
                }else {
                    int selectedTermId = term.getCheckedRadioButtonId();
                    Button termValue = (Button) findViewById(selectedTermId);
                    if (termValue.getText().equals("15 yrs.")) {
                        years = 15;
                    } else if (termValue.getText().equals("30 yrs.")) {
                        years = 30;
                    }
                    //Null field validation
                    if (nullFieldValidation()) {
                        //Get all the values in respective data types
                        setValues();

                        //Validate each fields
                        if (fieldValidation()) {
                            //Calculate and show
                            BigDecimal[] loanMonthlyPayment = calculate();
                            loanAmount.setText("$" + String.valueOf(loanMonthlyPayment[0]));
                            monthlyPayment.setText("$" + String.valueOf(loanMonthlyPayment[1]));


                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
            }
        });

        //Click on save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), MapLocation.class);
//                startActivity(i);
                if(nullFieldAddressValidation()){
                    Log.d("Location", "Valid Field");

                    Log.d("finalAddress", finalAddress);

                    LocationInfo locationInfo = (LocationInfo) new LocationInfo().execute(finalAddress);

                    try {
                        JSONObject res = (JSONObject) locationInfo.get();
                        locationInfo.get(1000, TimeUnit.MILLISECONDS);
                        if (false == checkResults(res)) {
                            alertDialog("Wrong", "Address information is invalid!");
                            return;
                        }
//                        Valid Address
                        latlng = getLatLong(res);

                        saveData();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
//                    JSONObject locationInfo = getLocationInfo(finalAddress);
//                    if(getLatLong(locationInfo)){
//                        Log.d("Location", "Valid Location");
//                    }
                }
            }
        });

        //Click on reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyPrice.setText("");
                downPayment.setText("");
                apr.setText("");
                monthlyPayment.setText("0");
            }
        });

    }

    //Null Field Validation
    public boolean nullFieldValidation(){
        if(propertyPrice.getText().toString().length() == 0){
            alertDialog("Missing", "Please provide property price");
            return false;
        }

        if(downPayment.getText().toString().length() == 0){
            alertDialog("Missing", "Please provide down payment");
            return false;
        }

        if(apr.getText().toString().length() == 0){
            alertDialog("Missing", "Please provide annual percentage rate");
            return false;
        }
        return true;
    }

    //Validation
    public boolean fieldValidation(){
        if(aprValue.compareTo(new BigDecimal("100")) == 1){
            alertDialog("Alert", "ARP should not more than 100");
            return false;
        }

        if(downPaymentValue.compareTo(propertyPriceValue) == 1){
            alertDialog("Alert", "Down payment should not be larger than property price");
            return false;
        }
        return true;
    }


    //Calculate
    private BigDecimal[] calculate(){

        BigDecimal loanValue = propertyPriceValue.subtract(downPaymentValue);
        BigDecimal aprMonthlyValue = aprValue.divide(new BigDecimal("1200"));
        int months = years * 12;

        BigDecimal pow = new BigDecimal(Math.pow(1+(aprMonthlyValue.doubleValue()), months));
        BigDecimal partNumerator = aprMonthlyValue.multiply(pow);
        BigDecimal denominator = pow.subtract(new BigDecimal("1"));
        BigDecimal numerator = partNumerator.multiply(loanValue);
        BigDecimal monthlyPayment = numerator.divide(denominator, 4, BigDecimal.ROUND_HALF_UP);

        BigDecimal[] returnValue = new BigDecimal[2];
        returnValue[0] = loanValue;
        returnValue[1] = monthlyPayment.setScale(0, RoundingMode.HALF_UP);
        return returnValue;
    }

    private void setValues(){
        propertyPriceValue = new BigDecimal(String.valueOf(propertyPrice.getText()));
        downPaymentValue = new BigDecimal(String.valueOf(downPayment.getText()));
        aprValue = new BigDecimal(String.valueOf(apr.getText()));
    }



    private void alertDialog(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);

        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    //Null Field Validation for Address
    public boolean nullFieldAddressValidation(){

        //Set String of Address
        addressString = String.valueOf(address.getText());
        cityString = String.valueOf(city.getText());
        zipString = String.valueOf(zip.getText());
        stateSpinnerString = statespinner.getSelectedItem().toString();
        propertyTypeString = propertyspinner.getSelectedItem().toString();

        if(addressString.length() == 0){
            alertDialog("Missing", "Please provide Address");
            return false;
        }

        if(cityString.length() == 0){
            alertDialog("Missing", "Please provide City details");
            return false;
        }

        if(stateSpinnerString.length() == 0){
            alertDialog("Missing", "Please provide State details");
            return false;
        }

        if(zipString.length() == 0){
            alertDialog("Missing", "Please provide Zipcode");
            return false;
        }

        addressString = addressString.replace(" ", "%20");
        cityString = cityString.replace(" ", "%20");

        finalAddress = addressString + "+" + cityString + "+" + stateSpinnerString + "+" + zipString;

        return true;
    }


    public JSONObject getLocationInfo(String finalAddress) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            finalAddress = finalAddress.replaceAll(" ","%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + finalAddress + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public double[] getLatLong(JSONObject jsonObject) {

        double longitute;
        double latitude;
        try {

            longitute = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            double[] returnlatlng = new double[2];
            returnlatlng[0] = latitude;
            returnlatlng[1] = longitute;

            return returnlatlng;
        } catch (JSONException e) {

        }

        return null;
    }


    private boolean checkResults(JSONObject res) throws JSONException{
        JSONArray addressComponents = null;
        int i = 0;
        System.out.println(res.getString("status"));

        if (!res.getString("status").equals("OK")) {return false;}

        addressComponents = res.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
        for (i = 0; i < addressComponents.length(); i++) {
            System.out.println(addressComponents.getJSONObject(i).get("types"));
            String types = (String) addressComponents.getJSONObject(i).getJSONArray("types").get(0);

            if (types.equals("postal_code")) {
                System.out.println(addressComponents.getJSONObject(i).getString("long_name"));
                if (addressComponents.getJSONObject(i).getString("long_name").equals(zipString)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public void saveData(){
        
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calculation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
