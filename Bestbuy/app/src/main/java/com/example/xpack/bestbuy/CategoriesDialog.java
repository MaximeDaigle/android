package com.example.xpack.bestbuy;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;


public class CategoriesDialog extends DialogFragment {




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.categories_dialog)
                .setItems(R.array.array_of_categories, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item);
                        Filter.lin.addView(Filter.getView(getResources().getStringArray(R.array.array_of_categories)[which],"categories"));
                       // lin.addView(getView(getResources().getStringArray(R.array.array_of_categories)[which]));
                        View v=(View) getActivity().findViewById(R.id.cat);
                        LinearLayout l=(LinearLayout) getActivity().findViewById(R.id.categories);
                        l.setVisibility(View.GONE);
                        v.setVisibility(View.GONE);




                    }
                });


        return builder.create();
    }







}
