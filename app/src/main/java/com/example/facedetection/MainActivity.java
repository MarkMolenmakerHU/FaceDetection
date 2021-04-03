package com.example.facedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.facedetection.Helper.GraphicOverlay;
import com.example.facedetection.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;


import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    private Button detectButton;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graphicOverlay = findViewById(R.id.graphic_overlay);
        detectButton = findViewById(R.id.detect_button);
        cameraView = findViewById(R.id.camera_view);
        textView = findViewById(R.id.text_view);

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                processFaceDetection(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    private void processFaceDetection(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setMinFaceSize(0.1f)
                .build();
        FaceDetector faceDetector = FaceDetection.getClient(faceDetectorOptions);

        Task<List<Face>> result =
                faceDetector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onSuccess(List<Face> faces) {

                                        for (Face face : faces) {
                                            Rect face_rect = face.getBoundingBox();
                                            PointF left_eye = Objects.requireNonNull(face.getLandmark(FaceLandmark.LEFT_EYE)).getPosition();
                                            PointF right_eye = Objects.requireNonNull(face.getLandmark(FaceLandmark.RIGHT_EYE)).getPosition();

                                            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, face_rect, left_eye, right_eye);
                                            graphicOverlay.add(rectOverlay);
                                            textView.setText(calcDistance(left_eye.x, right_eye.x));
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

    private String calcDistance(float lft, float rght) {
        double distance;
        distance = 300 / (rght - lft) * 30;
        return String.format("Distance: %.1fcm\nPixels: ( %.3f , %.3f )", distance, lft, rght);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

}