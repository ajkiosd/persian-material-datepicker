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

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ir.erfandm.persiandatepicker.JalaliCalendar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** Contains convenience operations for a month within a specific year. */
final class Month implements Comparable<Month>, Parcelable {

  /** The acceptable int values for month when using {@link Month#create(int, int)} */
  public static final int FARVARDIN = 0;
  public static final int ORDIBEHESHT = 1;
  public static final int KHORDAD = 2;
  public static final int TIR = 3;
  public static final int MORDAD = 4;
  public static final int SHAHRIVAR = 5;
  public static final int MEHR = 6;
  public static final int ABAN = 7;
  public static final int AZAR = 8;
  public static final int DEY = 9;
  public static final int BAHMAN = 10;
  public static final int ESFAND = 11;
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({
          FARVARDIN,
          ORDIBEHESHT,
          KHORDAD,
          TIR,
          MORDAD,
          SHAHRIVAR,
          MEHR,
          ABAN,
          AZAR,
          DEY,
          BAHMAN,
          ESFAND
  })
  @interface Months {}

  @NonNull private final JalaliCalendar firstOfMonth;
  @Months final int month;
  final int year;
  final int daysInWeek;
  final int daysInMonth;
  final long timeInMillis;

  @Nullable private String longName;

  private Month(@NonNull JalaliCalendar rawCalendar) {
    rawCalendar.set(Calendar.DAY_OF_MONTH, 1);
    firstOfMonth = UtcDates.getDayCopy(rawCalendar);
    month = firstOfMonth.get(Calendar.MONTH);
    year = firstOfMonth.get(Calendar.YEAR);
    daysInWeek = firstOfMonth.getMaximum(Calendar.DAY_OF_WEEK);
    daysInMonth = firstOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
    timeInMillis = firstOfMonth.getTimeInMillis();
  }

  /**
   * Creates an instance of Month that contains the provided {@code timeInMillis} where {@code
   * timeInMillis} is in milliseconds since 00:00:00 January 1, 1970, UTC.
   */
  @NonNull
  static Month create(long timeInMillis) {
    JalaliCalendar calendar = UtcDates.getUtcCalendarToJalali();
    calendar.setTimeInMillis(timeInMillis);
    return new Month(calendar);
  }

  /**
   * Creates an instance of Month with the given parameters backed by a {@link Calendar}.
   *
   * @param year The year
   * @param month The 0-index based month. Use {@link Calendar} constants (e.g., {@link
   *     Calendar#JANUARY}
   * @return A Month object backed by a new {@link Calendar} instance
   */
  @NonNull
  static Month create(int year, @Months int month) {
    JalaliCalendar calendar = UtcDates.getUtcCalendarToJalali();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    return new Month(calendar);
  }

  /**
   * Returns the {@link Month} that contains the first moment in current month in the default
   * timezone (as per {@link Calendar#getInstance()}.
   */
  @NonNull
  static Month current() {
    return new Month(UtcDates.getTodayCalendarToJalali());
  }

  int daysFromStartOfWeekToFirstOfMonth(int firstDayOfWeek) {
    int difference =
        firstOfMonth.get(Calendar.DAY_OF_WEEK)
            - (firstDayOfWeek > 0 ? firstDayOfWeek : firstOfMonth.getFirstDayOfWeek());
    if (difference < 0) {
      difference = difference + daysInWeek;
    }
    return difference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Month)) {
      return false;
    }
    Month that = (Month) o;
    return month == that.month && year == that.year;
  }

  @Override
  public int hashCode() {
    Object[] hashedFields = {month, year};
    return Arrays.hashCode(hashedFields);
  }

  @Override
  public int compareTo(@NonNull Month other) {
    return firstOfMonth.toGregorian().compareTo(other.firstOfMonth.toGregorian());
  }

  /**
   * Returns the number of months from this Month to the provided Month.
   *
   * <p>0 when {@code this.compareTo(other)} is 0. Negative when {@code this.compareTo(other)} is
   * negative.
   *
   * @throws IllegalArgumentException when {@link Calendar#getInstance()} is not an instance of
   *     {@link GregorianCalendar}
   */
  int monthsUntil(@NonNull Month other) {
    return (other.year - year) * 12 + (other.month - month);
//    if (firstOfMonth instanceof GregorianCalendar) {
//
//    } else {
//      throw new IllegalArgumentException("Only Gregorian calendars are supported.");
//    }
  }

  long getStableId() {
    return firstOfMonth.getTimeInMillis();
  }

  /**
   * Gets a long for the specific day within the instance's month and year.
   *
   * <p>This method only guarantees validity with respect to {@link Calendar#isLenient()}.
   *
   * @param day The desired day within this month and year
   * @return A long representing a time in milliseconds for the given day within the specified month
   *     and year
   */
  long getDay(int day) {
    JalaliCalendar dayCalendar = UtcDates.getDayCopy(firstOfMonth);
    dayCalendar.set(Calendar.DAY_OF_MONTH, day);
    return dayCalendar.getTimeInMillis();
  }

  int getDayOfMonth(long date) {
    JalaliCalendar dayCalendar = UtcDates.getDayCopy(firstOfMonth);
    dayCalendar.setTimeInMillis(date);
    return dayCalendar.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Returns a {@link Month} {@code months} months after this
   * instance.
   */
  @NonNull
  Month monthsLater(int months) {
    JalaliCalendar laterMonth = UtcDates.getDayCopy(firstOfMonth);
    laterMonth.add(Calendar.MONTH, months);
    return new Month(laterMonth);
  }

  /** Returns a localized String representation of the month name and year. */
  @NonNull
  String getLongName() {
    if (longName == null) {
      longName = DateStrings.getYearMonth(firstOfMonth.getTimeInMillis());
    }
    return longName;
  }

  /* Parcelable interface */

  /** {@link Creator} */
  public static final Creator<Month> CREATOR =
      new Creator<Month>() {
        @NonNull
        @Override
        public Month createFromParcel(@NonNull Parcel source) {
          int year = source.readInt();
          int month = source.readInt();
          return Month.create(year, month);
        }

        @NonNull
        @Override
        public Month[] newArray(int size) {
          return new Month[size];
        }
      };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeInt(year);
    dest.writeInt(month);
  }
}
