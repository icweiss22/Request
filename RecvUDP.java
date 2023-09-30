import java.net.*;
import java.io.*;

public class RecvUDP {

    public static void main(String[] args) throws Exception {

        if (args.length != 1 && args.length != 2) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Port> [<encoding>]");

        int port = Integer.parseInt(args[0]); // Receiving Port

        DatagramSocket sock = new DatagramSocket(port); // UDP socket for receiving

        while (true) { // Server continuously listens for incoming requests
            DatagramPacket packet = new DatagramPacket(new byte[2], 2);
            sock.receive(packet);

            // Display received bytes in hexadecimal format
            System.out.print("Received bytes in hexadecimal: ");
            byte[] receivedBytes = packet.getData();
            for (byte b : receivedBytes) {
                System.out.printf("%02X ", b);
            }
            System.out.println();

            // Extract the received number from the received bytes
            short receivedNumber = (short) ((receivedBytes[0] << 8) | (receivedBytes[1] & 0xFF));

            // Display the received number
            System.out.println("Received number: " + receivedNumber);

            // Display the IP address and port of the client
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            System.out.println("Client IP Address: " + clientAddress.getHostAddress());
            System.out.println("Client Port: " + clientPort);

            // Process the received request and generate a response
            String responseString;

            if (packet.getLength() != 2) {
                // If received bytes are not equal to 2, return the string "****"
                responseString = "****";
            } else {
                // Convert the received number to a string
                responseString = String.valueOf(receivedNumber);
            }

            // Encode the response as UTF-16
            byte[] encodedResponse = responseString.getBytes("UTF-16");

            // Display bytes of the encoded response string in hexadecimal format
            System.out.print("Response bytes in hexadecimal: ");
            for (byte b : encodedResponse) {
                System.out.printf("%02X ", b);
            }
            System.out.println();

            // Create a response packet with the encoded data
            DatagramPacket responsePacket = new DatagramPacket(encodedResponse, encodedResponse.length,
                    clientAddress, clientPort);

            // Send the response back to the client
            sock.send(responsePacket);

            System.out.println("Response sent: " + responseString);
        }
    }
}
