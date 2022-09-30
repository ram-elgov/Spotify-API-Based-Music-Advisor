package advisor;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        IOAuthServer oauthServer = OAuthServer.getOAuthServerInstance();
        String userInput;
        String categoryName = null;
        String[] userInputArray;
        Goal userGoal;
        OAuthServer.spotifyAccessServerPoint = "https://accounts.spotify.com";
        Advisor.spotifyApiServerPoint = "https://api.spotify.com";
        if (args != null && args.length > 1 && "-access".equals(args[0]) && "-resource".equals(args[2])) {
            OAuthServer.spotifyAccessServerPoint = args[1];
            Advisor.spotifyApiServerPoint = args[3];
        }
        while (true) {
            userInput = scanner.nextLine();
            userInputArray = userInput.split(" ");
            userGoal = setUserGoal(userInputArray);
            if (userGoal == null) {
                System.out.printf("The goal: %s is not supported.\n", userInputArray[0]);
                System.exit(-1);
            }
            if (userInput.contains("playlists")) {
                categoryName = userInput.substring(10);
            }
            runAdvisor(userGoal, categoryName, oauthServer);
        }
    }

    /**
     * a function to assign the input goal as was given by the user.
     *
     * @param userInput is the input from the user
     * @return the requested goal.
     */
    public static Goal setUserGoal(String[] userInput) {
        return switch (userInput[0]) {
            case "auth" -> Goal.AUTH;
            case "new" -> Goal.NEW;
            case "featured" -> Goal.FEATURED;
            case "categories" -> Goal.CATEGORIES;
            case "playlists" -> Goal.PLAYLISTS;
            case "exit" -> Goal.EXIT;
            default -> null;
        };
    }

    public static void runAdvisor(Goal userGoal, String categoryName,IOAuthServer oauthServer) throws IOException, InterruptedException {
        Advisor advisor = new Advisor();
        if (!oauthServer.isAuthorized() && userGoal != Goal.EXIT && userGoal != Goal.AUTH) {
            IOAuthServer.requestAuthorization();
            return;
        }
        switch (userGoal) {
            case AUTH -> oauthServer.authorizeUser();
            case NEW -> System.out.print(advisor.getNewReleases());
            case FEATURED -> System.out.print(advisor.getFeatured());
            case CATEGORIES -> System.out.print(advisor.getCategories());
            case PLAYLISTS -> System.out.print(advisor.getPlaylists(categoryName));
            case EXIT -> advisor.Exit();
            default -> throw (new RuntimeException("invalid goal provided!"));
        }
    }
}