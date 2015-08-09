package com.yuvaraj.volleytutorial;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    int pageCount = 0;

    String dynamicUrl = "http://www.chennaiyil.com/?json=1&post_type=item&page=";

    String tag_json_obj = "json_obj_req";
    ProgressDialog pDialog;
    String TAG = MainActivity.class.getSimpleName();
    ListView listView;
    ArrayList<String> stringArrayList;
    ArrayAdapter<String> adapter;
    CustomAdapter customAdapter;
    boolean userScrolled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView);
        stringArrayList = new ArrayList<String>();
        Volleyoperation();

    }

    private void Volleyoperation() {

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setTitle("Volley Operation");
        pDialog.setMessage("Loading...");
        pDialog.show();
        pageCount = pageCount + 1;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                dynamicUrl+pageCount, (String)null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray jsonArray = response.getJSONArray("posts");
                            int inital_count = 1;
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.d("track", "" + object.getString("title_plain"));
                                //  stringArrayList.add(""+object.getInt("id"));
                                String title_plain = inital_count + ". " + object.getString("title_plain");
                                stringArrayList.add(title_plain);
                                inital_count++;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("track", "done");
                       // adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, stringArrayList);
                        //listView.setAdapter(adapter);
                        customAdapter = new CustomAdapter(MainActivity.this,stringArrayList);
                        listView.setAdapter(customAdapter);
                        listView.setOnScrollListener(MainActivity.this);


                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                VolleyLog.d(TAG, "error msg" +error.networkResponse);
                Log.d("track", "Error");

                if(error.networkResponse == null){
                    Log.d("track","Time out");
                }
                pDialog.hide();
            }
        });
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL){
            userScrolled = true;
        }

    }

    @Override
    public void onScroll(AbsListView absListView, int firstvisibleitem, int visibleitemcount, int totalitemcount) {



        if (totalitemcount > 0) {
            if (firstvisibleitem + visibleitemcount == totalitemcount && userScrolled) {
                Log.d("track", "firstvisibleitem" + firstvisibleitem);
                Log.d("track", "visibleitemcount" + visibleitemcount);
                Log.d("track", "totalitemcount" + totalitemcount);
                Log.d("track", "Load more");
                    doMoreVolleyOperation();
                    userScrolled = false;
            }
        }
    }



    private void doMoreVolleyOperation() {

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setTitle("Doing more Volley Operation");
        pDialog.setMessage("Loading...");
        pDialog.show();
        pageCount = pageCount + 1;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                dynamicUrl+pageCount, (String)null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                      //  Log.d(TAG, response.toString());

                        try {
                            JSONArray jsonArray = response.getJSONArray("posts");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                Log.d("track", "" + object.getString("title_plain"));
                                //  stringArrayList.add(""+object.getInt("id"));
                                int arrayListSize = stringArrayList.size();
                                stringArrayList.add((arrayListSize+1) + ". " + object.getString("title_plain"));
                                arrayListSize++;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }




                        Log.d("track", "done two");
                        customAdapter.notifyDataSetChanged();

                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                VolleyLog.d(TAG, "error msg" +error.networkResponse);
                Log.d("track", "Error");

                if(error.networkResponse == null){
                    Log.d("track","Time out");
                }
                pDialog.hide();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }
}
