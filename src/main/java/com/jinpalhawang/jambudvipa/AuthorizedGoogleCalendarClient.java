package com.jinpalhawang.jambudvipa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

public class AuthorizedGoogleCalendarClient {

  private static final Logger log = LoggerFactory.getLogger(AuthorizedGoogleCalendarClient.class);

  private static final String APPLICATION_NAME = "Jambudvipa";
  /** Directory to store user credentials for this application. */
  private static final File DATA_STORE_DIR =
      new File(System.getProperty("user.home"), ".credentials/jambudvipa");

  private static HttpTransport HTTP_TRANSPORT;
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  /** Global instance of the scopes.
   * If modifying these scopes, delete your previously saved credentials
   * at ~/.credentials/calendar-java-quickstart.json
   */
  private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

  public static Credential authorize() throws IOException {
    InputStream in = AuthorizedGoogleCalendarClient.class.getResourceAsStream("/client_secret.json");
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    log.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
    return credential;
  }

  public static Calendar getCalendarService() throws IOException {
    Credential credential = authorize();
    return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }
}
