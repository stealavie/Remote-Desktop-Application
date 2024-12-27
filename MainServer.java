import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer {
    public static void main(String[] args) {
        new MainServer();
    }

    private final static int port = 4444;
    private static List<ClientHandler> clientList = new ArrayList<>();

    public MainServer() {
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server dang chay...");
            while (true) {
                Socket soc = server.accept();
                ClientHandler newClient = new ClientHandler(soc, this);
                clientList.add(newClient);
                System.out.println("Da them 1 client");
            }
        } catch (Exception e) {
        }
    }

    public ClientHandler checkClient(String ID) {
        for (ClientHandler client : clientList) {
            if (!client.getID().equals(ID)) {
                continue;
            }
            return client;
        }
        return null;
    }

    public void disconnect(ClientHandler client) {
        try {
            client.pairClient.dos.writeUTF("disconnect");
            client.changeActive(false);
            client.pairClient.changeActive(false);
            client.resetPair();
            client.pairClient.resetPair();
        } catch (Exception e) {
        }
    }

    public void deleteClient(String ID) {
        for (int i = 0; i < clientList.size(); i++) {
            if (clientList.get(i).getID().equals(ID)) {
                clientList.remove(i);
                break;
            }
        }
    }
}

class ClientHandler extends Thread {
    MainServer server;
    Socket soc;
    private String clientID;
    private String clientName;
    private String clientIP;
    private boolean isActive;
    ClientHandler pairClient;
    DataOutputStream dos;
    DataInputStream dis;

    public ClientHandler(Socket soc, MainServer server) {
        this.soc = soc;
        this.server = server;
        System.out.println("Client Connected");
        try {
            this.clientIP = soc.getInetAddress().getHostAddress();
            dis = new DataInputStream(soc.getInputStream());
            dos = new DataOutputStream(soc.getOutputStream());
            this.pairClient = null;
            this.isActive = false;

            this.clientID = dis.readUTF();
            this.clientName = dis.readUTF();
            
            clientID = clientID.replaceAll("\\s", "");
        } catch (Exception e) {
            // TODO: handle exception
        }
        this.start();
    }

    public void run() {
        System.out.println("Running");
        try {
            while (true) {
                String req = dis.readUTF();
                System.out.println(req);
                // Danh cho nguoi muon ket noi(Client)
                /*
                 * 1. connect: Khi Remote User yeu cau ket noi voi Host
                 * 2. disconnect: Khi Remote User/Host muon ngat ket noi
                 * 3. accept: Khi Host dong y ket noi
                 * 4. refuse_00: Khi Remote User muon ket noi nhung server khong tim thay Host
                 * 5. refuse_01: Khi Remote User nhap sai mat khau
                 * 6. refuse_02: Khi Remote User muon ket noi nhung Host dang ket noi voi Remote
                 * User khac
                 * 7. refuse_03: Khi Host tu choi ket noi
                 * 8. changeName: Khi Remote User/Host muon doi ten
                 * 9. request: Gui xac nhan cho Host
                 */
                switch (req) {
                    case "connect":
                        if (isActive) {
                            dos.writeUTF("refuse_02");
                        } else {
                            String ID = dis.readUTF();
                            ID = ID.replaceAll("\\s", "");
                            String Pass = dis.readUTF();
                            this.pairClient = server.checkClient(ID);

                            if (this.pairClient != null) {
                                if (this.pairClient.isActive()) {
                                    dos.writeUTF("refuse_02");
                                    dos.flush();
                                } else {
                                    this.pairClient.pairClient = this;
                                    this.pairClient.dos.writeUTF("request");
                                    this.pairClient.dos.writeUTF(Pass);
                                    this.pairClient.dos.writeUTF(clientName);
                                    this.pairClient.dos.flush();
                                }
                            } else {
                                dos.writeUTF("refuse_00");
                            }
                        }
                        break;
                    case "disconnect":
                        server.disconnect(this);
                        break;
                    case "accept":
                        this.pairClient.dos.writeUTF(req);
                        this.pairClient.dos.writeUTF(this.getIP());
                        this.pairClient.dos.flush();
                        this.changeActive(true);
                        this.pairClient.changeActive(true);
                        break;
                    case "refuse_03":
                        this.pairClient.dos.writeUTF("refuse_03");
                        this.pairClient.dos.flush();
                        break;
                    case "refuse_01":
                        this.pairClient.dos.writeUTF("refuse_01");
                        this.pairClient.dos.flush();
                        break;
                    case "changeName":
                        String newName = dis.readUTF();
                        this.changeUserName(newName);
                        break;
                    default:
                        System.out.println("Error");
                        break;
                }
            }
        } catch (Exception e) {
            try {
                soc.close();
                dis.close();
                dos.close();
                server.deleteClient(clientID);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String getID() {
        return clientID;
    }

    public String getIP() {
        return clientIP;
    }

    public boolean isActive() {
        return isActive;
    }

    public void changeActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void resetPair() {
        this.pairClient = null;
    }

    public void changeUserName(String newName) {
        this.clientName = newName;
    }
}
