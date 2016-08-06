package pl.kamiku.shoppinglist.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import pl.kamiku.shoppinglist.MainApplication;
import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.adapters.ArchivedShoppingItemsAdapter;
import pl.kamiku.shoppinglist.data.ShoppingList;

public class ArchivedShoppingListActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_archived_shopping_list);
        emptyListLayout = (LinearLayout) findViewById(R.id.empty_list_layout);
        preferences = getSharedPreferences(getClass().getName(),MODE_PRIVATE);
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
        setTitle(shoppingList.getName() + " (archived)");

    }

    @Override
    protected void onResume() {
        super.onResume();
        shoppingList.loadItems();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if(recyclerView!=null) {
            recyclerView.setHasFixedSize(false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArchivedShoppingItemsAdapter archivedShoppingItemsAdapter = new ArchivedShoppingItemsAdapter(shoppingList.getItems());
        recyclerView.setAdapter(archivedShoppingItemsAdapter);

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
