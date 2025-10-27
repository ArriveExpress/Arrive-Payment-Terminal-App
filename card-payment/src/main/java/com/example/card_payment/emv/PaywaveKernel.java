package com.example.card_payment.emv;

import android.os.Bundle;
import android.util.Log;

import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.example.card_payment.emvconfig.BerTag;
import com.example.card_payment.emvconfig.BerTlv;
import com.example.card_payment.emvconfig.BerTlvBuilder;
import com.example.card_payment.emvconfig.ByteUtil;
import com.example.card_payment.emvconfig.EntryPoint;
import com.example.card_payment.emvconfig.ObjectUtil;

//Paywave need to load KernelConfig and TTQ(9F66 Qualifiers)
public class PaywaveKernel {
    private static final String TAG = "PaywaveKernel";

    private static final byte VISA_SUPPORT_DRL_BIT = ByteUtil.MASK_01;// (byte) 0x01;
    private static final byte VISA_SUPPORT_MAG_STRIPE_BIT = ByteUtil.MASK_80;// 0x80
    private static final byte VISA_SUPPORT_QVSDC_BIT = ByteUtil.MASK_20;// 0x20
    private static final byte VISA_SUPPORT_CONTACT_BIT = ByteUtil.MASK_10;// 0x10;
    private static final byte VISA_OFFLINE_ONLY_BIT = ByteUtil.MASK_08;// 0x08;
    private static final byte VISA_SUPPORT_ONLINE_PIN_BIT = ByteUtil.MASK_04;// 0x04;
    private static final byte VISA_SUPPORT_SIGNATURE_BIT = ByteUtil.MASK_02;// 0x02;
    private static final byte VISA_SUPPORT_ONLINE_ODA_BIT = ByteUtil.MASK_01;// 0x01;
    private static final byte VISA_SUPPORT_ISSUER_SCRIPT_UPDATE_BIT = ByteUtil.MASK_80;// 0x80;
    private static final byte VISA_SUPPORT_CDCVM_BIT = ByteUtil.MASK_40;// (byte) 0x40;

    boolean SupportMagStripe = false;
    boolean SupportQVSDC = true;
    boolean SupportContact = true;
    boolean OfflineOnly = false;
    boolean SupportOnlinePIN = true;
    boolean SupportSignature = true;
    boolean SupportOnlineODA = false;
    boolean SupportIssuerScriptUpdate = false;
    boolean SupportCDCVM = true;
    boolean SupportDRL = false;

    public static int loadDefault() {
        PaywaveKernel paywaveKernel = new PaywaveKernel();
        EntryPoint entryPoint = new EntryPoint(true, true, true, true, true);
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_VISA_SET_ENTRY_POINT),
                entryPoint.toData()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_VISA_SET_STATUS_ZERO_AMOUNT),
                new byte[]{0x01}));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_VISA_SET_KERNEL_CONFIG),
                paywaveKernel.toKernelConfig()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_VISA_SET_QUALIFIERS),
                paywaveKernel.toQualifiers()));
        Bundle bundle = new Bundle();
        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(
                POIEmvCoreManager.EmvTerminalConstraints.TYPE_VISA, bundle);
        if(result!=0){
            Log.d(TAG, "loadMir: failed");
        }
        Log.d(TAG, "loadMir: success");
        return 0;
    }

    public byte[] toKernelConfig() {
        byte[] kernelConfig = new byte[1];
        kernelConfig[0] |= ObjectUtil.iif(this.SupportDRL, VISA_SUPPORT_DRL_BIT);//0x01
        return kernelConfig;
    }

    public byte[] toQualifiers() {
        byte[] qualifiers = new byte[4];
        qualifiers[0] |= ObjectUtil.iif(this.SupportMagStripe, VISA_SUPPORT_MAG_STRIPE_BIT);//0x80;
        qualifiers[0] |= ObjectUtil.iif(this.SupportQVSDC, VISA_SUPPORT_QVSDC_BIT);//0x20;
        qualifiers[0] |= ObjectUtil.iif(this.SupportContact, VISA_SUPPORT_CONTACT_BIT);//0x10;
        qualifiers[0] |= ObjectUtil.iif(this.OfflineOnly, VISA_OFFLINE_ONLY_BIT);//0x08;
        qualifiers[0] |= ObjectUtil.iif(this.SupportOnlinePIN, VISA_SUPPORT_ONLINE_PIN_BIT);//0x04;
        qualifiers[0] |= ObjectUtil.iif(this.SupportSignature, VISA_SUPPORT_SIGNATURE_BIT);//0x02;
        qualifiers[0] |= ObjectUtil.iif(this.SupportOnlineODA, VISA_SUPPORT_ONLINE_ODA_BIT);//0x01;
        qualifiers[2] |= ObjectUtil.iif(this.SupportIssuerScriptUpdate, VISA_SUPPORT_ISSUER_SCRIPT_UPDATE_BIT); //0x80
        qualifiers[2] |= ObjectUtil.iif(this.SupportCDCVM, VISA_SUPPORT_CDCVM_BIT); //0x40
        return qualifiers;
    }
}
