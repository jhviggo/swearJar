package main;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

public class SpeechClientHandler {

    private ClientStream<StreamingRecognizeRequest> stream;

    public SpeechClientHandler(SpeechClient speechClient, ResponseObserver<StreamingRecognizeResponse> observer) {
        this.stream = speechClient.streamingRecognizeCallable().splitCall(observer);
        setupStream();
    }

    private void setupStream() {
        RecognitionConfig recognitionConfig =
                RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setLanguageCode("en-US")
                        .setSampleRateHertz(16000)
                        .build();

        StreamingRecognitionConfig streamingRecognitionConfig =
                StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();

        StreamingRecognizeRequest request =
                StreamingRecognizeRequest.newBuilder()
                        .setStreamingConfig(streamingRecognitionConfig)
                        .build();

        stream.send(request);
    }

    public void send(ByteString data) {
        Thread thread = new Thread(() -> {
            StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(data)
                    .build();
            stream.send(request);
        });
        thread.start();
    }
}
