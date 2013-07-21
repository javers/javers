package pl.edu.icm.sedno.common.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.ObjectUtils;

import pl.edu.icm.crmanager.model.CrmSimpleEmbeddable;

/**
 * ValueObject dla daty <br/>
 *
 * Effectively Immutable, data bez czasu, zmienna precyzja: rok, miesiąc, dzień, see {@link DatePrecision}.
 * Np: '2000-01-01', '2000-01', '2000'.
 * Przeznaczona do embedowania w klasach modelu danych, w bazie danych persystuje się jako String
 *
 * @see DatePrecision
 * @author bart
 */
@SuppressWarnings("serial")
@Embeddable
public class SednoDate implements CrmSimpleEmbeddable, Serializable {
    private static final int NULL_REPLACEMENT = 1;

    private LocalDate     localDate;
    private DatePrecision precision;

    /** for hibernate */
    public SednoDate() {}

    public SednoDate(String databaseValue) {
        setDatabaseValue(databaseValue);
    }

    /**
     * factory method, tworzy datę zainicjowaną na today
     */
    public static SednoDate today() {
    	return new SednoDate(new LocalDate());
    }

    /**
     * with default precision YEAR_MONTH_DAY
     */
    public SednoDate (LocalDate date) {
        this.localDate = date;
        this.precision = DatePrecision.YEAR_MONTH_DAY;
        trimToPrecision();
    }

    /**
     * uwaga: przycina wewnętrzny localDate do zadanej precyzji
     * (czyli np ustawia dzień na 1 jeśli precision == YEAR_MONTH)
     */
    public SednoDate (LocalDate date, DatePrecision precision) {
        this.localDate = date;
        this.precision = precision;
        trimToPrecision();
    }

    /**
     * with precision YEAR
     */
    public SednoDate (int year) {
        this.localDate = new LocalDate(year,NULL_REPLACEMENT,NULL_REPLACEMENT);
        this.precision = DatePrecision.YEAR;
    }

    /**
     * with precision YEAR_MONTH
     */
    public SednoDate (int year, int month) {
        this.localDate = new LocalDate(year,month,NULL_REPLACEMENT);
        this.precision = DatePrecision.YEAR_MONTH;
    }

    /**
     * with precision YEAR_MONTH_DAY
     */
    public SednoDate (int year, int month, int day) {
        this.localDate = new LocalDate(year,month,day);
        this.precision = DatePrecision.YEAR_MONTH_DAY;
    }

    public SednoDate (Date date, DatePrecision precision) {
        this.localDate = new LocalDate(date);
        this.precision = precision;
    }

    /**
     * with default precision YEAR_MONTH_DAY
     */
    public SednoDate (Date date) {
        this.localDate = new LocalDate(date);
        this.precision = DatePrecision.YEAR_MONTH_DAY;
    }

    @Override
    @Column
    public String getDatabaseValue() {
    	if (localDate == null) {
    		return null;
    	}
        return getFormatter().print(localDate);
    }

    /**
     * for hibernate only
     */
    private void setDatabaseValue(String value) {
        SednoDate fromValue = parse(value);
        this.localDate = fromValue.getLocalDate();
        this.precision = fromValue.getPrecision();
    }

    /**
     * @see DatePrecision#getPattern()
     */
    public static SednoDate parse(String value) {
        SednoDate ret = new SednoDate();

        if (value == null) {
            ret.precision = DatePrecision.YEAR_MONTH_DAY;
            ret.localDate = null;
            return ret;
        }

        if (value.length() == DatePrecision.YEAR.getPattern().length()) {
            ret.precision = DatePrecision.YEAR;
        } else
        if (value.length() == DatePrecision.YEAR_MONTH.getPattern().length()) {
            ret.precision = DatePrecision.YEAR_MONTH;
        } else
        if (value.length() == DatePrecision.YEAR_MONTH_DAY.getPattern().length()) {
            ret.precision = DatePrecision.YEAR_MONTH_DAY;
        } else {
            throw new RuntimeException("malformed SednoDate: "+value);
        }

        ret.localDate = ret.getFormatter().parseDateTime(value).toLocalDate();

        return ret;
    }

    @Transient
    public int getYear() {
        return localDate.getYear();
    }


    /**
     * In case of insufficient precision, throws
     * RuntimeException("Insufficient precision")
     *
     * @return Month of year.
     */
    @Transient
    public Integer getMonth() {
        if (precision == DatePrecision.YEAR_MONTH || precision == DatePrecision.YEAR_MONTH_DAY) {
            return localDate.getMonthOfYear();
        } else {
            return null;
        }
    }


