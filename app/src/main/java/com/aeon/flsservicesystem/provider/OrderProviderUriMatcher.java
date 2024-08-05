package com.aeon.flsservicesystem.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.util.SparseArray;

public class OrderProviderUriMatcher {
    private UriMatcher mUriMatcher;

    private SparseArray<OrderUriEnum> mEnumsMap = new SparseArray<>();

    public OrderProviderUriMatcher() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        buildUriMatcher();
    }

    private void buildUriMatcher() {
        final String authority = OrderContract.CONTENT_AUTHORITY;

        OrderUriEnum[] uris = OrderUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mUriMatcher.addURI(authority, uris[i].path, uris[i].code);
        }

        buildEnumsMap();
    }

    private void buildEnumsMap() {
        OrderUriEnum[] uris = OrderUriEnum.values();
        for (int i = 0; i < uris.length; i++) {
            mEnumsMap.put(uris[i].code, uris[i]);
        }
    }

    public OrderUriEnum matchUri(Uri uri) {
        final int code = mUriMatcher.match(uri);
        try {
            return matchCode(code);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    public OrderUriEnum matchCode(int code) {
        OrderUriEnum orderUriEnum = mEnumsMap.get(code);
        if (orderUriEnum != null) {
            return orderUriEnum;
        } else {
            throw new UnsupportedOperationException("Unknown uri with code " + code);
        }
    }
}
