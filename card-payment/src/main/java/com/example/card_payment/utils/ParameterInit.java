package com.example.card_payment.utils;

import android.util.Log;

import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.PosEmvAid;
import com.pos.sdk.emvcore.PosEmvCapk;
import com.pos.sdk.emvcore.PosEmvExceptionFile;
import com.pos.sdk.emvcore.PosEmvRevocationIPK;
import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedKcvInfo;
import com.pos.sdk.security.PedKeyInfo;
import com.pos.sdk.utils.PosByteArray;
import com.pos.sdk.utils.PosUtils;
import com.example.card_payment.emv.AIDUtils;
import com.example.card_payment.emv.PaywaveKernel;
import com.example.card_payment.emvconfig.CAPKUtils;
import com.example.card_payment.emvconfig.CLSettingUtils;
import com.example.card_payment.emvconfig.EMVConfig;

import java.util.ArrayList;
import java.util.List;

public class ParameterInit {
    private static final String TAG = "paraInit";
    private static final PedKcvInfo pedKcvInfo =  new PedKcvInfo(0, new byte[5]);


    public class KeyIndexConstants {
        public static final int MASTER_KEY_INDEX = 2;
        public static final int DUKPT_PIN_KEY_INDEX = 2;
        public static final int SESSION_DATA_KEY_INDEX = 10;
        public static final int SESSION_PIN_KEY_INDEX = 11;
        public static final int SESSION_MAC_KEY_INDEX = 12;

        public static final int MASTER_KEY_AES_INDEX = 5;
        public static final int SESSION_DATA_KEY_AES_INDEX = 6;
        public static final int SESSION_PIN_KEY_AES_INDEX = 7;
        public static final int SESSION_MAC_KEY_AES_INDEX = 8;

    }
    public class KeyConstants {
        public static final String TMK_3DES_DATA  = "C1D0F8FB4958670DBA40AB1F3752EF0D";

        public static final String TDK_3DES_DATA  = "952FB7A4E173E9735E899C60029B181C";  // plainText: C1D0F8FB4958670DBA40AB1F3752EF0D

        public static final String TPK_3DES_DATA  = "952FB7A4E173E9735E899C60029B181C";  // plainText: C1D0F8FB4958670DBA40AB1F3752EF0D

        public static final String TAK_3DES_DATA  = "952FB7A4E173E9735E899C60029B181C";  // plainText: C1D0F8FB4958670DBA40AB1F3752EF0D
        public static final String DUKPT_3DES_IPEK  = "EC77946D7BEFAD60DFDC6D5028AF1BEF"; //
        //1FF229BF144379CA47C2EC11216725EF
        //    public static String DUKPT_KSN   = "62994901190000000001";
        public static final String DUKPT_3DES_KSN   = "FFFFFF00000000000001";


        public static final String TMK_AES_DATA  = "41473E32EA160D224F8198CEBCC0855A";


        // TDK need to encrypted by TMK
        // plainText: C1D0F8FB4958670DBA40AB1F3752EF0D
        public static final String TDK_AES_DATA  = "D7FED63BAF7686D9E725CA5CA2F9A1EE";  // plainText: 24F131957314EF7F2CD8604019CE39A8

        // TPK need to encrypted by TMK
        // plainText: C1D0F8FB4958670DBA40AB1F3752EF0D
        public static final String TPK_AES_DATA  = "9863DB85584C6520301E0705D3EF5C6A";

        // TAK need to encrypted by TMK
        // plainText: C1D0F8FB4958670DBA40AB1F3752EF0D
        public static final String TAK_AES_DATA  = "9863DB85584C6520301E0705D3EF5C6A";

    }

    enum symAlgorithm{
        KEY_ALG_3DES(0),
        KEY_ALG_AES(0x10);
        private int value;
        symAlgorithm(int i) {
            value =i;
        }
    }

