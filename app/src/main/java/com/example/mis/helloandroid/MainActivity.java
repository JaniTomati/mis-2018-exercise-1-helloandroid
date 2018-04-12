package com.example.mis.helloandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {

    EditText  mInput;
    TextView  mResultText;
    ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // https://stackoverflow.com/questions/25093546/android-os-networkonmainthreadexception-at-android-os-strictmodeandroidblockgua
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mInput      = findViewById(R.id.inputField);
        mResultText = findViewById(R.id.resultView);
        mImage      = findViewById(R.id.imageView);

        mResultText.setMovementMethod(new ScrollingMovementMethod());

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void helloWorld(View view) throws IOException {
        // reset content
        mResultText.setText("");
        mImage.setImageBitmap(null);

        // https://stackoverflow.com/questions/32964827/get-text-from-a-url-using-android-httpurlconnection
        HttpURLConnection urlConnection = null;
        String result_data = "";
        String url_text = mInput.getText().toString().trim();

        Toast http_response;
        String response_string;

        if (Patterns.WEB_URL.matcher(url_text).matches()) {
            URL url = new URL(url_text);
            urlConnection = (HttpURLConnection) url.openConnection();

            int response_code = urlConnection.getResponseCode();

            String mime_type = urlConnection.getContentType();
            Toast header = Toast.makeText(MainActivity.this, mime_type, Toast.LENGTH_SHORT);
            header.show();

            if (response_code == HttpURLConnection.HTTP_OK) {
                response_string = "URL connection ok!";

                // display image types
                if (mime_type.startsWith("image/")) {
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    mImage.setImageBitmap(bmp);
                }

                // display source code
                else {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result_data += line;
                    }

                    in.close();
                    mResultText.setText(result_data);
                }

                urlConnection.disconnect();

            } else if (response_code == HttpURLConnection.HTTP_NOT_FOUND) {
                response_string = "URL not found!";
            } else if (response_code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                response_string = "Gateway Timeout!";
            } else if (response_code == HttpURLConnection.HTTP_BAD_GATEWAY) {
                response_string = "Bad Gateway!";
            } else {
                response_string = "Whoops! Something went wrong!";
            }

            http_response = Toast.makeText(MainActivity.this, response_string, Toast.LENGTH_SHORT);
            http_response.setGravity(Gravity.CENTER, 0, 0);
            http_response.show();


        } else {
            Toast invalid_url = Toast.makeText(MainActivity.this, "Invalid URL!", Toast.LENGTH_SHORT);
            invalid_url.setGravity(Gravity.CENTER, 0, 0);
            invalid_url.show();
        }
    }

    public void resetInputField(View view) {
        mInput.setText("");
    }
}


