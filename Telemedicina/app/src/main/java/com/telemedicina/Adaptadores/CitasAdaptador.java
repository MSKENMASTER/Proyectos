package com.telemedicina.Adaptadores;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telemedicina.Entidades.Firebase.Cita;
import com.telemedicina.R;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

public class CitasAdaptador extends FirestoreRecyclerAdapter<Cita, CitasAdaptador.ViewHolderCitas> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CitasAdaptador(@NonNull FirestoreRecyclerOptions<Cita> options) {
        super(options);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public CitasAdaptador.ViewHolderCitas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.citas_card,parent,false);
        return new CitasAdaptador.ViewHolderCitas(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull ViewHolderCitas holder, int position, @NonNull Cita model) {
        holder.fecha.setText(model.getFecha());
        holder.hora.setText(model.getHora());
        holder.doctor.setText(model.getDoctor());
        holder.especialidad.setText(model.getEspecialidad());
        int dia, mes, year, horas, minuto;
        String[] datos = holder.fecha.getText().toString().split("-");
        String[] datos2 = holder.hora.getText().toString().split(":");

        dia = Integer.parseInt(datos[0].trim());
        mes = Integer.parseInt(datos[1].trim());
        year = Integer.parseInt(datos[2].trim());
        horas = Integer.parseInt(datos2[0].trim());
        minuto = Integer.parseInt(datos2[1].trim());

        LocalDateTime fechaFinal = LocalDateTime.of(year,mes,dia, horas,minuto);
        if (fechaFinal.isAfter(LocalDateTime.now())){
            holder.editar.setVisibility(View.VISIBLE);
        } else if (fechaFinal.isBefore(LocalDateTime.now())) {
            holder.editar.setVisibility(View.GONE);
        }
    }

    public class ViewHolderCitas extends RecyclerView.ViewHolder {

        TextView doctor, fecha, hora, especialidad;
        ImageButton cancelar, editar;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public ViewHolderCitas(@NonNull View itemView) {
            super(itemView);
            doctor = itemView.findViewById(R.id.doctorCita);
            fecha = itemView.findViewById(R.id.fechaCita);
            hora = itemView.findViewById(R.id.horaCita);
            cancelar = itemView.findViewById(R.id.cancelarButton);
            editar = itemView.findViewById(R.id.editarButton);
            especialidad = itemView.findViewById(R.id.especialidadCita);

            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

                    LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                    View view2 = inflater.inflate(R.layout.editar_dialog, null);
                    EditText fechaDialog = view2.findViewById(R.id.CorreoOlvida);
                    fechaDialog.setText(fecha.getText().toString());
                    EditText horaDialog = view2.findViewById(R.id.editarHora);
                    horaDialog.setText(hora.getText().toString());

                    final Calendar calendar = Calendar.getInstance();
                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int year = calendar.get(Calendar.YEAR);
                    final int month = calendar.get(Calendar.MONTH);
                    final int mHour = calendar.get(Calendar.HOUR_OF_DAY);
                    final int mMinute = calendar.get(Calendar.MINUTE);

                    fechaDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.CorreoOlvida:
                                    DatePickerDialog datePicker = new DatePickerDialog(view.getContext());
                                    datePicker = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                            // adding the selected date in the edittext
                                            fechaDialog.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                        }
                                    }, year, month, day);
                                    datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                                    datePicker.show();
                                    break;
                            }
                        }
                    });

                    horaDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.editarHora:
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
                                                        horaDialog.setText(hora+":"+minute);
                                                    } else {
                                                        Toast.makeText(view.getContext(), "Hora no permitida! " + mSDF.format(tempDate.getTime()), Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }, mHour, mMinute, true);
                                    timePickerDialog.show();
                                    break;

                            }
                        }
                    });



                    builder.setView(view2);

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    Button btnSI = view2.findViewById(R.id.enviarBoton);
                    btnSI.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if(docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                DocumentReference docRef2 = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas")
                                        .document("(" + fecha.getText().toString() + ")" + " (" + hora.getText().toString() + ")");
                                docRef2.update("fecha",fechaDialog.getText().toString());
                                docRef2.update("hora",horaDialog.getText().toString());
                                dialog.cancel();
                            }
                            DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if(docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                DocumentReference docRef3 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas")
                                        .document("(" + fecha.getText().toString() + ")" + " (" + hora.getText().toString() + ")");
                                docRef3.update("fecha",fechaDialog.getText().toString());
                                docRef3.update("hora",horaDialog.getText().toString());
                                dialog.cancel();
                            }
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


            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

                    LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

                    View view2 = inflater.inflate(R.layout.cancelaar_cita_dialog, null);

                    builder.setView(view2);

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    Button btnSI = view2.findViewById(R.id.aceptarButton);
                    btnSI.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if(docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                DocumentReference docRef2 = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas")
                                        .document("(" + fecha.getText().toString() + ")" + " (" + hora.getText().toString() + ")");
                                docRef2.delete();
                                dialog.cancel();
                            }
                            DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if(docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                DocumentReference docRef3 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas")
                                        .document("(" + fecha.getText().toString() + ")" + " (" + hora.getText().toString() + ")");
                                docRef3.delete();
                                dialog.cancel();
                            }

                        }
                    });

                    Button btnNO = view2.findViewById(R.id.nobutton);
                    btnNO.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                }
            });
        }
    }
}
