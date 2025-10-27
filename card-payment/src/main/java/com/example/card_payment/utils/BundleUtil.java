package com.example.card_payment.utils;

import android.os.Bundle;
import android.util.Log;

import com.pos.sdk.utils.PosUtils;

import java.util.Set;

public class BundleUtil {
    private static final String TAG = "BundleUtil";

    static public String showKeyTypesInBundle(
            Bundle bundle)  // bundle to query
    {
        // Notes:
        // 'print()' is my output function (e.g. Log.i).
        // keys with null values are not shown.
        String returnStr = "";
        int size;
        Set<String> ks = bundle.keySet();   // get keys in bundle
        size = ks.size();                   // get key count

        Log.d(TAG, "KEYS IN BUNDLE:");
        if( size > 0 ) {                    // any keys in bundle?
            for (String key : ks ) {        // for every key in keyset...
                String type = getKeyType(bundle, key);  // get type
                Log.d(TAG,"  key \"" + key + "\": type =\"" + type + "\"");

                Object tmp = bundle.get(key);
                if (tmp instanceof Boolean) {
                    boolean finalValue = ((Boolean)tmp).booleanValue();
                    returnStr += String.format("%s=%b", key,finalValue);
                    returnStr +=" \n";
                } else if (tmp instanceof String) {
                    String finalValue = ((String)tmp);
                    returnStr += String.format("%s=%s", key,finalValue);
                    returnStr +=" \n";
                }else if (tmp instanceof Long) {
                    Long finalValue = ((Long)tmp);
                    returnStr += String.format("%s=%d", key,finalValue);
                    returnStr +=" \n";
                }else if (tmp instanceof Integer) {
                    Integer finalValue = ((Integer)tmp);
                    returnStr += String.format("%s=%d", key,finalValue);
                    returnStr +=" \n";
                }else if (tmp instanceof Short) {
                    Short finalValue = ((Short)tmp);
                    returnStr += String.format("%s=%d", key,finalValue);
                    returnStr +=" \n";
                }else if (tmp instanceof Byte) {
                    Byte finalValue = ((Byte)tmp);
                    returnStr += String.format("%s=0X%02X", key,finalValue);
                    returnStr +=" \n";
                }
                else if (tmp instanceof byte[]) {
                    byte[] finalValue = ((byte[])tmp);
                    returnStr += String.format("%s=%s", key,
                            PosUtils.bytesToHexString(finalValue));
                    returnStr +=" \n";
                }
            }
        }
        else {                               // no keys in bundle?
            Log.d(TAG, "  No Keys found" );
        }
        Log.d(TAG, "END BUNDLE." );
        return returnStr;
    }
    //@ Get type of bundle key
    static public String getKeyType(
            Bundle bundle,  // bundle containing key
            String key)     // key name

    {
        Object keyobj;
        String type;

        if( bundle == null || key == null ) return( null );     // bad call/

        keyobj = bundle.get( key );                             // get key as object
        if( keyobj == null ) return( null );                    // not present?
        type = keyobj.getClass().getName();                     // get class name
        return( type );
        // Returns name of key value type
        // e.g. "java.lang.String" or "java.lang.Integer"
        // or null on error        }
    }
}
