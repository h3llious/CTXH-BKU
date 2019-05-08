package com.luong.mainctxhactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class DetailFragment extends Fragment {
    CtxhItem item;
    FirebaseFirestore db;
    View view;

    ImageView img;
    TextView deadline;
    TextView title;
    TextView start;
    TextView end;
    TextView social_day;
    TextView faculty;
    TextView location;
    TextView maxReg;
    TextView desc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_detail, container, false);

        String docId = getArguments().getString("docId");

        img = view.findViewById(R.id.imageView);
        deadline = view.findViewById(R.id.deadline_detail);
        title = view.findViewById(R.id.title_detail);
        start = view.findViewById(R.id.time_start_detail);
        end = view.findViewById(R.id.time_end_detail);
        social_day = view.findViewById(R.id.social_day);
        faculty = view.findViewById(R.id.faculty);
        location = view.findViewById(R.id.location2);
        maxReg = view.findViewById(R.id.maxNum);
        desc = view.findViewById(R.id.description2);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("ctxh").document(docId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String facultyNum = document.get("id_faculty").toString();

                    DocumentReference docFacultyName = db.collection("faculty").document(facultyNum);
                    docFacultyName.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot docFaculty = task.getResult();
                                if (docFaculty.exists()) {
                                    faculty.setText(docFaculty.get("name").toString());;
                                }
                            }
                        }
                    });


                    if (document.exists()) {
                        Log.d("SocialWork", "DocumentSnapshot data: " + document.getData());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, dd-MM-yyyy");

                        title.setText(document.get("title").toString());
                        Picasso.get().load(document.get("image").toString()).into(img);
                        deadline.setText(dateFormat.format(document.getTimestamp("deadline_register").toDate()));
                        start.setText(dateFormat.format(document.getTimestamp("time_start").toDate()));
                        end.setText(dateFormat.format(document.getTimestamp("time_end").toDate()));
                        social_day.setText(document.get("maximum_ctxh_day").toString());
                        location.setText(document.get("location").toString());
                        maxReg.setText(document.get("maximum_register").toString());
                        desc.setText(document.get("description").toString());
                    } else {
                        Log.d("SocialWork", "No such document");
                    }
                } else {
                    Log.d("SocialWork", "get failed with ", task.getException());
                }
            }
        });

        return view;
    }

}
