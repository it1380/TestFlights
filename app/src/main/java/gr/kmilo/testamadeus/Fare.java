package gr.kmilo.testamadeus;

/**
 * Created by komis1gr on 08/08/2017.
 * "fare": {
 "total_price": "528.30",
 "price_per_adult": {
 "total_fare": "528.30",
 "tax": "340.30"
 },
 "restrictions": {
 "refundable": false,
 "change_penalties": true
 }
 }
 */

class Fare {
    String total_fare;
    Price price_per_adult;
    Price price_per_child;
    Price price_per_infant;
    Restrictions restrictions;

    public Fare(String total_fare, Price price_per_adult, Price price_per_child, Price price_per_infant, Restrictions restrictions) {
        this.total_fare = total_fare;
        this.price_per_adult = price_per_adult;
        this.price_per_child = price_per_child;
        this.price_per_infant = price_per_infant;
        this.restrictions = restrictions;
    }
}
