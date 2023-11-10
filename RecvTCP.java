import java.io.*;   // for Input/OutputStream
import java.net.*;  // for Socket and ServerSocket

public class RecvTCP {

  public static void main(String args[]) throws Exception {

    if (args.length != 1)  // Test for correct # of args
      throw new IllegalArgumentException("Parameter(s): <Port>");

    int port = Integer.parseInt(args[0]);   // Receiving Port

    ServerSocket servSock = new ServerSocket(port);
    RequestDecoder decoder = new RequestDecoderBin();
    Socket clientSock = servSock.accept();

    while (true) {
      try {
        InputStream in = clientSock.getInputStream();
        while (in.available() == 0) {
        }

        // Handle error when not receiving 2 bytes (shouldn't happen when encoding/decoding)
        if (in.available() != 2) {
          System.out.println("Error: Did not receive 2 bytes - " + in.available() + " bytes received.");
          byte[] temp = new byte[in.available()];
          in.read(temp, 0, in.available());
          OutputStream out = clientSock.getOutputStream();
          out.write("****".getBytes("UTF-16"));
          out.flush();
          System.out.println("Response sent: ****");
          continue;
        }

        // Receive request data
        Request request = decoder.decode(clientSock.getInputStream());
        short num = request.num;

        // Display the received number
        System.out.println("Received bytes in hexadecimal: " + String.format("%02X %02X", (num >> 8) & 0xFF, num & 0xFF));
        System.out.println("Received number: " + num);

        System.out.println("Client IP Address: " + clientSock.getInetAddress().getHostAddress());
        System.out.println("Client Port: " + clientSock.getPort());

        // Encode the response as UTF-16
        String numString = request.numString;

        // Encode response with UF-16
        byte[] encodedResponse = numString.getBytes("UTF-16");

        // Display response
        System.out.print("Response bytes in hexadecimal: ");
        for (byte b : encodedResponse) {
          System.out.printf("%02X ", b);
        }
        System.out.println();
        System.out.println("Response sent: " + numString);

        // Send encoded response
        try {
          OutputStream out = clientSock.getOutputStream();
          out.write(encodedResponse);
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
