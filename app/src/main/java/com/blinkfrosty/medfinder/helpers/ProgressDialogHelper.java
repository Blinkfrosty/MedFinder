package com.blinkfrosty.medfinder.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blinkfrosty.medfinder.R;

public class ProgressDialogHelper {

    private AlertDialog dialog;

    // Utility method to show the progress dialog
    public void showProgressDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_progress, null);
        builder.setView(dialogView);

        TextView progressText = dialogView.findViewById(R.id.dialog_progress_text);
        progressText.setText(message);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    // Utility method to dismiss the progress dialog
    public void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
