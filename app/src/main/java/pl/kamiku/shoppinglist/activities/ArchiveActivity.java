package pl.kamiku.shoppinglist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import pl.kamiku.shoppinglist.MainApplication;
import pl.kamiku.shoppinglist.R;
import pl.kamiku.shoppinglist.adapters.ArchivedShoppingListsAdapter;
import pl.kamiku.shoppinglist.data.ShoppingList;

public class ArchiveActivity extends AppCompatActivity implements ArchivedShoppingListsAdapter.ArchivedShoppingListClickListener {

    private static final String LIST_SCROLL_POSITION = "scrollPosition";
    private static final String LIST_SCROLL_OFFSET = "scrollOffset";
    private List<ShoppingList> shoppingLists;
    private RecyclerView recyclerView;
    private SharedPreferences preferences;
    private LinearLayout emptyListLayout;
    //used to reset list position
    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        emptyListLayout = (LinearLayout) findViewById(R.id.empty_list_layout);
        preferences = getSharedPreferences(this.getClass().getName(),MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shoppingLists = MainApplication.getDbHelper().getShoppingLists(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if(recyclerView!=null) {
            recyclerView.setHasFixedSize(false);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArchivedShoppingListsAdapter shoppingListsAdapter = new ArchivedShoppingListsAdapter(shoppingLists,this);
        recyclerView.setAdapter(shoppingListsAdapter);

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
    public void onShowClick(int position) {
        Intent intent = new Intent(this, ArchivedShoppingListActivity.class);
        intent.putExtra(ArchivedShoppingListActivity.LIST_ID, shoppingLists.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Reset scroll position while leaving archive
        isBackPressed = true;
        super.onBackPressed();
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
