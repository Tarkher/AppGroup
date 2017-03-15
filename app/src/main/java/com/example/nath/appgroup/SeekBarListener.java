package com.example.nath.appgroup;

import android.widget.SeekBar;

public class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
    private CustomImageView customImageView;

    public SeekBarListener(CustomImageView customImageView) {
        this.customImageView = customImageView;
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

        switch (seekBar.getMax()) {
            case 255:
                Algorithms.contrastEqualization(imageToProcess, seekBar.getProgress());
                break;
            case 360:
                Algorithms.colorize(imageToProcess, seekBar.getProgress());
                break;
            case 510:
                Algorithms.luminosity(imageToProcess, seekBar.getProgress() - 255);
                break;
        }

        customImageView.setImage(imageToProcess, false);
    }
}
