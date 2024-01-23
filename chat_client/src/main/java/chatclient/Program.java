package chatclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите свое имя: ");
            String name = scanner.nextLine();
            InetAddress address = InetAddress.getLocalHost();
            Socket socket = new Socket(address,4500);
            Client client = new Client(socket, name);
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println("InetAddress: "+inetAddress);
            String remoteIp = inetAddress.getHostAddress();
            System.out.println("RemoteIp: "+ remoteIp);
            System.out.println("Port: "+socket.getLocalPort());
            client.listnerFormessage();
            client.sendMessage();

        } catch (UnknownHostException e){
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
