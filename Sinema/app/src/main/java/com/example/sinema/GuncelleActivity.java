package com.example.sinema;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

public class GuncelleActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String id;
    EditText edtFilmAdi,edtPuan;

    ImageView imgFoto;
    Uri imageUri;
    Spinner edtTur;
    String[] turler = new String[] { "Korku", "Komedi", "Aksiyon", "Macera", "Animasyon", "Romantik", "Bilim Kurgu", "Fantastik"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guncelle);
        edtFilmAdi=findViewById(R.id.gedtAd);
        edtPuan=findViewById(R.id.gedtPuan);
        edtTur = findViewById(R.id.gedtTur);
        imgFoto = findViewById(R.id.GimgFoto);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        db = FirebaseFirestore.getInstance();



        DocumentReference docRef = db.collection("filmler").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Sinema sinema = documentSnapshot.toObject(Sinema.class);
                edtFilmAdi.setText(sinema.filmAdi);
                edtPuan.setText(sinema.filmPuan);
                turler[0] = sinema.filmTuru;
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference fotoRef = storageRef.child("images/"+sinema.filmAfis);

                final long ONE_MEGABYTE = 1024 * 1024;
                fotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() { @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imgFoto.setImageBitmap(bitmap);
                }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Hata: "+ exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item , turler);
        edtTur.setAdapter(spinnerAdapter);;

    }
    public void GaddImage(View view) {

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

    public void guncelle(View view) {


        FirebaseStorage storage = FirebaseStorage.getInstance();
        String fotoAdi = UUID.randomUUID().toString();
        StorageReference storageReference = storage.getReference();
        StorageReference fotoRef = storageReference.child("images/" + fotoAdi);
        Sinema sinema = new Sinema();
        sinema.filmAdi = edtFilmAdi.getText().toString();
        sinema.filmPuan = edtPuan.getText().toString();
        sinema.filmTuru = edtTur.getSelectedItem().toString();
        sinema.filmAfis = fotoAdi;

        fotoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                db.collection("filmler").document(id)
                        .set(sinema)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GuncelleActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}