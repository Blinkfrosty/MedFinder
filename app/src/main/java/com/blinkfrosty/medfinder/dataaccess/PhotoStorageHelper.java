package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoStorageHelper {

    private final StorageReference storageReference;
    private final Context context;
    private static final String STORAGE_CHILD = "user_photos";

    public PhotoStorageHelper(Context context) {
        this.context = context;
        this.storageReference = FirebaseStorage.getInstance().getReference().child(STORAGE_CHILD);
    }

    public void storeRemoteImage(String userId, Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        new DownloadAndUploadTask(context, storageReference, userId, photoUri, onSuccessListener, onFailureListener).execute();
    }

    public void storeLocalImage(String userId, Uri fileUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        StorageReference userPhotoRef = storageReference.child(userId + ".jpg");
        userPhotoRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> userPhotoRef.getDownloadUrl().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener))
                .addOnFailureListener(onFailureListener);
    }

    public void loadImageIntoImageView(String photoUrl, ImageView imageView) {
        Glide.with(context)
                .load(photoUrl)
                .into(imageView);
    }

    private static class DownloadAndUploadTask extends AsyncTask<Void, Void, Uri> {
        private final Context context;
        private final StorageReference storageReference;
        private final String userId;
        private final Uri photoUri;
        private final OnSuccessListener<Uri> onSuccessListener;
        private final OnFailureListener onFailureListener;

        public DownloadAndUploadTask(Context context, StorageReference storageReference, String userId, Uri photoUri, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
            this.context = context.getApplicationContext();
            this.storageReference = storageReference;
            this.userId = userId;
            this.photoUri = photoUri;
            this.onSuccessListener = onSuccessListener;
            this.onFailureListener = onFailureListener;
        }

        @Override
        protected Uri doInBackground(Void... voids) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                URL url = new URL(photoUri.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                inputStream = connection.getInputStream();
                File tempFile = File.createTempFile("profile_picture", ".jpg", context.getCacheDir());
                outputStream = new FileOutputStream(tempFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                Log.d("PhotoStorageHelper", "Image downloaded to " + tempFile.getAbsolutePath());
                return Uri.fromFile(tempFile);
            } catch (Exception e) {
                Log.e("PhotoStorageHelper", "Failed to download image", e);
                return null;
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                } catch (Exception e) {
                    Log.e("PhotoStorageHelper", "Failed to close streams", e);
                }
            }
        }

        @Override
        protected void onPostExecute(Uri localUri) {
            if (localUri != null) {
                StorageReference userPhotoRef = storageReference.child(userId + ".jpg");
                userPhotoRef.putFile(localUri)
                        .addOnSuccessListener(taskSnapshot -> userPhotoRef.getDownloadUrl()
                                .addOnSuccessListener(onSuccessListener)
                                .addOnFailureListener(onFailureListener))
                        .addOnFailureListener(onFailureListener);
            } else {
                Log.e("PhotoStorageHelper", "Failed to download image");
                onFailureListener.onFailure(new Exception("Failed to download image"));
            }
        }
    }
}