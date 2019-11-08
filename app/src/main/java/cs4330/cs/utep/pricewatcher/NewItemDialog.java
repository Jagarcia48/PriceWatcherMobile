package cs4330.cs.utep.pricewatcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

public class NewItemDialog extends AppCompatDialogFragment {

    public NewItemDialogListener listener;
    private EditText productsName;
    private EditText productsUrl;
    //private EditText productsPrice;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add, null);

        builder.setView(view).setTitle("Add new item")
                .setNegativeButton("cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("ok", (dialog, which) -> {
                    String name = productsName.getText().toString();
                    String url = productsUrl.getText().toString();
                    if(!name.equals("") && !url.equals("")){
                        listener.addItem(name, url);
                    }
                    else{
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        productsName = view.findViewById(R.id.ProductNameEditText);
        productsUrl = view.findViewById(R.id.ProductUrlEditText);
        if (getArguments() != null) {
            productsUrl.setText(getArguments().getString("url"));
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (NewItemDialogListener) context;
    }

    public interface NewItemDialogListener {
        void addItem(String name, String url);
    }
}
