package com.example.card_payment.emvconfig;


public class EntryPoint {
    public boolean StatusCheck;
    public boolean ZeroAmountCheck;
    public boolean CTLCheck;
    public boolean CFLCheck;
    public boolean CVMCheck;

    public EntryPoint(){}

    /**
     *
     * @param statusCheck
     * @param zeroAmountCheck
     * @param CTLCheck
     * @param CFLCheck
     * @param CVMCheck
     */
    public EntryPoint(boolean statusCheck, boolean zeroAmountCheck, boolean CTLCheck, boolean CFLCheck, boolean CVMCheck) {
        StatusCheck = statusCheck;
        ZeroAmountCheck = zeroAmountCheck;
        this.CTLCheck = CTLCheck;
        this.CFLCheck = CFLCheck;
        this.CVMCheck = CVMCheck;
    }

    public byte[] toData() {
        byte[] data = new byte[1];
        data[0] |= ObjectUtil.iif(StatusCheck, ByteUtil.MASK_80);//(byte) 0x80);
        data[0] |= ObjectUtil.iif(ZeroAmountCheck, ByteUtil.MASK_40);//(byte) 0x40);
        data[0] |= ObjectUtil.iif(CTLCheck, ByteUtil.MASK_20);//(byte) 0x20);
        data[0] |= ObjectUtil.iif(CFLCheck, ByteUtil.MASK_10);//(byte) 0x10);
        data[0] |= ObjectUtil.iif(CVMCheck, ByteUtil.MASK_08);//(byte) 0x08);
        return data;
    }
}