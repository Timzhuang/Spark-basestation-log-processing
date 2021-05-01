import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class LogSender {
    String hostname;
    int port;
    String machineId;

    LogSender(String h, int p, String m) {
        hostname = h;
        port = p;
        machineId = m;
    }

    void start() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while (true) {

            try (Socket socket = new Socket(hostname, port)) {
                socket.setKeepAlive(true);
                socket.setSoTimeout(600);
                OutputStream outputStream = socket.getOutputStream();
                InputStream in = socket.getInputStream();
                Scanner networkInput = new Scanner(in);
                if (!line.isEmpty()) {
                    outputStream.write(line.getBytes());
                    line = "";
                }
                while (socket.isConnected()) {
                    line = logPreProcess(scanner.nextLine());
                    outputStream.write(line.getBytes());
                    networkInput.nextLine();
                    line = "";//line is empty if succefully acked
                }

            } catch (UnknownHostException ex) {

                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {
                System.out.println("I/O error: " + ex.getMessage());
            } catch(NoSuchElementException e){
                System.out.println("I/O error: " + e.getMessage());
            }
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            System.err.println("usage: program host port unique-machine-id");
            return;
        }
        System.out.println(Arrays.toString(args));

        LogSender ls = new LogSender(args[0], Integer.parseInt(args[1]), args[2]);
        ls.start();
    }

    public String logPreProcess(String l) {
	return l+'\n';        
	//return System.currentTimeMillis() + "," + machineId + "," + l + '\n';
    }
}
