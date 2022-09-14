import java.io.*;
import java.lang.invoke.SwitchPoint;
import java.net.NoRouteToHostException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.StringTokenizer;

public class FTpClient {

    private boolean isConnected = false;
    private Socket clientSocket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private JSOnEditor js = null;

    private boolean passiveMode = false;
    private boolean activeMode = false;


    public synchronized void connect() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String response;
        String[] conDate;
        //get server IP and connect to server
        while (true) {
            System.out.print("Input server IP: ");
            String serverIP = scanner.nextLine();
            System.out.println("Trying to connect...");
            try {
                clientSocket = new Socket(serverIP, 21);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                response = readCommand();
                if (!response.startsWith("220 ")) {
                    System.out.println("Error: can't connect to server. Maybe you have input wrong IP. Please try again.");
                } else {
                    System.out.println("You have successfully connected");
                    break;
                }
            } catch (NoRouteToHostException | UnknownHostException e) {
                System.out.println("Error: can't connect to server. Maybe you have input wrong IP. Please try again.");
            }
        }

        while (true) {
            System.out.print("Input your username: ");
            String username = scanner.nextLine();
            sendCommand("USER " + username);

            response = readCommand();
            if (!response.startsWith("331 ")) {
                System.out.println("Error: Wrong username");
            }

            System.out.print("Input your password: ");
            String password = scanner.nextLine();
            sendCommand("PASS " + password);

            response = readCommand();
            if (!response.startsWith("230 ")) {
                System.out.println("Error: wrong username or password");
            } else {
                break;
            }
        }

        isConnected = true;
        System.out.println("Log in successfully");
    }

    public synchronized void disconnect() throws IOException {
        try {
            sendCommand("QUIT");
        } finally {
            clientSocket = null;
        }
    }

    public void sendData(String massage) throws IOException {
        Socket dataSocket;
        if (activeMode) {
            dataSocket = PORT("STOR students");
        } else {
            dataSocket = PASV("STOR students");
        }

        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[1024];
        buffer = massage.getBytes();
        output.write(buffer, 0, buffer.length);
        output.flush();
        output.close();
        dataSocket.close();

        readCommand();
        readCommand();
    }

    public String receiveData() throws IOException {
        Socket dataSocket;
        if (activeMode) {
            dataSocket = PORT("RETR students");
        } else {
            dataSocket = PASV("RETR students");
        }

        BufferedInputStream inputStream = new BufferedInputStream(dataSocket.getInputStream());
        byte[] buffer = new byte[1024];
        inputStream.read(buffer);

        String massage = new String(buffer);
        inputStream.close();
        dataSocket.close();
        readCommand();
        readCommand();
        return massage;
    }

    //turning on a passive mode
    public Socket PASV(String command) throws IOException {
        sendCommand("PASV");
        String response = readCommand();
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

            }
        }
        sendCommand(command);


        return new Socket(ip, port);

    }

    public Socket PORT(String command) throws IOException {
        //localhost, your port 50001
        sendCommand("PORT 127,0,0,1,195,81");
        String response = readCommand();
        ServerSocket connectionSocket = new ServerSocket(50001);
        sendCommand(command);
        Socket dataSocket = connectionSocket.accept();
        connectionSocket.close();
        return dataSocket;
    }

    public void bin() throws IOException {
        sendCommand("TYPE I");
        String response = readCommand();
    }

    public void cwd(String dir) throws IOException {
        sendCommand("CWD " + dir);
        String response = readCommand();
    }

    private void sendCommand(String line) throws IOException {
        if (clientSocket == null) {
            throw new IOException("Error: you are not connected");
        }
        try {
            writer.write(line + "\n"); //for linux \n for windows \r\n
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

    private void chooseMode() {

        System.out.println("For using passive mode type \"0\", for active \"1\"");
        while (true) {
            Scanner scanner = new Scanner(System.in);
            try {
                int mode = scanner.nextInt();
                if (mode == 0) {
                    passiveMode = true;
                    System.out.println("Now you use passive mode.");
                    break;
                } else if (mode == 1) {
                    activeMode = true;
                    System.out.println("Now you use active mode.");
                    break;
                } else {
                    System.out.println("Wrong input. Try again");
                }
            } catch (Exception e) {
                System.out.println("Wrong input. Try again");
            }
        }
    }

    public void mainLoop() throws IOException {
        connect();
        bin();
        chooseMode();
        startMenu();
        while (isConnected) {
            System.out.println("Input a command:");
            Scanner scanner = new Scanner(System.in);
            int num = getIntFromUser("Error: Wrong command. Please try again.");
            switch (num) {
                case 1: {
                    getStudentsList();
                    break;
                }
                case 2: {
                    getStudentByID();
                    break;
                }
                case 3: {
                    addStudent();
                    break;
                }
                case 4: {
                    deleteStudent();
                    break;
                }
                case 5: {
                    isConnected = false;
                    break;
                }
                default: {
                    System.out.println("Error: Wrong command. Please try again.");
                    break;
                }
            }
        }

        disconnect();
    }

    public void addStudent() throws IOException {
        String newStudent;
        System.out.println("Input name of a new student:");
        while (true){
            Scanner scanner = new Scanner(System.in);
            newStudent = scanner.nextLine();
            if(newStudent.indexOf('\"') >= 0){
                System.out.println("Students name shouldn't contain '\"'");
            }
            else{
                break;
            }
        }
        sendData(JSOnEditor.addStudent(receiveData(), newStudent));
    }

    public void deleteStudent() throws IOException {
        System.out.print("Input id of student you want to delete: ");
        int studentForDelete = getIntFromUser("Error: Id could be only integer. Please try again.");
        String json = JSOnEditor.deleteStudent(receiveData(), studentForDelete);
        if (json == null) {
            System.out.println("There is no student with ID " + studentForDelete);
        } else {
            sendData(json);
        }

    }

    public void getStudentsList() throws IOException {
        String studentsList = JSOnEditor.studentsToString(receiveData());
        if (studentsList.length() == 0) {
            System.out.println("There are no any student in list");
        } else {
            System.out.println("Students:\n" + studentsList);
        }
    }

    public void getStudentByID() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input id of student to see his/her name: ");
        int student = getIntFromUser("Error: Id could be only integer. Please try again.");
        String studentName = JSOnEditor.getStudentById(receiveData(), student);
        if (studentName == null) {
            System.out.println("There is no student with ID " + student);
        } else {
            System.out.println("Student: " + studentName);
        }
    }


    public int getIntFromUser(String errorMassage) {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            int value;
            try {
                value = scanner.nextInt();
            } catch (Exception e) {
                System.out.println(errorMassage);
                continue;
            }
            return value;
        }
    }

    public void startMenu() {
        System.out.println();
        System.out.println("Chose one of this commands");
        System.out.println("1. Get the students list");
        System.out.println("2. Get student by ID");
        System.out.println("3. Add a student");
        System.out.println("4. Delete a student");
        System.out.println("5. Quit");

    }
}