package com.example.nath.appgroup.AlgorithmThread;

import com.example.nath.appgroup.Image;

public class AlgorithmThread {
    public final static int IMG_FACTOR = 3;
    public final static int ALGORITHM_TO_GRAY = 10;
    public final static int ALGORITHM_COLOR_FILTER = 11;
    public final static int ALGORITHM_COLORIZE = 12;

    private Image imgPointer;
    private Image img;
    private Thread[] threadPool;
    private int algorithm;
    private Object[] input;

    public AlgorithmThread(Image img, int algorithm, Object[] input) {
        this.imgPointer = img;
        this.img = img.clone();
        this.algorithm = algorithm;
        this.input = input;
    }

    public void run() {
        int width = img.getWidth();
        int height = img.getHeight();

        try {
            threadPool = new Thread[IMG_FACTOR * IMG_FACTOR];

            int widthStep = width / IMG_FACTOR;
            int heightStep = height / IMG_FACTOR;

            for (int i = 0; i < IMG_FACTOR; ++i) {
                for (int j = 0; j < IMG_FACTOR; ++j) {
                    switch (algorithm) {
                        case ALGORITHM_TO_GRAY:
                            threadPool[i * IMG_FACTOR + j] = new AlgorithmThreadToGray(img, imgPointer,
                                    i * widthStep, j * heightStep, (i + 1) * widthStep, (j + 1) * heightStep);
                            break;
                        case ALGORITHM_COLOR_FILTER:
                            int radius = (Integer)input[0];
                            threadPool[i * IMG_FACTOR + j] = new AlgorithmThreadColorFilter(img, imgPointer,
                                    i * widthStep, j * heightStep, (i + 1) * widthStep, (j + 1) * heightStep, radius);
                            break;
                        case ALGORITHM_COLORIZE:
                            int hue = (Integer)input[0];
                            threadPool[i * IMG_FACTOR + j] = new AlgorithmThreadColorize(img, imgPointer,
                                    i * widthStep, j * heightStep, (i + 1) * widthStep, (j + 1) * heightStep, hue);
                            break;
                    }
                }
            }

            for (int i = 0; i < IMG_FACTOR * IMG_FACTOR; ++i)
                threadPool[i].start();

            for (int i = 0; i < IMG_FACTOR * IMG_FACTOR; ++i)
                threadPool[i].join();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
