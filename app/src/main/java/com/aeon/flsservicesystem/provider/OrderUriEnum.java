package com.aeon.flsservicesystem.provider;

public enum OrderUriEnum {
    CUSTOMERS(100, "customers", OrderContract.Customers.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.CUSTOMERS),
    CUSTOMERS_UNDONE(101, "customers_undone", OrderContract.Customers.CONTENT_TYPE_ID, false, null),
    CUSTOMERS_DONE(102, "customers_done", OrderContract.Customers.CONTENT_TYPE_ID, false, null),
    ORDERS(200, "orders", OrderContract.Orders.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.ORDERS),
    ORDERS_UNDONE(201, "orders_undone", OrderContract.Orders.CONTENT_TYPE_ID, false, null),
    ORDERS_DONE(202, "orders_done", OrderContract.Orders.CONTENT_TYPE_ID, false, null),
    ORDERS_GUID(203, "orders/*", OrderContract.Orders.CONTENT_TYPE_ID, true, null),
    ORDERS_SEND_AUTOCALL(204, "orders_send_autocall", OrderContract.Orders.CONTENT_TYPE_ID, false, null),
    RECEIPTS(300, "receipts", OrderContract.Receipts.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.RECEIPTS),
    COLLECTOR_RESULTS(500, "collector_results", OrderContract.CollectorResults.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.COLLECTOR_RESULTS),
    COLLECTOR_RESULTS_ACTIVE(501, "collector_results_active", OrderContract.CollectorResults.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.COLLECTOR_RESULTS),
    COLLECTOR_RESULTS_ACTIVE_EXCLUDE_GET_MONEY(502, "collector_results_active_exclude_get_money", OrderContract.CollectorResults.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.COLLECTOR_RESULTS),
    LOCATIONS(600, "locations", OrderContract.Locations.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.LOCATIONS),
    LOCATIONS_LATEST(601, "locations_latest", OrderContract.Locations.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.LOCATIONS),
    CUSTOMERS_INDICES(700, "customers_indices", OrderContract.CustomersIndices.CONTENT_TYPE_ID,
            false, OrderDatabase.Tables.CUSTOMERS_INDICES);

    public int code;

    public String path;

    public String contentType;

    public String table;

    OrderUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? OrderContract.makeContentItemType(contentTypeId)
                : OrderContract.makeContentType(contentTypeId);
        this.table = table;
    }
}
