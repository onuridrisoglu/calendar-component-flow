package org.vaadin.onur;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.vaadin.calendar.CalendarItemTheme;
import org.vaadin.calendar.data.AbstractCalendarDataProvider;
import org.vaadin.onur.DemoView.EventData;

public class CustomDataProvider extends AbstractCalendarDataProvider<DemoView.EventData> {

  @Override
  public Collection<DemoView.EventData> getItems(Date fromDate, Date toDate) {
    Date index = fromDate;
    List<DemoView.EventData> events = new ArrayList<>();
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    while (index.before(toDate)) {
      events.add(new EventData(index, df.format(index), CalendarItemTheme.LightBlue));
      index = DateUtils.addDays(index, 1);
    }
    return events;
  }

}
