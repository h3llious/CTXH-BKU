package com.luong.mainctxhactivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

public class AccountFragment extends Fragment {
    View view;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;

    ImageView img;
    TextView change_password;
    TextView name;
    TextView mssv;
    TextView email;
    TextView faculty;
    TextView ctxh_days;
    Button logout;

    String id;

    public AccountFragment() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        change_password = view.findViewById(R.id.change_password);
        change_password.setPaintFlags(change_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        img = view.findViewById(R.id.imageView1);


        name = view.findViewById(R.id.name_account);
        mssv = view.findViewById(R.id.MSSV);

        email = view.findViewById(R.id.email_account);

        faculty = view.findViewById(R.id.faculty);
        ctxh_days = view.findViewById(R.id.day_of_ctxh);

        logout = view.findViewById(R.id.logout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        id = mAuth.getCurrentUser().getUid();

        initView();
        updateCtxhDays();
        return view;
    }

    private void initView() {
        db.collection("users").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            updateFaculty(doc.get("id_faculty").toString());
                            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/ctxh-manager.appspot.com/o/437965.jpg?alt=media&token=da244dcd-e255-4ab0-920c-adb17f1f605a").into(img);
                            name.setText(doc.get("lastname").toString()+ " " + doc.get("firstname").toString());
                            email.setText(doc.get("emailcollege").toString());
                            mssv.setText(doc.get("mssv").toString());
                        }
                    }
                });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePassword();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm();
            }
        });
    }

    private void dialogChangePassword(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_reset_password);

        Button reset_btn = dialog.findViewById(R.id.reset_pass);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "Email sent.");
                                }
                            }
                        });
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void dialogConfirm(){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        Button logout_btn = dialog.findViewById(R.id.log_out);
        Button huy_btn = dialog.findViewById(R.id.huy);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(context, "Log out successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        huy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateFaculty(String id_faculty){
        db.collection("faculty").document(id_faculty)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            faculty.setText(task.getResult().get("name").toString());
                        }
                    }
                });
    }

    private void updateCtxhDays() {
        db.collection("registration")
                .whereEqualTo("id_user", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Double days = 0.0;
                        if(task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                days += doc.getDouble("ctxh_day_addto_user");
                                ctxh_days.setText(Double.toString(days));
                            }
                        }
                    }
                });
    }
}
