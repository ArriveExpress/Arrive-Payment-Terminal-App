package com.example.card_payment.emvconfig;

import android.os.Bundle;
import android.util.Log;

import com.pos.sdk.emvcore.POIEmvCoreManager;
import com.pos.sdk.utils.PosUtils;

import java.util.ArrayList;
import java.util.List;

public class CLSettingUtils {
    private static final String TAG = "CLSettingUtils";
    protected static class DiscoverParameter {
        boolean SupportMagstripe = true;
        boolean SupportEMV = true;
        boolean SupportContact = true;
        boolean OfflineOnly = false;
        boolean SupportOnlinePIN = true;
        boolean SupportSignature = true;
        boolean SupportIssuerScriptUpdate = false;
        boolean SupportCDCVM = true;
    }

    protected static class AmexParameter {
        boolean SupportContact = true;
        boolean SupportMagstripe = true;
        boolean TryAnotherInterface = true;
        boolean SupportCDCVM = true;
        boolean SupportOnlinePIN = true;
        boolean SupportSignature = true;
        boolean OfflineOnly = false;
        boolean ExemptNoCVMCheck = false;
        boolean DelayedAuthorization = false;
        boolean SupportDRL = false;
    }

    protected static class MirParameter {
        private final boolean SupportOnlinePIN = true;
        private final boolean SupportSignature = true;
        private final boolean SupportCDCVM = true;
        private final boolean UnableOnline = false;
        private final boolean SupportContact = true;
        private final boolean OfflineOnly = false;
        private final boolean DelayedAuthorization = false;
        private final boolean ATM = false;
    }

    protected static class DynamicReaderLimit {
        private String ProgramID;
        private int TransLimit;
        private int CVMLimit;
        private int FloorLimit;
    }

    protected static class PRMacq {
        private byte Index;
        private byte[] Key;
        private byte[] Kcv;
    }
    protected static class Service {
        private byte Priority;
        private byte[] ServiceID;
        private byte[] ServiceManage;
        private byte[] ServiceData;
        private byte[] PRMiss;
        private List<PRMacq> PRMacq;
    }

