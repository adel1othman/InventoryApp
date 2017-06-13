package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Adel on 6/12/2017.
 */

public class DetailsFragment extends Fragment {

    View rootView;
    private Uri mCurrentProductUri;
    Cursor cursor;
    ContentResolver contentResolver;
    ImageView productImg;
    TextView title, price, quantity, sold, email, phone;
    Button recieve, sale;
    int productQuantityValue, productSoldValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        mCurrentProductUri = intent.getData();

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_TITLE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SOLD,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL};

        contentResolver = getActivity().getContentResolver();

        cursor = contentResolver.query(
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        productImg = (ImageView)rootView.findViewById(R.id.fdImg);
        title = (TextView)rootView.findViewById(R.id.fdTitle);
        price = (TextView)rootView.findViewById(R.id.fdPrice);
        quantity = (TextView)rootView.findViewById(R.id.fdTxtQuantity);
        sold = (TextView)rootView.findViewById(R.id.fdtTxtSold);
        email = (TextView)rootView.findViewById(R.id.fdEmailClick);
        phone = (TextView)rootView.findViewById(R.id.fdPhoneClick);
        recieve = (Button)rootView.findViewById(R.id.fdBtnReceive);
        sale = (Button)rootView.findViewById(R.id.fdBtnSale);

        int titleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int prdctImgColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
        int soldColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD);
        int emailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
        int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

        while (cursor.moveToNext()){
            final String productTitle = cursor.getString(titleColumnIndex);
            double productPrice = cursor.getDouble(priceColumnIndex);
            final int productQuantity = cursor.getInt(quantityColumnIndex);
            String productImage = cursor.getString(prdctImgColumnIndex);
            int productSold = cursor.getInt(soldColumnIndex);
            final String productEmail = cursor.getString(emailColumnIndex);
            final String productPhone = cursor.getString(phoneColumnIndex);

            productQuantityValue = productQuantity;
            productSoldValue = productSold;

            title.setText(productTitle);
            if (productPrice == 0){
                price.setText(getContext().getString(R.string.free));
            }else {
                price.setText(String.format("%.2f$", productPrice));
            }

            if (productQuantity == 0){
                quantity.setText(getContext().getString(R.string.no_quantity));
            }else {
                quantity.setText(String.valueOf(productQuantity) + " " + getContext().getString(R.string.peace_s));
            }

            if (ProductEntry.isImageResourceProvided(productImage)){
                Utility.setPic(getContext(), productImg, Uri.parse(productImage));
            }else {
                productImg.setImageResource(R.drawable.ic_no_image);
            }

            sold.setText(String.valueOf(productSold));

            if (!TextUtils.isEmpty(productEmail)){
                SpannableString content = new SpannableString(productEmail);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                email.setText(content);
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailTo:", productEmail, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order " + productTitle);
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Order of " + productTitle);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
            }else {
                email.setText(R.string.not_provided);
            }

            if (!TextUtils.isEmpty(productPhone)){
                SpannableString content = new SpannableString(productPhone);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                phone.setText(content);
                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + productPhone));
                        startActivity(phoneIntent);
                    }
                });
            }else {
                phone.setText(R.string.not_provided);
            }
        }

        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();

                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, ++productQuantityValue);
                int rowsUpdated = contentResolver.update(mCurrentProductUri, values, null, null);
                reInitViews();
            }
        });

        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                if (productQuantityValue > 0){

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --productQuantityValue);
                    values.put(ProductEntry.COLUMN_PRODUCT_SOLD, ++productSoldValue);

                    int rowsUpdated = contentResolver.update(mCurrentProductUri, values, null, null);
                    reInitViews();
                }
            }
        });

        return rootView;
    }

    private void reInitViews(){
        String[] projection = {
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SOLD};

        contentResolver = getActivity().getContentResolver();

        cursor = contentResolver.query(
                mCurrentProductUri,
                projection,
                null,
                null,
                null);

        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int soldColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SOLD);

        while (cursor.moveToNext()){
            int productQuantity = cursor.getInt(quantityColumnIndex);
            int productSold = cursor.getInt(soldColumnIndex);
            if (productQuantity == 0){
                quantity.setText(getContext().getString(R.string.no_quantity));
            }else {
                quantity.setText(String.valueOf(productQuantity) + " " + getContext().getString(R.string.peace_s));
            }
            sold.setText(String.valueOf(productSold));
        }
    }
}