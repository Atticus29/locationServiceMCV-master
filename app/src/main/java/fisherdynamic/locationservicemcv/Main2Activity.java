package fisherdynamic.locationservicemcv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    private Double currentLatitude;
    private Double currentLongitude;
    private BroadcastReceiver mMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final TextView latLongView = findViewById(R.id.activity2LocationView);

        mMessageReceiver = new BroadcastReceiver() {
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
        LocalBroadcastManager.getInstance(Main2Activity.this).unregisterReceiver(mMessageReceiver);
    }

    private static final String TAG = "Main2Activity";
}
