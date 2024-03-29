package com.luong.mainctxhactivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {


    static final String CHANNEL_ID = "ctxh bku";
    private static final String CHANNEL_NAME = "CTXH BKU";
    private static final String CHANNEL_DESC = "CTXH BKU Notification";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private int login = 0;

    FirebaseFirestore db;

    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        // notification initial

        //signIn("1611949@hcmut.edu.vn", "1611949");
        if(user == null) {
            requireLogin();
        }
        else {
            updateUI(user);
        }
        //Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
    }

    private void notificationInitial() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nchannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            nchannel.setDescription(CHANNEL_DESC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(nchannel);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();
                        myFirebaseMessagingService.newToken(token);
                        // Log and toast
                       // Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
        //.makeText(this, "onStart", Toast.LENGTH_SHORT).show();

        if (login == 1) {
            updateUI(mAuth.getCurrentUser());
            login += 1;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        if (login == 1) {
            updateUI(mAuth.getCurrentUser());
            login += 1;
        }
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(false);
    }

    private void requireLogin() {
        user = mAuth.getCurrentUser();
        login += 1;
        if(user == null) {
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navUserListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    ((HomeFragment) selectedFragment).setContext(MainActivity.this);
                    break;
                case R.id.registered:
                    selectedFragment = new RegisteredFragment();
                    ((RegisteredFragment) selectedFragment).setContext(MainActivity.this);
                    break;
                case R.id.notify:
                    selectedFragment = new NotificationFragment();
                    ((NotificationFragment) selectedFragment).setContext(MainActivity.this);
                    break;
                case R.id.account:
                    selectedFragment = new AccountFragment();
                    ((AccountFragment) selectedFragment).setContext(MainActivity.this);
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navAdminListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.posted:
                    selectedFragment = new PostedFragment();
                    ((PostedFragment) selectedFragment).setContext(MainActivity.this);
                    break;
                case R.id.add_ctxh:
                    selectedFragment = new AddFragment();
                    ((AddFragment) selectedFragment).setContext(MainActivity.this);
                    break;
                case R.id.notify:
                    selectedFragment = new NotificationFragment();
                    ((NotificationFragment) selectedFragment).setContext(MainActivity.this);
                    break;
                case R.id.account:
                    selectedFragment = new AccountFragment();
                    ((AccountFragment) selectedFragment).setContext(MainActivity.this);
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };


//    public void signIn(String email, String password) {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if (user == null) {
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Log.i("TAG", "signInWithEmail:success");
//                                Toast.makeText(MainActivity.this, "Authentication passed.", Toast.LENGTH_SHORT).show();
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.i("TAG", "signInWithEmail:failure", task.getException());
//                                Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                                updateUI(null);
//                            }
//
//                            // ...
//                        }
//                    });
//        }
//        else {
//            updateUI(user);
//        }
//    }

    public void updateUI(FirebaseUser user) {
        this.user = user;

        if(user != null) {
            notificationInitial();

            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getBoolean("admin")) {
                                bottomNavigationView.inflateMenu(R.menu.bottom_navigation_admin);

                                bottomNavigationView.setOnNavigationItemSelectedListener(navAdminListener);

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostedFragment()).commit();
                            }
                            else {
                                bottomNavigationView.inflateMenu(R.menu.bottom_navigation_user);

                                bottomNavigationView.setOnNavigationItemSelectedListener(navUserListener);

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                            }
                        } else {
                            Log.d("Get user", "No such document");
                        }
                    } else {
                        Log.d("Get user", "get failed with ", task.getException());
                    }
                }
            });
        }

    }
}
