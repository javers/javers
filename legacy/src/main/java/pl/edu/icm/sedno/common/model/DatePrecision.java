package pl.edu.icm.sedno.common.model;

/**
 * @see SednoDate
 * @author bart
 */
public enum DatePrecision {
    YEAR           ("yyyy"),
    YEAR_MONTH     ("yyyy-MM"),
    YEAR_MONTH_DAY ("yyyy-MM-dd");
    
    private String pattern;
       
    private DatePrecision(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return pattern;
    }
}