package com.jinpalhawang.jambudvipa;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

@RestController
public class GoogleCalendarMiddleRestController {

  private static final Logger log = LoggerFactory.getLogger(GoogleCalendarMiddleRestController.class);

  static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

  @RequestMapping("/calendar/list")
  public CalendarList getCalendarList() throws IOException {

    log.info("Getting Calendar List...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final CalendarList calendarList = client
        .calendarList()
        .list()
        .execute();

    View.header("Calendar List");
    View.display(calendarList);

    return calendarList;
  }

  @RequestMapping("/calendar/primary/events")
  public Events getCalendarEvents() throws IOException {

    log.info("Getting Primary Calendar Events...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final Events events = client
        .events()
        .list("primary")
        .setMaxResults(10)
        .setTimeMin(new DateTime(System.currentTimeMillis()))
        .setOrderBy("startTime")
        .setSingleEvents(true)
        .execute();

    View.header("Primary Calendar Events");
    View.display(events);

    return events;
  }

  @RequestMapping("/calendar/batch/add")
  public void addCalendarsUsingBatch() throws IOException {

    log.info("Adding Calendars using Batch...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final BatchRequest batch = client.batch();

    // Create the callback.
    final JsonBatchCallback<Calendar> callback = new JsonBatchCallback<Calendar>() {

      @Override
      public void onSuccess(Calendar calendar, HttpHeaders responseHeaders) {
        View.header("Add Calendars using Batch");
        View.display(calendar);
        addedCalendarsUsingBatch.add(calendar);
      }

      @Override
      public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
        System.out.println("Error Message: " + e.getMessage());
        log.error("Error Message: " + e.getMessage());
      }

    };

    // Create 2 Calendar Entries to insert.
    final Calendar entry1 = new Calendar().setSummary("Calendar for Testing 1");
    client.calendars().insert(entry1).queue(batch, callback);

    final Calendar entry2 = new Calendar().setSummary("Calendar for Testing 2");
    client.calendars().insert(entry2).queue(batch, callback);

    batch.execute();
  }

  @RequestMapping("/calendar/add")
  public Calendar addCalendar() throws IOException {

    log.info("Adding Calendar...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final Calendar entry = new Calendar();
    entry.setSummary("Calendar for Testing 3");

    final Calendar result = client
        .calendars()
        .insert(entry)
        .execute();

    View.header("Add Calendar");
    View.display(result);

    return result;
  }

  @RequestMapping("/calendar/{calendarId}/update")
  public Calendar updateCalendar(@PathVariable String calendarId) throws IOException {

    log.info("[" + calendarId + "] Updating Calendar...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final Calendar entry = new Calendar();
    entry.setSummary("Updated Calendar for Testing");

    final Calendar result = client
        .calendars()
        .patch(calendarId, entry)
        .execute();

    View.header("Update Calendar");
    View.display(result);

    return result;
  }

  @RequestMapping("/calendar/{calendarId}/add")
  public void addEvent(@PathVariable String calendarId) throws IOException {

    log.info("[" + calendarId + "] Adding Event...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final Event event = newEvent();

    final Event result = client
        .events()
        .insert(calendarId, event)
        .execute();

    View.header("Add Event");
    View.display(result);
  }

  @RequestMapping("/calendar/{calendarId}/events/list")
  public Events showEvents(@PathVariable String calendarId) throws IOException {

    log.info("[" + calendarId + "] Listing Events...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    final Events events = client
        .events()
        .list(calendarId)
        .execute();

    View.header("Show Events");
    View.display(events);

    return events;
  }

  @RequestMapping("/calendar/batch/delete")
  public void deleteCalendarsUsingBatch() throws IOException {

    log.info("Deleting Calendars using Batch...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    View.header("Delete Calendars Using Batch");

    final BatchRequest batch = client.batch();

    for (Calendar calendar : addedCalendarsUsingBatch) {

      client.calendars().delete(calendar.getId()).queue(batch, new JsonBatchCallback<Void>() {

        @Override
        public void onSuccess(Void content, HttpHeaders responseHeaders) {
          System.out.println("Delete is successful!");
        }

        @Override
        public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
          System.out.println("Error Message: " + e.getMessage());
          log.error("Error Message: " + e.getMessage());
        }

      });
    }

    batch.execute();
  }

  @RequestMapping("/calendar/{calendarId}/delete")
  public void deleteCalendar(@PathVariable String calendarId) throws IOException {

    log.info("[" + calendarId + "] Deleting Calendar...");

    final com.google.api.services.calendar.Calendar client =
        AuthorizedGoogleCalendarClient.getCalendarService();

    View.header("Delete Calendar");

    client
        .calendars()
        .delete(calendarId)
        .execute();
  }

  private static Event newEvent() {

    final Event event = new Event();
    event.setSummary("New Event");

    final Date startDate = new Date();
    final Date endDate = new Date(startDate.getTime() + 3600000);

    final DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
    event.setStart(new EventDateTime().setDateTime(start));

    final DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
    event.setEnd(new EventDateTime().setDateTime(end));

    return event;
  }

}
