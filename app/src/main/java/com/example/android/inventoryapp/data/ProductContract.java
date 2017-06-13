package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created by Adel on 6/9/2017.
 */

public final class ProductContract {

    private ProductContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_TITLE ="title";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SOLD = "sold";
        public final static String COLUMN_PRODUCT_IMAGE = "image";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplier_email";

        public static boolean isValidInfo(String info) {
            if (!TextUtils.isEmpty(info)) {
                return true;
            }
            return false;
        }

        public static boolean isImageResourceProvided(String imgResource) {
            if (!TextUtils.isEmpty(imgResource)) {
                return true;
            }
            return false;
        }
    }
}