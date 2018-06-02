package pl.com.suwala.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import pl.com.suwala.inventoryapp.data.ProductDbHelper;
import pl.com.suwala.inventoryapp.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity {
    public static final String TAG = CatalogActivity.class.getSimpleName();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void insertData() {
        ProductDbHelper dbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Book");
        values.put(ProductEntry.COLUMN_PRICE, 14);
        values.put(ProductEntry.COLUMN_QUANTITY, 2);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Max");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "555-555-555");
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);
        Log.d(TAG, "insertData(): new row added");
    }

    private Cursor queryData() {
        ProductDbHelper dbHelper = new ProductDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(ProductEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        return c;
    }

    private void displayDatabaseInfo() {
        Cursor c = queryData();
        TextView displayView = findViewById(R.id.text_view);
        try {

            displayView.setText("The \"" + ProductEntry.TABLE_NAME + "\" table contains " + c.getCount() + " rows.\n\n");

            int idColumnIndex = c.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = c.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = c.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int qtyColumnIndex = c.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supNameColumnIndex = c.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supPhoneColumnIndex = c.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);

            while (c.moveToNext()) {
                int currendId = c.getInt(idColumnIndex);
                String currendName = c.getString(nameColumnIndex);
                int currendPrice = c.getInt(priceColumnIndex);
                int currendQty = c.getInt(qtyColumnIndex);
                String currendSupName = c.getString(supNameColumnIndex);
                String currendSupPhone = c.getString(supPhoneColumnIndex);

                displayView.append(
                        "\n" + currendId + " - "
                                + currendName + " - "
                                + currendPrice + " - "
                                + currendQty + " - "
                                + currendSupName + " - "
                                + currendSupPhone);
                Log.d(TAG, "displayDatabaseInfo(): displayView.append called");
            }

        } finally {
            c.close();
            Log.d(TAG, "displayDatabaseInfo(): cursor closed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dummy_data:
                insertData();
                displayDatabaseInfo();
                return true;
            case R.id.clear_database:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
