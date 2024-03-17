package org.nbu.service;

/**
 * @author Nikolay Boyko
 */
public interface ICurrencyService <T, R> {

    T getAllCurrency();

    T getCurrencyByDate(R date);

    void deleteCurrencyDataByDate(R date);

}
