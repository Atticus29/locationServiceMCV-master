package fisherdynamic.locationservicemcv;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private Double currentLatitude;
    private Double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final TextView latLongView = findViewById(R.id.activity2LocationView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }
//        startLocationService();
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "<<<<on receive reached");
                // Get extra data included in the Intent
                currentLatitude = Double.parseDouble(intent.getStringExtra("ServiceLatitudeUpdate"));
                currentLongitude = Double.parseDouble(intent.getStringExtra("ServiceLongitudeUpdate"));
                latLongView.setText(Double.toString(currentLatitude) + ", " + Double.toString(currentLongitude));
            }
        };
        IntentFilter intentFilter = new IntentFilter("locationServiceUpdates");
        LocalBroadcastManager.getInstance(Main2Activity.this).registerReceiver(mMessageReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopLocationService();
    }

//    public void startLocationService(){
//        Intent intent = new Intent(this, LocationService.class);
//        startService(intent);
//        Log.d(TAG, "<<<<location service started");
//    }

    public void stopLocationService(){
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }

    private static final String TAG = "Main2Activity";
}
