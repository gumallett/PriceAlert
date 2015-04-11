package com.pricealert.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductPriceHistory;
import com.pricealert.data.model.ProductTarget;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RecentPricesDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "pricealert";
    private static final int VERSION = 1;

    private static final String PRODUCT_DETAILS_SQL = "select p.id as p_id, p.url as p_url, p.product_name as p_product_name, p.create_date as p_create_date, ph.price as ph_price, ph.update_date as ph_update_date, t.target_val as t_target_val, t.target_percent as t_target_pct " +
            "from products p join price_history ph on ph.product_id=p.id join targets t on t.product_id=p.id " +
            "order by p_create_date desc, ph_update_date desc;";

    private static final String PRODUCT_INSERT_SQL = "insert into products (url, product_name, create_date) values (?, ?, datetime('now'));";
    private static final String PRODUCT_TARGETS_INSERT_SQL = "insert into targets (product_id, target_val, target_percent) values (?, ?, ?);";
    private static final String PRODUCT_HISTORY_INSERT_SQL = "insert into price_history (product_id, price, update_date) values (?, ?, datetime('now'));";

    public RecentPricesDb(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PRODUCTS (ID INTEGER PRIMARY KEY, url TEXT NOT NULL, PRODUCT_NAME TEXT, CREATE_DATE DATETIME NOT NULL);");
        db.execSQL("CREATE TABLE TARGETS (PRODUCT_ID INTEGER NOT NULL, TARGET_VAL DECIMAL(8,2), TARGET_PERCENT INT, FOREIGN KEY(PRODUCT_ID) REFERENCES PRODUCTS(ID));");
        db.execSQL("CREATE TABLE PRICE_HISTORY (PRODUCT_ID INTEGER NOT NULL, PRICE DECIMAL(8,2) NOT NULL, UPDATE_DATE DATETIME NOT NULL, FOREIGN KEY(PRODUCT_ID) REFERENCES PRODUCTS(ID));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PRODUCTS;");
        db.execSQL("DROP TABLE IF EXISTS TARGETS;");
        db.execSQL("DROP TABLE IF EXISTS PRICE_HISTORY;");
        onCreate(db);
    }

    public List<Product> selectProductDetails() {
        List<Product> results = new ArrayList<Product>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Log.d(RecentPricesDb.class.getSimpleName(), "Querying for recent quotes...");

        String sql = PRODUCT_DETAILS_SQL;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        while(cursor.moveToNext()) {
            Product product = new Product();
            product.setId(cursor.getLong(cursor.getColumnIndex("p_id")));
            product.setUrl(cursor.getString(cursor.getColumnIndex("p_url")));
            product.setName(cursor.getString(cursor.getColumnIndex("p_product_name")));
            product.setCreateDate(new Date(cursor.getLong(cursor.getColumnIndex("p_create_date"))));

            Log.d(RecentPricesDb.class.getSimpleName(), "Adding product " + product + " to query results.");

            results.add(product);
        }

        cursor.close();
        sqLiteDatabase.close();
        return results;
    }

    public long addProduct(Product product) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(PRODUCT_INSERT_SQL, new String[]{product.getUrl(), product.getName()});
        Long productId = getLastInsertId();
        product.setId(productId);

        ProductTarget targets = product.getTargets();
        sqLiteDatabase.execSQL(PRODUCT_TARGETS_INSERT_SQL, new Object[]{productId, targets.getTargetValue(), targets.getTargetPercent()});

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();

        return productId;
    }

    public void newHistory(ProductPriceHistory productPriceHistory) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL(PRODUCT_HISTORY_INSERT_SQL, new Object[]{productPriceHistory.getProductId(), productPriceHistory.getPrice()});

        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    private Long getLastInsertId() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select last_insert_rowid();", null);
        cursor.moveToNext();
        return cursor.getLong(0);
    }
}
