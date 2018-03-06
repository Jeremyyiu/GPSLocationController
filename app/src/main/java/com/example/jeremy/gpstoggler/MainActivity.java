package com.example.jeremy.gpstoggler;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.gc.materialdesign.views.Switch;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private LocationManager locationManager;
    private GPSObserver gpsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = getApplicationContext();
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Switch gpsSwitch = (Switch) findViewById(R.id.gpsSwitch);
        initGPSSwitch(gpsSwitch);
        initGPSStateObserver();
    }

    private void initGPSStateObserver() {
        final Uri GPS_URL = Settings.System.getUriFor(Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        gpsObserver = new GPSObserver(new Handler());
        getApplicationContext().getContentResolver()
                .registerContentObserver(GPS_URL, true, gpsObserver);
    }

    private void initGPSSwitch(Switch gpsSwitch) {
        boolean GPSstatus = GPSstatus();
        gpsSwitch.setChecked(GPSstatus);

        gpsSwitch.setOncheckListener(new Switch.OnCheckListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheck(Switch view, boolean check) {
                askGPSPermission();
            }
        });
    }

    public boolean GPSstatus() {
        boolean GPSstatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //https://stackoverflow.com/questions/16748300/locationmanager-isproviderenabledlocationmanager-network-provider-is-not-relia
        return GPSstatus;
    }

    /**
     * Because toggling the GPS/Location service cannot be done on the app, the user has to go into settings to change.
     */
    public void askGPSPermission() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
        //https://stackoverflow.com/questions/4721449/how-can-i-enable-or-disable-the-gps-programmatically-on-android
    }

    /**
     * Checks the status of the GPS - toggles the switch
     */
    private void GPStoggle() {
        Switch gpsSwitch = findViewById(R.id.gpsSwitch);
        boolean GPSstatus = GPSstatus();
        gpsSwitch.setChecked(GPSstatus);
    }

    /**
     * GPS Observer: Handle the change in GPS location connection in real time and change whether the switch is checked or not
     */
    private class GPSObserver extends ContentObserver {
        public GPSObserver(Handler h) {
            super(h);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            GPStoggle();
        }
    }


}
