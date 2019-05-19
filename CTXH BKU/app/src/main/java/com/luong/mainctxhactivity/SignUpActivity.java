package com.luong.mainctxhactivity;

import android.app.Person;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private Button btnSignup;
    private EditText eEmail, ePass, eLname, eFname, eCheck,eMSSV, eMajor;
    private FirebaseAuth auth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        eEmail =  findViewById(R.id.edit_email);
        ePass = findViewById(R.id.edit_pass_sn);
        eLname = findViewById(R.id.edit_lname);
        eFname =  findViewById(R.id.edit_fname);
        eCheck = findViewById(R.id.edit_checkpass);
        eMSSV = findViewById(R.id.edit_MSSV);
        eMajor = findViewById(R.id.edit_major);

        btnSignup =  findViewById(R.id.login_sn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = eEmail.getText().toString().trim();
                String pass = ePass.getText().toString().trim();
                String check = eCheck.getText().toString().trim();
                final String major = eMajor.getText().toString().trim();
                final String mssv = eMSSV.getText().toString().trim();
                final String lna = eLname.getText().toString().trim();
                final String fna = eFname.getText().toString().trim();
                //kiem tra du lieu nhap vao rong
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getBaseContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getBaseContext(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(check)) {
                    Toast.makeText(getBaseContext(), "Mật khẩu xác nhận không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lna.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập họ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fna.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập tên!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mssv.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập MSSV!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (major.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập tên khoa!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //kiem tra mssv phai la so
                try {
                    Integer.parseInt(mssv);
                }catch (Exception e) {
                    Toast.makeText(getBaseContext(), "MSSV không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //dang tai tai khoan
                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getBaseContext(), "Thông tin đăng kí không đúng do email lỗi hoặc bị trùng!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                                    //write new user
                                    String id_fac = "";
                                    if (major.toLowerCase().equals("Khoa Khoa học và Kỹ thuật Máy tính".toLowerCase())){
                                        id_fac = "1";
                                    }else{
                                        if (major.toLowerCase().equals("Khoa điện - điện tử".toLowerCase())){
                                            id_fac = "2";
                                        }
                                        else
                                            id_fac = "3";
                                    }

                                    FirebaseUser profileuser = auth.getCurrentUser();
                                    String ID = profileuser.getUid();
                                    DocumentReference db = FirebaseFirestore.getInstance().collection("users").document(ID);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("admin", false);
                                    user.put("emailcollege", email);
                                    user.put("firstname", fna);
                                    user.put("id_faculty", id_fac);
                                    user.put("lastname", lna);
                                    user.put("mssv", mssv);
                                    db.set(user);
                                    //finish();
                                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                                }
                            }
                        });
            }
        });
    }
}
