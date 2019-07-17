package com.data.smartvoice.entry;

import java.util.List;

/**
 * A success http request result with a {@link T} data.
 * @param
 */
public class RecogintionEntry{
    String Speaker;
    float Score;

    public String getSpeaker() {
        return Speaker;
    }

    public void setSpeaker(String speaker) {
        Speaker = speaker;
    }

    public float getScore() {
        return Score;
    }

    public void setScore(float score) {
        Score = score;
    }

    @Override
    public String toString() {
        return "RecogintionEntry{" +
                "Speaker='" + Speaker + '\'' +
                ", Score=" + Score +
                '}';
    }
}
