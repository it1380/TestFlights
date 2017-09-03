package gr.kmilo.testamadeus;

class Flight {
    String departs_at;
    String arrives_at;
    AirportInf origin;
    AirportInf destination;
    String marketing_airline;
    String operating_airline;
    String flight_number;
    String aircraft;
    Booking_info booking_info;

    public Flight(String departs_at, String arrives_at, AirportInf origin, AirportInf destination,
                  String marketing_airline, String operating_airline, String flight_number, String aircraft, Booking_info booking_info) {
        this.departs_at = departs_at;
        this.arrives_at = arrives_at;
        this.origin = origin;
        this.destination = destination;
        this.marketing_airline = marketing_airline;
        this.operating_airline = operating_airline;
        this.flight_number = flight_number;
        this.aircraft = aircraft;
        this.booking_info = booking_info;
    }
}