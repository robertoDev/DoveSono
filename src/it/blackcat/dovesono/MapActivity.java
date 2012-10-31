package it.blackcat.dovesono;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.maps.*;
import it.blackcat.R;

/**
 * Created with IntelliJ IDEA.
 * User: homeuser
 * Date: 08/04/12
 * Time: 10.45
 * To change this template use File | Settings | File Templates.
 */
public class MapActivity extends com.google.android.maps.MapActivity implements View.OnClickListener {
    static MapView mapView=null;

    static MapController mapController=null;

    static MapActivity myInstance;
    {
        myInstance=this;
    }


    String mainProvider=null;
    MapCenterOverlay mapCenterOverlay=null;

    //---------------------------------------------------------------------------- OnClickListener  methods:
    public void onClick(View v) {
        int vId=v.getId();
        switch (vId){
            case R.id.buttonBack:
                myInstance=null;    // is a kind of destructor for this instance
                finish();
        }
    }
/*
    public static void drawLocationS(Location location, String accurancy, int eventsCount){
        if(enabled && myInstance!=null)  myInstance.drawLocation(location, accurancy, eventsCount);
    }
*/

    //---------------------------------------------------------------------------- MapActivity  methods:

    @Override
    protected boolean isRouteDisplayed(){
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // attivo il listener per i bottoni
        findViewById(R.id.buttonBack).setOnClickListener(this);

        // passato dalla mainActivity indica qual'è il provider da seguire sulla mappa
        mainProvider= this.getIntent().getStringExtra("provider");

        // visualizzo l'icona corretta che indica il provider seguito sulla mappa
        ImageView iw= (ImageView) findViewById(R.id.providerIcon);
        if(mainProvider.equals(LocationManager.NETWORK_PROVIDER))
            iw.setImageResource(R.drawable.network_icon);
        else
            iw.setImageResource(R.drawable.gps_icon);

        // inizializzo la mappa
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);
        mapController =  ((MapView)findViewById(R.id.mapView)).getController();

        mapCenterOverlay=new MapCenterOverlay(getResources());
        mapView.getOverlays().add(mapCenterOverlay);


        // adesso visualizzo sulla mappa gli ultimi eventi che ho salvato per i 2 providers
        if(MainActivity.myInstance.lastGpsLocation!=null) drawLocation(MainActivity.myInstance.lastGpsLocation);
        if(MainActivity.myInstance.lastNetworkLocation!=null) drawLocation(MainActivity.myInstance.lastNetworkLocation);

    }

    //------------------------------------------------------------------------------------------


    public void drawLocation(Location location){

        int latE6=(int) Math.floor(location.getLatitude() * 1.0E6);
        int lonE6=(int) Math.floor(location.getLongitude() * 1.0E6);
        GeoPoint gp=new GeoPoint(latE6,lonE6);
        mapCenterOverlay.setLocation(location);
        if(mainProvider.equals(location.getProvider())){
            mapController.animateTo(gp);

            if(mainProvider.equals(LocationManager.NETWORK_PROVIDER)){
                copyField(MainActivity.myInstance,R.id.txtEventsN, this, R.id.txtEvents);
                copyField(MainActivity.myInstance,R.id.txtPrecisionN, this, R.id.txtPrecision);
            }else{
                copyField(MainActivity.myInstance,R.id.txtEventsG, this, R.id.txtEvents);
                copyField(MainActivity.myInstance,R.id.txtPrecisionG, this, R.id.txtPrecision);
            }
/*
            ((TextView)findViewById(R.id.txtEvents)).setText(
                ((TextView)findViewById(R.id.txtEventsG)).getText()
            );
            ((TextView)findViewById(R.id.txtPrecision)).setText(
                    ((TextView)findViewById(R.id.txtPrecisionG)).getText()
            );
  */
        }else{
            //todo devo vedere come tracciare gli overlay anche se non ci sono movimenti sulla mappa!
//            mapController. scrollBy(0, 0);    // traccia di sicuro gli overlay senza spostare la mappa
        }
    }

    private CharSequence copyField(Activity srcAct,int srcField,Activity destAct, int destField ){
        CharSequence value=((TextView)srcAct.findViewById(srcField)).getText();
        ((TextView)destAct.findViewById(destField)).setText(value);
        return value;
    }


}