package com.example.sinema;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SinemaAdapter extends RecyclerView.Adapter<SinemaAdapter.ViewHolder> {
    private Context context;
    StorageReference storageRef;
    private ArrayList<Sinema> arrayList;

    public SinemaAdapter(Context context, ArrayList<Sinema> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sinema_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtBaslik.setText(arrayList.get(position).filmAdi);
        holder.txtAciklama.setText(arrayList.get(position).filmTuru + " - Puan: " + arrayList.get(position).filmPuan);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sinema sinema = arrayList.get(holder.getAdapterPosition());
                Intent intent = new Intent(context,DetayActivity.class);
                intent.putExtra("id",sinema.id);
                intent.putExtra("fotoAdi",sinema.filmAfis);
                intent.putExtra("filmAdi",sinema.filmAdi);
                intent.putExtra("filmPuan",sinema.filmPuan);
                intent.putExtra("filmTuru",sinema.filmTuru);
                context.startActivity(intent);
            }
        });


        StorageReference imageRef = storageRef.child("images/"+arrayList.get(position).filmAfis);

        final long ONE_MEGABYTE = 1024 * 1024;
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.imgAfis.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Firebase", "Resim indirme başarısız: " + exception.getMessage());
                    }
                });

        holder.imgAfis.setImageDrawable(Drawable.createFromPath(arrayList.get(position).filmAfis));

        /*
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
         */
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBaslik, txtAciklama;

        ImageView imgAfis;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtBaslik = itemView.findViewById(R.id.txtBaslik);
            txtAciklama = itemView.findViewById(R.id.txtAciklama);
            imgAfis = itemView.findViewById(R.id.imgAfis);
        }
    }
}