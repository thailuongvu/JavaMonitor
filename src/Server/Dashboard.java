package Server;

import java.awt.event.ActionEvent;
import java.io.*;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.table.*;


public class Dashboard {
    public static JFrame window;
    public static JList<String> user;
    public static Map<String, String> mapPath = new HashMap<String, String>();
    public static Map<String, Socket> map = new HashMap<String, Socket>();;
    public static String address;
    public static DefaultTableModel jobsModel;
    public static JTable jtable;
    public static String pathDirectory = "D:\\aJavaMonitor\\";

    public JButton disconec, btnList;
    public JTextField message, jtf;
    public JLabel jip;
    public JLabel jport;
    public TableRowSorter<TableModel> sorter ;
    public Dashboard() {

    }

    public Dashboard(int port) {

        if (Serverhandler.ss != null && !Serverhandler.ss.isClosed()) {
            JOptionPane.showMessageDialog(window, "Server is running!");
        } else {
            if (port != 0) {
                try {
                    Serverhandler.flag = true;
                    address = InetAddress.getLocalHost().getHostAddress();
                    new Thread(new Serverhandler(port)).start();
                    pathDirectory = Paths.get(".").normalize().toAbsolutePath().toString();
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(window, "Cannot start server");
                }
            }
        }
        init(port);
    }

    public void init(int port) { // layout do servidor
        
        window = new JFrame("Monitoring Server System");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout(20,15));
        window.setSize(1200, 500);
        window.setResizable(false);
        FlowLayout layout = new FlowLayout(5);
        layout.setHgap(10);
        layout.setVgap(10);
        JPanel head=new JPanel();
        head.setLayout(layout);

        JLabel labelServidor = new JLabel("IP: "+String.valueOf(address));

        labelServidor.setSize(new Dimension(100, 100));
        head.add(labelServidor);

        JLabel label_porta = new JLabel("Port: "+String.valueOf(port));

        label_porta.setSize(new Dimension(100, 100));
        head.add(label_porta);
        

        JPanel content1=new JPanel();
        content1.setLayout(new BorderLayout(5,10));

        JLabel label_text = new JLabel("List client");
        label_text.setSize(new Dimension(100, 50));
        content1.add(label_text,BorderLayout.NORTH);


        btnList = new JButton("List All Notification");
        btnList.setSize(new Dimension(100, 40));
        head.add(btnList);

 

        head.setComponentOrientation(
                ComponentOrientation.LEFT_TO_RIGHT);
        window.add(head,BorderLayout.NORTH);
        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setPreferredSize(new Dimension(100, 40));

        user.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {

                    
                    JList source = (JList) event.getSource();
                    String selected = source.getSelectedValue().toString();

                    if (selected.length() == 0) {
                        sorter.setRowFilter(null);
                    } else {
                        sorter.setRowFilter(RowFilter.regexFilter(selected));
                    }

                    JFileChooser myfileChooser = new JFileChooser();
                    myfileChooser.setDialogTitle("Select folder");
                    myfileChooser.setCurrentDirectory(new java.io.File(mapPath.get(selected)));
                    myfileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int findresult = myfileChooser.showOpenDialog(window);
                    if (findresult == myfileChooser.APPROVE_OPTION) {
                        String pathClient = myfileChooser.getSelectedFile().getAbsolutePath();
                        try {
                            new ServerSend(map.get(selected), pathClient, "13", "Server");
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();

                            Object[] obj = new Object[] { jobsModel.getRowCount() + 1, pathClient,
                                    dateFormat.format(date), "Change paths",
                                    selected,
                                    "Change path monitoring system" };


                            jobsModel.addRow(obj);
                            jtable.setModel(jobsModel);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    System.out.println(selected);
                }
            }
        });

        content1.add(paneUser,BorderLayout.WEST);
        window.add(content1,BorderLayout.WEST);
        message = new JTextField();
        message.setBounds(0, 0, 0, 0);
        window.add(message);

        disconec = new JButton("Disconnect");
        disconec.setBounds(0, 0, 0, 0);
        window.add(disconec);

        jobsModel = new DefaultTableModel(
                new String[] { "STT", "Monitoring directory", "Time", "Action", "Name Client", "Description" }, 0) {
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
        sorter = new TableRowSorter<TableModel>(jobsModel);
        jtable.setRowSorter(sorter);
        jtable.setBounds(145, 110, 1030, 320);

        TableColumnModel columnModel = jtable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(20);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(400);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(jtable);
        sp.setSize( 1000, 300);
        JPanel content2 =new JPanel();
        content2.setLayout(new BorderLayout());
        content2.add(sp);
        window.add(content2,BorderLayout.CENTER);

        btnList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sorter.setRowFilter(null);
            }
        });

        myEvent();
        window.setVisible(true);
    }


    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (Serverhandler.listaClient != null && Serverhandler.listaClient.size() != 0) {
                    try {
                        new ServerSend(Serverhandler.listaClient, "Server die", "5", "Server");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });


        disconec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Serverhandler.ss == null || Serverhandler.ss.isClosed()) {
                    JOptionPane.showMessageDialog(window, "Máy chủ đã đóng!");
                } else {
                    if (Serverhandler.listaClient != null && Serverhandler.listaClient.size() != 0) {
                        try {
                            new ServerSend(Serverhandler.listaClient, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        disconec.setText("Đóng");
                        Serverhandler.ss.close();
                        Serverhandler.ss = null;
                        Serverhandler.listaClient = null;
                        Serverhandler.flag = false; // importante
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
}
