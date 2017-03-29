package ipro239.iitbeaconproject.bluetooth;

/**
 * Created by shuao23 on 3/28/2017.
 */

public class BeaconScannerHelper {
    public static final byte[] PDU = {
            0x2,0x1,0x6,0x3,
            0x3,(byte)0xAA,(byte)0xFE,0x17,
            0x16,(byte)0xAA,(byte)0xFE, 0x0 };

    public static final byte[] UUID = {
            (byte)0xBB,(byte)0xC7,0x66,(byte)0xB6,
            0x68,0x1D,(byte)0x82,(byte)0x9D,
            0x58,0x0D};

    public static final int INSTANCE_ID_LENGTH = 6;
}
