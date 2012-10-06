package it.blackcat.dovesono;

import android.app.Activity;
import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import it.blackcat.R;

import java.security.PublicKey;
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
public class MainActivity extends Activity implements LocationListener,  View.OnClickListener {
    int eventsN=0;
    int eventsG=0;
    public void onLocationChanged(Location currentLocation) {
         displayLocation(currentLocation);
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {}

    public void onProviderEnabled(String s) {
        checkStatus();
    }

    public void onProviderDisabled(String s) {
        checkStatus();
    }



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.buttonShowG).setOnClickListener(this);
        findViewById(R.id.buttonShowN ).setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatus();
        startEvents();
        /*
        if(myLp!=null){
            myLpDescription=myLp.getName();
            Location lastLocation=locationManager.getLastKnownLocation(myLpDescription);
//            displayLocation(lastLocation);
            locationManager.requestLocationUpdates("gps", 2000, 0, this);
            locationManager.requestLocationUpdates("network", 2000, 0, this);
        }else{
//            displayLocation("No location device");
        }
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
//        locationManager.removeUpdates(this);
    }


    private void checkStatus(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String txtStatus="";
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

    private void startEvents(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.getProvider(LocationManager.GPS_PROVIDER)!=null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, this);
        }

        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER)!=null){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 20, this);
        }
    }


    private void displayField(int field, String value){
        ((TextView)findViewById(field)).setText(value);
    }

    private void displayLocation(Location location){
        // costruisco una stringa descrittiva per l'accuratezza
        String accuracy="n/d";
        if(location.hasAccuracy()) accuracy=String.valueOf(location.getAccuracy())+ " mt.";

        // costruisco una stringa descrittiva dell'ora
        Date dt=new Date(location.getTime());
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        String sdt=sdf.format(dt);

        // distinguo gps/network
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            // visualizzo le info sulla location
            displayField(R.id.txtXG,String.valueOf(location.getLatitude()));
            displayField(R.id.txtYG,String.valueOf(location.getLongitude()));
            displayField(R.id.txtPrecisionG,String.valueOf(accuracy));
            displayField(R.id.txtTimeG,String.valueOf(sdt));
            displayField(R.id.txtLocationG,getAddress(location));
            // conteggio gli eventi processati per GPS
            eventsG++;
            displayField(R.id.txtEventsG,String.valueOf(eventsG));
            MapActivity.drawLocationS(location);


        }else if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
            // visualizzo le info sulla location
            displayField(R.id.txtXN,String.valueOf(location.getLatitude()));
            displayField(R.id.txtYN,String.valueOf(location.getLongitude()));
            displayField(R.id.txtPrecisionN,String.valueOf(accuracy));
            displayField(R.id.txtTimeN,String.valueOf(sdt));
            displayField(R.id.txtLocationN,getAddress(location));
            // conteggio gli eventi processati per NETWORK
            eventsN++;
            displayField(R.id.txtEventsN,String.valueOf(eventsN));
            MapActivity.drawLocationS(location);
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

    // gestore di click per tutti i pulsanti
    public void onClick(View v) {
        int vId=v.getId();
        switch (vId){
            case R.id.buttonShowG:
                showMap("gps");
                break;
            case R.id.buttonShowN:
                showMap("network");
                break;
        }
    }

    private void showMap(String provider){
        Intent ni=new Intent(this, MapActivity.class);
        startActivity(ni);
        MapActivity.initS(provider);
    }



}