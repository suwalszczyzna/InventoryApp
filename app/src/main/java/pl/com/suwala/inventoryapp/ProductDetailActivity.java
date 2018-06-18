package pl.com.suwala.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import pl.com.suwala.inventoryapp.data.ProductContract.ProductEntry;
import pl.com.suwala.inventoryapp.data.ProductCursorAdapter;

public class ProductDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter cursorAdapter;
    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri productUri;
    private TextView productNameText;
    private TextView supplierNameText;
    private TextView priceText;
    private TextView quantityText;
    private TextView supplierPhoneText;
    private Button plusButton, minusButton;

    private int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_layout);
        Intent intent = getIntent();
        productUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        productNameText = findViewById(R.id.product_name_detail);
        supplierNameText = findViewById(R.id.supplier_name_detail);
        priceText = findViewById(R.id.price_detail);
        quantityText = findViewById(R.id.quantity_detail);
        plusButton = findViewById(R.id.plus_button_detail);
        minusButton = findViewById(R.id.minus_button_detail);

        updateViews();

    }

    private void updateViews(){
        String [] projection = {"*"};
        Cursor cursor = getContentResolver().query(productUri,
                projection,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);

            String productName = cursor.getString(productNameColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);

            productNameText.setText(productName);
            supplierNameText.setText(supplierName);
            supplierPhoneText.setText(supplierPhone);
            priceText.setText(price);
            quantityText.setText(String.format("%s", quantity));
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_PHONE,
                ProductEntry.COLUMN_SUPPLIER_NAME};

        return new CursorLoader(this,   // Parent activity context
                ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
