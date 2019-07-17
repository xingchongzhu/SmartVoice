package com.data.smartvoice.entry;

import java.util.List;

/**
 * A success http request result with a {@link T} data.
 * @param
 */
public class SpeakerEntry extends BaseResult {
    List<String> Data;

    public List<String> getData() {
        return Data;
    }

    public void setData(List<String> result) {
        Data = result;
    }
}
