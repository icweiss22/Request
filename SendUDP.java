import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class SendUDP {

    public static void main(String[] args) throws Exception {
        if (args.length != 2 && args.length != 3) // Test for correct number of parameters
            throw new IllegalArgumentException("Parameter(s): <Destination> <Port> [<encoding]");

        InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
        int destPort = Integer.parseInt(args[1]);               // Destination port

        DatagramSocket sock = new DatagramSocket(); // UDP socket for sending

        // Use the encoding scheme given on the command line (args[2])
        RequestEncoder encoder = (args.length == 3 ?
                new RequestEncoderBin(args[2]) :
                new RequestEncoderBin());

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 5; i++) { // Repeat 5 times for 5 different numbers
            // Prompt the user to enter a number 'num' (using a Scanner)
            System.out.print("Enter a number (-32768 to 32767): ");
            short num = scanner.nextShort();

            // Encode num and send it to the server
            byte[] codedRequest = encoder.encode(new Request(num));
            DatagramPacket message = new DatagramPacket(codedRequest, codedRequest.length, destAddr, destPort);
            sock.send(message);

            // Measure the start time
            long startTime = System.currentTimeMillis();

            // Receive the response from the server (decode it)
            byte[] receiveData = new byte[1024]; // Adjust the size as needed
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            sock.receive(receivePacket);

            // Measure the end time
            long endTime = System.currentTimeMillis();

            // Decode the received data into a Response object
            /*ResponseDecoder decoder = new ResponseDecoderBin();
            Response response = decoder.decode(receivePacket.getData());*/

            // Calculate round-trip time (end time - start time)
            long roundTripTime = endTime - startTime;
            String responseString = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-16");

            // Display received string and round-trip time in milliseconds
            System.out.println("Received string: " + responseString);
            System.out.println("Round-trip time: " + roundTripTime + " ms");
        }

        // Close the socket
        sock.close();
    }
}