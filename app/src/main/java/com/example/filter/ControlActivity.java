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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.filter.utility.Helper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.zomato.photofilters.imageprocessors.subfilters.VignetteSubfilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;

public class ControlActivity extends AppCompatActivity {
   //static for zomatofilter effect using c library
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }



    Toolbar mControlToolbar;
    ImageView mTickImageView;

    ImageView mCenterImageView;
    //added from here
    Target mSmallTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);


            Filter myFilter = new  Filter();
            myFilter.addSubFilter (new BrightnessSubfilter(90));//90 is brightness intensity value
            Bitmap ouputImage = myFilter.processFilter(mutableBitmap);

            String  filename = System.currentTimeMillis()+"_brightness.png";
            Helper.writeDataIntoExternalStorage(ControlActivity.this,filename, ouputImage);
            Picasso.with(ControlActivity.this).load(Helper.getFileFromEXternlStorage(ControlActivity.this,filename)).fit() .centerInside().into(mFirstFilterPreviewimageView);

            // Saturation Effect //not showing after compilation [Help]
            mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Filter myFilterSaturation = new  Filter();
            myFilterSaturation.addSubFilter (new SaturationSubfilter(1.7f));//1.7f is brightness intensity value
            Bitmap ouputImageSaturation = myFilterSaturation.processFilter(mutableBitmap);

            String  filenameSaturation = System.currentTimeMillis()+"_saturation.png";
            Helper.writeDataIntoExternalStorage(ControlActivity.this,filenameSaturation, ouputImageSaturation);
            Picasso.with(ControlActivity.this).load(Helper.getFileFromEXternlStorage(ControlActivity.this,filenameSaturation)).fit() .centerInside().into(mSecondFilterPreviewimageView);

            //Vignette effect //not showing after compilation [Help]
            mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Filter myFilterVignette = new  Filter();
            myFilterVignette.addSubFilter(new VignetteSubfilter(ControlActivity.this, 170));//170 is brightness intensity value
            Bitmap ouputImageVignette = myFilterVignette.processFilter(mutableBitmap);

            String  filenameVignette = System.currentTimeMillis()+"_vignette.png";
            Helper.writeDataIntoExternalStorage(ControlActivity.this,filenameVignette, ouputImageVignette);
            Picasso.with(ControlActivity.this).load(Helper.getFileFromEXternlStorage(ControlActivity.this,filenameVignette)).fit() .centerInside().into(mThirdFilterPreviewimageView);

            //contrast effect //not showing after compilation [Help]
            mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888,true);
            Filter myFilterContrast = new  Filter();
            myFilterContrast.addSubFilter(new ContrastSubfilter(1.6f));//1.6f is brightness intensity value
            Bitmap ouputImageContrast = myFilterContrast.processFilter(mutableBitmap);

            String  filenameContrast = System.currentTimeMillis()+"_contrast.png";
            Helper.writeDataIntoExternalStorage(ControlActivity.this,filenameContrast, ouputImageContrast);
            Picasso.with(ControlActivity.this).load(Helper.getFileFromEXternlStorage(ControlActivity.this,filenameContrast)).fit() .centerInside().into(mFourthFilterPreviewimageView);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    //ended here

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
//cut from here
                requestStoragePermissions();

                if(ContextCompat.checkSelfPermission(ControlActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    return;
                }

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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
            //added here
            Picasso.with(ControlActivity.this).load(R.drawable.center_image) .into(mSmallTarget);
            //ended here

            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mFirstFilterPreviewimageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mSecondFilterPreviewimageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mThirdFilterPreviewimageView);
            Picasso.with(ControlActivity.this).load(selectedImageUri).fit().centerInside().  into(mFourthFilterPreviewimageView);


        }
    }
//upto this point everything is working
    public void requestStoragePermissions(){

        //cut from imageview
        if(ContextCompat.checkSelfPermission(ControlActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(ControlActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //SHOW USER A MESSAGE
                new MaterialDialog.Builder(ControlActivity.this).title("Permission Required")
                        .content("You need to  give the storage to easily save your filtered image")
                        .negativeText("No")
                        .positiveText("Yes")
                        .canceledOnTouchOutside(true)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS),0);
                            }
                        })
                        .show();

            }  else{
                ActivityCompat.requestPermissions(ControlActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST_STORAGE_PERMISSION);
            }
            return;
        }

    }

}
