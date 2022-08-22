package com.nic.nammaoorusuper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.nammaoorusuper.R;
import com.nic.nammaoorusuper.api.Api;
import com.nic.nammaoorusuper.api.ApiService;
import com.nic.nammaoorusuper.api.ServerResponse;
import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.dataBase.DBHelper;
import com.nic.nammaoorusuper.dataBase.dbData;
import com.nic.nammaoorusuper.databinding.CameraScreenBinding;
import com.nic.nammaoorusuper.session.PrefManager;
import com.nic.nammaoorusuper.support.MyLocationListener;
import com.nic.nammaoorusuper.utils.CameraUtils;
import com.nic.nammaoorusuper.utils.UrlGenerator;
import com.nic.nammaoorusuper.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class CameraScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    private PrefManager prefManager;
    private CameraScreenBinding cameraScreenBinding;





    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private dbData dbData = new dbData(this);
    String pmay_id;

    private String hab_code;
    private String campaign_id;
    private String activity_id;
    private String campaign_activity_id;
    private String campaign_activity_entry_id;
    private String image_category_id;
    private String item_no;
    private String no_of_images;
    ///Image With Description
    ImageView imageView, image_view_preview;
    TextView latitude_text, longtitude_text;
    EditText myEditTextView;
    private List<View> viewArrayList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraScreenBinding = DataBindingUtil.setContentView(this, R.layout.camera_screen);
        cameraScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);

        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        intializeUI();

        cameraScreenBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageButtonClick();
            }
        });
        cameraScreenBinding.btnSaveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
    }

    public void intializeUI() {
        Utils.statuscolor(CameraScreen.this);
        campaign_id = getIntent().getStringExtra("campaign_id");
        campaign_activity_id = getIntent().getStringExtra("campaign_activity_id");
        activity_id = getIntent().getStringExtra("activity_id");
        hab_code = getIntent().getStringExtra("hab_code");
        campaign_activity_entry_id = getIntent().getStringExtra("campaign_activity_entry_id");
        image_category_id = getIntent().getStringExtra("image_category_id");
        item_no = getIntent().getStringExtra("item_no");
        no_of_images = getIntent().getStringExtra("no_of_images");
        cameraScreenBinding.singleCaptureLayout.setVisibility(View.GONE);
        cameraScreenBinding.multiCaptureLayout.setVisibility(View.VISIBLE);
        updateView(CameraScreen.this,cameraScreenBinding.cameraLayout,"","");


        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        //pmay_id = getIntent().getStringExtra("lastInsertedID");
        cameraScreenBinding.btnSave.setOnClickListener(this::onClick);
        cameraScreenBinding.btnSaveLocal.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_save:
                saveImage();
            break;
        }
    }




    private void captureImage() {
        /*if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }*/
        Intent intent = new Intent(this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CROP, false);//default is false
        startActivityForResult(intent, 1213);
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }
    }

    public void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        Integer gpsFreqInMillis = 1000;
        Integer gpsFreqInDistance = 1;

        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(CameraScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(CameraScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(CameraScreen.this, getResources().getString(R.string.satellite));
            }
        } else {
            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.gps_is_not_turned_on));
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_required))
                .setMessage(getResources().getString(R.string.camera_need_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(CameraScreen.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
            cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
            image_view_preview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
            imageView.setImageBitmap(rotatedBitmap);
            latitude_text.setText(""+offlatTextValue);
            longtitude_text.setText(""+offlongTextValue);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap photo= (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    image_view_preview.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    latitude_text.setText(""+offlatTextValue);
                    longtitude_text.setText(""+offlongTextValue);

                    cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
                    cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
                    cameraScreenBinding.imageView.setImageBitmap(photo);
                }
                else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_to_capture), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if(requestCode == 1213){
            if(data!=null){
                String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Bitmap rotatedBitmap = BitmapFactory.decodeFile(filePath);
                imageView.setImageBitmap(rotatedBitmap);
                image_view_preview.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                latitude_text.setText(""+offlatTextValue);
                longtitude_text.setText(""+offlongTextValue);

                cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
                cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
                cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
            }

        }
        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            }
            else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_video), Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_capture_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("image_save".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                   //Utils.showAlert(CameraScreen.this,jsonObject.getString("MESSAGE"));
                   Toasty.success(CameraScreen.this,jsonObject.getString("MESSAGE"),Toasty.LENGTH_SHORT,true).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        }
                    }, 500);
                }
                else {
                    Utils.showAlert(CameraScreen.this,jsonObject.getString("MESSAGE"));
                }
                Log.d("image_save", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        //setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    public void addImageButtonClick(){
        if(viewArrayList.size() < Integer.parseInt(no_of_images)) {
            if (imageView.getDrawable() != null && viewArrayList.size() > 0) {
                updateView(CameraScreen.this, cameraScreenBinding.cameraLayout, "", "");
            } else {
                Utils.showAlert(CameraScreen.this, getResources().getString(R.string.please_capture_image));
            }
        }
        else {
            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.maximum_three_photos));

        }
    }
    private final void focusOnView(final ScrollView your_scrollview) {
        your_scrollview.post(new Runnable() {
            @Override
            public void run() {
                your_scrollview.fullScroll(View.FOCUS_DOWN);

            }
        });
    }

    //Method for update single view based on email or mobile type
    public View updateView(final Activity activity, final LinearLayout emailOrMobileLayout, final String values, final String type) {
        final View hiddenInfo = activity.getLayoutInflater().inflate(R.layout.image_with_description, emailOrMobileLayout, false);
        final ImageView imageView_close = (ImageView) hiddenInfo.findViewById(R.id.imageView_close);
        final LinearLayout description_layout = (LinearLayout) hiddenInfo.findViewById(R.id.description_layout);
        imageView = (ImageView) hiddenInfo.findViewById(R.id.image_view);
        image_view_preview = (ImageView) hiddenInfo.findViewById(R.id.image_view_preview);
        myEditTextView = (EditText) hiddenInfo.findViewById(R.id.description);
        latitude_text = hiddenInfo.findViewById(R.id.latitude);
        longtitude_text = hiddenInfo.findViewById(R.id.longtitude);
        description_layout.setVisibility(View.GONE);
        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageView.setVisibility(View.VISIBLE);
                    if (viewArrayList.size() != 1) {
                        ((LinearLayout) hiddenInfo.getParent()).removeView(hiddenInfo);
                        viewArrayList.remove(hiddenInfo);
                    }

                } catch (IndexOutOfBoundsException a) {
                    a.printStackTrace();
                }
            }
        });
        image_view_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLong();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatLong();
            }
        });
        emailOrMobileLayout.addView(hiddenInfo);

        View vv = emailOrMobileLayout.getChildAt(viewArrayList.size());
        EditText myEditTextView1 = (EditText) vv.findViewById(R.id.description);
        //myEditTextView1.setSelection(myEditTextView1.length());
        myEditTextView1.requestFocus();
        viewArrayList.add(hiddenInfo);
        return hiddenInfo;
    }

    @SuppressLint("CheckResult")
    public void showToast(){
        Toasty.success(CameraScreen.this,getResources().getString(R.string.inserted_success),Toast.LENGTH_SHORT,true).show();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    public void saveImage() {
        int childCount = cameraScreenBinding.cameraLayout.getChildCount();
        int count = 0;
        if(childCount == Integer.parseInt(no_of_images)){
            if (childCount > 0) {
                JSONArray imageJson = new JSONArray();
                for (int i = 0; i < childCount; i++) {
                    JSONObject image_json = new JSONObject();

                    View vv = cameraScreenBinding.cameraLayout.getChildAt(i);
                    imageView = vv.findViewById(R.id.image_view);
                    myEditTextView = vv.findViewById(R.id.description);
                    latitude_text = vv.findViewById(R.id.latitude);
                    longtitude_text = vv.findViewById(R.id.longtitude);


                    if (imageView.getDrawable() != null) {
                        //if(!myEditTextView.getText().toString().equals("")){
                        count = count + 1;
                        byte[] imageInByte = new byte[0];
                        String image_str = "";
                        String description = "";
                        String image_path = "";
                        try {
                            description = myEditTextView.getText().toString();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            image_str = BitMapToString(bitmap);

                            image_json.put("image_serial_no",count);
                            image_json.put("image_lat",latitude_text.getText().toString());
                            image_json.put("image_long",longtitude_text.getText().toString());
                            image_json.put("image_description",myEditTextView.getText().toString());
                            image_json.put("image",image_str);
                            imageJson.put(image_json);

                        } catch (Exception e) {
                            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.at_least_capture_one_photo));
                        }


                    } else {
                        Utils.showAlert(CameraScreen.this, getResources().getString(R.string.please_capture_image));
                    }

                    if(Utils.isOnline()){
                        saveImageService(imageJson);
                    }
                    else {
                        Utils.showAlert(CameraScreen.this,"no Internet");
                    }
                }
            }
        }
        else {
            Utils.showAlert(CameraScreen.this,"Please Capture Image");
        }
        focusOnView(cameraScreenBinding.scrollView);
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        long lengthbmp = b.length/1024;
        Log.d("size",""+lengthbmp);
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void saveImageService(JSONArray image_json) {
        try {
            new ApiService(this).makeJSONObjectRequest("image_save", Api.Method.POST, UrlGenerator.getPMAYListUrl(), save_image_en_JsonParams(image_json), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject save_image_en_JsonParams(JSONArray image_json) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), save_image_no_JsonParams(image_json).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("image", "" + authKey);
        return dataSet;
    }
    private JSONObject save_image_no_JsonParams(JSONArray image_json) {
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_SERVICE_ID, "campaign_activity_photos_save");
            dataSet.put("hab_code",hab_code);
            dataSet.put("campaign_id", campaign_id);
            dataSet.put("activity_id", activity_id);
            dataSet.put("campaign_activity_id", campaign_activity_id);
            dataSet.put("campaign_activity_entry_id", campaign_activity_entry_id);
            dataSet.put("item_no", item_no);
            dataSet.put("image_category_id", image_category_id);
            dataSet.put("image_details", image_json);
            Log.d("activty", "" + dataSet);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return dataSet;
    }
}
