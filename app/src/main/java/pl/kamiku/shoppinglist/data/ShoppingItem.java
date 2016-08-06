package pl.kamiku.shoppinglist.data;

public class ShoppingItem {

    public static final String TABLE = "item";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_BOUGHT = "bought";
    public static final String COLUMN_LIST_ID = "listid";

    public static final String COLUMNS[] = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_BOUGHT, COLUMN_LIST_ID};
    public static final String CREATE_TABLE = "create table " + TABLE +
            "(" + COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text, " +
            COLUMN_BOUGHT + " text, " +
            COLUMN_LIST_ID + " integer);";

    private Long id;
    private String name;
    private boolean bought;
    private Long listId;

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

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }
}
