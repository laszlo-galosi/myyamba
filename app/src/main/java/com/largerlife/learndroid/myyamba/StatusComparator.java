package com.largerlife.learndroid.myyamba;

import java.util.Comparator;
import java.util.Date;
import winterwell.jtwitter.Status;

/**
 * Created by László Gálosi on 18/03/16
 */
public class StatusComparator implements Comparator<Status> {
    @Override
    public int compare(final Status objectOne, final Status objectTwo) {
        Date dateTimeOne = objectOne != null ? objectOne.createdAt : new Date();
        Date dateTimeTwo = objectTwo != null ? objectTwo.createdAt : new Date();
        return dateTimeTwo.compareTo(dateTimeOne);
    }
}
