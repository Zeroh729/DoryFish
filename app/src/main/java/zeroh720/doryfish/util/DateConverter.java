package zeroh720.doryfish.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateConverter {
    public static String getFormattedDate(String datetime){
        DateTime dateTime = new DateTime(datetime);

        DateTimeFormatter uiDateFormat = DateTimeFormat.forPattern("MMMM dd, yyyy");

        return uiDateFormat.print(dateTime);
    }

    public static String getFormattedTime(String datetime){
        DateTime dateTime = new DateTime(datetime);

        DateTimeFormatter uiDateFormat = DateTimeFormat.forPattern("HH:mm a");

        return uiDateFormat.print(dateTime);
    }
}
