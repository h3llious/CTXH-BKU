package com.luong.mainctxhactivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private Button btnSignup;
    private EditText eEmail, ePass, eLname, eFname, eCheck;
    private FirebaseAuth auth;


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
        btnSignup =  findViewById(R.id.login_sn);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmail.getText().toString().trim();
                String pass = ePass.getText().toString().trim();
                String check = eCheck.getText().toString().trim();
                String lna = eLname.getText().toString().trim();
                String fna = eFname.getText().toString().trim();
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
                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getBaseContext(), "Tạo tài khoản thành công!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getBaseContext(), "Thông tin đăng kí không đúng!" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                                    //finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
}
