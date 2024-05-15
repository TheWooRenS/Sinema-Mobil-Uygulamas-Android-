package com.example.sinema;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

public class DetayActivity extends AppCompatActivity {
    ImageView imgFoto;
    String fotoAdi, filmAdi, filmTur, filmPuan, id;
    TextView txtFotoAdi, txtAciklama;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detay);

        db = FirebaseFirestore.getInstance();
        imgFoto = findViewById(R.id.imgFoto);
        txtFotoAdi = findViewById(R.id.txtFilmAdi);
        txtAciklama = findViewById(R.id.detayTxtAciklama);
        Intent intent = getIntent();
        fotoAdi = intent.getStringExtra("fotoAdi");
        filmAdi = intent.getStringExtra("filmAdi");
        filmPuan = intent.getStringExtra("filmPuan");
        filmTur = intent.getStringExtra("filmTuru");
        id = intent.getStringExtra("id");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("images/"+fotoAdi);

        final long ONE_MEGABYTE = 1024 * 1024;
        fotoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgFoto.setImageBitmap(bitmap);
                txtFotoAdi.setText(filmAdi);
                txtAciklama.setText("Film Türü: " + filmTur + "\n" + "Film Puanı: "+ filmPuan);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(DetayActivity.this, "Hata: "+ exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sil(View view) {
        db.collection("filmler").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference fotoRef = storageRef.child("images/"+fotoAdi);

                        fotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(DetayActivity.this, "Hata " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetayActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                    }
                });


        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void goToGuncelle(View view) {
        Intent intent = new Intent(getApplicationContext(),GuncelleActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }
}