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

    private Context myContext;
    final String LOG_TAG = ProductCursorAdapter.class.getSimpleName();

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        myContext = context;

        TextView titleTextView = (TextView) view.findViewById(R.id.prdctTitle);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView prdctImgImageView = (ImageView) view.findViewById(R.id.imgProduct);
        ImageView saleImageView = (ImageView) view.findViewById(R.id.imgSale);

        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int titleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int soldColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD);
        int prdctImgColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

        final int productID = cursor.getInt(idColumnIndex);
        final String productTitle = cursor.getString(titleColumnIndex);
        final double productPrice = cursor.getDouble(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        final int productSold = cursor.getInt(soldColumnIndex);
        final String productImage = cursor.getString(prdctImgColumnIndex);

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

        final ContentResolver resolver = view.getContext().getContentResolver();

        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();

                Uri uri = ProductEntry.currentProductUri(productID);
                if (productQuantity > 0){
                    int newQuantity = productQuantity;
                    int newSold = productSold;

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --newQuantity);
                    values.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++newSold);

                    int rowsUpdated = resolver.update(uri, values, null, null);
                    myContext.getContentResolver().notifyChange(uri, null);
                }
            }
        });
    }
}
