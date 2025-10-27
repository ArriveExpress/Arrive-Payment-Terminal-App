package com.example.card_payment.emvconfig;

public class ObjectUtil {

    public static byte iif(boolean test, byte ifTrue, byte ifFalse) {
        return test ? ifTrue : ifFalse;
    }

    public static byte iif(boolean test, byte ifTrue) {
        byte zero = 0;
        return iif(test, ifTrue, zero);
    }

    public static int iif(boolean test, int ifTrue, int ifFalse) {
        return test ? ifTrue : ifFalse;
    }

    public static int iif(boolean test, int ifTrue) {
        return iif(test, ifTrue, 0);
    }
}