    enum DUKPT_PEK_USAGE{
        //        0x00 - Use Request or both ways MAC key
//        0x01 - Use Request or both ways Data key
//        0x02 - Use DATA Response key
//        0x03 - Use PIN Encryption key
        REQUEST_MAC(0x00),
        REQUEST_DATA(0x01),
        RESPONSE_MAC(0x02),
        PIN_ENCYPTION(0x03);

        private int value;
        DUKPT_PEK_USAGE(int value) {
            this.value = value;
        }
    };


    private static final int PED_NO_PROTECT_KEY = 0;
    private static final int PED_NO_PROTECT_KEY_INDEX = 0;

    private static int updateKeyMKSK(int protectKeyType,
                                         int protectKeyIndex,
                                         String Keydata,
                                         int keyType,
                                         int keyIndex) {
        byte[] keyData = PosUtils.hexStringToBytes(Keydata);
        PedKeyInfo mkInfo = new PedKeyInfo(protectKeyType,
                protectKeyIndex,
                keyType,
                keyIndex,
                symAlgorithm.KEY_ALG_3DES.value,
                keyData.length,
                keyData);
        int rt = POIHsmManage.getDefault().PedWriteKey(mkInfo,
                pedKcvInfo);
        return rt;
    }

    private static int updateKeyMKSKAES(int protectKeyType,
                                        int protectKeyIndex,
                                        String Keydata,
                                        int keyType,
                                        int keyIndex) {
        byte[] keyData = PosUtils.hexStringToBytes(Keydata);
        PedKeyInfo mkInfo = new PedKeyInfo(protectKeyType,
                protectKeyIndex,
                keyType,
                keyIndex,
                symAlgorithm.KEY_ALG_AES.value,
                keyData.length,
                keyData);
        int rt = POIHsmManage.getDefault().PedWriteKey(mkInfo,
                pedKcvInfo);
        return rt;
    }
    private static int injectMKSK() {
        int result = -1;
        Log.d(TAG, "injectMKSK: =============0");
        // TODO: 2023/10/9 1.inject plainText TMK
        result = updateKeyMKSK(
                PED_NO_PROTECT_KEY,
                PED_NO_PROTECT_KEY_INDEX,
                KeyConstants.TMK_3DES_DATA,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.MASTER_KEY_INDEX
        );
        Log.d(TAG, "injectMKSK: =============1");
        if(result!=0){
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TMK failed");
            return result;
        }
        // TODO: 2023/10/9 2.inject TDK
        result = updateKeyMKSK(
                POIHsmManage.PED_TMK,
                KeyIndexConstants.MASTER_KEY_INDEX,
                KeyConstants.TDK_3DES_DATA,
                POIHsmManage.PED_TDK,
                KeyIndexConstants.SESSION_DATA_KEY_INDEX
        );
        Log.d(TAG, "injectMKSK: =============2");
        if(result!=0){
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TDK failed");
            return result;
        }

        // TODO: 2023/10/9 3.inject TPK
        result = updateKeyMKSK(
                POIHsmManage.PED_TMK,
                KeyIndexConstants.MASTER_KEY_INDEX,
                KeyConstants.TPK_3DES_DATA,
                POIHsmManage.PED_TPK,
                KeyIndexConstants.SESSION_PIN_KEY_INDEX
        );
        Log.d(TAG, "injectMKSK: =============3");
        if(result!=0){
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TPK failed");
            return result;
        }


        // TODO: 2023/10/9 4.inject TAK
        result = updateKeyMKSK(
                POIHsmManage.PED_TMK,
                KeyIndexConstants.MASTER_KEY_INDEX,
                KeyConstants.TAK_3DES_DATA,
                POIHsmManage.PED_TAK,
                KeyIndexConstants.SESSION_MAC_KEY_INDEX
        );
        Log.d(TAG, "injectMKSK: =============4");
        if(result!=0){
            Log.d(TAG, "injectMKSK: updateKeyMKSK PED_TAK failed");
            return result;
        }

        boolean testMKSK = false;
        if(testMKSK) {

            //key is:C1D0F8FB4958670DBA40AB1F3752EF0D
            String crytoStr = "B02310D37A8A9D7952C1C1D5F8F73D61";
            byte[] cryptoSrcData = PosUtils.hexStringToBytes(crytoStr);
            PosByteArray rspBuf = new PosByteArray();
            PosByteArray macResult = new PosByteArray();

            result = POIHsmManage.getDefault().PedCalDes(
                    KeyIndexConstants.SESSION_DATA_KEY_INDEX,
                    POIHsmManage.PED_CALC_DES_MODE_ECB_ENC,
                    cryptoSrcData,
                    rspBuf);
            Log.d(TAG, "PedCalDes PED_CALC_DES_MODE_ECB_DEC: " + result);
            Log.d(TAG, "PedCalDes: " + PosUtils.bytesToHexString(rspBuf.buffer));
            //expected:51c24e4553c50666dd7793e362a55a74

//                        0x02: ANSI-X9.19 MAC
//                        0x03: ANSI-X9.9 MAC
            result = POIHsmManage.getDefault().PedGetMac(
                    KeyIndexConstants.SESSION_MAC_KEY_INDEX,
                    0x02,  // ANSI-X9.19 MAC
                    cryptoSrcData,
                    macResult);
            Log.d(TAG, "PedGetMac ANSI-X9.19 MAC: " + result);
            Log.d(TAG, "PedGetMac: " + PosUtils.bytesToHexString(macResult.buffer));
            //expected:B39294C40541D8D1

            result = POIHsmManage.getDefault().PedGetMac(
                    KeyIndexConstants.SESSION_MAC_KEY_INDEX,
                    0x03,  // ANSI-X9.9 MAC
                    cryptoSrcData,
                    macResult);
            Log.d(TAG, "PedGetMac ANSI-X9.9 MAC: " + result);
            Log.d(TAG, "PedGetMac: " + PosUtils.bytesToHexString(macResult.buffer));
            //expected:5A74234CCCD676F1
        }

        boolean useAESMKSK = false;
        if(useAESMKSK) {
            result = updateKeyMKSKAES(
                    PED_NO_PROTECT_KEY,
                    PED_NO_PROTECT_KEY_INDEX,
                    KeyConstants.TMK_AES_DATA,
                    POIHsmManage.PED_TMK,
                    KeyIndexConstants.MASTER_KEY_AES_INDEX
            );
            if (result != 0) {
                Log.d(TAG, "injectMKSKAES: updateKeyMKSK PED_TMK failed");
                return result;
            }
            Log.d(TAG, "injectMKSKAES: success");
            //        keyType
//        01H - TLK
//        02H - TMK
//        03H - TPK
//        04H - TAK
//        05H - TDK
//        06H - TEK
//        07H - TIK
            PosByteArray kcvBuf = new PosByteArray();
            byte[] aucCheckBufIn = new byte[5];
            boolean isDukpt = true;
            PedKcvInfo pedKcvInfo = new PedKcvInfo(0, aucCheckBufIn);
            result=-1;

            if (POIHsmManage.getDefault().PedGetKcv(0x02,
                    KeyIndexConstants.MASTER_KEY_AES_INDEX,
                    pedKcvInfo,
                    kcvBuf) != 0) {
                Log.d(TAG,  "MK-AES Index: " + KeyIndexConstants.MASTER_KEY_AES_INDEX + " No Key");
            } else {
                Log.d(TAG,  "MK-AES Index:" + KeyIndexConstants.MASTER_KEY_AES_INDEX + " have Key"+
                        " KCV ="+ kcvBuf.toString());
            }


            // TODO: 2023/10/9 2.inject TDK
            result = updateKeyMKSKAES(
                    POIHsmManage.PED_TMK,
                    KeyIndexConstants.MASTER_KEY_AES_INDEX,
                    KeyConstants.TDK_AES_DATA,
                    POIHsmManage.PED_TDK,
                    KeyIndexConstants.SESSION_DATA_KEY_AES_INDEX
            );
            if (result != 0) {
                Log.d(TAG, "injectMKSKAES: updateKeyMKSK PED_TDK failed");
                return result;
            }

            // TODO: 2023/10/9 3.inject TPK
            result = updateKeyMKSKAES(
                    POIHsmManage.PED_TMK,
                    KeyIndexConstants.MASTER_KEY_AES_INDEX,
                    KeyConstants.TPK_AES_DATA,
                    POIHsmManage.PED_TPK,
                    KeyIndexConstants.SESSION_PIN_KEY_AES_INDEX
            );
            if (result != 0) {
                Log.d(TAG, "injectMKSKAES: updateKeyMKSK PED_TPK failed");
                return result;
            }


            // TODO: 2023/10/9 4.inject TAK
            result = updateKeyMKSKAES(
                    POIHsmManage.PED_TMK,
                    KeyIndexConstants.MASTER_KEY_AES_INDEX,
                    KeyConstants.TAK_AES_DATA,
                    POIHsmManage.PED_TAK,
                    KeyIndexConstants.SESSION_MAC_KEY_AES_INDEX
            );
            if (result != 0) {
                Log.d(TAG, "injectMKSKAES: updateKeyMKSK PED_TAK failed");
                return result;
            }

            boolean testAESMKSK = true;
            if (testAESMKSK) {
                String crytoStr = "B02310D37A8A9D7952C1C1D5F8F73D61C1D0F8FB4958670DBA40AB1F3752EF0D";
                byte[] cryptoSrcData = PosUtils.hexStringToBytes(crytoStr);
                PosByteArray rspBuf = new PosByteArray();
                PosByteArray macResult = new PosByteArray();

                result = POIHsmManage.getDefault().PedCalDes(
                        KeyIndexConstants.SESSION_DATA_KEY_AES_INDEX,
                        POIHsmManage.PED_CALC_DES_MODE_AES_ECB_ENC,
                        cryptoSrcData,
                        rspBuf);
                Log.d(TAG, "PedCalDes PED_CALC_DES_MODE_AES_ECB_ENC: " + result);
                Log.d(TAG, "PedCalDes: " + PosUtils.bytesToHexString(rspBuf.buffer));


//                result = POIHsmManage.getDefault().PedCalDes(
//                        KeyIndexConstants.SESSION_DATA_KEY_AES_INDEX,
//                        POIHsmManage.PED_CALC_DES_MODE_AES_CBC_ENC,
//                        cryptoSrcData,
//                        rspBuf);
//                Log.d(TAG, "PedCalDes PED_CALC_DES_MODE_AES_CBC_ENC: " + result);
//                Log.d(TAG, "PedCalDes: " + PosUtils.bytesToHexString(rspBuf.buffer));

                result = POIHsmManage.getDefault().PedGetMac(
                        KeyIndexConstants.SESSION_MAC_KEY_AES_INDEX,
                        0x00,  // XOR-ECB-MAC
                        cryptoSrcData,
                        macResult);
                Log.d(TAG, "PedGetMac AES CMAC: " + result);
                Log.d(TAG, "PedGetMac: " + PosUtils.bytesToHexString(macResult.buffer));




                result = POIHsmManage.getDefault().PedGetMac(
                        KeyIndexConstants.SESSION_MAC_KEY_AES_INDEX,
                        0x01,  // XOR-ECB-MAC
                        cryptoSrcData,
                        macResult);
                Log.d(TAG, "PedGetMac AES XOR-ECB-MAC: " + result);
                Log.d(TAG, "PedGetMac: " + PosUtils.bytesToHexString(macResult.buffer));

            }
        }
        return 0;
    }

