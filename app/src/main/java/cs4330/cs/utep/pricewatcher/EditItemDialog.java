package cs4330.cs.utep.pricewatcher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;


public class EditItemDialog extends AppCompatDialogFragment {

    public EditItemDialogListener listener;
    private EditText productName;
    private EditText productUrl;

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit, null);
        builder.setView(view).setTitle("Edit item")
                .setNegativeButton("cancel", (dialog, which) -> dialog.cancel())
                .setPositiveButton("ok", (dialog, which) -> {
                    String name = productName.getText().toString();
                    String url = productUrl.getText().toString();
                    if (!name.equals("") && !url.equals("")) {
                        listener.updateItem(name, url, getArguments().getInt("index"));
                    }
                });
        productName = view.findViewById(R.id.EditTextName);
        productUrl = view.findViewById(R.id.EditTextUrl);
        productName.setText(getArguments().getString("currentName"));
        productUrl.setText(getArguments().getString("currentUrl"));
        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (EditItemDialogListener) context;
    }

    public interface EditItemDialogListener {
        void updateItem(String name, String url, int index);
    }
}
