package summit.baseproject.view.test.location;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import summit.baseproject.R;
import summit.baseproject.databinding.ActivityLocationBinding;
import summit.baseproject.view.BaseActivity;

public class LocationActivity extends BaseActivity {

    ActivityLocationBinding mView;

    private GpsInfo mGps;

    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = DataBindingUtil.setContentView(this, R.layout.activity_location);

        callPermission();

        init();
    }

    private void callPermission(){
        setPermission(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                ShowToast( "위치기능 권한이 허용되어 있습니다.");
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                finish();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION );
    }

    @SuppressLint("MissingPermission")
    private void init(){

        mGps = new GpsInfo( this );

        //LocationManager
        mView.btnGetLocation.setOnClickListener( v -> {
//
                    if( mGps.isGetLocation ){
                        mGps.setEditViewEvent( mView.tvLocationResult );
                        double latitude = mGps.getLatitude();
                        double longitude = mGps.getLongitude();
                        String rst = latitude + ", " + longitude + "\n";
                        mView.tvLocationResult.setText( mView.tvLocationResult.getText() + rst );
                        toast( rst );
                    } else {
                        // GPS 를 사용할수 없으므로
                        mGps.showSettingsAlert();
                    }
                }
        );
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient( this );

        //구글플레이서비스 위치

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mView.btnGetLocation2.setOnClickListener( v-> {

            client.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    double lat = task.getResult().getLatitude();
                    double lon = task.getResult().getLongitude();
                    String rst = lat + ", " + lon + "\n";
                    toast( rst );
                    mView.tvLocationResult2.setText( rst );


                }
            });

            client.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        });





    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                String rst = location.getLatitude() + ", " + location.getLongitude()+ "\n";
                mView.tvLocationResult2.setText( mView.tvLocationResult2.getText() + rst );
                Log.i("MapsActivity", rst );
                toast( rst );
            }
        }
    };

}

