package pl.kamiku.shoppinglist.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.data.ShoppingList;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ShoppingListViewHolder>{
    List<ShoppingList> shoppingLists;
    ShoppingListClickListener shoppingListClickListener;

    public ShoppingListsAdapter(List<ShoppingList> shoppingLists, ShoppingListClickListener shoppingListClickListener) {
        this.shoppingLists = shoppingLists;
        this.shoppingListClickListener = shoppingListClickListener;
    }

    @Override
    public ShoppingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shopping_list,parent,false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ShoppingListViewHolder holder, int position) {
        ShoppingList shoppingList = shoppingLists.get(position);
        holder.name.setText(shoppingList.getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
        holder.date.setText(simpleDateFormat.format(shoppingList.getCreated()));
        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingListClickListener.onShowClick(holder.getAdapterPosition());
            }
        });
        holder.archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoppingListClickListener.onArchiveClick(holder.getAdapterPosition());
            }
        });
        int boughtItems = shoppingList.getBoughtItemsCount();
        int allItems = shoppingList.getItemsCount();
        if(allItems == 0)
        {
            holder.items.setText(R.string.no_items);
            holder.progressBar.setVisibility(View.GONE);
        }
        else if(allItems == boughtItems) {
            holder.items.setText(R.string.all_items_bought);
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(100);
        }
        else
        {
            holder.items.setText(holder.items.getContext().getResources().getString(R.string.items_bought,boughtItems,allItems));
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(100*boughtItems/allItems);
        }
    }

    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView name;
        TextView items;
        ProgressBar progressBar;
        Button archive;
        Button show;

        public ShoppingListViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            name = (TextView) itemView.findViewById(R.id.name);
            items = (TextView) itemView.findViewById(R.id.items);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            archive = (Button) itemView.findViewById(R.id.archive);
            show = (Button) itemView.findViewById(R.id.show);
        }
    }

    public interface ShoppingListClickListener
    {
        void onShowClick(int position);
        void onArchiveClick(int position);
    }
}
