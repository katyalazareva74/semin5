package chatserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable {
    private Socket socket;
    private String name;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    public static ArrayList<ClientManager> clients = new ArrayList<>();

    public ClientManager(Socket socket) {
        try {
            this.socket = socket;
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " подключился к чату.");
            addClient();
            broadcastMessage("Server: " + name + " подключился к чату.");
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    private void addClient() {
        try {
            this.bufferedWriter.write("Чтобы написать личное сообщение какому-то участнику чата,\nнужно написать имя участника чата, поставить пробел и написать сообщение.");
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
        }catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClient();
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient() {
        clients.remove(this);
        System.out.println(name + " покинул чат.");
        broadcastMessage("Server: " + name + " покинул чат.");
    }

    private void broadcastMessage(String message) {
        for (ClientManager client : clients) {
            try {
                if (!client.name.equals(name) && message != null) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }


    @Override
    public void run() {
        String messageFromclient;
        while (socket.isConnected()) {
            try {
                messageFromclient = bufferedReader.readLine();
                if (messageFromclient == null) {
                    // для macOS  и linux
                    closeEverything(socket, bufferedWriter, bufferedReader);
                    break;
                }
                if (!privateMessage(messageFromclient)) {
                    broadcastMessage(messageFromclient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    private boolean privateMessage(String messageFC) {
        String[] messages = messageFC.split(" ");
        String message = messages[0] + " ";
        for (int i = 2; i < messages.length; i++) {
            message += messages[i] + " ";
        }
        try {
            for (ClientManager client : clients) {
                if (client.name.equals(messages[1])) {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                    return true;
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
        return false;
    }
}
