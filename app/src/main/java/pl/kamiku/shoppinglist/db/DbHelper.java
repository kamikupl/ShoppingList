package pl.kamiku.shoppinglist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.kamiku.shoppinglist.data.ShoppingItem;
import pl.kamiku.shoppinglist.data.ShoppingList;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shoppinglist.db";
    private static final int DATABASE_VERSION = 2;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ShoppingList.CREATE_TABLE);
        sqLiteDatabase.execSQL(ShoppingItem.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists "+ShoppingList.TABLE);
        sqLiteDatabase.execSQL("drop table if exists "+ShoppingItem.TABLE);
        onCreate(sqLiteDatabase);
    }

    public List<ShoppingList> getShoppingLists(boolean archived)
    {
        List<ShoppingList> shoppingLists = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ShoppingList.TABLE,ShoppingList.COLUMNS,ShoppingList.COLUMN_ARCHIVED +"=?",new String[]{archived+""},null,null,ShoppingList.COLUMN_CREATED + " desc");
        if(c.moveToFirst())
        {
            do {
                ShoppingList shoppingList = new ShoppingList();
                shoppingList.setId(Long.parseLong(c.getString(0)));
                shoppingList.setName(c.getString(1));
                shoppingList.setCreated(new Date(Long.parseLong(c.getString(2))));
                shoppingList.setArchived(Boolean.parseBoolean(c.getString(3)));
                shoppingLists.add(shoppingList);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return shoppingLists;
    }

    public List<ShoppingItem> getShoppingItems(long listId)
    {
        List<ShoppingItem> shoppingItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ShoppingItem.TABLE,ShoppingItem.COLUMNS,ShoppingItem.COLUMN_LIST_ID +"=?",new String[]{listId+""},null,null,ShoppingItem.COLUMN_NAME + " collate nocase;");
        if(c.moveToFirst())
        {
            do {
                ShoppingItem shoppingItem = new ShoppingItem();
                shoppingItem.setId(Long.parseLong(c.getString(0)));
                shoppingItem.setName(c.getString(1));
                shoppingItem.setBought(Boolean.parseBoolean(c.getString(2)));
                shoppingItem.setListId(Long.parseLong(c.getString(3)));
                shoppingItems.add(shoppingItem);
            }while (c.moveToNext());
        }
        c.close();
        db.close();
        return shoppingItems;
    }

    public Long insertShoppingList(ShoppingList shoppingList)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        if(shoppingList.getId()!=null)
        {
            cv.put(ShoppingList.COLUMN_ID, shoppingList.getId());
        }
        cv.put(ShoppingList.COLUMN_NAME, shoppingList.getName());
        cv.put(ShoppingList.COLUMN_CREATED, shoppingList.getCreated().getTime());
        cv.put(ShoppingList.COLUMN_ARCHIVED, shoppingList.isArchived()+"");

        Long id = db.insert(ShoppingList.TABLE, null, cv);
        db.close();
        shoppingList.setId(id);
        return id;
    }

    public void deleteShoppingList(ShoppingList shoppingList)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ShoppingList.TABLE,ShoppingList.COLUMN_ID + "=?",new String[]{shoppingList.getId()+""});
        db.close();
    }

    public void deleteShoppingItem(ShoppingItem shoppingItem)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ShoppingItem.TABLE,ShoppingItem.COLUMN_ID + "=?",new String[]{shoppingItem.getId()+""});
        db.close();
    }

    public ShoppingList getShoppingList(Long id)
    {
        ShoppingList shoppingList = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ShoppingList.TABLE,ShoppingList.COLUMNS,ShoppingList.COLUMN_ID +"=?",new String[]{id+""},null,null,null);
        if(c.moveToFirst())
        {
            shoppingList = new ShoppingList();
            shoppingList.setId(Long.parseLong(c.getString(0)));
            shoppingList.setName(c.getString(1));
            shoppingList.setCreated(new Date(Long.parseLong(c.getString(2))));
            shoppingList.setArchived(Boolean.parseBoolean(c.getString(3)));
        }
        c.close();
        db.close();
        return shoppingList;
    }

    public Long insertShoppingItem(ShoppingItem shoppingItem)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        if(shoppingItem.getId()!=null)
        {
            cv.put(ShoppingList.COLUMN_ID, shoppingItem.getId());
        }
        cv.put(ShoppingItem.COLUMN_NAME, shoppingItem.getName());
        cv.put(ShoppingItem.COLUMN_BOUGHT, shoppingItem.isBought() + "");
        cv.put(ShoppingItem.COLUMN_LIST_ID, shoppingItem.getListId());

        Long id = db.insert(ShoppingItem.TABLE, null, cv);
        db.close();
        shoppingItem.setId(id);
        return id;
    }

    public void updateShoppingItem(ShoppingItem shoppingItem) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ShoppingItem.COLUMN_NAME, shoppingItem.getName());
        cv.put(ShoppingItem.COLUMN_BOUGHT, shoppingItem.isBought() + "");
        cv.put(ShoppingItem.COLUMN_LIST_ID, shoppingItem.getListId());

        db.update(ShoppingItem.TABLE, cv, ShoppingItem.COLUMN_ID + "=" + shoppingItem.getId(), null);
        db.close();
    }

    public int getItemsCount(Long shoppingListId)
    {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + ShoppingItem.TABLE +
                " WHERE " + ShoppingItem.COLUMN_LIST_ID + "=?;",
                new String[]{shoppingListId+""});
        if(c.moveToFirst())
        {
            count = Integer.parseInt(c.getString(0));
        }
        c.close();
        return count;
    }

    public int getBoughtItemsCount(Long shoppingListId)
    {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + ShoppingItem.TABLE +
                        " WHERE " + ShoppingItem.COLUMN_LIST_ID + "=? AND " + ShoppingItem.COLUMN_BOUGHT + "=?;",
                new String[]{shoppingListId+"","true"});
        if(c.moveToFirst())
        {
            count = Integer.parseInt(c.getString(0));
        }
        c.close();
        return count;
    }

    public void updateShoppingList(ShoppingList shoppingList) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ShoppingList.COLUMN_NAME, shoppingList.getName());
        cv.put(ShoppingList.COLUMN_ARCHIVED, shoppingList.isArchived() + "");
        cv.put(ShoppingList.COLUMN_CREATED, shoppingList.getCreated().getTime()+"");

        db.update(ShoppingList.TABLE, cv, ShoppingList.COLUMN_ID + "=" + shoppingList.getId(), null);
        db.close();
    }
}
