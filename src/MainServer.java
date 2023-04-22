
import Client.*;
import Server.*;

import javax.swing.*;

public class MainServer {
    public static JFrame window;
    public static void main(String[] args) {
        new MainServer();
    }

    public MainServer() {
        init();
    }

    public void init() {

        Dashboard win = new Dashboard(8888);
        win.window.setVisible(true);

    }


}

