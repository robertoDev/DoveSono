package it.blackcat.dovesono;

import android.location.Location;
import android.os.Bundle;
import com.google.android.maps.*;
import it.blackcat.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: homeuser
 * Date: 08/04/12
 * Time: 10.45
 * To change this template use File | Settings | File Templates.
 */
public class MapActivity extends com.google.android.maps.MapActivity {
    static String mainProvider=null;
    static MapActivity myInstance=null;
    static MapView mapView=null;
    static MapController mapController=null;
    static boolean enabled=false;


    static Location lastGpsLocation=null;
    static Location lastNetworkLocation=null;

    public static void drawLocationS(Location location){
        if(location.getProvider().equals("gps")){
            lastGpsLocation=location;
        }else{
            lastNetworkLocation=location;
        }


        if(enabled && myInstance!=null)  myInstance.drawLocation(location);
    }
    public static void initS(String mProvider){
        mainProvider=mProvider;
        enabled=true;
    }





    MapCenterOverlay mapCenterOverlay=null;

    protected boolean isRouteDisplayed(){
        return true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(false);

        mapController =  ((MapView)findViewById(R.id.mapView)).getController();

        mapCenterOverlay=new MapCenterOverlay();
        mapCenterOverlay.setResource(getResources());
        mapView.getOverlays().add(mapCenterOverlay);

        if(lastGpsLocation!=null) drawLocation(lastGpsLocation);
        if(lastNetworkLocation!=null) drawLocation(lastNetworkLocation);
        myInstance=this;
    }

        GeoPoint gp=null;   //new GeoPoint(1000,1000);
        //mapController.setCenter(gp);

    public void drawLocation(Location location){

        if (location!=null){
            int latE6=(int) Math.floor(location.getLatitude() * 1.0E6);
            int lonE6=(int) Math.floor(location.getLongitude() * 1.0E6);
            GeoPoint gp=new GeoPoint(latE6,lonE6);
            mapCenterOverlay.setLocation(location);
            if(mainProvider.equals(location.getProvider())){
                mapController.animateTo(gp);
            }
        }

    }





}