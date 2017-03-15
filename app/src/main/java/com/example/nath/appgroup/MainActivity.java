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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ocean);
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
                Algorithms.toGray(imageToProcess);

                float[][] Gx = new float[3][3];
                Gx[0][0] = -1;
                Gx[0][1] = 0;
                Gx[0][2] = 1;

                Gx[1][0] = -2;
                Gx[1][1] = 0;
                Gx[1][2] = 2;

                Gx[2][0] = -1;
                Gx[2][1] = 0;
                Gx[2][2] = 1;

                float[][] Gy = new float[3][3];
                Gy[0][0] = -1;
                Gy[0][1] = -2;
                Gy[0][2] = -1;

                Gy[1][0] = 0;
                Gy[1][1] = 0;
                Gy[1][2] = 0;

                Gy[2][0] = 1;
                Gy[2][1] = 2;
                Gy[2][2] = 1;

                Image imgGx = imageToProcess.clone();
                Image imgGy = imageToProcess.clone();

                Algorithms.convolution(imgGx, Gx);
                Algorithms.convolution(imgGy, Gy);

                int[] imgGxPixels = imgGx.getPixels(0, 0, imgGx.getWidth(), imgGx.getHeight());
                int[] imgGyPixels = imgGy.getPixels(0, 0, imgGy.getWidth(), imgGy.getHeight());

                int[] output = new int[imageToProcess.getHeight() * imageToProcess.getWidth()];

                for (int i = 0; i < imageToProcess.getHeight(); ++i) {
                    for (int j = 0; j < imageToProcess.getWidth(); ++j) {
                        int valGx = Color.red(imgGxPixels[i * imageToProcess.getWidth() + j]);
                        int valGy = Color.red(imgGyPixels[i * imageToProcess.getWidth() + j]);

                        int val = (int)Math.sqrt(valGx * valGx + valGy * valGy);
                        output[i * imageToProcess.getWidth() + j] = Color.rgb(val, val, val);
                    }
                }

                imageToProcess.setPixels(output, 0, 0, imageToProcess.getWidth(), imageToProcess.getHeight());
                break;
            case R.id.moyenneur:
                Algorithms.toGray(imageToProcess);

                float matrixMoyenneur[][] = new float[3][3];
                matrixMoyenneur[0][0] = 1f/9f;
                matrixMoyenneur[0][1] = 1f/9f;
                matrixMoyenneur[0][2] = 1f/9f;

                matrixMoyenneur[1][0] = 1f/9f;
                matrixMoyenneur[1][1] = 1f/9f;
                matrixMoyenneur[1][2] = 1f/9f;

                matrixMoyenneur[2][0] = 1f/9f;
                matrixMoyenneur[2][1] = 1f/9f;
                matrixMoyenneur[2][2] = 1f/9f;

                Algorithms.convolution(imageToProcess, matrixMoyenneur);
                break;
            case R.id.gaussien:
                Algorithms.toGray(imageToProcess);

                float[][] matrixGaussien = new float[5][5];
                matrixGaussien[0][0] = 1f/98f;
                matrixGaussien[0][1] = 2f/98f;
                matrixGaussien[0][2] = 3f/98f;
                matrixGaussien[0][3] = 2f/98f;
                matrixGaussien[0][4] = 1f/98f;

                matrixGaussien[1][0] = 2f/98f;
                matrixGaussien[1][1] = 6f/98f;
                matrixGaussien[1][2] = 8f/98f;
                matrixGaussien[1][3] = 6f/98f;
                matrixGaussien[1][4] = 2f/98f;

                matrixGaussien[2][0] = 3f/98f;
                matrixGaussien[2][1] = 8f/98f;
                matrixGaussien[2][2] = 10f/98f;
                matrixGaussien[2][3] = 8f/98f;
                matrixGaussien[2][4] = 3f/98f;

                matrixGaussien[3][0] = 2f/98f;
                matrixGaussien[3][1] = 6f/98f;
                matrixGaussien[3][2] = 8f/98f;
                matrixGaussien[3][3] = 6f/98f;
                matrixGaussien[3][4] = 2f/98f;

                matrixGaussien[4][0] = 1f/98f;
                matrixGaussien[4][1] = 2f/98f;
                matrixGaussien[4][2] = 3f/98f;
                matrixGaussien[4][3] = 2f/98f;
                matrixGaussien[4][4] = 1f/98f;

                Algorithms.convolution(imageToProcess, matrixGaussien);
                break;
            case R.id.laplacien:
                Algorithms.toGray(imageToProcess);

                float matrixLaplacien[][] = new float[3][3];
                matrixLaplacien[0][0] = 0;
                matrixLaplacien[0][1] = -1;
                matrixLaplacien[0][2] = 0;

                matrixLaplacien[1][0] = -1;
                matrixLaplacien[1][1] = 4;
                matrixLaplacien[1][2] = -1;

                matrixLaplacien[2][0] = 0;
                matrixLaplacien[2][1] = -1;
                matrixLaplacien[2][2] = 0;

                Algorithms.convolution(imageToProcess, matrixLaplacien);
                break;
            case R.id.cartoonize:
                Algorithms.cartoonize(imageToProcess);
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
