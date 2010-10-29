package org.vaadin.devoxx2k10.ui.view;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;

/**
 * CustomField for selecting days from a certain date interval given in the
 * constructor. Supports also selecting the day via an URI fragment.
 */
public class DaySelectorField extends CustomField implements
        Button.ClickListener, Property.ValueChangeListener,
        FragmentChangedListener {

    private static final long serialVersionUID = -4777985742354898074L;

    private ComponentContainer layout;
    private UriFragmentUtility uriFragment;
    private Map<String, Button> uriFragmentToButtonMap = new HashMap<String, Button>();

    public DaySelectorField(Date firstDay, Date lastDay,
            UriFragmentUtility uriFragment) {
        layout = new CssLayout();
        setWidth("260px");
        setCompositionRoot(layout);
        setStyleName("day-selector-field");
        addListener(this);

        Calendar javaCalendar = Calendar.getInstance();
        javaCalendar.setTime(firstDay);

        Button dayButton = null;
        while (javaCalendar.getTime().compareTo(lastDay) <= 0) {
            String dayName = javaCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.SHORT, Locale.US);

            boolean first = (dayButton == null);
            dayButton = new Button(dayName, (Button.ClickListener) this);
            dayButton.setStyleName("day-button");
            if (first) {
                dayButton.addStyleName("first");
            }
            dayButton.setData(javaCalendar.getTime());
            layout.addComponent(dayButton);
            uriFragmentToButtonMap.put(dayName.toLowerCase(), dayButton);

            // next day
            javaCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        dayButton.addStyleName("last");

        if (uriFragment != null) {
            uriFragment.addListener(this);
            this.uriFragment = uriFragment;
        }
    }

    @Override
    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
        // set the style name for selected
        for (Map.Entry<String, Button> entry : uriFragmentToButtonMap
                .entrySet()) {
            String fragment = entry.getKey();
            Button button = entry.getValue();

            if (dayEquals((Date) button.getData(), (Date) getValue())) {
                button.addStyleName("selected");
                if (uriFragment != null) {
                    uriFragment.setFragment(fragment, false);
                }
            } else {
                button.removeStyleName("selected");
            }
        }
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        if (newValue instanceof Date
                && !dayEquals((Date) getValue(), (Date) newValue)) {
            super.setValue(newValue);
        }
    }

    /**
     * Compares the two given dates ignoring the time of day.
     * 
     * @param first
     * @param second
     * @return true if the dates represent the same day.
     */
    private boolean dayEquals(Date first, Date second) {
        if (first == null || second == null) {
            return false;
        }

        Calendar firstCal = Calendar.getInstance();
        firstCal.setTime(first);
        Calendar secondCal = Calendar.getInstance();
        secondCal.setTime(second);

        return firstCal.get(Calendar.DAY_OF_YEAR) == secondCal
                .get(Calendar.DAY_OF_YEAR)
                && firstCal.get(Calendar.YEAR) == secondCal.get(Calendar.YEAR);

    }

    @Override
    public Class<?> getType() {
        return Date.class;
    }

    public void buttonClick(ClickEvent event) {
        Date clickedValue = (Date) event.getButton().getData();
        setValue(clickedValue);
    }

    public void fragmentChanged(FragmentChangedEvent source) {
        // URI fragment changed -> see if there is a corresponding Button and
        // select that Button's value.
        String fragment = source.getUriFragmentUtility().getFragment();
        if (uriFragmentToButtonMap.containsKey(fragment)) {
            setValue(uriFragmentToButtonMap.get(fragment).getData());
        }
    }

}
