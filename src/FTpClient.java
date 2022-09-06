import java.io.*;
import java.lang.invoke.SwitchPoint;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class FTpClient {

    private boolean isConnected = false;
    private Socket clientSocket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private JSOnEditor js = null;


    public synchronized void connect() throws IOException {
        String[] conDate =  getDateForConnection();
        System.out.println("Trying to connect...");
        if (clientSocket != null) {
            throw new IOException("Error: You hava already connected");
        }
        clientSocket = new Socket(conDate[2], 21);
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

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
                        "Error: received an unknown response when connecting to the FTP server: "
                                + response);
            }
        }

        sendCommand("USER " + conDate[0]);

        response = readCommand();
        if (!response.startsWith("331 ")) {
            throw new IOException(
                    "SimpleFTP received an unknown response after sending the user: "
                            + response);
        }

        sendCommand("PASS " + conDate[1]);

        response = readCommand();
        if (!response.startsWith("230 ")) {
            throw new IOException(
                    "SimpleFTP was unable to log in with the supplied password: "
                            + response);
        }

        isConnected = true;
        System.out.println("You have successfully connected");
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
        Socket dataSocket = PASV();
        sendCommand("STOR " + "students.txt");
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[1024];
        buffer = massage.getBytes();
        output.write(buffer, 0, buffer.length);
        output.flush();
        output.close();

        readCommand();
        readCommand();
    }

    public String receiveData() throws IOException {
        Socket dataSocket = PASV();
        sendCommand("RETR " + "students.txt");
        BufferedInputStream inputStream = new BufferedInputStream(dataSocket.getInputStream());
        byte[] buffer = new byte[1024];
        buffer = inputStream.readAllBytes();

        String massage = new String(buffer);
        inputStream.close();

        readCommand();
        readCommand();
        return massage;
    }

    //turning on a passive mode
    public Socket PASV() throws IOException {
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


         return new Socket(ip, port);

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
            writer.write(line + "\r\n"); //for linux \n for windows \r\n
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

    public void workSpase() throws IOException {
        //connect("127.0.0.1", 21, "Vovai", "23343");
        connect();
        cwd("books");
        bin();
        while (isConnected){
            System.out.println("Input a command:");
            Scanner scanner = new Scanner(System.in);
            int num = scanner.nextInt();
            switch(num){
                case 1:{
                    getStudentsList();
                    break;
                }
                case 2:{
                    getStudentByID();
                    break;
                }
                case 3:{
                    addStudent();
                    break;
                }
                case 4:{
                    deleteStudent();
                    break;
                }
                case 5:{
                    isConnected = false;
                    break;
                }
                default:{
                    System.out.println("Error: you tried to input wrong command");
                    break;
                }
            }
        }

        disconnect();
    }

    public void addStudent() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input name of a new student:");
        String newStudent = scanner.nextLine();
        sendData(JSOnEditor.addStudent(receiveData(), newStudent));
    }

    public void deleteStudent() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input id of student you want to delete");
        int studentForDelete = scanner.nextInt();
        sendData(JSOnEditor.deleteStudent(receiveData(), studentForDelete));
    }
    public void getStudentsList() throws IOException {
        System.out.println("Students:\n" + JSOnEditor.studentsToString(receiveData()));
    }

    public void getStudentByID() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input id of student to see his/her name: ");
        int student = scanner.nextInt();
        System.out.println(JSOnEditor.getStudentById(receiveData(), student));
    }

    public String[] getDateForConnection(){
        String[] connectInfo = new String[3];
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input your username: ");
        connectInfo[0] = scanner.nextLine();
        System.out.print("Input your password: ");
        connectInfo[1] = scanner.nextLine();
        System.out.print("Input server IP: ");
        connectInfo[2] = scanner.nextLine();

        return connectInfo;
    }

}
