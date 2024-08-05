package com.aeon.flsservicesystem.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.aeon.flsservicesystem.provider.OrderContract.CollectorResults;
import com.aeon.flsservicesystem.provider.OrderContract.Customers;
import com.aeon.flsservicesystem.provider.OrderContract.CustomersIndices;
import com.aeon.flsservicesystem.provider.OrderContract.Locations;
import com.aeon.flsservicesystem.provider.OrderContract.Orders;
import com.aeon.flsservicesystem.provider.OrderContract.Receipts;
import com.aeon.flsservicesystem.provider.OrderContract.SyncColumns;
import com.aeon.flsservicesystem.provider.OrderDatabase.Tables;


import java.util.ArrayList;
import java.util.Arrays;

public class OrderProvider extends ContentProvider {
    private static final String TAG = OrderProvider.class.getSimpleName();

    private OrderDatabase mOpenHelper;

    private OrderProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new OrderDatabase(getContext());
        mUriMatcher = new OrderProviderUriMatcher();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        OrderUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "uri=" + uri + " code=" + matchingUriEnum.code + " proj="
                    + Arrays.toString(projection) + " selection=" + selection + " args="
                    + Arrays.toString(selectionArgs) + ")");
        }

        switch (matchingUriEnum) {
            default: {
                // Most cases are handled with simple SelectionBuilder.
                final SelectionBuilder builder = buildExpandedSelection(uri, matchingUriEnum.code);

                boolean distinct = false;

                Cursor cursor = builder
                        .where(selection, selectionArgs)
                        .query(db, distinct, projection, sortOrder, null);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case CUSTOMERS_UNDONE: {
                Cursor cursor = db.rawQuery("SELECT "
                        + "t3." + Customers._ID + ","
                        + "t3." + Customers.CUSTOMER_IDCARD_NO + ","
                        + "t3." + Customers.CUSTOMER_NAME + ","
                        + "t3." + Customers.CUSTOMER_GENDER + ","
                        + "t3." + Customers.CUSTOMER_AGE + ","
                        + "t3." + Customers.CUSTOMER_ADDRESS + ","
                        + "t3." + Customers.CUSTOMER_ZIPCODE + ","
                        + "t3." + Customers.CUSTOMER_SECTION + ","
                        + "t3." + Customers.CUSTOMER_PHONE + ","
                        + "t3." + Customers.CUSTOMER_PHONE_EXT + ","
                        + "t3." + Customers.CUSTOMER_MOBILE + ","
                        + "t3." + Customers.CUSTOMER_DELN + ","
                        + "t3." + Customers.CUSTOMER_OS_BALANCE + ","
                        + "t3." + Customers.CUSTOMER_TOTAL_BILL + ","
                        + "t3." + SyncColumns.UPDATED_DATE + ","
                        + "COUNT(t1." + Orders.ORDER_AGREEMENT_NO + ") AS total_agreements,"
                        + "0 AS total_canceled_agreements,"
                        + "IFNULL(SUM(t1." + Orders.ORDER_COLLECT_AMOUNT + "), 0) AS total_collect_amount,"
                        + "SUM(CASE WHEN t1." + Orders.ORDER_PRIORITY + " = 'U' THEN 1 ELSE 0 END) AS total_urgent_agreements,"
                        + "t1." + Orders.ORDER_CLIENT_NAME_EN + ","
                        + "t1." + Orders.ORDER_CLIENT_NAME_TH + ","
                        + "t1." + Orders.ORDER_CLIENT_CONTACT_NO + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ","
                        + "t1." + Orders.ORDER_AUTOCALL_REMARK + ","
                        + "t3." + Customers.CUSTOMER_LAT + ","
                        + "t3." + Customers.CUSTOMER_LON + ","
                        + "t3." + Customers.CUSTOMER_SURVEY_TYPE
                        + " FROM (SELECT * FROM " + Tables.ORDERS
                        + " GROUP BY " + Orders.ORDER_AGREEMENT_NO
                        + " ORDER BY CAST(" + Orders.ORDER_NO + " AS INTEGER) DESC) AS t1"
                        + " LEFT JOIN " + Tables.CUSTOMERS + " AS t3"
                        + " ON t1." + Orders.CUSTOMER_IDCARD_NO + " = t3." + Customers.CUSTOMER_IDCARD_NO
                        + " LEFT JOIN " + Tables.CUSTOMERS_INDICES + " AS t4"
                        + " ON t1." + Orders.CUSTOMER_IDCARD_NO + " = t4." + CustomersIndices.CUSTOMERS_INDICES_CUSTOMER_ID
                        + " WHERE 1=1"
//                        + " AND t1." + Orders.ORDER_STATUS + "='5'"
                        + " AND t1." + Orders.ORDER_STATUS + " IN (4,5)"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NULL"
//                        + " AND t1." + Orders.ORDER_COLLECT_DATE + " > (strftime('%s', 'now') - 2678400)"
                        + " GROUP BY t3." + Customers.CUSTOMER_IDCARD_NO
                        + " ORDER BY CASE WHEN t4." + CustomersIndices.CUSTOMERS_INDICES_NO
                        + " IS NULL THEN CASE WHEN t1." + Orders.ORDER_PRIORITY + " IS 'U' THEN 0 ELSE 2 END ELSE 1 END,"
                        + " t4." + CustomersIndices.CUSTOMERS_INDICES_NO + " ASC,"
                        + " CASE t1." + Orders.ORDER_PRIORITY + " WHEN 'U' THEN 0 WHEN 'N' THEN 1 END,"
                        + " t3." + SyncColumns.UPDATED_DATE + " DESC", null);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case CUSTOMERS_DONE: {
                Cursor cursor = db.rawQuery("SELECT t3." + Customers._ID + ","
                        + "t3." + Customers.CUSTOMER_IDCARD_NO + ","
                        + "t3." + Customers.CUSTOMER_NAME + ","
                        + "t3." + Customers.CUSTOMER_GENDER + ","
                        + "t3." + Customers.CUSTOMER_AGE + ","
                        + "t3." + Customers.CUSTOMER_ADDRESS + ","
                        + "t3." + Customers.CUSTOMER_ZIPCODE + ","
                        + "t3." + Customers.CUSTOMER_SECTION + ","
                        + "t3." + Customers.CUSTOMER_PHONE + ","
                        + "t3." + Customers.CUSTOMER_PHONE_EXT + ","
                        + "t3." + Customers.CUSTOMER_MOBILE + ","
                        + "t3." + Customers.CUSTOMER_DELN + ","
                        + "t3." + Customers.CUSTOMER_OS_BALANCE + ","
                        + "t3." + Customers.CUSTOMER_TOTAL_BILL + ","
                        + "t3." + SyncColumns.UPDATED_DATE + ","
                        + "COUNT(t1." + Orders.ORDER_AGREEMENT_NO + ") AS total_agreements,"
                        + "SUM(CASE WHEN t1." + Orders.ORDER_STATUS + " IN (97, 98, 99) THEN 1 ELSE 0 END) AS total_canceled_agreements,"
                        + "IFNULL(SUM(t1." + Orders.ORDER_RESULT_COLLECTED_AMOUNT + "), 0) AS total_collect_amount,"
                        + "0 AS total_urgent_agreements,"
                        + "t1." + Orders.ORDER_CLIENT_NAME_EN + ","
                        + "t1." + Orders.ORDER_CLIENT_NAME_TH + ","
                        + "t1." + Orders.ORDER_CLIENT_CONTACT_NO + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ","
                        + "t1." + Orders.ORDER_AUTOCALL_REMARK + ","
                        + "t3." + Customers.CUSTOMER_LAT + ","
                        + "t3." + Customers.CUSTOMER_LON + ","
                        + "t3." + Customers.CUSTOMER_SURVEY_TYPE
                        + " FROM (SELECT * FROM " + Tables.ORDERS
                        + " GROUP BY " + Orders.ORDER_AGREEMENT_NO
                        + " ORDER BY CAST(" + Orders.ORDER_NO + " AS INTEGER) DESC) AS t1"
                        + " LEFT JOIN " + Tables.CUSTOMERS + " AS t3"
                        + " ON t1." + Orders.CUSTOMER_IDCARD_NO + " = t3." + Customers.CUSTOMER_IDCARD_NO
                        + " WHERE 1=1"
                        + " AND t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ">=?"
                        + " AND t1." + Orders.ORDER_RESULT_COLLECTED_DATE + "<=?"
                        + " AND ((t1." + Orders.ORDER_STATUS + " IN (89, 97, 98, 99)"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NULL)"
                        + " OR (t1." + Orders.ORDER_STATUS + " = '5'"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NOT NULL))"
                        + " GROUP BY t3." + Customers.CUSTOMER_IDCARD_NO
                        + " ORDER BY t1." + Orders.ORDER_RESULT_COLLECTED_DATE + " DESC", selectionArgs);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case ORDERS_UNDONE: {
                Cursor cursor = db.rawQuery("SELECT t1." + Orders.ORDER_NO + ","
                        + "t1." + Orders.CUSTOMER_IDCARD_NO + ","
                        + "t1." + Orders.ORDER_AGREEMENT_NO + ","
                        + "t1." + Orders.ORDER_GUID + ","
                        + "t1." + Orders.ORDER_DESCRIPTION + ","
                        + "t1." + Orders.ORDER_SURVEY_NAME + ","
                        + "t1." + Orders.ORDER_COLLECT_DATE + ","
                        + "t1." + Orders.ORDER_COLLECT_AMOUNT + ","
                        + "t1." + Orders.ORDER_STATUS + ","
                        + "t1." + Orders.ORDER_TASK_TYPE + ","
                        + "t1." + Orders.ORDER_PRIORITY + ","
                        + "t1." + Orders.ORDER_OPERATOR_NAME + ","
                        + "t1." + Orders.ORDER_AUTOCALL_REMARK + ","
                        + "t1." + Orders.ORDER_DELINQUENT_STATUS + ","
                        + "t1." + Orders.ORDER_OUTSTANDING_BALANCE + ","
                        + "t1." + Orders.ORDER_PENALTY + ","
                        + "t1." + Orders.ORDER_CURRENT_BILL + ","
                        + "t1." + Orders.ORDER_D1 + ","
                        + "t1." + Orders.ORDER_D1_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D2 + ","
                        + "t1." + Orders.ORDER_D2_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D3 + ","
                        + "t1." + Orders.ORDER_D3_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D4 + ","
                        + "t1." + Orders.ORDER_D4_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D5 + ","
                        + "t1." + Orders.ORDER_D5_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_TOTAL_DELINQUENT + ","
                        + "t1." + Orders.ORDER_TOTAL_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_MINIMUM_BILL + ","
                        + "t1." + Orders.ORDER_FULL_BILL + ","
                        + "t1." + Orders.COLLECTOR_RESULT_CODE + ","
                        + "\"\" AS " + CollectorResults.COLLECTOR_RESULT_NAME + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_AMOUNT + ","
                        + "t1." + Orders.ORDER_RESULT_PROMISED_DATE + ","
                        + "t1." + Orders.ORDER_RESULT_REMARK + ","
                        + "t1." + SyncColumns.UPDATED_FLAG + ","
                        + "t1." + Orders.ORDER_RESULT_SEND_TO_AUTOCALL_FLAG + ","
                        + "t1." + Orders.ORDER_SURVEY_CODE + ","
                        + "t1." + Orders.ORDER_TOKEN + ","
                        + "t1." + Orders.ORDER_CLIENT_NAME_EN + ","
                        + "t1." + Orders.ORDER_CLIENT_NAME_TH + ","
                        + "t1." + Orders.ORDER_CLIENT_CONTACT_NO
                        + " FROM (SELECT * FROM " + Tables.ORDERS
                        + " WHERE " + Orders.CUSTOMER_IDCARD_NO + "=?"
                        + " GROUP BY " + Orders.ORDER_AGREEMENT_NO
                        + " ORDER BY CAST(" + Orders.ORDER_NO + " AS INTEGER) DESC) AS t1"
                        + " WHERE 1=1"
//                        + " AND t1." + Orders.ORDER_STATUS + "='5'"
                        + " AND t1." + Orders.ORDER_STATUS + " IN (4,5)"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NULL"
//                        + " AND t1." + Orders.ORDER_COLLECT_DATE + " > (strftime('%s', 'now') - 2678400)"
                        + " ORDER BY t1." + Orders.ORDER_SURVEY_PRIORITY + " ASC", selectionArgs);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case ORDERS_DONE: {
                Cursor cursor = db.rawQuery("SELECT t1." + Orders.ORDER_NO + ","
                        + "t1." + Orders.CUSTOMER_IDCARD_NO + ","
                        + "t1." + Orders.ORDER_AGREEMENT_NO + ","
                        + "t1." + Orders.ORDER_GUID + ","
                        + "t1." + Orders.ORDER_DESCRIPTION + ","
                        + "t1." + Orders.ORDER_SURVEY_NAME + ","
                        + "t1." + Orders.ORDER_COLLECT_DATE + ","
                        + "t1." + Orders.ORDER_COLLECT_AMOUNT + ","
                        + "t1." + Orders.ORDER_STATUS + ","
                        + "t1." + Orders.ORDER_TASK_TYPE + ","
                        + "t1." + Orders.ORDER_PRIORITY + ","
                        + "t1." + Orders.ORDER_OPERATOR_NAME + ","
                        + "t1." + Orders.ORDER_AUTOCALL_REMARK + ","
                        + "t1." + Orders.ORDER_DELINQUENT_STATUS + ","
                        + "t1." + Orders.ORDER_OUTSTANDING_BALANCE + ","
                        + "t1." + Orders.ORDER_PENALTY + ","
                        + "t1." + Orders.ORDER_CURRENT_BILL + ","
                        + "t1." + Orders.ORDER_D1 + ","
                        + "t1." + Orders.ORDER_D1_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D2 + ","
                        + "t1." + Orders.ORDER_D2_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D3 + ","
                        + "t1." + Orders.ORDER_D3_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D4 + ","
                        + "t1." + Orders.ORDER_D4_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_D5 + ","
                        + "t1." + Orders.ORDER_D5_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_TOTAL_DELINQUENT + ","
                        + "t1." + Orders.ORDER_TOTAL_ADD_PENALTY + ","
                        + "t1." + Orders.ORDER_MINIMUM_BILL + ","
                        + "t1." + Orders.ORDER_FULL_BILL + ","
                        + "t1." + Orders.COLLECTOR_RESULT_CODE + ","
                        + "t3." + CollectorResults.COLLECTOR_RESULT_NAME + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_AMOUNT + ","
                        + "t1." + Orders.ORDER_RESULT_PROMISED_DATE + ","
                        + "t1." + Orders.ORDER_RESULT_REMARK + ","
                        + "t1." + SyncColumns.UPDATED_FLAG + ","
                        + "t1." + Orders.ORDER_RESULT_SEND_TO_AUTOCALL_FLAG + ","
                        + "t1." + Orders.ORDER_SURVEY_CODE + ","
                        + "t1." + Orders.ORDER_TOKEN + ","
                        + "t1." + Orders.ORDER_CLIENT_NAME_EN + ","
                        + "t1." + Orders.ORDER_CLIENT_NAME_TH + ","
                        + "t1." + Orders.ORDER_CLIENT_CONTACT_NO
                        + " FROM (SELECT * FROM " + Tables.ORDERS
                        + " WHERE " + Orders.CUSTOMER_IDCARD_NO + "=?"
                        + " GROUP BY " + Orders.ORDER_AGREEMENT_NO
                        + " ORDER BY CAST(" + Orders.ORDER_NO + " AS INTEGER) DESC) AS t1"
                        + " LEFT JOIN " + Tables.COLLECTOR_RESULTS + " AS t3"
                        + " ON t1." + Orders.COLLECTOR_RESULT_CODE + " = t3." + CollectorResults.COLLECTOR_RESULT_CODE
                        + " WHERE 1=1"
                        + " AND t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ">=?"
                        + " AND t1." + Orders.ORDER_RESULT_COLLECTED_DATE + "<=?"
                        + " AND ((t1." + Orders.ORDER_STATUS + " IN (89, 97, 98, 99)"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NULL)"
                        + " OR (t1." + Orders.ORDER_STATUS + " = '5'"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NOT NULL))"
                        + " ORDER BY t1." + Orders.ORDER_RESULT_COLLECTED_DATE + " DESC", selectionArgs);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case ORDERS_SEND_AUTOCALL: {
                Cursor cursor = db.rawQuery("SELECT t1." + Orders.ORDER_NO + ","
                        + "t1." + Orders.ORDER_GUID + ","
                        + "t1." + Orders.ORDER_AGREEMENT_NO + ","
                        + "t1." + Orders.CUSTOMER_IDCARD_NO + ","
                        + "t1." + Orders.COLLECTOR_RESULT_CODE + ","
                        + "t1." + Orders.ORDER_RESULT_EMP_CODE + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_DATE + ","
                        + "t1." + Orders.ORDER_RESULT_COLLECTED_AMOUNT + ","
                        + "t1." + Orders.ORDER_RESULT_REMARK + ","
                        + "t1." + Orders.ORDER_RESULT_PROMISED_DATE + ","
                        + "t2." + CollectorResults.COLLECTOR_RESULT_NAME + ","
                        + "t2." + CollectorResults.COLLECTOR_RESULT_TYPE
                        + " FROM " + Tables.ORDERS + " AS t1"
                        + " LEFT JOIN " + Tables.COLLECTOR_RESULTS + " AS t2"
                        + " ON t1." + Orders.COLLECTOR_RESULT_CODE + " = t2." + CollectorResults.COLLECTOR_RESULT_CODE
                        + " WHERE t1." + Orders.ORDER_RESULT_SEND_TO_AUTOCALL_FLAG + " = 1"
                        + " AND t1." + Orders.COLLECTOR_RESULT_CODE + " IS NOT NULL"
                        + " ORDER BY t1." + Orders.ORDER_RESULT_COLLECTED_DATE + " ASC", null);

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case COLLECTOR_RESULTS_ACTIVE: {
                Cursor cursor = db.rawQuery(
                        "SELECT * "
                                + " FROM " + Tables.COLLECTOR_RESULTS
                                + " WHERE " + CollectorResults.COLLECTOR_RESULT_STATUS + " = 1", null);

                Context context = getContext();
                if (context != null) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case COLLECTOR_RESULTS_ACTIVE_EXCLUDE_GET_MONEY: {
                Cursor cursor = db.rawQuery(
                        "SELECT * "
                                + " FROM " + Tables.COLLECTOR_RESULTS
                                + " WHERE " + CollectorResults.COLLECTOR_RESULT_STATUS + " = 1"
                                + " AND " + CollectorResults.COLLECTOR_RESULT_GET_MONEY + " = 1", null);

                Context context = getContext();
                if (context != null) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
            case LOCATIONS_LATEST: {
                Cursor cursor = db.query(Tables.LOCATIONS, projection, selection, selectionArgs,
                        null, null, Locations.LOCATION_TIME_LATEST_SORT, "1");

                Context context = getContext();
                if (null != context) {
                    cursor.setNotificationUri(context.getContentResolver(), uri);
                }
                return cursor;
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        OrderUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        return matchingUriEnum.contentType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        OrderUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        if (matchingUriEnum.table != null) {
            db.insertOrThrow(matchingUriEnum.table, null, values);
            notifyChange(uri);
        }

        switch (matchingUriEnum) {
            case COLLECTOR_RESULTS: {
                return CollectorResults.buildCollectorResultUri(
                        values.getAsString(CollectorResults._ID));
            }
            case CUSTOMERS: {
                return Customers.buildCustomerUri(values.getAsString(Customers._ID));
            }
            case ORDERS: {
                notifyChange(uri);
                return Orders.buildOrderUri(values.getAsString(Orders._ID));
            }
            case RECEIPTS: {
                return Receipts.buildReceiptUri(values.getAsString(Receipts._ID));
            }
            case LOCATIONS: {
                return Locations.buildLocationUri(values.getAsString(Locations._ID));
            }
            case CUSTOMERS_INDICES: {
                return CustomersIndices.buildCustomersIndicesUri(
                        values.getAsString(CustomersIndices._ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        OrderUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        if (retVal > 0) {
            notifyChange(uri);
        }
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        OrderUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);

        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null);
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        OrderUriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        switch (matchingUriEnum) {
            case COLLECTOR_RESULTS: {
                return builder.table(matchingUriEnum.table);
            }
            case CUSTOMERS: {
                return builder.table(matchingUriEnum.table);
            }
            case ORDERS: {
                return builder.table(matchingUriEnum.table);
            }
            case RECEIPTS: {
                return builder.table(matchingUriEnum.table);
            }
            case LOCATIONS: {
                return builder.table(matchingUriEnum.table);
            }
            case CUSTOMERS_INDICES: {
                return builder.table(matchingUriEnum.table);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        OrderUriEnum matchingUriEnum = mUriMatcher.matchCode(match);
        if (matchingUriEnum == null) {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        switch (matchingUriEnum) {
            case COLLECTOR_RESULTS: {
                return builder.table(Tables.COLLECTOR_RESULTS);
            }
            case CUSTOMERS: {
                return builder.table(Tables.CUSTOMERS);
            }
            case ORDERS: {
                return builder.table(Tables.ORDERS);
            }
            case ORDERS_GUID: {
                final String orderGuid = Orders.getOrderGuid(uri);
                return builder.table(Tables.ORDERS).where(Orders.ORDER_GUID + "=?", orderGuid);
            }
            case RECEIPTS: {
                return builder.table(Tables.RECEIPTS);
            }
            case LOCATIONS: {
                return builder.table(Tables.LOCATIONS);
            }
            case CUSTOMERS_INDICES: {
                return builder.table(Tables.CUSTOMERS_INDICES);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
