package com.telemedicina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CrearCuenta extends AppCompatActivity {

    Button crearcuenta;
    EditText correo, pass, nombre, dni, telefono, pass2;
    RadioButton usuario, doctor;
    Spinner spinner;
    ImageButton back;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cuenta);
        crearcuenta = findViewById(R.id.iniciar);
        correo = findViewById(R.id.correoText);
        pass = findViewById(R.id.passText);
        pass2 = findViewById(R.id.passText2);
        nombre = findViewById(R.id.nombreText);
        dni = findViewById(R.id.dniText);
        telefono = findViewById(R.id.telefonoText);
        usuario = findViewById(R.id.Usuario);
        doctor = findViewById(R.id.Doctor);
        spinner = findViewById(R.id.spinner);
        spinner.setEnabled(false);

        back = findViewById(R.id.back3);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartActivity.class);
                startActivity(intent);
            }
        });

        doctor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

           @Override
           public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    spinner.setEnabled(true);
                }else{
                    spinner.setEnabled(false);
                }
           }
          }
        );

        crearcuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((correo.getText().toString().isEmpty() == false && pass.getText().toString().isEmpty() == false) &&
                        (dni.getText().toString().length()==9 && (dni.getText().toString().charAt(8) >= 65 || dni.getText().toString().charAt(8) <= 90)) && telefono.getText().toString().length()==9 && (pass.getText().toString().equals(pass2.getText().toString()))) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo.getText().toString(), pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CrearCuenta.this, "El registro se ha realizado correctamente", Toast.LENGTH_LONG).show();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/imagenes_chat%2Fdefault.png?alt=media&token=30b9c3e3-5ed3-47e0-a137-08c0088107da"))
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    }
                                                }
                                            });

                                    if (usuario.isChecked()) {
                                        Map<String, Object> users = new HashMap<>();
                                        users.put("Nombre", nombre.getText().toString());
                                        users.put("DNI", dni.getText().toString());
                                        users.put("Telefono", telefono.getText().toString());
                                        users.put("Correo", correo.getText().toString());
                                        users.put("Password", pass.getText().toString());
                                        users.put("FotoPerfil", "https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/imagenes_chat%2Fdefault.png?alt=media&token=30b9c3e3-5ed3-47e0-a137-08c0088107da");
                                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(users);
                                    } else if (doctor.isChecked()) {
                                        Map<String, Object> doctors = new HashMap<>();
                                        doctors.put("Nombre", nombre.getText().toString());
                                        doctors.put("DNI", dni.getText().toString());
                                        doctors.put("Telefono", telefono.getText().toString());
                                        doctors.put("Correo", correo.getText().toString());
                                        doctors.put("Password", pass.getText().toString());
                                        doctors.put("Especialidad", spinner.getSelectedItem());
                                        doctors.put("FotoPerfil", "https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/imagenes_chat%2Fdefault.png?alt=media&token=30b9c3e3-5ed3-47e0-a137-08c0088107da");
                                        db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(doctors);
                                    }

                                    Intent intent = new Intent(CrearCuenta.this, StartActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CrearCuenta.this, "Error, Ese correo ya tiene una cuenta asociada", Toast.LENGTH_LONG).show();
                            }
                        });

                }else{
                    Toast.makeText(CrearCuenta.this, "Error, Algún campo del formulario está rellenado de forma incorrecta", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}