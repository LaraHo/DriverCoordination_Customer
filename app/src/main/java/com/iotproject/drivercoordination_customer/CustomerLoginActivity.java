package com.iotproject.drivercoordination_customer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.iotproject.drivercoordination_customer.utils.HttpPostRequest;
import com.iotproject.drivercoordination_customer.utils.ReadJson;
import com.iotproject.drivercoordination_customer.utils.UserInfor;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomerLoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin, mRegistration,mBack;
    private RelativeLayout lCustomerLoginRoot;
    private String email,password,passwordConfirm ,name,phone;
    private static final String TAG = "连接后端测试：";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.customerLoginRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Record the root Layout
        lCustomerLoginRoot = (RelativeLayout)findViewById(R.id.customerLoginRoot);
        //edit text
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        //buttons
        mLogin = (Button) findViewById(R.id.login);
        mRegistration = (Button) findViewById(R.id.registration);
        mBack = (Button) findViewById(R.id.back);

        //set register button event
        mRegistration.setOnClickListener(v -> {
            //弹窗
            showRegistrationDialog();
        });


        //set login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试用！直接跳转！！
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
                finish();

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                //check empty input
                if(TextUtils.isEmpty(email)){
                    Snackbar.make(lCustomerLoginRoot,"please enter your email address",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Snackbar.make(lCustomerLoginRoot,"please enter password",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(CustomerLoginActivity.this);
                progressDialog.setMessage("Logging...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String url = HttpPostRequest.url+"/User/Login"; //邮箱+密码；String
                RequestBody requestBody = new FormBody.Builder().
                        add("email", email).
                        add("password", password)
                        .build();

                HttpPostRequest.okhttpPost(url, requestBody, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                        progressDialog.dismiss();
                        Snackbar.make(lCustomerLoginRoot,"post fail: "+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                        progressDialog.dismiss();
                        ReadJson jsonHere = new ReadJson(response.body().string());
                        int codeCheck  = jsonHere.Code();
                        if (codeCheck==200) {
                            String name = jsonHere.ReadString("userName");
                            String phone = jsonHere.ReadString("phone");
                            runOnUiThread(() -> {
                                UserInfor.setUserName(name);
                                UserInfor.setUserEmail(email);
                                UserInfor.setUserPhone(phone);
                                Snackbar.make(lCustomerLoginRoot, "success: " + name, Snackbar.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            switch (codeCheck) {
                                case 404:
                                    Snackbar.make(lCustomerLoginRoot, "fail: the account does not exist! ", Snackbar.LENGTH_SHORT).show();
                                    break;
                                case 401:
                                    Snackbar.make(lCustomerLoginRoot, "fail: password error! ", Snackbar.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Snackbar.make(lCustomerLoginRoot, "Error Unknown! ", Snackbar.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }

                });
            }
        });

        //set back button
        mBack.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerLoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        });
    }

    private void showRegistrationDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");
        //dialog.setCancelable(false);//cannot close dialog if click outside the dialog

        LayoutInflater inflater = LayoutInflater.from(this);
        View registration_layout = inflater.inflate(R.layout.layout_customer_registration,null);

        MaterialEditText regEmail = registration_layout.findViewById(R.id.email);
        regEmail.setText(email);
        MaterialEditText regName= registration_layout.findViewById(R.id.name);
        regName.setText(name);
        MaterialEditText regPhone = registration_layout.findViewById(R.id.phone);
        regPhone.setText(phone);
        MaterialEditText regPassword = registration_layout.findViewById(R.id.password);
        regPassword.setText(password);
        MaterialEditText regPasswordConfirm = registration_layout.findViewById(R.id.passwordConfirm);
        regPasswordConfirm.setText(passwordConfirm);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(registration_layout);
        dialog.setView(scrollView);
//        dialog.setView(registration_layout);

        //set register button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
                email = regEmail.getText().toString();
                password = regPassword.getText().toString();
                passwordConfirm = regPasswordConfirm.getText().toString();
                name = regName.getText().toString();
                phone = regPhone.getText().toString();

                //check empty input
                if(TextUtils.isEmpty(email)){
                    showToastAndReopenDialog("please enter your email address");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    showToastAndReopenDialog("please enter password");
                    return;
                }
                if(TextUtils.isEmpty(passwordConfirm)){
                    showToastAndReopenDialog("please enter your password again");
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    showToastAndReopenDialog("please enter your name");
                    return;
                }
                if(TextUtils.isEmpty(phone)){
                    showToastAndReopenDialog("please enter your phone number");
                    return;
                }
                // 将字符串表示的电话号码转换为长整型
                try {
                    final long phoneNumber = Long.parseLong(phone);
                    // 在此处使用 phoneNumber
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    showToastAndReopenDialog("please check your phone number, is not valid");
                    return;
                }
                //check password
                if(password.length()<6){
                    showToastAndReopenDialog("please enter a password longer than 6");
                    return;
                }
                if(!password.equals(passwordConfirm)){
                    showToastAndReopenDialog("Please check your password. The two entries do not match.");
                    return;
                }


                //测试用！直接跳转！！！
//                Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
//                startActivity(intent);
//                finish();

                ProgressDialog progressDialog = new ProgressDialog(CustomerLoginActivity.this);
                progressDialog.setMessage("Registering...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                //registration, connect to backend
                String url = "http://172.20.10.3:8080/User/Register";
                RequestBody requestBody = new FormBody.Builder().
                        add("email", email)
                        .add("name",name)
                        .add("password", password)
                        .add("phone",phone).build();

                HttpPostRequest.okhttpPost(url, requestBody, new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                        progressDialog.dismiss();
                        Snackbar.make(lCustomerLoginRoot,"post fail: "+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                        //失败不跳转页面
                    }

                    @Override
                    public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                        progressDialog.dismiss();
                        ReadJson jsonHere = new ReadJson(response.body().string());
                        int codeCheck  = jsonHere.Code();
                        if (codeCheck==200){
                            UserInfor.setUserEmail(email);
                            UserInfor.setUserName(name);
                            UserInfor.setUserPhone(phone);
//                                Log.d(TAG, "为什么"+codeCheck+"信息: "+responseBodyString);
                            Snackbar.make(lCustomerLoginRoot,"success: "+email,Snackbar.LENGTH_SHORT).show();
                            Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else if (codeCheck==409){
                            Snackbar.make(lCustomerLoginRoot,"fail: the account already exist ",Snackbar.LENGTH_SHORT).show();
                        }else{
                            Snackbar.make(lCustomerLoginRoot,"unknown error! ",Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //set cancel button
        dialog.setNegativeButton("CANCEL", (dialog1, which) -> {
        });
        dialog.show();
    }

    private void showToastAndReopenDialog(String message) {
        runOnUiThread(() -> Toast.makeText(CustomerLoginActivity.this, message, Toast.LENGTH_SHORT).show());
        showRegistrationDialog();
    }

}