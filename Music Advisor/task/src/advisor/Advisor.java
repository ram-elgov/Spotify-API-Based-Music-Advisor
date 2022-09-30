package advisor;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class Advisor implements IAdvisor {
    private static final HttpClient client = HttpClient.newBuilder().build();
    public static String spotifyApiServerPoint;
    private static JsonObject allCategories;

    private static String findCategoryIdByName(String categoryName) {
        JsonArray categoryItems = allCategories.get("categories").getAsJsonObject().getAsJsonArray("items");
        for (JsonElement item: categoryItems) {
            if(categoryName.toLowerCase(Locale.ROOT).equals(item.getAsJsonObject().get("name").getAsString().toLowerCase(Locale.ROOT))) {
                return item.getAsJsonObject().get("id").getAsString();
            }
        }
        return null;
    }

    public static String createTitle(String title) {
        return "---" + title + "---\n";
    }

    @Override
    public String getNewReleases() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OAuthServer.accessToken)
                .uri(URI.create(spotifyApiServerPoint + "/v1/browse/new-releases"))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return Parser.parseNewRelease(JsonParser.parseString(response.body()).getAsJsonObject());
    }

    @Override
    public String getFeatured() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OAuthServer.accessToken)
                .uri(URI.create(spotifyApiServerPoint + "/v1/browse/featured-playlists"))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return Parser.parseFeatured(JsonParser.parseString(response.body()).getAsJsonObject());
    }

    @Override
    public String getCategories() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OAuthServer.accessToken)
                .uri(URI.create(spotifyApiServerPoint + "/v1/browse/categories"))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        allCategories = JsonParser.parseString(response.body()).getAsJsonObject();
        return Parser.parseCategories(JsonParser.parseString(response.body()).getAsJsonObject());
    }

    public String getPlaylists(String categoryName) throws IOException, InterruptedException {
        getCategories();
        String categoryId = findCategoryIdByName(categoryName);
        JsonObject json;
        if (categoryId == null) {
            return "Unknown category name.\n";
        }
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + OAuthServer.accessToken)
                .uri(URI.create(spotifyApiServerPoint + "/v1/browse/categories/" + categoryId + "/playlists"))
                .build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (response.body().contains("error")) {
            JsonObject error = json.getAsJsonObject("error");
            return (error.get("message").getAsString());
        }
        return Parser.parsePlaylistsByCategory(json);
    }

    @Override
    public void Exit() {
        System.out.println(createTitle("GOODBYE!"));
        System.exit(0);
    }
}