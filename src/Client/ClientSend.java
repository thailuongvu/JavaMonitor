package Client;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSend {
    ClientSend(Socket s, Object message, String info, String name, String path) throws IOException {
        String messages = info + ",," + message + ",," + name + ",," + path;
        PrintWriter pwOut = new PrintWriter(s.getOutputStream(), true);
        pwOut.println(messages);
    }
}
