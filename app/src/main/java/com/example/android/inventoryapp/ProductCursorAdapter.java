package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Adel on 6/9/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {

        final Cursor myCursor = cursor;

        TextView titleTextView = (TextView) view.findViewById(R.id.prdctTitle);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView prdctImgImageView = (ImageView) view.findViewById(R.id.imgProduct);
        ImageView saleImageView = (ImageView) view.findViewById(R.id.imgSale);

        int titleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int prdctImgColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

        String productTitle = cursor.getString(titleColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);
        String productImage = cursor.getString(prdctImgColumnIndex);

        titleTextView.setText(productTitle);

        if (productPrice == 0){
            priceTextView.setText(context.getString(R.string.free));
        }else {
            priceTextView.setText(String.format("%.2f$", productPrice));
        }

        if (productQuantity == 0){
            quantityTextView.setText(context.getString(R.string.no_quantity));
        }else {
            quantityTextView.setText(String.valueOf(productQuantity) + " " + context.getString(R.string.peace_s));
        }

        if (ProductEntry.isImageResourceProvided(productImage)){
            Utility.setPic(context, prdctImgImageView, Uri.parse(productImage));
        }else {
            prdctImgImageView.setImageResource(R.drawable.ic_no_image);
        }

        final Uri uri = ProductEntry.CONTENT_URI;

        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                ContentResolver resolver = view.getContext().getContentResolver();

                int quantityColumnIndex = myCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                int productQuantity = myCursor.getInt(quantityColumnIndex);
                if (productQuantity > 0){
                    int soldColumnIndex = myCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD);
                    int productSold = myCursor.getInt(soldColumnIndex);

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --productQuantity);
                    values.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++productSold);

                    int rowsUpdated = resolver.update(uri, values, null, null);
                }
            }
        });
    }
}