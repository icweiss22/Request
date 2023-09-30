public class Request {

    public short num; // Number that will be converted to a string
    public byte[] numBytes; // Byte array of number
    public String numString; // String of the number
    public byte[] stringBytes; // Byte array of the string

  public Request(short num)  {
      try {
          this.num = num;
          byte[] arr = new byte[2];
          arr[0] = (byte)(num & 0xff);
          arr[1] = (byte)((num >> 8) & 0xff);
          this.numBytes = arr;
          this.numString = String.valueOf(num);
          this.stringBytes = this.numString.getBytes("UTF-16");
      } catch (Exception e) {
          e.printStackTrace();
      }
  }

  public String toString() {
    final String EOLN = java.lang.System.getProperty("line.separator");
    String value = "Number = " + num + EOLN +
                   "Number Bytes = " + numBytes + EOLN +
                   "String = " + numString + EOLN +
                   "String Bytes = " + stringBytes + EOLN;
    return value;
  }
}
