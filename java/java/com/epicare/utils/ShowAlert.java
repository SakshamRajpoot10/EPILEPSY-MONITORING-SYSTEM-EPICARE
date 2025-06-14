package com.epicare.utils;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by AndroiddevPP on 5/19/2015.
 */
public class ShowAlert {

    /**
     * Added by Ramakrishna
     */
    public static void getConfirmDialog(Context mContext,String title, String msg, String positiveBtnCaption, String negativeBtnCaption, boolean isCancelable, final AlertMagnaticInterface target) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        //android.support.v7.app.AlertDialog.Builder builder =

        //int imageResource = android.R.drawable.ic_dialog_alert;
        //Drawable image = mContext.getResources().getDrawable(imageResource);

        builder.setTitle(title).setMessage(msg).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                target.PositiveMethod(dialog, id);
            }
        }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                target.NegativeMethod(dialog, id);
            }
        });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
        if (isCancelable) {
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    target.NegativeMethod(null, 0);
                }
            });
        }
    }

}