    public static int loadUnionPay() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        EntryPoint entryPoint = new EntryPoint(true,
                true,
                true,
                true,
                true);
        tlvBuilder.addBerTlv(new BerTlv(
                new BerTag(POIEmvCoreManager.EmvTerminalConstraints.TAG_UNIONPAY_SET_ENTRY_POINT),
                entryPoint.toData()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_UNIONPAY_SET_STATUS_ZERO_AMOUNT),
                PosUtils.hexStringToBytes("01")));

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_UNIONPAY_SET_QUALIFIERS),
                PosUtils.hexStringToBytes("36800000")));
        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(
                POIEmvCoreManager.EmvTerminalConstraints.TYPE_UNIONPAY, bundle);
        if(result!=0){
            Log.d(TAG, "loadUnionPay: failed");
        }
        Log.d(TAG, "loadUnionPay: success");
        return 0;
    }


    public static int loadPaypass() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_CVM_CAPABILITIES),
                PosUtils.hexStringToBytes("60")));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_NO_CVM_CAPABILITIES),
                PosUtils.hexStringToBytes("08")));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_MAGSTRIPE_CVM_CAPABILITIES),
                PosUtils.hexStringToBytes("10")));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_MAGSTRIPE_NO_CVM_CAPABILITIES),
                PosUtils.hexStringToBytes("00")));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_DEFAULT_UDOL),
                PosUtils.hexStringToBytes("9F6A04")));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_KERNEL_CONFIG),
                PosUtils.hexStringToBytes("30")));

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MASTERCARD_SET_MOBILE_SUPPORT_INDICATOR),
                PosUtils.hexStringToBytes("00")));


        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(
                POIEmvCoreManager.EmvTerminalConstraints.TYPE_MASTERCARD, bundle);
        if(result!=0){
            Log.d(TAG, "loadPaypass: failed");
        }
        Log.d(TAG, "loadPaypass: success");
        return 0;
    }


    public static int loadDiscover() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();

        DiscoverParameter discoverParameter = new DiscoverParameter();
        EntryPoint entryPoint = new EntryPoint();
        entryPoint.CTLCheck = true;
        entryPoint.CVMCheck = true;
        entryPoint.CFLCheck = true;
        entryPoint.StatusCheck = true;
        entryPoint.ZeroAmountCheck = true;

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_DISCOVER_SET_ENTRY_POINT),
                entryPoint.toData()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_DISCOVER_SET_STATUS_ZERO_AMOUNT),
                PosUtils.hexStringToBytes("01")));

        byte[] qualifiers = new byte[4];
        if (discoverParameter.SupportMagstripe) {
            qualifiers[0] |= 0x80;
        }
        if (discoverParameter.SupportEMV) {
            qualifiers[0] |= 0x20;
        }
        if (discoverParameter.SupportContact) {
            qualifiers[0] |= 0x10;
        }
        if (discoverParameter.OfflineOnly) {
            qualifiers[0] |= 0x08;
        }
        if (discoverParameter.SupportOnlinePIN) {
            qualifiers[0] |= 0x04;
        }
        if (discoverParameter.SupportSignature) {
            qualifiers[0] |= 0x02;
        }
        if (discoverParameter.SupportIssuerScriptUpdate) {
            qualifiers[2] |= 0x80;
        }
        if (discoverParameter.SupportCDCVM) {
            qualifiers[2] |= 0x40;
        }

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_DISCOVER_SET_QUALIFIERS), qualifiers));

        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(
                POIEmvCoreManager.EmvTerminalConstraints.TYPE_DISCOVER, bundle);
        if(result!=0){
            Log.d(TAG, "loadDiscover: failed");
        }
        Log.d(TAG, "loadDiscover: success");
        return 0;
    }

    public static int loadAmex() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();

        AmexParameter amexParameter = new AmexParameter();
        EntryPoint entryPoint = new EntryPoint();
        entryPoint.CTLCheck = true;
        entryPoint.CVMCheck = true;
        entryPoint.CFLCheck = true;
        entryPoint.StatusCheck = true;
        entryPoint.ZeroAmountCheck = true;

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_AMEX_SET_ENTRY_POINT),
                entryPoint.toData()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_AMEX_SET_STATUS_ZERO_AMOUNT),
                PosUtils.hexStringToBytes("01")));

        byte[] kernelConfig = new byte[1];
        if (amexParameter.SupportDRL) {
            kernelConfig[0] |= 0x01;
        }
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_AMEX_SET_KERNEL_CONFIG),
                kernelConfig));

        byte[] qualifiers = new byte[4];
        if (amexParameter.SupportContact) {
            qualifiers[0] |= 0x80;
        }
        if (amexParameter.SupportMagstripe) {
            qualifiers[0] |= 0x40;
        }
        if (amexParameter.TryAnotherInterface) {
            qualifiers[0] |= 0x04;
        }
        if (amexParameter.SupportCDCVM) {
            qualifiers[1] |= 0x80;
        }
        if (amexParameter.SupportOnlinePIN) {
            qualifiers[1] |= 0x40;
        }
        if (amexParameter.SupportSignature) {
            qualifiers[1] |= 0x20;
        }
        if (amexParameter.OfflineOnly) {
            qualifiers[2] |= 0x80;
        }
        if (amexParameter.ExemptNoCVMCheck) {
            qualifiers[3] |= 0x80;
        }
        if (amexParameter.DelayedAuthorization) {
            qualifiers[3] |= 0x40;
        }
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_AMEX_SET_QUALIFIERS), qualifiers));

        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(
                POIEmvCoreManager.EmvTerminalConstraints.TYPE_AMEX, bundle);
        if(result!=0){
            Log.d(TAG, "loadAmex: failed");
        }
        Log.d(TAG, "loadAmex: success");
        return 0;
    }

    public static final int SETTINGS_JCB                 = 25;
    public static int loadJCB(){
        Bundle bundle = new Bundle();

        // 9F5303 728000 DF1B013F DF300107 DF310101 DF320101
        // 9F5303728000DF1B013FDF300107DF310101DF320101
        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG,
                PosUtils.hexStringToBytes("9F5303720000DF1B013FDF300107DF310101DF320101"));
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(SETTINGS_JCB, bundle);
        if(result!=0){
            Log.d(TAG, "loadMir: failed");
        }
        Log.d(TAG, "loadMir: success");
        return 0;
    }
    public static int loadMir() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();

        MirParameter mirParameter = new MirParameter();
        EntryPoint entryPoint = new EntryPoint(true, true, true, true, true);

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MIR_SET_ENTRY_POINT),
                entryPoint.toData()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MIR_SET_STATUS_ZERO_AMOUNT),
                PosUtils.hexStringToBytes("01")));

        byte[] qualifiers = new byte[4];
        if (mirParameter.SupportOnlinePIN) {
            qualifiers[0] |= 0x80;
        }
        if (mirParameter.SupportSignature) {
            qualifiers[0] |= 0x40;
        }
        if (mirParameter.SupportCDCVM) {
            qualifiers[0] |= 0x20;
        }
        if (mirParameter.UnableOnline) {
            qualifiers[0] |= 0x10;
        }
        if (mirParameter.SupportContact) {
            qualifiers[0] |= 0x08;
        }
        if (mirParameter.OfflineOnly) {
            qualifiers[0] |= 0x04;
        }
        if (mirParameter.DelayedAuthorization) {
            qualifiers[0] |= 0x02;
        }
        if (mirParameter.ATM) {
            qualifiers[0] |= 0x01;
        }

        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvTerminalConstraints.TAG_MIR_SET_QUALIFIERS), qualifiers));

        bundle.putByteArray(POIEmvCoreManager.EmvTerminalConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetTerminal(
                POIEmvCoreManager.EmvTerminalConstraints.TYPE_MIR, bundle);
        if(result!=0){
            Log.d(TAG, "loadMir: failed");
        }
        Log.d(TAG, "loadMir: success");
        return 0;
    }

    public static int loadRupayService() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();

        Service service = new Service();
        service.ServiceID = PosUtils.hexStringToBytes("1010");
        service.Priority = 60;
        service.ServiceManage = PosUtils.hexStringToBytes("B500");
        service.ServiceData = PosUtils.hexStringToBytes("0106053005010111000002000102010206000A0101030201020304050607081122330102010215050110101000000000100001000000010203000102040100000000000000000000000000000000000000000000000000000000000000000000");
        service.PRMiss = PosUtils.hexStringToBytes("13131313131313131313131313131313");
        service.PRMacq = new ArrayList<>();
        PRMacq acq = new PRMacq();
        acq.Index = 1;
        acq.Key = PosUtils.hexStringToBytes("D694705EDF0DFBB52023C134CEB954E5");
        acq.Kcv = PosUtils.hexStringToBytes("A32BBA");
        service.PRMacq.add(acq);
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_DELIMITER), serviceToData(service)));

        service = new Service();
        service.ServiceID = PosUtils.hexStringToBytes("1011");
        service.Priority = 60;
        service.ServiceManage = PosUtils.hexStringToBytes("B500");
        service.ServiceData = PosUtils.hexStringToBytes("0106053005010111000002000102010206000A0101030201020304050607081122330102010215050110101000000000100001000000010203000102040100000000000000000000000000000000000000000000000000000000000000000000");
        service.PRMiss = PosUtils.hexStringToBytes("13131313131313131313131313131313");
        service.PRMacq = new ArrayList<>();
        acq = new PRMacq();
        acq.Key = PosUtils.hexStringToBytes("D694705EDF0DFBB52023C134CEB954E5");
        acq.Kcv = PosUtils.hexStringToBytes("A32BBA");
        service.PRMacq.add(acq);
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_DELIMITER),
                serviceToData(service)));

        bundle.putByteArray(POIEmvCoreManager.EmvServiceConstraints.CONFIG, tlvBuilder.buildArray());
        int result = POIEmvCoreManager.getDefault().EmvSetService(bundle);
        if(result!=0){
            Log.d(TAG, "loadRupayService: failed");
        }
        Log.d(TAG, "loadRupayService: success");
        return 0;
    }

    public static int loadVisaDRL() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        EntryPoint entryPoint = new EntryPoint(false, false, true, true, true);
        DynamicReaderLimit limit = new DynamicReaderLimit();
        limit.ProgramID = "01";
        limit.TransLimit = 200000;
        limit.CVMLimit = 10000;
        limit.FloorLimit = 10000;
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_DELIMITER), drlToData(limit, entryPoint)));
        limit.ProgramID = "010203";
        limit.TransLimit = 200000;
        limit.CVMLimit = 10000;
        limit.FloorLimit = 10000;
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_DELIMITER), drlToData(limit, entryPoint)));
        limit.ProgramID = "0102030405";
        limit.TransLimit = 200000;
        limit.CVMLimit = 10000;
        limit.FloorLimit = 10000;
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_DELIMITER), drlToData(limit, entryPoint)));

        bundle.putByteArray(POIEmvCoreManager.EmvDrlConstraints.CONFIG, tlvBuilder.buildArray());
        POIEmvCoreManager.getDefault().EmvSetDRL(POIEmvCoreManager.EmvDrlConstraints.TYPE_VISA, bundle);
        return 0;
    }

    public static int loadAmexDRL() {
        Bundle bundle = new Bundle();
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();

        EntryPoint entryPoint = new EntryPoint();
        entryPoint.CTLCheck = true;
        entryPoint.CVMCheck = true;
        entryPoint.CFLCheck = true;

        DynamicReaderLimit[] limits = new DynamicReaderLimit[17];
        for (int i = 0; i < limits.length; i++) {
            limits[i] = new DynamicReaderLimit();
            switch (i) {
                case 0:
                case 2:
                case 15:
                    limits[i].ProgramID = "1";
                    break;
                default:
                    break;
            }
            limits[i].TransLimit = 200000;
            limits[i].CVMLimit = 10000;
            limits[i].FloorLimit = 10000;
        }

        for (DynamicReaderLimit limit : limits) {
            if (limit.ProgramID == null) {
                tlvBuilder.addEmpty(new BerTag(POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_DELIMITER));
                continue;
            }
            limit.ProgramID = null;
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_DELIMITER), drlToData(limit, entryPoint)));
        }

        bundle.putByteArray(POIEmvCoreManager.EmvDrlConstraints.CONFIG, tlvBuilder.buildArray());
        POIEmvCoreManager.getDefault().EmvSetDRL(POIEmvCoreManager.EmvDrlConstraints.TYPE_AMEX, bundle);
        return 0;
    }



    private static byte[] drlToData(DynamicReaderLimit limit, EntryPoint entryPoint) {
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        if (limit.ProgramID != null) {
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                    POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_PROGRAM_ID),
                    limit.ProgramID.getBytes()));
        }
        tlvBuilder.addIntAsHex(new BerTag(
                POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_TRANSACTION_LIMIT),
                limit.TransLimit);
        tlvBuilder.addIntAsHex(new BerTag(
                POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_CVM_REQUIRED_LIMIT),
                limit.CVMLimit);
        tlvBuilder.addIntAsHex(new BerTag(
                POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_FLOOR_LIMIT),
                limit.FloorLimit);
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_ENTRY_POINT),
                entryPoint.toData()));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(
                POIEmvCoreManager.EmvDrlConstraints.TAG_DRL_SET_STATUS_ZERO_AMOUNT),
                PosUtils.hexStringToBytes("01")));
        return tlvBuilder.buildArray();
    }

    private static byte[] prmacqToData(PRMacq prmacq) {
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        tlvBuilder.addByte(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_PRMACQ_SET_INDEX), prmacq.Index);
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_PRMACQ_SET_KEY), prmacq.Key));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_PRMACQ_SET_KCV), prmacq.Kcv));
        return tlvBuilder.buildArray();
    }

    private static byte[] serviceToData(Service service) {
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_ID), service.ServiceID));
        tlvBuilder.addByte(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_PRIORITY), service.Priority);
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_MANAGEMENT), service.ServiceManage));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_DATA), service.ServiceData));
        tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_PRMISS), service.PRMiss));
        if (service.PRMacq != null) {
            BerTlvBuilder acqTlvBuilder = new BerTlvBuilder();
            for (PRMacq prmacq : service.PRMacq) {
                if (prmacq == null) {
                    continue;
                }

                if (prmacq.Key == null || !(prmacq.Key.length == 8 || prmacq.Key.length == 16)) {
                    continue;
                }

                if (prmacq.Kcv == null || prmacq.Kcv.length != 3) {
                    continue;
                }

                acqTlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_PRMACQ_SET_DELIMITER), prmacqToData(prmacq)));
            }
            tlvBuilder.addBerTlv(new BerTlv(new BerTag(POIEmvCoreManager.EmvServiceConstraints.TAG_SERVICE_SET_PRMACQ), acqTlvBuilder.buildArray()));
        }
        return tlvBuilder.buildArray();
    }
}
