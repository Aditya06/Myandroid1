package hk.ust.cse.comp107x.chatclient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contacts extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG="Contacts";

    ListView friendView;

    ContactArrayAdapter mArrayAdapter;

    // This class stores all the information about your friend
    public class FriendInfo {
        int id;
        String name;
        String statusMsg;
        String imageURL;
    }

    List<FriendInfo> friendInfoList = null;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mContext = this;

        mArrayAdapter = new ContactArrayAdapter(this, friendInfoList);


        friendView = (ListView) findViewById(R.id.friendListView);
        friendView.setOnItemClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Should set up to add new contacts", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        if (isOnline()) {
            FriendsProcessor mytask = new FriendsProcessor();
            mytask.execute(Constants.JSON_URL);
        }
        else {
            // Toast displays the message on the screen for a period of time
            Toast.makeText(this, "You are Offline! Turn on your network!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        Intent mIntent = new Intent(this,ChatClient.class);
        TextView friendName = (TextView) view.findViewById(R.id.friendName);
        mIntent.putExtra(getString(R.string.friend), friendName.getText().toString());
        startActivity(mIntent);

    }

    private class FriendsProcessor extends AsyncTask<String, Void, Integer> {

        ProgressDialog progressDialog;

        public FriendsProcessor() {
            super();
        }

     
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show the progress dialog on the screen
            progressDialog = ProgressDialog.show(mContext, "Wait!","Downloading Friends List");
        }


        @Override
        protected Integer doInBackground(String... urls) {

            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            Integer result = 0;

            try {
            // TODO connect to server, download and process the JSON string
                URL url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestMethod("GET");
                int stausCode=urlConnection.getResponseCode();
                if(stausCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response=convertInputStreamToString(inputStream);
                    processFriendInfo(response);
                    result=1;
                }
                else{
                    result = 0;
                }


            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            mArrayAdapter = new ContactArrayAdapter(mContext, friendInfoList);
            friendView.setAdapter((ListAdapter) mArrayAdapter);

            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            progressDialog.dismiss();
        }
    }

   
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }
        return result;
    }

    private void processFriendInfo(String infoString) {

       
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

       
        friendInfoList = new ArrayList<FriendInfo>();
        friendInfoList = Arrays.asList(gson.fromJson(infoString, FriendInfo[].class));
    }
}
