package gr.kmilo.testamadeus;

/**
 * Created by komis1gr on 08/08/2017.
 */

public class Restrictions {
    boolean refundable;
    boolean change_penalties;

    public Restrictions(boolean refundable, boolean change_penalties) {
        this.refundable = refundable;
        this.change_penalties = change_penalties;
    }
}
