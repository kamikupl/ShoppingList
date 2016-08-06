package pl.kamiku.shoppinglist;

import android.app.Application;

import pl.kamiku.shoppinglist.db.DbHelper;

public class MainApplication extends Application {

    private DbHelper dbHelper;
    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new DbHelper(this);
    }

    public static DbHelper getDbHelper()
    {
        return instance.dbHelper;
    }
}
