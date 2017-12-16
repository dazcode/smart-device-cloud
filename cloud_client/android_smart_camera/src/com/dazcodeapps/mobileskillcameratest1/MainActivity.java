package com.dazcodeapps.mobileskillcameratest1;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazon.identity.auth.device.api.workflow.RequestContext;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity implements NetworkRequests.NetResponseDelegate {


    private final String KEY_SMART_LABELS = "KEY_SMART_LABELS";
    private final String KEY_CAMERA = "KEY_CAMERA";
    private final String KEY_ACCOUNT_ID = "KEY_ACCOUNT_ID";
    private final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
    private final String KEY_SMART_IMAGE = "KEY_SMART_IMAGE";
    private final String KEY_SMART_DATA = "KEY_SMART_DATA";
    private final String KEY_IMAGE_ROTATION = "KEY_IMAGE_ROTATION";


    private final Integer PICK_IMAGE_REQUEST = 1;


    private ArrayList<RekognitionLabel> smart_labels = new ArrayList<RekognitionLabel>();
    private String smart_data = "";
    private String device_id = "";
    private String account_id = "";
    private String android_id = "";
    private int camera_id = 0;

    private boolean image_rotation = false;


    private String currentImageUrl;
    private RequestContext requestContext;
    private String AWS_ENDPOINT;

    private AmazonAuthorizationManager mAmazonAuthorizationManager;

    private Utilities utilities = new Utilities();
    private NetworkRequests networkRequests = new NetworkRequests();
    private SmartCamera smartCamera = new SmartCamera();
    private LoginWithAmazonActivity loginWithAmazonActivity = new LoginWithAmazonActivity();


    @Override
    protected void onPause() {
        super.onPause();
        smartCamera.releaseCameraAndPreview();
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestContext.onResume();
    }


    @Override
    public void onDataRecieved(Object[] results) {
        Log.d("datarec", "RESULTS RECIEVED:" + results[0]);


        if (results[0].toString().equals("ReckognitionLabels")) {
            smart_labels = (ArrayList<RekognitionLabel>) results[1];
            smartDataDisplay(smart_labels);


        }

        setNetworkRequestProgressBar(false);

    }

    public void smartDataDisplay(ArrayList<RekognitionLabel> labels) {
        //TextView txtImageSmartData = (TextView)findViewById(R.id.txtImageSmartData);
        smart_data = "";
        for (int i = 0; i < labels.size(); i++) {

            smart_data += labels.get(i).getLabelName();
            smart_data += "\t\t\t";
            smart_data += labels.get(i).getLabelConfidence();
            smart_data += "\n";
        }

        //txtImageSmartData.setText("" + smart_data);
        bindAdapter();

    }

    public void smart_camera_begin() {

        smartCamera.run_camera(this, cameraCallback);

    }


    private void setNetworkRequestProgressBar(boolean progressBarVisibleState) {
        ListView listView = (ListView) findViewById(R.id.listView);
        ProgressBar progressBarSmartLabels = (ProgressBar) findViewById(R.id.progressBarSmartLabels);
        TextView txtSmartLabelCount = (TextView) findViewById(R.id.txtSmartLabelCount);
        Button btnSmartCamera = (Button) findViewById(R.id.btnSmartCamera);
        Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);


        if (progressBarVisibleState) {
            progressBarSmartLabels.setVisibility(View.VISIBLE);
            txtSmartLabelCount.setVisibility(View.GONE);
            btnSmartCamera.setEnabled(false);
            btnSelectImage.setEnabled(false);
            //listView.setVisibility(View.INVISIBLE);
        } else {
            progressBarSmartLabels.setVisibility(View.GONE);
            txtSmartLabelCount.setVisibility(View.VISIBLE);
            btnSmartCamera.setEnabled(true);
            btnSelectImage.setEnabled(true);

            //listView.setVisibility(View.VISIBLE);
        }
    }


    public void smart_camera_action(String filename, boolean isCameraImage) {
        ImageView imageView = (ImageView) findViewById(R.id.smartCameraPreview);
        Utilities.displayImageFromPath(filename, imageView, isCameraImage);


        this.currentImageUrl = filename;
        setNetworkRequestProgressBar(true);
        smart_labels = null;
        bindAdapter();
        networkRequests.upload_smart_photo(Utilities.Get_Service_Endpoint(getApplicationContext()), this, device_id, account_id, filename);

    }


    final Camera.PictureCallback cameraCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            String filename = Utilities.getFile();
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(filename));
                outputStream.write(data);
                outputStream.close();
            } catch (Exception e) {
                Log.d("Camera callback ERROR!:", e.toString());
            }

            Log.d("Camera callback", "complete!");
            image_rotation = true;

            smart_camera_action(filename, image_rotation);
        }
    };

    private void bindAdapter() {

        if (smart_labels != null && smart_labels.size() > 0) {
            ListView listView = (ListView) findViewById(R.id.listView);
            LabelListAdpater listAdapter = new LabelListAdpater(this);
            listAdapter.smart_labels = smart_labels;
            listView.setAdapter(listAdapter);

            TextView txtListHeader = (TextView) findViewById(R.id.txtSmartLabelCount);
            txtListHeader.setText("Smart Labels: " + listAdapter.smart_labels.size());

            Log.d("BINDADAPTER", "test");
        } else {
            ListView listView = (ListView) findViewById(R.id.listView);
            LabelListAdpater listAdapter = new LabelListAdpater(this);
            listAdapter.smart_labels = smart_labels;
            listView.setAdapter(listAdapter);

            TextView txtListHeader = (TextView) findViewById(R.id.txtSmartLabelCount);
            txtListHeader.setText("No smart labels available");

        }

    }

    private void smart_select_image() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, PICK_IMAGE_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String fileName = "";

                    try {
                        fileName = Utilities.getFilePath(this, selectedImage);
                    } catch (Exception ex) {
                        Log.d("EXCEPTION!", ex.toString());
                    }
                    image_rotation = false;

                    smart_camera_action(fileName, image_rotation);
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String fileName = "";

                    try {
                        fileName = Utilities.getFilePath(this, selectedImage);
                    } catch (Exception ex) {
                        Log.d("EXCEPTION!", ex.toString());
                    }

                    image_rotation = false;

                    smart_camera_action(fileName, image_rotation);
                }
                break;
        }
    }

    private void setup_ui(Bundle savedInstanceState) {

        View btnSmartCamera = findViewById(R.id.btnSmartCamera);
        btnSmartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smart_camera_begin();
            }
        });

        View btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smart_select_image();
            }
        });


        if (savedInstanceState != null) {
            ImageView imageView = (ImageView) findViewById(R.id.smartCameraPreview);

            currentImageUrl = savedInstanceState.getString(KEY_SMART_IMAGE);
            if (currentImageUrl != null) {

                Utilities.displayImageFromPath(currentImageUrl, imageView, image_rotation);
            }

            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Log.d("adapter", "test");
                }
            });


            bindAdapter();

            //TextView txtImageSmartData = (TextView) findViewById(R.id.txtImageSmartData);
            //txtImageSmartData.setText(savedInstanceState.getString(KEY_SMART_DATA));
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        image_rotation = savedInstanceState.getBoolean(KEY_IMAGE_ROTATION);
        camera_id = savedInstanceState.getInt(KEY_CAMERA);
        account_id = savedInstanceState.getString(KEY_ACCOUNT_ID);
        device_id = savedInstanceState.getString(KEY_DEVICE_ID);
        smart_data = savedInstanceState.getString(KEY_SMART_DATA);
        currentImageUrl = savedInstanceState.getString(KEY_SMART_IMAGE);
        smart_labels = savedInstanceState.getParcelableArrayList(KEY_SMART_LABELS);


        setup_ui(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IMAGE_ROTATION, image_rotation);
        outState.putInt(KEY_CAMERA, camera_id);
        outState.putString(KEY_ACCOUNT_ID, account_id);
        outState.putString(KEY_DEVICE_ID, device_id);
        outState.putString(KEY_SMART_DATA, smart_data);
        outState.putString(KEY_SMART_IMAGE, currentImageUrl);
        outState.putParcelableArrayList(KEY_SMART_LABELS, smart_labels);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestContext = RequestContext.create(this);


        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString(LoginWithAmazonActivity.EXTRA_MESSAGE) != null) {
            account_id = bundle.getString(LoginWithAmazonActivity.EXTRA_MESSAGE);
        }
        AWS_ENDPOINT = Utilities.Get_Service_Endpoint(getApplicationContext());
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        device_id = android_id;

        setup_ui(savedInstanceState);


        //NetworkRequests.register_smart_device(this,AWS_ENDPOINT, device_id, account_id);

    }

}