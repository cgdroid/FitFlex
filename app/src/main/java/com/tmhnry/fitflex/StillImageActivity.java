/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tmhnry.fitflex;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tmhnry.fitflex.databinding.ActivityStillImageBinding;
import com.tmhnry.fitflex.labeldetector.LabelDetectorProcessor;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.Belly;
import com.tmhnry.fitflex.vision.BitmapUtils;
import com.tmhnry.fitflex.vision.VisionImageProcessor;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@KeepName
public final class StillImageActivity extends AppCompatActivity {

    ActivityStillImageBinding b;

    private static final int PERMISSION_REQUESTS = 1;
    private static final String TAG = "StillImageActivity";

    private static final String IMAGE_LABELING_CUSTOM = "Custom Image Labeling";

    private static final String SIZE_SCREEN = "Width:Screen"; // Match screen width
    private static final String SIZE_1024_768 = "Width:1024"; // ~1024*768 in a normal ratio
    private static final String SIZE_640_480 = "Width:640"; // ~640*480 in a normal ratio

    private static final String KEY_IMAGE_URI = "example.fitflex.KEY_IMAGE_URI";
    private static final String KEY_SELECTED_SIZE = "example.fitflex.KEY_SELECTED_SIZE";
    private static String model = "body_part";

    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private static final int REQUEST_CHOOSE_IMAGE = 1002;

    boolean isLandScape;

    private String selectedMode = IMAGE_LABELING_CUSTOM;
    private String selectedSize = SIZE_SCREEN;

    private int progress = 0;
    private Uri imageUri;
    private int imageMaxWidth;
    private int imageMaxHeight;
    private int bellyBodySize;
    private int bellyTypeSize;

    private float w0 = 0.45f;
    private float w1 = 0.55f;

    private float iw0;
    private float iw1;

    private float a0, a1, b0, b1, cba, cbb, cia, cib, caa, cab;
    private List<Float> confidences;
    private Dialog dAlert;
    private TextView vTit;
    private TextView vMes;
    private TextView vPos;
    private TextView vNeg;
    private List<String> bel1;
    private List<Float> con1;

    private VisionImageProcessor imageProcessor;

    @Override
    public void onBackPressed() {
        if ((boolean) MyPreferences.load(StillImageActivity.this, MyPreferences.PREF_HAS_BELLY)) {
            startActivity(new Intent(StillImageActivity.this, MainActivity.class));
            finish();
        }
    }

    private float interpolate(float rangeVal, float rangeMax) {
        return (rangeVal) / rangeMax;
    }

    private Belly.Type computeType() {
        if (isMax(cba)) {
            return Belly.Type.BA;
        }
        if (isMax(cbb)) {
            return Belly.Type.BB;
        }
        if (isMax(cia)) {
            return Belly.Type.IA;
        }
        if (isMax(cib)) {
            return Belly.Type.IB;
        }
        if (isMax(caa)) {
            return Belly.Type.AA;
        }
        return Belly.Type.AB;
    }

    private boolean isMax(float val) {
        boolean isMax;
        if (confidences == null) {
            confidences = new ArrayList<Float>();
            confidences.add(cba);
            confidences.add(cbb);
            confidences.add(cia);
            confidences.add(cib);
            confidences.add(caa);
            confidences.add(cab);
        }
        for (Float confidence : confidences) {
            if (confidence != val) {
                isMax = !(confidence > val);
                if (!isMax) {
                    return false;
                }
            }
        }
        return true;
    }

