import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.NoSuchElementException;
import java.util.Scanner;
/*
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
*/

public class LogReceiver {
    int port;
    int threshold;
    String resultDir;

    LogReceiver(int p, int t, String r) {
        port = p;
        threshold = t;
        resultDir = r;
    }

    public void run() throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);


        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        int id = 0;
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new Receiver(clientSocket, id++)).start();
        }

    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage: program port moveToFileThreshold resultDir");
            return;
        }
        LogReceiver lr = new LogReceiver(Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2]);
        try {
            lr.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final class Receiver implements Runnable {
        Socket socket;
        int connectionId;
        int generation;
        int fileCounter;

        Receiver(Socket s, int i) {
            socket = s;
            connectionId = i;
            fileCounter = 0;
            generation = 0;
        }

        @Override
        public void run() {
            File target = new File(resultDir + "/");
            File f = new File(connectionId + "-" + generation + '-' + System.currentTimeMillis() / 1000);
            FileWriter fileWriter = null;
            try {
                target.mkdir();
                f.createNewFile();
                fileWriter = new FileWriter(f);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                InputStream inputStream = socket.getInputStream();
                Scanner s = new Scanner(inputStream);
                OutputStream outputStream = socket.getOutputStream();
                while (socket.isConnected()) {
                    String l = s.nextLine();
                    fileWriter.write(l + "\n");
                    fileWriter.flush();
                    outputStream.write("ack\n".getBytes());
                    outputStream.flush();
                    fileCounter++;
                    if (fileCounter > threshold) {
                        fileCounter = 0;
                        f.renameTo(new File(resultDir + "/" + connectionId + "-" + generation));
                        generation++;
                        f = new File(connectionId + "-" + generation + '-' + System.currentTimeMillis() / 1000);
                        f.createNewFile();
                        fileWriter.close();
                        fileWriter = new FileWriter(f);
                    }
                    System.out.println("connection " + connectionId + " reporting: " + l);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchElementException e) {
                System.out.printf("[%d] connection %d disconnected", System.currentTimeMillis(), connectionId);
                fileCounter = 0;
                f.renameTo(new File(resultDir + "/" + connectionId + "-" + generation));//commit last line
                generation++;
            }

        }
    }
}
