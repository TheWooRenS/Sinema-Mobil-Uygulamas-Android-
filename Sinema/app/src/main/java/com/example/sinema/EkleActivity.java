package com.example.sinema;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class EkleActivity extends AppCompatActivity {
    Spinner spinner;
    ImageView imgFoto;
    Uri imageUri;
    FirebaseFirestore db;

    EditText edtAdi, edtPuan;
    private static String[] turler = new String[] { "Korku", "Komedi", "Aksiyon", "Macera", "Animasyon", "Romantik", "Bilim Kurgu", "Fantastik"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekle);
        db = FirebaseFirestore.getInstance();
        edtAdi = findViewById(R.id.edtAd);
        edtPuan = findViewById(R.id.edtPuan);
        spinner = findViewById(R.id.edtTur);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item , turler);
        spinner.setAdapter(spinnerAdapter);

        imgFoto = findViewById(R.id.edtAfis);

    }

    public void addImage(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        galeriIntent.launch(intent);

    }

    ActivityResultLauncher<Intent> galeriIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        imageUri = result.getData().getData();
                        imgFoto.setImageURI(imageUri);
                    }
                }
            });

    public void ekle(View view) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fotoAdi = UUID.randomUUID().toString();
        StorageReference storageReference = storage.getReference();
        StorageReference fotoRef = storageReference.child("images/"+fotoAdi);


        Sinema sinema = new Sinema();
        sinema.filmAdi=edtAdi.getText().toString();
        sinema.filmPuan=edtPuan.getText().toString();
        sinema.filmTuru=spinner.getSelectedItem().toString();
        sinema.filmAfis = fotoAdi;

        fotoRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        db.collection("filmler")
                                .add(sinema)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EkleActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Hata: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}