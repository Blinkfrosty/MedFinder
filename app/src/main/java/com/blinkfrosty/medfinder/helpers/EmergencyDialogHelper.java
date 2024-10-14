package com.blinkfrosty.medfinder.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.blinkfrosty.medfinder.R;

public class EmergencyDialogHelper {

    public static void showEmergencyCallDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_emergency_confirmation, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button buttonYes = dialogView.findViewById(R.id.emergency_dialog_button_yes);
        buttonYes.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999"));
            context.startActivity(intent);
        });

        Button buttonNo = dialogView.findViewById(R.id.emergency_dialog_button_no);
        buttonNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}