    /**
     * In case of insufficient precision, throws
     * RuntimeException("Insufficient precision")
     *
     * @return Day of month
     */
    @Transient
    public Integer getDay() {
        if (precision == DatePrecision.YEAR_MONTH_DAY) {
            return localDate.getDayOfMonth();
        } else {
            return null;
        }
    }


    //

    /**
     * Uwaga! w polach poniżej progu precyzji będzie NULL_REPLACEMENT, ex
     * dla daty '2000-06' otrzymasz new LocalDate(2000,06,01)
     */
    @Transient
    public LocalDate getLocalDate() {
        return localDate;
    }

    @Transient
    public DatePrecision getPrecision() {
        return precision;
    }

    @Override
    public String toString() {
      return getFormatter().print(localDate);
    }

    @Override
    public int hashCode() {
    	return new HashCodeBuilder().
	    	   append(this.getDatabaseValue()).
	    	   toHashCode();
    }

    @Override
    public boolean equals(Object o){
        if( o == null ) {
            return false;
        }

        if( this == o ) {
            return true;
        }

        if (o instanceof Date) {
        	if (precision != DatePrecision.YEAR_MONTH_DAY) {
                return false;
            }

        	Date date = (Date)o;
        	int y = DateUtil.getYear(date);
        	int m = DateUtil.getMonth(date);
        	int d = DateUtil.getDay(date);

        	return this.equals( new SednoDate(y,m,d));
        }
        if (o instanceof SednoDate) {
            return ObjectUtils.nullSafeEquals(this.getDatabaseValue(),
                                    ((SednoDate)o).getDatabaseValue());
        }
        if (o instanceof String) {
            return ObjectUtils.nullSafeEquals(this.getDatabaseValue(),(o));
        }

        return false;
    }


    /**
     * Checks if the passed date is later than the other date. In case of
     * insufficient precision, returns false.
     *
     * @param other
     *            The date to compare
     *
     */
    public boolean after(SednoDate other) {
        if (other == null) {
            throw new RuntimeException("Cannot compare dates - the passed argument is null");
        }

        // It is always possible to compare years.
        final int thisYear = this.getYear();
        final int otherYear = other.getYear();

        if (thisYear > otherYear) {
            return true;
        }
        if (thisYear < otherYear) {
            return false;
        }

        // Further comparisons depend on precision of the dates.
        final DatePrecision thisPrecision = this.precision;
        final DatePrecision othePrecision = other.precision;

        // If it is possible to compare months, do it.
        if ((thisPrecision == DatePrecision.YEAR_MONTH || thisPrecision == DatePrecision.YEAR_MONTH_DAY)
                && (othePrecision == DatePrecision.YEAR_MONTH || othePrecision == DatePrecision.YEAR_MONTH_DAY)) {
            final int thisMonth = this.getMonth();
            final int otherMonth = other.getMonth();
            if (thisMonth > otherMonth) {
                return true;
            }
            if (thisMonth < otherMonth) {
                return false;
            }
        }

        // If it is possible to compare days, do it.
        if (thisPrecision == DatePrecision.YEAR_MONTH_DAY && othePrecision == DatePrecision.YEAR_MONTH_DAY) {
            final int thisDay = this.getDay();
            final int otherDay = other.getDay();
            if (thisDay > otherDay) {
                return true;
            }
            if (thisDay < otherDay) {
                return false;
            }
        }

        // We are not sure if this date is later than other, so we return false.
        return false;
    }

    /**
     * Does this object have month set ?
     */
    public boolean hasMonth() {
        return DatePrecision.YEAR_MONTH.equals(getPrecision())
                || DatePrecision.YEAR_MONTH_DAY.equals(getPrecision());
    }


    /**
     * Does this object have day of month set ?
     */
    public boolean hasDay() {
        return DatePrecision.YEAR_MONTH_DAY.equals(getPrecision());
    }


    //-- private

    private void trimToPrecision() {
        if (DatePrecision.YEAR.equals(precision)) {
            trimDay();
            trimMonth();
        }

        if (DatePrecision.YEAR_MONTH.equals(precision)) {
            trimDay();
        }
    }

    private void trimDay() {
        if (localDate == null) {
            return;
        }

        if (localDate.getDayOfMonth() != NULL_REPLACEMENT) {
            localDate = localDate.withDayOfMonth(NULL_REPLACEMENT);
        }
    }

    private void trimMonth() {
        if (localDate == null) {
            return;
        }

        if (localDate.getMonthOfYear() != NULL_REPLACEMENT) {
            localDate = localDate.withMonthOfYear(NULL_REPLACEMENT);
        }
    }

    @Transient
    private DateTimeFormatter getFormatter() {
        return DateTimeFormat.forPattern(precision.getPattern());
    }

}
