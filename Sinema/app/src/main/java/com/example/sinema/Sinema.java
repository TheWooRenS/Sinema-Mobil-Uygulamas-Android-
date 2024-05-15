package com.example.sinema;

import com.google.firebase.firestore.DocumentId;

public class Sinema {

    @DocumentId
    public String id;
    public String filmAdi;
    public String filmTuru;
    public String filmAfis;
    public String filmPuan;

}
