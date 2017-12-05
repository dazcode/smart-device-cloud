package com.dazcodeapps.mobileskillcameratest1;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.api.Listener;
import com.amazon.identity.auth.device.api.authorization.AuthCancellation;
import com.amazon.identity.auth.device.api.authorization.AuthorizationManager;
import com.amazon.identity.auth.device.api.authorization.AuthorizeListener;
import com.amazon.identity.auth.device.api.authorization.AuthorizeRequest;
import com.amazon.identity.auth.device.api.authorization.AuthorizeResult;
import com.amazon.identity.auth.device.api.authorization.ProfileScope;
import com.amazon.identity.auth.device.api.authorization.Scope;
import com.amazon.identity.auth.device.api.authorization.User;
import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class SmartCameraLauncherActivity extends Activity {

    private static final String TAG = SmartCameraLauncherActivity.class.getName();
    private String device_id;
    private String account_id;
    AmazonAuthorizationManager mAmazonAuthorizationManager;
    private ToggleButton toggleButton;
    private TextView mProfileText;
    private TextView mLogoutTextView;
    private ProgressBar mLogInProgress;
    private RequestContext requestContext;
    private boolean mIsLoggedIn;
    private View mLoginButton;
    private String android_id;

    private String AWS_ENDPOINT;


    /*************************************************************/
    // CAMERA
    /*************************************************************/
    private final Camera.PictureCallback mCall = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            String filename = getFile();


            try {
                FileOutputStream outputStream = new FileOutputStream(new File(filename));
                outputStream.write(data);
                outputStream.close();
            } catch (Exception e) {
                Log.d("Camera callback ERROR!:", e.toString());
            }

            Log.d("Camera callback", "complete!");


            displayImageFromPath(filename);
            upload_smart_photo(Get_Service_Endpoint(), device_id, account_id, filename);

        }
    };
    /*************************************************************/

    private Camera camera;
    private int cameraId = 0;

    private void run_camera() {

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
        } else {
            //cameraId = findFrontFacingCamera();
            cameraId = 0;
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                safeCameraOpen(cameraId);

            }
        }


        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        try {

            camera.setPreviewTexture(surfaceTexture);

            Camera.Parameters params = camera.getParameters();
            params.setJpegQuality(100);
            camera.setParameters(params);
            camera.startPreview();

            camera.takePicture(null, null, mCall);
        } catch (Exception ex) {
            Log.d("ERROR", ex.toString());

        }
    }


    private void safeCameraOpen(int id) {

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
            return;
        }
        camera.startPreview();

    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("DEBUG", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }


    private String getFile() {
        File folder = Environment.getExternalStoragePublicDirectory("/");
        if (!folder.exists()) {
            folder.mkdir();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "image"+ timeStamp + "_";
        String imageFileName = "Temp_Image";
        File image_file = null;

        try {
            image_file = File.createTempFile(imageFileName, ".jpg", folder);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return image_file.getAbsolutePath();
    }


    private void displayImageFromPath(String fileName) {


        File imgFile = new File(fileName);

        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.imageView1);

            myImage.setImageBitmap(myBitmap);

        }
    }




    private String Get_Service_Endpoint(){
        return getApplicationContext().getString(R.string.aws_service_endpoint);
    }


    /*************************************************************/
    /*************************************************************/
    // CREATE
    /*************************************************************/
    /*************************************************************/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContext = RequestContext.create(this);

        AWS_ENDPOINT = Get_Service_Endpoint();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        device_id = android_id;


        register_smart_device(AWS_ENDPOINT,device_id, account_id);


        requestContext.registerListener(new AuthorizeListener() {
            /* Authorization was completed successfully. */
            @Override
            public void onSuccess(AuthorizeResult authorizeResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // At this point we know the authorization completed, so remove the ability to return to the app to sign-in again
                        setLoggingInState(true);
                    }
                });
                fetchUserProfile();
            }

            /* There was an error during the attempt to authorize the application */
            @Override
            public void onError(AuthError authError) {
                Log.e(TAG, "AuthError during authorization", authError);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthToast("Error during authorization.  Please try again.");
                        resetProfileView();
                        setLoggingInState(false);
                    }
                });
            }

            /* Authorization was cancelled before it could be completed. */
            @Override
            public void onCancel(AuthCancellation authCancellation) {
                Log.e(TAG, "User cancelled authorization");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showAuthToast("Authorization cancelled");
                        resetProfileView();
                    }
                });
            }
        });


        setContentView(R.layout.activity_main);
        initializeUI();


    }


    /*************************************************************/
    /*************************************************************/
    // AWS
    /*************************************************************/
    /*************************************************************/


    private void getJSONObjectFromURL(String urlString, String messageBody)  {
        new CallNetworkTask().execute(messageBody, "", urlString);
    }


    private void register_smart_device(String url_endpoint,String device_id, String account_id) {


        String register_url_string = url_endpoint + "?device_id=" + device_id + "&account_id=" + account_id;


        Log.d("AWS_REGISTER", register_url_string);

        try {
            getJSONObjectFromURL(register_url_string, "");

        } catch (Exception ex) {
            Log.d("AWSERROR", ex.toString());
        }


    }

    public void sync_smart_device_status(String url_endpoint,String device_id, String account_id) {


        String register_url_string = url_endpoint + "?device_id=" + device_id + "&account_id=" + account_id;


        Log.d("AWS_REGISTER", register_url_string);

        try {
            getJSONObjectFromURL(register_url_string, "");

        } catch (Exception ex) {
            Log.d("AWSERROR", ex.toString());
        }


    }

    private void upload_smart_photo(String url_endpoint,String device_id, String account_id, String path_to_image) {


        Bitmap bm = BitmapFactory.decodeFile(path_to_image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        encodedImage = encodedImage.replaceAll("\n", "");
        String update_device_url_string = url_endpoint + "?device_id=" + device_id + "&account_id=" + account_id + "&device_on=True";

        Log.d("AWS_SEND_PHOTO", update_device_url_string);
        Log.d("AWS_SEND_PHOTO", encodedImage);

        try {
            getJSONObjectFromURL(update_device_url_string, encodedImage);

        } catch (Exception ex) {
            Log.d("AWSERROR_AWS_SEND_PHOTO", ex.toString());
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        requestContext.onResume();
    }


    /*************************************************************/
    /*************************************************************/
    // LOGIN

    /*************************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        Scope[] scopes = {ProfileScope.profile(), ProfileScope.postalCode()};
        AuthorizationManager.getToken(this, scopes, new Listener<AuthorizeResult, AuthError>() {
            @Override
            public void onSuccess(AuthorizeResult result) {
                if (result.getAccessToken() != null) {
                    /* The user is signed in */
                    fetchUserProfile();
                } else {
                    /* The user is not signed in */
                }
            }

            @Override
            public void onError(AuthError ae) {
                /* The user is not signed in */
            }
        });
    }

    private void fetchUserProfile() {
        User.fetch(this, new Listener<User, AuthError>() {

            /* fetch completed successfully. */
            @Override
            public void onSuccess(User user) {
                final String name = user.getUserName();
                final String email = user.getUserEmail();
                final String account = user.getUserId();
                final String zipCode = user.getUserPostalCode();


                account_id = account;
                ////
                // register device here
                ////

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateProfileData(name, email, account, zipCode);
                    }
                });
            }

            /* There was an error during the attempt to get the profile. */
            @Override
            public void onError(AuthError ae) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoggedOutState();
                        String errorMessage = "Error retrieving profile information.\nPlease log in again";
                        Toast errorToast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                        errorToast.setGravity(Gravity.CENTER, 0, 0);
                        errorToast.show();
                    }
                });
            }
        });
    }

    private void updateProfileData(String name, String email, String account, String zipCode) {
        StringBuilder profileBuilder = new StringBuilder();
        //profileBuilder.append(String.format("Welcome, %s!\n", name));
        profileBuilder.append(String.format("Logged in: %s\n", email));
        //profileBuilder.append(String.format("Your zipCode is %s\n", zipCode));
        final String profile = profileBuilder.toString();
        Log.d(TAG, "Profile Response: " + profile);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateProfileView(profile);
                setLoggedInState();
            }
        });
    }

    /**
     * Initializes all of the UI elements in the activity
     */
    private void initializeUI() {

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setVisibility(View.GONE);


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                showAuthToast("CLICKED!");
                //launch_camera();


                run_camera();


            }
        });

        mLoginButton = findViewById(R.id.login_with_amazon);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthorizationManager.authorize(
                        new AuthorizeRequest.Builder(requestContext)
                                .addScopes(ProfileScope.profile(), ProfileScope.postalCode())
                                .build()
                );
            }
        });

        // Find the button with the logout ID and set up a click handler
        View logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AuthorizationManager.signOut(getApplicationContext(), new Listener<Void, AuthError>() {
                    @Override
                    public void onSuccess(Void response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoggedOutState();
                            }
                        });
                    }

                    @Override
                    public void onError(AuthError authError) {
                        Log.e(TAG, "Error clearing authorization state.", authError);
                    }
                });
            }
        });

        String logoutText = getString(R.string.logout);
        mProfileText = (TextView) findViewById(R.id.profile_info);
        mLogoutTextView = (TextView) logoutButton;
        mLogoutTextView.setText(logoutText);
        mLogInProgress = (ProgressBar) findViewById(R.id.log_in_progress);
    }

    /**
     * Sets the text in the mProfileText {@link TextView} to the value of the provided String.
     *
     * @param profileInfo the String with which to update the {@link TextView}.
     */
    private void updateProfileView(String profileInfo) {
        Log.d(TAG, "Updating profile view");
        mProfileText.setText(profileInfo);
    }

    /**
     * Sets the text in the mProfileText {@link TextView} to the prompt it originally displayed.
     */
    private void resetProfileView() {
        setLoggingInState(false);
        mProfileText.setText(getString(R.string.default_message));
    }

    /**
     * Sets the state of the application to reflect that the user is currently authorized.
     */
    private void setLoggedInState() {
        toggleButton.setVisibility(Button.VISIBLE);
        mLoginButton.setVisibility(Button.GONE);
        setLoggedInButtonsVisibility(Button.VISIBLE);
        mIsLoggedIn = true;
        setLoggingInState(false);
    }

    /**
     * Sets the state of the application to reflect that the user is not currently authorized.
     */
    private void setLoggedOutState() {
        toggleButton.setVisibility(Button.GONE);
        mLoginButton.setVisibility(Button.VISIBLE);
        setLoggedInButtonsVisibility(Button.GONE);
        mIsLoggedIn = false;
        resetProfileView();
    }

    /**
     * Changes the visibility for both of the buttons that are available during the logged in state
     *
     * @param visibility the visibility to which the buttons should be set
     */
    private void setLoggedInButtonsVisibility(int visibility) {
        mLogoutTextView.setVisibility(visibility);
    }

    /**
     * Turns on/off display elements which indicate that the user is currently in the process of logging in
     *
     * @param loggingIn whether or not the user is currently in the process of logging in
     */
    private void setLoggingInState(final boolean loggingIn) {
        if (loggingIn) {
            mLoginButton.setVisibility(Button.GONE);
            setLoggedInButtonsVisibility(Button.GONE);
            mLogInProgress.setVisibility(ProgressBar.VISIBLE);
            mProfileText.setVisibility(TextView.GONE);
        } else {
            if (mIsLoggedIn) {
                setLoggedInButtonsVisibility(Button.VISIBLE);
            } else {
                mLoginButton.setVisibility(Button.VISIBLE);
            }
            mLogInProgress.setVisibility(ProgressBar.GONE);
            mProfileText.setVisibility(TextView.VISIBLE);
        }
    }

    private void showAuthToast(String authToastMessage) {
        Toast authToast = Toast.makeText(getApplicationContext(), authToastMessage, Toast.LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

    /*************************************************************/

    class CallNetworkTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected String doInBackground(String... urls) {

            HttpsURLConnection urlConnection = null;

            try {
                URL url = new URL(urls[2]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                PrintStream printStream = new PrintStream(out);
                printStream.print(urls[0]);
                printStream.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                String jsonString = sb.toString();

                Log.d("AsyncTask", "" + jsonString);

                return jsonString;


            } catch (Exception e) {
                Log.d("AsyncTask ERROR!", "" + e.toString());
                this.exception = e;
                return null;
            } finally {
                urlConnection.disconnect();
            }


        }

        protected void onPostExecute(String resultString) {
            Log.d("onPostExecute", "" + resultString);

            TextView textView = (TextView) findViewById(R.id.txtImageSmartData);
            textView.setText(resultString);
        }
    }

}
