import java.io.IOException;

public class TestClientSocket {
    public static void main(String[] args) throws IOException {
        FTpClient ftpClient = new FTpClient();
        ftpClient.connect("127.0.0.1", 21, "Vovai", "23343");
        ftpClient.cwd("books");
        ftpClient.bin();
        //ftpClient.sendData(ftpClient.createJSON());
        ftpClient.receiveData();
        ftpClient.disconnect();

    }
}
