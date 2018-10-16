package org.vaadin.calendar.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

  public static Date getBeginingOfMonthFrom(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.DATE, 1);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    // Find the first monday
    while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
      c.add(Calendar.DATE, -1);
    return c.getTime();
  }

  // Goes
  public static Date getEndOfMonthFrom(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(getBeginingOfMonthFrom(date));
    c.add(Calendar.MONTH, 1);
    c.add(Calendar.SECOND, -1);
    // Find sunday
    while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
      c.add(Calendar.DATE, 1);
    return c.getTime();
  }
}
