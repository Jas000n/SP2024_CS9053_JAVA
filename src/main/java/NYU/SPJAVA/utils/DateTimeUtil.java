package NYU.SPJAVA.utils;

import java.sql.Timestamp;
import org.joda.time.LocalDateTime;

public class DateTimeUtil {

	/**
	 * converts a joda LocalDateTime to SQL TimeStamp
	 * 
	 * @param LocalDateTime dt
	 * @return SQL TimeStamp
	 */
	public static Timestamp toTimestamp(LocalDateTime datetime) {
		return new Timestamp(datetime.toDateTime().getMillis());
	}

	/**
	 * converts SQL TimeSTamp to joda datetime
	 * 
	 * @param timestamp
	 * @return LocalDateTime
	 */
	public static LocalDateTime toDateTime(Timestamp timestamp) {
		return LocalDateTime.fromDateFields(timestamp);
	}

	// calculated the elapsed Milli seconds between to datetimes
	//public static long elapsedMilliSeconds(LocalDateTime start, LocalDateTime end) {
	// TODO: UI Implementation -- implement this method for timer
	// }

	
	public static void main(String[] args) {
		LocalDateTime dt = new LocalDateTime();
		Timestamp ts = DateTimeUtil.toTimestamp(dt);
		LocalDateTime dtBack = DateTimeUtil.toDateTime(ts);

		System.out.println("datetime now " + dt);
		System.out.println("to Timestamp : " + ts);
		System.out.println("and back to datetime: " + dtBack);

	}

}
