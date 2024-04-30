package com.example.cloudgazer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CaptureImageActivity is the central component of the Cloud Gazer Android application.
 * This activity handles all user interactions related to capturing images either through
 * a camera or from the gallery. Once an image is captured or selected, this activity
 * leverages a trained PyTorch model to classify the type of cloud in the image.
 * <p>
 * The activity is responsible for managing the camera and gallery intents, processing images
 * for model compatibility, and displaying classification results. It ensures permissions are
 * handled correctly, manages the lifecycle of image capture and selection, and utilizes the
 * ModelManager class to load and run inference with the PyTorch model. Results are then
 * processed and displayed in a user-friendly format.
 */
public class CaptureImageActivity extends AppCompatActivity implements CaptureImage {
    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private ImageView imageView;
    private Module model;

    /**
     * Initializes the activity with required UI components and permissions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);

        ViewPager2 viewPagerResults = findViewById(R.id.viewPagerResults);
        Button takePhotoButton = findViewById(R.id.button_take_photo);
        Button chooseFromGalleryButton = findViewById(R.id.button_choose_from_gallery);
        imageView = findViewById(R.id.imageView);


        loadModel();

        checkCameraPermission();

        takePhotoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            }
        });

        chooseFromGalleryButton.setOnClickListener(v -> {
            launchGalleryPicker();
        });
    }

    /**
     * Asynchronously loads the machine learning model from the application's assets.
     */
    @Override
    public void loadModel() {
        new Thread(() -> {
            try {
                model = Module.load(assetFilePath("model.ptl"));
            } catch (Exception e) {
                Toast.makeText(this, "Model couldn't be loaded", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).start();
    }

    /**
     * Retrieves the file path of an asset required by the application, particularly the ML model.
     *
     * @param assetName the name of the asset
     * @return the file path of the asset
     * @throws Exception if an error occurs while creating the file path
     */
    @Override
    public String assetFilePath(String assetName) throws Exception {
        File file = new File(getFilesDir(), assetName);
        if (!file.exists()) {
            try (InputStream is = getAssets().open(assetName); FileOutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            } catch (IOException e) {
                Log.e("CaptureImageActivity", "Error processing asset " + assetName + " to file path");
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * Checks if the application has the necessary permissions to use the camera and requests
     * permissions if not already granted.
     */
    @Override
    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Initiates an intent to capture an image using the device's camera.
     */
    @Override
    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Initiates an intent to select an image from the device's gallery.
     */
    @Override
    public void launchGalleryPicker() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        pickPhotoIntent.setType("image/*");
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    /**
     * Handles the results from requesting permissions.
     *
     * @param requestCode  the request code passed in requestPermissions(android.app.Activity, String[], int)
     * @param permissions  the requested permissions. Never null
     * @param grantResults the grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to use the camera.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Resizes and crops the given Bitmap image to a specified size and aspect ratio, typically
     * used to prepare images for model inference.
     *
     * @param original the original Bitmap image to be processed
     * @return a new bitmap image resized and cropped to 224x224 pixels
     */
    @Override
    public Bitmap resizeAndCropImage(Bitmap original) {
        int width = original.getWidth();
        int height = original.getHeight();
        float scale = 224.0f / Math.min(width, height);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original,
                (int) (width * scale), (int) (height * scale), true);

        // CenterCrop 224x224
        int xStart = (scaledBitmap.getWidth() - 224) / 2;
        int yStart = (scaledBitmap.getHeight() - 224) / 2;

        return Bitmap.createBitmap(scaledBitmap, xStart, yStart, 224, 224);
    }

    /**
     * Processes the result of an image capture or selection.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = null;
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                    break;
                case REQUEST_IMAGE_PICK:
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        imageView.setImageBitmap(imageBitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to read image from gallery.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            if (imageBitmap != null) {
                Bitmap processedImage = resizeAndCropImage(imageBitmap);
                imageView.setImageBitmap(processedImage);
                runInference(processedImage);
            }
        }
    }

    /**
     * Runs inference on a given Bitmap image using the preloaded model and processes the output
     * to generate predictions.
     *
     * @param bitmap the Bitmap image on which inference is to be performed.
     */
    @Override
    public void runInference(Bitmap bitmap) {
        if (model == null) {
            Toast.makeText(this, "Model is not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the image to a tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                new float[]{0.485f, 0.456f, 0.406f}, // ImageNet mean
                new float[]{0.229f, 0.224f, 0.225f}); // ImageNet std

        // Forward pass to get output tensor
        final Tensor outputTensor = model.forward(IValue.from(inputTensor)).toTensor();

        // Extract scores (model output)
        final float[] scores = outputTensor.getDataAsFloatArray();

        // Process the results
        processInferenceResults(scores);
    }

    /**
     * Applies the Softmax function to the raw output scores from the model to convert them
     * into probabilities, facilitating easier interpretation of results.
     *
     * @param scores the raw scores from the model output
     * @return an array of probabilities derived from the scores
     */
    @Override
    public float[] softmax(float[] scores) {
        float[] expScores = new float[scores.length];
        float sumExpScores = 0.0f;
        for (int i = 0; i < scores.length; i++) {
            expScores[i] = (float) Math.exp(scores[i]);
            sumExpScores += expScores[i];
        }
        for (int i = 0; i < scores.length; i++) {
            expScores[i] = expScores[i] / sumExpScores;
        }
        return expScores;
    }

    /**
     * Holds cloud type abbreviations mapped to their full names.
     * This is used for display and reference throughout the application.
     */
    private static final Map<String, String> CLOUD_ABBREVIATIONS = new LinkedHashMap<>();

    static {
        CLOUD_ABBREVIATIONS.put("Ci", "Cirrus");
        CLOUD_ABBREVIATIONS.put("Cs", "Cirrostratus");
        CLOUD_ABBREVIATIONS.put("Cc", "Cirrocumulus");
        CLOUD_ABBREVIATIONS.put("Ac", "Altocumulus");
        CLOUD_ABBREVIATIONS.put("As", "Altostratus");
        CLOUD_ABBREVIATIONS.put("Cu", "Cumulus");
        CLOUD_ABBREVIATIONS.put("Cb", "Cumulonimbus");
        CLOUD_ABBREVIATIONS.put("Ns", "Nimbostratus");
        CLOUD_ABBREVIATIONS.put("Sc", "Stratocumulus");
        CLOUD_ABBREVIATIONS.put("St", "Stratus");
        CLOUD_ABBREVIATIONS.put("Ct", "Contrail");
    }

    /**
     * Provides detailed descriptions for each cloud type, indexed by an integer.
     * The descriptions are used to provide informative output in the user interface.
     */
    private static final Map<Integer, String> CLOUD_DESCRIPTIONS = new HashMap<>();

    static {
        CLOUD_DESCRIPTIONS.put(0, "From Latin Altus, \"high\", cumulus, \"heaped\" \n\nAltocumulus is a middle-altitude cloud genus that belongs mainly to the stratocumuliform physical category characterized by globular masses or rolls in layers or patches, the individual elements being larger and darker than those of cirrocumulus and smaller than those of stratocumulus.");
        CLOUD_DESCRIPTIONS.put(1, "Altostratus is a middle-altitude cloud genus made up of water droplets, ice crystals, or a mixture of the two. Altostratus clouds are usually gray or blueish featureless sheets, although some variants have wavy or banded bases.");
        CLOUD_DESCRIPTIONS.put(2, "From Latin cumulus \"heaped\", and nimbus \"rainstorm\" \n\nCumulonimbus is a dense, towering vertical cloud, typically forming from water vapor condensing in the lower troposphere that builds upward carried by powerful buoyant air currents.");
        CLOUD_DESCRIPTIONS.put(3, "Cirrocumulus clouds are made up of lots of small white clouds called cloudlets, which are usually grouped together at high levels. Composed almost entirely from ice crystals, the little cloudlets are regularly spaced, often arranged as ripples in the sky.");
        CLOUD_DESCRIPTIONS.put(4, "Cirrus is a genus of high cloud made of ice crystals. Cirrus clouds typically appear delicate and wispy with white strands. Cirrus are usually formed when warm, dry air rises, causing water vapor deposition onto rocky or metallic dust particles at high altitudes. ");
        CLOUD_DESCRIPTIONS.put(5, "Cirrostratus are transparent high clouds, which cover large areas of the sky. They sometimes produce white or coloured rings, spots or arcs of light around the Sun or Moon, that are known as halo phenomena.");
        CLOUD_DESCRIPTIONS.put(6, "Contrails are clouds that form when water vapor condenses and freezes around small particles (aerosols) in aircraft exhaust.");
        CLOUD_DESCRIPTIONS.put(7, "From the Latin cumulus, meaning \"heap\" or \"pile\" \n\nCumulus clouds are clouds that have flat bases and are often described as puffy, cotton-like, or fluffy in appearance.");
        CLOUD_DESCRIPTIONS.put(8, "Nimbostratus are layered clouds with low bases that produce precipitation and are usually formed by advection. They are thick, dark gray with a ragged base, and are often associated with the passage of warm fronts.");
        CLOUD_DESCRIPTIONS.put(9, "Stratocumulus clouds are low-level clumps or patches of cloud varying in colour from bright white to dark grey. They are the most common clouds on earth recognised by their well-defined bases, with some parts often darker than others. ");
        CLOUD_DESCRIPTIONS.put(10, "Stratus clouds are low-level clouds characterized by horizontal layering with a uniform base, as opposed to convective or cumuliform clouds formed by rising thermals. The term stratus describes flat, hazy, featureless clouds at low altitudes varying in color from dark gray to nearly white.");
    }

    /**
     * A sorted and indexed collection of cloud types, allowing for ordered access based on model output.
     * The indices correspond to the sorted order of cloud type abbreviations.
     */
    private static final Map<Integer, String> CLOUD_TYPES = new HashMap<>();

    static {
        // Sort and index cloud type abbreviations for ordering purposes
        List<String> sortedKeys = new ArrayList<>(CLOUD_ABBREVIATIONS.keySet());
        Collections.sort(sortedKeys);
        for (int i = 0; i < sortedKeys.size(); i++) {
            String key = sortedKeys.get(i);
            CLOUD_TYPES.put(i, CLOUD_ABBREVIATIONS.get(key));
        }
    }

    /**
     * Processes inference results by converting raw scores to probabilities and sorting them to
     * identify the most likely cloud types. the results are then displayed to the user.
     *
     * @param scores array of raw scores from the model inference
     */
    private void processInferenceResults(float[] scores) {

        if (scores == null || scores.length < 3) {
            Toast.makeText(this, "Not enough results to display", Toast.LENGTH_SHORT).show();
            return;
        }

        // Apply softmax to convert raw scores to probabilities
        float[] probabilities = softmax(scores);

        // Create an array of indices to sort scores in descending order
        Integer[] indices = new Integer[scores.length];
        for (int i = 0; i < scores.length; i++) {
            indices[i] = i;
        }

        // Sort indices based on the scores (in descending order)
        Arrays.sort(indices, (a, b) -> Float.compare(scores[b], scores[a]));

        // Prepare the top 3 results for display
        String[] results = new String[3];
        String[] descriptions = new String[3];
        for (int i = 0; i < 3; i++) {
            int idx = indices[i];
            String cloudType = CLOUD_TYPES.getOrDefault(idx, "Unknown Cloud Type");
            String cloudDescription = CLOUD_DESCRIPTIONS.getOrDefault(idx, "No description available.");
            String probabilityPercent = String.format("%.2f", probabilities[idx] * 100) + "%";
            results[i] = "Rank " + (i + 1) + "\nIt might be " + cloudType + " cloud( Probability  " + probabilityPercent + ")\n";
            descriptions[i] = cloudDescription;
        }

        // Update the ViewPager with the results on the main thread
        runOnUiThread(() -> {
            ViewPager2 viewPagerResults = findViewById(R.id.viewPagerResults);
            viewPagerResults.setAdapter(new ResultsPagerAdapter(results, descriptions));
        });
    }


}


