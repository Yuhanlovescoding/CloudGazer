package com.example.cloudgazer;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * This class performs integration tests on the CaptureImageActivity to ensure
 * that the machine learning model and asset files are properly loaded and accessible
 * in the Android environment.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CaptureImageActivityAndroidTest {

    /**
     * Tests that the machine learning model is loaded without any exceptions.
     * This test assumes that the loadModel method is implemented correctly in the
     * CaptureImageActivity and that the model file is correctly placed in the assets folder.
     */
    @Test
    public void testLoadModel() {
        try (ActivityScenario<CaptureImageActivity> scenario = ActivityScenario.launch(CaptureImageActivity.class)) {
            scenario.onActivity(activity -> {
                try {
                    activity.loadModel();
                    assertTrue(true);
                } catch (Exception e) {
                    fail(e.getMessage());
                }
            });
        }
    }

    /**
     * Tests the functionality of retrieving the file path of an asset in the Android environment.
     * It ensures that the asset file path is not null and that the file actually exists in the
     * specified directory. This is critical for assets such as machine learning models.
     */
    @Test
    public void testAssetFilePath() {

        try (ActivityScenario<CaptureImageActivity> scenario = ActivityScenario.launch(CaptureImageActivity.class)) {
            scenario.onActivity(activity -> {

                String assetName = "model.ptl";

                try {
                    String filePath = activity.assetFilePath(assetName);
                    assertNotNull(filePath);

                    File file = new File(filePath);
                    assertTrue(file.exists());

                } catch (Exception e) {
                    fail(e.getMessage());
                }
            });
        }
    }

}

