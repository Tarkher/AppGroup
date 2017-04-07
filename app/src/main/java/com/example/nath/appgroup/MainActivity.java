package com.example.nath.appgroup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nath.appgroup.AlgorithmThread.AlgorithmThread;

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

        TextView textViewMiddle = (TextView)findViewById(R.id.textViewMiddle);
        textViewMiddle.setText("");
        TextView textViewBottom = (TextView)findViewById(R.id.textViewBottom);
        textViewBottom.setText("");

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

        SeekBar seekBarColorFilter = (SeekBar)findViewById(R.id.seekBarColorFilter);
        seekBarColorFilter.setMax(360);
        seekBarColorFilter.setVisibility(View.INVISIBLE);
        seekBarColorFilter.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_COLOR_FILTER));

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

        SeekBar seekBarPainting = (SeekBar)findViewById(R.id.seekBarPainting);
        seekBarPainting.setMax(100);
        seekBarPainting.setProgress(20);
        seekBarPainting.setVisibility(View.INVISIBLE);
        seekBarPainting.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_PAINTING));

        SeekBar seekBarLabyMax = (SeekBar)findViewById(R.id.seekBarLabyMax);
        seekBarLabyMax.setMax(15);
        seekBarLabyMax.setProgress(6);
        seekBarLabyMax.setVisibility(View.INVISIBLE);
        seekBarLabyMax.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_LABYRINTH_MAX));

        SeekBar seekBarLabyRatio = (SeekBar)findViewById(R.id.seekBarLabyRatio);
        seekBarLabyRatio.setMax(254);
        seekBarLabyRatio.setProgress(179);
        seekBarLabyRatio.setVisibility(View.INVISIBLE);
        seekBarLabyRatio.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_LABYRINTH_RATIO));

        SeekBar seekBarFlashlightIntensity = (SeekBar)findViewById(R.id.seekBarFlashlightIntensity);
        seekBarFlashlightIntensity.setMax(200);
        seekBarFlashlightIntensity.setProgress(100);
        seekBarFlashlightIntensity.setVisibility(View.INVISIBLE);
        seekBarFlashlightIntensity.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_FLASHLIGHT_INTENSITY));

        SeekBar seekBarFlashlightRadius = (SeekBar)findViewById(R.id.seekBarFlashlightRadius);
        seekBarFlashlightRadius.setMax(100);
        seekBarFlashlightRadius.setProgress(80);
        seekBarFlashlightRadius.setVisibility(View.INVISIBLE);
        seekBarFlashlightRadius.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_FLASHLIGHT_RADIUS));

        SeekBar seekBarRadialBlur = (SeekBar)findViewById(R.id.seekBarRadialBlur);
        seekBarRadialBlur.setMax(180);
        seekBarRadialBlur.setProgress(20);
        seekBarRadialBlur.setVisibility(View.INVISIBLE);
        seekBarRadialBlur.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_RADIAL_BLUR));

        SeekBar seekBarCircularBlur = (SeekBar)findViewById(R.id.seekBarCircularBlur);
        seekBarCircularBlur.setMax(180);
        seekBarCircularBlur.setProgress(20);
        seekBarCircularBlur.setVisibility(View.INVISIBLE);
        seekBarCircularBlur.setOnSeekBarChangeListener(
                new SeekBarListener(this, customImageView, SeekBarListener.ALGORITHM_CIRCULAR_BLUR));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
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

        TextView textViewMiddle = (TextView)findViewById(R.id.textViewMiddle);
        textViewMiddle.setText("");
        TextView textViewBottom = (TextView)findViewById(R.id.textViewBottom);
        textViewBottom.setText("");

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
        SeekBar seekBarPainting = (SeekBar) findViewById(R.id.seekBarPainting);
        seekBarPainting.setVisibility(View.INVISIBLE);
        SeekBar seekBarLabyMax = (SeekBar)findViewById(R.id.seekBarLabyMax);
        seekBarLabyMax.setVisibility(View.INVISIBLE);
        SeekBar seekBarLabyRatio = (SeekBar)findViewById(R.id.seekBarLabyRatio);
        seekBarLabyRatio.setVisibility(View.INVISIBLE);
        SeekBar seekBarColorFilter = (SeekBar)findViewById(R.id.seekBarColorFilter);
        seekBarColorFilter.setVisibility(View.INVISIBLE);
        SeekBar seekBarFlashlightIntensity = (SeekBar)findViewById(R.id.seekBarFlashlightIntensity);
        seekBarFlashlightIntensity.setVisibility(View.INVISIBLE);
        SeekBar seekBarFlashlightRadius = (SeekBar)findViewById(R.id.seekBarFlashlightRadius);
        seekBarFlashlightRadius.setVisibility(View.INVISIBLE);
        SeekBar seekBarRadialBlur = (SeekBar) findViewById(R.id.seekBarRadialBlur);
        seekBarRadialBlur.setVisibility(View.INVISIBLE);
        SeekBar seekBarCircularBlur = (SeekBar) findViewById(R.id.seekBarCircularBlur);
        seekBarCircularBlur.setVisibility(View.INVISIBLE);

        switch (item.getItemId()){
            case R.id.camera:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra("return-data", true);
                startActivityForResult(intentCamera, GET_PHOTO);
                break;

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

            case R.id.toGrayThread:
                Object[] input = new Object[0];
                AlgorithmThread algorithmThread = new AlgorithmThread(imageToProcess,
                        AlgorithmThread.ALGORITHM_TO_GRAY, input);
                algorithmThread.run();
                break;

            case R.id.toGray:
                Algorithms.toGray(imageToProcess);
                break;

            case R.id.dynamicExtensionColor:
                Algorithms.dynamicExtensionColor(imageToProcess);
                break;

            case R.id.contrastEqualization:
                textViewBottom.setText("Contrast");
                customImageView.saveImageTemporary();
                seekBarContrastEqualization.setVisibility(View.VISIBLE);
                break;

            case R.id.colorize:
                textViewBottom.setText("Hue");
                customImageView.saveImageTemporary();
                seekBarColorize.setVisibility(View.VISIBLE);
                break;

            case R.id.colorFilter:
                textViewBottom.setText("Hue");
                customImageView.saveImageTemporary();
                seekBarColorFilter.setVisibility(View.VISIBLE);
                break;

            case R.id.luminosity:
                textViewBottom.setText("Intensity");
                customImageView.saveImageTemporary();
                seekBarLuminosity.setVisibility(View.VISIBLE);
                break;

            case R.id.flash:
                textViewBottom.setText("Intensity");
                customImageView.saveImageTemporary();
                seekBarFlash.setVisibility(View.VISIBLE);
                break;

            case R.id.sobel:
                Algorithms.sobelEdgeDetector(imageToProcess);
                break;

            case R.id.canny:
                textViewBottom.setText("Low Threshold");
                textViewMiddle.setText("High Threshold");
                customImageView.saveImageTemporary();
                seekBarCannyHigh.setVisibility(View.VISIBLE);
                seekBarCannyLow.setVisibility(View.VISIBLE);
                break;

            case R.id.moyenneur:
                Algorithms.meanFilter(imageToProcess, 3);
                break;

            case R.id.circularBlur:
                textViewBottom.setText("Intensity");
                customImageView.saveImageTemporary();
                seekBarCircularBlur.setVisibility(View.VISIBLE);
                break;

            case R.id.radialBlur:
                textViewBottom.setText("Intensity");
                customImageView.saveImageTemporary();
                seekBarRadialBlur.setVisibility(View.VISIBLE);
                break;

            case R.id.radialExplosion:
                Algorithms.radialExplosion(imageToProcess);
                break;

            case R.id.flashlight:
                textViewBottom.setText("Intensity");
                textViewMiddle.setText("Radius");
                customImageView.saveImageTemporary();
                seekBarFlashlightIntensity.setVisibility(View.VISIBLE);
                seekBarFlashlightRadius.setVisibility(View.VISIBLE);
                break;

            case R.id.relief:
                Algorithms.relief(imageToProcess);
                break;

            case R.id.painting:
                textViewBottom.setText("Iteration");
                customImageView.saveImageTemporary();
                seekBarPainting.setVisibility(View.VISIBLE);
                break;

            case R.id.duplicate:
                Algorithms.duplicate(imageToProcess);
                break;

            case R.id.labyrinthe:
                textViewBottom.setText("Step");
                textViewMiddle.setText("Intensity");
                customImageView.saveImageTemporary();
                seekBarLabyMax.setVisibility(View.VISIBLE);
                seekBarLabyRatio.setVisibility(View.VISIBLE);
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
                textViewBottom.setText("Seeds");
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
                Algorithms.gaussianFilter(trace, 3, 0.8);
                Algorithms.cannyEdgeDetector(trace, 0.08, 0.15);
                Algorithms.cartoonize(imageToProcess, 11);
                Algorithms.traceEdges(imageToProcess, trace);
                break;

            case R.id.houghtransform:
                textViewBottom.setText("Theta");
                textViewMiddle.setText("Threshold");
                customImageView.saveImageTemporary();
                seekBarHoughTheta.setVisibility(View.VISIBLE);
                seekBarHoughThreshold.setVisibility(View.VISIBLE);
                break;

            case R.id.spheres:
                textViewBottom.setText("Radius");
                customImageView.saveImageTemporary();
                seekBarSpheres.setVisibility(View.VISIBLE);
                break;

            case R.id.rotation:
                Algorithms.rotate(imageToProcess, customImageView);
                break;

            case R.id.resize:
                customImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;

            case R.id.reset:
                customImageView.setImage(customImageView.getImageBackup(), true);
                return true;
        }

        customImageView.drawImage();
        return true;
    }

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