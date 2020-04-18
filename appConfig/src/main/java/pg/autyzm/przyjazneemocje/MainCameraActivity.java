package pg.autyzm.przyjazneemocje;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.security.Policy;
import java.sql.SQLOutput;

public class MainCameraActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    private Button capture, switchCamera;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static Bitmap bitmap;
    private RelativeLayout overlay;
    public String emotion;
    public String sex;
    int sumOfAnother;
    TextView emocja,plec;
    public int labelHeight = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        myContext = this;
        setContentView(R.layout.activity_camera_main);


     Bundle extras = getIntent().getExtras();
        emotion = extras.getString("SpinnerValue_Emotion");
        sex = extras.getString("SpinnerValue_Sex");
        sumOfAnother = extras.getInt("sumOfAnother");

        emocja = findViewById(R.id.photo_emocja);
        plec = findViewById(R.id.photo_plec);
        emocja.setText(emotion);
        plec.setText(sex.replace("a ","").replace("an ","").replace("ty","ta").replace("ny","na"));

        System.out.println("EMOCJA BUNDLEEEEE " + emotion);

        mCamera =  Camera.open();
        Camera.Parameters p = mCamera.getParameters();
        mCamera.setDisplayOrientation(0);
        cameraPreview = (LinearLayout) findViewById(R.id.cPreview);
        System.out.println("CAMERA WIDTH " + cameraPreview.getWidth() + " HEIGHT " + cameraPreview.getHeight());
        overlay = (RelativeLayout) findViewById(R.id.overlay);
        mPreview = new CameraPreview(myContext, mCamera);
        releaseCamera();
        chooseCamera();
        cameraPreview.addView(mPreview);
        System.out.println("MPREVIEW  WIDTH " + mPreview.getWidth() + " HEIGHT " + mPreview.getHeight());


        capture = (Button) findViewById(R.id.btnCam);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamera.takePicture(null, null, mPicture);
            }
        });

        switchCamera = (Button) findViewById(R.id.btnSwitch);
        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the number of cameras
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    //release the old camera instance
                    //switch camera, from the front and the back and vice versa

                    releaseCamera();
                    chooseCamera();
                } else {

                }
            }
        });

        Button exitCamera = (Button) findViewById(R.id.btnExit);
        exitCamera.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              //get the number of cameras
                                              int camerasNumber = Camera.getNumberOfCameras();
                                              Intent intent = new Intent(MainCameraActivity.this,MainActivity.class);
                                              startActivity(intent);
                                          }
                                      });
        mCamera.startPreview();

    }

    private int findFrontFacingCamera() {

        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            System.out.println("info o kamerach " + i +"/"+ numberOfCameras + "INFO facing" + info.facing );
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;

    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;

            }

        }
        return cameraId;
    }

    public void onResume() {

            super.onResume();
            if(mCamera == null) {
                mCamera = Camera.open();
                mCamera.setDisplayOrientation(0);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
                Log.d("nu", "null");
            }else {
                Log.d("nu","no null");
            }

    }

    public void chooseCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                //mCamera.setDisplayOrientation(180);
                setCameraDisplayOrientation(MainCameraActivity.this,cameraId,mCamera);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                mCamera = Camera.open(cameraId);
                //mCamera.setDisplayOrientation(0));
                setCameraDisplayOrientation(MainCameraActivity.this,cameraId,mCamera);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
               // bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Intent intent = new Intent(MainCameraActivity.this,PictureActivity.class);
                intent.putExtra("emocja",emotion);
                intent.putExtra("sex", sex);
                intent.putExtra("sumOfAnother",sumOfAnother);
                startActivity(intent);
                try {
                    bitmap = processImage(data); //DOPISAŁAM bitmap
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        return picture;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = cameraPreview.getMeasuredWidth(),
                previewHeight = cameraPreview.getMeasuredHeight();

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.width =  previewWidth - previewHeight;
        //overlayParams.width =  300;

        overlay.setLayoutParams(overlayParams);
    }

    private Bitmap processImage(byte[] data) throws IOException {

        // Determine the width/height of the image
        int width = mCamera.getParameters().getPictureSize().width;
        int height = mCamera.getParameters().getPictureSize().height;
        System.out.println("ANIAwysokosc " + height + "szerokosc " + width);

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
/////////////////UCINA GÓRNY PASEK A NIE TEN DOLNY ;(((((((((
        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        int minSize = Math.min(width,height);
        int maxSize = Math.max(width,height);
        System.out.println("Main IMAGEPROCCESS MINSIZE " + minSize + " MAXSIZE " + maxSize);


        Matrix matrix = new Matrix();
       matrix.postRotate(0);

        Bitmap cropped;
        //TODO - TRZEBA WYKRYĆ MIRROR REFLECTION?FLIP?
        if (cameraFront) {
            matrix.postScale(-1.0f, 1.0f);
            //matrix.preRotate(90);
            cropped = Bitmap.createBitmap(bitmap, (maxSize - minSize), 0, minSize, minSize, matrix, true);
                 System.out.println("processing frontcamera");
        }
        else {
            cropped = Bitmap.createBitmap(bitmap, 0, 0, minSize, minSize, matrix, true);
             System.out.println("processing backcamera");
        }
        bitmap.recycle();

        // Scale down to the output size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, 500, 500, true);
        //było 600
        cropped.recycle();

        return scaledBitmap;
    }
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
