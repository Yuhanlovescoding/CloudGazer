package com.example.cloudgazer;

import android.graphics.Bitmap;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This class performs unit tests on the CaptureImageActivity using Robolectric,
 * enabling Android framework dependencies to be managed within the JVM.
 * It verifies the functionality of image resizing, cropping and softmax computation.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class CaptureImageActivityTest {

    /**
     * Tests the resizeAndCropImage method to ensure it correctly resizes and crops an image.
     * This method verifies that the input Bitmap is resized and cropped to the specific dimensions (224x224).
     */
    @Test
    public void testResizeAndCropImage() {
        // Create a simulated Bitmap
        Bitmap original = Bitmap.createBitmap(500, 300, Bitmap.Config.ARGB_8888);

        CaptureImageActivity activity = new CaptureImageActivity();

        Bitmap resized = activity.resizeAndCropImage(original);


        assertNotNull(resized);
        assertEquals(224, resized.getWidth());
        assertEquals(224, resized.getHeight());
    }

    /**
     * Tests the softmax function to ensure it correctly computes the probabilities from given scores.
     * It verifies the computed probabilities against manually calculated expected values,
     * taking into consideration typical floating-point precision errors.
     */
    @Test
    public void testSoftmaxFunction() {

        CaptureImageActivity activity = new CaptureImageActivity();

        // Define input scores and manually calculated expected probabilities
        float[] scores = {1.0f, 2.0f, 3.0f};
        float[] expectedProbabilities = {0.09003f, 0.24473f, 0.66524f};
        float[] probabilities = activity.softmax(scores);

        // Check each element for correctness within an allowed error margin
        assertNotNull(probabilities);
        assertEquals(3, probabilities.length);  // Ensure the output array is of the correct length
        for (int i = 0; i < probabilities.length; i++) {
            assertEquals("Probability at index " + i + " did not match expected value",
                    expectedProbabilities[i], probabilities[i], 0.0001);
        }

        // Check that the sum of probabilities is 1
        float sum = 0.0f;
        for (float prob : probabilities) {
            sum += prob;
        }
        assertEquals("Sum of probabilities should be 1", 1.0f, sum, 0.001);
    }
}

