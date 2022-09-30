package advisor;

import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OAuthServer implements IOAuthServer {
    private static final String REDIRECT_URI = "http://localhost:8080";
    private static final String CLIENT_ID = "2749798bb6cb4ba5a0d9c001e874d805";
    private static final String CLIENT_SECRET = "6c2e5156e52943338f53fc80a79df3d0";
    private static final String GRANT_TYPE = "authorization_code";
    public static String spotifyAccessServerPoint;
    public static String accessToken;

    private HttpServer server;
    private boolean authorized;

    public OAuthServer() {
        this.authorized = false;
    }

    static OAuthServer getOAuthServerInstance() {
        return new OAuthServer();
    }

    @Override
    public void authorizeUser() throws IOException {
        startServer();
        createHttpHandler();
        getCode();
        while (!this.isAuthorized()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (this.isAuthorized()) {
                stopServer();
                System.out.println("Success!");
                break;
            }
        }
    }

    public void authorizeInstance() {
        this.authorized = true;
    }

    @Override
    public boolean isAuthorized() {
        return this.authorized;
    }

    private void startServer() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);
        server.start();
    }

    private void stopServer() {
        server.stop(1);
    }

    private void createHttpHandler() {
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Getting the response query (contains authentication code or error)
                String query = exchange.getRequestURI().getQuery();
                final String clientMessage;
                if(query != null && query.contains("code")) {
                    System.out.println("code received");
                    clientMessage = "Got the code. Return back to your program.";
                    // Request the access token with the authentication code provided.
                    // The code starts in the query one index after the '=' sign.
                    requestToken(query.substring(query.indexOf('=') + 1));
                } else {
                    clientMessage = "Authorization code not found. Try again.";
                }
                exchange.sendResponseHeaders(200, clientMessage.length());
                exchange.getResponseBody().write(clientMessage.getBytes());
                exchange.getResponseBody().close();
            }
        });
    }

    private void requestToken(String code) {
        System.out.println("making http request for access_token...");
        HttpClient client = HttpClient.newBuilder().build();
        // Setting up a POST request of the form: POST https://accounts.spotify.com/api/token
        // REQUEST BODY PARAMETER VALUE: grant_type = "authorization_code", code (the code returned from query),
        //redirect_uri="http://localhost:8080",
        //Authorization comprised of client_id and client_secret.
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(spotifyAccessServerPoint + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString(
                        "client_id=" + CLIENT_ID
                                + "&client_secret=" + CLIENT_SECRET
                                + "&grant_type=" + GRANT_TYPE
                                + "&code=" + code
                                + "&redirect_uri=" + REDIRECT_URI
                                +"&scope=user-modify-playback-state"))
                .build();
        // Send the request to https://accounts.spotify.com/api/token
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            authorized = true; // Success
            accessToken = JsonParser.parseString(response.body()).getAsJsonObject().get("access_token").getAsString(); // Parsing the access token
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCode() {
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code&scope=user-modify-playback-state"
                ,spotifyAccessServerPoint,CLIENT_ID,REDIRECT_URI);
        System.out.println();
        System.out.println("waiting for code...");
    }
}
