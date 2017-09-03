package gr.kmilo.testamadeus;

/**
 * Created by komis1gr on 08/08/2017.
 */

class Price {
    private String price_per_person;
    private String tax;

    public Price(String price_per_person, String tax) {
        this.price_per_person = price_per_person;
        this.tax = tax;
    }
}
