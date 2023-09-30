public class Request {

    protected final short num; // Number that will be converted to a string
    protected final byte[] numBytes; // Byte array of number
    protected final String numString; // String of the number
    protected final byte[] stringBytes; // Byte array of the string

    public Request(short num) {
        this.num = num;
        this.numBytes = new byte[]{(byte) (num & 0xff), (byte) ((num >> 8) & 0xff)};
        this.numString = String.valueOf(num);
        try {
            this.stringBytes = this.numString.getBytes("UTF-16");
        } catch (Exception e) {
            throw new RuntimeException("Error encoding stringBytes.", e);
        }
    }

    public short getNum() {
        return num;
    }

    public byte[] getNumBytes() {
        return numBytes.clone(); // Return a copy to maintain immutability
    }

    public String getNumString() {
        return numString;
    }

    public byte[] getStringBytes() {
        return stringBytes.clone(); // Return a copy to maintain immutability
    }

    @Override
    public String toString() {
        final String EOLN = System.getProperty("line.separator");
        String value = "Number = " + num + EOLN +
                "Number Bytes = " + byteArrayToString(numBytes) + EOLN +
                "String = " + numString + EOLN +
                "String Bytes = " + byteArrayToString(stringBytes) + EOLN;
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
