package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A library that handles parsing the returned data from the Spotify API.
 * Each goal has an appropriate parsing function.
 * @author Ram Elgov
 */

public class Parser {
    public static String parseNewRelease(JsonObject json) {
        List<JsonElement> items = new ArrayList<>();
        JsonArray artistsObjects;
        JsonObject albums;
        StringBuilder result = new StringBuilder("");
        try {
            albums = json.get("albums").getAsJsonObject();
            for (JsonElement item : albums.getAsJsonArray("items"))
                items.add(item);
            for (JsonElement item : items) {
                List<String> artists = new ArrayList<>();
                result.append(item.getAsJsonObject().get("name").getAsString());
                result.append("\n");
                artistsObjects = item.getAsJsonObject().getAsJsonArray("artists");
                for (JsonElement artistObject : artistsObjects)
                    artists.add(artistObject.getAsJsonObject().get("name").getAsString());
                result.append(artists);
                result.append("\n");

                result.append(item.getAsJsonObject().get("external_urls")
                        .getAsJsonObject()
                        .get("spotify")
                        .getAsString());
                result.append("\n\n");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result.toString();
    }

    public static String parseFeatured(JsonObject json) {
        JsonArray items = json.getAsJsonObject("playlists").getAsJsonArray("items");
        StringBuilder result = new StringBuilder();
        for (JsonElement item : items) {
            result.append(item.getAsJsonObject().get("name").getAsString());
            result.append(System.getProperty("line.separator"));
            result.append(item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString());
            result.append(System.getProperty("line.separator"));
            result.append(System.getProperty("line.separator"));
        }
        return result.toString();
    }

    public static String parseCategories(JsonObject json) {
        JsonArray items = json.getAsJsonObject("categories").getAsJsonArray("items");
        StringBuilder result = new StringBuilder();
        for (JsonElement item : items) {
            result.append(item.getAsJsonObject().get("name").getAsString());
            result.append(System.getProperty("line.separator"));
        }
        return result.toString();
    }

    public static String parsePlaylistsByCategory(JsonObject json) {
        JsonArray items = json.getAsJsonObject("playlists").getAsJsonArray("items");
        StringBuilder result = new StringBuilder();
        for (JsonElement item : items) {
            result.append(item.getAsJsonObject().get("name").getAsString());
            result.append("\n");
            result.append(item.getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString());
            result.append("\n\n");
        }
        return result.toString();
    }
}
