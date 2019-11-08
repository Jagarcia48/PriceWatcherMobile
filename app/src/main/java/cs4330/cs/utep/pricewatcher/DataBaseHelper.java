package cs4330.cs.utep.pricewatcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "priceWatcherDB";
    private static final String Product_Table = "products";
    
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String KEY_PRICE = "price";
    private static final String KEY_CHANGE = "change";
    private static final String KEY_DATE = "date";
    private static final String KEY_INITIAL = "initial";

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String table = "CREATE TABLE " + Product_Table + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_URL + " TEXT, "
                + KEY_PRICE + " REAL, "
                + KEY_CHANGE + " REAL, "
                + KEY_DATE + " TEXT, "
                + KEY_INITIAL + " REAL" + ")";
        database.execSQL(table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + Product_Table);
        onCreate(database);
    }

    public void addItem(Product product) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getItem());
        values.put(KEY_URL, product.getUrl());
        values.put(KEY_PRICE, product.getInitialPrice());
        values.put(KEY_CHANGE, product.getCurrentPrice());
        values.put(KEY_DATE, product.getAddedDate());
        values.put(KEY_INITIAL, product.getInitialPrice());

        long id = database.insert(Product_Table, null, values);
        product.setId((int)id);
        database.close();
    }

    public List<Product> allProducts() {
        List<Product> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Product_Table;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()) {
            do{
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String url = cursor.getString(2);
                float price = cursor.getFloat(3);
                float percentageChange = cursor.getFloat(4);
                String addedDate = cursor.getString(5);
                float initialPrice = cursor.getFloat(6);
                Product product = new Product(name, url, price, percentageChange, addedDate, initialPrice, id);
                list.add(product);
            }
            while(cursor.moveToNext());
        }
        return list;
    }

    public void delete(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(Product_Table, KEY_ID + " = ?", new String[] { Integer.toString(id) } );
        database.close();
    }

    public void update(Product product) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_NAME, product.getItem());
        values.put(KEY_URL, product.getUrl());
        values.put(KEY_PRICE, product.getInitialPrice());
        values.put(KEY_CHANGE, product.getCurrentPrice());
        values.put(KEY_DATE, product.getAddedDate());
        values.put(KEY_INITIAL, product.getInitialPrice());

        database.update(Product_Table, values, KEY_ID + " = ?", new String[]{String.valueOf(product.getId())});
        database.close();
    }
}
