package pl.com.suwala.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.com.suwala.inventoryapp.data.ProductContract.ProductEntry;
import pl.com.suwala.inventoryapp.data.ProductCursorAdapter;
import pl.com.suwala.inventoryapp.databinding.ProductDetailsLayoutBinding;
import pl.com.suwala.inventoryapp.utils.InventoryUtils;

public class ProductDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    public static final String TAG = "ProductDetailActivity";
    private Uri productUri;
    private InventoryUtils utils = new InventoryUtils();
    private boolean deleteProductCalled;
    private String supplierPhoneTextString;
    private int quantity = 0;
    private ProductDetailsLayoutBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.product_details_layout);
        Intent intent = getIntent();
        productUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        deleteProductCalled = false;

        bind.plusButtonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(0);
            }
        });
        bind.minusButtonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(1);
            }
        });

        bind.editButtonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, EditorActivity.class);
                intent.setData(productUri);
                startActivity(intent);
            }
        });

        bind.deleteButtonDetail.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: deleteButton CLICKED!!!!!!!!!");
                deleteProduct();
            }
        });

        bind.phoneCallOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (supplierPhoneTextString.isEmpty()) {
                    Toast.makeText(ProductDetailActivity.this, getString(R.string.no_supplier_phone), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supplierPhoneTextString, null));
                    startActivity(intent);
                }
            }
        });


    }

    public void updateQuantity(int button) {
        switch (button) {
            case 0:
                addQuantity();
                break;
            case 1:
                subtractQuantity();
                break;
        }
    }

    private void subtractQuantity() {
        if (quantity > 0) {
            quantity--;
            bind.quantityDetail.setText(utils.getQuantityFormat(String.format("%s", quantity)));
            saveProduct();
        } else {
            Toast.makeText(this, getString(R.string.error_subtract_quantity),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addQuantity() {
        quantity++;
        bind.quantityDetail.setText(utils.getQuantityFormat(String.format("%s", quantity)));
        saveProduct();
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
        String[] projection = {"*"};
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
            supplierPhoneTextString = supplierPhone;
            bind.productNameDetail.setText(productName);
            bind.supplierNameDetail.setText(supplierName);
            bind.priceDetail.setText(utils.getPriceFormat(price, utils.getCurrencySymbol()));
            bind.quantityDetail.setText(utils.getQuantityFormat(String.format("%s", quantity)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bind.productNameDetail.setText("");
        bind.supplierNameDetail.setText("");
        bind.priceDetail.setText("");
        bind.quantityDetail.setText("");
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
        } else if (!deleteProductCalled) {
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

    public void deleteProduct() {
        Log.d(TAG, "onClick: deleteProduct() called");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick: positive button clicked");
                deleteProductCalled = true;
                deleteProductAction();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteProductAction() {
        if (productUri != null) {
            int rowsDeleted = getContentResolver().delete(productUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}