    public static int updateDukptKey(int idx, String dukptKey, String ksn) {
        byte[] keyData = PosUtils.hexStringToBytes(dukptKey);
        byte[] ksnData = PosUtils.hexStringToBytes(ksn);

//        PED_NO_PROTECT_KEY,
//                PED_NO_PROTECT_KEY_INDEX,
        int rt = POIHsmManage.getDefault().PedWriteTIK(idx,
                PED_NO_PROTECT_KEY_INDEX,
                keyData.length,
                keyData,
                ksnData,
                pedKcvInfo);
        return rt;
    }


    private static int injectDUKPT() {
        int result = -1;

        result = updateDukptKey(KeyIndexConstants.DUKPT_PIN_KEY_INDEX,
                KeyConstants.DUKPT_3DES_IPEK,
                KeyConstants.DUKPT_3DES_KSN
        );
        boolean testDUKPT = true;
        if(testDUKPT){

            //IPEK: EC77946D7BEFAD60DFDC6D5028AF1BEF KSN:FFFFFF00000000000001
            String crytoStr = "B02310D37A8A9D7952C1C1D5F8F73D61";
            byte[] cryptoSrcData = PosUtils.hexStringToBytes(crytoStr);
            PosByteArray rspBuf = new PosByteArray();
            PosByteArray macResult = new PosByteArray();
            byte[] initVector = new byte[8];
            PosByteArray rspKsn = new PosByteArray();

            result = POIHsmManage.getDefault().PedDukptDes(KeyIndexConstants.DUKPT_PIN_KEY_INDEX,
                    DUKPT_PEK_USAGE.REQUEST_DATA.value,
                    initVector,
                    POIHsmManage.PED_CALC_DES_MODE_CBC_ENC,
                    cryptoSrcData,
                    rspKsn,
                    rspBuf);
            Log.d(TAG, "PedDukptDes PED_CALC_DES_MODE_CBC_ENC: " + result);
            Log.d(TAG, "PedDukptDes: " + PosUtils.bytesToHexString(rspBuf.buffer));
            Log.d(TAG, "PedDukptDes ksn: " + PosUtils.bytesToHexString(rspKsn.buffer));
            //expected:D3E9745DD5DE8494570F31DFF54B9DB7

            macResult = new PosByteArray();
            rspKsn = new PosByteArray();
            result = POIHsmManage.getDefault().PedGetMacDukpt(KeyIndexConstants.DUKPT_PIN_KEY_INDEX,
                    22,  //
                    cryptoSrcData,
                    macResult,
                    rspKsn);
            Log.d(TAG, "PedGetMacDukpt ANSI-X9.19 MAC: " + result);
            Log.d(TAG, "PedGetMacDukpt: " + PosUtils.bytesToHexString(macResult.buffer));
            Log.d(TAG, "PedGetMacDukpt ksn: " + PosUtils.bytesToHexString(rspKsn.buffer));
            //expected:7AEA4D2BA639B7B3

            macResult = new PosByteArray();
            rspKsn = new PosByteArray();
            result = POIHsmManage.getDefault().PedGetMacDukpt(KeyIndexConstants.DUKPT_PIN_KEY_INDEX,
                    23,  //
                    cryptoSrcData,
                    macResult,
                    rspKsn);
            Log.d(TAG, "PedGetMacDukpt ANSI-X9.9 MAC: " + result);
            Log.d(TAG, "PedGetMacDukpt: " + PosUtils.bytesToHexString(macResult.buffer));
            Log.d(TAG, "PedGetMacDukpt ksn: " + PosUtils.bytesToHexString(rspKsn.buffer));
            //expected:2901D17AA4AA28B2

        }
        return 0;
    }


