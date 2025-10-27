package com.example.card_payment.utils;


import android.util.Log;

import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedKcvInfo;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdk.utils.PosUtils;


public class DukptAESUtil {
    private static final String TAG = DukptAESUtil.class.getName();


    public class KeyIndexConstants {
        public static final int DUKPT_AES_PIN_KEY_128_INDEX = 2;
        public static final int DUKPT_AES_PIN_KEY_192_INDEX = 3;
        public static final int DUKPT_AES_PIN_KEY_256_INDEX = 4;
    }

    public class KeyConstants {
        public static final String DUKPT_AES_PIN_KEY_128  = "1273671EA26AC29AFA4D1084127652A1"; //
        public static final String DUKPT_AES_PIN_KEY_128_KSN   = "123456789012345600000001";

        // 5B6DEE2B5B7FABFFA32591F35BF8F23DD9329AE85131E584
        public static final String DUKPT_AES_PIN_KEY_192  = "5B6DEE2B5B7FABFFA32591F35BF8F23DD9329AE85131E584"; //
        public static final String DUKPT_AES_PIN_KEY_192_KSN   = "123456789012345600000001";

        public static final String DUKPT_AES_PIN_KEY_256  = "CE9CE0C101D1138F97FB6CAD4DF045A7083D4EAE2D35A31789D01CCF0949550F"; //
        public static final String DUKPT_AES_PIN_KEY_256_KSN   = "123456789012345600000001";
    }


    public static byte[] getCheckValue(byte[] checkValue) {
        if (checkValue == null) {
            return new byte[5];
        }
        byte[] value = new byte[checkValue.length + 1];
        value[0] = (byte) checkValue.length;
        System.arraycopy(checkValue, 0, value, 1, checkValue.length);
        return value;
    }


    public static int writeKey(int srcKeyIndex,
                               int destKeyIndex,
                               String destKeyStr,
                               String destKsnStr,
                               byte[] destCheckValue) {
        PedKcvInfo kcvInfo = new PedKcvInfo();

        kcvInfo.checkMode = destCheckValue == null ? 0 : 1;
        kcvInfo.checkBuf = getCheckValue(destCheckValue);
        POIHsmManage poiHsmManage = POIHsmManage.getDefault();

        byte[] destKeyValue = HexUtil.parseHex(destKeyStr);
        byte[] destKsnValue = HexUtil.parseHex(destKsnStr);

        return poiHsmManage.PedWriteTIKAES(
                destKeyIndex,
                srcKeyIndex,
                destKeyValue.length,
                destKeyValue,
                destKsnValue,
                kcvInfo);
    }

    public static int writeKey(int srcKeyIndex,
                        int destKeyIndex,
                        byte[] destKeyValue,
                        byte[] destKsnValue,
                        byte[] destCheckValue) {
        PedKcvInfo kcvInfo = new PedKcvInfo();

        kcvInfo.checkMode = destCheckValue == null ? 0 : 1;
        kcvInfo.checkBuf = getCheckValue(destCheckValue);
        POIHsmManage poiHsmManage = POIHsmManage.getDefault();
        return poiHsmManage.PedWriteTIKAES(
                destKeyIndex,
                srcKeyIndex,
                destKeyValue.length,
                destKeyValue,
                destKsnValue,
                kcvInfo);
    }


    public static int dataEncDec(int keyIndex,
                          int keyType,
                          byte[] iv,
                          int mode,
                          byte[] dataIn,
                          byte[] aesIv,
                          byte[] ksnOut,
                          byte[] dataOut) {
        PosByteArray rspBuffer = new PosByteArray();
        PosByteArray ksnBuffer = new PosByteArray();
        POIHsmManage poiHsmManage = POIHsmManage.getDefault();
        int result = poiHsmManage.PedDukptAes(
                keyIndex,
                keyType,
                iv,
                mode ,
                dataIn,
                aesIv,
                ksnBuffer,
                rspBuffer);
        if (result == 0 &&
                ksnOut.length >= ksnBuffer.len &&
                dataOut.length >= rspBuffer.len) {
            System.arraycopy(ksnBuffer.buffer,
                    0,
                    ksnOut,
                    0,
                    ksnBuffer.len);
            System.arraycopy(rspBuffer.buffer,
                    0,
                    dataOut,
                    0,
                    rspBuffer.len);
        }
        return result;
    }


