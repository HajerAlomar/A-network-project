package server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IMServer {

    ServerSocket serverSocket;
    public static String listNames = new String("");
    BufferedReader input;
    PrintWriter output;
    public static ArrayList<ServerProgress> allClients = new ArrayList<>();

    public IMServer() throws FileNotFoundException, IOException {
        try {
            serverSocket = new ServerSocket(5000, 1, InetAddress.getByName("10.6.203.125"));
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~\t\t\t Starting IM Chat Server\t\t\t~");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("~\t\t\t The server host IP :" + serverSocket.getInetAddress());
            while (true) {
                System.out.println("Surver is  running now, and waiting for clients ...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("there's a Connected client ");                
                ServerProgress process = new ServerProgress(this,clientSocket);
                allClients.add(process);
                process.start();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void PrintAllClients() {
        String data = "";
        for (ServerProgress item : IMServer.allClients) {
            data += item.username + "#";
        }
        for (ServerProgress items : allClients) {
            items.output.println(data);
            items.output.flush();
        }
        
    }

    public static void main(String[] args) {
        try {
            new IMServer();
        } catch (IOException ex) {
            Logger.getLogger(IMServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

class ServerProgress extends Thread {
    IMServer server;
    Socket client;
    String username;
    BufferedReader input;
    PrintWriter output;
    String recievedData;

    ServerProgress(IMServer server,Socket clientSocket) throws IOException {
        this.server=server;
        client = clientSocket;
        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        output = new PrintWriter(client.getOutputStream(), true);
    }
    @Override
    public void run() {

        while (true) {
            try {
                recievedData = input.readLine();
                if (!recievedData.equals(null)) {
                    if (recievedData.contains("end")) {
                        String data = recievedData.replace("end#", "");                      
                        IMServer.allClients.remove(this);
                        server.PrintAllClients();
                        //input.close();
                        //output.close();
                        //client.close();
                    } else if (recievedData.contains("Name:")) {
                        username = recievedData.replace("Name:", "");
                        server.PrintAllClients();
                    }
                }else{
                     PrintAllClients();
                }
                PrintAllClients();

            } catch (IOException ex) {
                Logger.getLogger(ServerProgress.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void PrintAllClients() {
        String data = "";
        for (ServerProgress item : IMServer.allClients) {
            data += item.username + "#";
        }
        output.println(data);
        output.flush();
    }

}