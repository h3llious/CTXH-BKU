package info.androidhive.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {
    private EditText eEmailL, ePassL;
    private Button btnLogin, btnRegis;
    private Button rePass;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rePass = findViewById(R.id.change_password);

        eEmailL = (EditText) findViewById(R.id.edit_mail);
        ePassL = (EditText) findViewById(R.id.edit_pass);
        btnLogin = (Button) findViewById(R.id.login);
        btnRegis = (Button) findViewById(R.id.register);

        /////////////////////////////////////login///////////////////
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmailL.getText().toString().trim();
                final String pass = ePassL.getText().toString().trim();

                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(AccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    Toast.makeText(getApplicationContext(), "Email hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                }
        });
        /////////////////////////////////////////signin////////////////////////////
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        /////////////////////////////////////changepassword///////////////////////
        rePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmailL.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Kiểm tra mail để nhận link thay dổi", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Kiểm tra mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
