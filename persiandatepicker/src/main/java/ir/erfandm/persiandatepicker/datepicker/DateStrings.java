/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ir.erfandm.persiandatepicker.datepicker;

import android.content.Context;
import android.icu.text.DateFormat;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.format.DateUtils;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ir.erfandm.persiandatepicker.JalaliCalendar;
import ir.erfandm.persiandatepicker.R;

/** Util methods for formatting date strings for use in {@link MaterialDatePicker}. */
class DateStrings {

  private DateStrings() {}

  static String getYearMonth(long timeInMillis) {
    var calendar = new JalaliCalendar(new Date(timeInMillis));
    return calendar.getMonthString() + " " + String.format(UtcDates.PERSIAN_LOCALE, "%d", calendar.getYear());
  }

  static String getYearMonthDay(long timeInMillis) {
    return getYearMonthDay(timeInMillis, UtcDates.PERSIAN_LOCALE);
  }

  /**
   * Get date string with year, month, and day formatted properly for the specified Locale.
   *
   * <p>Uses {@link DateFormat#getInstanceForSkeleton(String, Locale)} for API 24+, and {@link
   * java.text.DateFormat#MEDIUM} before API 24.
   *
   * @param timeInMillis long in UTC milliseconds to turn into string with year, month, and day.
   * @param locale Locale for date string.
   * @return Date string with year, month, and day formatted properly for the specified Locale.
   */
  static String getYearMonthDay(long timeInMillis, Locale locale) {
    var calendar = new JalaliCalendar(new Date(timeInMillis));
    return String.format(locale, "%d", calendar.getDay()) + " " + calendar.getMonthString() + " " + String.format(locale, "%d", calendar.getYear());
  }

  static String getMonthDay(long timeInMillis) {
    return getMonthDay(timeInMillis, UtcDates.PERSIAN_LOCALE);
  }

  /**
   * Get date string with month and day formatted properly for the specified Locale.
   *
   * <p>Uses {@link DateFormat#getInstanceForSkeleton(String, Locale)} for API 24+, and {@link
   * java.text.DateFormat#MEDIUM} before API 24.
   *
   * @param timeInMillis long in UTC milliseconds to turn into string with month and day.
   * @param locale Locale for date string.
   * @return Date string with month and day formatted properly for the specified Locale.
   */
  static String getMonthDay(long timeInMillis, Locale locale) {
    var calendar = new JalaliCalendar(new Date(timeInMillis));
    return String.format(locale, "%d", calendar.getDay()) + " " + calendar.getMonthString();
  }

  static String getMonthDayOfWeekDay(long timeInMillis) {
    return getMonthDayOfWeekDay(timeInMillis, UtcDates.PERSIAN_LOCALE);
  }

  static String getMonthDayOfWeekDay(long timeInMillis, Locale locale) {
    var calendar = new JalaliCalendar(new Date(timeInMillis));
    return calendar.getDayOfWeekString() + " " + String.format(locale, "%d", calendar.getDay()) + " " + calendar.getMonthString();
  }

  static String getYearMonthDayOfWeekDay(long timeInMillis) {
    return getYearMonthDayOfWeekDay(timeInMillis, UtcDates.PERSIAN_LOCALE);
  }

  static String getYearMonthDayOfWeekDay(long timeInMillis, Locale locale) {
    var calendar = new JalaliCalendar(new Date(timeInMillis));
    return calendar.getDayOfWeekString() + " " + String.format(locale, "%d", calendar.getDay()) + " " + calendar.getMonthString() + " " + String.format(locale, "%d", calendar.getYear());
  }

  /**
   * Does not show year if date is within current year.
   *
   * @param timeInMillis milliseconds since UTC epoch.
   * @return Formatted date string.
   */
  static String getOptionalYearMonthDayOfWeekDay(long timeInMillis) {
    if (isDateWithinCurrentYear(timeInMillis)) {
      return getMonthDayOfWeekDay(timeInMillis);
    }
    return getYearMonthDayOfWeekDay(timeInMillis);
  }

  static String getDateString(long timeInMillis) {
    return getDateString(timeInMillis, null);
  }

  /**
   * Return a date string for the given date.
   *
   * <p>Does not show year if date is within current year.
   *
   * <p>If userDefinedDateFormat is set, this format overrides the rule above.
   *
   * @param timeInMillis milliseconds since UTC epoch.
   * @param userDefinedDateFormat {@link SimpleDateFormat} specified by the user, if set.
   * @return Formatted date string.
   */
  static String getDateString(long timeInMillis, @Nullable SimpleDateFormat userDefinedDateFormat) {
    if (userDefinedDateFormat != null) {
      Date date = new Date(timeInMillis);
      return userDefinedDateFormat.format(date);
    } else if (isDateWithinCurrentYear(timeInMillis)) {
      return getMonthDay(timeInMillis);
    }
    return getYearMonthDay(timeInMillis);
  }

