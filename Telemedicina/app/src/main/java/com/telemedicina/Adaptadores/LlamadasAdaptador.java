package com.telemedicina.Adaptadores;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.telemedicina.Entidades.Firebase.Llamada;
import com.telemedicina.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class LlamadasAdaptador extends FirestoreRecyclerAdapter<Llamada, LlamadasAdaptador.ViewHolderLlamadas> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LlamadasAdaptador(@NonNull FirestoreRecyclerOptions<Llamada> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderLlamadas holder, int position, @NonNull Llamada model) {
        holder.tlf.setText(model.getTlf());
        holder.fecha.setText(model.getFecha());
        holder.setFotoLlamada(model);
    }

    @NonNull
    @Override
    public ViewHolderLlamadas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.call_card,parent,false);
        return new ViewHolderLlamadas(view);
    }


    public static class ViewHolderLlamadas extends RecyclerView.ViewHolder{
        TextView tlf, fecha;
        CircleImageView tipo;
         ImageButton llamada;

        public ViewHolderLlamadas(@NonNull View itemView) {
            super(itemView);
            tlf = itemView.findViewById(R.id.fechaCita);
            fecha = itemView.findViewById(R.id.fecha);
            tipo = itemView.findViewById(R.id.tipoLlamada);
            llamada = itemView.findViewById(R.id.devolverllamada);
            llamada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phoneNo = tlf.getText().toString();
                    if(!TextUtils.isEmpty(phoneNo)) {
                        String dial = "tel:" + phoneNo;
                        itemView.getContext().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
                    }else {
                        Toast.makeText(itemView.getContext(), "Enter a phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void setFotoLlamada(Llamada llamada){
                Glide.with(itemView.getContext()).load(llamada.getTipo()).into(tipo);

        }
    }


}
