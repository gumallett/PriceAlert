package com.pricealert.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.pricealert.data.model.Product;
import com.pricealert.data.model.ProductImg;
import com.pricealert.data.model.ProductPriceHistory;
import com.pricealert.data.model.ProductTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RecentPricesDb extends SQLiteOpenHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RecentPricesDb.class);

    private static final String DB_NAME = "pricealert";
    private static final int VERSION = 1;

    private static final String PRODUCT_DETAILS_SQL = "select p.id as p_id, p.url as p_url, p.product_name as p_product_name, p.create_date as p_create_date, t.target_val as t_target_val, t.target_percent as t_target_pct, ph.price as ph_price, ph.update_date as ph_update_date, pimg.product_id as pimg_product_id, pimg.img_url as pimg_url, pimg.img as pimg_img " +
            "from products p join targets t on t.product_id=p.id left join (select * from price_history order by update_date desc) ph on p.id=ph.product_id left join product_img pimg on p.id=pimg.product_id ";

    private static final String ALL_PRODUCT_DETAILS_SQL = "select p.id as p_id, p.url as p_url, p.product_name as p_product_name, p.create_date as p_create_date, t.target_val as t_target_val, t.target_percent as t_target_pct, pimg.img_url as pimg_url, pimg.product_id as pimg_product_id, pimg.img as pimg_img " +
            "from products p join targets t on t.product_id=p.id left join product_img pimg on p.id=pimg.product_id ";

    private static final String PRODUCT_DETAILS_QUERY = PRODUCT_DETAILS_SQL + "where p.id=?;";

    private static final String PRODUCT_INSERT_SQL = "insert into products (url, product_name, create_date) values (?, ?, ?);";
    private static final String PRODUCT_TARGETS_INSERT_SQL = "insert into targets (product_id, target_val, target_percent) values (?, ?, ?);";
    private static final String PRODUCT_HISTORY_INSERT_SQL = "insert into price_history (product_id, price, update_date) values (?, ?, ?);";

    private static final String PRODUCT_IMAGE_INSERT_SQL = "insert into product_img (product_id, img_url, img) values (?, ?, ?);";

    private static final String DELETE_PRODUCT_SQL = "delete from products where id=?";

    public RecentPricesDb(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE PRODUCTS (ID INTEGER PRIMARY KEY, url TEXT NOT NULL, PRODUCT_NAME TEXT, CREATE_DATE DATETIME NOT NULL);");
        db.execSQL("CREATE TABLE TARGETS (PRODUCT_ID INTEGER NOT NULL, TARGET_VAL DECIMAL(8,2), TARGET_PERCENT INT, FOREIGN KEY(PRODUCT_ID) REFERENCES PRODUCTS(ID) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE PRICE_HISTORY (PRODUCT_ID INTEGER NOT NULL, PRICE DECIMAL(8,2) NOT NULL, UPDATE_DATE DATETIME NOT NULL, FOREIGN KEY(PRODUCT_ID) REFERENCES PRODUCTS(ID) ON DELETE CASCADE);");
        db.execSQL("CREATE TABLE PRODUCT_IMG (PRODUCT_ID INTEGER NOT NULL, IMG_URL TEXT, IMG BLOB, FOREIGN KEY(PRODUCT_ID) REFERENCES PRODUCTS(ID) ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PRODUCTS;");
        db.execSQL("DROP TABLE IF EXISTS TARGETS;");
        db.execSQL("DROP TABLE IF EXISTS PRICE_HISTORY;");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        if(!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public List<Product> selectProducts() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        LOG.info("Querying for products");

        List<Product> productList = new ArrayList<Product>();
        Cursor cursor = sqLiteDatabase.rawQuery(ALL_PRODUCT_DETAILS_SQL, null);

        while(cursor.moveToNext()) {
            Product product = createProduct(cursor);
            LOG.info("Adding product to results: {}", product);
            productList.add(product);
        }

        cursor.close();
        sqLiteDatabase.close();
        return productList;
    }

    public Product selectProductDetails(long id) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        LOG.info("Querying for product with id {}...", id);

        String sql = PRODUCT_DETAILS_QUERY;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, new String[] {String.valueOf(id)});

        Product product = null;
        if(cursor.moveToNext()) {
            product = createProduct(cursor);
            List<ProductPriceHistory> history = new ArrayList<ProductPriceHistory>();
            history.add(product.getMostRecentPrice());

            while(cursor.moveToNext()) {
                history.add(createProductPriceHistory(cursor));
            }

            product.setPriceHistory(history);
            LOG.info("Loaded product: {}", product);
        }

        cursor.close();
        sqLiteDatabase.close();
        return product;
    }

    public void saveProduct(Product product) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        LOG.info("Saving product {}", product);

        try {
            if(product.getId() == null) {
                doInsert(product);
            }
            else {
                doUpdate(product);
            }

            sqLiteDatabase.setTransactionSuccessful();
        }
        catch(Exception e) {
            LOG.error("Error saving product: ", e);
        }

        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    public void saveProductImage(ProductImg img) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        LOG.info("Saving product image {}", img);

        try {
            sqLiteDatabase.execSQL("delete from product_img where product_id=?", new Object[] {img.getProduct_id()});

            SQLiteStatement statement = sqLiteDatabase.compileStatement(PRODUCT_IMAGE_INSERT_SQL);
            statement.bindLong(1, img.getProduct_id());
            statement.bindString(2, img.getImgUrl());
            statement.bindBlob(3, img.getImg());
            statement.executeInsert();
            sqLiteDatabase.setTransactionSuccessful();
        }
        catch(Exception e) {
            LOG.error("Error saving product image: ", e);
        }

        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    public void deleteProduct(Product product) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        LOG.info("Deleting product {}", product);

        try {
            sqLiteDatabase.execSQL(DELETE_PRODUCT_SQL, new Object[]{product.getId()});
            sqLiteDatabase.setTransactionSuccessful();
        }
        catch(Exception e) {
            LOG.error("Error deleting product: ", e);
        }

        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    public void newHistory(ProductPriceHistory productPriceHistory) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        sqLiteDatabase.beginTransaction();
        LOG.info("Running query {}", PRODUCT_HISTORY_INSERT_SQL);

        try {
            sqLiteDatabase.execSQL(PRODUCT_HISTORY_INSERT_SQL, new Object[]{productPriceHistory.getProductId(), productPriceHistory.getPrice(), System.currentTimeMillis()/1000});
            sqLiteDatabase.setTransactionSuccessful();
        }
        catch(Exception e) {
            LOG.error("Error saving price history: ", e);
        }

        LOG.info("Finished running query {}", PRODUCT_HISTORY_INSERT_SQL);

        sqLiteDatabase.endTransaction();
        sqLiteDatabase.close();
    }

    private static Product createProduct(Cursor cursor) {
        Product product = new Product();
        product.setId(cursor.getLong(cursor.getColumnIndex("p_id")));
        product.setUrl(cursor.getString(cursor.getColumnIndex("p_url")));
        product.setName(cursor.getString(cursor.getColumnIndex("p_product_name")));
        product.setCreateDate(new Date(cursor.getLong(cursor.getColumnIndex("p_create_date"))*1000));

        if(cursor.getColumnIndex("ph_price") != -1) {
            product.setMostRecentPrice(createProductPriceHistory(cursor));
        }

        if(cursor.getColumnIndex("pimg_product_id") != -1) {
            product.setProductImg(createProductImg(cursor));
        }

        ProductTarget targets = new ProductTarget();
        targets.setTargetValue(cursor.getDouble(cursor.getColumnIndex("t_target_val")));
        targets.setTargetPercent(cursor.getInt(cursor.getColumnIndex("t_target_pct")));
        product.setTargets(targets);

        return product;
    }

    private static ProductPriceHistory createProductPriceHistory(Cursor cursor) {
        ProductPriceHistory recentHistory = new ProductPriceHistory();
        recentHistory.setProductId(cursor.getLong(cursor.getColumnIndex("p_id")));
        recentHistory.setDate(new Date(cursor.getLong(cursor.getColumnIndex("ph_update_date")) * 1000));
        recentHistory.setPrice(cursor.getDouble(cursor.getColumnIndex("ph_price")));

        return recentHistory;
    }

    private static ProductImg createProductImg(Cursor cursor) {
        ProductImg img = new ProductImg();
        img.setProduct_id(cursor.getLong(cursor.getColumnIndex("pimg_product_id")));
        img.setImgUrl(cursor.getString(cursor.getColumnIndex("pimg_url")));
        img.setImg(cursor.getBlob(cursor.getColumnIndex("pimg_img")));

        return img;
    }

    private Long getLastInsertId() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select last_insert_rowid();", null);
        cursor.moveToNext();
        return cursor.getLong(0);
    }

    private Long doInsert(Product product) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL(PRODUCT_INSERT_SQL, new Object[]{product.getUrl(), product.getName(), System.currentTimeMillis()/1000});
        Long productId = getLastInsertId();
        product.setId(productId);

        ProductTarget targets = product.getTargets();
        sqLiteDatabase.execSQL(PRODUCT_TARGETS_INSERT_SQL, new Object[]{productId, targets.getTargetValue(), targets.getTargetPercent()});
        return productId;
    }

    private void doUpdate(Product product) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues productsContent = new ContentValues();
        productsContent.put("url", product.getUrl());
        productsContent.put("product_name", product.getName());
        sqLiteDatabase.update("products", productsContent, "id=?", new String[]{product.getId().toString()});

        ProductTarget targets = product.getTargets();
        ContentValues targetValues = new ContentValues();
        targetValues.put("target_val", targets.getTargetValue());
        targetValues.put("target_percent", targets.getTargetPercent());
        sqLiteDatabase.update("targets", targetValues, "product_id=?", new String[]{product.getId().toString()});
    }
}
