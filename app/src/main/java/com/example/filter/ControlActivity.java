package com.example.filter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.filter.utility.Helper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubfilter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;

import android.widget.ImageView;

public class ControlActivity extends AppCompatActivity {

    {
        System.loadLibrary("NativeImageProcessor");
    }

    Toolbar mControlToolbar;
    ImageView mTickImageView;

    ImageView mCenterImageView;
    final static int  PICK_IMAGE = 2;
    final static int MY_PERMISSION_REQUEST_STORAGE_PERMISSION = 3;

    ImageView mFirstFilterPreviewimageView;
    ImageView mSecondFilterPreviewimageView;
    ImageView mThirdFilterPreviewimageView;
    ImageView mFourthFilterPreviewimageView;

    private static final String TAG = ControlActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);



        mControlToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCenterImageView = (ImageView)findViewById(R.id.centerimageView3);

        mControlToolbar.setTitle(getString(R.string.app_name));
        mControlToolbar.setNavigationIcon(R.mipmap.icon);
        mControlToolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        mFirstFilterPreviewimageView =(ImageView)  findViewById(R.id.imageView12);
        mSecondFilterPreviewimageView =(ImageView) findViewById(R.id.imageView13);
        mThirdFilterPreviewimageView =(ImageView)  findViewById(R.id.imageView14);
        mFourthFilterPreviewimageView = (ImageView)findViewById(R.id.imageView15);


mTickImageView = (ImageView)findViewById(R.id.imageView11);
        mTickImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent =  new Intent(ControlActivity.this,imagePreviewActivity.class);
                startActivity(intent);
            }
        });

        mCenterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestStoragePermissions();

                if(ContextCompat.checkSelfPermission(ControlActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    if(ActivityCompat.shouldShowRequestPermissionRationale(ControlActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        //SHOW USER A MESSAGE
                        new MaterialDialog.Builder(ControlActivity.this).title("Permission Required")
                                .content("You need to  give the storage to easily save your filtered image")
                                .negativeText("No")
                                .positiveText("Yes")
                                .canceledOnTouchOutside(true)
                                .show();

                    }  else{
                          ActivityCompat.requestPermissions(ControlActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_STORAGE_PERMISSION);
                     }
                     return;
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        Picasso.with(ControlActivity.this).load(R.drawable.center_image) .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Filter myFilter = new  Filter();
                myFilter.addSubFilter (new BrightnessSubfilter(30));
                Bitmap ouputImage = myFilter.processFilter(bitmap);

                String  filename = System.currentTimeMillis()+"_brightness.png";

                Helper.writeDataIntoExternalStorage(ControlActivity.this,filename, ouputImage);

                Picasso.with(ControlActivity.this).load(Helper.getFileFromEXternlStorage(ControlActivity.this,filename)).fit() .centerInside().into(mFirstFilterPreviewimageView);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    public void onRequestPermissionResult(int requestCode, String permission[],int[] grantResults){
        switch (requestCode){
            case MY_PERMISSION_REQUEST_STORAGE_PERMISSION:
                if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //show a message
                    new MaterialDialog.Builder(ControlActivity.this).title("Permission Granted")
                            .content("Thank You for providing storage permission")
                            .canceledOnTouchOutside(true)
                            .positiveText("OK").show();

                }else{
                    Log.d(TAG,"Permission denied");
                }
        }
    }


    @Override
    public void onActivityResult(int requestcode, int resultCode, Intent data){
        if(requestcode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();

            Picasso.with(ControlActivity.this).load(selectedImageUri) .fit().centerInside(). into(mCenterImageView);

            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mFirstFilterPreviewimageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mSecondFilterPreviewimageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mThirdFilterPreviewimageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mFourthFilterPreviewimageView);


        }
    }
//upto this point everything is working
    public void requestStoragePermissions(){

    }

}
