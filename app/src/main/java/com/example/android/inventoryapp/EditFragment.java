package com.example.android.inventoryapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Adel on 6/9/2017.
 */

public class EditFragment extends Fragment {

    View rootView;
    private Uri mCurrentProductUri;
    Cursor cursor;
    ContentResolver contentResolver;
    ImageView productImg;
    EditText title, price, quantity, email, phone;

    String productTitle, productImage, productEmail, productPhone;
    double productPrice;
    int productQuantity;
    private String imagePath = "";
    public static final int PICK_IMAGE_REQUEST = 1;

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
        rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        productImg = (ImageView)rootView.findViewById(R.id.feImg);
        title = (EditText)rootView.findViewById(R.id.feTitle);
        price = (EditText)rootView.findViewById(R.id.fePrice);
        quantity = (EditText)rootView.findViewById(R.id.feQuantity);
        email = (EditText)rootView.findViewById(R.id.feEmail);
        phone = (EditText)rootView.findViewById(R.id.fePhone);

        int titleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int prdctImgColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);
        int emailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
        int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

        while (cursor.moveToNext()){
            productTitle = cursor.getString(titleColumnIndex);
            productPrice = cursor.getDouble(priceColumnIndex);
            productQuantity = cursor.getInt(quantityColumnIndex);
            productImage = cursor.getString(prdctImgColumnIndex);
            productEmail = cursor.getString(emailColumnIndex);
            productPhone = cursor.getString(phoneColumnIndex);

            title.setText(productTitle);
            price.setText(String.format("%.2f", productPrice));
            quantity.setText(String.valueOf(productQuantity));

            if (ProductEntry.isImageResourceProvided(productImage)){
                Utility.setPic(getContext(), productImg, Uri.parse(productImage));
            }else {
                productImg.setImageResource(R.drawable.ic_no_image);
            }

            email.setText(productEmail);
            phone.setText(productPhone);
        }

        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Utility.verifyStoragePermissions(getActivity());
                } else {
                    Intent imageIntent = new Intent();
                    imageIntent.setType("image/*");
                    imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(imageIntent,
                            "Select Image"), PICK_IMAGE_REQUEST);
                }
            }
        });

        return rootView;
    }

    public void saveChanges(){
        String newProductTitle = title.getText().toString().trim();
        String newProductEmail = email.getText().toString().trim();
        String newProductPhone = phone.getText().toString().trim();
        String newProductPriceStr = price.getText().toString().trim();
        String newProductQuantityStr = quantity.getText().toString().trim();
        double newPrice = 0;
        int newProductQuantity = 0;

        ContentValues values = new ContentValues();

        if (!TextUtils.isEmpty(newProductPriceStr)){
            newPrice = Double.parseDouble(newProductPriceStr);
        }

        if (!TextUtils.isEmpty(newProductQuantityStr)){
            newProductQuantity = Integer.parseInt(newProductQuantityStr);
        }

        if ((TextUtils.isEmpty(newProductTitle) && TextUtils.isEmpty(newProductEmail) &&
                TextUtils.isEmpty(newProductPriceStr) && TextUtils.isEmpty(newProductPhone) &&
                TextUtils.isEmpty(newProductQuantityStr)) || (productTitle.equals(newProductTitle) &&
                productEmail.equals(newProductEmail) && productPhone.equals(newProductPhone) &&
                productImage.equals(imagePath) && productPrice == newPrice && productQuantity == newProductQuantity)) {
            return;
        }

        if (TextUtils.isEmpty(newProductTitle)){
            Toast.makeText(getContext(), getString(R.string.titleRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_TITLE, newProductTitle);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, newPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newProductQuantity);

        if (!TextUtils.isEmpty(imagePath)){
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imagePath);
        }

        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, newProductPhone);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, newProductEmail);

        int rowsUpdated = contentResolver.update(mCurrentProductUri, values, null, null);

        if (rowsUpdated == 0) {
            Toast.makeText(getContext(), getString(R.string.edit_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), getString(R.string.edit_product_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                imagePath = uri.toString();
                Log.d("productImg", "onActivityCreated URI: " + imagePath);
                Utility.setPic(getContext(), productImg, uri);
            }
        }
    }
}