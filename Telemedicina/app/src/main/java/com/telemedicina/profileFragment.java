package com.telemedicina;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link profileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class profileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    String storage_path = "pet/*";

    private static final int COD_SEL_STORAGE = 200;
    private static final int COD_SEL_IMAGE = 300;

    private Uri mImageUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/proyecto-dam-ded5c.appspot.com/o/imagenes_chat%2Fdefault.png?alt=media&token=30b9c3e3-5ed3-47e0-a137-08c0088107da");
    String photo = "photo";
    String idd;

    ProgressDialog progressDialog;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public profileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment profileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static profileFragment newInstance(String param1, String param2) {
        profileFragment fragment = new profileFragment();
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

    CircleImageView foto;
    TextView correo;
    EditText nombre, dni, telefono;
    Button editar, guardar;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        correo = view.findViewById(R.id.correoPerfil);
        nombre = view.findViewById(R.id.nombrePerfil);
        dni = view.findViewById(R.id.dniPerfil);
        telefono = view.findViewById(R.id.telefonoPerfil);
        foto = view.findViewById(R.id.CambiarImagen);
        editar = view.findViewById(R.id.editarCampos);
        guardar = view.findViewById(R.id.aceptar);
        guardar.setVisibility(View.GONE);
        nombre.setEnabled(false);
        dni.setEnabled(false);
        telefono.setEnabled(false);
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(foto);
        DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            correo.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    nombre.setText(value.getString("Nombre"));
                    dni.setText(value.getString("DNI"));
                    telefono.setText(value.getString("Telefono"));
                }
            });
        }
        DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            correo.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    nombre.setText(value.getString("Nombre"));
                    dni.setText(value.getString("DNI"));
                    telefono.setText(value.getString("Telefono"));
                }
            });
        }

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre.setEnabled(true);
                dni.setEnabled(true);
                telefono.setEnabled(true);
                editar.setVisibility(View.GONE);
                guardar.setVisibility(View.VISIBLE);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    docRef.update("Nombre", nombre.getText().toString());
                    docRef.update("DNI", dni.getText().toString());
                    docRef.update("Telefono", telefono.getText().toString());
                    Toast.makeText(view.getContext(), "Se ha actualizado tu perfil", Toast.LENGTH_SHORT).show();
                }
                DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                if (docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    docRef2.update("Nombre", nombre.getText().toString());
                    docRef2.update("DNI", dni.getText().toString());
                    docRef2.update("Telefono", telefono.getText().toString());
                    Toast.makeText(view.getContext(), "Se ha actualizado tu perfil", Toast.LENGTH_SHORT).show();
                }
                nombre.setEnabled(false);
                dni.setEnabled(false);
                telefono.setEnabled(false);
                editar.setVisibility(View.VISIBLE);
                guardar.setVisibility(View.GONE);
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto();
                subirPhoto(mImageUri);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                mImageUri = data.getData();
                subirPhoto(mImageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void subirPhoto(Uri image_url) {
        String rute_storage_photo = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(image_url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            DocumentReference docRef = db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if (docRef.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                HashMap<String, Object> doctors = new HashMap<>();
                                doctors.put("FotoPerfil", download_uri);
                                db.collection("doctors").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(doctors);
                                Glide.with(getView().getContext()).load(download_uri).into(foto);
                                MainActivity.ActualizarImagenPerfil(download_uri, getView().getContext());
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(download_uri))
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                }
                                            }
                                        });
                            }
                            DocumentReference docRef2 = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            if (docRef2.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                HashMap<String, Object> users = new HashMap<>();
                                users.put("FotoPerfil", download_uri);
                                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(users);
                                Glide.with(getView().getContext()).load(download_uri).into(foto);
                                MainActivity.ActualizarImagenPerfil(download_uri, getView().getContext());
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(download_uri))
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getView().getContext(), "Su foto se actualizó correctamente", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");

        startActivityForResult(i, COD_SEL_IMAGE);
    }

}