  private static boolean isDateWithinCurrentYear(long timeInMillis) {
    JalaliCalendar currentCalendar = UtcDates.getTodayCalendarToJalali();
    JalaliCalendar calendarDate = UtcDates.getUtcCalendarToJalali();
    calendarDate.setTimeInMillis(timeInMillis);
    return currentCalendar.get(Calendar.YEAR) == calendarDate.get(Calendar.YEAR);
  }

  static Pair<String, String> getDateRangeString(@Nullable Long start, @Nullable Long end) {
    return getDateRangeString(start, end, null);
  }

  /**
   * Return a pair of strings representing the start and end dates of this date range.
   *
   * <p>Does not show year if dates are within the same year (Nov 17 - Dec 19).
   *
   * <p>Shows year for end date if range is not within the current year (Nov 17 - Nov 19, 2018).
   *
   * <p>Shows year for start and end date if range spans several years (Dec 31, 2016 - Jan 1, 2017).
   *
   * <p>If userDefinedDateFormat is set, this format overrides the rules above.
   *
   * @param start Start date.
   * @param end End date.
   * @param userDefinedDateFormat {@link SimpleDateFormat} specified by the user, if set.
   * @return Formatted date range string.
   */
  static Pair<String, String> getDateRangeString(
      @Nullable Long start, @Nullable Long end, @Nullable SimpleDateFormat userDefinedDateFormat) {
    if (start == null && end == null) {
      return Pair.create(null, null);
    } else if (start == null) {
      return Pair.create(null, getDateString(end, userDefinedDateFormat));
    } else if (end == null) {
      return Pair.create(getDateString(start, userDefinedDateFormat), null);
    }

    JalaliCalendar currentCalendar = UtcDates.getTodayCalendarToJalali();
    JalaliCalendar startCalendar = UtcDates.getUtcCalendarToJalali();
    startCalendar.setTimeInMillis(start);
    JalaliCalendar endCalendar = UtcDates.getUtcCalendarToJalali();
    endCalendar.setTimeInMillis(end);

    if (userDefinedDateFormat != null) {
      Date startDate = new Date(start);
      Date endDate = new Date(end);
      return Pair.create(
          userDefinedDateFormat.format(startDate), userDefinedDateFormat.format(endDate));
    } else if (startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR)) {
      if (startCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
        return Pair.create(
            getMonthDay(start, UtcDates.PERSIAN_LOCALE), getMonthDay(end, UtcDates.PERSIAN_LOCALE));
      } else {
        return Pair.create(
            getMonthDay(start, UtcDates.PERSIAN_LOCALE), getYearMonthDay(end, UtcDates.PERSIAN_LOCALE));
      }
    }
    return Pair.create(
        getYearMonthDay(start, UtcDates.PERSIAN_LOCALE), getYearMonthDay(end, UtcDates.PERSIAN_LOCALE));
  }

  /**
   * Returns the day content description.
   *
   * @param context the {@link Context}
   * @param dayInMillis UTC milliseconds representing the first moment of the day in local timezone
   * @param isToday boolean representing if the day is today
   * @param isStartOfRange boolean representing if the day is the start of a range
   * @param isEndOfRange boolean representing if the day is the end of a range
   * @return Day content description string
   */
  static String getDayContentDescription(
      Context context,
      long dayInMillis,
      boolean isToday,
      boolean isStartOfRange,
      boolean isEndOfRange) {
    String dayContentDescription = getOptionalYearMonthDayOfWeekDay(dayInMillis);
    if (isToday) {
      dayContentDescription =
          String.format(
              context.getString(R.string.mtrl_picker_today_description), dayContentDescription);
    }
    if (isStartOfRange) {
      return String.format(
          context.getString(R.string.mtrl_picker_start_date_description), dayContentDescription);
    } else if (isEndOfRange) {
      return String.format(
          context.getString(R.string.mtrl_picker_end_date_description), dayContentDescription);
    }
    return dayContentDescription;
  }

  /**
   * Returns the year content description.
   *
   * @param context the {@link Context}
   * @param year the year, example: 2020
   * @return Year content description string
   */
  static String getYearContentDescription(Context context, int year) {
    if (UtcDates.getTodayCalendar().get(Calendar.YEAR) == year) {
      return String.format(
          context.getString(R.string.mtrl_picker_navigate_to_current_year_description), year);
    }
    return String.format(
        context.getString(R.string.mtrl_picker_navigate_to_year_description), year);
  }
}