    private void analyzeFrontBelly() {
        if (0.90 < a0 && a0 < 0.95) {
            float ave = (0.90f + 0.95f) / 2;
            float d1 = Math.abs(a0 - ave);
            float r1 = 0.95f - ave;
            cba += interpolate(d1, r1) * iw1;
            return;
        }
        if (0.95 < a0 && a0 < 1.05) {
            float ave = (0.95f + 1.05f) / 2;
            float d1 = Math.abs(a0 - ave);
            float r1 = 1.05f - ave;
            cbb += interpolate(d1, r1) * iw1;
            return;
        }
        if (0.90 < a1 && a1 < 0.95) {
            float ave = (0.90f + 0.95f) / 2;
            float d1 = Math.abs(a1 - ave);
            float r1 = 0.95f - ave;
            caa += interpolate(d1, r1) * iw1;
            return;
        }
        if (0.95 < a1 && a1 < 1.05) {
            float ave = (0.95f + 1.05f) / 2;
            float d1 = Math.abs(a1 - ave);
            float r1 = 1.05f - ave;
            cab += interpolate(d1, r1) * iw1;
            return;
        }
        float v1 = Math.max(a0, a1) - 0.5f;
        if (0.0f < v1 && v1 < 0.2f) {
            float ave = (0.2f + 0.0f) / 2;
            float d1 = Math.abs(v1 - ave);
            float r1 = 0.2f - ave;
            cia += interpolate(d1, r1) * iw1;
            return;
        }
        float ave = (0.4f + 0.2f) / 2;
        float d1 = Math.abs(v1 - ave);
        float r1 = 0.4f - ave;
        cib += interpolate(d1, r1) * iw1;
    }

    private void analyzeSideBelly() {
        if (0.90 < b0 && b0 < 0.95) {
            float ave = (0.90f + 0.95f) / 2;
            float d1 = Math.abs(b0 - ave);
            float r1 = 0.95f - ave;
            cba += interpolate(d1, r1) * iw1;
            return;
        }
        if (0.95 < b0 && b0 < 1.05) {
            float ave = (0.95f + 1.05f) / 2;
            float d1 = Math.abs(b0 - ave);
            float r1 = 1.05f - ave;
            cbb += interpolate(d1, r1) * iw1;
            return;
        }
        if (0.90 < b1 && b1 < 0.95) {
            float ave = (0.90f + 0.95f) / 2;
            float d1 = Math.abs(b1 - ave);
            float r1 = 0.95f - ave;
            caa += interpolate(d1, r1) * iw1;
            return;
        }
        if (0.95 < b1 && b1 < 1.05) {
            float ave = (0.95f + 1.05f) / 2;
            float d1 = Math.abs(b1 - ave);
            float r1 = 1.05f - ave;
            cab += interpolate(d1, r1) * iw1;
            return;
        }
        float v1 = Math.max(b0, b1) - 0.5f;
        if (0.0f < v1 && v1 < 0.2f) {
            float ave = (0.2f + 0.0f) / 2;
            float d1 = Math.abs(v1 - ave);
            float r1 = 0.2f - ave;
            cia += interpolate(d1, r1) * iw1;
            return;
        }
        float ave = (0.4f + 0.2f) / 2;
        float d1 = Math.abs(v1 - ave);
        float r1 = 0.4f - ave;
        cib += interpolate(d1, r1) * iw1;
    }