    static public int initKey(boolean EraseAllKey) {
        int result = -1;
        if(EraseAllKey){
            result = POIHsmManage.getDefault().PedErase();
            if(result!=0){
                Log.d(TAG, "initKey: PedErase failed");
                return result;
            }
        }

        result = injectMKSK();
        if(result!=0){
            Log.d(TAG, "initKey: injectMKSK failed");
            return result;
        }
        Log.d(TAG, "initKey: injectMKSK success");

        result = injectDUKPT();
        if(result!=0){
            Log.d(TAG, "initKey: injectDUKPT failed");
            return result;
        }
        Log.d(TAG, "initKey: injectDUKPT success");


        boolean useTR31 = false;
        if(useTR31) {
            result = TR31KeyUtils.injectKeyInTR31Format();
            if (result != 0) {
                Log.d(TAG, "injectKey TR31: injectKey failed");
                return result;
            }
            Log.d(TAG, "injectKey TR31: injectKey success");
        }
        boolean DUKPTAES256 = false;
        if(DUKPTAES256) {
            result = DukptAESUtil.injectDUKPTAES256();
            if (result != 0) {
                Log.d(TAG, "initKey: injectDUKPT AES failed");
                return result;
            }
            Log.d(TAG, "initKey: injectDUKPT AES success");
        }

        return 0;
    }



