package Client;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.*;


public class ClientHandler {
    public static JFrame window;
    public static JButton connect;
    public static JTable jtable;
    public static Socket socket = null;
    public static String nameClient = "Client";
    public static DefaultTableModel jobsModel;
    public static String pathDirectory = "D:\\aJavaMonitor\\";

    String globalId;
    int globalPort;
    public static JLabel labelPath;
    JTextField jtf;
    JButton msgPrivada, enviar, btnbrowse;

    public ClientHandler(String ip, int port, String name) {

            try {
                
                socket = new Socket(ip, port);
                nameClient = name;
                globalId = ip;
                globalPort = port;
                new ClientSend(socket, name, "2", "Connected", pathDirectory);
                new Thread(new ClientReceive(socket)).start();

            } catch (Exception e2) {
                JOptionPane.showMessageDialog(window, "Can't connect check ip and port");
            }
        
        init(ip, port, name);
        new Thread(new WatchFolder(socket)).start();
    }

    public void init(String ip, int port, String name) {
        window = new JFrame("Client monitoring");
        window.setLayout(null);
        window.setBounds(400, 400, 1200, 480);
        window.setResizable(true);

        JLabel label_ip = new JLabel("Ip:"+ip);
        label_ip.setBounds(10, 28, 70, 30);
        window.add(label_ip);

        JLabel label_porta = new JLabel("port:"+port);
        label_porta.setBounds(180, 28, 90, 30);
        window.add(label_porta);

        JLabel labelname = new JLabel("name: " + name);
        labelname.setBounds(300, 28, 100, 30);
        window.add(labelname);

        connect = new JButton("Disconnect");
        connect.setBounds(400, 28, 150, 30);
        window.add(connect);

        labelPath = new JLabel("Path: " + pathDirectory);
        labelPath.setBounds(600, 28, 600, 30);
        window.add(labelPath);

        btnbrowse = new JButton("Browse");
        btnbrowse.setBounds(1050, 28, 100, 30);
        window.add(btnbrowse);

        jobsModel = new DefaultTableModel(
                new String[] { "STT", "Monitoring directory", "Date&&Time", "Action", "Name Client", "Description" }, 0) {
            public Class getColumnClass(int column) {
                Class returnValue;
                if ((column >= 0) && (column < getColumnCount())) {
                    returnValue = getValueAt(0, column).getClass();
                } else {
                    returnValue = Object.class;
                }
                return returnValue;
            }
        };

        jtable = new JTable();
        jtable.setModel(jobsModel);
        jtable.setAutoCreateRowSorter(true);
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jobsModel);
        jtable.setRowSorter(sorter);
        jtable.setBounds(10, 120, 1160, 300);

        TableColumnModel columnModel = jtable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(400);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(jtable);
        sp.setBounds(10, 120, 1160, 300);
        window.add(sp);

        myEvent();
        window.setVisible(true);
    }


    public void readFile(String path) {
        try {
            Scanner scan = new Scanner(new File(path), "UTF-8");
            while (scan.hasNext()) {
                String data1 = scan.nextLine();
                String data2 = data1.replaceAll("\\{", "");
                String data3 = data2.replaceAll("\\}", "");
                String[] arrOfStr = data3.split(",", -2);
                Object[] obj = new Object[] { ClientHandler.jobsModel.getRowCount() + 1,
                        arrOfStr[1],
                        arrOfStr[2], arrOfStr[3],
                        arrOfStr[4],
                        arrOfStr[5] };

                jobsModel.addRow(obj);
            }
            jtable.setModel(jobsModel);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, nameClient, "3", "Disconnected", pathDirectory);
                        WatchFolder.watchService.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        btnbrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JFileChooser myfileChooser = new JFileChooser();
                myfileChooser.setDialogTitle("Select Folder");

                myfileChooser.setCurrentDirectory(new java.io.File(pathDirectory));
                
                myfileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int findresult = myfileChooser.showOpenDialog(window);
                if (findresult == myfileChooser.APPROVE_OPTION) {
                    pathDirectory = myfileChooser.getSelectedFile().getAbsolutePath();
                    labelPath.setText("Path: " + pathDirectory);
                    try {
                        WatchFolder.watchService.close();
                        new Thread(new WatchFolder(socket)).start();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });


        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    try {
                        globalId = "127.0.0.1";
                        globalPort = 8888;
                        socket = new Socket(globalId, globalPort);
                        connect.setText("Disconnec");

                        new ClientSend(socket, getnomeUsuario(), "2", "Connected", pathDirectory);
                        new Thread(new ClientReceive(socket)).start();
                        new Thread(new WatchFolder(socket)).start();

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();

                        Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathDirectory,
                                dateFormat.format(date), "Connected",
                                nameClient,
                                "(Notification) " + nameClient + " connected to server!" };



                        jobsModel.addRow(obj);
                        jtable.setModel(jobsModel);
                    } catch (Exception e2) {
                        JOptionPane.showMessageDialog(window, "Can't connect check ip and port");
                    }
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getnomeUsuario(), "3", "Disconnected", pathDirectory);
                        connect.setText("Connect");
                        WatchFolder.watchService.close();
                        socket.close();
                        socket = null;


                        jtable.setModel(jobsModel);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    String getnomeUsuario() {
        return nameClient;
    }
}