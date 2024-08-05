package com.aeon.flsservicesystem.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class OrderContract {

    public static final String CONTENT_TYPE_APP_BASE = "flsservicesystem.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    public interface SyncColumns {
        String UPDATED_FLAG = "updated_flag";
        String UPDATED_DATE = "updated_date";
    }

    interface CustomersColumns {
        String CUSTOMER_IDCARD_NO = "customer_idcard_no";
        String CUSTOMER_NAME = "customer_name";
        String CUSTOMER_GENDER = "customer_gender";
        String CUSTOMER_AGE = "customer_age";
        String CUSTOMER_ADDRESS = "customer_address";
        String CUSTOMER_ZIPCODE = "customer_zipcode";
        String CUSTOMER_SECTION = "customer_section";
        String CUSTOMER_PHONE = "customer_phone";
        String CUSTOMER_PHONE_EXT = "customer_phone_ext";
        String CUSTOMER_MOBILE = "customer_mobile";
        String CUSTOMER_DELN = "customer_deln";
        String CUSTOMER_OS_BALANCE = "customer_os_balance";
        String CUSTOMER_TOTAL_BILL = "customer_total_bill";
        String CUSTOMER_IMPORT_HASHCODE = "customer_import_hashcode";
        /** custom columns */
        String CUSTOMER_TOTAL_AGREEMENTS = "total_agreements";
        String CUSTOMER_TOTAL_URGENT_AGREEMENTS = "total_urgent_agreements";
        String CUSTOMER_TOTAL_CANCELED_AGREEMENTS = "total_canceled_agreements";
        String CUSTOMER_TOTAL_COLLECT_AMOUNT = "total_collect_amount";

        String CUSTOMER_SURVEY_TYPE = "customer_survey_type";

        String CUSTOMER_LAT = "customer_latitude";

        String CUSTOMER_LON = "customer_longitude";
    }

    interface OrdersColumns {
        String CUSTOMER_IDCARD_NO = CustomersColumns.CUSTOMER_IDCARD_NO;
        String COLLECTOR_RESULT_CODE = CollectorResultsColumns.COLLECTOR_RESULT_CODE;
        String ORDER_NO = "order_no";
        String ORDER_PLAN_NO = "order_plan_no";
        String ORDER_PLAN_SEQ_NO = "order_plan_seq_no";
        String ORDER_GUID = "order_guid";
        String ORDER_AGREEMENT_NO = "order_agreement_no";
//        String ORDER_AGREEMENT_NO_FULL_PAN = "order_agreement_no_full_pan";
        String ORDER_TOKEN = "order_token";
        String ORDER_DESCRIPTION = "order_description";
        String ORDER_SURVEY_CODE = "order_survey_code";
        String ORDER_SURVEY_NAME = "order_survey_name";
        String ORDER_SURVEY_PRIORITY = "order_survey_priority";
        String ORDER_PLAN_SEQUENCE = "order_plan_sequence";
        String ORDER_COLLECT_DATE = "order_collect_date";
        String ORDER_COLLECT_AMOUNT = "order_collect_amount";
        String ORDER_STATUS = "order_status";
        String ORDER_TASK_TYPE = "order_task_type";
        String ORDER_PRIORITY = "order_priority";
        String ORDER_OPERATOR_NAME = "order_operator_name";
        String ORDER_AUTOCALL_REMARK = "order_autocall_remark";
        String ORDER_DELINQUENT_STATUS = "order_delinquent_status";
        String ORDER_OUTSTANDING_BALANCE = "order_outstanding_balance";
        String ORDER_PENALTY = "order_penalty";
        String ORDER_CURRENT_BILL = "order_current_bill";
        String ORDER_D1 = "order_d1";
        String ORDER_D1_ADD_PENALTY = "order_d1_add_penalty";
        String ORDER_D2 = "order_d2";
        String ORDER_D2_ADD_PENALTY = "order_d2_add_penalty";
        String ORDER_D3 = "order_d3";
        String ORDER_D3_ADD_PENALTY = "order_d3_add_penalty";
        String ORDER_D4 = "order_d4";
        String ORDER_D4_ADD_PENALTY = "order_d4_add_penalty";
        String ORDER_D5 = "order_d5";
        String ORDER_D5_ADD_PENALTY = "order_d5_add_penalty";
        String ORDER_TOTAL_DELINQUENT = "order_total_delinquent";
        String ORDER_TOTAL_ADD_PENALTY = "order_total_add_penalty";
        String ORDER_PAYMENT_CODE = "order_payment_code";
        String ORDER_MINIMUM_BILL = "order_minimum_bill";
        String ORDER_FULL_BILL = "order_full_bill";
        String ORDER_RECEIVED_FLAG = "order_received_flag";
        String ORDER_UPDATE_RECEIVED_DATE = "order_update_received_date";
        String ORDER_IMPORT_HASHCODE = "order_import_hashcode";
        String ORDER_RESULT_EMP_CODE = "order_result_emp_code";
        String ORDER_RESULT_COLLECTED_DATE = "order_result_collected_date";
        String ORDER_RESULT_COLLECTED_AMOUNT = "order_result_collected_amount";
        String ORDER_RESULT_PROMISED_DATE = "order_result_promised_date";
        String ORDER_RESULT_REMARK = "order_result_remark";
        String ORDER_RESULT_LAT = "order_result_lat";
        String ORDER_RESULT_LON = "order_result_lon";
        String ORDER_RESULT_SIGNAL = "order_result_signal";
        String ORDER_RESULT_SEND_TO_AUTOCALL_FLAG = "order_result_send_to_autocall_flag";
        String ORDER_RESULT_SEND_TO_AUTOCALL_DATE = "order_result_send_to_autocall_date";
        String ORDER_RESULT_SEND_TRACKING_FLAG = "order_result_send_tracking_flag";
        String ORDER_RESULT_SEND_TRACKING_DATE = "order_result_send_tracking_date";
        String ORDER_CLIENT_NAME_TH = "order_client_name_th";
        String ORDER_CLIENT_NAME_EN = "order_client_name_en";
        String ORDER_CLIENT_CONTACT_NO = "order_client_contact_no";
    }

    interface ReceiptsColumns {
        String GUID = OrdersColumns.ORDER_GUID;
        String AGREEMENT_NO = OrdersColumns.ORDER_AGREEMENT_NO;
        String RECEIPT_NO = "receipt_no";
        String RECEIPT_AMOUNT = "receipt_amount";
        String RECEIPT_STATUS = "receipt_status";
        String RECEIPT_PRINTED_DATE = "receipt_printed_date";
    }

    interface CollectorResultsColumns {
        /** Unique code identifying collector result */
        String COLLECTOR_RESULT_CODE = "collector_result_code";
        /** The names of collector result */
        String COLLECTOR_RESULT_NAME = "collector_result_name";
        /** Flag collector result type (0 loop, 1 end). */
        String COLLECTOR_RESULT_TYPE = "collector_result_type";
        /** Flag print receipt (0 print, 1 not print). */
        String COLLECTOR_RESULT_PRINT_RECEIPT = "collector_result_print_receipt";
        /** Flag print letter (0 print, 1 not print). */
        String COLLECTOR_RESULT_PRINT_LETTER = "collector_result_print_letter";
        /** Flag require get money (0 get money, 1 not to get money). */
        String COLLECTOR_RESULT_GET_MONEY = "collector_result_get_money";
        /**
         * Flag maximum payment status
         * (1 nothing, 2 minimum bill, 3 full bill, 4 amount of bill, 5 = 0, 6 = 1).
         */
        String COLLECTOR_RESULT_MAX_PAYMENT = "collector_result_max_payment";
        /**
         * Flag minimum payment status
         * (1 nothing, 2 minimum bill, 3 full bill, 4 amount of bill, 5 = 0, 6 = 1).
         */
        String COLLECTOR_RESULT_MIN_PAYMENT = "collector_result_min_payment";
        /** Flag required remark (0 required, 1 not required). */
        String COLLECTOR_RESULT_REQUIRED_REMARK = "collector_result_required_remark";
        /** Priority of collector result (1 -> 5 less is more important). */
        String COLLECTOR_RESULT_GROUPING_ORDER = "collector_result_grouping_order";
        /** Flag status is used (1 is used, 2 not used). */
        String COLLECTOR_RESULT_STATUS = "collector_result_status";
        /** The hashcode of the data used to compare with data from webservice. */
        String COLLECTOR_RESULT_IMPORT_HASHCODE = "collector_result_import_hashcode";
        /** The names of collector result group */
        String COLLECTOR_RESULT_GROUP_NAME = "collector_result_group_name";
    }

    interface LocationsColumns {
        String ORDER_NO = OrdersColumns.ORDER_NO;
        String LOCATION_TIME = "location_time";
        String LOCATION_LATITUDE = "location_latitude";
        String LOCATION_LONGITUDE = "location_longitude";
        String LOCATION_BATTERY = "location_battery";
        String LOCATION_SPEED = "location_speed";
        String LOCATION_PROVIDER = "location_provider";
        String LOCATION_EMPLOYEE_CODE = "location_employee_code";
        String LOCATION_NOTE = "location_note";
    }

    interface CustomersIndicesColumns {
        String CUSTOMERS_INDICES_CUSTOMER_ID = "customers_indices_customer_id";
        String CUSTOMERS_INDICES_NO = "customers_indices_no";
    }

    public static final String CONTENT_AUTHORITY = "com.aeon.flsservicesystem.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_CUSTOMERS = "customers";

    private static final String PATH_CUSTOMERS_UNDONE = "customers_undone";

    private static final String PATH_CUSTOMERS_DONE = "customers_done";

    private static final String PATH_ORDERS = "orders";

    private static final String PATH_ORDERS_UNDONE = "orders_undone";

    private static final String PATH_ORDERS_DONE = "orders_done";

    private static final String PATH_ORDERS_SEND_AUTOCALL = "orders_send_autocall";

    private static final String PATH_RECEIPTS = "receipts";

    private static final String PATH_COLLECTOR_RESULTS = "collector_results";

    private static final String PATH_COLLECTOR_RESULTS_ACTIVE = "collector_results_active";

    private static final String PATH_COLLECTOR_RESULTS_ACTIVE_EXCLUDE_GET_MONEY = "collector_results_active_exclude_get_money";

    private static final String PATH_LOCATIONS = "locations";

    private static final String PATH_LOCATIONS_LATEST = "locations_latest";

    private static final String PATH_CUSTOMERS_INDICES = "customers_indices";

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static class Customers implements CustomersColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS).build();

        public static final Uri CONTENT_URI_CUSTOMERS_UNDONE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS_UNDONE).build();

        public static final Uri CONTENT_URI_CUSTOMERS_DONE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS_DONE).build();

        public static final String CONTENT_TYPE_ID = "customer";

        public static final String CUSTOMER_OLD_RECORD_SELECTION = Customers.CUSTOMER_IDCARD_NO
                + " NOT IN (SELECT " + Orders.CUSTOMER_IDCARD_NO + " FROM " + OrderDatabase.Tables.ORDERS + ")";

        public static final String[] NORMAL_PROJECTION = {
                BaseColumns._ID,
                SyncColumns.UPDATED_DATE,
                Customers.CUSTOMER_NAME,
                Customers.CUSTOMER_IDCARD_NO,
                Customers.CUSTOMER_AGE,
                Customers.CUSTOMER_GENDER,
                Customers.CUSTOMER_ADDRESS,
                Customers.CUSTOMER_ZIPCODE,
                Customers.CUSTOMER_SECTION,
                Customers.CUSTOMER_MOBILE,
                Customers.CUSTOMER_PHONE,
                Customers.CUSTOMER_PHONE_EXT,
                Customers.CUSTOMER_LAT,
                Customers.CUSTOMER_LON,
                Customers.CUSTOMER_SURVEY_TYPE
        };

        public static Uri buildCustomerUri(String customerId) {
            return CONTENT_URI.buildUpon().appendPath(customerId).build();
        }

        public static String getCustomerId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildCustomerListUri() {
            return CONTENT_URI.buildUpon().build();
        }
    }

    public static class Orders implements OrdersColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS).build();

        public static final Uri CONTENT_URI_ORDERS_UNDONE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS_UNDONE).build();

        public static final Uri CONTENT_URI_ORDERS_DONE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS_DONE).build();

        public static final Uri CONTENT_URI_ORDERS_SEND_AUTOCALL =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDERS_SEND_AUTOCALL).build();

        public static final String CONTENT_TYPE_ID = "order";

        public static final String ORDER_UPDATE_RESULT_SELECTION = Orders.ORDER_NO + " = ?";

        public static final String ORDER_OLD_RECORD_SELECTION = Orders.ORDER_COLLECT_DATE
                + " < (strftime('%s', 'now') - 2592000)";

        public static final String RESULT_COLLECTED_DATE_ORDER =
                Orders.ORDER_RESULT_COLLECTED_DATE + " ASC";

        public static final String[] NORMAL_PROJECTION = {
                BaseColumns._ID,
                Orders.ORDER_NO,
                Orders.CUSTOMER_IDCARD_NO,
                Orders.ORDER_AGREEMENT_NO,
                Orders.ORDER_GUID,
                Orders.ORDER_DESCRIPTION,
                Orders.ORDER_SURVEY_NAME,
                Orders.ORDER_COLLECT_DATE,
                Orders.ORDER_COLLECT_AMOUNT,
                Orders.ORDER_STATUS,
                Orders.ORDER_PRIORITY,
                Orders.ORDER_TASK_TYPE,
                Orders.ORDER_OPERATOR_NAME,
                Orders.ORDER_AUTOCALL_REMARK,
                Orders.ORDER_DELINQUENT_STATUS,
                Orders.ORDER_OUTSTANDING_BALANCE,
                Orders.ORDER_PENALTY,
                Orders.ORDER_CURRENT_BILL,
                Orders.ORDER_D1,
                Orders.ORDER_D1_ADD_PENALTY,
                Orders.ORDER_D2,
                Orders.ORDER_D2_ADD_PENALTY,
                Orders.ORDER_D3,
                Orders.ORDER_D3_ADD_PENALTY,
                Orders.ORDER_D4,
                Orders.ORDER_D4_ADD_PENALTY,
                Orders.ORDER_D5,
                Orders.ORDER_D5_ADD_PENALTY,
                Orders.ORDER_TOTAL_DELINQUENT,
                Orders.ORDER_TOTAL_ADD_PENALTY,
                Orders.ORDER_MINIMUM_BILL,
                Orders.ORDER_FULL_BILL,
                Orders.COLLECTOR_RESULT_CODE,
                Orders.ORDER_RESULT_COLLECTED_DATE,
                Orders.ORDER_RESULT_COLLECTED_AMOUNT,
                Orders.ORDER_RESULT_PROMISED_DATE,
                Orders.ORDER_RESULT_REMARK,
                SyncColumns.UPDATED_FLAG,
                Orders.ORDER_RESULT_SEND_TO_AUTOCALL_FLAG,
                Orders.ORDER_TOKEN,
                Orders.ORDER_CLIENT_NAME_EN,
                Orders.ORDER_CLIENT_NAME_TH,
                Orders.ORDER_CLIENT_CONTACT_NO
        };

        public static Uri buildOrderUri(String guid) {
            return CONTENT_URI.buildUpon().appendPath(guid).build();
        }

        public static String getOrderGuid(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class CollectorResults implements CollectorResultsColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTOR_RESULTS).build();

        public static final Uri CONTENT_URI_ACTIVE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTOR_RESULTS_ACTIVE).build();

        public static final Uri CONTENT_URI_ACTIVE_EXCLUDE_GET_MONEY =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLECTOR_RESULTS_ACTIVE_EXCLUDE_GET_MONEY).build();

        public static final String CONTENT_TYPE_ID = "collector_result";

        public static final String COLLECTOR_RESULT_ORDER_BY_GROUPING_ORDER =
                CollectorResults.COLLECTOR_RESULT_GROUPING_ORDER + " ASC, "
                        + CollectorResults.COLLECTOR_RESULT_CODE + " ASC";

        public static final String COLLECTOR_RESULT_IS_USED_SELECTION =
                CollectorResults.COLLECTOR_RESULT_STATUS + " = 1";

        public static final String COLLECTOR_RESULT_IS_USED_EXCLUDE_GET_MONEY_SELECTION =
                CollectorResults.COLLECTOR_RESULT_STATUS + " = 1 AND "
                        + CollectorResults.COLLECTOR_RESULT_GET_MONEY + " = 1";

        public static final String COLLECTOR_RESULT_IS_ACTIVE_SELECTION =
                CollectorResults.COLLECTOR_RESULT_STATUS + " = ?";

        public static final String COLLECTOR_RESULT_IS_ACTIVE_EXCLUDE_GET_MONEY_SELECTION =
                CollectorResults.COLLECTOR_RESULT_STATUS + " = ? AND "
                        + CollectorResults.COLLECTOR_RESULT_GET_MONEY + " = ?";

        public static final String[] NORMAR_PROJECTION = {
                CollectorResults.COLLECTOR_RESULT_CODE,
                CollectorResults.COLLECTOR_RESULT_NAME,
                CollectorResults.COLLECTOR_RESULT_TYPE,
                CollectorResults.COLLECTOR_RESULT_PRINT_RECEIPT,
                CollectorResults.COLLECTOR_RESULT_PRINT_LETTER,
                CollectorResults.COLLECTOR_RESULT_GET_MONEY,
                CollectorResults.COLLECTOR_RESULT_MAX_PAYMENT,
                CollectorResults.COLLECTOR_RESULT_MIN_PAYMENT,
                CollectorResults.COLLECTOR_RESULT_REQUIRED_REMARK
        };

        public static Uri buildCollectorResultUri(String collectorResultCode) {
            return CONTENT_URI.buildUpon().appendPath(collectorResultCode).build();
        }
    }

    public static class Receipts implements ReceiptsColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECEIPTS).build();

        public static final String CONTENT_TYPE_ID = "receipt";

        public static final String RECEIPT_PRINT_SELECTION = Receipts.RECEIPT_STATUS + " = 'P' AND "
                + Receipts.RECEIPT_PRINTED_DATE + " >= ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " <= ?";

        public static final String RECEIPT_ITEM_SELECTION = Receipts.GUID + " = ? AND "
                + Receipts.RECEIPT_STATUS + " = ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " >= ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " <= ?";

        public static final String RECEIPT_ITEM_REPRINT_SELECTION = Receipts.GUID + " = ? AND "
                + Receipts.AGREEMENT_NO + " = ? AND "
                + Receipts.RECEIPT_NO + " = ? AND "
                + Receipts.RECEIPT_STATUS + " = ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " >= ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " <= ?";

        public static final String RECEIPT_LATEST_BY_GUID_SELECTION = Receipts.GUID + " = ? AND "
                + Receipts.AGREEMENT_NO + " = ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " >= ? AND "
                + Receipts.RECEIPT_PRINTED_DATE + " <= ?";

        public static final String RECEIPT_OLD_RECORD_SELECTION = Receipts.RECEIPT_PRINTED_DATE
                + " < (strftime('%s', 'now') - 2592000)";

        public static final String RECEIPT_DATA_SYNC_UPDATE = Receipts.RECEIPT_NO + "=? AND "
                + Receipts.RECEIPT_STATUS + "=? AND "
                + Receipts.RECEIPT_PRINTED_DATE + "=?";

        public static final String DATA_SYNC_SORT = Receipts.RECEIPT_NO + " ASC, "
                + Receipts.RECEIPT_PRINTED_DATE + " ASC";

        public static Uri buildReceiptUri(String receiptNo) {
            return CONTENT_URI.buildUpon().appendPath(receiptNo).build();
        }
    }

    public static class Locations implements LocationsColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATIONS).build();

        public static final Uri CONTENT_URI_LATEST =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATIONS_LATEST).build();

        public static final String CONTENT_TYPE_ID = "location";

        public static final String LOCATION_UPDATE_SELECTION = SyncColumns.UPDATED_FLAG + "=?";

        public static final String LOCATION_UPDATED_SELECTION = Locations._ID + "=?";

        public static final String LOCATION_LATEST_SELECTION = Locations.LOCATION_EMPLOYEE_CODE + "=?";

        public static final String LOCATION_OLD_RECORD_SELECTION = Locations.LOCATION_TIME
                + " < (strftime('%s', 'now') - 2592000)";

        public static final String LOCATION_TIME_SORT = Locations.LOCATION_TIME + " ASC";

        public static final String LOCATION_TIME_LATEST_SORT = Locations.LOCATION_TIME + " DESC";

        public static final String[] DEFAULT_PROJECTION = {
                BaseColumns._ID,
                Locations.ORDER_NO,
                Locations.LOCATION_TIME,
                Locations.LOCATION_LATITUDE,
                Locations.LOCATION_LONGITUDE,
                Locations.LOCATION_BATTERY,
                Locations.LOCATION_SPEED,
                Locations.LOCATION_PROVIDER,
                Locations.LOCATION_EMPLOYEE_CODE
        };

        public static final String[] LATEST_PROJECTION = {
                Locations.LOCATION_LATITUDE,
                Locations.LOCATION_LONGITUDE,
                Locations.LOCATION_TIME,
                Locations.LOCATION_SPEED,
                Locations.LOCATION_PROVIDER
        };

        public static Uri buildLocationUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }

    public static class CustomersIndices implements CustomersIndicesColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS_INDICES).build();

        public static final String CONTENT_TYPE_ID = "customers_indices";

        public static Uri buildCustomersIndicesUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }

    public OrderContract() {

    }
}
