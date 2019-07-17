package com.data.smartvoice.entry;

public class VoiceEntry extends BaseEntry{
    protected String Audio;

    public VoiceEntry(){

    }

    public VoiceEntry(String id,String audio){
        Id = id;
        Audio = audio;
    }

    public String getAudio() {
        return Audio;
    }

    public void setAudio(String audio) {
        Audio = audio;
    }

    @Override
    public String toString() {
        return "VoiceEntry{" +
                "Id='" + Id + '\'' +
                ", Audio='" + Audio + '\'' +
                '}';
    }
}
