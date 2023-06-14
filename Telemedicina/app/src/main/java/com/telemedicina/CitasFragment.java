package com.telemedicina;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.telemedicina.Adaptadores.CitasAdaptador;
import com.telemedicina.Adaptadores.LlamadasAdaptador;
import com.telemedicina.Entidades.Firebase.Cita;
import com.telemedicina.Entidades.Firebase.Llamada;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CitasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CitasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RecyclerView recycler;
    CitasAdaptador mAdapter;
    FirebaseFirestore mFirestore;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CitasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CitasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CitasFragment newInstance(String param1, String param2) {
        CitasFragment fragment = new CitasFragment();
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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_citas, container, false);
        recycler = view.findViewById(R.id.recyclerCitas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mFirestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Query query = mFirestore.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas");
            FirestoreRecyclerOptions<Cita> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Cita>().setQuery(query, Cita.class).build();
            mAdapter = new CitasAdaptador(firestoreRecyclerOptions);
            mAdapter.notifyDataSetChanged();
            recycler.setAdapter(mAdapter);
        }
        DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            Query query = mFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("citas");
            FirestoreRecyclerOptions<Cita> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Cita>().setQuery(query, Cita.class).build();
            mAdapter = new CitasAdaptador(firestoreRecyclerOptions);
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
}