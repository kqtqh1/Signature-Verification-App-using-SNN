package com.example.signatureverification;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.opencv.core.Mat;
import org.opencv.android.Utils;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.DataType;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.yalantis.ucrop.UCrop;

import org.opencv.android.OpenCVLoader;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //initialize variables
    Button captureBtn1, selectBtn1,selectBtn2,captureBtn2, btnVerify, btnReset;
    ConstraintLayout resultCard;
    TextView tvResult;
    ImageView imageView1, imageView2;
    ProgressBar progressBar;
    View progBarContainer;
    ScrollView scroll;
    Bitmap signature1, signature2;
    Uri camUri;
    ActivityResultLauncher<Intent> Launcher;
    int ucropRequestCode1= 105;
    int ucropRequestCode2= 106;
    int currentReqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         //check if opencv is loaded
        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV successfully loaded.");
        }

        //initialize views
        imageView1= findViewById(R.id.image1);
        imageView2= findViewById(R.id.image2);
        captureBtn1= findViewById(R.id.capture1);
        selectBtn1=findViewById(R.id.select1);
        captureBtn2 = findViewById(R.id.capture2);
        selectBtn2= findViewById(R.id.select2);
        btnVerify = findViewById(R.id.verify);
        tvResult=findViewById(R.id.tvResult);
        progressBar = findViewById(R.id.progressBar);
        btnReset= findViewById(R.id.reset);
        resultCard=findViewById(R.id.resultCard);
        progBarContainer= findViewById(R.id.progBarCont);
        scroll= findViewById(R.id.scroll);

        permissions();
        captureBtn1.setOnClickListener(v -> openCamera(101));
        selectBtn1.setOnClickListener(v -> openGallery(102));
        captureBtn2.setOnClickListener(v -> openCamera(103));
        selectBtn2.setOnClickListener(v -> openGallery(104));

        btnVerify.setOnClickListener(v-> {
            verifySignatures();
            //hide & disable buttons after verifying signatures and show result
            if(signature1 != null && signature2 != null){
                resultCard.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                btnVerify.setAlpha(0.5f);
                btnVerify.setEnabled(false);
                captureBtn2.setAlpha(0.5f);
                captureBtn1.setAlpha(0.5f);
                selectBtn1.setAlpha(0.5f);
                selectBtn2.setAlpha(0.5f);
                captureBtn2.setEnabled(false);
                selectBtn2.setEnabled(false);
                captureBtn1.setEnabled(false);
                selectBtn1.setEnabled(false);

                //scroll to the result card
                resultCard.post(() -> {
                    int[] location = new int[2];
                    resultCard.getLocationOnScreen(location);
                    int y = location[1];
                    scroll.smoothScrollTo(0, y);
                });
            }else{
                String msg= "Please upload or take photo of both signatures.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
        btnReset.setOnClickListener(v -> resetUI());

        //handle result activity of camera and gallery intent
        Launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //for camera
                        if (currentReqCode == 101 || currentReqCode == 103) {
                            crop(camUri, currentReqCode == 101 ? ucropRequestCode1 : ucropRequestCode2);
                        //for gallery
                        }else if (currentReqCode == 102 || currentReqCode == 104) {
                            Uri imageUri = result.getData() != null ? result.getData().getData() : null;
                            if (imageUri != null) {
                                crop(imageUri, currentReqCode == 102 ? ucropRequestCode1 : ucropRequestCode2);
                            }
                        }
                    } else {
                        Log.e("Intent", "Action failed.");
                    }
                }
        );
    }
    //methods to open camera
    private void openCamera(int requestCode) {
        currentReqCode = requestCode;
        Intent cam_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //temporarily store the captured image in the external cache directory
        File photoFile = new File(getExternalCacheDir(), "cameraImg.jpg");
        camUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

        cam_intent.putExtra(MediaStore.EXTRA_OUTPUT, camUri);
        cam_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cam_intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Launcher.launch(cam_intent);
    }

    //method to open gallery
    private void openGallery(int requestCode) {
        currentReqCode =requestCode;
        Intent gallery_intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Launcher.launch(gallery_intent);
    }

    //crop the image after uploading/taking pics using Ucrop library
    private void crop(@NonNull Uri uri, int requestCode) {
        try {
            String filename = "croppedImage.jpg";
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), filename));
            //configure the options
            UCrop.Options options = new UCrop.Options();
            options.setCompressionQuality(90);
            options.setFreeStyleCropEnabled(true);
            options.setToolbarColor(ContextCompat.getColor(this, R.color.dark_blue));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.Light_blue));
            options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white));

            UCrop.of(uri, destinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(400, 400)
                    .withOptions(options)
                    .start(this, requestCode);
        } catch (Exception e) {
            Log.e("UCropError", "Error cropping image: " + e.getMessage());
        }
    }

    //this method handles the activity result of the cropping task
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        //handle the result of the crop
        if (requestCode== ucropRequestCode1 || requestCode== ucropRequestCode2) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                try {
                    Bitmap croppedImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    if (requestCode == ucropRequestCode1) {
                        signature1 = croppedImg;
                        imageView1.setImageBitmap(signature1);
                    } else {
                        signature2 = croppedImg;
                        imageView2.setImageBitmap(signature2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //load the TF Lite model
    private MappedByteBuffer loadModel(String modelPath) throws IOException {
        //open the specified model file from the assets folder
        try (AssetFileDescriptor descriptor = getAssets().openFd(modelPath);
             FileInputStream stream = new FileInputStream(descriptor.getFileDescriptor());
             FileChannel fileChannel = stream.getChannel()) {
            long offset = descriptor.getStartOffset();
            long length = descriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, offset, length);
        }
    }

    //preprocess the images before verifying
    public TensorBuffer preprocessImage(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("PreprocessImageError", "Bitmap is null");
            return null;
        }
        try {
            Mat imgMat = new Mat();
            Utils.bitmapToMat(bitmap, imgMat);

            Imgproc.cvtColor(imgMat, imgMat, Imgproc.COLOR_BGR2GRAY); //convert to grayscale
            Imgproc.resize(imgMat, imgMat, new Size(150, 150)); //resize
            Mat edges = new Mat();
            Imgproc.Canny(imgMat, edges, 30, 220);   //canny edge detection
            float[] flatArr = new float[150 * 150];
            for (int i = 0; i < edges.rows(); i++) {
                for (int j = 0; j < edges.cols(); j++) {
                    flatArr[i * edges.cols() + j] = (float) (edges.get(i, j)[0] / 255.0);  //normalization
                }
            }
            TensorBuffer tensorBuff = TensorBuffer.createFixedSize(new int[]{1, 150, 150, 1}, DataType.FLOAT32);
            tensorBuff.loadArray(flatArr);
            Bitmap preprocessedBitmap = Bitmap.createBitmap(edges.cols(), edges.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(edges, preprocessedBitmap);

            return tensorBuff;

        } catch (Exception e) {
            Log.e("PreprocessImageError", "Error during preprocessing", e);
            return null;
        }
    }

    private void verifySignatures() {
        Interpreter interpreter = null;
        try {
            MappedByteBuffer modelFile = loadModel("modelVeriF.tflite");
            interpreter = new Interpreter(modelFile);
            //preprocess images into tensor buffer for model input
            TensorBuffer inputFeature0= preprocessImage(signature1);
            TensorBuffer inputFeature1= preprocessImage(signature2);
            //input arrays
            float[][][][] inputArray1 =new float[1][150][150][1];
            float[][][][] inputArray2=new float[1][150][150][1];
            float[] signature1Arr= inputFeature0.getFloatArray();
            float[] signature2Arr = inputFeature1.getFloatArray();
            //fill the input arrays
            for (int i = 0; i < 150; i++){
                for (int j = 0; j < 150; j++){
                    inputArray1[0][i][j][0] = signature1Arr[i * 150 + j];
                    inputArray2[0][i][j][0] = signature2Arr[i * 150 + j];
                }
            }

            Object[] inputs= {inputArray1, inputArray2};
            float[][] outputArr= new float[1][1];
            Map<Integer, Object> outputs = new HashMap<>();
            outputs.put(0, outputArr);
            //perform prediction
            interpreter.runForMultipleInputsOutputs(inputs, outputs);
            float prediction= outputArr[0][0];
            String result = (prediction >= 0.5) ? "Matched" : "Forged";
            float scorePercent= prediction * 100;
            float similarity = scorePercent;
            if (result.equals("Forged")){
                similarity= 100-scorePercent;
             }
            tvResult.setText(String.format("%.2f%% %s", similarity, result));

            int totalWidth = progBarContainer.getWidth();
            int progWidth = (int) (totalWidth * (similarity / 100.0f));
            //update the progress bar layout parameters
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) progressBar.getLayoutParams();
            params.width = progWidth;
            progressBar.setLayoutParams(params);

            progressBar.setProgress((int) similarity);
            int green = Color.parseColor("#cbd87e");
            int red = Color.parseColor("#ec5951");

            progressBar.getProgressDrawable().setColorFilter(
                    result.equals("Matched") ? green : red, PorterDuff.Mode.SRC_IN );

        } catch (IOException e) {
            Log.e("Model error", "Error loading the model: "+  e.getMessage());
        }catch (Exception e) {
            Log.e("Inference error", "Error during inference:"+ e.getMessage());
        }finally {
            if (interpreter != null) {
                interpreter.close();
            }
        }
    }

    //request camera and storage permissions if not yet granted
    private void permissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    //reset all variables, buttons, bitmap, and views
    private void resetUI() {
        imageView1.setImageBitmap(null);
        imageView2.setImageBitmap(null);
        tvResult.setText("");
        progressBar.setProgress(0);
        resultCard.setVisibility(View.INVISIBLE);
        btnReset.setVisibility(View.INVISIBLE);
        btnVerify.setVisibility(View.VISIBLE);
        btnVerify.setAlpha(1f);
        captureBtn2.setAlpha(1f);
        captureBtn1.setAlpha(1f);
        selectBtn1.setAlpha(1f);
        selectBtn2.setAlpha(1f);
        captureBtn2.setEnabled(true);
        captureBtn1.setEnabled(true);
        selectBtn2.setEnabled(true);
        selectBtn1.setEnabled(true);
        btnVerify.setEnabled(true);
        signature1= null;
        signature2= null;
    }
}