    private void onSave() {
        float max = Math.max(w0, w1);
        float dis = Math.abs(max - 0.5f);

        cia += interpolate(dis, 0.5f) * iw0;
        cib += interpolate(dis, 0.5f) * iw0;

        cba += w0 * iw0;
        cbb += w0 * iw0;

        caa += w1 * iw0;
        cab += w1 * iw0;

        analyzeFrontBelly();
        analyzeSideBelly();

        clear();

        MyPreferences.save(this, MyPreferences.PREF_HAS_BELLY, true);
        MyPreferences.save(this, MyPreferences.PREF_BELLY_TYPE, computeType().getCode());

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 3000);
    }

    private void onStartProcess() {
        b.txtSelectImageAgain.setVisibility(View.INVISIBLE);
        b.sizeSelector.setVisibility(View.INVISIBLE);
        b.btnSelectImg.setVisibility(View.INVISIBLE);
        b.btnLoading.setVisibility(View.VISIBLE);
        onFirstProcess();
    }

    private void clear() {
        imageProcessor.stop();
        imageProcessor = null;
    }

    private void onFirstProcess() {
        model = "body_part";
        createImageProcessor();
        tryReloadAndDetectInImage();

        int waitDuration = new Random(4).nextInt(6);

        new Handler().postDelayed(() -> {
            List<ImageLabel> labels = ((LabelDetectorProcessor) imageProcessor).getImageLabels();
            if (labels == null) {
                b.btnSelectImg.setVisibility(View.VISIBLE);
                b.btnLoading.setVisibility(View.INVISIBLE);
                b.txtSelectImageAgain.setVisibility(View.VISIBLE);
                b.sizeSelector.setVisibility(View.VISIBLE);

                vTit.setText("Processing Failed");
                vMes.setText("We cannot process the provided image. Please restart the process or provide another image.");
                vPos.setText("Okay");

                vPos.setOnClickListener(view -> {
                    dAlert.dismiss();
                });
                dAlert.show();
                return;
            }

            if (!labels.get(0).getText().equals("Belly") || labels.get(0).getText().isEmpty()) {
                b.btnSelectImg.setVisibility(View.VISIBLE);
                b.btnLoading.setVisibility(View.INVISIBLE);
                b.txtSelectImageAgain.setVisibility(View.VISIBLE);
                b.sizeSelector.setVisibility(View.VISIBLE);

                vTit.setText("Invalid Image");
                vMes.setText("The image provided is not a belly. Please provide another image.");
                vPos.setText("Okay");

                vPos.setOnClickListener(view -> {
                    dAlert.dismiss();
                });

                dAlert.show();
                return;
            }
            onSecondProcess();
        }, waitDuration * 1000);
    }

    private void onSecondProcess() {
        model = "belly_type";
        createImageProcessor();
        tryReloadAndDetectInImage();

        new Handler().postDelayed(() -> {
            LabelDetectorProcessor processor = (LabelDetectorProcessor) imageProcessor;

            if (processor.getImageLabels() == null) {
                return;
            }

            if (progress == 0) {
                ImageLabel l0 = processor.getImageLabels().get(0);
                ImageLabel l1 = processor.getImageLabels().get(1);

                if (l0.getText().equals("Beginner")) {
                    a0 = l0.getConfidence();
                    a1 = l1.getConfidence();
                }

                if (l0.getText().equals("Advanced")) {
                    a1 = l0.getConfidence();
                    a0 = l1.getConfidence();
                }
            }

            if (progress == 1) {
                ImageLabel l0 = processor.getImageLabels().get(0);
                ImageLabel l1 = processor.getImageLabels().get(1);

                if (l0.getText().equals("Beginner")) {
                    b0 = l0.getConfidence();
                    b1 = l1.getConfidence();
                }

                if (l0.getText().equals("Advanced")) {
                    b1 = l0.getConfidence();
                    b0 = l1.getConfidence();
                }
            }

            progress += 1;
            b.stepperIndicator.setCurrentStep(progress);

            b.btnSelectImg.setVisibility(View.VISIBLE);
            b.btnLoading.setVisibility(View.INVISIBLE);
            b.txtSelectImageAgain.setVisibility(View.VISIBLE);
            b.sizeSelector.setVisibility(View.VISIBLE);

            if (progress == 2) {
                dAlert.setCancelable(false);

                vTit.setText("Validation");
                vMes.setText("Thank you for taking the time to take images of your belly. No image will be saved in the database and thus will be discarded after this process. \n\nDo you want to restart the process or proceed and start working out?");

                vPos.setText("Work Out");

                vPos.setOnClickListener(view -> {
                    dAlert.dismiss();

                    b.btnSelectImg.setVisibility(View.INVISIBLE);
                    b.btnLoading.setVisibility(View.VISIBLE);

                    onSave();
                });

                vNeg.setVisibility(View.VISIBLE);
                vNeg.setText("Restart");

                vNeg.setOnClickListener(view -> {
                    progress = 0;
                    b.stepperIndicator.setCurrentStep(0);
                    vNeg.setVisibility(View.INVISIBLE);
                    dAlert.dismiss();
                });

                dAlert.show();
            }
        }, 3000);
    }


    private static boolean isPermissionGranted(Context context, String permission) {

        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }

        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = ActivityStillImageBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.imgSkip.setVisibility(View.VISIBLE);

        Tools.hideSystemBars(this);

        dAlert = new Dialog(this);

        dAlert.setContentView(R.layout.dialog_alert_notification);

        dAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vPos = dAlert.findViewById(R.id.txt_positive);
        vNeg = dAlert.findViewById(R.id.txt_negative);
        vMes = dAlert.findViewById(R.id.txt_alert_message);
        vTit = dAlert.findViewById(R.id.dialog_title);

        b.imgSkip.setOnClickListener(view -> {
            onSkip();
        });

        a0 = 0f;
        a1 = 0f;
        b0 = 0f;
        b1 = 0f;
        cba = 0f;
        cbb = 0f;
        cia = 0f;
        cib = 0f;
        caa = 0f;
        cab = 0f;

        bellyBodySize = 200;
        bellyTypeSize = 176;

        iw0 = (bellyBodySize * 1f) / (bellyBodySize + bellyTypeSize);
        iw1 = (bellyTypeSize * 1f) / (bellyBodySize + bellyTypeSize);

        vNeg.setVisibility(View.INVISIBLE);

        String[] d = {"Front", "Side", "Next"};

        b.stepperIndicator.setLabels(d);
        b.stepperIndicator.setIndicatorColor(getColor(R.color.stepper_indicator));
        b.stepperIndicator.setLabelColor(getColor(R.color.stepper_label));

        b.stepperIndicator.setCurrentStep(0);

        b.btnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectImage(view);
            }
        });

        populateFeatureSelector();

        populateSizeSelector();

        isLandScape =
                (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI);
            selectedSize = savedInstanceState.getString(KEY_SELECTED_SIZE);
        }

        b.getRoot()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                b.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                imageMaxWidth = b.getRoot().getWidth();
                                imageMaxHeight = b.getRoot().getHeight() - findViewById(R.id.control).getHeight();
                                if (SIZE_SCREEN.equals(selectedSize)) {
                                    tryReloadAndDetectInImage();
                                }
                            }
                        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    private void onSkip() {
        dAlert.setCancelable(true);

        vTit.setText("Skip Activity");
        vMes.setText("Do you want to skip this process?\n\nSkipping will lose the current progress and you will be asked to do the process again from the beginning next time.\n\nDo you want to continue?");

        vPos.setText("Skip");

        vPos.setOnClickListener(view -> {
            dAlert.dismiss();
            b.txtSelectImageAgain.setVisibility(View.INVISIBLE);
            b.sizeSelector.setVisibility(View.INVISIBLE);
            b.btnSelectImg.setVisibility(View.INVISIBLE);
            b.btnLoading.setVisibility(View.VISIBLE);
            b.imgSkip.setVisibility(View.INVISIBLE);
            MyPreferences.save(StillImageActivity.this, MyPreferences.PREF_HAS_BELLY, true);
            new Handler().postDelayed(() -> {
                startActivity(new Intent(StillImageActivity.this, MainActivity.class));
                finish();
            }, 2000);
        });

        vNeg.setVisibility(View.VISIBLE);
        vNeg.setText("Cancel");

        vNeg.setOnClickListener(view -> {
            vNeg.setVisibility(View.INVISIBLE);
            dAlert.dismiss();
        });

        dAlert.show();
    }

    //
    private void onSelectImage(View view) {

        PopupMenu popup = new PopupMenu(StillImageActivity.this, view);
        popup.setOnMenuItemClickListener(
                menuItem -> {
                    int itemId = menuItem.getItemId();
                    if (itemId == R.id.select_images_from_local) {
                        startChooseImageIntentForResult();
                        return true;
                    } else if (itemId == R.id.take_photo_using_camera) {
                        startCameraIntentForResult();
                        return true;
                    }
                    return false;
                });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.camera_button_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createImageProcessor();
        tryReloadAndDetectInImage();
    }

    private void populateFeatureSelector() {
        List<String> options = new ArrayList<>();
        options.add(IMAGE_LABELING_CUSTOM);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void populateSizeSelector() {
        List<String> options = new ArrayList<>();

        options.add(SIZE_SCREEN);
        options.add(SIZE_1024_768);
        options.add(SIZE_640_480);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        b.sizeSelector.setAdapter(dataAdapter);
        b.sizeSelector.setOnItemSelectedListener(
                new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                        selectedSize = parentView.getItemAtPosition(pos).toString();
                        createImageProcessor();
                        tryReloadAndDetectInImage();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_IMAGE_URI, imageUri);
        outState.putString(KEY_SELECTED_SIZE, selectedSize);
    }

    private void startCameraIntentForResult() {
        // Clean up last time's image
        imageUri = null;
        b.preview.setImageBitmap(null);
        Toast.makeText(StillImageActivity.this, "Starting Camera...", Toast.LENGTH_LONG).show();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void startChooseImageIntentForResult() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            b.btnSelectImg.setText("Analyze");

            b.btnSelectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStartProcess();
                }
            });

            b.txtSelectImageAgain.setVisibility(View.VISIBLE);
            b.txtSelectImageAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSelectImage(view);
                }
            });
            b.sizeSelector.setVisibility(View.VISIBLE);
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            imageUri = data.getData();
            b.btnSelectImg.setText("Analyze");

            b.btnSelectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStartProcess();
                }
            });

            b.txtSelectImageAgain.setVisibility(View.VISIBLE);
            b.txtSelectImageAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSelectImage(view);
                }
            });
            b.sizeSelector.setVisibility(View.VISIBLE);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void tryReloadAndDetectInImage() {
        Log.d(TAG, "Try reload and detect image");
        try {
            if (imageUri == null) {
                return;
            }

            if (SIZE_SCREEN.equals(selectedSize) && imageMaxWidth == 0) {
                // UI layout has not finished yet, will reload once it's ready.
                return;
            }

            Bitmap imageBitmap = BitmapUtils.getBitmapFromContentUri(getContentResolver(), imageUri);
            if (imageBitmap == null) {
                return;
            }

            b.graphicOverlay.clear();

            // Get the dimensions of the image view
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) imageBitmap.getWidth() / (float) targetedSize.first,
                            (float) imageBitmap.getHeight() / (float) targetedSize.second);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            imageBitmap,
                            (int) (imageBitmap.getWidth() / scaleFactor),
                            (int) (imageBitmap.getHeight() / scaleFactor),
                            true);

            b.preview.setImageBitmap(resizedBitmap);

            if (imageProcessor != null) {
                b.graphicOverlay.setImageSourceInfo(
                        resizedBitmap.getWidth(), resizedBitmap.getHeight(), /* isFlipped= */ false);
                imageProcessor.processBitmap(resizedBitmap, b.graphicOverlay);
            } else {
                Log.e(TAG, "Null imageProcessor, please check adb logs for imageProcessor creation error");
            }

        } catch (IOException e) {
            Log.e(TAG, "Error retrieving saved image");
            imageUri = null;
        }
    }

    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;

        switch (selectedSize) {
            case SIZE_SCREEN:
                targetWidth = imageMaxWidth;
                targetHeight = imageMaxHeight;
                break;
            case SIZE_640_480:
                targetWidth = isLandScape ? 640 : 480;
                targetHeight = isLandScape ? 480 : 640;
                break;
            case SIZE_1024_768:
                targetWidth = isLandScape ? 1024 : 768;
                targetHeight = isLandScape ? 768 : 1024;
                break;
            default:
                throw new IllegalStateException("Unknown size");
        }

        return new Pair<>(targetWidth, targetHeight);
    }

    private void createImageProcessor() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("custom_models/model_");
            sb.append(model);
            sb.append(".tflite");
            switch (selectedMode) {
                case IMAGE_LABELING_CUSTOM:
                    Log.i(TAG, "Using Custom Image Label Detector Processor");
                    LocalModel localClassifier =
                            new LocalModel.Builder()
                                    .setAssetFilePath(sb.toString())
//                                    .setAssetFilePath("custom_models/model_body_part.tflite")
                                    .build();
                    CustomImageLabelerOptions customImageLabelerOptions =
                            new CustomImageLabelerOptions.Builder(localClassifier).build();
                    imageProcessor = new LabelDetectorProcessor(this, customImageLabelerOptions);
                    break;
                default:
                    Log.e(TAG, "Unknown selectedMode: " + selectedMode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: " + selectedMode, e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
}
