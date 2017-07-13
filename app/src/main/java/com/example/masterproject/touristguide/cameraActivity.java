package com.example.masterproject.touristguide;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class cameraActivity extends AppCompatActivity {

    Background background;
    View popupView;
    Boolean isGPSEnabled, isNetworkEnabled;
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextView titleOnPopUp;
    private TextView textOnPopUp;
    private TextView displayData;
    private TextureView textureView;
    private ProgressDialog mProgressDialog;
    Button dismissPopUp;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    ToggleButton toggleSound;
    PopupWindow pw;
    LayoutInflater inflater;
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_LOCATION_PERMISSION = 99;
    double longitude = 0.00;
    double lattitude = 0.00;
    String loc_flag = "0";
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    boolean takeAnotherPicture = false;

    //---------------speech global--------------------

    TextToSpeech t1;

    //--------------speech global ends-----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toast.makeText(getApplicationContext(), "On create", Toast.LENGTH_LONG).show();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            ActivityCompat.requestPermissions(cameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
////                                Toast.makeText(getApplicationContext()," Nolocationn", Toast.LENGTH_LONG).show();
//            // for ActivityCompat#requestPermissions for more details.
        } else {
            // getting GPS status
            isGPSEnabled = lm
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = lm
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnabled&& !isNetworkEnabled) {

            }
            else{
                if(isGPSEnabled){
                    lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            2000,
                            0, ll);
                }
                if(isNetworkEnabled){
                    lm.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            2000,
                            0, ll);
                }
            }

//            Location location = lm.getLastKnownLocation((LocationManager.GPS_PROVIDER));

//            Toast.makeText(getApplicationContext(), String.valueOf(longitude), Toast.LENGTH_LONG).show();
        }


        //-------------------speech initializer---------------------------------

