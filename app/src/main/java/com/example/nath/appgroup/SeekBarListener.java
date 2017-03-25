package com.example.nath.appgroup;

import android.app.Activity;
import android.widget.SeekBar;

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
    final static int ALGORITHM_CONTRAST_EQUALIZATION = 1;
    final static int ALGORITHM_COLORIZE = 2;
    final static int ALGORITHM_LUMINOSITY = 3;
    final static int ALGORITHM_FLASH = 4;
    final static int ALGORITHM_HOUGH_THETA = 5;
    final static int ALGORITHM_HOUGH_THRESHOLD = 6;
    final static int ALGORITHM_SPHERES = 7;
    final static int ALGORITHM_MOSAIC = 8;
    final static int ALGORITHM_CANNY_HIGH = 9;
    final static int ALGORITHM_CANNY_LOW = 10;

    private CustomImageView customImageView;
    private int algorithm;
    private Activity activity;

    public SeekBarListener(Activity activity, CustomImageView customImageView, int algorithm) {
        this.customImageView = customImageView;
        this.algorithm = algorithm;
        this.activity = activity;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Image imageToProcess = customImageView.getImageTmp();

        SeekBar seekBarTheta = (SeekBar)activity.findViewById(R.id.seekBarHoughTheta);
        SeekBar seekBarThreshold = (SeekBar)activity.findViewById(R.id.seekBarHoughThreshold);

        SeekBar seekBarHigh = (SeekBar)activity.findViewById(R.id.seekBarCannyHigh);
        SeekBar seekBarLow = (SeekBar)activity.findViewById(R.id.seekBarCannyLow);

        switch (algorithm) {
            case ALGORITHM_CONTRAST_EQUALIZATION:
                Algorithms.contrastEqualization(imageToProcess, seekBar.getProgress());
                break;
            case ALGORITHM_COLORIZE:
                Algorithms.colorize(imageToProcess, seekBar.getProgress());
                break;
            case ALGORITHM_LUMINOSITY:
                Algorithms.luminosity(imageToProcess, seekBar.getProgress() - 255);
                break;
            case ALGORITHM_FLASH:
                int prog = seekBar.getProgress();
                double val;
                if (prog == 0)
                    val = 0.0;
                else if (prog < 10)
                    val = 1.0 - 1.0/prog;
                else if (prog == 10)
                    val = 1.0;
                else
                    val = 1.0 + (prog - 10) * 0.05;
                Algorithms.multiplicativeLuminosity(imageToProcess, val);
                break;
            case ALGORITHM_HOUGH_THETA:
                Algorithms.hough_transform(imageToProcess,
                        (seekBarTheta.getProgress() + 1.0) * 1.0, seekBarThreshold.getProgress() + 1);
                break;
            case ALGORITHM_HOUGH_THRESHOLD:
                Algorithms.hough_transform(imageToProcess,
                        (seekBarTheta.getProgress() + 1.0) * 1.0, seekBar.getProgress() + 1);
                break;
            case ALGORITHM_SPHERES:
                Algorithms.spheres(imageToProcess, (seekBar.getProgress() + 2) * 2 + 1);
                break;
            case ALGORITHM_MOSAIC:
                Algorithms.mosaic(imageToProcess, seekBar.getProgress());
                break;
            case ALGORITHM_CANNY_HIGH:
                double progHigh = seekBarHigh.getProgress();
                double progLow = seekBarLow.getProgress();
                if (progHigh != 0)
                    progHigh = 1 / progHigh;
                if (progLow != 0)
                    progLow = 1 / progLow;
                Algorithms.cannyEdgeDetector(imageToProcess, progLow, progHigh);
                break;
            case ALGORITHM_CANNY_LOW:
                double progHigh2 = seekBarHigh.getProgress();
                double progLow2 = seekBarLow.getProgress();
                if (progHigh2 != 0)
                    progHigh2 = 1 / progHigh2;
                if (progLow2 != 0)
                    progLow2 = 1 / progLow2;
                Algorithms.cannyEdgeDetector(imageToProcess, progLow2, progHigh2);
                break;
        }

        customImageView.setImage(imageToProcess, false);
    }
}