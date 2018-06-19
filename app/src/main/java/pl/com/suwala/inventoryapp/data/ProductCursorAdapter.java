package pl.com.suwala.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Currency;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import pl.com.suwala.inventoryapp.data.ProductContract.ProductEntry;

import java.util.Locale;

import pl.com.suwala.inventoryapp.R;
import pl.com.suwala.inventoryapp.utils.InventoryUtils;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView productNameTextView = view.findViewById(R.id.product_name_detail);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        int productNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_QUANTITY);

        String productName = cursor.getString(productNameColumnIndex);
        double price = cursor.getDouble(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);

        InventoryUtils utils = new InventoryUtils();

        productNameTextView.setText(productName);
        priceTextView.setText(utils.getPriceFormat(String.format("%s", price), utils.getCurrencySymbol()));
        quantityTextView.setText(utils.getQuantityFormat(quantity));

        final String[] selectionArgs = {productName};
        final String idString = cursor.getString(cursor.getColumnIndex(ProductEntry._ID));
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] projection = {ProductEntry.COLUMN_QUANTITY};

                Cursor cursor = context.getContentResolver().query(
                        Uri.withAppendedPath(ProductEntry.CONTENT_URI, idString),
                        projection,
                        ProductEntry.COLUMN_PRODUCT_NAME + "=?",
                        selectionArgs,
                        null);
                cursor.moveToFirst();
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY));
                if (quantity >= 1) {
                    ContentValues values1 = new ContentValues();
                    values1.put(ProductEntry.COLUMN_QUANTITY, quantity - 1);
                    context.getContentResolver().update(Uri.withAppendedPath(ProductEntry.CONTENT_URI, idString),
                            values1,
                            ProductEntry.COLUMN_PRODUCT_NAME + "=?",
                            selectionArgs);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.toast_message_cant_sell_more_nothings_left), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
