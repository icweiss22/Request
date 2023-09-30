import java.net.*;  // for DatagramSocket and DatagramPacket
import java.io.*;   // for IOException

public class RecvUDP {

    public static void main(String[] args) throws Exception {

        if (args.length != 1 && args.length != 2) // Test for correct # of args
            throw new IllegalArgumentException("Parameter(s): <Port> [<encoding>]");

        int port = Integer.parseInt(args[0]); // Receiving Port

        DatagramSocket sock = new DatagramSocket(port); // UDP socket for receiving

        while (true) { // Server continuously listens for incoming requests
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
            sock.receive(packet);

            RequestDecoder decoder = (args.length == 2 ? // Which encoding
                    new RequestDecoderBin(args[1]) :
                    new RequestDecoderBin());

            Request receivedRequest = decoder.decode(packet);

            System.out.println("Received Binary-Encoded Request");
            System.out.println(receivedRequest);

            // Process the received request and generate a response
            short receivedNumber = receivedRequest.getNum();
            String responseString = String.valueOf(receivedNumber); // Convert the number to a string

            // Encode the response as UTF-16
            byte[] encodedResponse = responseString.getBytes("UTF-16BE");

            // Create a response packet with the encoded data
            DatagramPacket responsePacket = new DatagramPacket(encodedResponse, encodedResponse.length,
                    packet.getAddress(), packet.getPort());

            // Send the response back to the client
            sock.send(responsePacket);

            System.out.println("Response sent: " + responseString);
        }
    }
}

