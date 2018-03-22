package emplogtech.com.mytimesheet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import emplogtech.com.mytimesheet.R;

public class LeaveActivity extends AppCompatActivity {

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


    Calendar fromCalender,toCalender;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        ButterKnife.bind(this);
        initCustomSpinner();
        initImageview();

        fromCalender = Calendar.getInstance();
        toCalender= Calendar.getInstance();


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    File myFile = new File(uri.toString());
                    String path = myFile.getAbsolutePath();
                    String displayName = null;
                    showToast(data.getData().getPath());
                    open(uri);

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
                }
                break;
        }

    }


    /**
     * Select file from local storage
     *
     */
    private void browseDocuments()
    {

        String[] mimeTypes =
                {"application/jpg", "text/plain", "application/pdf", "application/png"};

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

    public void open(Uri path)
    {
       /* File pdfFile = new File(fullpath);
        Uri path = Uri.fromFile(pdfFile);*/
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/*");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            showToast("No Application available to view PDF");
        }
    }

    private void updateLabel(EditText edtText, Date dte) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        edtText.setText(sdf.format(dte));
    }

    private void initCustomSpinner() {

        //leaveSpinner = (Spinner)findViewById(R.id.leaveSpinner);
        ArrayList<String> leaveType = new ArrayList<String>();
        leaveType.add("--Select Leave Type--");
        leaveType.add("Vacation");
        leaveType.add("Sick");
        leaveType.add("Maternity");
        leaveType.add("Other (Specify)");

        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(LeaveActivity.this,leaveType);
        leaveSpinner.setAdapter(customSpinnerAdapter);

        leaveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
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

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
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
