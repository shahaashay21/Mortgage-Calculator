package smartapp.mortgagecalculator;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by aashayshah on 3/21/17.
 */

public class LocationInfo extends AsyncTask {
    public LocationInfo() {
        super();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        StringBuilder stringBuilder = new StringBuilder();
        try {

            String finalAddress = String.valueOf(params[0]);
            Log.d("NEW", finalAddress);
//            https://maps.googleapis.com/maps/api/geocode/json?address=
//            HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?address=" + finalAddress +"&key=AIzaSyATrh3WSuilVWcQRc7npsOrMzEWopi1Rlc");
            HttpPost httppost = new HttpPost("https://maps.googleapis.com/maps/api/geocode/json?address=" + finalAddress +"&key=AIzaSyBva0BGgxEyr-3BduMVYM_hJvuTiWNIy7w");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
}
