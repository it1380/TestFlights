package gr.kmilo.testamadeus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.JsonReader;
import android.util.Log;

/**
 * Created by komis1gr on 08/08/2017.
 * read amadeus response
 */
class JSONParser {
    static JSONObject jObj = null;
    static String json = "";
    String currency="";
    // constructor
    public JSONParser() {

    }

    public List<Result> getJSONFromUrl(URL url) {

        // Making HTTP request
        JsonReader jsonReader = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                return readJsonStream(urlConnection.getInputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Result> readJsonStream(InputStream in) throws IOException {
        List<Result> results = null;
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("currency")) {
                    currency = reader.nextString();
                }
                if (name.equals("results")) {
                    results = readResultsArray(reader);
                }
            }
        }catch(Exception e){
                e.printStackTrace();
        } finally {
            reader.close();
        }
        return results;
    }

    private List<Result> readResultsArray(JsonReader reader) throws IOException {
        List<Result> results = new ArrayList<Result>();

        reader.beginArray();
        while (reader.hasNext()) {
            results.add(readResult(reader));
        }
        reader.endArray();
        return results;
    }

    private Result readResult(JsonReader reader) throws IOException {
        String text = null;
        List<Itinerary> itineraries = null;
        Fare fare = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("itineraries")) {
                itineraries = readItinerariesArray(reader);
            }
            else if (name.equals("fare")) {
                fare = readFare(reader);
            }
        }

        reader.endObject();
        return new Result(itineraries, fare);
    }

    private List<Itinerary> readItinerariesArray(JsonReader reader) throws IOException {
        List<Itinerary> itineraries = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            itineraries.add(readItinerary(reader));
        }
        reader.endArray();
        return itineraries;
    }

    private Itinerary readItinerary(JsonReader reader) throws IOException {
        List<Flight> outbounds = null;
        List<Flight> inbounds = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("outbound")) {
                reader.beginObject();
                name = reader.nextName();
                if(name.equals("flights"))
                    outbounds = readFlightsArray(reader);
                reader.endObject();
            }
            else if (name.equals("inbound")) {
                reader.beginObject();
                name = reader.nextName();
                if(name.equals("flights"))
                    inbounds = readFlightsArray(reader);
                reader.endObject();
            }

        }

        reader.endObject();
        return new Itinerary(outbounds, inbounds);
    }

    private List<Flight> readFlightsArray(JsonReader reader) throws IOException {
        List<Flight> flights = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            flights.add(readFlight(reader));
        }
        reader.endArray();
        return flights;
    }

    private Flight readFlight(JsonReader reader) throws IOException {
        String departs_at = null;
        String arrives_at = null;
        AirportInf origin = null;
        AirportInf destination = null;
        String marketing_airline = null;
        String operating_airline = null;
        String flight_number = null;
        String aircraft = null;
        Booking_info booking_info = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "departs_at":
                    departs_at = reader.nextString();
                    break;
                case "arrives_at":
                    arrives_at = reader.nextString();
                    break;
                case "origin":
                    String airport = "", terminal = "";
                    reader.beginObject();
                    reader.nextName();
                    airport = reader.nextString();
//                    if(reader.nextName().equals("airport")){
//                        airport = reader.nextString();
//                    }
                    if(reader.hasNext()){
                        reader.nextName();
                        terminal = reader.nextString();
                    }

                    origin = new AirportInf(airport, terminal);

                    reader.endObject();
                    break;
                case "destination":
                    String airport1 = "", terminal1 = "";
                    reader.beginObject();
                    reader.nextName();
                    airport1 = reader.nextString();
                    if(reader.hasNext()){
                        reader.nextName();
                        terminal1 = reader.nextString();
                    }
                    destination = new AirportInf(airport1, terminal1);
                    reader.endObject();
                    break;
                case "marketing_airline":
                    marketing_airline = reader.nextString();
                    break;
                case "operating_airline":
                    operating_airline = reader.nextString();
                    break;
                case "flight_number":
                    flight_number = reader.nextString();
                    break;
                case "aircraft":
                    aircraft = reader.nextString();
                    break;
                case "booking_info":
                    reader.beginObject();
                    String travelClass = "", bookingCode = "";
                    int seatsRemaining = 0;
                    reader.nextName();
                    travelClass = reader.nextString();
                    reader.nextName();
                    bookingCode = reader.nextString();
                    reader.nextName();
                    seatsRemaining = reader.nextInt();
                    booking_info = new Booking_info(travelClass, bookingCode, seatsRemaining);
                    reader.endObject();
                    break;
            }
        }

        reader.endObject();
        return new Flight(departs_at,arrives_at,origin,destination,marketing_airline,operating_airline,flight_number,aircraft,booking_info);
    }


    private Fare readFare(JsonReader reader) throws IOException {
        String total_fare = null;
        Price price_per_adult = null;
        Price price_per_child = null;
        Price price_per_infant = null;
        Restrictions restrictions = null;
        reader.beginObject();
        String price, tax;
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "total_price":
                    total_fare = reader.nextString();
                    break;
                case "price_per_adult":
                    reader.beginObject();
                    reader.nextName();
                    price = reader.nextString();
                    reader.nextName();
                    tax = reader.nextString();
                    price_per_adult = new Price(price, tax);
                    reader.endObject();
                    break;
                case "price_per_child":
                    reader.beginObject();
                    reader.nextName();
                    price = reader.nextString();
                    reader.nextName();
                    tax = reader.nextString();
                    price_per_child = new Price(price, tax);
                    reader.endObject();
                    break;
                case "price_per_infant":
                    reader.beginObject();
                    reader.nextName();
                    price = reader.nextString();
                    reader.nextName();
                    tax = reader.nextString();
                    price_per_infant = new Price(price, tax);
                    reader.endObject();
                    break;
                case "restrictions":
                    reader.beginObject();
                    boolean refundable, penalties;
                    reader.nextName();
                    refundable = reader.nextBoolean();
                    reader.nextName();
                    penalties = reader.nextBoolean();
                    restrictions = new Restrictions(refundable, penalties);
                    reader.endObject();
                    break;
            }
        }
        reader.endObject();
        return new Fare(total_fare,price_per_adult,price_per_child,price_per_infant,restrictions);
    }
    

}
