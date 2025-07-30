package org.apache.commons.compress.archivers.zip;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;

public class ZipUtilTime {
    /**
     * DOS time constant for representing timestamps before 1980. Smallest date/time ZIP can handle.
     * <p>
     * MS-DOS records file dates and times as packed 16-bit values. An MS-DOS date has the following format.
     * </p>
     * <p>
     * Bits Contents
     * </p>
     * <ul>
     * <li>0-4: Day of the month (1-31).</li>
     * <li>5-8: Month (1 = January, 2 = February, and so on).</li>
     * <li>9-15: Year offset from 1980 (add 1980 to get the actual year).</li>
     * </ul>
     *
     * An MS-DOS time has the following format.
     * <p>
     * Bits Contents
     * </p>
     * <ul>
     * <li>0-4: Second divided by 2.</li>
     * <li>5-10: Minute (0-59).</li>
     * <li>11-15: Hour (0-23 on a 24-hour clock).</li>
     * </ul>
     *
     * This constant expresses the minimum DOS date of January 1st 1980 at 00:00:00 or, bit-by-bit:
     * <ul>
     * <li>Year: 0000000</li>
     * <li>Month: 0001</li>
     * <li>Day: 00001</li>
     * <li>Hour: 00000</li>
     * <li>Minute: 000000</li>
     * <li>Seconds: 00000</li>
     * </ul>
     *
     * <p>
     * This was copied from {@link ZipEntry}.
     * </p>
     *
     * @since 1.23
     */
    static final long DOSTIME_BEFORE_1980 = 1 << 21 | 1 << 16; // 0x210000
    /** Java time representation of the smallest date/time ZIP can handle */
    static final long DOSTIME_BEFORE_1980_AS_JAVA_TIME = dosToJavaTime(DOSTIME_BEFORE_1980);
    /**
     * Approximately 128 years, in milliseconds (ignoring leap years, etc.).
     *
     * <p>
     * This establish an approximate high-bound value for DOS times in milliseconds since epoch, used to enable an efficient but sufficient bounds check to
     * avoid generating extended last modified time entries.
     * </p>
     * <p>
     * Calculating the exact number is locale dependent, would require loading TimeZone data eagerly, and would make little practical sense. Since DOS times
     * theoretically go to 2107 - with compatibility not guaranteed after 2099 - setting this to a time that is before but near 2099 should be sufficient.
     * </p>
     *
     * <p>
     * This was copied from {@link ZipEntry}.
     * </p>
     *
     * @since 1.23
     */
    private static final long UPPER_DOSTIME_BOUND = 128L * 365 * 24 * 60 * 60 * 1000;

    static Date dosToJavaDate(final long dosTime) {
        final Calendar cal = Calendar.getInstance();
        // CheckStyle:MagicNumberCheck OFF - no point
        cal.set(Calendar.YEAR, (int) (dosTime >> 25 & 0x7f) + 1980);
        cal.set(Calendar.MONTH, (int) (dosTime >> 21 & 0x0f) - 1);
        cal.set(Calendar.DATE, (int) (dosTime >> 16) & 0x1f);
        cal.set(Calendar.HOUR_OF_DAY, (int) (dosTime >> 11) & 0x1f);
        cal.set(Calendar.MINUTE, (int) (dosTime >> 5) & 0x3f);
        cal.set(Calendar.SECOND, (int) (dosTime << 1) & 0x3e);
        cal.set(Calendar.MILLISECOND, 0);
        // CheckStyle:MagicNumberCheck ON
        return cal.getTime();
    }

    /**
     * Converts DOS time to Java time (number of milliseconds since epoch).
     *
     * @param dosTime time to convert
     * @return converted time
     */
    public static long dosToJavaTime(final long dosTime) {
        return dosToJavaDate(dosTime).getTime();
    }

    /**
     * Tests whether a given time (in milliseconds since Epoch) can be safely represented as DOS time
     *
     * @param time time in milliseconds since epoch
     * @return true if the time can be safely represented as DOS time, false otherwise
     * @since 1.23
     */
    public static boolean isDosTime(final long time) {
        return time <= UPPER_DOSTIME_BOUND &&
                (time == DOSTIME_BEFORE_1980_AS_JAVA_TIME || javaToDosTime(time) != DOSTIME_BEFORE_1980);
    }

    private static LocalDateTime javaEpochToLocalDateTime(final long time) {
        final Instant instant = Instant.ofEpochMilli(time);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    // version with integer overflow fixed - see https://bugs.openjdk.org/browse/JDK-8130914
    static long javaToDosTime(final long t) {
        final LocalDateTime ldt = javaEpochToLocalDateTime(t);
        if (ldt.getYear() < 1980) {
            return DOSTIME_BEFORE_1980;
        }
        return (ldt.getYear() - 1980 << 25 | ldt.getMonthValue() << 21 | ldt.getDayOfMonth() << 16 | ldt.getHour() << 11 | ldt.getMinute() << 5
                | ldt.getSecond() >> 1) & 0xffffffffL;
    }
}
