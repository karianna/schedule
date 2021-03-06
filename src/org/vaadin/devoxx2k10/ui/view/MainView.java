package org.vaadin.devoxx2k10.ui.view;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.vaadin.devoxx2k10.Configuration;
import org.vaadin.devoxx2k10.DevoxxScheduleApplication;
import org.vaadin.devoxx2k10.ui.FullScreenButton;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendar;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxCalendarEvent;
import org.vaadin.devoxx2k10.ui.calendar.DevoxxEventProvider;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;

/**
 * The main view of the application displaying navigation, calendar and details.
 */
public class MainView extends HorizontalLayout implements EventClickHandler, ValueChangeListener, URIHandler {

    private static final long serialVersionUID = 7622207451668068454L;

    private DevoxxCalendarEvent currentEvent;

    private DevoxxCalendar calendar;
    private EventDetailsPanel detailsPanel;
    private HorizontalLayout toolbar;
    private DaySelectorField daySelector;
    private FullScreenButton fullScreenButton;
    private Label dayLabel;
    private UriFragmentUtility uriFragment;

    public MainView() {
        initUi();
    }

    private void initUi() {
        setWidth("100%");
        setHeight("100%");

        dayLabel = new Label();
        dayLabel.setStyleName("selected-day");

        calendar = new DevoxxCalendar();
        calendar.setHandler(this);
        calendar.setDate(DevoxxCalendar.getDefaultDate());

        uriFragment = new UriFragmentUtility();
        addComponent(uriFragment);

        daySelector = new DaySelectorField(DevoxxCalendar.DEVOXX_FIRST_DAY, DevoxxCalendar.DEVOXX_LAST_DAY, uriFragment);
        daySelector.addListener(this);
        daySelector.setValue(DevoxxCalendar.getDefaultDate());

        toolbar = new HorizontalLayout();
        toolbar.setWidth("100%");
        toolbar.setHeight("33px");
        toolbar.setStyleName("v-toolbar");
        toolbar.addComponent(new UserLayout(calendar));
        toolbar.addComponent(daySelector);
        final Label placeHolder = new Label("");
        placeHolder.setWidth("275px");
        toolbar.addComponent(placeHolder);
        toolbar.setComponentAlignment(daySelector, Alignment.TOP_CENTER);
        toolbar.setExpandRatio(daySelector, 1.0f);

        fullScreenButton = new FullScreenButton(false);
        fullScreenButton.setVisible(!iOSUserAgent());
        final CssLayout calendarWrapper = new CssLayout();
        calendarWrapper.setMargin(true);
        calendarWrapper.setSizeFull();
        calendarWrapper.addComponent(fullScreenButton);
        calendarWrapper.addComponent(dayLabel);
        calendarWrapper.addComponent(calendar);

        final Panel calendarPanel = new Panel(new VerticalLayout());
        ((Layout) calendarPanel.getContent()).setMargin(false);
        calendarPanel.setStyleName("calendar-panel");
        calendarPanel.setSizeFull();
        calendarPanel.addComponent(toolbar);
        calendarPanel.addComponent(calendarWrapper);
        calendarPanel.addComponent(new FooterLinksLayout());
        addComponent(calendarPanel);

        detailsPanel = new EventDetailsPanel(this);
        addComponent(detailsPanel);
        detailsPanel.setVisible(false); // hide at first

        // make the calendar expand to use all available space
        setExpandRatio(calendarPanel, 1f);
    }

    private boolean iOSUserAgent() {
        if (DevoxxScheduleApplication.getCurrentInstance().getContext() instanceof WebApplicationContext) {
            final WebBrowser browser = ((WebApplicationContext) DevoxxScheduleApplication.getCurrentInstance().getContext())
                    .getBrowser();
            final String userAgent = browser.getBrowserApplication();

            if (userAgent.contains("iPod") || userAgent.contains("iPhone") || userAgent.contains("iPad")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void eventClick(final EventClick eventClick) {
        if (!eventClick.getCalendarEvent().equals(currentEvent)) {
            final CalendarEvent calEvent = eventClick.getCalendarEvent();
            // Select the event without repainting the calendar as the selected
            // style name is set on the client-side when clicking an event.
            selectCalendarEvent(calEvent, false);
        }
    }

    private void selectCalendarEvent(final CalendarEvent calEvent, final boolean repaintCalendar) {
        if (calEvent instanceof DevoxxCalendarEvent) {
            detailsPanel.setVisible(true);

            final DevoxxCalendarEvent devoxxCalEvent = (DevoxxCalendarEvent) calEvent;

            if (!fullScreenButton.isFullScreen()
                    && Configuration.getBooleanProperty("ui.scrollup.onselection")) {
                getWindow().scrollIntoView(toolbar);
            }

            currentEvent = devoxxCalEvent;
            calendar.setSelectedPresentation(devoxxCalEvent.getDevoxxEvent());
            detailsPanel.setEvent(devoxxCalEvent);

            if (repaintCalendar) {
                calendar.requestRepaint();
            }

            DevoxxScheduleApplication.trackPageview("view", devoxxCalEvent.getDevoxxEvent());
        }
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        calendar.setDate((Date) daySelector.getValue());
        dayLabel.setValue(getLabelForDate((Date) daySelector.getValue()));
    }

    private String getLabelForDate(final Date value) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        String day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US).toLowerCase();

        String dayLabel = Configuration.getProperty("conference.day." + day);
        if (dayLabel == null) {
            dayLabel = "";
        }
        return dayLabel;
    }

    public void selectPresentationWithId(final int id) {
        final DevoxxCalendarEvent event = (DevoxxCalendarEvent) ((DevoxxEventProvider) calendar.getEventProvider())
                .getEvent(id);
        if (event != null) {
            // select correct date and event
            daySelector.setValue(event.getDevoxxEvent().getFromTime());
            selectCalendarEvent(event, true);
        }
    }

    @Override
    public DownloadStream handleURI(final URL context, String relativeUri) {
        // Workaround for an URIHandler quirk:
        // http://dev.vaadin.com/ticket/6390
        if (context.toString().endsWith("presentation/")) {
            relativeUri = "presentation/" + relativeUri;
        }

        if (relativeUri.startsWith("presentation/")) {
            try {
                final int id = Integer.valueOf(relativeUri.split("/")[1]);
                selectPresentationWithId(id);
            } catch (final NumberFormatException e) {
                // the id was not an integer -> simply ignore
            }
        }
        return null;
    }
}
