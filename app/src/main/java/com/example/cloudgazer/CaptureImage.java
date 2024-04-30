package com.example.cloudgazer;

import android.graphics.Bitmap;

/**
 * The CaptureImage interface defines the operations necessary for capturing and processing
 * images in the Cloud Gazer application. It includes methods for model loading, file handling,
 * camera and gallery interactions, image processing, and running inference using a neural network model.
 */
public interface CaptureImage {
    /**
     * Asynchronously loads the machine learning model from the application's assets.
     */
    void loadModel();

    /**
     * Retrieves the file path of an asset required by the application, particularly the ML model.
     *
     * @param assetName the name of the asset file
     * @return the file path of the asset
     * @throws Exception if there is an error accessing the asset
     */
    String assetFilePath(String assetName) throws Exception;

    /**
     * Checks if the application has the necessary permissions to use the camera and requests
     * permissions if not already granted.
     */
    void checkCameraPermission();

    /**
     * Initiates an intent to capture an image using the device's camera.
     */
    void launchCamera();

    /**
     * Initiates an intent to select an image from the device's gallery.
     */
    void launchGalleryPicker();

    /**
     * Resizes and crops the given Bitmap image to a specified size and aspect ratio, typically
     * used to prepare images for model inference.
     *
     * @param original the original Bitmap image to be processed.
     * @return a resized and cropped Bitmap image.
     */
    Bitmap resizeAndCropImage(Bitmap original);

    /**
     * Runs inference on a given Bitmap image using the preloaded model and processes the output
     * to generate predictions.
     *
     * @param bitmap the Bitmap image on which inference is to be performed
     */
    void runInference(Bitmap bitmap);

    /**
     * Applies the Softmax function to the raw output scores from the model to convert them
     * into probabilities, facilitating easier interpretation of results.
     *
     * @param scores the raw scores from the model output
     * @return an array of probabilities derived from the scores
     */
    float[] softmax(float[] scores);
}
