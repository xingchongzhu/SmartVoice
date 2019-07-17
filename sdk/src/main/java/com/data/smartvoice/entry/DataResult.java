package com.data.smartvoice.entry;

import java.sql.SQLRecoverableException;
import java.util.List;

/**
 * A success http request result with a {@link T} data.
 * @param
 */
public class DataResult<T> extends BaseResult {
    List<T> Data;

    public List<T> getData() {
        return Data;
    }

    public void setData(List<T> result) {
        Data = result;
    }
}
