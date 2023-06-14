package com.telemedicina;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.telemedicina.Adaptadores.DoctoresAdaptador;
import com.telemedicina.Entidades.Firebase.DatePickerFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorActivity extends AppCompatActivity {

    TextView nombre;
    ImageButton llamar, email, video;
    Button cita;
    Bundle datos;
    ImageButton back;
    EditText etPlannedDate, time;
    int hour, minute;
    DatePickerDialog datePicker;
    CircleImageView foto;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        nombre = findViewById(R.id.nombreDoctor);
        llamar = findViewById(R.id.llamadaButton);
        email = findViewById(R.id.CorreoButton);
        video = findViewById(R.id.VideoButton);
        cita = findViewById(R.id.reservar);
        foto = findViewById(R.id.CambiarImagen);
        etPlannedDate = (EditText) findViewById(R.id.etPlannedDate);
        time = (EditText) findViewById(R.id.to_time);
        back = findViewById(R.id.back);
        datePicker = new DatePickerDialog(DoctorActivity.this);
        final Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int mMinute = calendar.get(Calendar.MINUTE);
        final String[] fotos = new String[1];

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.etPlannedDate:
                        datePicker = new DatePickerDialog(DoctorActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                // adding the selected date in the edittext
                                etPlannedDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                            }
                        }, year, month, day);
                        datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                        datePicker.show();
                        break;
                }
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.to_time:
                        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int selectedHourOfDay, int selectedMinute) {
                                        Calendar tempDate = Calendar.getInstance();
                                        tempDate.set(Calendar.HOUR_OF_DAY,selectedHourOfDay);
                                        tempDate.set(Calendar.MINUTE,selectedMinute);

                                        SimpleDateFormat mSDF=new SimpleDateFormat("MM-dd-yyyy hh: mm ");


                                        //Hora minima 5:59 A.M
                                        Calendar dateTimeMin=Calendar.getInstance();
                                        dateTimeMin.set(Calendar.HOUR_OF_DAY,8);
                                        dateTimeMin.set(Calendar.MINUTE,00);

                                        //Hora maxima 6:01 P.M
                                        Calendar dateTimeMax=Calendar.getInstance();
                                        dateTimeMax.set(Calendar.HOUR_OF_DAY,21);
                                        dateTimeMax.set(Calendar.MINUTE,00);

                                        String hora=String.valueOf(selectedHourOfDay), minute=String.valueOf(selectedMinute);
                                        //*Valida si la hora seleccionada es permitida.
                                        if(tempDate.after(dateTimeMin) && tempDate.before(dateTimeMax)) {
                                            if (selectedHourOfDay < 10){
                                                hora = "0" + hora;
                                            }
                                            if (selectedMinute < 10){
                                                 minute = "0" + minute;
                                            }
                                            time.setText(hora+":"+minute);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Hora no permitida! " + mSDF.format(tempDate.getTime()), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }, mHour, mMinute, true);
                        timePickerDialog.show();
                        break;

                }
            }
        });

        datos = getIntent().getExtras();
        nombre.setText(datos.getString("nombre"));
        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = datos.getString("tlf");
                if (!TextUtils.isEmpty(phoneNo)) {
                    String dial = "tel:" + phoneNo;
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                }
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                Intent chooser = Intent.createChooser(intent, "Duo:");
                try {
                    startActivity(chooser);
                } catch (ActivityNotFoundException e) {
                    // Define what your app should do if no activity can handle the intent.
                }
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{datos.getString("correo").toString()});
                startActivity(intent);
            }
        });

        cita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorActivity.this);

                LayoutInflater inflater = getLayoutInflater();

                View view2 = inflater.inflate(R.layout.cita_dialog, null);

                builder.setView(view2);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button btnSI = view2.findViewById(R.id.aceptarButton);
                btnSI.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> doctors = new HashMap<>();
                                doctors.put("fecha", etPlannedDate.getText().toString());
                                doctors.put("hora", time.getText().toString());
                                db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas").document("(" + etPlannedDate.getText().toString() + ")" + " (" + time.getText().toString() + ")").set(doctors);
                                db.collection("doctors")
                                        .whereEqualTo("DNI", datos.getString("DNI"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> doctors = new HashMap<>();
                                                    doctors.put("fecha", etPlannedDate.getText().toString());
                                                    doctors.put("hora", time.getText().toString());
                                                    db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas").document("(" + etPlannedDate.getText().toString() + ")" + " (" + time.getText().toString() + ")").set(doctors);
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> users = new HashMap<>();
                                users.put("fecha", etPlannedDate.getText().toString());
                                users.put("hora", time.getText().toString());
                                users.put("doctor", datos.getString("nombre"));
                                users.put("especialidad", datos.getString("especialidad"));
                                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas").document("(" + etPlannedDate.getText().toString() + ")" + " (" + time.getText().toString() + ")").set(users);
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

}
        }

