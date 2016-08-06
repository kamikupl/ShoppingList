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

public class ShoppingItemsAdapter extends RecyclerView.Adapter<ShoppingItemsAdapter.ShoppingItemViewHolder>{

    List<ShoppingItem> shoppingItems;
    ShoppingItemClickListener shoppingItemClickListener;

    public ShoppingItemsAdapter(List<ShoppingItem> shoppingItems,ShoppingItemClickListener shoppingItemClickListener)
    {
        this.shoppingItems = shoppingItems;
        this.shoppingItemClickListener = shoppingItemClickListener;
    }


    @Override
    public ShoppingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shopping_item,parent,false);
        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShoppingItemViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItems.get(position);
        holder.name.setText(shoppingItem.getName());
        holder.bought.setChecked(shoppingItem.isBought());
        holder.bought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shoppingItemClickListener != null) {
                    shoppingItemClickListener.onBoughtClick(holder.getAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CheckBox bought;


        public ShoppingItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            bought = (CheckBox) itemView.findViewById(R.id.bought);
        }
    }

    public interface ShoppingItemClickListener
    {
        void onBoughtClick(int position);
    }
}
