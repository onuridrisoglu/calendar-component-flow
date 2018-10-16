package org.vaadin.calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.vaadin.calendar.data.AbstractCalendarDataProvider;
import org.vaadin.calendar.data.ListCalendarDataProvider;
import org.vaadin.calendar.utils.DateUtils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.data.binder.HasItems;
import com.vaadin.flow.data.provider.CompositeDataGenerator;
import com.vaadin.flow.data.provider.KeyMapper;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * Server-side component for the {@code calendar-component} webcomponent.
 *
 * @param <T>
 *          the type of the items to be displayed in calendar view
 */

@Tag("calendar-component")
@HtmlImport("bower_components/calendar-component/src/calendar-component.html")
public class CalendarComponent<T> extends Component implements HasItems<T> {

  private static final long serialVersionUID = 2644617317086352602L;
  public static final String ITEM_LABEL_PROPERTY = "subject";
  public static final String ITEM_DATE_PROPERTY = "date";
  public static final String ITEM_THEME_PROPERTY = "theme";
  public static final String ITEM_ID_PROPERTY = "id";

  private static DateFormat jsonDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
  private static DateFormat jsDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00XXX");

  private final CompositeDataGenerator<T> dataGenerator = new CompositeDataGenerator<>();

  private SerializableFunction<T, String> itemLabelGenerator = String::valueOf;
  private SerializableFunction<T, Date> itemDateGenerator = t -> null;
  private SerializableFunction<T, CalendarItemTheme> itemThemeGenerator = t -> CalendarItemTheme.Blue;

  private KeyMapper<T> keyMapper = new KeyMapper<>();
  private AbstractCalendarDataProvider<T> dataProvider;
  private DateRange activeRange = new DateRange(new Date());

  public CalendarComponent() {
    getElement().addEventListener("active-date-changed", this::handleActiveDateChanged).addEventData("event.detail");
  }

  /**
   * Sets the item label generator that is used to produce the strings shown in
   * the event for each item. By default, {@link String#valueOf(Object)} is
   * used.
   * 
   * @param itemLabelGenerator
   *          the item label provider to use, not null
   */
  public CalendarComponent<T> withItemLabelGenerator(SerializableFunction<T, String> itemLabelGenerator) {
    this.itemLabelGenerator = itemLabelGenerator;
    return this;
  }

  /**
   * Sets the item date generator that is used to place the items in the
   * calendar
   * 
   * @param itemDateGenerator
   *          the item date provider to use, not null
   */
  public CalendarComponent<T> withItemDateGenerator(SerializableFunction<T, Date> itemDateGenerator) {
    this.itemDateGenerator = itemDateGenerator;
    return this;
  }

  /**
   * Sets the item theme generator that is used to set different colors for
   * items in the calendar. See {@link CalendarItemTheme} for alternatives, by
   * default items are displayed as blue
   * 
   * @param itemThemeGenerator
   *          the item theme provider to use, not null
   */
  public CalendarComponent<T> withItemThemeGenerator(SerializableFunction<T, CalendarItemTheme> itemThemeGenerator) {
    this.itemThemeGenerator = itemThemeGenerator;
    return this;
  }

  @Override
  public void setItems(Collection<T> items) {
    setDataProvider(new ListCalendarDataProvider<>(items).withItemDateGenerator(itemDateGenerator));
  }

  public void setHideWeekends(boolean isHiddenWeekends) {
    getElement().setAttribute("hide-weekends", isHiddenWeekends);
  }

  /**
   * Sets the data provider for this calendar view. The data provider is queried
   * for displayed items as needed.
   * 
   * @param dataProvider
   *          data provider to use, not null
   */
  public void setDataProvider(AbstractCalendarDataProvider<T> dataProvider) {
    this.dataProvider = dataProvider;
    refresh();
  }

  /**
   * Refreshes the items in calendar
   */
  public void refresh() {
    Collection<T> items = dataProvider.getItems(activeRange.getFrom(), activeRange.getTo());
    JsonArray array = Json.createArray();
    items.stream().map(item -> generateJson(item)).forEachOrdered(json -> array.set(array.length(), json));
    getElement().setPropertyJson("items", array);
  }

  /**
   * Adds a listener for {@code event-clicked} events fired by the web
   * component.
   * 
   * @param listener
   *          the listener
   * @return a {@link Registration} for removing the event listener
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Registration addEventClickListener(
      ComponentEventListener<CalendarItemClickedEvent<CalendarComponent<T>, T>> listener) {
    return addListener(CalendarItemClickedEvent.class, (ComponentEventListener) listener);
  }

  protected T getData(String key) {
    return keyMapper.get(key);
  }

  private JsonObject generateJson(T item) {
    JsonObject json = Json.createObject();
    String key = keyMapper.key(item);
    json.put(ITEM_ID_PROPERTY, key);

    Date date = itemDateGenerator.apply(item);
    if (date == null) {
      throw new IllegalStateException(String.format("Got 'null' as a date value for the item '%s'.", item));
    }
    json.put(ITEM_DATE_PROPERTY, jsDateFormatter.format(date));

    String label = itemLabelGenerator.apply(item);
    if (label == null) {
      throw new IllegalStateException(String.format("Got 'null' as a label value for the item '%s'.", item));
    }
    json.put(ITEM_LABEL_PROPERTY, label);

    CalendarItemTheme theme = itemThemeGenerator.apply(item);
    if (theme == null) {
      throw new IllegalStateException(String.format("Got 'null' as a theme value for the item '%s'.", item));
    }
    json.put(ITEM_THEME_PROPERTY, theme.getThemeName());
    dataGenerator.generateData(item, json);
    return json;
  }

  private void handleActiveDateChanged(com.vaadin.flow.dom.DomEvent domEvent) {
    activeRange = new DateRange(parseDate(domEvent));
    refresh();
  }

  private Date parseDate(com.vaadin.flow.dom.DomEvent domEvent) {
    JsonObject jsonObject = domEvent.getEventData().getObject("event.detail");
    try {
      return jsonDateFormatter.parse(jsonObject.getString("value"));
    } catch (ParseException e) {
      throw new RuntimeException(String.format("couldn't parse date for [%s]", jsonObject.getString("value")), e);
    }
  }

  @SuppressWarnings("serial")
  @DomEvent("event-clicked")
  public static class CalendarItemClickedEvent<CMP extends CalendarComponent<ITEM>, ITEM> extends ComponentEvent<CMP> {
    private ITEM detail;

    public CalendarItemClickedEvent(CMP source, boolean fromClient, @EventData("event.detail") JsonObject detail) {
      super(source, fromClient);
      this.detail = (ITEM) source.getData(detail.getString(ITEM_ID_PROPERTY));
    }

    public ITEM getDetail() {
      return detail;
    }
  }

  private class DateRange {
    private Date from;
    private Date to;

    public DateRange(Date date) {
      from = DateUtils.getBeginingOfMonthFrom(date);
      to = DateUtils.getEndOfMonthFrom(date);
    }

    public Date getFrom() {
      return from;
    }

    public Date getTo() {
      return to;
    }
  }
}
