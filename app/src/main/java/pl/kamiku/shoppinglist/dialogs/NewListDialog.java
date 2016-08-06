package pl.kamiku.shoppinglist.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.kamiku.shoppinglist.MainApplication;
import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.data.ShoppingList;

public class NewListDialog extends DialogFragment {

    private ListAddedListener listAddedListener;

    @Override
    public void onDestroyView()
    {
        Dialog dialog = getDialog();

        // Work around bug: http://code.google.com/p/android/issues/detail?id=17423
        if ((dialog != null) && getRetainInstance())
            dialog.setDismissMessage(null);

        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ListAddedListener)
        {
            listAddedListener = (ListAddedListener) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_new_list, null);
        final EditText name  = (EditText) v.findViewById(R.id.name);
        final TextInputLayout textInputLayout = (TextInputLayout) v.findViewById(R.id.text_input_layout);
        final AlertDialog d = new AlertDialog.Builder(getActivity(),R.style.NewDialogStyle)

                .setTitle(R.string.new_shopping_list)
                .setPositiveButton(R.string.create,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .setView(v)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button cancel = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button create = d.getButton(AlertDialog.BUTTON_POSITIVE);
                if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    cancel.setTextColor(getResources().getColor(R.color.colorAccent));
                    create.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(name.getText().toString().isEmpty())
                        {
                            textInputLayout.setErrorEnabled(true);
                            textInputLayout.setError(getResources().getString(R.string.error_empty_name));
                        }
                        else {
                            ShoppingList shoppingList = new ShoppingList(name.getText().toString());
                            MainApplication.getDbHelper().insertShoppingList(shoppingList);
                            if (listAddedListener != null) {
                                listAddedListener.onListAdded(shoppingList);
                            }
                            d.dismiss();
                        }
                    }
                });
            }
        });
        return d;
    }

    public void setListAddedListener(ListAddedListener listAddedListener) {
        this.listAddedListener = listAddedListener;
    }

    public interface ListAddedListener
    {
        void onListAdded(ShoppingList shoppingList);
    }
}
