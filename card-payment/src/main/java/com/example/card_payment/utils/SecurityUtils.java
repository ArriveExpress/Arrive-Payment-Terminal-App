package com.example.card_payment.utils;

import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedKcvInfo;
import com.pos.sdk.security.PedKeyInfo;

public class SecurityUtils {

    private final POIHsmManage hsm = POIHsmManage.getDefault();

    public int writeKey(int srcKeyType, int srcKeyIndex, int destKeyType, int destKeyIndex, int destKeyAlgType, byte[] destKeyValue, byte[] destCheckValue) {
        PedKeyInfo keyInfo = new PedKeyInfo();
        PedKcvInfo kcvInfo = new PedKcvInfo();

        keyInfo.srcKeyType = srcKeyType;
        keyInfo.srcKeyIdx = srcKeyIndex;
        keyInfo.dstKeyType = destKeyType;
        keyInfo.dstKeyIdx = destKeyIndex;
        keyInfo.dstAlgorithm = destKeyAlgType;
        keyInfo.dstKeyLen = destKeyValue.length;
        keyInfo.dstKeyData = destKeyValue;

        kcvInfo.checkMode = destCheckValue == null ? 0 : 1;
        kcvInfo.checkBuf = getCheckValue(destCheckValue);

        return hsm.PedWriteKey(keyInfo, kcvInfo);
    }

    public byte[] getCheckValue(byte[] checkValue) {
        if (checkValue == null) {
            return new byte[5];
        }
        byte[] value = new byte[checkValue.length + 1];
        value[0] = (byte) checkValue.length;
        System.arraycopy(checkValue, 0, value, 1, checkValue.length);
        return value;
    }

    /**
     * Write TR31 Key Extension Method.
     *
     * @param  keyValue  TR31 Format Key Data.
     * @param  srcKeyType  Source Key Type. <br>
     *      {@link POIHsmManage#PED_TLK} <br>
     *      {@link POIHsmManage#PED_TMK}
     * @param  srcKeyIndex  Source Key Index.
     * @param  writeKeyType  Target Key Type. <br>
     *      {@link POIHsmManage#PED_TMK} <br>
     *      {@link POIHsmManage#PED_TPK} <br>
     *      {@link POIHsmManage#PED_TAK} <br>
     *      {@link POIHsmManage#PED_TDK} <br>
     *      {@link POIHsmManage#PED_TEK} <br>
     *      {@link POIHsmManage#PED_TIK}
     * @param  writeKeyIndex  Target Key Index.
     * @param  writeKeyAlgorithm  Target Key Algorithm. <br>
     *      TDEA : 0x00 <br>
     *      AES  : 0x10
     *
     * @return Operation Result. 0 : Success, Other : Fail.
     */

    public int writeTR31KeyEX(byte[] keyValue, int srcKeyType, int srcKeyIndex, int writeKeyType, int writeKeyIndex, int writeKeyAlgorithm) {
        return POIHsmManage.getDefault().PedWriteTR31KeyEx(keyValue, srcKeyType, srcKeyIndex, writeKeyType, writeKeyIndex, writeKeyAlgorithm);
    }


    public int writeTR31Key(byte[] keyValue, int srcKeyType, int srcKeyIndex, int writeKeyType, int writeKeyIndex, int writeKeyAlgorithm) {
        return POIHsmManage.getDefault().PedWriteTR31Key(keyValue,  srcKeyIndex, writeKeyType, writeKeyIndex, writeKeyAlgorithm);
    }


}
