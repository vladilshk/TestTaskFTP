import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class FTpClient {
    private Socket clientSocket = null;

    private BufferedReader reader = null;

    private BufferedWriter writer = null;

    public synchronized void connect(String host, int port, String user,
                                     String pass) throws IOException {
        if (clientSocket != null) {
            throw new IOException("Error: You hava already connected");
        }
        clientSocket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(clientSocket.getOutputStream()));

        /*String response = readLine();
        if (!response.startsWith("220 ")) {
            throw new IOException(
                    "SimpleFTP received an unknown response when connecting to the FTP server: "
                            + response);
        }*/
        String response;
        for (int i = 0; i < 3; i++) {
            response = readCommand();
            if (!response.startsWith("220")) {
                throw new IOException(
                        "SimpleFTP received an unknown response when connecting to the FTP server: "
                                + response);
            }
        }

        sendCommand("USER " + user);

        response = readCommand();
        if (!response.startsWith("331 ")) {
            throw new IOException(
                    "SimpleFTP received an unknown response after sending the user: "
                            + response);
        }

        sendCommand("PASS " + pass);

        response = readCommand();
        if (!response.startsWith("230 ")) {
            throw new IOException(
                    "SimpleFTP was unable to log in with the supplied password: "
                            + response);
        }

        // Now logged in.
    }

    public synchronized void disconnect() throws IOException {
        try {
            sendCommand("QUIT");
        } finally {
            clientSocket = null;
        }
    }

    public void sendData(String massage) throws IOException {
        sendCommand("PASV");
        String response = readCommand();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }

        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: "
                        + response);
            }
        }

        sendCommand("STOR " + "students.txt");
        Socket dataSocket = new Socket(ip, port);
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[1024];
        buffer = massage.getBytes();
        output.write(buffer, 0, buffer.length);
        output.flush();
        output.close();

        readCommand();
        readCommand();
    }

    public void receiveData() throws IOException {
        sendCommand("PASV");
        String response = readCommand();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: "
                    + response);
        }

        String ip = null;
        int port = -1;
        int opening = response.indexOf('(');
        int closing = response.indexOf(')', opening + 1);
        if (closing > 0) {
            String dataLink = response.substring(opening + 1, closing);
            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException("SimpleFTP received bad data link information: "
                        + response);
            }
        }

        sendCommand("RETR " + "students.txt");
        Socket dataSocket = new Socket(ip, port);
        BufferedInputStream inputStream = new BufferedInputStream(dataSocket.getInputStream());
        byte[] buffer = new byte[1024];
        buffer = inputStream.readAllBytes();

        String massage = new String(buffer);
        System.out.println(massage);
        inputStream.close();

        readCommand();
        readCommand();
    }

    public synchronized boolean bin() throws IOException {
        sendCommand("TYPE I");
        String response = readCommand();
        return (response.startsWith("200 "));
    }

    public synchronized boolean cwd(String dir) throws IOException {
        sendCommand("CWD " + dir);
        String response = readCommand();
        return (response.startsWith("250 "));
    }

    private void sendCommand(String line) throws IOException {
        if (clientSocket == null) {
            throw new IOException("Error: you are not connected");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
        } catch (IOException e) {
            clientSocket = null;
            throw e;
        }
    }

    private String readCommand() throws IOException {
        String line = reader.readLine();

        return line;
    }

    /*public String createJSON(){
        JSOnEditor js = new JSOnEditor();

        //return js.addStudent(js.createJson(), "Voldi");
        String str = js.createJson();
       str = js.addStudent(str, "cccvovan");
        str = js.addStudent(str, "dddnastia");
       str = js.addStudent(str, "hhhuila");
        str = js.addStudent(str, "bpppidor");
        str = js.addStudent(str, "aaasosiChlen");
        System.out.println(js.getAllStudents(str));
        //str = js.deleteStudent(str, 1);
        //str = js.deleteStudent(str, 3);
        //str = js.deleteStudent(str, 4);
        //str = js.deleteStudent(str, 5);


        return str;
    }*/
}
