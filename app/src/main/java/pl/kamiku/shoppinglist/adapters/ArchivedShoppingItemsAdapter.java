package pl.kamiku.shoppinglist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.data.ShoppingItem;

public class ArchivedShoppingItemsAdapter  extends RecyclerView.Adapter<ArchivedShoppingItemsAdapter.ArchivedShoppingItemViewHolder>{

    List<ShoppingItem> shoppingItems;

    public ArchivedShoppingItemsAdapter(List<ShoppingItem> shoppingItems)
    {
        this.shoppingItems = shoppingItems;
    }

    @Override
    public ArchivedShoppingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_archived_shopping_item,parent,false);
        return new ArchivedShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArchivedShoppingItemViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItems.get(position);
        holder.name.setText(shoppingItem.getName());
        holder.bought.setChecked(shoppingItem.isBought());

    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public class ArchivedShoppingItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CheckBox bought;

        public ArchivedShoppingItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            bought = (CheckBox) itemView.findViewById(R.id.bought);
        }
    }

}
