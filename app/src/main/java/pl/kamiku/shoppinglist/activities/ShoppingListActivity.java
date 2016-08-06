package pl.kamiku.shoppinglist.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import pl.kamiku.shoppinglist.MainApplication;
import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.adapters.ShoppingItemsAdapter;
import pl.kamiku.shoppinglist.data.ShoppingItem;
import pl.kamiku.shoppinglist.data.ShoppingList;
import pl.kamiku.shoppinglist.db.DbHelper;
import pl.kamiku.shoppinglist.dialogs.NewItemDialog;

public class ShoppingListActivity extends AppCompatActivity implements ShoppingItemsAdapter.ShoppingItemClickListener, NewItemDialog.ItemAddedListener {

    public static final String LIST_ID = "listId";
    private static final String LIST_SCROLL_POSITION = "scrollPosition";
    private static final String LIST_SCROLL_OFFSET = "scrollOffset";
    private ShoppingList shoppingList;
    private RecyclerView recyclerView;
    private SharedPreferences preferences;
    private LinearLayout emptyListLayout;
    //used to reset list position
    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        emptyListLayout = (LinearLayout) findViewById(R.id.empty_list_layout);
        preferences = getSharedPreferences(this.getClass().getName(),MODE_PRIVATE);
        long id = getIntent().getLongExtra(LIST_ID, -1);
        if(id == -1)
        {
            finish();
            return;
        }
        shoppingList = MainApplication.getDbHelper().getShoppingList(id);
        if(shoppingList == null)
        {
            finish();
            return;
        }
        setTitle(shoppingList.getName());
        shoppingList.loadItems();


    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if(recyclerView!=null) {
            recyclerView.setHasFixedSize(false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        createTouchHelper();

        ShoppingItemsAdapter shoppingItemsAdapter = new ShoppingItemsAdapter(shoppingList.getItems(),this);
        recyclerView.setAdapter(shoppingItemsAdapter);

        int position = preferences.getInt(LIST_SCROLL_POSITION,0);
        int offset = preferences.getInt(LIST_SCROLL_OFFSET,0);

        layoutManager.scrollToPositionWithOffset(position,offset);
        checkListContent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveScrollPosition();
    }

    private void saveScrollPosition() {
        View firstChild = recyclerView.getChildAt(0);

        if(isBackPressed)
        {
            preferences.edit()
                    .putInt(LIST_SCROLL_POSITION, 0)
                    .putInt(LIST_SCROLL_OFFSET, 0)
                    .apply();
        }
        else if(firstChild!=null) {
            int firstVisiblePosition = recyclerView.getChildAdapterPosition(firstChild);
            int offset = firstChild.getTop();
            preferences.edit()
                    .putInt(LIST_SCROLL_POSITION, firstVisiblePosition)
                    .putInt(LIST_SCROLL_OFFSET, offset)
                    .apply();
        }
    }

    @Override
    public void onBackPressed() {
        //Reset scroll position while leaving archive
        isBackPressed = true;
        super.onBackPressed();
    }

    @Override
    public void onBoughtClick(int position) {
        ShoppingItem shoppingItem = shoppingList.getItems().get(position);
        shoppingItem.setBought(!shoppingItem.isBought());
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.updateShoppingItem(shoppingItem);
    }

    public void newItem(View view) {
        NewItemDialog newItemDialog = NewItemDialog.newInstance(shoppingList.getId());
        newItemDialog.setItemAddedListener(this);
        newItemDialog.show(getSupportFragmentManager(),"new_exp");
    }

    private void createTouchHelper()
    {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = 0;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ShoppingItem shoppingItem = shoppingList.getItems().get(position);
                shoppingList.getItems().remove(shoppingItem);
                recyclerView.getAdapter().notifyItemRemoved(position);
                checkListContent();
                MainApplication.getDbHelper().deleteShoppingItem(shoppingItem);
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),getResources().getString(R.string.deleted_item) + " " + shoppingItem.getName(),Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainApplication.getDbHelper().insertShoppingItem(shoppingItem);
                        shoppingList.getItems().add(position,shoppingItem);
                        recyclerView.getAdapter().notifyItemInserted(position);
                        checkListContent();
                    }
                });
                snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onItemAdded(ShoppingItem shoppingItem) {
        int position;
        List<ShoppingItem> shoppingItems = shoppingList.getItems();
        for(position = 0; position < shoppingItems.size(); position++)
        {
            if(shoppingItem.getName().compareToIgnoreCase(shoppingItems.get(position).getName()) < 0)
            {
                break;
            }
        }
        shoppingItems.add(position,shoppingItem);
        recyclerView.getAdapter().notifyItemInserted(position);
        checkListContent();
    }

    public void checkListContent()
    {
        if(shoppingList.getItems().isEmpty())
        {
            emptyListLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyListLayout.setVisibility(View.GONE);
        }
    }
}

