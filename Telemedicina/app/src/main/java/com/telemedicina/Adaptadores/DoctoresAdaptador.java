package com.telemedicina.Adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.telemedicina.DoctorActivity;
import com.telemedicina.Entidades.Firebase.Doctor;
import com.telemedicina.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctoresAdaptador extends FirestoreRecyclerAdapter<Doctor, DoctoresAdaptador.ViewHolderDoctores> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DoctoresAdaptador(@NonNull FirestoreRecyclerOptions<Doctor> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolderDoctores onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctors_card,parent,false);
        return new ViewHolderDoctores(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderDoctores holder, int position, @NonNull Doctor doctor) {
        holder.nombre.setText(doctor.getNombre());
        holder.especialidad.setText(doctor.getEspecialidad());
        holder.setFotoperfil(doctor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), DoctorActivity.class);
                intent.putExtra("nombre", doctor.getNombre());
                intent.putExtra("tlf", doctor.getTelefono());
                intent.putExtra("DNI", doctor.getDNI());
                intent.putExtra("especialidad", doctor.getEspecialidad());
                intent.putExtra("correo", doctor.getCorreo());
                intent.putExtra("foto", doctor.getFotoPerfil());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    public class ViewHolderDoctores extends RecyclerView.ViewHolder {

        TextView nombre, especialidad;
        CircleImageView fotoperfil;

        public ViewHolderDoctores(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.fechaCita);
            especialidad = itemView.findViewById(R.id.fecha);
            fotoperfil = itemView.findViewById(R.id.tipoLlamada);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        public void setFotoperfil(Doctor doctor){
            Glide.with(itemView.getContext()).load(doctor.getFotoPerfil()).into(fotoperfil);
        }
    }
}
