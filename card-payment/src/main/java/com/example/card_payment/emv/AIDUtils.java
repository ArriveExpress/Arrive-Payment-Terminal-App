package com.example.card_payment.emv;

import com.example.card_payment.emvconfig.EMVConfig;
import com.pos.sdk.emvcore.PosEmvAid;
import com.pos.sdk.utils.PosUtils;

import java.util.ArrayList;
import java.util.List;

public class AIDUtils {

    public static final String MASTERCARD_AID_ROOT = "A000000004";
    public static final String UPI_AID_ROOT = "A000000333";
    public static PosEmvAid createAID(String aid, String version, EMVConfig emvConfig) {
        PosEmvAid appConifg = new PosEmvAid();
        appConifg.AID = PosUtils.hexStringToBytes(aid);
        appConifg.Version = PosUtils.hexStringToBytes(version);
        appConifg.SelectIndicator = true;
        appConifg.dDOL = PosUtils.hexStringToBytes("9F0206");
        appConifg.tDOL = PosUtils.hexStringToBytes("9F3704");
        appConifg.TACDenial = PosUtils.hexStringToBytes("0010000000");
        appConifg.TACOnline = PosUtils.hexStringToBytes("dc4004f800");
        appConifg.TACDefault = PosUtils.hexStringToBytes("dc4000a800");
        appConifg.Threshold = 10000;
        appConifg.TargetPercentage = 0;
        appConifg.MaxTargetPercentage = 99;
        appConifg.FloorLimit = 0;
        if (emvConfig != null) {
            appConifg.ContactlessTransLimit = Integer.valueOf(emvConfig.contactlessTransactionLimit);
            appConifg.ContactlessCVMLimit = Integer.valueOf(emvConfig.contactlessCvmLimit);
            appConifg.ContactlessFloorLimit = Integer.valueOf(emvConfig.contactlessFloorLimit);
            if (appConifg.ContactlessTransLimit <= appConifg.ContactlessCVMLimit) {
                appConifg.ContactlessTransLimit = 1999999999;
            }
        } else {
            appConifg.ContactlessTransLimit = 9999900;
            appConifg.ContactlessCVMLimit = 15000;
            appConifg.ContactlessFloorLimit = 0;
        }
        appConifg.DynamicTransLimit = 9999900;
        return appConifg;
    }

    public static List<PosEmvAid> generateAids(EMVConfig emvConfig) {
        List<PosEmvAid> aidList = new ArrayList<>();
        PosEmvAid aid;

        // VISA
        aid = createAID("A0000000031010", "008C", emvConfig);
//        aid.ContactlessTransLimit
        aidList.add(aid);

        aid = createAID("A0000000032010", "008C", emvConfig);
        aidList.add(aid);

        aid = createAID("A0000000033010", "008C", emvConfig);
        aidList.add(aid);

        // Unionpay
        aid = createAID("A000000333010101", "0030", emvConfig);
        aidList.add(aid);

        aid = createAID("A000000333010102", "0030", emvConfig);
        aidList.add(aid);

        aid = createAID("A000000333010103", "0030", emvConfig);
        aidList.add(aid);
        // only for Only for USA
        aid = createAID("A000000333010108", "0030", emvConfig);
        aidList.add(aid);


        // MasterCard
        aid = createAID("A00000000410", "0002", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0400000000");
        aid.TACOnline = PosUtils.hexStringToBytes("f850acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50aca000");
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("6C00000000000000");
        aidList.add(aid);

        aid = createAID("A00000000430", "0002", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0400000000");
        aid.TACOnline = PosUtils.hexStringToBytes("f850acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50aca000");
        // aid.ContactlessCVMLimit = 50000;
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("4C00800000000000");
        aidList.add(aid);

        aid = createAID("A0000000043060", "0002", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000800000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc50bcf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50bca000");
        // aid.ContactlessCVMLimit = 50000;
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("4C00800000000000");
        aidList.add(aid);

        aid = createAID("A0000000041010", "0002", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc50808800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc50b8a000");
        aid.TerminalRiskManagementData = PosUtils.hexStringToBytes("6C00000000000000");
        aidList.add(aid);

        // Discover
        aid = createAID("A0000001523010", "0001", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fce09cf800");
        aid.TACDefault = PosUtils.hexStringToBytes("dc00002000");
        aidList.add(aid);

        aid = createAID("A0000001524010", "0001", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fce09cf800");
        aid.TACDefault = PosUtils.hexStringToBytes("dc00002000");
        aidList.add(aid);

        // AMEX
        aid = createAID("A00000002501", "0001", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000000000");
        aid.TACOnline = PosUtils.hexStringToBytes("c800000000");
        aid.TACDefault = PosUtils.hexStringToBytes("c800000000");
        aidList.add(aid);

        aid = createAID("A000000025010402", "0001", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0000000000");
        aid.TACOnline = PosUtils.hexStringToBytes("c800000000");
        aid.TACDefault = PosUtils.hexStringToBytes("c800000000");
        aidList.add(aid);

        // RuPay
        aid = createAID("A0000005241010", "0064", emvConfig);
        aidList.add(aid);

        // MIR
        aid = createAID("A0000006581010", "0100", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aidList.add(aid);

        aid = createAID("A0000006581099", "0100", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aidList.add(aid);

        aid = createAID("A0000006582010", "0100", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aidList.add(aid);

        // JCB
        aid = createAID("A0000000651010", "0021", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aidList.add(aid);

        aid = createAID("A0000003710001", "0001", emvConfig);
        aid.dDOL = PosUtils.hexStringToBytes("9F3704");
        aid.tDOL = PosUtils.hexStringToBytes("9F3704");
        aid.TACDenial = PosUtils.hexStringToBytes("0010000000");
        aid.TACOnline = PosUtils.hexStringToBytes("fc60acf800");
        aid.TACDefault = PosUtils.hexStringToBytes("fc60242800");
        aidList.add(aid);


        return aidList;
    }

    public static boolean isMastercard(String app) {
        return app != null && app.startsWith(MASTERCARD_AID_ROOT);
    }

    public static boolean isMastercard(PosEmvAid kaid) {
        return isMastercard(PosUtils.bytesToHexString(kaid.AID));
    }

    public static boolean isMastercard(byte[] app) {
        return app != null && isMastercard(PosUtils.bytesToHexString(app));
    }
}
