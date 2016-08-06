package pl.kamiku.shoppinglist.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.kamiku.shoppinglist.MainApplication;

public class ShoppingList {

    public static final String TABLE = "list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_ARCHIVED = "archived";

    public static final String COLUMNS[] = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_CREATED, COLUMN_ARCHIVED};
    public static final String CREATE_TABLE = "create table " + TABLE +
            "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text, " +
            COLUMN_CREATED + " integer, " +
            COLUMN_ARCHIVED + " text);";

    private Long id;
    private String name;
    private Date created;
    private boolean archived;

    private List<ShoppingItem> items;

    public ShoppingList(String name, Date created, boolean archived) {
        this.name = name;
        this.created = created;
        this.archived = archived;
    }

    public ShoppingList(String name) {
        this.name = name;
        this.created = Calendar.getInstance().getTime();
        this.archived = false;
    }

    public ShoppingList() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingItem> items) {
        this.items = items;
    }

    public int getItemsCount()
    {
        return MainApplication.getDbHelper().getItemsCount(id);
    }

    public int getBoughtItemsCount()
    {
        return MainApplication.getDbHelper().getBoughtItemsCount(id);
    }

    public void update() {
        MainApplication.getDbHelper().updateShoppingList(this);
    }

    public void loadItems() {
        items = MainApplication.getDbHelper().getShoppingItems(id);
    }
}
