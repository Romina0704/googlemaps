package com.example.googlemaps;

import android.os.Bundle;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.googlemaps.WebService.Asynchtask;
import com.example.googlemaps.WebService.WebService;
import com.example.googlemaps.modelos.AdaptadorPais;
import com.example.googlemaps.modelos.Pais;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class mapaspaises extends AppCompatActivity implements Asynchtask {

    GridView GridViewPaises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapaspaises);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GridViewPaises = findViewById(R.id.gridViewPaises);
        Map<String, String> datos = new HashMap<String, String>();
        String url = "https://restcountries.com/v3.1/all?fields=name,cca2,flags";
        WebService ws = new WebService(url, datos, this);
        ws.execute("GET");
    }

    @Override
    public void processFinish(String result) throws JSONException {
        ArrayList<Pais> lstPaises = new ArrayList<>();
        try {
            if (result == null || result.isEmpty()) {
                // Handle empty or null response
                return;
            }
            JSONArray JSONlista = new JSONArray(result);
            for(int i=0; i< JSONlista.length();i++){
                JSONObject pais = JSONlista.getJSONObject(i);
                String nombre = pais.getJSONObject("name").getString("common");
                String urlBandera = pais.getJSONObject("flags").getString("png");
                String cca2 = pais.getString("cca2");
                lstPaises.add(new Pais(nombre, urlBandera, cca2));
            }

            AdaptadorPais adaptadorPais = new AdaptadorPais(this, lstPaises);
            GridViewPaises.setAdapter(adaptadorPais);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
