package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import com.blinkfrosty.medfinder.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelperBase {

    private final DatabaseReference databaseReference;

    public DatabaseHelperBase(Context context) {
        String databaseUrl = context.getString(R.string.firebase_database_url);
        databaseReference = FirebaseDatabase.getInstance(databaseUrl).getReference();
    }

    protected DatabaseReference getDatabaseReference() {
        return databaseReference;
    }
}