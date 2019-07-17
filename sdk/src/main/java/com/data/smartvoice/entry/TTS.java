package com.data.smartvoice.entry;

import java.util.Arrays;
import java.util.List;

public class TTS extends BaseResult{
    List<String> list_of_slpit_text;

    public List<String> getList_of_slpit_text() {
        return list_of_slpit_text;
    }

    public void setList_of_slpit_text(List<String> list_of_slpit_text) {
        this.list_of_slpit_text = list_of_slpit_text;
    }

    @Override
    public String toString() {
        return "TTS{" +
                list_of_slpit_text +
                '}';
    }
}
