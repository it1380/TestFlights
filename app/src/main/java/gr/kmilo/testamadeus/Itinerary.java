package gr.kmilo.testamadeus;

import java.util.List;

/**
 * Created by komis1gr on 08/08/2017.
 *
 */

public class Itinerary {
    List<Flight> outbound;
    List<Flight> inbound;

    public Itinerary(List<Flight> outbound, List<Flight> inbound) {
        this.outbound = outbound;
        this.inbound = inbound;
    }
}
