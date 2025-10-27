package com.example.card_payment.emvconfig;

public class EMVConfig {

    public String currencyCode;
    public String currencyExponent;
    public String merchantIdentifier;
    public String terminalCountryCode;
    public String terminalIdentification;
    public String terminalCapabilities;
    public String terminalType;
    public String additionalTerminalCapabilities;
    public String merchantNameAndLocation;
    public String ttq1;
    public String contactlessFloorLimit;
    public String contactlessTransactionLimit;
    public String contactlessCvmLimit;
    public String statusCheckSupport;

    @Override
    public String toString() {
        return "EMVConfig{" +
                "currencyCode='" + currencyCode + '\'' +
                ", currencyExponent='" + currencyExponent + '\'' +
                ", merchantIdentifier='" + merchantIdentifier + '\'' +
                ", terminalCountryCode='" + terminalCountryCode + '\'' +
                ", terminalIdentification='" + terminalIdentification + '\'' +
                ", terminalCapabilities='" + terminalCapabilities + '\'' +
                ", terminalType='" + terminalType + '\'' +
                ", additionalTerminalCapabilities='" + additionalTerminalCapabilities + '\'' +
                ", merchantNameAndLocation='" + merchantNameAndLocation + '\'' +
                ", ttq1='" + ttq1 + '\'' +
                ", contactlessFloorLimit='" + contactlessFloorLimit + '\'' +
                ", contactlessTransactionLimit='" + contactlessTransactionLimit + '\'' +
                ", contactlessCvmLimit='" + contactlessCvmLimit + '\'' +
                ", statusCheckSupport='" + statusCheckSupport + '\'' +
                '}';
    }
    static public EMVConfig getDefault() {
        EMVConfig emvConfig = new EMVConfig();
        emvConfig.terminalCountryCode = "170"; //"0604";
        emvConfig.ttq1 = "36";
        emvConfig.terminalType = "21";
        emvConfig.statusCheckSupport = "00";
        emvConfig.merchantNameAndLocation = "COMMERCE 1";
        emvConfig.merchantIdentifier = "123645678";
        emvConfig.terminalIdentification = "1234";

        emvConfig.terminalCapabilities = "E0F8E8";
        emvConfig.additionalTerminalCapabilities = "FF80F0A001";
        emvConfig.contactlessFloorLimit = "0";
        emvConfig.contactlessCvmLimit = "30000";
        emvConfig.contactlessTransactionLimit = "9999999";

        emvConfig.currencyCode = "170";//"0604";
        emvConfig.currencyExponent = "02"; //
        emvConfig.currencyCode = padLeft(emvConfig.currencyCode, 4);
        return emvConfig;
    }
    public static String padLeft(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
}
