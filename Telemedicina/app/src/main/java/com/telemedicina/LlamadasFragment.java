package com.telemedicina;


import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.telemedicina.Adaptadores.LlamadasAdaptador;
import com.telemedicina.Entidades.Firebase.Llamada;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LlamadasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LlamadasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    int REQUEST_CODE = 200;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recycler;
    LlamadasAdaptador mAdapter;
    FirebaseFirestore mFirestore;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    SwipeRefreshLayout mSwipeRefreshLayout;


    public LlamadasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LlamadasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LlamadasFragment newInstance(String param1, String param2) {
        LlamadasFragment fragment = new LlamadasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi (api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_llamadas, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mSwipeRefreshLayout.isRefreshing()) {
                    ObtenerDatosLlamadas();
                    mSwipeRefreshLayout.setRefreshing(false);
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        recycler = view.findViewById(R.id.recyclerllamadas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Query query = mFirestore.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("calls");
            FirestoreRecyclerOptions<Llamada> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Llamada>().setQuery(query, Llamada.class).build();
            mAdapter = new LlamadasAdaptador(firestoreRecyclerOptions);
            mAdapter.notifyDataSetChanged();
            recycler.setAdapter(mAdapter);
        }
        DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Query query = mFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("calls");
            FirestoreRecyclerOptions<Llamada> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Llamada>().setQuery(query, Llamada.class).build();
            mAdapter = new LlamadasAdaptador(firestoreRecyclerOptions);
            mAdapter.notifyDataSetChanged();
            recycler.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recycler.getRecycledViewPool().clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recycler.getRecycledViewPool().clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.stopListening();
    }
    @Override
    public void onPause() {
        super.onPause();
        recycler.getRecycledViewPool().clear();
        mAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean verficarPermisos2(){

        int PermisosLlamada2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);

        if(PermisosLlamada2 == PackageManager.PERMISSION_GRANTED){
        }else{
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
        }

        return true;
    }

    protected void ObtenerDatosLlamadas() {

        Uri uri;

        /*
        content://media/internal/images
        content://media/external/video
        content://media/internal/audio
        */

        //         content://media/*/images
        //         content://settings/system/ringtones

        uri = Uri.parse("content://call_log/calls");

        String[] projeccion = new String[]{CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DATE};



        Cursor c = getContext().getContentResolver().query(
                uri,
                projeccion,
                null,
                null,
                null);

        String tipo="", tlf="", fecha="";
        if(c.getCount()>0) {

            while (c.moveToNext()) {
                tipo = c.getString(0);
                tlf = c.getString(1);
                fecha = c.getString(2);
            }
            if(tipo.equals("1")) {
                tipo = "https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/llamada_recibida.png?alt=media&token=99844ef3-a8d0-42d4-ad4b-93d6643b025a";
            }else if(tipo.equals("2")){
                tipo = "https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/llamada_enviada.png?alt=media&token=b4002798-407a-476f-910c-1d8dc5e21a70";
            }
            final String tipofinal = tipo;
            final String tlffinal = tlf;

            Date date = new Date();
            date.setTime(Long.parseLong(fecha));
            final String fechafinal = new SimpleDateFormat().format(date);
            DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> doctors = new HashMap<>();
                            doctors.put("tipo", tipofinal);
                            doctors.put("tlf", tlffinal);
                            doctors.put("fecha", fechafinal.replaceAll("/", "-"));
                            db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("calls").document(fechafinal.replaceAll("/", "-")).set(doctors);
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
                            users.put("tipo", tipofinal);
                            users.put("tlf", tlffinal);
                            users.put("fecha", fechafinal.replaceAll("/", "-"));
                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("calls").document(fechafinal.replaceAll("/", "-")).set(users);
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

    }

}