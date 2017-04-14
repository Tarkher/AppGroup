package com.example.nath.appgroup.AlgorithmThread;

import com.example.nath.appgroup.Image;

/**
 * <b>AlgorithmThread manages the thread pool that works on the image</b>
 * <p>
 * This class is not designed to be manipulated by user. The only usage is to create an object with
 * the right input and call the method run.
 * </p>
 *
 * @author Maxime Romeas Nathan Castets Aziz Fouche
 * @version 3.0
 */

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

    /**
     * Constructor which creates object with algorithm on img
     *
     * @param img
     * The image we work on
     *
     * @param algorithm
     * The algorithm identifier, defined above.
     *
     * @param input
     * Contains inputs that are specifics to each algorithm.
     * @since 1.0
     */
    public AlgorithmThread(Image img, int algorithm, Object[] input) {
        this.imgPointer = img;
        this.img = img.clone();
        this.algorithm = algorithm;
        this.input = input;
    }

    /**
     * Start all thread that will execute algorithm on different area of the image
     *
     * @since 1.0
     */
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
