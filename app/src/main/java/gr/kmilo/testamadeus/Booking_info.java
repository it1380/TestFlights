package gr.kmilo.testamadeus;

class Booking_info {
    String travel_class;
    String  booking_code;
    int  seats_remaining;

    public Booking_info(String travel_class, String booking_code, int seats_remaining) {
        this.travel_class = travel_class;
        this.booking_code = booking_code;
        this.seats_remaining = seats_remaining;
    }
}
