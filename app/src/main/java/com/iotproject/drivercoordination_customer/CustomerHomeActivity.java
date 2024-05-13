package com.iotproject.drivercoordination_customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.iotproject.drivercoordination_customer.databinding.ActivityCustomerHomeBinding;
import com.iotproject.drivercoordination_customer.utils.UserInfor;
import com.rengwuxian.materialedittext.MaterialEditText;

public class CustomerHomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityCustomerHomeBinding binding;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCustomerHome.toolbar);

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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CustomerHomeActivity.this, "please enter a password longer than 6", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //Snackbar.make(lDriverHome,"please enter a password longer than 6",Snackbar.LENGTH_SHORT).show();
                                    return;
                                }
                                if(!password.equals(passwordConfirm)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CustomerHomeActivity.this, "Please check your password. The two entries do not match.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //Snackbar.make(lDriverHome,"Please check your password. The two entries do not match.",Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                //连接后端修改密码！！
                                String email = UserInfor.getUserEmail();
                                //后端成功后跳转页面！！
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
                return true;
            }
        });

        //set data for user information
        View headerView = navigationView.getHeaderView(0);
        TextView mUserName = (TextView)headerView.findViewById(R.id.userName);
        TextView mUserEmail = (TextView)headerView.findViewById(R.id.userEmail);
        mUserName.setText(UserInfor.getUserName());
        mUserEmail.setText(UserInfor.getUserEmail());




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
}