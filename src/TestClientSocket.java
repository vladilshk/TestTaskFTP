import java.io.IOException;

public class TestClientSocket {
    public static void main(String[] args) throws IOException {
        FTpClient ftpClient = new FTpClient();
        ftpClient.mainLoop();

    }
}
