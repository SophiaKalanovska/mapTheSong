package com.example.sophia.mapthesong;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.Pair;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private Button goGlobal;
    private EditText songName;
    private String track = "" ;

    private ArrayList<City> citiesArray = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle( this, R.raw.style_json));

        songName = (EditText) findViewById(R.id.nameOfSong);
        goGlobal = (Button) findViewById(R.id.goGlobalButton) ;



        songName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || goGlobal.callOnClick()) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(songName.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        songName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                songName.setText("");
            }
        });

        goGlobal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(songName.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                track = songName.getText().toString();


                citiesArray = getCitiesInWorld(track);
                mMap.clear();


                for (City city : citiesArray){

                    drawCircle(city);
                }
            }

        });


        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle( this, R.raw.style_json));
        if (citiesArray.size() != 0){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(citiesArray.get(0).getLat(),citiesArray.get(0).getLng())));
        }

    }



    private void drawCircle(City city){
        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(new LatLng(city.getLat(), city.getLng()));

        circleOptions.radius(Math.sqrt(city.getPeopleListnening()) * 100000);

        circleOptions.strokeColor(Color.RED);

        circleOptions.fillColor(0x30ff0000);

        circleOptions.strokeWidth(2);

        mMap.addCircle(circleOptions);

    }

    public byte[] readJsonFile(String fileAddress){
      try {
          InputStream is = getAssets().open(fileAddress);

          int size = is.available();

          byte[] buffer = new byte[size];

          is.read(buffer);

          is.close();

          return buffer;

      } catch (IOException ex) {
          ex.printStackTrace();
          Log.d("not happen", "sorry" );
      }
    }

    public ArrayList<City> getCitiesInWorld(String tracktitle){

        ArrayList<Pair<Double,Double>> latLong = new ArrayList<>();
        ArrayList<City> citiesInWorld = new ArrayList<>();


        String json = null;
        json = new String(readJsonFile("data/tags1000.json"), "UTF-8");
        if (json != null && !json.isEmpty()){
          Log.d("must happen", json );
        }

        try {
            JSONArray  mArray = new JSONArray(json);
            for (int i = 0; i<mArray.length(); i++) {
                try {
                    String tmp = mArray.getJSONObject(i).getJSONObject("match").getJSONObject("track").getJSONObject("metadata").getString("tracktitle");
                    Log.d("must happen", tmp );
                    if (tmp.equals(tracktitle)){
                        Log.d("that is lemon", tmp );
                        latLong.add(Pair.create( mArray.getJSONObject(i).getJSONObject("geolocation").getDouble("latitude") , mArray.getJSONObject(i).getJSONObject("geolocation").getDouble("longitude")));
                    }
                }
                catch (JSONException e) {
                    Log.d("exception", "why");
                    // If id doesn't exist, this exception is thrown
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        HashMap<Pair<Double,Double>,Integer> hach = new HashMap<Pair<Double,Double>,Integer>();

        for(int i=0; i< latLong .size(); i++){
            if(hach.containsKey(latLong.get(i))){
                hach.put(latLong.get(i), hach.get(latLong.get(i)) + 1);
            } else {
                hach.put(latLong.get(i), 1);
            }
        }


        for (Map.Entry<Pair<Double,Double>,Integer> pair : hach.entrySet()){
            citiesInWorld.add(new City(pair.getKey().second, pair.getKey().first, pair.getValue()));
        }
        Log.d("numberOfcities", "" + citiesInWorld.size());

        return citiesInWorld;
    }

}
