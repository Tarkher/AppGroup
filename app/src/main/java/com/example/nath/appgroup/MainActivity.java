package com.example.nath.appgroup;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    final int SAVE_GALLERY = 100;
    final int GET_PHOTO = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.melenchon);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        int[] pixels = new int[bitmapHeight * bitmapWidth];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmapWidth, bitmapHeight);
        Image img = new Image(pixels, bitmapHeight, bitmapWidth);

        CustomImageView customImageView = (CustomImageView)findViewById(R.id.customImageView);
        customImageView.setImage(img, true);
        customImageView.setOnTouchListener(new CustomOnTouchListener());

        SeekBar seekBarContrastEqualization = (SeekBar)findViewById(R.id.seekBarContrastEqualization);
        seekBarContrastEqualization.setMax(255);
        seekBarContrastEqualization.setVisibility(View.INVISIBLE);
        seekBarContrastEqualization.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_CONTRAST_EQUALIZATION));

        SeekBar seekBarColorize = (SeekBar)findViewById(R.id.seekBarColorize);
        seekBarColorize.setMax(360);
        seekBarColorize.setVisibility(View.INVISIBLE);
        seekBarColorize.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_COLORIZE));

        SeekBar seekBarLuminosity = (SeekBar)findViewById(R.id.seekBarLuminosity);
        seekBarLuminosity.setMax(510);
        seekBarLuminosity.setVisibility(View.INVISIBLE);
        seekBarLuminosity.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_LUMINOSITY));

        SeekBar seekBarFlash = (SeekBar)findViewById(R.id.seekBarFlash);
        seekBarFlash.setMax(265);
        seekBarFlash.setProgress(10);
        seekBarFlash.setVisibility(View.INVISIBLE);
        seekBarFlash.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_FLASH));

        SeekBar seekBarHoughTheta = (SeekBar)findViewById(R.id.seekBarHoughTheta);
        seekBarHoughTheta.setMax(59);
        seekBarHoughTheta.setProgress(4);
        seekBarHoughTheta.setVisibility(View.INVISIBLE);
        seekBarHoughTheta.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_HOUGH_THETA));

        SeekBar seekBarHoughThreshold = (SeekBar)findViewById(R.id.seekBarHoughThreshold);
        seekBarHoughThreshold.setMax(200);
        seekBarHoughThreshold.setProgress(40);
        seekBarHoughThreshold.setVisibility(View.INVISIBLE);
        seekBarHoughThreshold.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_HOUGH_THRESHOLD));

        SeekBar seekBarSpheres = (SeekBar)findViewById(R.id.seekBarSpheres);
        seekBarSpheres.setMax(10);
        seekBarSpheres.setProgress(1);
        seekBarSpheres.setVisibility(View.INVISIBLE);
        seekBarSpheres.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_SPHERES));

        SeekBar seekBarMosaic = (SeekBar)findViewById(R.id.seekBarMosaic);
        seekBarMosaic.setMax(1500);
        seekBarMosaic.setProgress(500);
        seekBarMosaic.setVisibility(View.INVISIBLE);
        seekBarMosaic.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_MOSAIC));

        SeekBar seekBarCannyHigh = (SeekBar)findViewById(R.id.seekBarCannyHigh);
        seekBarCannyHigh.setMax(50);
        seekBarCannyHigh.setProgress(8);
        seekBarCannyHigh.setVisibility(View.INVISIBLE);
        seekBarCannyHigh.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_CANNY_HIGH));

        SeekBar seekBarCannyLow = (SeekBar)findViewById(R.id.seekBarCannyLow);
        seekBarCannyLow.setMax(50);
        seekBarCannyLow.setProgress(15);
        seekBarCannyLow.setVisibility(View.INVISIBLE);
        seekBarCannyLow.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_CANNY_LOW));

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CustomImageView customImageView = (CustomImageView)findViewById(R.id.customImageView);
        Image imageToProcess = customImageView.getImage();

        SeekBar seekBarContrastEqualization = (SeekBar)findViewById(R.id.seekBarContrastEqualization);
        seekBarContrastEqualization.setVisibility(View.INVISIBLE);
        SeekBar seekBarColorize = (SeekBar)findViewById(R.id.seekBarColorize);
        seekBarColorize.setVisibility(View.INVISIBLE);
        SeekBar seekBarLuminosity = (SeekBar)findViewById(R.id.seekBarLuminosity);
        seekBarLuminosity.setVisibility(View.INVISIBLE);
        SeekBar seekBarFlash= (SeekBar)findViewById(R.id.seekBarFlash);
        seekBarFlash.setVisibility(View.INVISIBLE);
        SeekBar seekBarHoughTheta = (SeekBar)findViewById(R.id.seekBarHoughTheta);
        seekBarHoughTheta.setVisibility(View.INVISIBLE);
        SeekBar seekBarHoughThreshold = (SeekBar)findViewById(R.id.seekBarHoughThreshold);
        seekBarHoughThreshold.setVisibility(View.INVISIBLE);
        SeekBar seekBarSpheres = (SeekBar) findViewById(R.id.seekBarSpheres);
        seekBarSpheres.setVisibility(View.INVISIBLE);
        SeekBar seekBarMosaic = (SeekBar) findViewById(R.id.seekBarMosaic);
        seekBarMosaic.setVisibility(View.INVISIBLE);
        SeekBar seekBarCannyHigh = (SeekBar)findViewById(R.id.seekBarCannyHigh);
        seekBarCannyHigh.setVisibility(View.INVISIBLE);
        SeekBar seekBarCannyLow = (SeekBar)findViewById(R.id.seekBarCannyLow);
        seekBarCannyLow.setVisibility(View.INVISIBLE);

        switch (item.getItemId()){
            case R.id.camera:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra("return-data", true);
                startActivityForResult(intentCamera, GET_PHOTO);
                break;

            //Code from coderzheaven.com
            case R.id.gallery:
                Intent intentGallery = new Intent();
                intentGallery.setType("image/*");
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGallery, "Select Picture"), SAVE_GALLERY);
                break;

            case R.id.save:
                Bitmap bitmap = customImageView.getImage().getBitmap();
                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "myBitmap", "myBitmap");
                break;

            case R.id.toGray:
                Algorithms.toGray(imageToProcess);
                break;

            case R.id.dynamicExtensionColor:
                Algorithms.dynamicExtensionColor(imageToProcess);
                break;

            case R.id.contrastEqualization:
                customImageView.saveImageTemporary();
                seekBarContrastEqualization.setVisibility(View.VISIBLE);
                break;

            case R.id.colorize:
                customImageView.saveImageTemporary();
                seekBarColorize.setVisibility(View.VISIBLE);
                break;

            case R.id.luminosity:
                customImageView.saveImageTemporary();
                seekBarLuminosity.setVisibility(View.VISIBLE);
                break;

            case R.id.flash:
                customImageView.saveImageTemporary();
                seekBarFlash.setVisibility(View.VISIBLE);
                break;

            case R.id.sobel:
                Algorithms.sobelEdgeDetector(imageToProcess);
                break;

            case R.id.canny:
                customImageView.saveImageTemporary();
                seekBarCannyHigh.setVisibility(View.VISIBLE);
                seekBarCannyLow.setVisibility(View.VISIBLE);
                break;

            case R.id.moyenneur:
                //Algorithms.meanFilter(imageToProcess, 13);
                Algorithms.brush(imageToProcess, 10);
                break;

            case R.id.texture:
                Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(), R.drawable.briques);
                int bitmapHeight = bitmapTmp.getHeight();
                int bitmapWidth = bitmapTmp.getWidth();
                int[] pixels = new int[bitmapHeight * bitmapWidth];
                bitmapTmp.getPixels(pixels, 0, bitmapTmp.getWidth(), 0, 0, bitmapWidth, bitmapHeight);
                Image texture = new Image(pixels, bitmapHeight, bitmapWidth);

                Algorithms.applyTexture(imageToProcess, texture);
                break;

            case R.id.wallTag:
                Bitmap bitmapTmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.briques);
                int bitmapHeight2 = bitmapTmp2.getHeight();
                int bitmapWidth2 = bitmapTmp2.getWidth();
                int[] pixels2 = new int[bitmapHeight2 * bitmapWidth2];
                bitmapTmp2.getPixels(pixels2, 0, bitmapTmp2.getWidth(), 0, 0, bitmapWidth2, bitmapHeight2);
                Image texture2 = new Image(pixels2, bitmapHeight2, bitmapWidth2);

                Algorithms.applyTexture(imageToProcess, texture2);

                Image trace2 = imageToProcess.clone();
                Algorithms.cannyEdgeDetector(trace2, 0.08, 0.15);
                Algorithms.cartoonize(imageToProcess, 8);
                Algorithms.traceEdges(imageToProcess, trace2);
                break;

            case R.id.mosaic:
                customImageView.saveImageTemporary();
                seekBarMosaic.setVisibility(View.VISIBLE);
                break;

            case R.id.sharpens:
                Algorithms.sharpens(imageToProcess);
                break;

            case R.id.sharpens2:
                Algorithms.contrastFilter(imageToProcess);
                break;

            case R.id.gaussien:
                Algorithms.gaussianFilter(imageToProcess, 3, 0.8);
                break;

            case R.id.laplacien:
                Algorithms.laplacien(imageToProcess);
                break;

            case R.id.cartoonize:
                Image trace = imageToProcess.clone();
                Algorithms.cannyEdgeDetector(trace, 0.08, 0.15);
                Algorithms.cartoonize(imageToProcess, 8);
                Algorithms.traceEdges(imageToProcess, trace);
                break;

            case R.id.houghtransform:
                customImageView.saveImageTemporary();
                seekBarHoughTheta.setVisibility(View.VISIBLE);
                seekBarHoughThreshold.setVisibility(View.VISIBLE);
                break;

            case R.id.spheres:
                customImageView.saveImageTemporary();
                seekBarSpheres.setVisibility(View.VISIBLE);
                break;

            case R.id.rotation:
                Algorithms.rotate(imageToProcess, customImageView);
                break;

            case R.id.reset:
                customImageView.setImage(customImageView.getImageBackup(), true);
                return true;
        }

        customImageView.drawImage();
        return true;
    }

    //Code from coderzheaven.com
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SAVE_GALLERY) {

                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        int height = bitmap.getHeight();
                        int width = bitmap.getWidth();
                        int pixels[] = new int[height * width];
                        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                        Image imgTmp = new Image(pixels, height, width);

                        CustomImageView customImageView = (CustomImageView) findViewById(R.id.customImageView);
                        customImageView.setImage(imgTmp, true);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (requestCode == GET_PHOTO) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();
                    int[] pixels = new int[height * width];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                    CustomImageView customImageView = (CustomImageView) findViewById(R.id.customImageView);
                    customImageView.setImage(new Image(pixels, height, width), true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}