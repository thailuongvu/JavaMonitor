package Client;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientReceive implements Runnable {
    private Socket socket;
    public DateFormat dateFormat ;
    public Date date ;
    public static int cntC=0;
    public ClientReceive(Socket socket) {
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.date = new Date();
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread.sleep(300);
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];
                String line = strs[1];
                if (info.equals("1")) {
                } else if (info.equals("2") || info.equals("3")) {
                    if (info.equals("2")) {

                        Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                                ClientHandler.pathDirectory,
                                dateFormat.format(date), "Connected",
                                ClientHandler.nameClient,
                                "(Notification) " +ClientHandler.nameClient + " Connected to server!" };

                        String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                                + ClientHandler.pathDirectory + "," +
                                dateFormat.format(date).toString() + "," + "Connected" + "," +
                                ClientHandler.nameClient + "," +
                                "(Notification) " + ClientHandler.nameClient + " connected to server!" + "}";
                        if(cntC==0){
                        ClientHandler.jobsModel.addRow(obj);}
                        cntC++;
                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);

                    } else {

                        ClientHandler.jtable.setModel(ClientHandler.jobsModel);

                    }
                } else if (info.equals("4")) {
                    ClientHandler.connect.setText("Log-in");
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    JOptionPane.showMessageDialog(ClientHandler.window, "Someone used this username!!!");
                    break;
                } else if (info.equals("13")) {
                    ClientHandler.pathDirectory = line + "\\";
                    ClientHandler.labelPath.setText("Path: " + line);

                    Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(date), "Change path",
                            ClientHandler.nameClient,
                            "(Notification) Server send change path" };

                    String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                            + ClientHandler.pathDirectory + "," +
                            dateFormat.format(date).toString() + "," + "Change path" + "," +
                            ClientHandler.nameClient + "," +
                            "(Notification) Server send change path" + "}";

                    ClientHandler.jobsModel.addRow(obj);
                    ClientHandler.jtable.setModel(ClientHandler.jobsModel);

                    WatchFolder.watchService.close();
                    new Thread(new WatchFolder(this.socket)).start();

                    break;
                } else if (info.equals("5")) {

                    Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                            ClientHandler.pathDirectory,
                            dateFormat.format(date), "Server die",
                            ClientHandler.nameClient,
                            "(Notification) Server has  died" };

                    String data = "{" + (ClientHandler.jobsModel.getRowCount() + 1) + ","
                            + ClientHandler.pathDirectory + "," +
                            dateFormat.format(date).toString() + "," + "Server die" + "," +
                            ClientHandler.nameClient + "," +
                            "(Notification) Server has been die" + "}";

                    ClientHandler.jobsModel.addRow(obj);
                    ClientHandler.jtable.setModel(ClientHandler.jobsModel);
                    ClientHandler.connect.setText("Connect");
                    JOptionPane.showMessageDialog(ClientHandler.window, "Server disconnect, please connect againt");
                    WatchFolder.watchService.close();
                    ClientHandler.socket.close();
                    ClientHandler.socket = null;
                    break;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ClientHandler.window, "This user has left!!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
