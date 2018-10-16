package org.vaadin.calendar.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public abstract class AbstractCalendarDataProvider<T> implements Serializable {

	public abstract Collection<T> getItems(Date fromDate, Date toDate);

}