    public static int loadAids(List<PosEmvAid> aids) {
        int result = -1;
        POIEmvCoreManager emvCoreManager = POIEmvCoreManager.getDefault();
        emvCoreManager.EmvDeleteAid();
        ArrayList<PosEmvAid> finalList = new ArrayList<>(aids);

        // this is only the certification profile
        for (Object aid : finalList) {
            PosEmvAid kaid = (PosEmvAid) aid;
            // convert everything to partial match
            kaid.SelectIndicator = true;
            if (AIDUtils.isMastercard(kaid) && kaid.DynamicTransLimit <= 0) {
                kaid.DynamicTransLimit = kaid.ContactlessTransLimit;
            }
        }

        for (Object aid : finalList) {
            PosEmvAid kaid = (PosEmvAid) aid;
            Log.d(TAG, "loading aid["+PosUtils.bytesToHexString(kaid.AID)+"]");
            result = POIEmvCoreManager.getDefault().EmvSetAid(kaid);
            if(result!=0){
                Log.d(TAG, "loadAids aid["+PosUtils.bytesToHexString(kaid.AID)+"] failed");
                return result;
            }
            Log.d(TAG, "aid load success");
        }
        Log.d(TAG, "loadAids success");
        return 0;
    }

    public static int loadCAPKs(List<PosEmvCapk> capks) {
        int result = -1;
        POIEmvCoreManager emvCoreManager = POIEmvCoreManager.getDefault();
        emvCoreManager.EmvDeleteCapk();
        for (Object capk : capks) {
            result = emvCoreManager.EmvSetCapk((PosEmvCapk) capk);
            Log.d(TAG, "loading capk["+PosUtils.bytesToHexString(((PosEmvCapk) capk).RID)+"]");
            if(result!=0){
                Log.d(TAG, "loadCAPKs capk["+
                        PosUtils.bytesToHexString(((PosEmvCapk) capk).RID)+"] failed");
                return result;
            }
            Log.d(TAG, "capk load success");
        }
        return 0;
    }

