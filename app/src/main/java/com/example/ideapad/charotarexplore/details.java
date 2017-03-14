package com.example.ideapad.charotarexplore;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

public class details extends AppCompatActivity {
    cached cached = new cached();
    location_details location_details = new location_details();
    private AlertDialog rates;
    private MediaPlayer mediaPlayer;
    private ProgressDialog favorite;
    private String id, catid, name, number, address, time, lat, lon, image;
    private ImageView rimage, fav, more;
    private TextView call, direction, timing, add;
    private FloatingActionButton shareIt;
    ColorFilter white = new LightingColorFilter(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
    ColorFilter red = new LightingColorFilter(Color.parseColor("#ff0000"), Color.parseColor("#ff0000"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mediaPlayer = MediaPlayer.create(this, R.raw.favorite);
        favorite = new ProgressDialog(this);
        favorite.setTitle("Favorite");

        id = getIntent().getStringExtra("id");
        catid = getIntent().getStringExtra("catid");
        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");
        number = number.trim().replace(" ", "");
        address = getIntent().getStringExtra("address");
        time = getIntent().getStringExtra("time");
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        image = getIntent().getStringExtra("image");

        getSupportActionBar().setTitle(name);

        rimage = (ImageView) findViewById(R.id.rimage);
        fav = (ImageView) findViewById(R.id.fav);
        if (result.from.equals("home"))
            fav.setColorFilter(red);
        else
            fav.setColorFilter(white);
        call = (TextView) findViewById(R.id.call);
        direction = (TextView) findViewById(R.id.direction);
        timing = (TextView) findViewById(R.id.timing);
        add = (TextView) findViewById(R.id.add);
        more = (ImageView) findViewById(R.id.more);

        Glide.with(details.this).load(image).into(rimage);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fav.getColorFilter() == red) {
                    fav.setColorFilter(white);
                    favorite.setMessage("Removing from favorites...");
                    favorite.show();
                    removefavapi();
                } else if (fav.getColorFilter() == white) {
                    mediaPlayer.start();
                    fav.setColorFilter(red);
                    favorite.setMessage("Adding to favorites...");
                    favorite.show();
                    addtofavapi();
                }
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            }
        });
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + location_details.getLatitude() + "," + location_details.getLongitude() + "&daddr=" + lat + "," + lon));
                startActivity(intent);
            }
        });
        timing.setText(time.trim());
        add.setText(address.trim());

        if (catid.equals("2")) {
            more.setVisibility(View.VISIBLE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(details.this);
        builder.setCancelable(true);
        builder.setTitle("Rates");
        builder.setMessage("Inflation: 7.50\nBank rate: 6.75%\nCRR: 4.000%\nSLR: 20.50%\nRepo rate: 6.25%\nReverse repo rate: 5.75%\nMarginal Standing facility rate: 6.75%");
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rates.dismiss();
                    }
                });
        rates = builder.create();
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rates.show();
            }
        });

        shareIt = (FloatingActionButton) findViewById(R.id.shareIt);
        shareIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, name);
                String sAux = "Hey! Check-out " + name + " at:\nhttps://maps.google.com/?q=" + lat + "," + lon + "\n(or Contact them using: " + number + ")";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "Share app using:"));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toHome:
                Intent intent = new Intent(details.this, home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        toBack();
    }

    public void toBack() {
        Intent intent = new Intent(details.this, result.class);
        if (result.from.equals("home"))
            intent.putExtra("from", "home");
        else if (result.from.equals("category"))
            intent.putExtra("from", "category");
        startActivity(intent);
    }

    public void addtofavapi() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.link) + "addTofav";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(details.this, "Added to favourites.", Toast.LENGTH_SHORT).show();
                favorite.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
                favorite.dismiss();
                Toast.makeText(details.this, "Something is wrong.", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("uid", cached.getUser_id(getApplicationContext()));
                MyData.put("rid", id);
                return MyData;
            }
        };
        requestQueue.add(MyStringRequest);
    }

    public void removefavapi() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.link) + "deleteFav";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(details.this, "Removed from favourites.", Toast.LENGTH_SHORT).show();
                favorite.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
                favorite.dismiss();
                Toast.makeText(details.this, "Something is wrong.", Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("uid", cached.getUser_id(getApplicationContext()));
                MyData.put("rid", id);
                return MyData;
            }
        };
        requestQueue.add(MyStringRequest);
    }
}
