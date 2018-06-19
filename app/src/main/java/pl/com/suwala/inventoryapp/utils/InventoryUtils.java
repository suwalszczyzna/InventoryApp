package pl.com.suwala.inventoryapp.utils;

import android.icu.util.Currency;

import java.util.Locale;

public class InventoryUtils {

    public String getCurrencySymbol() {
        Currency currency = null;
        String symbol = "$";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            currency = Currency.getInstance(Locale.getDefault());
            symbol = currency.getCurrencyCode();
        }
        return symbol;
    }

    public String getPriceFormat(String price, String symbol) {
        return String.format("%s %s", price, symbol);
    }

    public String getQuantityFormat(String quantity) {
        return String.format("%s pcs", quantity);
    }
}
