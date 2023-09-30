public class Response {

    private String responseString; // The response string received from the server
    private byte[] responseBytes; // Byte array representation of the response string
    private long roundTripTime; // The round-trip time for the response in milliseconds

    public Response(String responseString, byte[] responseBytes, long roundTripTime) {
        this.responseString = responseString;
        this.responseBytes = responseBytes;
        this.roundTripTime = roundTripTime;
    }

    public String getResponseString() {
        return responseString;
    }

    public byte[] getResponseBytes() {
        return responseBytes.clone(); // Return a copy to maintain immutability
    }

    public long getRoundTripTime() {
        return roundTripTime;
    }

    @Override
    public String toString() {
        final String EOLN = System.getProperty("line.separator");
        String value = "Response String: " + responseString + EOLN +
                "Response Bytes: " + byteArrayToString(responseBytes) + EOLN +
                "Round-Trip Time: " + roundTripTime + " ms" + EOLN;
        return value;
    }

    private String byteArrayToString(byte[] byteArray) {
        StringBuilder builder = new StringBuilder();
        for (byte b : byteArray) {
            builder.append(String.format("%02X ", b)); // Format as hexadecimal
        }
        return builder.toString();
    }
}
