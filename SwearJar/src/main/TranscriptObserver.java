package main;

import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;

import java.util.ArrayList;
import java.util.List;

public class TranscriptObserver implements ResponseObserver<StreamingRecognizeResponse> {

    private List<AudioAnalyzerObserver> observers = new ArrayList<>();

    void add(AudioAnalyzerObserver observer) {
        observers.add(observer);
    }

    @Override
    public void onStart(StreamController controller) {}

    @Override
    public void onResponse(StreamingRecognizeResponse response) {

        StreamingRecognitionResult result = response.getResultsList().get(0);
        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
        System.out.printf("Transcript : %s\n", alternative.getTranscript());
        String transcript = alternative.getTranscript();

        for (AudioAnalyzerObserver o : observers) o.didAnalyze(transcript);

    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable t) {
        System.out.println(t);
    }
}
