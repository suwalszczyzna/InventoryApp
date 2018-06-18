package pl.com.suwala.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.com.suwala.inventoryapp.data.ProductContract.ProductEntry;
import pl.com.suwala.inventoryapp.data.ProductCursorAdapter;
import pl.com.suwala.inventoryapp.utils.InventoryUtils;

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
    private InventoryUtils utils = new InventoryUtils();

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

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(0);
            }
        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(1);
            }
        });

    }

    public void updateQuantity(int button){
        switch (button){
            case 0: addQuantity();
            break;
            case 1: subtractQuantity();
            break;
        }
    }

    private void subtractQuantity() {
        if(quantity>0){
            quantity--;
            quantityText.setText(utils.getQuantityFormat(String.format("%s", quantity)));
        }else{
            Toast.makeText(this, getString(R.string.error_subtract_quantity),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addQuantity() {
        quantity++;
        quantityText.setText(utils.getQuantityFormat(String.format("%s", quantity)));
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
            Log.d("ProductDetailActivity", "onLoadFinished: supplierPhone " + supplierPhone);
            String price = cursor.getString(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);



            productNameText.setText(productName);
            supplierNameText.setText(supplierName);
            priceText.setText(utils.getPriceFormat(price, utils.getCurrencySymbol()));
            quantityText.setText(utils.getQuantityFormat(String.format("%s", quantity)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        saveProduct();
        productNameText.setText("");
        supplierNameText.setText("");
        priceText.setText("");
        quantityText.setText("");
    }

    private void saveProduct() {
        ContentValues values = new ContentValues();
        String quantityString = String.format("%s", quantity);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);

        if (productUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {

                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(productUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
