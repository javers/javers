package pl.edu.icm.sedno.common.dao;

public class CriterionIsNotUnique extends RuntimeException {
    public CriterionIsNotUnique(String message) {
        super (message);
    }
}
