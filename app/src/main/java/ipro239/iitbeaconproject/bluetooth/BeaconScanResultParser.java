package ipro239.iitbeaconproject.bluetooth;

import android.util.Log;

/**
 * Created by shuao23 on 3/25/2017.
 */

public class BeaconScanResultParser {

    public BeaconScanResult parse(byte[] scanRecord, byte[] pdu, byte[] uuid){
        for(int i = 0; i < pdu.length; i++){
            if(pdu[i] != scanRecord[i])
                return null;
        }

        for (int i = 0; i < uuid.length; i++){
            if(uuid[i] != scanRecord[i + pdu.length + 1])
                return null;
        }

        BeaconScanResult result = new BeaconScanResult();
        StringBuilder sb = new StringBuilder();
        result.setTxPower(scanRecord[pdu.length]);
        for(int i = 0; i < 6; i++){
            int indexOff = pdu.length + uuid.length + 1;
            sb.append(String.format("%02x", scanRecord[indexOff + i]));
        }
        result.setInstanceID(sb.toString().toUpperCase());
        return result;
    }
}
