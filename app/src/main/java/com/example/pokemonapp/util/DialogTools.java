package com.example.pokemonapp.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.pokemonapp.R;

public class DialogTools {

    /**
     * Creates an AlertDialog object with two buttons.
     * @param context context of the activity calling this method.
     * @param title String defining the title of the dialog.
     * @param message String defining the text in the body of this dialog.
     * @param rightButtonText String defining text of the button on the right.
     * @param leftButtonText String defining text of the button on the left.
     * @param onClickListenerRightButton listener of the button on the right.
     * @param onClickListenerLeftButton listener of the button on the left.
     * @return a dialog object with two buttons.
     */
    public static AlertDialog dualButtonDialog(Context context, String title, String message,
                                               String rightButtonText, String leftButtonText,
                                               DialogInterface.OnClickListener onClickListenerRightButton,
                                               DialogInterface.OnClickListener onClickListenerLeftButton){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(rightButtonText, onClickListenerRightButton)
                .setNegativeButton(leftButtonText, onClickListenerLeftButton);
        return builder.create();
    }

    /**
     * Creates an AlertDialog object with one button.
     * @param context context of the activity calling this method.
     * @param title String defining the title of the dialog.
     * @param message String defining the text in the body of this dialog.
     * @param buttonText String defining the text of the button.
     * @param onClickListenerButton listener associated to the button.
     * @return a dialog object with one button.
     */
    public static AlertDialog singleButtonDialog(Context context, String title, String message, String buttonText,
                                                 DialogInterface.OnClickListener onClickListenerButton){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(buttonText, onClickListenerButton);
        return builder.create();
    }

    /**
     * Creates an AlertDialog object with one button.
     * @param context context of the activity calling this method.
     * @param title String defining the title of the dialog.
     * @param message Spanned object defining the text in the body of this dialog(a Spanned object
     *                allows us to define the text by using HTML and converting it to a Spanned by
     *                using the method Html.fromHtml().
     * @param buttonText String defining the text of the button.
     * @param onClickListenerButton listener associated to the button.
     * @return a dialog object with one button.
     */
    public static AlertDialog singleButtonDialog(Context context, String title, Spanned message, String buttonText,
                                                 DialogInterface.OnClickListener onClickListenerButton){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(buttonText, onClickListenerButton);
        return builder.create();
    }

    /**
     * Creates a ProgressDialog object with a spinner indicating that something is being processed.
     * When the processing finished it can be dismissed to allow the user to interact with the UI.
     * @param context context of the activity calling the method.
     * @return a ProgressDialog object with a spinner indicating that something is being loaded.
     */
    public static ProgressDialog loadingDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.loading_dialog_title));
        progressDialog.setMessage(context.getString(R.string.loading_dialog_message));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);    // it can be set to true if one wants to allow the
                                                // dialog to be dismissed when clicking out of it
        return progressDialog;
    }

    /**
     * dismisses the specified ProgressDialog when the specified view is drawn.
     * @param viewGroup view that will determine the dismissing of the dialog.
     * @param dialog ProgressDialog to be dismissed.
     */
    public static void dismissDialogWhenViewIsDrawn(ViewGroup viewGroup, ProgressDialog dialog) {
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dialog.dismiss();
                viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

}