    public static void loadExceptionFile() {
        POIEmvCoreManager emvCoreManager = POIEmvCoreManager.getDefault();
        emvCoreManager.EmvDeleteExceptionFile();
        PosEmvExceptionFile exceptionFile = new PosEmvExceptionFile();
        exceptionFile.PAN = PosUtils.hexStringToBytes("5413339123401596");
        exceptionFile.SerialNo = PosUtils.hexStringToBytes("00");
        emvCoreManager.EmvSetExceptionFile(exceptionFile);
        exceptionFile = new PosEmvExceptionFile();
        exceptionFile.PAN = PosUtils.hexStringToBytes("5413339123401196");
        exceptionFile.SerialNo = PosUtils.hexStringToBytes("01");
        emvCoreManager.EmvSetExceptionFile(exceptionFile);
    }

    public static void loadRevocationIPK() {
        POIEmvCoreManager emvCoreManager = POIEmvCoreManager.getDefault();
        emvCoreManager.EmvDeleteRevocationIPK();
        PosEmvRevocationIPK revocationIPK = new PosEmvRevocationIPK();
        revocationIPK.RID = PosUtils.hexStringToBytes("A000000124");
        revocationIPK.SerialNo = PosUtils.hexStringToBytes("001000");
        revocationIPK.CapkIndex = (byte) 0xF8;
        emvCoreManager.EmvSetRevocationIPK(revocationIPK);
        revocationIPK = new PosEmvRevocationIPK();
        revocationIPK.RID = PosUtils.hexStringToBytes("A000000224");
        revocationIPK.SerialNo = PosUtils.hexStringToBytes("001000");
        revocationIPK.CapkIndex = (byte) 0xF8;
        emvCoreManager.EmvSetRevocationIPK(revocationIPK);
    }

