package com.aeon.flsservicesystem.provider;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.aeon.flsservicesystem.provider.OrderContract.CollectorResultsColumns;
import com.aeon.flsservicesystem.provider.OrderContract.CustomersColumns;
import com.aeon.flsservicesystem.provider.OrderContract.CustomersIndicesColumns;
import com.aeon.flsservicesystem.provider.OrderContract.LocationsColumns;
import com.aeon.flsservicesystem.provider.OrderContract.OrdersColumns;
import com.aeon.flsservicesystem.provider.OrderContract.ReceiptsColumns;
import com.aeon.flsservicesystem.provider.OrderContract.SyncColumns;

import timber.log.Timber;

public class OrderDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "order.db";

    private static final int VER_2016_RELEASE_A = 1;
    private static final int VER_2017_RELEASE_A = 2;
    private static final int VER_2017_RELEASE_B = 3;
    private static final int VER_2018_RELEASE_A = 4;
    private static final int VER_2018_RELEASE_B = 5;

    private static final int VER_2023_RELEASE_A = 6;

    private static final int CUR_DATABASE_VERSION = VER_2023_RELEASE_A;

    interface Tables {
        String CUSTOMERS = "customers";
        String ORDERS = "orders";
        String RECEIPTS = "receipts";
        String COLLECTOR_RESULTS = "collector_results";
        String LOCATIONS = "locations";
        String CUSTOMERS_INDICES = "customers_indices";
    }

    public OrderDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.CUSTOMERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SyncColumns.UPDATED_DATE + " INTEGER,"
                + CustomersColumns.CUSTOMER_IDCARD_NO + " TEXT NOT NULL,"
                + CustomersColumns.CUSTOMER_NAME + " TEXT NOT NULL,"
                + CustomersColumns.CUSTOMER_GENDER + " TEXT,"
                + CustomersColumns.CUSTOMER_AGE + " TEXT,"
                + CustomersColumns.CUSTOMER_ADDRESS + " TEXT NOT NULL,"
                + CustomersColumns.CUSTOMER_ZIPCODE + " TEXT,"
                + CustomersColumns.CUSTOMER_SECTION + " TEXT,"
                + CustomersColumns.CUSTOMER_PHONE + " TEXT,"
                + CustomersColumns.CUSTOMER_PHONE_EXT + " TEXT,"
                + CustomersColumns.CUSTOMER_MOBILE + " TEXT,"
                + CustomersColumns.CUSTOMER_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
                + "UNIQUE (" + CustomersColumns.CUSTOMER_IDCARD_NO + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.ORDERS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SyncColumns.UPDATED_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + SyncColumns.UPDATED_DATE + " INTEGER,"
                + OrdersColumns.CUSTOMER_IDCARD_NO + " TEXT NOT NULL,"
                + OrdersColumns.COLLECTOR_RESULT_CODE + " TEXT,"
                + OrdersColumns.ORDER_NO + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_PLAN_NO + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_PLAN_SEQ_NO + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_GUID + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_AGREEMENT_NO + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_DESCRIPTION + " TEXT,"
                + OrdersColumns.ORDER_SURVEY_NAME + " TEXT NOT NULL DEFAULT '',"
                + OrdersColumns.ORDER_SURVEY_PRIORITY + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_PLAN_SEQUENCE + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_COLLECT_DATE + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_COLLECT_AMOUNT + " INTEGER NOT NULL DEFAULT 0,"
                + OrdersColumns.ORDER_STATUS + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_TASK_TYPE + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_PRIORITY + " TEXT NOT NULL,"
                + OrdersColumns.ORDER_OPERATOR_NAME + " TEXT NOT NULL DEFAULT '',"
                + OrdersColumns.ORDER_AUTOCALL_REMARK + " TEXT NOT NULL DEFAULT '',"
                + OrdersColumns.ORDER_DELINQUENT_STATUS + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_OUTSTANDING_BALANCE + " INTEGER,"
                + OrdersColumns.ORDER_PENALTY + " INTEGER,"
                + OrdersColumns.ORDER_CURRENT_BILL + " INTEGER,"
                + OrdersColumns.ORDER_D1 + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D1_ADD_PENALTY + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D2 + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D2_ADD_PENALTY + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D3 + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D3_ADD_PENALTY + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D4 + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D4_ADD_PENALTY + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D5 + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_D5_ADD_PENALTY + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_TOTAL_DELINQUENT + " INTEGER,"
                + OrdersColumns.ORDER_TOTAL_ADD_PENALTY + " INTEGER,"
                + OrdersColumns.ORDER_PAYMENT_CODE + " INTEGER,"
                + OrdersColumns.ORDER_MINIMUM_BILL + " INTEGER,"
                + OrdersColumns.ORDER_FULL_BILL + " INTEGER,"
                + OrdersColumns.ORDER_RECEIVED_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + OrdersColumns.ORDER_UPDATE_RECEIVED_DATE + " INTEGER NOT NULL,"
                + OrdersColumns.ORDER_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
                + OrdersColumns.ORDER_RESULT_EMP_CODE + " TEXT,"
                + OrdersColumns.ORDER_RESULT_COLLECTED_DATE + " INTEGER,"
                + OrdersColumns.ORDER_RESULT_COLLECTED_AMOUNT + " INTEGER,"
                + OrdersColumns.ORDER_RESULT_PROMISED_DATE + " INTEGER,"
                + OrdersColumns.ORDER_RESULT_REMARK + " TEXT,"
                + OrdersColumns.ORDER_RESULT_LAT + " DOUBLE,"
                + OrdersColumns.ORDER_RESULT_LON + " DOUBLE,"
                + OrdersColumns.ORDER_RESULT_SIGNAL + " INTEGER,"
                + OrdersColumns.ORDER_RESULT_SEND_TO_AUTOCALL_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + OrdersColumns.ORDER_RESULT_SEND_TO_AUTOCALL_DATE + " INTEGER,"
                + OrdersColumns.ORDER_RESULT_SEND_TRACKING_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + OrdersColumns.ORDER_RESULT_SEND_TRACKING_DATE + " INTEGER,"
                + "UNIQUE (" + OrdersColumns.ORDER_NO + ") ON CONFLICT IGNORE)");

        db.execSQL("CREATE TABLE " + Tables.RECEIPTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReceiptsColumns.GUID + " TEXT NOT NULL,"
                + ReceiptsColumns.AGREEMENT_NO + " TEXT NOT NULL,"
                + ReceiptsColumns.RECEIPT_NO + " TEXT NOT NULL,"
                + ReceiptsColumns.RECEIPT_AMOUNT + " INTEGER NOT NULL,"
                + ReceiptsColumns.RECEIPT_STATUS + " TEXT NOT NULL,"
                + ReceiptsColumns.RECEIPT_PRINTED_DATE + " INTEGER NOT NULL,"
                + SyncColumns.UPDATED_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + SyncColumns.UPDATED_DATE + " INTEGER NOT NULL,"
                + "UNIQUE (" + ReceiptsColumns.RECEIPT_NO + ","
                + ReceiptsColumns.RECEIPT_STATUS + ","
                + ReceiptsColumns.RECEIPT_PRINTED_DATE + ") ON CONFLICT IGNORE)");

        db.execSQL("CREATE TABLE " + Tables.COLLECTOR_RESULTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CollectorResultsColumns.COLLECTOR_RESULT_CODE + " TEXT NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_NAME + " TEXT NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_TYPE + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_PRINT_RECEIPT + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_PRINT_LETTER + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_GET_MONEY + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_MAX_PAYMENT + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_MIN_PAYMENT + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_REQUIRED_REMARK + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_GROUPING_ORDER + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_STATUS + " INTEGER NOT NULL,"
                + CollectorResultsColumns.COLLECTOR_RESULT_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
                + "UNIQUE (" + CollectorResultsColumns.COLLECTOR_RESULT_CODE + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.LOCATIONS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SyncColumns.UPDATED_FLAG + " INTEGER NOT NULL DEFAULT 0,"
                + SyncColumns.UPDATED_DATE + " INTEGER,"
                + LocationsColumns.ORDER_NO + " TEXT NOT NULL,"
                + LocationsColumns.LOCATION_TIME + " INTEGER NOT NULL,"
                + LocationsColumns.LOCATION_LATITUDE + " DOUBLE NOT NULL,"
                + LocationsColumns.LOCATION_LONGITUDE + " DOUBLE NOT NULL,"
                + LocationsColumns.LOCATION_BATTERY + " INTEGER NOT NULL,"
                + LocationsColumns.LOCATION_SPEED + " DOUBLE NOT NULL,"
                + LocationsColumns.LOCATION_PROVIDER + " TEXT NOT NULL,"
                + LocationsColumns.LOCATION_EMPLOYEE_CODE + " TEXT,"
                + "UNIQUE (" + LocationsColumns.LOCATION_TIME + ","
                + LocationsColumns.ORDER_NO + ") ON CONFLICT IGNORE)");

        upgradeFrom2016ATo2017A(db);
        upgradeFrom2017ATo2017B(db);
        upgradeFrom2017BTo2018A(db);
        upgradeFrom2018BTo2023A(db);
    }

    private void upgradeFrom2016ATo2017A(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.CUSTOMERS_INDICES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CustomersIndicesColumns.CUSTOMERS_INDICES_CUSTOMER_ID + " TEXT NOT NULL,"
                + CustomersIndicesColumns.CUSTOMERS_INDICES_NO + " INTEGER NOT NULL,"
                + "UNIQUE (" + CustomersIndicesColumns.CUSTOMERS_INDICES_CUSTOMER_ID
                + ") ON CONFLICT REPLACE)");
    }

    private void upgradeFrom2017ATo2017B(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Tables.COLLECTOR_RESULTS
                + " ADD COLUMN " + CollectorResultsColumns.COLLECTOR_RESULT_GROUP_NAME + " TEXT");

        db.execSQL("ALTER TABLE " + Tables.CUSTOMERS
                + " ADD COLUMN " + CustomersColumns.CUSTOMER_DELN + " TEXT");
        db.execSQL("ALTER TABLE " + Tables.CUSTOMERS
                + " ADD COLUMN " + CustomersColumns.CUSTOMER_OS_BALANCE + " TEXT");
        db.execSQL("ALTER TABLE " + Tables.CUSTOMERS
                + " ADD COLUMN " + CustomersColumns.CUSTOMER_TOTAL_BILL + " TEXT");

        db.execSQL("ALTER TABLE " + Tables.ORDERS
                + " ADD COLUMN " + OrdersColumns.ORDER_TOKEN + " TEXT");
        db.execSQL("ALTER TABLE " + Tables.ORDERS
                + " ADD COLUMN " + OrdersColumns.ORDER_SURVEY_CODE + " TEXT");
    }

    private void upgradeFrom2017BTo2018A(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Tables.ORDERS
                + " ADD COLUMN " + OrdersColumns.ORDER_CLIENT_NAME_TH + " TEXT");
        db.execSQL("ALTER TABLE " + Tables.ORDERS
                + " ADD COLUMN " + OrdersColumns.ORDER_CLIENT_NAME_EN + " TEXT");
        db.execSQL("ALTER TABLE " + Tables.ORDERS
                + " ADD COLUMN " + OrdersColumns.ORDER_CLIENT_CONTACT_NO + " TEXT");
    }

    private void upgradeFrom2018ATo2018B(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + Tables.ORDERS
                    + " ADD COLUMN " + OrdersColumns.ORDER_CLIENT_NAME_TH + " TEXT");
        } catch (SQLException e) {
            Timber.i("Column " + OrdersColumns.ORDER_CLIENT_NAME_TH + " already exist.");
        }

        try {
            db.execSQL("ALTER TABLE " + Tables.ORDERS
                    + " ADD COLUMN " + OrdersColumns.ORDER_CLIENT_NAME_EN + " TEXT");
        } catch (SQLException e) {
            Timber.i("Column " + OrdersColumns.ORDER_CLIENT_NAME_EN + " already exist.");
        }

        try {
            db.execSQL("ALTER TABLE " + Tables.ORDERS
                    + " ADD COLUMN " + OrdersColumns.ORDER_CLIENT_CONTACT_NO + " TEXT");
        } catch (SQLException e) {
            Timber.i("Column " + OrdersColumns.ORDER_CLIENT_CONTACT_NO + " already exist.");
        }
    }

    private void upgradeFrom2018BTo2023A(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + Tables.CUSTOMERS
                    + " ADD COLUMN " + CustomersColumns.CUSTOMER_SURVEY_TYPE + " TEXT");
        } catch (SQLException e) {
            Timber.i("Column " + CustomersColumns.CUSTOMER_SURVEY_TYPE + " already exist.");
        }

        try {
            db.execSQL("ALTER TABLE " + Tables.CUSTOMERS
                    + " ADD COLUMN " + CustomersColumns.CUSTOMER_LAT + " DOUBLE");
        } catch (SQLException e) {
            Timber.i("Column " + CustomersColumns.CUSTOMER_LAT + " already exist.");
        }

        try {
            db.execSQL("ALTER TABLE " + Tables.CUSTOMERS
                    + " ADD COLUMN " + CustomersColumns.CUSTOMER_LON + " DOUBLE");
        } catch (SQLException e) {
            Timber.i("Column " + CustomersColumns.CUSTOMER_LON + " already exist.");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.d("onUpgrade() from " + oldVersion + " to " + newVersion);

        int version = oldVersion;

        if (version == VER_2016_RELEASE_A) {
            Timber.d("Upgrading database from 2016 release A to 2017 release A.");
            upgradeFrom2016ATo2017A(db);
            version = VER_2017_RELEASE_A;
        }

        if (version == VER_2017_RELEASE_A) {
            Timber.d("Upgrading database from 2017 release A to 2017 release B.");
            upgradeFrom2017ATo2017B(db);
            version = VER_2017_RELEASE_B;
        }

        if (version == VER_2017_RELEASE_B) {
            Timber.d("Upgrading database from 2017 release B to 2018 release A.");
            upgradeFrom2017BTo2018A(db);
            version = VER_2018_RELEASE_A;
        }

        if (version == VER_2018_RELEASE_A) {
            Timber.d("Upgrading database from 2018 release A to 2018 release B.");
            upgradeFrom2018ATo2018B(db);
            version = VER_2018_RELEASE_B;
        }

        if (version == VER_2018_RELEASE_B) {
            Timber.d("Upgrading database from 2018 release B to 2023 release A.");
            upgradeFrom2018BTo2023A(db);
            version = VER_2023_RELEASE_A;
        }

        Timber.d("After upgrade logic, at version %s", version);

        if (version != CUR_DATABASE_VERSION) {
            Timber.w("Upgrade unsuccessful -- destroying old data during upgrade");
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
