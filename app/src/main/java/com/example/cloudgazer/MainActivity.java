package com.example.cloudgazer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.widget.Button;

/**
 * MainActivity serves as the entry point of the Cloud Gazer app.
 * It sets up the UI to handle edge-to-edge display and provides a button to navigate
 * to the CaptureImageActivity where the main functionality resides.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Initializes the activity with edge-to-edge support and sets up the main layout.
     * It also applies appropriate padding to handle system bars.
     *
     * @param savedInstanceState if the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it
     *                           most recently supplied in {@link #onSaveInstanceState}.
     *                           <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables edge-to-edge display to allow the UI to extend into the window's decor area.
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply insets for system bars to avoid UI overlapping with system UI such as the status bar.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup the button that starts CaptureImageActivity on click.
        Button uploadButton = findViewById(R.id.button);
        uploadButton.setOnClickListener(v -> startActivity(createCaptureImageIntent()));
    }

    // Intent to start CaptureImageActivity
    Intent createCaptureImageIntent() {
        return new Intent(MainActivity.this, CaptureImageActivity.class);
    }
}
