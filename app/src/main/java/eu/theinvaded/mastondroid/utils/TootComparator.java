package eu.theinvaded.mastondroid.utils;

import java.util.Comparator;

import eu.theinvaded.mastondroid.model.Toot;

/**
 * Created by alin on 28.12.2016.
 */

public class TootComparator implements Comparator<Toot> {
    @Override
    public int compare(Toot o1, Toot o2) {
        return o2.createdAt.compareTo(o1.createdAt);
    }
}
