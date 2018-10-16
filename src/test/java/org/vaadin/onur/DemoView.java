package org.vaadin.onur;

import java.util.Date;

import org.vaadin.calendar.CalendarComponent;
import org.vaadin.calendar.CalendarItemTheme;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

@Route("")
public class DemoView extends Div {
  private CalendarComponent<EventData> calendar;

  public DemoView() {
    calendar = new CalendarComponent<EventData>().withItemDateGenerator(e -> e.getDate())
        .withItemLabelGenerator(e -> e.getLabel());
    calendar.withItemThemeGenerator(e -> e.getTheme());
    calendar.setItems(new EventData(new Date(), "First Event", CalendarItemTheme.LightGreen),
        new EventData(new Date(), "Second Event", CalendarItemTheme.LightBlue),
        new EventData(new Date(), "Third Event", CalendarItemTheme.Blue));
    // calendar.setDataProvider(new CustomDataProvider());
    calendar.addEventClickListener(evt -> Notification.show("" + evt.getDetail().getLabel()));
    add(calendar);
  }

  public static class EventData {
    private Date date;
    private String label;
    private CalendarItemTheme theme;

    public EventData(Date d, String lbl, CalendarItemTheme thm) {
      date = d;
      label = lbl;
      theme = thm;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }

    public CalendarItemTheme getTheme() {
      return theme;
    }

    public void setTheme(CalendarItemTheme theme) {
      this.theme = theme;
    }

  }
}
