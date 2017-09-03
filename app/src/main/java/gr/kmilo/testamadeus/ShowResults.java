package gr.kmilo.testamadeus;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class ShowResults extends AppCompatActivity {
    ProgressBar pbSearching;
    TextView tvRes, tvQue, tvNRes;
    HashMap<String, String> airlinesMap = new HashMap<>();
    boolean searchingAirlines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_results);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        Bundle extras = getIntent().getExtras();
        String url = extras.getString("Url");
        pbSearching = (ProgressBar) findViewById(R.id.pbSearching);

        tvRes = (TextView) findViewById(R.id.tvRes);
        tvQue = (TextView) findViewById(R.id.tvQue);
        tvNRes = (TextView) findViewById(R.id.tvResults);
        new searchAirlines().execute();
        new searchFlights().execute(url);

    }

    class searchFlights extends AsyncTask<String, Void, List<Result>> {
        String currency = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbSearching.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(List<Result> s) {
            super.onPostExecute(s);
            int retries = 0;
            while(searchingAirlines){
                try {
                    Thread.sleep(50);
                    if(retries++ > 200){
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pbSearching.setVisibility(View.INVISIBLE);
            if(searchingAirlines){
                return;
            }

            String resCount = "";
            if(s != null){

                tvQue.setText("Origin: " + getIntent().getExtras().getString("Origin") + "\tDestination: " + getIntent().getExtras().getString("Destination") );
                tvNRes.setText("Results: " + s.size() );
            }


            if(s == null){
                tvRes.append("Sorry no results");
                return;
            }

            int j=1;
            Spannable txt;
            for(Result res: s){
                txt = new SpannableString("Result " + j++ + "\n");
                txt.setSpan(new ForegroundColorSpan(Color.BLUE), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt.setSpan(new StyleSpan(Typeface.BOLD), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                txt.setSpan(new UnderlineSpan(), 0, txt.length(), 0);
                tvRes.append(txt);

                int i=1;
                for(Itinerary it : res.itineraries){
                    tvRes.append("Itinerary " + i++ + "\n");
                    for(Flight fo : it.outbound){
                        txt = new SpannableString("Outbound:\n");
                        txt.setSpan(new ForegroundColorSpan(0xffE74C3C), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvRes.append(txt);
                        tvRes.append("\t" + fo.origin.airport + "   " + fo.origin.terminal + "\t\t-->\t\t" + fo.destination.airport + "   " + fo.origin.terminal + "\n");
                        tvRes.append("\t" + fo.departs_at.replace("T"," ") + "\t    " + fo.arrives_at.replace("T"," ") + "\n");
                        tvRes.append("\tAirline: " + fo.operating_airline + " " + fo.flight_number + "(" + airlinesMap.get(fo.operating_airline) + ")\n");
                        tvRes.append("\tSeats Remaining: " + fo.booking_info.seats_remaining + "\n");
                    }
                    if(it.inbound != null) {
                        for (Flight fi : it.inbound) {
                            txt = new SpannableString("Inbound:\n");
                            txt.setSpan(new ForegroundColorSpan(0xff5DADE2), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tvRes.append(txt);
                            tvRes.append("\t" + fi.origin.airport + "   " + fi.origin.terminal + "\t\t-->\t\t" + fi.destination.airport + "   " + fi.origin.terminal + "\n");
                            tvRes.append("\t" + fi.departs_at.replace("T", " ") + "\t    " + fi.arrives_at.replace("T", " ") + "\n");
                            tvRes.append("\tAirline: " + fi.operating_airline + " " + fi.flight_number + "(" + airlinesMap.get(fi.operating_airline) + ")\n");
                            tvRes.append("\tSeats Remaining: " + fi.booking_info.seats_remaining + "\n");
                        }
                    }

                }
                txt = new SpannableString("\tPrice: " + res.fare.total_fare + " " + currency + "\n");
                txt.setSpan(new ForegroundColorSpan(0xff196F3d ), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //dark green
                txt.setSpan(new StyleSpan(Typeface.BOLD), 0, txt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvRes.append(txt);
            }
        }

        @Override
        protected List<Result> doInBackground(String... params) {
            List<Result> results = null;
            try {
                URL url = new URL(params[0]);
                //call amadeus , parse results
                JSONParser jparser = new JSONParser();
                results = jparser.getJSONFromUrl(url);
                currency = jparser.currency;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return results;
        }

    }

    class searchAirlines extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchingAirlines = true;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject json;

            try {
                json = new JSONObject(s);
                JSONArray jsona = json.getJSONArray("response");
                for(int i=0;  i < jsona.length(); i++) {
                   JSONObject jscode = (JSONObject) jsona.get(i);
                    String code = jscode.getString("code");
                    String name = jscode.getString("name");
                    airlinesMap.put(code, name);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchingAirlines = false;

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://iatacodes.org/api/v6/airlines?api_key=4f69e7eb-4c36-4d79-ba5a-b627f9587525");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }



    }



}
