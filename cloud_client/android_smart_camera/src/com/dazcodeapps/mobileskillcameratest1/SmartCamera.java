package com.dazcodeapps.mobileskillcameratest1;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;


public class SmartCamera {

    private Camera camera;
    private final Integer defaultCameraId = 0;


    void SmartCamera() {

    }

    private static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
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


    public void run_camera(Context context, Camera.PictureCallback callback) {

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d("ERROR", "NO CAMERA");
        } else {
            safeCameraOpen(defaultCameraId);
        }

        SmartCamera.setCameraDisplayOrientation((Activity) context, defaultCameraId, camera);

        SurfaceTexture surfaceTexture = new SurfaceTexture(0);


        try {
            camera.setPreviewTexture(surfaceTexture);

            Camera.Parameters params = camera.getParameters();
            params.setJpegQuality(100);
            //params.set
            camera.setParameters(params);
            camera.startPreview();

            camera.takePicture(null, null, callback);
        } catch (
                Exception ex)

        {
            Log.d("ERROR", ex.toString());

        }

    }

    private void safeCameraOpen(int id) {

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        camera.startPreview();


    }


    public void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}
