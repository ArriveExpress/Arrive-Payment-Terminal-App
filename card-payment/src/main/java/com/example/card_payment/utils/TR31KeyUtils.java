package com.example.card_payment.utils;

import android.util.Log;

import com.pos.sdk.security.POIHsmManage;
import com.pos.sdk.security.PedKcvInfo;
import com.pos.sdk.utils.PosByteArray;

public class TR31KeyUtils {
    public static SecurityUtils utils = new SecurityUtils();
    private static final String TAG = "TR31";
    public static int TR31KeyLength = 128;



    public class KeyIndexConstants {
        //        public static final int MASTER_KEY_INDEX = 2;
        public static final int TR31_TMK_INDEX = 1;
        //        public static final int DUKPT_PIN_KEY_INDEX = 2;
        public static final int TR31_TMK1_INDEX = 2;
        public static final int TR31_TMK2_INDEX = 3;

        public static final int TR31_TEK_INDEX = 4;

        public static final int TR31_TPK_INDEX = 5;
    }



    public static int injectKeyInTR31Format(){
        int result = -1;

        byte[] key = HexUtil.parseHex("18DC9CCEA6C5D9FED769456C18ACFAEC");

        int index = 0x01;
        result = utils.writeKey(0x00,
                0x00,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK_INDEX,
                0x10,
                key,
                null);


        String tr31 = "D0112K0AB00E0000166924763BCCC76F9F17252E7DFD0BAEC3E50780BBED65296FA53D903E9D3A41903EA0F4873498A9E6B9C9F5DF9F31E6";
        byte[] keyValue = HexUtil.parseHex(HexUtil.encodeHex(tr31));

        result = utils.writeTR31KeyEX(
                keyValue,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK_INDEX,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK1_INDEX,
                0x10);

        tr31 = "D0112K0AB00E00005C67214A70B071F631A1102ACFCC918657DFBF11F0C5319D1A3D5DF4CDA0A480196327FB9D56A1A79895D022D741FB9F";
        keyValue = HexUtil.parseHex(HexUtil.encodeHex(tr31));
        result = utils.writeTR31KeyEX(keyValue,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK1_INDEX,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK2_INDEX,
                0x10);


        tr31 = "D0112D0AB00E00008EB97F31ACA81A2B8CB69344DF4B9E4E4EF45FA4E09BB8766E5002AF3886E19D998981D9633CF98041167AB5C88F9C79";
        keyValue = HexUtil.parseHex(HexUtil.encodeHex(tr31));
        result = utils.writeTR31KeyEX(keyValue,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK2_INDEX,
                POIHsmManage.PED_TEK,
                KeyIndexConstants.TR31_TEK_INDEX,
                0x10);



        tr31 = "D0112P0AB00E0000483C46E26B15EE90CFB47F330DA7226B23F7FB68EF6524C7FFA951A33B4742B8645E852C08E7B3DC50372FD90CE55171";
        keyValue = HexUtil.parseHex(HexUtil.encodeHex(tr31));
        result = utils.writeTR31KeyEX(keyValue,
                POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK2_INDEX,
                POIHsmManage.PED_TPK,
                KeyIndexConstants.TR31_TPK_INDEX,
                0x10);




        StringBuilder sb = new StringBuilder();
        PosByteArray rspBuf = new PosByteArray();
        PedKcvInfo kcvInfo = new PedKcvInfo();
        if (POIHsmManage.getDefault().PedGetKcv(POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK_INDEX, kcvInfo, rspBuf) == 0) {
            sb.append("TMK:").append(HexUtil.toHexString(rspBuf.buffer, 0, 3)).append("\n");

            Log.d(TAG, "TMK KCV: "+ HexUtil.toHexString(rspBuf.buffer));
        }
        if (POIHsmManage.getDefault().PedGetKcv(POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK1_INDEX, kcvInfo, rspBuf) == 0) {
            sb.append("TMK1:").append(HexUtil.toHexString(rspBuf.buffer, 0, 3)).append("\n");

            Log.d(TAG, "TMK1 KCV: "+ HexUtil.toHexString(rspBuf.buffer));
        }
        if (POIHsmManage.getDefault().PedGetKcv(POIHsmManage.PED_TMK,
                KeyIndexConstants.TR31_TMK2_INDEX, kcvInfo, rspBuf) == 0) {
            sb.append("TMK2:").append(HexUtil.toHexString(rspBuf.buffer, 0, 3)).append("\n");

            Log.d(TAG, "TMK2 KCV: "+ HexUtil.toHexString(rspBuf.buffer));
        }
        if (POIHsmManage.getDefault().PedGetKcv(POIHsmManage.PED_TEK,
                KeyIndexConstants.TR31_TEK_INDEX, kcvInfo, rspBuf) == 0) {
            sb.append("TEK:").append(HexUtil.toHexString(rspBuf.buffer, 0, 3)).append("\n");
            Log.d(TAG, "TEK KCV: "+ HexUtil.toHexString(rspBuf.buffer));

        }
        if (POIHsmManage.getDefault().PedGetKcv(POIHsmManage.PED_TPK,
                KeyIndexConstants.TR31_TPK_INDEX, kcvInfo, rspBuf) == 0) {
            sb.append("TPK:").append(HexUtil.toHexString(rspBuf.buffer, 0, 3)).append("\n");
            Log.d(TAG, "TPK KCV: "+ HexUtil.toHexString(rspBuf.buffer));
        }

        return result;
    }
}
