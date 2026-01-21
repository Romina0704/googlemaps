package com.example.googlemaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemaps.WebService.Asynchtask;
import com.example.googlemaps.WebService.WebService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class DetallePaisActivity extends AppCompatActivity implements Asynchtask, OnMapReadyCallback {

    private GoogleMap mMap;
    private JSONObject countryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_pais);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String cca2 = getIntent().getStringExtra("cca2");
        String url = "http://www.geognos.com/api/en/countries/info/" + cca2 + ".json";
        WebService ws = new WebService(url, new HashMap<>(), this);
        ws.execute("GET");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPais);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void processFinish(String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        countryData = json.getJSONObject("Results");

        TextView txtPaisNombre = findViewById(R.id.txtPaisNombre);
        txtPaisNombre.setText(countryData.getString("Name"));

        ImageView imgBandera = findViewById(R.id.imgBanderaPais);
        String urlBandera = "http://www.geognos.com/api/en/countries/flag/" + countryData.getJSONObject("CountryCodes").getString("iso2") + ".png";
        new DownloadImageTask(imgBandera).execute(urlBandera);

        TextView txtCapital = findViewById(R.id.txtCapital);
        txtCapital.setText(countryData.getJSONObject("Capital").getString("Name"));

        TextView txtIso2 = findViewById(R.id.txtIso2);
        txtIso2.setText(countryData.getJSONObject("CountryCodes").getString("iso2"));

        TextView txtIsoNum = findViewById(R.id.txtIsoNum);
        txtIsoNum.setText(countryData.getJSONObject("CountryCodes").getString("isoN"));

        TextView txtIso3 = findViewById(R.id.txtIso3);
        txtIso3.setText(countryData.getJSONObject("CountryCodes").getString("iso3"));

        TextView txtFips = findViewById(R.id.txtFips);
        txtFips.setText(countryData.getJSONObject("CountryCodes").getString("fips"));

        TextView txtTelPrefix = findViewById(R.id.txtTelPrefix);
        txtTelPrefix.setText(countryData.getString("TelPref"));

        TextView txtCenter = findViewById(R.id.txtCenter);
        JSONArray center = countryData.getJSONArray("GeoPt");
        txtCenter.setText(center.getDouble(0) + " " + center.getDouble(1));

        TextView txtRectangle = findViewById(R.id.txtRectangle);
        JSONObject rectangle = countryData.getJSONObject("GeoRectangle");
        txtRectangle.setText(rectangle.getDouble("West") + " " + rectangle.getDouble("North") + " " + rectangle.getDouble("East") + " " + rectangle.getDouble("South"));
        if(mMap != null) {
            updateMap();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (countryData != null) {
            updateMap();
        }
    }

    private void updateMap() {
        try {
            JSONObject rectangle = countryData.getJSONObject("GeoRectangle");
            double south = rectangle.getDouble("South");
            double west = rectangle.getDouble("West");
            double north = rectangle.getDouble("North");
            double east = rectangle.getDouble("East");

            double height = north - south;
            double width = east - west;
            double side = Math.max(height, width);

            double centerLat = (north + south) / 2;
            double centerLng = (east + west) / 2;

            double newNorth = centerLat + side / 2;
            double newSouth = centerLat - side / 2;
            double newEast = centerLng + side / 2;
            double newWest = centerLng - side / 2;

            LatLngBounds countryBounds = new LatLngBounds(
                    new LatLng(newSouth, newWest),
                    new LatLng(newNorth, newEast)
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(countryBounds, 50));

            PolygonOptions rectOptions = new PolygonOptions()
                    .add(new LatLng(newNorth, newWest),
                            new LatLng(newNorth, newEast),
                            new LatLng(newSouth, newEast),
                            new LatLng(newSouth, newWest))
                    .strokeColor(Color.BLUE)
                    .strokeWidth(5);

            mMap.addPolygon(rectOptions);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
