package gr.kmilo.testamadeus;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    List<String> airportCodesOrigin = new ArrayList<>();
    List<String> airportCodesDest = new ArrayList<>();
    TextView tvIATAOrigin, tvIATADest;
    AutoCompleteTextView actvOrigin, actvDest;
    ArrayList<String> namelist;
    EditText edFrom, edTo, edCur, edAdult, edChild, edInf, edMax, edRes;
    Calendar myCalendar;
    CheckBox cbOneWay, cbNonstop;
    ProgressBar pbSearching;
    boolean searching = false, nonstop = false;
    Spinner spCur;
    SimpleDateFormat sdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        pbSearching = (ProgressBar) findViewById(R.id.pbSearching);
        pbSearching.setVisibility(View.INVISIBLE);

        //AutoCompleteTextView for Origin
        actvOrigin = (AutoCompleteTextView) findViewById(R.id.actvOrigin);
        actvOrigin.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        actvOrigin.setThreshold(1);
        actvOrigin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if((count - before) < 2 && charSequence.length() >= 3){
                        if(!searching)
                            new CallAmadeus().execute(charSequence.toString(), "1");
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvIATAOrigin = (TextView) findViewById(R.id.tvIATAOrigin);

        //AutoCompleteTextView for Destination
        actvDest = (AutoCompleteTextView) findViewById(R.id.actvDest);
        actvDest.setDropDownWidth(getResources().getDisplayMetrics().widthPixels);
        actvDest.setThreshold(1);
        actvDest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if((count - before) < 2 && charSequence.length() >= 3){
                    if(!searching)
                        new CallAmadeus().execute(charSequence.toString(), "2");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvIATADest = (TextView) findViewById(R.id.tvIATADest);

        //Date From-To
        myCalendar = Calendar.getInstance();
        edFrom = (EditText) findViewById(R.id.edFrom);
        edTo = (EditText) findViewById(R.id.edTo);
        Calendar now = Calendar.getInstance();

        String myFormat = "yyyy-MM-dd"; //In which you need put here
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        edFrom.setText(sdf.format(now.getTime()));
        now.add(Calendar.DATE,+5);
        edTo.setText(sdf.format(now.getTime()));

        final DatePickerDialog.OnDateSetListener dpListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edCur.setText(sdf.format(myCalendar.getTime()));
            }

        };

        edFrom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               new DatePickerDialog(MainActivity.this, dpListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
               edCur = edFrom;

            }
        });

        edTo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, dpListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                edTo.setText(sdf.format(myCalendar.getTime()));
                edCur = edTo;
            }
        });

        //Timepicker isnt nesscessary to work
//        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
//
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                myCalendar.set(Calendar.MINUTE, minute);
//                edTime.setText(String.format("%02d:%02d", hourOfDay, minute));
//            }
//        };
//
//
//        edTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                new TimePickerDialog(MainActivity.this, time, myCalendar
//                        .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
//            }
        //});

        cbNonstop = (CheckBox) findViewById(R.id.cbNonstop);
        cbOneWay = (CheckBox) findViewById(R.id.cbOneWay);
        cbOneWay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    edTo.setEnabled(false);
                }
                else
                    edTo.setEnabled(true);
            }
        });



        edAdult = (EditText) findViewById(R.id.edAdult);
        edChild = (EditText) findViewById(R.id.edChild);
        edInf = (EditText) findViewById(R.id.edInf);
        edMax = (EditText) findViewById(R.id.edMax);
        spCur = (Spinner) findViewById(R.id.spCur);
        edRes = (EditText) findViewById(R.id.edRes);

        Button btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(tvIATAOrigin.getText().equals(tvIATADest.getText()) || tvIATAOrigin.getText().length() < 3 || tvIATADest.getText().length() < 3) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please check origin or destination", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fdate = null, tdate = null;
                try {
                    fdate = sdf.parse(edFrom.getText().toString());
                    tdate = sdf.parse(edTo.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //zero time today
                Date dtoday = new Date();
                try {
                    dtoday = sdf.parse(sdf.format(dtoday));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(fdate == null || dtoday.after(fdate)){
                    Toast toast = Toast.makeText(getApplicationContext(), "Check departure date", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(!cbOneWay.isChecked() && fdate.after(tdate)){
                    Toast toast = Toast.makeText(getApplicationContext(), "Check return date", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(edAdult.getText().toString().equals(""))
                    edAdult.setText("0");
                if(edChild.getText().toString().equals(""))
                    edChild.setText("0");
                if(edInf.getText().toString().equals(""))
                    edInf.setText("0");

                nonstop = cbNonstop.isChecked();
                String returnDate = "";
                if(!cbOneWay.isChecked())
                    returnDate = "&return_date=" + edTo.getText();
                String s = BuildConfig.AMADEUS_API_KEY;
                String url = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?apikey="+ s +
                        "&origin=" + tvIATAOrigin.getText()+
                        "&destination=" + tvIATADest.getText() +
                        "&departure_date=" + edFrom.getText() +
                        returnDate +
                        "&adults=" + edAdult.getText() +
                        "&children=" + edChild.getText() +
                        "&infants=" + edInf.getText() +
                        "&nonstop=" + nonstop +
                        "&max_price=" + edMax.getText() +
                        "&currency=" + spCur.getSelectedItem().toString().substring(0,3) +
                        "&number_of_results=" + edRes.getText();

                Intent i = new Intent(MainActivity.this, ShowResults.class);
                i.putExtra("Url", url);
                i.putExtra("Origin", tvIATAOrigin.getText());
                i.putExtra("Destination", tvIATADest.getText());
                startActivity(i);

            }


        });

    }


    private class CallAmadeus extends AsyncTask<String, Void, String>{
        String OriginOrDest;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbSearching.setVisibility(View.VISIBLE);
            airportCodesOrigin.clear();
            airportCodesDest.clear();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            searching = false;
            pbSearching.setVisibility(View.INVISIBLE);
            if(s == null)
                return;
            namelist = new ArrayList<>();

            // Convert String to json object
            JSONArray json = null;
            try {
                json = new JSONArray(s);
                for(int i=0;  i < json.length(); i++){
                    JSONObject details = (JSONObject) json.get(i);
                    String val = details.getString("value");
                    if(OriginOrDest == "1")
                        airportCodesOrigin.add(val);
                    else
                        airportCodesDest.add(val);
                    String lab = details.getString("label");
                    namelist.add(lab);
                }

                //kanw ena adapter to opoio tha to xreiaztw me to idio format 2 fores
                ArrayAdapter<String> adpter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.my_custom_dropdown, namelist);
                //an to actvOrigin einai epilegmeno tote doulepse se ekeino
                if(OriginOrDest == "1"){
                    //to OriginOrDest einai string anti na to kanw string petaw to "="
                    actvOrigin.setAdapter(adpter);

                    actvOrigin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            tvIATAOrigin.setText(airportCodesOrigin.get((int) id));
                        }
                    });
                }
                //an to actvDest einai epilegmeno tote doulepse se ekeino
                if(OriginOrDest == "2"){
                    actvDest.setAdapter(adpter);
                    actvDest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            tvIATADest.setText(airportCodesDest.get((int) id));
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected String doInBackground(String... params) {
            OriginOrDest = params[1];
            searching = true;

            try {
                //h metabliti AMADEUS_API_KEY einai sto ~/.gradle/gradle.properties
                //sto build.gradle (app) exw balei thn build config
                URL url = new URL("https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?apikey=" + BuildConfig.AMADEUS_API_KEY +"&term=" + params[0]);
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
