package emplogtech.com.mytimesheet.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import classes.FilePath;
import classes.SessionManager;
import classes.SingleUploadBroadcastReceiver;
import butterknife.BindView;
import butterknife.ButterKnife;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import emplogtech.com.mytimesheet.R;

public class LeaveActivity extends AppCompatActivity implements SingleUploadBroadcastReceiver.Delegate {

    @BindView(R.id.leaveSpinner)Spinner leaveSpinner;
    @BindView(R.id.edtOther)EditText edtOther;
    @BindView(R.id.edtFrom)EditText edtFromDate;
    @BindView(R.id.edtTo)EditText edtToDate;
    @BindView(R.id.edtAddress)EditText edtAddress;
    @BindView(R.id.edtEmail)EditText edtEmail;
    @BindView(R.id.edtCell)EditText edtCell;
    @BindView(R.id.btnApply)Button btnApply;
    @BindView(R.id.txtAttachment)TextView txtAttachment;
    @BindView(R.id.imgAttachment)ImageView imgAttachment;
    String serverURL = "http://nexgencs.co.za/devApi/upload.php";
    private static final String TAG = "AndroidUploadService";

    ProgressDialog prgDialog;

    Calendar fromCalender,toCalender;
    int responseCode;
    Uri uri;
    String leave_type, from_date, to_date,address,email,cell;
    String filePathHolder, fileID;
    String displayName = null;
    String prompt = "--Select Leave Type--";
    String other = "Other";

    SessionManager session;

