package it.blackcat.dovesono;

import android.app.Activity;
import android.app.Dialog;
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
 * User: Roberto Girelli
 * Date: 10/04/12
 * Time: 20.10
 *
 * DoveSono?
 * E' una applicazione dimostrativa sulla geolocalizzazione in Android.
 * Permette di monitorare le posizioni restituite dai due location provider (GPS e Network),
 * di vederne gli attributi e di rappresentarle sulle Google Maps.
 *
 */
public class MainActivity extends Activity implements LocationListener, OnClickListener {

    // Mantengo in maniera statica un riferimento all'istanza usata:
    static MainActivity myInstance;
    {
        myInstance=this;
    }


    Dialog infoDialog;      // Dialog delle info
    static final int OLD_EVENT_TIME = (1000* 60 * 120);     // Se uno degli eventi visualizzati è più vecchio di
                                                            // questa soglia verrà visualizzato accanto un warning "(OLD)":


    Location lastGpsLocation=null;      // ultima location del provider Network
    Location lastNetworkLocation=null;  // ultima location del provider GPS

    int eventsN=0;      // contatore di eventi per provider Network
    int eventsG=0;      // contatore di eventi per provider GPS

    //----------------------------------------------------------------------------- LocationListener methods:

    public void onLocationChanged(Location currentLocation) {
         traceLocation(currentLocation,true);
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    public void onProviderEnabled(String s) {
        traceStatus();
    }

    public void onProviderDisabled(String s) {
        traceStatus();
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
            case R.id.imageLogo:
                infoDialog.show();
                break;

            case R.id.buttonInfoOk:
                infoDialog.cancel();
                break;

        }
    }


    private void showMap(String provider){
        // visualizzo la mappa passandogli la mainLocation
        Intent ni=new Intent(this, MapActivity.class);
        ni.putExtra("provider",provider);
        startActivity(ni);
    }


    //---------------------------------------------------------------------------- Activity  methods:
    public void onCreate(Bundle savedInstanceState) {
        Log.d("*lifecycle*","onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        findViewById(R.id.buttonShowG).setOnClickListener(this);
        findViewById(R.id.buttonShowN).setOnClickListener(this);
        findViewById(R.id.imageLogo).setOnClickListener(this);

        // setup del dialog
        infoDialog = new Dialog(MainActivity.this);
        infoDialog.setContentView(R.layout.info_dialog);
        infoDialog.setTitle("About");
        infoDialog.setCancelable(true);
        infoDialog.findViewById(R.id.buttonInfoOk).setOnClickListener(this);

        // Verifico se sto ricreando un'istanza distrutta precedentemente:
        if (savedInstanceState != null) {
            // Ripristino dei valori precedentemente salvati:
            eventsG = savedInstanceState.getInt("frm_" +R.id.txtEventsG );
            eventsN = savedInstanceState.getInt("frm_" +R.id.txtEventsN );
            Log.d("*lifecycle*","(restore instance state)");
        } else {
            eventsG = 0;
            eventsN = 0;
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        traceLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER), false);
        traceLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER),false);

        startEvents();
        traceStatus();
    }


    @Override
    protected void onStart() {
        Log.d("*lifecycle*","onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("*lifecycle*","onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("*lifecycle*","onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("*lifecycle*","onResume");
        super.onResume();
//        traceStatus();
    }

    @Override
    protected void onPause() {
        Log.d("*lifecycle*","onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("*lifecycle*","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d("*lifecycle*","onSaveInstanceState");

        // Salvo alcuni valori da ripristinare se l'istanza verrà ricreata
        savedInstanceState.putInt("frm_" +R.id.txtEventsG , eventsG);
        savedInstanceState.putInt("frm_" +R.id.txtEventsN , eventsN);

        super.onSaveInstanceState(savedInstanceState);
    }


    //----------------------------------------------------------------------------
    /**
     * Avvia la rilevazione degli eventi
     */
    void startEvents(){

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager.getProvider(LocationManager.GPS_PROVIDER)!=null){
            locationManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER, 10000, 10,  this);
        }

        if(locationManager.getProvider(LocationManager.NETWORK_PROVIDER)!=null){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 50, this);
        }
    }

    /**
     * visualizza lo stato dei providers
     */
    private void traceStatus(){
//  -----------------------------------------------------
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

    /**
     * è arrivato un evento da tracciare
     * @param location nuova location da tracciare
     * @param flagInc  se l'evento è da conteggiare
     */
    void traceLocation(Location location, boolean flagInc){
//  -----------------------------------------------------

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
                if (flagInc) eventsG++;              // conteggio gli eventi processati per GPS

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
                if (flagInc) eventsN++;             // conteggio gli eventi processati per NETWORK
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


    private void displayField(int field, String value){
//  -----------------------------------------------------
        ((TextView)findViewById(field)).setText(value);
    }


    private String getAddress(Location currentLocation){
//  -----------------------------------------------------
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
}