    public static int loadContactlessConfig(){
        int result = -1;

        // paywave(VISA) contactless setting
        result = PaywaveKernel.loadDefault();
        result = CLSettingUtils.loadVisaDRL();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: paywave load failed");
            return result;
        }
        Log.d(TAG, "loadContactlessConfig: paywave load success");

        // Paypass(MasterCard) contactless setting
        result = CLSettingUtils.loadPaypass();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: paypass load failed");
            return result;
        }
        Log.d(TAG, "loadContactlessConfig: paypass load success");

        // Discover contactless setting
        result = CLSettingUtils.loadDiscover();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: discover load failed");
            return result;
        }
        Log.d(TAG, "loadContactlessConfig: discover load success");
        // AMEX contactless setting
        result = CLSettingUtils.loadAmex();
        result = CLSettingUtils.loadAmexDRL();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: AMEX load failed");
            return result;
        }
        Log.d(TAG, "loadContactlessConfig: AMEX load success");
        // UPI contactless setting
        result = CLSettingUtils.loadUnionPay();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: UnionPay load failed");
            return result;
        }

        Log.d(TAG, "loadContactlessConfig: UnionPay load success");
        // AMEX contactless setting
        result = CLSettingUtils.loadMir();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: MIR load failed");
            return result;
        }

        Log.d(TAG, "loadContactlessConfig: MIR load success");
        // JCB contactless setting
        result = CLSettingUtils.loadJCB();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: JCB load failed");
            return result;
        }
        Log.d(TAG, "loadContactlessConfig: JCB load success");
        // Rupay contactless setting
        result = CLSettingUtils.loadRupayService();
        if(result!=0){
            Log.d(TAG, "loadContactlessConfig: Rupay load failed");
            return result;
        }
        Log.d(TAG, "loadContactlessConfig: Rupay load success");
        return 0;
    }



    static public int initEMVConifg(boolean EraseAllAID){
        POIEmvCoreManager emvCoreManager = POIEmvCoreManager.getDefault();
        int result = -1;
        if(EraseAllAID){
            result = emvCoreManager.EmvDeleteAid();
            if(result!=0){
                Log.d(TAG, "initEMVConifg: EmvDeleteAid failed");
                return result;
            }
            Log.d(TAG, "initEMVConifg: EmvDeleteAid success");
            result = emvCoreManager.EmvDeleteCapk();
            if(result!=0){
                Log.d(TAG, "initEMVConifg: EmvDeleteCapk failed");
                return result;
            }
            Log.d(TAG, "initEMVConifg: EmvDeleteCapk success");
        }


        EMVConfig emvConfig = EMVConfig.getDefault();
        List<PosEmvAid>  aids = AIDUtils.generateAids(emvConfig);
        result = loadAids(aids);
        if(result!=0){
            Log.d(TAG, "initEMVConifg: loadAids failed");
            return result;
        }
        Log.d(TAG, "initEMVConifg: loadAids success");

        List<PosEmvCapk>  capks = CAPKUtils.generateCAPKs();
        result = loadCAPKs(capks);
        if(result!=0){
            Log.d(TAG, "initEMVConifg: loadCAPKs failed");
            return result;
        }
        Log.d(TAG, "initEMVConifg: loadCAPKs success");

        loadExceptionFile();
        loadRevocationIPK();

        result = loadContactlessConfig();
        if(result!=0){
            Log.d(TAG, "initEMVConifg: loadContactlessConfig failed");
            return result;
        }
        Log.d(TAG, "initEMVConifg: loadContactlessConfig success");

        return 0;
    }
}
