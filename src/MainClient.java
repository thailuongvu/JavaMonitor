import Client.*;
import Server.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainClient {
    public static JFrame window;
    public static JTextField jtextname;
    public static JButton jbutton;

    public static void main(String[] args) {
        new MainClient();
    }

    public MainClient() {
        init();
    }

    public void init() {
        window = new JFrame("Client");
        window.setLayout(new BorderLayout(30,30));
        window.setSize( 350, 300);
        window.setResizable(false);

        JLabel label = new JLabel("Client");
        label.setSize( 80, 30);
        label.setFont(new Font("Serif", Font.PLAIN, 30));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        window.add(label,BorderLayout.NORTH);

        JPanel content=new JPanel();
        content.setLayout(new FlowLayout(5,10,5));
        JLabel labelname = new JLabel("Name:");
        
        labelname.setFont(new Font("Serif", Font.PLAIN, 24));
        content.add(labelname);
        jtextname = new JTextField(20);
        jtextname.setPreferredSize(new Dimension(20, 28));
        
        content.add(jtextname);
        window.add(content,BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER,10,50));

        jbutton = new JButton("Connect");
        jbutton.setPreferredSize(new Dimension( 100, 30));
        jbutton.setHorizontalAlignment(JButton.CENTER);
        jbutton.setVerticalAlignment(JButton.CENTER);
        footer.add(jbutton);
        window.add(footer,BorderLayout.SOUTH);
        myEvent();
        window.setVisible(true);
    }

    public void myEvent() {
        jbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                String ip = "127.0.0.1";
                String name = jtextname.getText();

                if ( name.length() != 0) {
                    ClientHandler win = new ClientHandler(ip, 8888, name);
                    win.window.setVisible(true);
                    window.setVisible(false);
                    window.dispose();
                } else {
                    JOptionPane.showMessageDialog(window, "Invalid information!!");
                }
            }
        });

      
        
    }
}
