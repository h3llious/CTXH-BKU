package info.androidhive.firebase;

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

public class MainActivity extends AppCompatActivity {
    private Button btnSignup, btnSignout;
    private EditText eEmail, ePass, eLname, eFname, eCheck;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        eEmail = (EditText) findViewById(R.id.edit_email);
        ePass = (EditText) findViewById(R.id.edit_pass_sn);
        eLname = (EditText) findViewById(R.id.edit_lname);
        eFname = (EditText) findViewById(R.id.edit_fname);
        eCheck = (EditText) findViewById(R.id.edit_checkpass);
        btnSignup = (Button) findViewById(R.id.login_sn);
        btnSignout = (Button) findViewById(R.id.signout);
        FirebaseUser user = this.auth.getCurrentUser();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = eEmail.getText().toString().trim();
                String pass = ePass.getText().toString().trim();
                String check = eCheck.getText().toString().trim();
                String lna = eLname.getText().toString().trim();
                String fna = eFname.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Nhập email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Nhập mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(check)) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu xác nhận không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lna.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nhập họ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fna.equals("")) {
                    Toast.makeText(getApplicationContext(), "Nhập tên!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công!" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Thông tin đăng kí không đúng!" + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    finish();
                }
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