    public static int calcMac(int keyIndex,
                              int mode,
                              int algType,
                              byte[] dataIn,
                              byte[] ksnOut,
                              byte[] dataOut) {
        PosByteArray rspBuffer = new PosByteArray();
        PosByteArray ksnBuffer = new PosByteArray();
        POIHsmManage poiHsmManage = POIHsmManage.getDefault();
        int result = poiHsmManage.PedGetMacDukptAes(
                keyIndex,
                mode,
                algType,
                dataIn,
                rspBuffer,
                ksnBuffer);
        if (result == 0 &&
                ksnOut.length >= ksnBuffer.len &&
                dataOut.length >= rspBuffer.len) {
            System.arraycopy(ksnBuffer.buffer, 0, ksnOut, 0, ksnBuffer.len);
            System.arraycopy(rspBuffer.buffer, 0, dataOut, 0, rspBuffer.len);
        }
        return result;
    }

    private static final int PED_NO_PROTECT_KEY = 0;

    public static int injectDUKPTAES256() {
        int result = -1;

        result = writeKey(PED_NO_PROTECT_KEY,
                KeyIndexConstants.DUKPT_AES_PIN_KEY_256_INDEX,
                KeyConstants.DUKPT_AES_PIN_KEY_256,
                KeyConstants.DUKPT_AES_PIN_KEY_256_KSN,
                null
        );
        Log.d(TAG, "writeKey: " + result);

        if(result == 0){
//        01H - TLK
//        02H - TMK
//        03H - TPK
//        04H - TAK
//        05H - TDK
//        06H - TEK
//        07H - TIK
            int keyType = 0x07;
            int keyIdx = KeyIndexConstants.DUKPT_AES_PIN_KEY_256_INDEX;
            PedKcvInfo kcvInfo = new PedKcvInfo();
            PosByteArray rspBuf = new PosByteArray();
            POIHsmManage poiHsmManage = POIHsmManage.getDefault();
            int rst = poiHsmManage.PedGetKcv(
                    keyType,
                    keyIdx,
                    kcvInfo,
                    rspBuf
            );

            Log.d(TAG, "writeKey rst: " + rst);
            Log.d(TAG, "writeKey keyType: " + keyType);
            Log.d(TAG, "writeKey keyIdx: " + keyIdx);
            if((kcvInfo.checkBuf!=null) && (kcvInfo.checkBuf.length!=0)) {
                Log.d(TAG, "writeKey kcvInfo kcv value: " + HexUtil.toHexString(kcvInfo.checkBuf));
                Log.d(TAG, "writeKey kcvInfo kcv mode: " + kcvInfo.checkMode);
            }
            if(rspBuf.buffer!=null) {
                Log.d(TAG, "writeKey rspBuf: " + HexUtil.toHexString(rspBuf.buffer));
            }
        }


        boolean testDUKPTAES = true;
        if(testDUKPTAES){

            String crytoStr = "900D314BF59C1E4A25BFD725E12E547F52EEFCFF5C4848591FF8ADB050ADF220E4745D3566503ADFA2A0ECC7D597F6B73D079928E27EFE1C1C59AC4F0A99C9D5";
//            String crytoStr = "8078019584DCF529A23AFD90ABADE14C324E38F65E3FC3B5AEF606C7D1F2390DD35D69DB52C6DB38ED5A525FDB15BAAFE3A233379713F3CCFD05FD3AACC4A1AEE24F1BE1A7F06DACE07530854D11339D487FB07FB2904E45170AC1BA22CDA5EAE307B9F43D7741B6B87E6AFF979725E514019E86DC56540AEBBF2D64";
//            byte[] cryptoSrcData = PosUtils.hexStringToBytes(crytoStr);
//            int i =cryptoSrcData.length;
            PosByteArray rspBuf = new PosByteArray();
            PosByteArray macResult = new PosByteArray();
            byte[] initVector = new byte[8];
            PosByteArray rspKsn = new PosByteArray();


//            This Code is Coded by Two Part: X | Y. E.g 0x02 | 0x40.
//                    X (Key Usage) can be :
//            0x00 : Use Data Encrypt Key.
//            0x01 : Use Data Decrypt Key.
//            0x02 : Use Both Ways Key.
//
//            Y (Algorithm Type) can be :
//            0x00 : 2TDEA.
//            0x10 : 3TDEA.
//            0x20 : AES 128.
//            0x30 : AES 192.
//            0x40 : AES 256.
            int X = 0x00;
            int Y = 0x40;
            int keyType = X|Y;

//            initVector - 8-byte Initial Vector, Required for CBC Encryption and Decryption.
            byte[] iv = new byte[8];

//            This Code is Coded by Third Part: W | X | Y | Z. E.g 0x01 | 0x02 | 0x04 | 0x08.
//                    W (Operation Direction) can be :
//            0x00 : Decrypt.
//            0x01 : Encrypt.
//
//                    X (Operation mode) can be :
//            0x00 : ECB Mode.
//            0x02 : CBC Mode.
//
//            Y (KSN Self Increasing Mode) can be :
//            0x00 : Not Self Increasing.
//            0x04 : Self Increasing.
//
//            Z (Encryption Mode) can be :
//            0x00 : TDES Mode.
//            0x08 : AES Mode.

            int W = 0x00;
            int x = 0x00;
            int y = 0x00;
            int z = 0x08;
            int mode = W|x|y|z;

            byte[] dataIn = PosUtils.hexStringToBytes(crytoStr);
            byte[] aesIv = new byte[32];
            byte[] ksnOut = new byte[12];
            byte[] dataOut = new byte[64];

            result = dataEncDec(KeyIndexConstants.DUKPT_AES_PIN_KEY_256_INDEX,
                    keyType,
                    iv,
                    mode,
                    dataIn,
                    aesIv,
                    ksnOut,
                    dataOut);
            Log.d(TAG, "dataEncDec: " + result);
            Log.d(TAG, "PedDukptAes: " + PosUtils.bytesToHexString(dataOut));
            Log.d(TAG, "PedDukptAes ksn: " + PosUtils.bytesToHexString(ksnOut));
            //expected:D3E9745DD5DE8494570F31DFF54B9DB7

//            mode - .
//            This Code is Coded by Two Part: X | Y. E.g 0x00 | 0x40.
//                    X (Key Usage) can be :
//            0x00 : Message Authentication Generation.
//            0x01 : Message Authentication Verification.
//            0x02 : Message Authentication Both Ways.
//
//            Y (Derive Key Algorithm Type) can be :
//            0x00 : 2TDEA.
//            0x10 : 3TDEA.
//            0x20 : AES 128.
//            0x30 : AES 192.
//            0x40 : AES 256.
//

            X = 0;
            Y = 0x40;
            mode = X|Y;

//            operationMode - MAC Operation Control Code.
//                    This Code is Coded by Two Part: X | Y | Z. E.g 0x00 | 0x04 | 0x80.
//                    X (Algorithm Type) can be :
//            0x00 : CBC-MAC.
//            0x01 : XOR-ECB-MAC.
//            0x02 : ANSI-X9.19 MAC.
//
//                    Y (KSN Self Increasing Mode) can be :
//            0x00 : Not Self Increasing.
//            0x40 : Self Increasing.
//
//            Z (Dukpt Mode) can be :
//            0x80 : AES Mode.
//
//            data - Message Input to Calculate MAC.
//            macBuf - MAC Output.
//                    ksnBuf - KSN Output.
            X = 0x00;
            Y = 0x00;
            int Z = 0x80;
            int operationMode = X|Y|Z;
            ksnOut = new byte[12];
            dataOut = new byte[64];

            macResult = new PosByteArray();
            rspKsn = new PosByteArray();
            result = calcMac(KeyIndexConstants.DUKPT_AES_PIN_KEY_256_INDEX,
                    mode,  //
                    operationMode,
                    dataIn,
                    ksnOut,
                    dataOut);
            Log.d(TAG, "PedGetMacDukptAes: " + result);
            Log.d(TAG, "PedGetMacDukptAes: " + PosUtils.bytesToHexString(dataOut));
            Log.d(TAG, "PedGetMacDukpt ksn: " + PosUtils.bytesToHexString(ksnOut));


        }
        return 0;
    }
}
