package org.rti.rcd.researchstack;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.researchstack.skin.PermissionRequestManager;

public class ResearchStackApplication extends Application
{

    public static final String PERMISSION_NOTIFICATIONS = "SampleApp.permission.NOTIFICATIONS";
    public static Double longitude = new Double(0.0);
    public static Double latitude = new Double(0.0);


    @Override
    public void onCreate()
    {
        super.onCreate();

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Init RS Singleton
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        org.researchstack.skin.ResearchStack.init(this, new ResearchStack());

        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
        // Init permission objects
        //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*

        // If Build is M or >, add needed permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            PermissionRequestManager.PermissionRequest location = new PermissionRequestManager.PermissionRequest(Manifest.permission.ACCESS_FINE_LOCATION,
                    R.drawable.rss_ic_location_24dp,
                    R.string.rss_permission_location_title,
                    R.string.rss_permission_location_desc);
            location.setIsBlockingPermission(true);
            location.setIsSystemPermission(true);

            PermissionRequestManager.getInstance().addPermission(location);
        }

        // We have some unique permissions that tie into Settings. You will need
        // to handle the UI for this permission along w/ storing the result.
        PermissionRequestManager.PermissionRequest notifications =
                new PermissionRequestManager.PermissionRequest(
                        PERMISSION_NOTIFICATIONS,
                        R.drawable.rss_ic_notification_24dp,
                        R.string.rss_permission_notification_title,
                        R.string.rss_permission_notification_desc
                );

        PermissionRequestManager.getInstance().addPermission(notifications);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            Log.v(getClass().toString(), "IN ON LOCATION CHANGE, lat=" + latitude + ", lon=" + longitude);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
             /* This is called when the GPS status alters */
        }

        @Override
        public void onProviderEnabled(String provider) {
            /* This is called when the GPS status alters */
        }

        @Override
        public void onProviderDisabled(String provider) {
            /* This is called when the GPS status alters */
        }
    };

    @Override
    protected void attachBaseContext(Context base)
    {
        // This is needed for android versions < 5.0 or you can extend MultiDexApplication
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
