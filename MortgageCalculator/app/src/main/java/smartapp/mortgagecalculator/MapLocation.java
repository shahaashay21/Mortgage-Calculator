package smartapp.mortgagecalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MapLocation extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private static final String sharedPref = "MY_SHARED_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        showAllAddress();


        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        alertDialog((LocationInfoObject) marker.getTag());
    }



    private void alertDialog(final LocationInfoObject markLocation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(markLocation.getAddress());
        builder.setTitle(markLocation.getCity()+", "+markLocation.getState()+", "+ markLocation.getZip());

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Delete

                SharedPreferences sharedPreferences = getSharedPreferences(sharedPref,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(markLocation.getId());
                editor.commit();
                mMap.clear();

                showAllAddress();
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    public void showAllAddress(){

        SharedPreferences sharedPreferences = getSharedPreferences(sharedPref,MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        ArrayList<LocationInfoObject> allAddress = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Gson gson = new Gson();
            LocationInfoObject noteTitleObject = gson.fromJson(entry.getValue().toString(), LocationInfoObject.class);
            allAddress.add(noteTitleObject);
        }

        for(LocationInfoObject location : allAddress){
            Log.d("Lati",location.getLatitude());
            Log.d("Long",location.getLongitude());
            LatLng yourLocation = new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()));
            Marker amarker = mMap.addMarker(new MarkerOptions()
                    .position(yourLocation)
                    .title(location.getAddress())
                    .snippet(location.getCity()+", "+location.getState()+", "+location.getZip()));

            amarker.setTag(location);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(yourLocation)
                    .zoom(12)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}
