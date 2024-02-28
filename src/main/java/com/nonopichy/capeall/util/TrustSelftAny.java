package com.nonopichy.capeall.util;

import org.apache.http.conn.ssl.TrustStrategy;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustSelftAny implements TrustStrategy {

    public static final TrustSelftAny INSTANCE = new TrustSelftAny();

    @Override
    public boolean isTrusted(
            final X509Certificate[] chain, final String authType) throws CertificateException {
        return chain.length == 1;
    }

}
