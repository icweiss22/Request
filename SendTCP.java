import java.io.*;   // for Input/OutputStream
import java.net.*;  // for Socket
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SendTCP {

  public static void main(String args[]) throws Exception {

    if (args.length != 2)  // Test for correct # of args
      throw new IllegalArgumentException("Parameter(s): <Destination> <Port>");

    InetAddress destAddr = InetAddress.getByName(args[0]);  // Destination address
    int destPort = Integer.parseInt(args[1]);               // Destination port

    Socket sock = new Socket(destAddr, destPort); // TCP socket for sending

    // UTF-16 encoding scheme
    RequestEncoder encoder = new RequestEncoderBin();
    Scanner scanner = new Scanner(System.in);

    List<Long> roundTripTimes = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      short num = 0;

      // Prompt user number input
      boolean validInput = false;
      while (!validInput) {
        System.out.print("Enter a number (-32768 to 32767): ");

        if (scanner.hasNextShort()) {
          num = scanner.nextShort();
          validInput = true;
        }
        else {
          System.out.println("Invalid input: " + scanner.next() + " is not a valid input.");
        }
      }

      // Display bytes in hexadecimal
      System.out.println("Bytes in hexadecimal: " + String.format("%02X %02X", (num >> 8) & 0xFF, num & 0xFF));

      // Encode num and send to server
      byte[] encodedMessage = encoder.encode(new Request(num));
      OutputStream out = sock.getOutputStream();
      out.write(encodedMessage);

      // Measure start time
      long startTime = System.currentTimeMillis();

      // Receive server response
      InputStream in = sock.getInputStream();

      // wait for server to respond
      while (in.available() == 0) {
      }
      byte[] receiveData = new byte[in.available()];
      in.read(receiveData, 0, in.available());

      // Display received bytes in hexadecimal
      System.out.print("Received bytes in hexadecimal: ");
      for (byte b : receiveData) {
        System.out.printf("%02X ", b);
      }
      System.out.println();

      // Measure end time
      long endTime = System.currentTimeMillis();
      // Calc round-trip time (end - start)
      long roundTripTime = endTime - startTime;
      // Add round-trip to list
      roundTripTimes.add(roundTripTime);
      String responseString = new String(receiveData, 0, receiveData.length, "UTF-16");

      // Display received string and round-trip time in ms
      System.out.println("Received string: " + responseString);
      System.out.println("Round-trip time: " + roundTripTime + " ms");
    }

    // Display statistics
    long minTime = roundTripTimes.stream().min(Long::compare).orElse(0L);
    long maxTime = roundTripTimes.stream().max(Long::compare).orElse(0L);
    double averageTime = roundTripTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

    System.out.println("Minimum round-trip time: " + minTime + " ms");
    System.out.println("Maximum round-trip time: " + maxTime + " ms");
    System.out.println("Average round-trip time: " + averageTime + " ms");

    // CLose socket
    sock.close();
  }
}
