import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
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
        // List to store round-trip times
        List<Long> roundTripTimes = new ArrayList<>(); // list to store round trip times
        for (int i = 0; i < 5; i++) { // Repeat 5 times for 5 different numbers
            // Prompt the user to enter a number 'num' (using a Scanner)
            System.out.print("Enter a number (-32768 to 32767): ");
            short num = scanner.nextShort();

            // Display bytes of num in hexadecimal format
            byte[] numBytes = new byte[]{(byte) (num >> 8), (byte) num};
            System.out.print("Bytes in hexadecimal: ");
            for (byte b : numBytes) {
                System.out.printf("%02X ", b);
            }
            System.out.println();

            // Encode num and send it to the server
            byte[] codedRequest = encoder.encode(new Request(num));
            DatagramPacket message = new DatagramPacket(codedRequest, codedRequest.length, destAddr, destPort);
            sock.send(message);

            // Measure the start time
            long startTime = System.currentTimeMillis();

            // Receive the response from the server (decode it)
            byte[] receiveData = new byte[12]; // max bytes for the response string
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            sock.receive(receivePacket);

            int dataLength = receivePacket.getLength();
            byte[] validData = new byte[dataLength];
            System.arraycopy(receivePacket.getData(), 0, validData, 0, dataLength); // Copy valid data to array

            // Display the received bytes in hexadecimal format
            System.out.print("Received bytes in hexadecimal: ");
            for (byte b : validData) {
                System.out.printf("%02X ", b);
            }
            System.out.println(); // Print a newline to separate the output

            // Measure the end time
            long endTime = System.currentTimeMillis();

            // Calculate round-trip time (end time - start time)
            long roundTripTime = endTime - startTime;
            // Add round-trip time to the list
            roundTripTimes.add(roundTripTime);
            String responseString = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-16");

            // Display received string and round-trip time in milliseconds
            System.out.println("Received string: " + responseString);
            System.out.println("Round-trip time: " + roundTripTime + " ms");
        }

        // Calculate and display statistics
        long minTime = roundTripTimes.stream().min(Long::compare).orElse(0L);
        long maxTime = roundTripTimes.stream().max(Long::compare).orElse(0L);
        double averageTime = roundTripTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

        System.out.println("Minimum round-trip time: " + minTime + " ms");
        System.out.println("Maximum round-trip time: " + maxTime + " ms");
        System.out.println("Average round-trip time: " + averageTime + " ms");


        // Close the socket
        sock.close();
    }
}