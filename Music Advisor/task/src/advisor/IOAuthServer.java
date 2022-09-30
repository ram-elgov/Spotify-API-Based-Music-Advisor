package advisor;

import java.io.IOException;

public interface IOAuthServer {
    void authorizeUser() throws IOException;

    boolean isAuthorized();

    static void requestAuthorization() {
        System.out.println("Please, provide access for application.");
    }

    void authorizeInstance();
}
