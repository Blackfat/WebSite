package org.website.modules.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.website.modules.utils.Dates;

import java.util.Date;

public class DatesTest {


    @Test
    public void testFormatDate() {
        DateTime dateTime = new DateTime(2016, 11, 25, 00, 00);
        String formatDate = Dates.formatDate(dateTime.toDate(), "yyyy-MM-dd");
        assertThat(formatDate).isEqualTo("2016-11-25");
    }

    @Test
    public void testParseDate() {
        String date = "2016-11-25";
        Date parseDate = Dates.parseDate(date, "yyyy-MM-dd");
        System.out.println(parseDate.toString());
    }
}
