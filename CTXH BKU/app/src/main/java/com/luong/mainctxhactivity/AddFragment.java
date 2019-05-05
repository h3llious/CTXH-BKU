package com.luong.mainctxhactivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;

public class AddFragment extends Fragment {

    View view;
    EditText inputName;
    EditText inputDescription;
    EditText inputLocation;
    EditText inputStartTime;
    EditText inputDue;
    EditText inputDeadline;
    EditText inputDays;
    EditText inputMaxReg;
    Button buttonPost;

    Timestamp start;
    Timestamp due;
    Timestamp deadline;
    String name;
    String description;
    String location;
    double days;
    int maxReg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_ctxh, container, false);

        inputName = view.findViewById(R.id.editTextName);
        inputDescription = view.findViewById(R.id.editTextDescription);
        inputLocation = view.findViewById(R.id.editTextLocation);
        inputStartTime = view.findViewById(R.id.startTime);
        inputDue = view.findViewById(R.id.due);
        inputDeadline = view.findViewById(R.id.deadline);
        inputDays = view.findViewById(R.id.days);
        inputMaxReg = view.findViewById(R.id.maxReg);
        buttonPost = view.findViewById(R.id.buttonPost);

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    name = inputName.getText().toString();
                    if (name.length() == 0) {
                        inputName.setError("Empty title");
                    }
                }
            }
        });

        inputDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    description = inputDescription.getText().toString();
                }
            }
        });

        inputLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                location = inputLocation.getText().toString();
                if (!hasFocus) {
                    if (inputLocation.length() == 0) {
                        inputLocation.setError("Empty location");
                    }
                }
            }
        });

        inputStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    start = convertTime(inputStartTime.getText().toString());
                    if (start == null) {
                        inputStartTime.setError("Time input format must be \"DD/MM/YYYY HH:MM\"");
                    }
                }
            }
        });

        inputDue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    due = convertTime(inputDue.getText().toString());
                    if (due == null) {
                        inputDue.setError("Time input format must be \"DD/MM/YYYY HH:MM\"");
                    } else if (due.getSeconds() < start.getSeconds()) {
                        inputDue.setError("Wrong time: End before start");
                    }
                }
            }
        });

        inputDeadline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    deadline = convertTime(inputDeadline.getText().toString());
                    if (deadline == null) {
                        inputDeadline.setError("Time input format must be \"DD/MM/YYYY HH:MM\"");
                    } else if (deadline.getSeconds() > start.getSeconds()) {
                        inputDeadline.setError("Wrong time: Register after start");
                    }
                }
            }
        });

        inputDays.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (inputDays.length() == 0) {
                        inputDays.setError("Empty");
                    } else {
                        days = Double.parseDouble(inputDays.getText().toString());
                        if (days <= 0) {
                            inputDays.setError("Must be positive");
                        }
                    }
                }
            }
        });

        inputMaxReg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (inputMaxReg.length() == 0) {
                        inputMaxReg.setError("Empty");
                    } else {
                        maxReg = Integer.parseInt(inputMaxReg.getText().toString());
                        if (maxReg <= 0) {
                            inputMaxReg.setError("Must be positive");
                        }
                    }
                }
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parse();
            }
        });

        return view;
    }

    private void parse() {

        String id = "1";
        String imgURL = "https://firebasestorage.googleapis.com/v0/b/ctxh-manager.appspot.com/o/com_2k.jpg?alt=media&token=4493f4e9-5d1a-44ae-8f7e-27adc3854363";

        HashMap<String, Object> item = new HashMap<>();
        item.put("deadline_register", deadline);
        item.put("description", description);
        item.put("id_faculty", id);
        item.put("image", imgURL);
        item.put("location", location);
        item.put("maximum_ctxh_day", days);
        item.put("maximum_register", maxReg);
        item.put("time_end", due);
        item.put("time_start", start);
        item.put("title", name);

        FirebaseFirestore.getInstance()
                .collection("ctxh")
                .add(item)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Timestamp convertTime(String in) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault());
            return new Timestamp(format.parse(in + ":00"));
        } catch (ParseException e) {
            return null;
        }
    }

}