//        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    t1.setLanguage(Locale.US);
//                    Toast.makeText(getApplicationContext(), "Speech initialized", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Speech error man", Toast.LENGTH_LONG).show();
//                }
//            }
//        });


        //-------------------speech initializer ends----------------------------

        //------Kriti-----------------------------------------------------------------

        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;

        inflater = (LayoutInflater) cameraActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_layout, null);
        pw = new PopupWindow(popupView, 500, 600, true);
        findViewById(R.id.texture).post(new Runnable() {
            public void run() {
                pw.showAtLocation(findViewById(R.id.texture), Gravity.CENTER, 0, 0);
            }
        });

        dismissPopUp = (Button) popupView.findViewById(R.id.ok);
        dismissPopUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View popupView) {
                pw.dismiss();
            }
        });
        titleOnPopUp = (TextView) popupView.findViewById(R.id.textView1);
        textOnPopUp = (TextView) popupView.findViewById(R.id.textView2);
        displayData = (TextView) popupView.findViewById(R.id.displayText);
        displayData.setMovementMethod(new ScrollingMovementMethod());
        displayData.setVisibility(View.INVISIBLE);
        toggleSound = (ToggleButton) popupView.findViewById(R.id.toggleSound);
        toggleSound.setVisibility(View.GONE);

        // Creating a progress dialog window
        mProgressDialog = new ProgressDialog(this);


        // Close the dialog window on pressing back button
        mProgressDialog.setCancelable(true);

        // Setting a horizontal style progress bar
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        /** Setting a message for this progress dialog
         * Use the method setTitle(), for setting a title
         * for the dialog window
         *  */
        mProgressDialog.setMessage("Please wait ...");
        textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                takePicture();
            }
        });

        //-----kriti end oncreate-----------------------------------------------------

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ab);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
//        byte [] byte_arr = stream.toByteArray();
//        String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
//
//        background = new Background(MainActivity.this);
//        background.setUpdateListener(new Background.OnUpdateListener() {
//            @Override
//            public void onUpdateBackground(String data) {
//                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
//                Toast.makeText(getApplicationContext(), "In APP", Toast.LENGTH_LONG).show();
//
//            }
//        });
//        background.execute(image_str);


    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(cameraActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void takePicture() {
        if (null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory() + "/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];

                        buffer.get(bytes);
                        String image_str = Base64.encodeToString(bytes, Base64.DEFAULT);
//                        double longitude = 0.00;
//                        double lattitude = 0.00;
//                        String loc_flag = "0";
//                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            // TODO: Consider calling
//                            ActivityCompat.requestPermissions(cameraActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
////                                Toast.makeText(getApplicationContext()," Nolocationn", Toast.LENGTH_LONG).show();
//                            // for ActivityCompat#requestPermissions for more details.
//                            return;
//                        }
//
//
//                        else {
//                            Location location = lm.getLastKnownLocation((LocationManager.GPS_PROVIDER));
//                            longitude = location.getLongitude();
//                            lattitude = location.getLatitude();
//                            loc_flag = "1";
//                            Toast.makeText(getApplicationContext(), String.valueOf(longitude), Toast.LENGTH_LONG).show();
//                        }

                        background = new Background(cameraActivity.this);
                        background.setUpdateListener(new Background.OnUpdateListener() {
                            @Override
                            public void onUpdateBackground(final String data) {
//                                Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "In APP", Toast.LENGTH_LONG).show();
                                //------------Popup Window-------------------
                                findViewById(R.id.texture).post(new Runnable() {
                                    public void run() {
                                        pw.showAtLocation(findViewById(R.id.texture), Gravity.CENTER, 0, 0);
                                    }
                                });
                                titleOnPopUp.setVisibility(View.GONE);

                                textOnPopUp.setVisibility(View.GONE);
                                toggleSound.setVisibility(View.VISIBLE);
                                toggleSound.setChecked(true);
                                if (toggleSound.isChecked()) {
                                    displayData.setVisibility(View.INVISIBLE);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        ttsGreater21(data);
                                    } else {
                                        ttsUnder20(data);
                                    }
                                }
                                toggleSound.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (toggleSound.isChecked()) {
                                            displayData.setVisibility(View.INVISIBLE);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                ttsGreater21(data);
                                            } else {
                                                ttsUnder20(data);
                                            }

                                        } else {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                ttsUnder21stop();
                                            } else {
                                                ttsUnder20stop();
                                            }

                                            displayData.setVisibility(View.VISIBLE);
                                            displayData.setText(data);

                                        }
                                    }
                                });
                                dismissPopUp.setOnClickListener(new View.OnClickListener() {

                                    public void onClick(View popupView) {
                                        pw.dismiss();
                                        if (t1 != null) {

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                ttsUnder21stop();
                                            } else {
                                                ttsUnder20stop();
                                            }
                                        }
                                    }
                                });


                                //------------Popup Window ends-------------------
                                //-------------speech call-------------------


                                //t1.speak("success hey there how are you", TextToSpeech.QUEUE_FLUSH, null, null);

                                //------------speech call ends------------------


                            }
                        });
                        background.execute(image_str, String.valueOf(lattitude), String.valueOf(longitude), loc_flag);
                        //t1.speak("Hello how are you man", TextToSpeech.QUEUE_FLUSH,null,null);
                        save(bytes);
//                        //-------------speech call-------------------
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            ttsGreater21("Murtaza Get lost");
//                        } else {
//                            ttsUnder20("Kritikaaaaaaa khana");
//                        }
//                        //t1.speak("success hey there how are you", TextToSpeech.QUEUE_FLUSH, null, null);
//
//                        //------------speech call ends------------------
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(cameraActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //----------------speech functions--------------------------------

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        Toast.makeText(cameraActivity.this, "ttsunder20", Toast.LENGTH_SHORT).show();
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    private void ttsUnder20stop() {
        t1.stop();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        Toast.makeText(cameraActivity.this, "ttsgreater21", Toast.LENGTH_SHORT).show();
        String utteranceId = this.hashCode() + "";
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private void ttsUnder21stop() {
        t1.stop();
    }
    //----------------speech functions ends---------------------------

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(cameraActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(cameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {

            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(cameraActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(cameraActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        t1.setLanguage(Locale.US);
                        Toast.makeText(getApplicationContext(), "Speech initialized", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Speech error man", Toast.LENGTH_LONG).show();
                    }
                }
            });

    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");

//        toggleSound.setChecked(false);
        closeCamera();

        //---------------speech-----------
        if (t1 != null) {
            t1.shutdown();
        }
        //---------------speech-------------
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onPause();
    }

    private LocationListener ll = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            lattitude = location.getLatitude();
            loc_flag = "1";
            Log.e("toastinlocationlistener", "hey location changed");

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

    }
}
