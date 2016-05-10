package com.jinpalhawang.jambudvipa;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class View {

  // HEADER
  static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
  }

  static void display(Calendar entry) {
    System.out.println("---- Calendar ----");
    System.out.println("ID: " + entry.getId());
    System.out.println("Summary: " + entry.getSummary());
    if (entry.getDescription() != null) {
      System.out.println("Description: " + entry.getDescription());
    }
  }

  // CALENDAR LIST
  static void display(CalendarList feed) {
    if (feed.getItems() != null) {
      for (CalendarListEntry entry : feed.getItems()) {
        display(entry);
      }
    }
  }

  static void display(CalendarListEntry entry) {
    System.out.println();
    System.out.println("---- CalendarListEntry ----");
    System.out.println("ID: " + entry.getId());
    System.out.println("Summary: " + entry.getSummary());
    if (entry.getDescription() != null) {
      System.out.println("Description: " + entry.getDescription());
    }
  }

  // EVENTS
  static void display(Events feed) {
    if (feed.getItems() != null) {
      for (Event entry : feed.getItems()) {
        display(entry);
      }
    }
  }

  static void display(Event event) {
    System.out.println();
    System.out.println("---- Event ----");
    System.out.println("ID: " + event.getId());
    System.out.println("Summary: " + event.getSummary());
    if (event.getStart() != null) {
      System.out.println("Start Time: " + event.getStart());
    }
    if (event.getEnd() != null) {
      System.out.println("End Time: " + event.getEnd());
    }
    if (event.getDescription() != null) {
      System.out.println("Description: " + event.getDescription());
    }
  }

}
