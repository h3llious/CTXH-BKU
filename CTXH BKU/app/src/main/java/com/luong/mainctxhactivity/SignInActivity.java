package com.luong.mainctxhactivity;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.OnProgressListener;

public class SignInActivity extends AppCompatActivity {

    private EditText eEmailL, ePassL;
    private Button btnLogin;
    private TextView rePass, signup;
    private FirebaseAuth auth;
    private ProgressBar progress_Bar;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        rePass = findViewById(R.id.forgetpass);

        eEmailL = findViewById(R.id.edit_mail);
        ePassL = findViewById(R.id.edit_pass);
        btnLogin = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        progress_Bar = (ProgressBar) findViewById(R.id.progressBar);

        signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        rePass.setPaintFlags(rePass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        /////////////////////////////////////login///////////////////
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmailL.getText().toString().trim();
                final String pass = ePassL.getText().toString().trim();

                if (email.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progress_Bar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progress_Bar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    Toast.makeText(getBaseContext(), "Email hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                    startActivity(intent);
                                    //finish();
                                }
                            }

                        });
            }
        });
        /////////////////////////////////////////signin////////////////////////////
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SignUpActivity.class));
            }
        });
        /////////////////////////////////////changepassword///////////////////////
        rePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmailL.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(getBaseContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progress_Bar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progress_Bar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getBaseContext(), "Kiểm tra mail để nhận link thay dổi", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Email chưa đăng ký", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(false);
    }
}
