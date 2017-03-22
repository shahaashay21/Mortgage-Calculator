package smartapp.mortgagecalculator;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

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


        //Click on calculation button
        calculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedTermId = term.getCheckedRadioButtonId();
                Button termValue = (Button) findViewById(selectedTermId);
                if(termValue.getText().equals("15 yrs.")){
                    years = 15;
                }else if(termValue.getText().equals("30 yrs.")){
                    years = 30;
                }
                //Null field validation
                if(nullFieldValidation()) {
                    //Get all the values in respective data types
                    setValues();

                    //Validate each fields
                    if (fieldValidation()) {
                        //Calculate and show
                        BigDecimal[] loanMonthlyPayment = calculate();
                        loanAmount.setText("$"+String.valueOf(loanMonthlyPayment[0]));
                        monthlyPayment.setText("$"+String.valueOf(loanMonthlyPayment[1]));


                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        });

        //Click on save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog("Missing", "HEYY");
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
