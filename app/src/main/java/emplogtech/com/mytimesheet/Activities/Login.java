package emplogtech.com.mytimesheet.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import Classes.SessionManager;
import emplogtech.com.mytimesheet.R;

public class Login extends Activity {

    private EditText mail,pass;
    private ProgressDialog pDialog;
    SessionManager session;

    int responseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());

        mail = (EditText)findViewById(R.id.edtmail);
        pass = (EditText)findViewById(R.id.edtPass);

    }


    public  void login(View v){

        String email,password;
        email = mail.getText().toString();
        password = pass.getText().toString();
        if(email.length() >0 && password.length() > 0 ){
            new SendRequest().execute(email,password);
        }else
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){

            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Attempting login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            try{

                URL url = new URL("http://nexgencs.co.za/devApi/ness_login.php");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", args[0]);
                postDataParams.put("password", args[1]);
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

            int success = 0;
            String message = "";
            String name="",email;
            int id=0,compID=0,deptID=0;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if(result != null){
                try{

                    JSONObject object = new JSONObject(result);
                    success = object.getInt("success");
                    message = object.getString("message");
                    if(success == 1){
                    id = object.getInt("id");
                    compID = object.getInt("compID");
                    deptID = object.getInt("deptID");
                    name = object.getString("name");
                    }



                }catch (JSONException e){
                    //Toast.makeText(getApplicationContext(), "Error Occurred [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e("RESULT++++++:",result);
                    Log.e("RESPONSE CODE ++++++:",String.valueOf(responseCode));
                    if (responseCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (responseCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unexpected Error occurred! please ensure your device is connected to the Internet]",
                                Toast.LENGTH_LONG).show();
                    }
                }

            }

            if(success == 1){

                session.createLoginSession(name,String.valueOf(compID),String.valueOf(id),String.valueOf(deptID));
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
                Log.e("RESULT++++++:",result);

            }else{

                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
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
}