    private final SingleUploadBroadcastReceiver uploadReceiver =
            new SingleUploadBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);


        ButterKnife.bind(this);
        initCustomSpinner();
        initImageview();
        AllowRunTimePermission();

        fromCalender = Calendar.getInstance();
        toCalender= Calendar.getInstance();
        session = new SessionManager(this);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Uploading leave information...");
        prgDialog.setCancelable(false);

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromCalender.set(Calendar.YEAR, year);
                fromCalender.set(Calendar.MONTH, monthOfYear);
                fromCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(edtFromDate,fromCalender.getTime());
            }

        };



        final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                toCalender.set(Calendar.YEAR, year);
                toCalender.set(Calendar.MONTH, monthOfYear);
                toCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(edtToDate,toCalender.getTime());
            }

        };


        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LeaveActivity.this, R.style.AlerDialogTheme, fromDate,
                        fromCalender.get(Calendar.YEAR),
                        fromCalender.get(Calendar.MONTH),
                        fromCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(LeaveActivity.this, R.style.AlerDialogTheme, toDate,
                        toCalender.get(Calendar.YEAR),
                        toCalender.get(Calendar.MONTH),
                        toCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(submitForm()){

                    if(!edtOther.getText().toString().trim().isEmpty() && leaveSpinner.getSelectedItem().equals(other)){

                        leave_type = edtOther.getText().toString();
                    }
                    HashMap<String, String> user = session.getUserDetails();
                    String user_id = user.get(SessionManager.KEY_UID);

                    from_date = edtFromDate.getText().toString();
                    to_date = edtToDate.getText().toString();
                    address = edtAddress.getText().toString();
                    email = edtEmail.getText().toString();
                    cell = edtCell.getText().toString();

                    if(displayName != null){
                        prgDialog.show();
                    fileUploadFunction(displayName,leave_type,from_date,to_date,address,cell,email,user_id);
                    }else{
                        myDialog(leave_type,from_date,to_date,address,cell,email,user_id);
                    }

                }

            }
        });


    }

    private void initImageview(){

        imgAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    browseDocuments();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initCustomSpinner() {

        //leaveSpinner = (Spinner)findViewById(R.id.leaveSpinner);
        ArrayList<String> leaveType = new ArrayList<String>();
        leaveType.add(prompt);
        leaveType.add("Vacation");
        leaveType.add("Sick");
        leaveType.add("Maternity");
        leaveType.add(other);

        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(LeaveActivity.this,leaveType);
        leaveSpinner.setAdapter(customSpinnerAdapter);

        leaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                leave_type = parent.getItemAtPosition(position).toString();
                if(position == 4){
                    edtOther.setVisibility(View.VISIBLE);
                }else{
                    edtOther.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    uri = data.getData();
                    File myFile = new File(uri.toString());
                    showToast(data.getData().getPath());

                    if (uri.toString().startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uri.toString().startsWith("file://")) {
                        displayName = myFile.getName();
                    }
                    txtAttachment.setText(displayName);
                    //String respons = uploadLeave(displayName,url,uri.toString());
                   // showToast(respons);
                }
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }


    /**
     * Select file from local storage
     *
     */
    private void browseDocuments()
    {

        String[] mimeTypes =
                {"image/jpeg", "text/plain", "application/pdf", "image/png"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 100);

    }

   /* public void open(String fullpath)
    {
        File pdfFile = new File(fullpath);
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/*");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            showToast("No Application available to view PDF");
        }
    }*/

    private void updateLabel(EditText edtText, Date dte) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        edtText.setText(sdf.format(dte));
    }



    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    public void fileUploadFunction(String fileName,String leaveType,String fromDate,String toDate, String addr, String phone,String ema,String userID) {

        filePathHolder = FilePath.getPath(this, uri);

        if (filePathHolder == null) {

            showToast("Please move your file to internal storage & try again.");

        } else {

            try {

                fileID = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(fileID);

                new MultipartUploadRequest(this, fileID, serverURL)
                        .addFileToUpload(filePathHolder, "document")
                        .addParameter("name", fileName)
                        .addParameter("leaveType", leaveType)
                        .addParameter("fromDate", fromDate)
                        .addParameter("toDate", toDate)
                        .addParameter("addr", addr)
                        .addParameter("phone", phone)
                        .addParameter("ema", ema)
                        .addParameter("userId", userID)
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(5)
                        .startUpload();

            } catch (Exception exception) {

                showToast(exception.getMessage());
            }
        }
    }

    @Override
    public void onProgress(int progress) {
        //your implementation
    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {
        //your implementation
    }

    @Override
    public void onError(Exception exception) {
        //your implementation
        showToast(exception.getMessage());
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) {
        //your implementation
        prgDialog.hide();

        try {
            String response = new String(serverResponseBody, "UTF-8");
            showToast(response);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException");
        }

    }

    @Override
    public void onCancelled() {
        //your implementation
    }

    public void AllowRunTimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(LeaveActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            showToast("READ_EXTERNAL_STORAGE permission Access Dialog");

        } else {

            ActivityCompat.requestPermissions(LeaveActivity.this,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] Result) {

        switch (RC) {

            case 1:

                if (Result.length > 0 && Result[0] == PackageManager.PERMISSION_GRANTED) {
                   // showToast("Permission Granted");

                } else {
                    showToast("Permission Canceled");

                }
                break;
        }
    }

    private boolean submitForm() {
        if (!validateCell()) {
            return false;
        }

        if (!validateEmail()) {
            return false;
        }

        if (!validateAddress()) {
            return false;
        }

        if (!validateFromDate()) {
            return false;
        }

        if (!validatetoDate()) {
            return false;
        }

        if(!validateLeave()){
            return false;
        }

        if(!validateOther()){
            return false;
        }
        return true;
    }

    private boolean validateCell() {
        if (edtCell.getText().toString().trim().isEmpty()) {
            showToast("Please enter a cell number");
            requestFocus(edtCell);
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
           showToast("Please enter valid email address");
            requestFocus(edtEmail);
            return false;
        }

        return true;
    }

    private boolean validateAddress() {
        if (edtAddress.getText().toString().trim().isEmpty()) {
             showToast("Please enter Address");
            requestFocus(edtAddress);
            return false;
        }

        return true;
    }

    private boolean validateFromDate(){
        if (edtFromDate.getText().toString().trim().isEmpty()) {
            showToast("Please Select from date");
            requestFocus(edtFromDate);
            return false;
        }
        return true;
    }

    private boolean validatetoDate(){

        if (edtToDate.getText().toString().trim().isEmpty()) {
            showToast("Please Select to date");
            requestFocus(edtToDate);
            return false;
        }
        return true;
    }
    private boolean validateLeave(){
        if(leave_type.equals(prompt)){
            showToast("Please Select a leave type");
            return false;
        }
        return true;
    }
    private boolean validateOther(){

        if(leave_type.equals(other) && edtOther.length() <1){
            showToast("Please specify a leave type");
            requestFocus(edtOther);
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void myDialog(final String leaveType, final String fromDate, final String toDate, final String addr, final String phone, final String ema,final String userId){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlerDialogTheme);
        alertDialogBuilder.setMessage("Do you want to apply without an attachment?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Upload Without Attachment
                new NoAttachment().execute(leaveType,fromDate,toDate,addr,phone,ema,userId);
            }
        });
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class NoAttachment extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){

            prgDialog.show();

        }

        protected String doInBackground(String... args) {


            try{

                URL url = new URL(serverURL);
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("leaveType", args[0]);
                postDataParams.put("fromDate", args[1]);
                postDataParams.put("toDate", args[2]);
                postDataParams.put("addr", args[3]);
                postDataParams.put("phone", args[4]);
                postDataParams.put("ema", args[5]);
                postDataParams.put("userId", args[6]);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }


            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }


        @Override
        protected void onPostExecute(String result) {

            if (prgDialog != null && prgDialog.isShowing()) {
                prgDialog.dismiss();
            }

            if(result != null){
                showToast(result);
            }else{
                showToast("Error, please ensure you have data connection");
            }

            if (responseCode == 404) {
                showToast("Requested resource not found");
            } else if (responseCode == 500) {
               showToast("Something went wrong at server end");
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context,ArrayList<String> asr) {
            this.asr=asr;
            activity = context;
        }



        public int getCount()
        {
            return asr.size();
        }

        public Object getItem(int i)
        {
            return asr.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }



        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(LeaveActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(20);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(LeaveActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(20);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.drop_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

    }
}
