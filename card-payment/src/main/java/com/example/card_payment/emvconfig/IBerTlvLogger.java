package com.example.card_payment.emvconfig;

public interface IBerTlvLogger {

    boolean isDebugEnabled();

    void debug(String aFormat, Object... args);
}
