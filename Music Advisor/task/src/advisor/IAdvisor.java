package advisor;

import java.io.IOException;

public interface IAdvisor {
    String getNewReleases() throws IOException, InterruptedException;
    String getFeatured() throws IOException, InterruptedException;
    String getCategories() throws IOException, InterruptedException;
    String getPlaylists(String categoryName) throws IOException, InterruptedException;
    void Exit();
}
