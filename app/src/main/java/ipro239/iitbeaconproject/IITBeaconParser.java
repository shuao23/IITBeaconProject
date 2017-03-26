package ipro239.iitbeaconproject;

import android.util.Log;

/**
 * Created by shuao23 on 3/25/2017.
 */

public class IITBeaconParser {
    //NEEDS UUID of:
    //0xBBC766B6681D829D580D
    private static final byte[] PDU = {
            0x2,0x1,0x6,0x3,
            0x3,(byte)0xAA,(byte)0xFE,0x17,
            0x16,(byte)0xAA,(byte)0xFE, 0x0 };
    private static final byte[] UUID = {
            (byte)0xBB,(byte)0xC7,0x66,(byte)0xB6,
            0x68,0x1D,(byte)0x82,(byte)0x9D,
            0x58,0x0D};
    private static final int INSTANCE_ID_LENGTH = 6;

    private String instanceID;
    private int txPower;

    public IITBeaconParser(byte[] scanRecord){
        parse(scanRecord);
    }

    public String getInstanceID() {
        return instanceID;
    }

    public static int getInstanceIDLength(){
        return INSTANCE_ID_LENGTH;
    }

    public int getTxPower() {
        return txPower;
    }

    public boolean validBeacon(){
        return instanceID != null;
    }

    private void parse(byte[] scanRecord){
        for(int i = 0; i < PDU.length; i++){
            if(PDU[i] != scanRecord[i])
                return;
        }

        for (int i = 0; i < UUID.length; i++){
            if(UUID[i] != scanRecord[i + PDU.length + 1])
                return;
        }

        StringBuilder sb = new StringBuilder();
        txPower = scanRecord[PDU.length];
        for(int i = 0; i < 6; i++){
            int indexOff = PDU.length + UUID.length + 1;
            sb.append(String.format("%02x", scanRecord[indexOff + i]));
        }
        instanceID = sb.toString().toUpperCase();
    }
}
