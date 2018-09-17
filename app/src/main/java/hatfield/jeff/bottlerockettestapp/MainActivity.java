package hatfield.jeff.bottlerockettestapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String JSON_URL = "http://sandbox.bottlerocketapps.com/BR_Android_CodingExam_2015_Server/stores.json";

    private ConstraintLayout frame;
    private TextView myName;
    private Switch modeSwitch;
    private CircleImageView getButton;
    private DualProgressView progressView;
    private RecyclerView recyclerView;
    private Adapter mAdapter;

    private ArrayList<StoreObject> storeObjects = new ArrayList<>();
    private AnimatorSet crossFade;

    private static boolean isLightMode = false;
    private static int backgroundColor, textColor;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frame = findViewById(R.id.background);
        myName = findViewById(R.id.my_name);
        modeSwitch = findViewById(R.id.mode_switch);
        getButton = findViewById(R.id.get_button);
        progressView = findViewById(R.id.progress_view);
        recyclerView = findViewById(R.id.snapping_recycler_list);

        mAdapter = new Adapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        backgroundColor = getColor(R.color.color_background_dark);
        textColor = getColor(R.color.color_text_dark);

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLightMode = isChecked;

                backgroundColor = isLightMode ?
                        getColor(R.color.color_background_light) :
                        getColor(R.color.color_background_dark);

                textColor = isLightMode ?
                        getColor(R.color.color_text_light) :
                        getColor(R.color.color_text_dark);

                frame.setBackgroundColor(backgroundColor);

                myName.setTextColor(textColor);

                modeSwitch.setText(isLightMode ?
                        "Light" :
                        "Dark");

                modeSwitch.setTextColor(textColor);

                progressView.setInnerCircleColor(isLightMode ?
                        getColor(R.color.color_progress_inner_ring_light) :
                        getColor(R.color.color_progress_inner_ring_dark));

                progressView.setOuterCircleColor(isLightMode ?
                        getColor(R.color.color_progress_outer_ring_light) :
                        getColor(R.color.color_progress_outer_ring_dark));

                mAdapter.notifyDataSetChanged();
            }
        });

        crossFade = Animations.crossFadeAnimation(progressView, getButton, 500, 50);
        crossFade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new JsonTask().execute(JSON_URL);
            }
        });

        getButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        getButton.setImageResource(R.drawable.circle_button_down);
                        return true;
                    case MotionEvent.ACTION_UP:
                        getButton.setImageResource(R.drawable.circle_button_up);
                        progressView.resetAnimation();
                        crossFade.start();
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        return true;
                }
                return false;
            }
        });
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                returnBackToMain("Malformed URL");
                e.printStackTrace();
            } catch (IOException e) {
                returnBackToMain("Error while connecting to URL");
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    returnBackToMain("Error while connecting to URL");
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            if(result != null){
                Log.d(TAG, "RESULT: "+result);

                AnimatorSet crossFade = Animations.crossFadeAnimation(recyclerView, progressView, 500, 0);
                crossFade.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            for (int i = 0; i < jsonObject.getJSONArray("stores").length(); i++) {
                                storeObjects.add(new StoreObject(jsonObject.getJSONArray("stores").get(i)));
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getButton.setOnTouchListener(null);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
                crossFade.start();
            }
        }

        @Override
        protected void onCancelled(String result) {
            returnBackToMain("Fetch was cancelled");

        }
    }

    private void returnBackToMain(final String errorString){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,
                        errorString,
                        Toast.LENGTH_LONG).show();
                AnimatorSet returnBackAnimatorSet = Animations.crossFadeAnimation(getButton, progressView, 500, 0);
                returnBackAnimatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                });
                returnBackAnimatorSet.start();
            }
        });

    }

    protected class Adapter extends RecyclerView.Adapter<Adapter.ItemViewHolder> {

        private final LayoutInflater inflater;

        public Adapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(inflater.inflate(R.layout.store_info_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, int position) {
            Glide.with(holder.storeLogo.getContext())
                    .load(storeObjects.get(position).getLogoURL())
                    .into(holder.storeLogo);
            holder.storeName.setText(storeObjects.get(position).getName().length() > 15?
                    storeObjects.get(position).getName() + "\n" + storeObjects.get(position).getStoreID() :
                    storeObjects.get(position).getName() + " - " + storeObjects.get(position).getStoreID());
            holder.storePhone.setText(storeObjects.get(position).getPhone());
            holder.storePhone.setPaintFlags(holder.storePhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.storeAddress.setText(storeObjects.get(position).getFullAddress());
            holder.storeLatLong.setText("(" + storeObjects.get(position).getLatitude() + ", " + storeObjects.get(position).getLongitude() + ")");


            holder.storeInfoLayout.setBackgroundColor(isLightMode ?
                    getColor(R.color.color_item_background_light) :
                    getColor(R.color.color_item_background_dark));
            holder.storeName.setTextColor(textColor);
            holder.storePhone.setTextColor(textColor);
            holder.storePhone.setTextColor(textColor);
            holder.storeAddress.setTextColor(textColor);
            holder.storeLatLong.setTextColor(textColor);
        }

        @Override
        public int getItemCount() {
            return storeObjects.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public ConstraintLayout storeInfoLayout;
            public ImageView storeLogo;
            public TextView storeName;
            public TextView storePhone;
            public TextView storeAddress;
            public TextView storeLatLong;

            public ItemViewHolder(final View itemView) {

                super(itemView);

                storeInfoLayout = (ConstraintLayout) itemView;
                storeLogo = storeInfoLayout.findViewById(R.id.store_logo);
                storeName = storeInfoLayout.findViewById(R.id.name_store_id);
                storePhone = storeInfoLayout.findViewById(R.id.phone);
                storeAddress = storeInfoLayout.findViewById(R.id.address);
                storeLatLong = storeInfoLayout.findViewById(R.id.lat_long);



                storeInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String[] colors = {"Find in Maps", "Call Location"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Pick an option");
                        builder.setItems(colors, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int option) {
                                switch(option){
                                    case 0:
                                        String map = "http://maps.google.co.in/maps?q=" + storeAddress.getText();
                                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                                        MainActivity.this.startActivity(i);
                                        break;
                                    case 1:
                                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            intent.setData(Uri.parse("tel:" + storePhone.getText()));
                                            MainActivity.this.startActivity(intent);
                                        }else{
                                            ActivityCompat.requestPermissions(MainActivity.this,
                                                    new String[]{Manifest.permission.CALL_PHONE},
                                                    1);
                                        }
                                        break;

                                }
                            }
                        });
                        builder.show();
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                            "You can now call store locations by tapping on the phone number",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Permission denied to use your phone",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
