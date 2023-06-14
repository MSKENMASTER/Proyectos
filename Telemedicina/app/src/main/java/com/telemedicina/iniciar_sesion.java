package com.telemedicina;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.ktx.Firebase;

public class iniciar_sesion extends AppCompatActivity {

    Button login, olvide;
    EditText correo, password;
    ImageButton back;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        login = findViewById(R.id.iniciar);
        correo = findViewById(R.id.correoText);
        password = findViewById(R.id.passText);
        back = findViewById(R.id.back2);
        olvide = findViewById(R.id.olvidaPass);

        olvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                LayoutInflater inflater = LayoutInflater.from(view.getContext());

                View view2= inflater.inflate(R.layout.olvida_layout, null);
                EditText correo2 = view2.findViewById(R.id.CorreoOlvida);

                builder.setView(view2);

                final AlertDialog dialog = builder.create();
                dialog.show();

                Button btnSI = view2.findViewById(R.id.enviarBoton);
                btnSI.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAuth.setLanguageCode("es");
                        mAuth.sendPasswordResetEmail(correo2.getText().toString());
                        dialog.cancel();
                    }
                });

                Button btnNO = view2.findViewById(R.id.cancelarboton);
                btnNO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), StartActivity.class);
                startActivity(intent);
            }
        });


        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(iniciar_sesion.this, MainActivity.class);
                                    startActivity(intent);
                        }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(iniciar_sesion.this, "Error, el correo o la contraseña son erróneos", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }
}