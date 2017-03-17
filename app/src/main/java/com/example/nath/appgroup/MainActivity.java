package com.example.nath.appgroup;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

/*
Image : Classe personnalisée pour représenter une image. Elle contient un tableau à 1 dimension de int.
Ce tableau est le même tableau que dans Bitmap. La classe contient aussi les attribut height pour la hauteur
et width pour la largeur. Toutes les méthodes sont justes des setter/getter sauf getBitmap qui renvoit
le Bitmap généré à partir du tableau pixels.

CustomImageView : Classe personnalisée qui extends du ImageView d'Android.
Elle contient tout d'abord un attribut Image img qui est l'image affiché à l'écran, Image imgBackup
est l'image telle qu'elle était la première fois qu'elle a été chargée et permet donc de faire un reset,
Image imgTmp est utilisé pour certains algorithmes(luminosity, contrastEqualization et colorize). Ces
algorithmes font varier certains paramètres toujours à partir des données de l'image de départ. Pour ne pas
modifier en permanence l'image et donc pouvoir voir la différence sur l'image selon certains paramètre,
on change en permanence img mais pas imgTmp que l'on utilise pour faire les calculs. Ces 3 algorithmes ne
mettent jamais à jour imgTmp mais les autres le font.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lenna);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        int[] pixels = new int[bitmapHeight * bitmapWidth];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmapWidth, bitmapHeight);
        Image img = new Image(pixels, bitmapHeight, bitmapWidth);

        CustomImageView customImageView = (CustomImageView)findViewById(R.id.customImageView);
        customImageView.setImage(img, true);

        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBarListener(customImageView));

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
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);

        Image imageToProcess = customImageView.getImage();
        seekBar.setVisibility(View.INVISIBLE);

        switch (item.getItemId()){
            case R.id.camera:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra("return-data", true);
                startActivityForResult(intentCamera, 101);
                break;

            //Code from coderzheaven.com
            case R.id.gallery:
                Intent intentGallery = new Intent();
                intentGallery.setType("image/*");
                intentGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentGallery, "Select Picture"), 100);
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
                seekBar.setMax(255);
                seekBar.setVisibility(View.VISIBLE);
                break;
            case R.id.colorize:
                customImageView.saveImageTemporary();
                seekBar.setMax(360);
                seekBar.setVisibility(View.VISIBLE);
                break;
            case R.id.luminosity:
                customImageView.saveImageTemporary();
                seekBar.setMax(510);
                seekBar.setVisibility(View.VISIBLE);
                break;
            case R.id.sobel:
                Algorithms.sobelEdgeDetector(imageToProcess);
                break;
            case R.id.moyenneur:
                Algorithms.meanFilter(imageToProcess);
                break;
            case R.id.gaussien:
                Algorithms.gaussianFilter(imageToProcess);
                break;
            case R.id.laplacien:
                Algorithms.laplacien(imageToProcess);
                break;
            case R.id.cartoonize:
                Image trace_edges = imageToProcess.clone();
                Algorithms.cartoonize(imageToProcess, 12); // SEEKBAR STP
                Algorithms.sobelEdgeDetector(trace_edges);
                // LA FONCTION TRACE DOIT AVOIR UNE SEEKBAR DE 0 A 255 MAIS UNIQUEMENT TRACE
                Algorithms.trace(imageToProcess, trace_edges, 255);
                break;
            case R.id.houghtransform:
                Algorithms.hough_transform(imageToProcess, 1.0, 5.0, 50);
                break;
            case R.id.reset:
                customImageView.setImage(customImageView.getImageBackup(), false);
                return true;
        }

        customImageView.drawImage();
        return true;
    }

    //Code from coderzheaven.com
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {

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

            if (requestCode == 101) {
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
