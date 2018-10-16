package org.vaadin.calendar.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.function.SerializableFunction;

public class ListCalendarDataProvider<T> extends AbstractCalendarDataProvider<T> {

	private SerializableFunction<T, Date> itemDateGenerator = t -> null;
	private Collection<T> cachedItems = Collections.emptyList();

	public ListCalendarDataProvider(Collection<T> items) {
		cachedItems = items;
	}

	public ListCalendarDataProvider<T> withItemDateGenerator(SerializableFunction<T, Date> dateGenerator) {
		itemDateGenerator = dateGenerator;
		return this;
	}

	@Override
	public List<T> getItems(Date fromDate, Date toDate) {
		return cachedItems.stream().filter(item -> {
			Date itemDate = itemDateGenerator.apply(item);
			return (fromDate.before(itemDate) || fromDate.equals(itemDate))
					&& (toDate.after(itemDate) || toDate.equals(itemDate));
		}).collect(Collectors.toList());
	}

	public void setItems(List<T> items) {
		cachedItems = items;
	}

}
