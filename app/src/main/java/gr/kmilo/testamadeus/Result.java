package gr.kmilo.testamadeus;

import java.util.List;

/**
 * Created by komis1gr on 08/08/2017.
 *
 */
class Result {
    List<Itinerary> itineraries;
    Fare fare;

    public Result(List<Itinerary> itineraries, Fare fare) {
        this.itineraries = itineraries;
        this.fare = fare;
    }

}
