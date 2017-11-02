package fisherdynamic.locationservicemcv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private Context serviceContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        serviceContext = this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10 * 1000)
            .setFastestInterval(1 * 1000);
        mGoogleApiClient.connect();
    }

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (location != null) {
            Log.d(TAG, "<<<<location not null");
            handleNewLocation(location);
        } else {
            Log.d(TAG, "<<<<location null");
        }
    }

    private void handleNewLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        if (currentLatitude != null && currentLongitude != null) {
            sendToActivity(currentLatitude, currentLongitude);
        }
    }

    private void sendToActivity(Double currentLatitude, Double currentLongitude) {
        Intent intent = new Intent("locationServiceUpdates");
        intent.putExtra("ServiceLatitudeUpdate", currentLatitude.toString());
        intent.putExtra("ServiceLongitudeUpdate", currentLongitude.toString());
        if (serviceContext != null) {
            LocalBroadcastManager.getInstance(serviceContext).sendBroadcast(intent);
            Log.d(TAG, "<<<<broadcast launched from the location service");
            Toast.makeText(this, "broadcast launched from the location service", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "<<<<didn't broadcast the location updates because serviceContext is null");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (serviceContext != null) {
            Toast.makeText(serviceContext, "<<<<Location services suspended. Please reconnect", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "<<<<connection suspended, but serviceContext was null");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "<<<<connection failed");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        Toast.makeText(this, "Location service destroyed", Toast.LENGTH_SHORT).show();
    }

    private static final String TAG = "LocationService";
}
