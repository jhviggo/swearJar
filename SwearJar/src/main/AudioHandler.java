package main;

import javax.sound.sampled.*;

public class AudioHandler {

    private AudioFormat format;
    private DataLine.Info targetInfo;

    public AudioHandler() {
        this.format = new AudioFormat(16000,16,1,true,false);
        this.targetInfo = new DataLine.Info(TargetDataLine.class, format);
    }

    public boolean isStreamAvaiable() {
        return AudioSystem.isLineSupported(targetInfo);
    }

    public TargetDataLine stream() {
        try {
            TargetDataLine dataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            dataLine.open(format);
            return dataLine;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }
}
