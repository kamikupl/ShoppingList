package pl.kamiku.shoppinglist.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import pl.kamiku.shoppinglist.MainApplication;
import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.data.ShoppingItem;

public class NewItemDialog extends DialogFragment {

    ItemAddedListener itemAddedListener;
    private static final String LIST_ID = "listId";
    private Long listId;

    public static NewItemDialog newInstance(Long listId)
    {
        NewItemDialog d = new NewItemDialog();

        Bundle args = new Bundle();
        args.putLong(LIST_ID, listId);
        d.setArguments(args);

        return d;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listId = getArguments().getLong(LIST_ID);
    }

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
        if(activity instanceof ItemAddedListener)
        {
            itemAddedListener = (ItemAddedListener) activity;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_new_item, null);
        final EditText name  = (EditText) v.findViewById(R.id.name);
        final TextInputLayout textInputLayout = (TextInputLayout) v.findViewById(R.id.text_input_layout);
        final AlertDialog d = new AlertDialog.Builder(getActivity(),R.style.NewDialogStyle)

                .setTitle(R.string.new_item)
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
                            ShoppingItem shoppingItem = new ShoppingItem();
                            shoppingItem.setBought(false);
                            shoppingItem.setName(name.getText().toString());
                            shoppingItem.setListId(listId);
                            shoppingItem.setId(MainApplication.getDbHelper().insertShoppingItem(shoppingItem));
                            itemAddedListener.onItemAdded(shoppingItem);
                            d.dismiss();
                        }
                    }
                });
            }
        });
        return d;
    }

    public void setItemAddedListener(ItemAddedListener itemAddedListener) {
        this.itemAddedListener = itemAddedListener;
    }

    public interface ItemAddedListener
    {
        void onItemAdded(ShoppingItem shoppingItem);
    }
}
