package main;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SwearManager {
    private static SwearManager singleton = new SwearManager();
    private SwearManager() {
        try {
            this.swears = getSwears();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static SwearManager getInstance( ) {
        return singleton;
    }

    private List<SwearModel> swears = new ArrayList<>();

    public List<SwearModel> analyzeString(String transcription) {

        List<SwearModel> found = new ArrayList<>();

        for (SwearModel m : swears) {
            if (transcription.toLowerCase().contains(m.word.toLowerCase())) found.add(m);
        }

        return found;
    }

    public List<SwearModel> getSwears() throws IOException {
        try (InputStream is = new URL("https://swearjar-4a26e.firebaseio.com/known_swears.json").openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            Gson gson = new Gson();

            Map<String, SwearModel> users = gson.fromJson(rd, new TypeToken<Map<String, SwearModel>>(){}.getType());
            for (Map.Entry<String, SwearModel> entry : users.entrySet()) {
                entry.getValue().id = entry.getKey();
            }

            return new ArrayList<>(users.values());
        }
    }
}
