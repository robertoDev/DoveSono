package it.blackcat.dovesono;

import android.content.res.Resources;
import android.graphics.*;
import android.location.Location;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import it.blackcat.R;

/**
 * Created with IntelliJ IDEA.
 * User: homeuser
 * Date: 25/04/12
 * Time: 16.56
 * To change this template use File | Settings | File Templates.
 */
public class MapCenterOverlay extends Overlay {

    Resources resources=null;
    Location locationGps=null;
    Location locationNetwork=null;
    Paint paintGps;
    Paint paintNetwork;
    Bitmap pointerBitmapGps;
    Bitmap pointerBitmapNetwork;

    public MapCenterOverlay(Resources res) {
        super();
        resources=res;
        paintGps = new Paint();
        paintGps.setStyle(Paint.Style.FILL);
        paintGps.setAntiAlias(true);
        paintGps.setColor(Color.GREEN);
        paintGps.setAlpha(50);

        paintNetwork = new Paint();
        paintNetwork.setStyle(Paint.Style.FILL);
        paintNetwork.setAntiAlias(true);
        paintNetwork.setColor(Color.BLUE);
        paintNetwork.setAlpha(50);

        pointerBitmapNetwork = BitmapFactory.decodeResource( resources, R.drawable.map_pointer_n);
        pointerBitmapGps = BitmapFactory.decodeResource( resources, R.drawable.map_pointer_g);

    }

    public void setLocation (Location location){
        if(location.getProvider().equals("gps"))
            locationGps=location;
        else
            locationNetwork=location;
    }

    public void draw(android.graphics.Canvas canvas,MapView mapView, boolean shadow){

        Projection projection = mapView.getProjection();

        if(!shadow){
            if(locationGps!=null){
                int latE6=(int) Math.floor(locationGps.getLatitude() * 1.0E6);
                int lonE6=(int) Math.floor(locationGps.getLongitude() * 1.0E6);
                int radius=(int)projection.metersToEquatorPixels(locationGps.getAccuracy());
                GeoPoint gp=new GeoPoint(latE6,lonE6);
                Point p=new Point();
                projection.toPixels(gp, p);
                canvas.drawCircle(p.x,p.y, radius, paintGps);
                canvas.drawBitmap(pointerBitmapGps,p.x,p.y,null);
            }
            if(locationNetwork!=null){
                int latE6=(int) Math.floor(locationNetwork.getLatitude() * 1.0E6);
                int lonE6=(int) Math.floor(locationNetwork.getLongitude() * 1.0E6);
                int radius=(int)projection.metersToEquatorPixels(locationNetwork.getAccuracy());
                GeoPoint gp=new GeoPoint(latE6,lonE6);
                Point p=new Point();
                projection.toPixels(gp, p);
                canvas.drawCircle(p.x, p.y, radius, paintNetwork);
                // il puntatore per il network non ha la "punta" in (0,0) ma in (width,0)
                canvas.drawBitmap(pointerBitmapNetwork, p.x - pointerBitmapNetwork.getWidth() , p.y, null);
            }
        }
    }

}
