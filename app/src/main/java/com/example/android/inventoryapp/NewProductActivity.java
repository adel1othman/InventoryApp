package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Adel on 6/9/2017.
 */

public class NewProductActivity extends AppCompatActivity {

    private EditText mTitleEditText, mPriceEditText, mQuantityEditText, mPhoneEditText, mEmailEditText;
    private ImageView productImg;
    private String imagePath = "";
    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        setTitle(getString(R.string.add_new));

        mTitleEditText = (EditText) findViewById(R.id.edtTxtTitle);
        mPriceEditText = (EditText) findViewById(R.id.edtTxtPrice);
        mQuantityEditText = (EditText) findViewById(R.id.edtTxtQuantity);
        mPhoneEditText = (EditText) findViewById(R.id.edtTxtPhone);
        mEmailEditText = (EditText) findViewById(R.id.edtTxtEmail);
        productImg = (ImageView)findViewById(R.id.productImg);
        productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Utility.verifyStoragePermissions(NewProductActivity.this);
                } else {
                    Intent imageIntent = new Intent();
                    imageIntent.setType("image/*");
                    imageIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(imageIntent,
                            "Select Image"), PICK_IMAGE_REQUEST);
                }
            }
        });
    }

    private void saveProduct() {
        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(titleString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(phoneString) &&
                TextUtils.isEmpty(emailString)) {
            return;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(titleString)){
            Toast.makeText(this, getString(R.string.titleRquired), Toast.LENGTH_SHORT).show();
            return;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_TITLE, titleString);

        double price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Double.parseDouble(priceString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        int sold = 0;
        values.put(ProductEntry.COLUMN_PRODUCT_SOLD, sold);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imagePath);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, phoneString);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, emailString);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.insert_product_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.insert_product_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;
            case android.R.id.home:
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(NewProductActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_new_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                imagePath = uri.toString();
                Log.d("productImg", "onActivityCreated URI: " + imagePath);
                Utility.setPic(getBaseContext(), productImg, uri);
            }
        }
    }
}