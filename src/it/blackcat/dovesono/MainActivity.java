package it.blackcat.dovesono;

import android.app.Activity;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import it.blackcat.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: homeuser
 * Date: 10/04/12
 * Time: 20.10
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity implements LocationListener, OnClickListener {
    static final String EVENTS_COUNT_N = "evN";
    static final String EVENTS_COUNT_G = "evG";
    static final int OLD_EVENT_TIME = (1000* 60 * 120);

    static MainActivity myInstance;
    {
        myInstance=this;
    }

    Location lastGpsLocation=null;
    Location lastNetworkLocation=null;

    int eventsN=0;
    int eventsG=0;
    //----------------------------------------------------------------------------- LocationListener methods:
    public void onLocationChanged(Location currentLocation) {
         traceLocation(currentLocation);
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {
        checkStatus();
    }

    public void onProviderDisabled(String s) {
        checkStatus();
    }
//---------------------------------------------------------------------------- OnClickListener  methods:
    public void onClick(View v) {
        int vId=v.getId();
        switch (vId){
            case R.id.buttonShowG:
                showMap(LocationManager.GPS_PROVIDER);
                break;
            case R.id.buttonShowN:
                showMap(LocationManager.NETWORK_PROVIDER);
                break;
        }
    }

    //---------------------------------------------------------------------------- Activity  methods:
    public void onCreate(Bundle savedInstanceState) {
        Log.d("*lifecycle*","onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        findViewById(R.id.buttonShowG).setOnClickListener(this);
        findViewById(R.id.buttonShowN).setOnClickListener(this);

// Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            eventsG = savedInstanceState.getInt(EVENTS_COUNT_G);
            eventsN = savedInstanceState.getInt(EVENTS_COUNT_N);
            Log.d("*lifecycle*","(restore instance state)");
        } else {
            // Probably initialize members with default values for a new instance
            eventsG = 0;
            eventsN = 0;
            // look if i got a last know location:
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            traceLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            traceLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

        }

        startEvents();
        checkStatus();
    }

    @Override
    protected void onStart() {
        Log.d("*lifecycle*","onStart");
        super.onStart();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onStop() {
        Log.d("*lifecycle*","onStop");
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onRestart() {
        Log.d("*lifecycle*","onRestart");
        super.onRestart();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onResume() {
        Log.d("*lifecycle*","onResume");
        super.onResume();
//        checkStatus();
    }

    @Override
    protected void onPause() {
        Log.d("*lifecycle*","onPause");
        super.onPause();
//        locationManager.removeUpdates(this);
    }

    @Override
    protected void onDestroy() {
        Log.d("*lifecycle*","onDestroy");
        super.onDestroy();
//        locationManager.removeUpdates(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("*lifecycle*","onSaveInstanceState");

        // Save the user's current game state
        savedInstanceState.putInt(EVENTS_COUNT_G, eventsG);
        savedInstanceState.putInt(EVENTS_COUNT_N, eventsN);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    //----------------------------------------------------------------------------



    private void checkStatus(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String txtStatus;
        if(locationManager.getProvider(LocationManager.GPS_PROVIDER)==null){
            txtStatus="not available";
        }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            txtStatus="active";
        }else{
            txtStatus="not enabled";
        }
        displayField(R.id.txtStatusG,txtStatus);

        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER)==null){
            txtStatus="not available";
        }else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            txtStatus="active";
        }else{
            txtStatus="not enabled";
        }
        displayField(R.id.txtStatusN,txtStatus);
    }

    void startEvents(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.getProvider(LocationManager.GPS_PROVIDER)!=null){
            locationManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER, 10000, 10,  this);
        }

        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER)!=null){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 40000, 0, this);
        }
    }


    private void displayField(int field, String value){
        ((TextView)findViewById(field)).setText(value);
    }

    /**
     * è arrivato un evento da tracciare
     * @param location nuova location da tracciare
     */
    void traceLocation(Location location){

        if (location!=null){
            // costruisco una stringa descrittiva per l'accuratezza
            String accuracy="n/d";
            if(location.hasAccuracy()) accuracy=String.valueOf(location.getAccuracy())+ " mt.";

            // costruisco una stringa descrittiva dell'ora
            Date dt=new Date(location.getTime());
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM HH:mm:ss");
            String sdt=sdf.format(dt);

            if(new Date().getTime()- location.getTime()> OLD_EVENT_TIME ){
                sdt+=" (OLD!)";
            }
            // distinguo gps/network
            if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
                lastGpsLocation=location;
                eventsG++;              // conteggio gli eventi processati per GPS

                // visualizzo le info sulla location:
                displayField(R.id.txtXG,String.valueOf(location.getLatitude()));
                displayField(R.id.txtYG,String.valueOf(location.getLongitude()));
                displayField(R.id.txtPrecisionG,String.valueOf(accuracy));
                displayField(R.id.txtTimeG,String.valueOf(sdt));
                displayField(R.id.txtLocationG,getAddress(location));
                displayField(R.id.txtEventsG,String.valueOf(eventsG));
                displayField(R.id.txtAltitudeG,String.valueOf(location.getAltitude()));

            }else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
                lastNetworkLocation=location;
                eventsN++;             // conteggio gli eventi processati per NETWORK
                // visualizzo le info sulla location
                displayField(R.id.txtXN,String.valueOf(location.getLatitude()));
                displayField(R.id.txtYN,String.valueOf(location.getLongitude()));
                displayField(R.id.txtPrecisionN,String.valueOf(accuracy));
                displayField(R.id.txtTimeN,String.valueOf(sdt));
                displayField(R.id.txtLocationN,getAddress(location));
                displayField(R.id.txtEventsN,String.valueOf(eventsN));
                displayField(R.id.txtAltitudeN,String.valueOf(location.getAltitude()));
            }

            // vediamo se ho la mappa attiva:
            if (MapActivity.myInstance!=null){
                MapActivity.myInstance.drawLocation(location);
            }
        }
    }

    private String getAddress(Location currentLocation){
        String txt="";
        if (currentLocation==null) {
            txt="waiting location...";
        }else{
            double latitude= currentLocation.getLatitude();
            double longitude= currentLocation.getLongitude();

            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses;
            try{
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(addresses.size()>0){
                    Address returnedAddress = addresses.get(0);
                    for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                        txt=returnedAddress.getAddressLine(i)+" ";
                    }
                }else{
                    txt+="no location";
                }
            }catch (Exception e){
                txt+="unavailable location";
            }
        }
        return(txt);
    }


    private void showMap(String provider){
        // visualizzo la mappa passandogli la mainLocation
        Intent ni=new Intent(this, MapActivity.class);
        ni.putExtra("provider",provider);
        startActivity(ni);

    }



}