package com.example.nath.appgroup.AlgorithmThread;

import com.example.nath.appgroup.Image;

public class AlgorithmThread {
    private final int IMG_FACTOR = 3;
    private final int ALGORITHM_TO_GRAY = 10;
    private final int ALGORITHM_SPHERES = 11;

    private Image imgPointer;
    private Image img;
    private Thread[] threadPool;
    private int algo;
    private int radius;

    public AlgorithmThread(Image img, int algo, int radius) {
        this.imgPointer = img;
        this.img = img.clone();
        this.algo = algo;
        this.radius = radius;
    }

    public void run() {
        int width = img.getWidth();
        int height = img.getHeight();

        try {
            threadPool = new Thread[IMG_FACTOR * IMG_FACTOR];

            int widthStep = width / IMG_FACTOR;
            int heightStep = height / IMG_FACTOR;

            int count = 0;

            for (int i = 0; i < IMG_FACTOR; ++i) {
                for (int j = 0; j < IMG_FACTOR; ++j) {
                    switch (algo) {
                        case ALGORITHM_TO_GRAY:
                            threadPool[count] = new AlgorithmThreadToGray(img,
                                    i * widthStep, j * heightStep, (i + 1) * widthStep, (j + 1) * heightStep);
                            break;
                        case ALGORITHM_SPHERES:
                            threadPool[count] = new AlgorithmThreadColorFilter(img,
                                    i * widthStep, j * heightStep, (i + 1) * widthStep, (j + 1) * heightStep, radius);
                            break;
                    }
                    ++count;
                }
            }

            for (int i = 0; i < IMG_FACTOR * IMG_FACTOR; ++i)
                threadPool[i].start();

            for (int i = 0; i < IMG_FACTOR * IMG_FACTOR; ++i)
                threadPool[i].join();

            imgPointer.setPixels(img.getPixels(0, 0, img.getWidth(), img.getHeight()),
                    0, 0, img.getWidth(), img.getHeight());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
