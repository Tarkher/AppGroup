package com.example.nath.appgroup;

import android.app.Activity;
import android.widget.SeekBar;

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
    final static int ALGORITHM_CONTRAST_EQUALIZATION = 1;
    final static int ALGORITHM_COLORIZE = 2;
    final static int ALGORITHM_LUMINOSITY = 3;
    final static int ALGORITHM_HOUGH_THETA = 5;
    final static int ALGORITHM_HOUGH_THRESHOLD = 6;

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
            case ALGORITHM_HOUGH_THETA:
                Algorithms.hough_transform(imageToProcess,
                        (seekBarTheta.getProgress() + 1.0) * 1.0, seekBarThreshold.getProgress() + 1);
                break;
            case ALGORITHM_HOUGH_THRESHOLD:
                Algorithms.hough_transform(imageToProcess,
                        (seekBarTheta.getProgress() + 1.0) * 1.0, seekBar.getProgress() + 1);
                break;

        }

        customImageView.setImage(imageToProcess, false);
    }
}