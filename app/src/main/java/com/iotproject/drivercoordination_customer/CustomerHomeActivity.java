package com.iotproject.drivercoordination_customer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.iotproject.drivercoordination_customer.databinding.ActivityCustomerHomeBinding;
import com.iotproject.drivercoordination_customer.utils.HttpPostRequest;
import com.iotproject.drivercoordination_customer.utils.SetMap;
import com.iotproject.drivercoordination_customer.utils.UserInfor;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomerHomeActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityCustomerHomeBinding binding;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private LocationManager locationManager;
    private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private Context ctx;
    private IMapController controller;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView text_welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCustomerHome.toolbar);
        //邮箱悬浮按钮
//        binding.appBarDriverHome.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null)
//                        .setAnchorView(R.id.fab).show();
//            }
//        });
        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_customer_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences((ctx)));
        init();
        //////////////////////////set map//////////////////////////////////////////////////////////////////
        mapInit();
        //获取当前定位在地图上！get current and set on map
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),mMapView);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.setDrawAccuracyEnabled(true);
        this.mLocationOverlay.enableFollowLocation();
        mMapView.getOverlays().add(this.mLocationOverlay);

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        requestPermissionsIfNecessary(permissions);
        ////////////////////////////////////////////////////////////////////////////////////////////
        //测试用！直接跳转！！
        Button mConfirm = (Button)findViewById(R.id.confirm);
        //set back button
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerHomeActivity.this, CustomerConfirmActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void init(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.nav_sign_out){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomeActivity.this);
                    builder.setTitle("Sign Out")
                            .setMessage("Are you sure to sign out?")
                            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("CONFIRM", (dialog, which) -> {
                                Intent intent = new Intent(CustomerHomeActivity.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialog1 ->{
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(android.R.color.black));
                    });
                    dialog.show();
                }
                if(menuItem.getItemId()==R.id.nav_delete_account){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomeActivity.this);
                    builder.setTitle("Delete Account")
                            .setMessage("Are you sure to delete account?")
                            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("CONFIRM", (dialog, which) -> {
                                //需要连接后端删除账号！！！！！
                                String email = UserInfor.getUserEmail();
                                ProgressDialog progressDialog = new ProgressDialog(CustomerHomeActivity.this);
                                progressDialog.setMessage("Deleting...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();


                                String url = "http://172.20.10.3:8080/User/Delete";
                                RequestBody requestBody = new FormBody.Builder().
                                        add("email", email)
                                        .build();
                                HttpPostRequest.okhttpPost(url, requestBody, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        progressDialog.dismiss();
                                        runOnUiThread(() -> Toast.makeText(CustomerHomeActivity.this, "post fail: "+e.getMessage(), Toast.LENGTH_SHORT).show());
                                        //失败不跳转页面
                                    }
                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        progressDialog.dismiss();
                                        UserInfor.clear();
                                        Intent intent = new Intent(CustomerHomeActivity.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            })
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialog1 ->{
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(android.R.color.black));
                    });
                    dialog.show();
                }
                if(menuItem.getItemId()==R.id.nav_change_password){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerHomeActivity.this);

                    LayoutInflater inflater = LayoutInflater.from(CustomerHomeActivity.this);
                    View registration_layout = inflater.inflate(R.layout.layout_change_password,null);
                    MaterialEditText mPassword = registration_layout.findViewById(R.id.password);
                    MaterialEditText mPasswordConfirm = registration_layout.findViewById(R.id.passwordConfirm);
                    builder.setTitle("Change your Password");
                    builder.setView(registration_layout);

                    builder.setTitle("Change your Password")
                            .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                            .setPositiveButton("CONFIRM", (dialog, which) -> {
                                final String password = mPassword.getText().toString();
                                final String passwordConfirm = mPasswordConfirm.getText().toString();
                                //check password
                                if(password.length()<6){
                                    runOnUiThread(() -> Toast.makeText(CustomerHomeActivity.this, "please enter a password longer than 6", Toast.LENGTH_SHORT).show());
                                    //Snackbar.make(lDriverHome,"please enter a password longer than 6",Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if(!password.equals(passwordConfirm)){
                                    runOnUiThread(() -> Toast.makeText(CustomerHomeActivity.this, "Please check your password. The two entries do not match.", Toast.LENGTH_SHORT).show());
                                    //Snackbar.make(lDriverHome,"Please check your password. The two entries do not match.",Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                String email = UserInfor.getUserEmail();
                                ProgressDialog progressDialog = new ProgressDialog(CustomerHomeActivity.this);
                                progressDialog.setMessage("Changing...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                String url = "http://172.20.10.3:8080/User/ResetPassword";
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("email", email)
                                        .add("password", password)
                                        .build();
                                HttpPostRequest.okhttpPost(url, requestBody, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        progressDialog.dismiss();
                                        runOnUiThread(() -> Toast.makeText(CustomerHomeActivity.this, "post fail: "+e.getMessage(), Toast.LENGTH_SHORT).show());
                                        //失败不跳转页面
                                    }
                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(CustomerHomeActivity.this,MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            })
                            .setCancelable(false);
                    AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(dialog1 ->{
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(android.R.color.black));
                    });
                    dialog.show();
                }
                return true;
            }
        });

        //set data for user information
        View headerView = navigationView.getHeaderView(0);
        TextView mUserName = (TextView)headerView.findViewById(R.id.userName);
        TextView mUserEmail = (TextView)headerView.findViewById(R.id.userEmail);
        TextView mUserPhone = (TextView)headerView.findViewById(R.id.userPhone);
        mUserName.setText(UserInfor.getUserName());
        mUserEmail.setText(UserInfor.getUserEmail());
        mUserPhone.setText(UserInfor.getUserPhone());
    }

    private void mapInit(){
        mMapView = (MapView) findViewById(R.id.map);
        SetMap setMap = new SetMap(mMapView,ctx);
        controller = setMap.getController();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_customer_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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