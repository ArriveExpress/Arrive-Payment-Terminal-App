package com.example.card_payment.emvconfig;

import com.pos.sdk.utils.PosUtils;

public class BerTlvBuilder {

    private static final int DEFAULT_SIZE = 30 * 1024;

    public BerTlvBuilder() {
        this(null);
    }

    public BerTlvBuilder(BerTag aTemplate) {
        this(aTemplate, new byte[DEFAULT_SIZE], 0, DEFAULT_SIZE);
    }

    public BerTlvBuilder(BerTag aTemplate, byte[] aBuffer, int aOffset, int aLength) {
        theTemplate = aTemplate;
        theBuffer = aBuffer;
        thePos = aOffset;
        theBufferOffset = aOffset;
    }

    public static BerTlvBuilder from(BerTlv aTlv) {
        if (aTlv.isConstructed()) {
            BerTlvBuilder builder = template(aTlv.getTag());
            for (BerTlv tlv : aTlv.theList) {
                builder.addBerTlv(tlv);
            }
            return builder;
        } else {
            return new BerTlvBuilder().addBerTlv(aTlv);
        }
    }

    public static BerTlvBuilder template(BerTag aTemplate) {
        return new BerTlvBuilder(aTemplate);
    }

    public BerTlvBuilder addEmpty(BerTag aObject) {
        return addBytes(aObject, new byte[]{}, 0, 0);
    }

    public BerTlvBuilder addByte(BerTag aObject, byte aByte) {
        int len = aObject.bytes.length;
        System.arraycopy(aObject.bytes, 0, theBuffer, thePos, len);
        thePos += len;
        theBuffer[thePos++] = 1;
        theBuffer[thePos++] = aByte;
        return this;
    }
    public int build() {
        if (theTemplate != null) {
            int tagLen = theTemplate.bytes.length;
            int lengthBytesCount = calculateBytesCountForLength(thePos);

            System.arraycopy(theBuffer, theBufferOffset, theBuffer, tagLen + lengthBytesCount, thePos);

            System.arraycopy(theTemplate.bytes, 0, theBuffer, theBufferOffset, theTemplate.bytes.length);

            fillLength(theBuffer, tagLen, thePos);

            thePos += tagLen + lengthBytesCount;
        }
        return thePos;
    }

    private void fillLength(byte[] aBuffer, int aOffset, int aLength) {
        if (aLength < 0x80) {
            aBuffer[aOffset] = (byte) aLength;
        } else if (aLength < 0x100) {
            aBuffer[aOffset] = (byte) 0x81;
            aBuffer[aOffset + 1] = (byte) aLength;
        } else if (aLength < 0x10000) {
            aBuffer[aOffset] = (byte) 0x82;
            aBuffer[aOffset + 1] = (byte) (aLength / 0x100);
            aBuffer[aOffset + 2] = (byte) (aLength % 0x100);
        } else if (aLength < 0x1000000) {
            aBuffer[aOffset] = (byte) 0x83;
            aBuffer[aOffset + 1] = (byte) (aLength / 0x10000);
            aBuffer[aOffset + 2] = (byte) (aLength / 0x100);
            aBuffer[aOffset + 3] = (byte) (aLength % 0x100);
        } else {
            throw new IllegalStateException("length [" + aLength + "] out of range (0x1000000)");
        }
    }

    private int calculateBytesCountForLength(int aLength) {
        int ret;
        if (aLength < 0x80) {
            ret = 1;
        } else if (aLength < 0x100) {
            ret = 2;
        } else if (aLength < 0x10000) {
            ret = 3;
        } else if (aLength < 0x1000000) {
            ret = 4;
        } else {
            throw new IllegalStateException("length [" + aLength + "] out of range (0x1000000)");
        }
        return ret;
    }

    public BerTlvBuilder addHex(BerTag aObject, String aHex) {
        byte[] buffer = PosUtils.hexStringToBytes(aHex);
        return addBytes(aObject, buffer, 0, buffer.length);
    }

    public BerTlvBuilder addBytes(BerTag aObject, byte[] aBytes) {
        return addBytes(aObject, aBytes, 0, aBytes.length);
    }

    public BerTlvBuilder addBytes(BerTag aTag, byte[] aBytes, int aFrom, int aLength) {
        int tagLength = aTag.bytes.length;
        int lengthBytesCount = calculateBytesCountForLength(aLength);

        System.arraycopy(aTag.bytes, 0, theBuffer, thePos, tagLength);
        thePos += tagLength;

        fillLength(theBuffer, thePos, aLength);
        thePos += lengthBytesCount;

        System.arraycopy(aBytes, aFrom, theBuffer, thePos, aLength);
        thePos += aLength;
        return this;
    }

    public BerTlvBuilder add(BerTlvBuilder aBuilder) {
        byte[] array = aBuilder.buildArray();
        System.arraycopy(array, 0, theBuffer, thePos, array.length);
        thePos += array.length;
        return this;
    }

    public BerTlvBuilder addBerTlv(BerTlv aTlv) {
        if (aTlv.isConstructed()) {
            return add(from(aTlv));
        } else {
            return addBytes(aTlv.getTag(), aTlv.getBytesValue());
        }
    }

    public BerTlvBuilder addIntAsHex(BerTag aObject, int aCode) {
        String s = (String.valueOf(aCode));
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        return addHex(aObject, s);
    }

    public byte[] buildArray() {
        int count = build();
        byte[] buf = new byte[count];
        System.arraycopy(theBuffer, 0, buf, 0, count);
        return buf;
    }


    private final int    theBufferOffset;
    private       int    thePos;
    private final byte[] theBuffer;
    private final BerTag theTemplate;
}
