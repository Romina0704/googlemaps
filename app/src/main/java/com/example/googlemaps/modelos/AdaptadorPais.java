package com.example.googlemaps.modelos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.googlemaps.DetallePaisActivity;
import com.example.googlemaps.R;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class AdaptadorPais extends ArrayAdapter<Pais> {

    public AdaptadorPais(Context context, ArrayList<Pais> datos) {
        super(context, R.layout.ly_itempais, datos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.ly_itempais, null);

        TextView nombrePais = item.findViewById(R.id.txtNombrePais);
        nombrePais.setText(getItem(position).getNombre());

        ImageView imagenBandera = item.findViewById(R.id.imgBandera);
        new DownloadImageTask(imagenBandera).execute(getItem(position).getUrl());

        item.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DetallePaisActivity.class);
            intent.putExtra("cca2", getItem(position).getCca2());
            getContext().startActivity(intent);
        });

        return item;
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
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
