package org.javers.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;

public abstract class JaversCoreProperties {
    private String algorithm = "simple";
    private String mappingStyle = "field";
    private boolean newObjectSnapshot = false;
    private boolean prettyPrint = true;
    private boolean typeSafeValues = false;
    private String packagesToScan = "";
    private DatePrettyPrintFormats datePrettyPrintFormats = new DatePrettyPrintFormats();

    public String getAlgorithm() {
        return algorithm;
    }

    public String getMappingStyle() {
        return mappingStyle;
    }

    public boolean isNewObjectSnapshot() {
        return newObjectSnapshot;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public boolean isTypeSafeValues() {
        return typeSafeValues;
    }

    public String getPackagesToScan() {
        return packagesToScan;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setMappingStyle(String mappingStyle) {
        this.mappingStyle = mappingStyle;
    }

    public void setNewObjectSnapshot(boolean newObjectSnapshot) {
        this.newObjectSnapshot = newObjectSnapshot;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public void setTypeSafeValues(boolean typeSafeValues) {
        this.typeSafeValues = typeSafeValues;
    }

    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public DatePrettyPrintFormats getDatePrettyPrintFormats() {
        return datePrettyPrintFormats;
    }

    /**
     * @see DateTimeFormatter#ofPattern(String)
     */
    public void registerDatePrettyPrintFormat(Class<? extends Temporal> forType, String format) {
        this.datePrettyPrintFormats.registerFormat(forType, format);
    }

    public static class DatePrettyPrintFormats {
        private Map<Class<? extends Temporal>, String> formats = new HashMap<>();

        private String LocalDateTimeFormat;
        private String ZonedDateTimeFormat;
        private String LocalDateFormat;
        private String LocalTimeFormat;

        private static final String DEFAULT_DATE_FORMAT = "dd M yyyy";
        private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

        public DatePrettyPrintFormats() {
            setLocalDateTimeFormat(DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT);
            setZonedDateTimeFormat(DEFAULT_DATE_FORMAT + " " + DEFAULT_TIME_FORMAT+"Z");
            setLocalDateFormat(DEFAULT_DATE_FORMAT);
            setLocalTimeFormat(DEFAULT_TIME_FORMAT);
        }

        public void registerFormat(Class<? extends Temporal> forType, String format) {
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            formats.put(forType, format);
        }

        public void setLocalDateTimeFormat(String localDateTimeFormat) {
            registerFormat(LocalDateTime.class, localDateTimeFormat);
        }

        public void setZonedDateTimeFormat(String zonedDateTimeFormat) {
            registerFormat(ZonedDateTime.class, zonedDateTimeFormat);
        }

        public void setLocalDateFormat(String localDate) {
            registerFormat(LocalDate.class, localDate);
        }

        public void setLocalTimeFormat(String localTime) {
            registerFormat(LocalTime.class, localTime);
        }

        public String getLocalDateTimeFormat() {
            return formats.get(LocalDateTime.class);
        }

        public String getZonedDateTimeFormat() {
            return formats.get(ZonedDateTime.class);
        }

        public String getLocalDateFormat() {
            return formats.get(LocalDate.class);
        }

        public String getLocalTimeFormat() {
            return formats.get(LocalTime.class);
        }
    }
}
