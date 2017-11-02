package fisherdynamic.locationservicemcv;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private Double currentLatitude;
    private Double currentLongitude;
    private Button nextActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView latLongView = findViewById(R.id.latLongView);
        nextActivityButton = findViewById(R.id.nextActivityButton);
        nextActivityButton.setOnClickListener(this);

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
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver, intentFilter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                    .setMessage("Really need \"location\" permission to continue.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestLocationPermission();
                        }
                    })
                    .show();
            } else {
                requestLocationPermission();
            }
        } else {
            startLocationService();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == nextActivityButton) {
            Log.d(TAG, "<<<<nextActivityButton clicked");
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                                          new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                          MY_PERMISSIONS_REQUEST_FINE_LOCATION);
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
        Log.d(TAG, "<<<<location service started");
    }

    private void stopLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
        Log.d(TAG, "<<<<location service stopped");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull  int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                } else {
                    finish();
                }
            }
        }
    }

    private static final String TAG = "MainActivity";
}
