package com.example.card_payment;

import android.os.Bundle;

import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.emvcore.POIEmvCoreManager.EmvCardInfoConstraints;
import com.example.card_payment.emvconfig.BerTag;
import com.example.card_payment.emvconfig.BerTlv;
import com.example.card_payment.emvconfig.BerTlvBuilder;
import com.example.card_payment.emvconfig.BerTlvParser;
import com.example.card_payment.emvconfig.BerTlvs;
import com.example.card_payment.emvconfig.HexUtil;

import java.util.List;

public class SelectKernelUtils {

    private static final byte KERNEL_VISA       = 0x01;
    private static final byte KERNEL_UNIONPAY   = 0x02;
    private static final byte KERNEL_MASTERCARD = 0x03;
    private static final byte KERNEL_DISCOVER   = 0x04;
    private static final byte KERNEL_AMEX       = 0x05;
    private static final byte KERNEL_JCB        = 0x06;
    private static final byte KERNEL_MIR        = 0x07;
    private static final byte KERNEL_RUPAY      = 0x08;
    private static final byte KERNEL_PURE       = 0x09;
    private static final byte KERNEL_INTERAC    = 0x0A;
    private static final byte KERNEL_EFTPOS     = 0x0B;

    private static final String[] VISA_RID = new String[]{"A000000003"};
    private static final String[] MASTERCARD_RID = new String[]{"A000000004"};
    private static final String[] AMEX_RID = new String[]{"A000000025"};
    private static final String[] DISCOVER_RID = new String[]{"A000000152"};
    private static final String[] UPI_RID = new String[]{"A000000333"};
    private static final String[] JCB_RID = new String[]{"A000000065"};
    private static final String[] MIR_RID = new String[]{"A000000658"};
    private static final String[] RUPAY_RID = new String[]{"A000000524"};

    private static final String[] VERVE_RID= new String[]{"A000000371"};



    private static final String[] TROY_RID = new String[]{"A000000672"};
    private static final String TROY_AID1 = "A0000006723010";
    private static final String TROY_AID2 = "A0000006723020";

    public static void doSelectKernel(byte[] data) {
        byte kernel = 0;

        List<BerTlv> tlvs = new BerTlvParser().parse(data).findAll(new BerTag("DF01"));
        for (BerTlv tlv : tlvs) {
            BerTlvs tlvs1 = new BerTlvParser().parse(tlv.getBytesValue());
            BerTlv aid = tlvs1.find(new BerTag("4F"));
//            BerTlv label = tlvs1.find(new BerTag("50"));
//            BerTlv preferredName = tlvs1.find(new BerTag("9F12"));
//            BerTlv priority = tlvs1.find(new BerTag("87"));
            if (aid != null && aid.getHexValue().length() >= 5) {
                String val = HexUtil.toHexString(aid.getBytesValue(), 0, 5);
                if (is(val, VISA_RID)) {
                    kernel = KERNEL_VISA;
                }
                else if (is(val, MASTERCARD_RID)) {
                    kernel = KERNEL_MASTERCARD;
                }
                else if (is(val, AMEX_RID)) {
                    kernel = KERNEL_AMEX;
                }
                else if (is(val, DISCOVER_RID)) {
                    kernel = KERNEL_DISCOVER;
                }
                else if (is(val, UPI_RID)) {
                    kernel = KERNEL_UNIONPAY;
                }
                else if (is(val, JCB_RID)) {
                    kernel = KERNEL_JCB;
                }
                else if (is(val, MIR_RID)) {
                    kernel = KERNEL_MIR;
                }
                else if (is(val, RUPAY_RID)) {
                    kernel = KERNEL_RUPAY;
                }
                else if (is(val, TROY_RID)) {
                    kernel = KERNEL_DISCOVER;
                }
                else if(is(val, VERVE_RID)){
                    kernel = KERNEL_PURE;
                }

//                String aidStr = HexUtil.toHexString(aid.getBytesValue());
//                if(aidStr.contains(TROY_AID1)||aidStr.contains(TROY_AID2)){
//                    kernel = KERNEL_DISCOVER;
//                }
            }
        }




        Bundle bundle = new Bundle();

        if (kernel == 0) {
            bundle.putByteArray(EmvCardInfoConstraints.OUT_TLV, new byte[0]);
            POIEmvCoreManager.getDefault().onSetCardInfoResponse(bundle);
            return;
        }

        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        tlvBuilder.addByte(new BerTag("DF10"), kernel);
        bundle.putByteArray(EmvCardInfoConstraints.OUT_TLV, tlvBuilder.buildArray());
        POIEmvCoreManager.getDefault().onSetCardInfoResponse(bundle);
    }

    private static boolean is(String aid, String[] aids) {
        for (String var : aids) {
            if (aid.equalsIgnoreCase(var)) {
                return true;
            }
        }
        return false;
    }
}
