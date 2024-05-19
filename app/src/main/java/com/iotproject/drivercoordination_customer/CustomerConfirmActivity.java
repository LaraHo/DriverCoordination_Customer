package com.iotproject.drivercoordination_customer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.iotproject.drivercoordination_customer.utils.Readroute;
import com.iotproject.drivercoordination_customer.utils.SetMap;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class CustomerConfirmActivity extends AppCompatActivity {
    private Button mConfirm, mCancel;
    private FloatingActionButton facMylocation;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private LocationManager locationManager;
    private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private MinimapOverlay mMinimapOverlay;
    private GeoPoint myLocation;
    private Context ctx;
    private IMapController controller;

    private SetMap setMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_confirm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences((ctx)));
        mapInit();
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),mMapView);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.setDrawAccuracyEnabled(true);
        this.mLocationOverlay.enableFollowLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);
        init();
        routeInit();
        //获取当前定位在地图上！get current and set on map

        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        requestPermissionsIfNecessary(permissions);
    }

    private void init(){
        facMylocation = (FloatingActionButton)findViewById(R.id.fab);
        facMylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.setZoom(15); //初始的放大尺度
                mLocationOverlay.enableFollowLocation();
                mMapView.setMapOrientation(0, true);
                Snackbar.make(v, "Your Location!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        mCancel = (Button)findViewById(R.id.cancle);
        //set back button
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerConfirmActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        mConfirm = (Button)findViewById(R.id.confirm);
        //set back button
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);//cannot close dialog if click outside the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View findDriver_layout = inflater.inflate(R.layout.layout_find_driver,null);
        dialog.setView(findDriver_layout);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //需要连接后端更改司机状态
                //跳转到接乘客页面
                Intent intent = new Intent(CustomerConfirmActivity.this, CustomerWaitDriverActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();

    }
    private void mapInit() {
        mMapView = (MapView) findViewById(R.id.map);
        setMap = new SetMap(mMapView,ctx);
        controller = setMap.getController();
    }

    private void routeInit() {
//        Polyline polyline = new Polyline(mMapView);
//        polyline.setColor(Color.BLUE);
        //需从后端请求路线
        double[][] routeExample={{53.3969988, -6.3309447},
                {53.3968485, -6.3313508},
                {53.3946909, -6.3372548},
                {53.3936317, -6.3400928},
                {53.3913896, -6.3461865},
                {53.3904459, -6.34874},
                {53.3898671, -6.3502788},
                {53.3891082, -6.3523403},
                {53.3885465, -6.3538162},
                {53.3883384, -6.3543789},
                {53.3880293, -6.3550978},
                {53.3876734, -6.3558327},
                {53.3871797, -6.3568006},
                {53.386854, -6.3573492},
                {53.3864561, -6.3579868},
                {53.3861971, -6.3583692},
                {53.385623, -6.3591701},
                {53.3851597,-6.3597522},
                {53.3848042, -6.3601605},
                {53.3842192, -6.3608526},
                {53.3826274,-6.3627172},
                {53.3824219, -6.3629549},
                {53.381571,-6.3638899},
                {53.3801752, -6.3654818},
                {53.3791338, -6.3666793},
                {53.3781307, -6.3678427},
                {53.3763419, -6.3699187},
                {53.375273, -6.3711311},
                {53.3748084, -6.3716008},
                {53.3743024, -6.3721123},
                {53.3736432, -6.3727775},
                {53.3727963,-6.3735742},
                {53.3723088,-6.374021},
                {53.3718411,-6.3745113},
                {53.3714618, -6.3749964},
                {53.3710809,-6.3755275},
                {53.3708804, -6.375871},
                {53.370601,-6.3763497},
                {53.3702561, -6.376935},
                {53.3699858, -6.3774128},
                {53.3697362,-6.3778044},
                {53.3693936, -6.3782967},
                {53.3691369,-6.3786328},
                {53.3688365, -6.3790257},
                {53.3684783, -6.3794459},
                {53.3682774, -6.3796422},
                {53.3680833, -6.3798318},
                {53.3676843, -6.3802187},
                {53.3672738, -6.3805284},
                {53.3667503, -6.3809177},
                {53.3663682, -6.3811511},
                {53.3658426,-6.3813794},
                {53.3647352, -6.3817476}};
        setMap.drawRoute(routeExample);
//        Readroute readroute = new Readroute();
//        List<GeoPoint> route = readroute.storeRoute(routeExample);
//        GeoPoint start = route.get(0);
//        GeoPoint end = route.get(route.size()-1);
//        polyline.setPoints(route);
//        mMapView.getOverlays().add(polyline);

        //标点！
//        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
//        items.add(new OverlayItem("Start", "here!",start)); // Lat/Lon decimal degrees
//        items.add(new OverlayItem("End", "here!",end)); // Lat/Lon decimal degrees
//        //the overlay
//        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
//                items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
//            @Override
//            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
//                controller.animateTo(item.getPoint());
//                controller.setCenter(item.getPoint());
//                controller.setZoom(15); //放大尺度
//                //do something
//                return true;
//            }
//            @Override
//            public boolean onItemLongPress(final int index, final OverlayItem item) {
//                return false;
//            }
//        }, ctx);
//        mOverlay.setFocusItemsOnTap(true);
//        mMapView.getOverlays().add(mOverlay);
    }
    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mMapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        mMapView.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}