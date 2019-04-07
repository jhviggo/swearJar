package main;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AudioAnalyzer implements Runnable {

    private TranscriptObserver transcriptObserver = new TranscriptObserver();
    private AudioHandler audioHandler = new AudioHandler();
    private TargetDataLine dataLine;

    void add(AudioAnalyzerObserver observer) {
        transcriptObserver.add(observer);
    }

    private boolean run = false;

    synchronized void analyze(boolean run) {
        this.run = run;
    }

    synchronized boolean getRun() {
        return run;
    }

    public AudioAnalyzer() {
        if (!audioHandler.isStreamAvaiable()) {
            System.out.println("Microphone not supported");
            System.exit(0);
        }
    }

    @Override
    public void run() {
        dataLine = audioHandler.stream();
        dataLine.start();

        CredentialsProvider credentialsProvider = null;
        SpeechSettings settings = null;

        try {
            String json = "{\n  \"type\": \"service_account\",\n  \"project_id\": \"swearjar-1554492811418\",\n  \"private_key_id\": \"13e438a8a55fa7bd23c1a3194bbeccddb1220f55\",\n  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCqY+/aaholbm9y\\nCZfjyV167CzicoQ1Npk0mqFaxHXK0Tw5EJkV+w0AzCWOCBL3gOPymR7KtROcUNfG\\nUPcSrxh76aWROFIJXffF1XwrVEPDAEkOzlMxRsfMXNc4l4ofgOKoGaNkP+bKsgyo\\nJLlNzJQ/jp5OsSu8+86+OSBxb51ACWt4EkKmoxEMAYsd1pnBXTbVlmN+5PJ0Phmh\\nd7g8oqRGW21gKRv6ABE/PCI7YOSFN4/PPsZKoviwb5BwNvATiNyGwyrWMT9a2AaM\\nStQ45elAiWptdL6ME5Z3axePyzB3oEtf0dVQhD1Ewz3iO13OvVTQiJaDfNAAGzqC\\nRbHcPPN1AgMBAAECggEAIlgb9bSaug5BOOVtn+yL4tW9c1RdSSOM7HE5kkPUsVGM\\n17y8epV8iLtdW3xNpGUurQzHi9jFT+XspCzYLEQuRXWyGWwWfwxZf58ZFNu6f9Ds\\nKOeWFbHcXlkdSWskHFb8eKEYUcHemV8blX23exLAE/KBfr+P3xsLUETQAQVcFxaq\\ns9YMWiky3DOSbrP26FrU/y4s42ifHG9lcn0+WmpfjGFC2I6DNl/83GkC8huseK2w\\nRhInQoJ16Q4UnobgwxQhyF63zKwTiRc+lrwsmSmch+PhYmNfGICXKY48RioHkZBe\\nrb6wpRhLdjbRvc8mV4kj6i+n7T7XXzmxW8oYjLlDkQKBgQDaOJs/EQfqryVfEh6n\\nPLx5AyqM+mpcjFiQZHbJNp+DdhuiKvkBiqlezTyRvEZTSsQ1x6YR2JzQDTMyZjJ1\\n54qMsDV9a7tCicG5RMU5VvM1jmzEgCdEQFZniBO9cgj0XZ4X53J/uw14tuaxhhdG\\n6xOH1QJ7S8r7XR/jWIBLFoIA8QKBgQDH44IxQNatxmNlSHORrAHrLMVDj2dnedvl\\nogQomiQOC1OrBZCyHxVw9+d/a7UR34XsUkl4PomuHBKigVj7OLuTct/TCKn9H6MS\\nbb95zgRS7umQH+DmGjD+J1D/sV9OlUYd+dmn4jLgAVWb2zXfRCUkrvuPJzn5nJcR\\nlLpm27XaxQKBgGN4JAPGTpaCCYXha4mNnNeIQvAKAUt4SbJfqOmk9WbmAkiUT/aM\\n7xEgejhv2pqC3Rcqjx9ZXnlTWv/hJ6UoyrXAaFFoNu5ofp5lo4S0hp3l8txLEhsB\\nPKFqnJzhCwXgWnzI6h8g804QCO3oklmoM7nZtYzgCoEzfKKTp78SXmXxAoGAc6EJ\\nFCLUzOuBXfqeV/svlPJuHlnbUEFu0hR54V7hUnp86OasQE244oOVl+EfenpaB03G\\nkFdk36H/qZ+3cIPuBiqijXpqkzF07NkttXFWkP8Bf0zhlVo7Uxo9QdxCLLwvNnHs\\n1bAFDA3ZjvIs5L2P2vuaK8JeSCWsQhBOD+gy950CgYEAjxyi66yzy/ms4Hc/dZ+r\\nCy18TaPWIDva2EQuX9Uec5xc4Atc3dwMEJQSqXtZYeIC2GYoEJsBnKqmbPVEp0a7\\nOA2zDvuApdRPugK369K5l3JFysDQppxMQdqM4GAZR09vv8wz5FfP3sntqj9+YtQO\\nMHg9You7AKDam1fhfRmkEa8=\\n-----END PRIVATE KEY-----\\n\",\n  \"client_email\": \"swearjar-190406001342@swearjar-1554492811418.iam.gserviceaccount.com\",\n  \"client_id\": \"104988934829356483720\",\n  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/swearjar-190406001342%40swearjar-1554492811418.iam.gserviceaccount.com\"\n}\n";
            InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
            credentialsProvider = FixedCredentialsProvider.create(ServiceAccountCredentials.fromStream(stream));
            settings = SpeechSettings.newBuilder().setCredentialsProvider(credentialsProvider).build();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Credentials not able");
            System.exit(0);
        }

        while (true) {
            while (getRun()) {
                try (SpeechClient client = SpeechClient.create(settings)) {
                    startClient(client);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    private void startClient(SpeechClient client) throws Exception {

        SpeechClientHandler handler = new SpeechClientHandler(client, transcriptObserver);

        long endTime = System.currentTimeMillis() + 60000;
        AudioInputStream audio = new AudioInputStream(dataLine);

        while (System.currentTimeMillis() < endTime && run) {
            byte[] data = new byte[6400];
            audio.read(data);
            handler.send(ByteString.copyFrom(data));
        }
    }
}