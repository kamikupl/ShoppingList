package pl.kamiku.shoppinglist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import pl.kamiku.shoppinglist.MainApplication;
import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.adapters.ShoppingListsAdapter;
import pl.kamiku.shoppinglist.data.ShoppingItem;
import pl.kamiku.shoppinglist.data.ShoppingList;
import pl.kamiku.shoppinglist.dialogs.NewListDialog;

public class MainActivity extends AppCompatActivity implements ShoppingListsAdapter.ShoppingListClickListener, NewListDialog.ListAddedListener {

    private static final String LIST_SCROLL_POSITION = "scrollPosition";
    private static final String LIST_SCROLL_OFFSET = "scrollOffset";
    RecyclerView recyclerView;
    private List<ShoppingList> shoppingLists;
    private SharedPreferences preferences;
    private LinearLayout emptyListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyListLayout = (LinearLayout) findViewById(R.id.empty_list_layout);
        preferences = getSharedPreferences(this.getClass().getName(),MODE_PRIVATE);
        if(!preferences.getBoolean("tutorialSeen", false))
        {
            ShoppingList tutorialList = new ShoppingList(getResources().getString(R.string.tutorial_list_name));
            MainApplication.getDbHelper().insertShoppingList(tutorialList);
            ShoppingItem addItem = new ShoppingItem();
            addItem.setName(getResources().getString(R.string.tutorial_add_item));
            addItem.setListId(tutorialList.getId());
            MainApplication.getDbHelper().insertShoppingItem(addItem);
            ShoppingItem buyItem = new ShoppingItem();
            buyItem.setName(getResources().getString(R.string.tutorial_buy_item));
            buyItem.setListId(tutorialList.getId());
            buyItem.setBought(true);
            MainApplication.getDbHelper().insertShoppingItem(buyItem);
            ShoppingItem deleteItem = new ShoppingItem();
            deleteItem.setName(getResources().getString(R.string.tutorial_delete_item));
            deleteItem.setListId(tutorialList.getId());
            MainApplication.getDbHelper().insertShoppingItem(deleteItem);
            preferences.edit().putBoolean("tutorialSeen",true).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shoppingLists = MainApplication.getDbHelper().getShoppingLists(false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if(recyclerView!=null) {
            recyclerView.setHasFixedSize(false);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ShoppingListsAdapter shoppingListsAdapter = new ShoppingListsAdapter(shoppingLists,this);
        recyclerView.setAdapter(shoppingListsAdapter);
        createTouchHelper();

        int position = preferences.getInt(LIST_SCROLL_POSITION,0);
        int offset = preferences.getInt(LIST_SCROLL_OFFSET,0);
        layoutManager.scrollToPositionWithOffset(position,offset);
        checkListContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_archived:
                Intent intent = new Intent(this, ArchiveActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void newList(View view) {
        NewListDialog newListDialog = new NewListDialog();
        newListDialog.setListAddedListener(this);
        newListDialog.show(getSupportFragmentManager(),"new_exp");
    }

    @Override
    public void onShowClick(int position) {
        Intent intent = new Intent(this, ShoppingListActivity.class);
        intent.putExtra(ShoppingListActivity.LIST_ID, shoppingLists.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onArchiveClick(int position) {
        ShoppingList shoppingList = shoppingLists.get(position);
        shoppingList.setArchived(true);
        shoppingList.update();
        shoppingLists.remove(position);
        recyclerView.getAdapter().notifyItemRemoved(position);
        checkListContent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveScrollPosition();
    }

    private void saveScrollPosition() {
        View firstChild = recyclerView.getChildAt(0);
        if(firstChild!=null) {
            int firstVisiblePosition = recyclerView.getChildAdapterPosition(firstChild);
            int offset = firstChild.getTop();
            preferences.edit()
                    .putInt(LIST_SCROLL_POSITION, firstVisiblePosition)
                    .putInt(LIST_SCROLL_OFFSET, offset)
                    .apply();
        }
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
                final ShoppingList shoppingList = shoppingLists.get(position);
                shoppingList.loadItems();
                final List<ShoppingItem> shoppingItems = shoppingList.getItems();
                shoppingLists.remove(shoppingList);
                recyclerView.getAdapter().notifyItemRemoved(position);
                MainApplication.getDbHelper().deleteShoppingList(shoppingList);
                for(ShoppingItem shoppingItem: shoppingItems)
                {
                    MainApplication.getDbHelper().deleteShoppingItem(shoppingItem);
                }
                checkListContent();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),getResources().getString(R.string.deleted_list) + " " + shoppingList.getName(),Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shoppingLists.add(position,shoppingList);
                        recyclerView.getAdapter().notifyItemInserted(position);
                        checkListContent();
                        Long newListId = MainApplication.getDbHelper().insertShoppingList(shoppingList);
                        for(ShoppingItem shoppingItem: shoppingItems)
                        {
                            shoppingItem.setListId(newListId);
                            MainApplication.getDbHelper().insertShoppingItem(shoppingItem);
                        }
                    }
                });
                snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onListAdded(ShoppingList shoppingList) {
        shoppingLists.add(0,shoppingList);
        recyclerView.getAdapter().notifyItemInserted(0);
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,null,0);
        checkListContent();
    }

    public void checkListContent()
    {
        if(shoppingLists.isEmpty())
        {
            emptyListLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            emptyListLayout.setVisibility(View.GONE);
        }
    }
}
