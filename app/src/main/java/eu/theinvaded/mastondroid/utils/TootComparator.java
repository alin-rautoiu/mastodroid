package eu.theinvaded.mastondroid.utils;

import java.util.Comparator;

import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 28.12.2016.
 */

public class TootComparator implements Comparator<Toot> {
    private boolean reverse;

    public TootComparator() {
        reverse = false;
    }

    public TootComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(Toot o1, Toot o2) {
        if (o1.createdAt != null && o2.createdAt != null)
            return reverse
                    ? o1.createdAt.compareTo(o2.createdAt)
                    : o2.createdAt.compareTo(o1.createdAt);
        else return 1;
    }
}
