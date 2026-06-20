package edu.rutmiit.demo.events;

public final class RoutingKeys {
    private RoutingKeys() {}

    public static final String EXCHANGE = "w.events";

    // города
    public static final String CITY_CREATED = "city.created";
    public static final String CITY_PATCHUPDATED = "city.patchupdated";

    // маршруты
    public static final String ROUTE_CREATED = "route.created";
    public static final String ROUTE_UPDATED = "route.updated";
    public static final String ROUTE_PATCHUPDATED = "route.patchupdated";
    public static final String ROUTE_DELETED = "route.deleted";

    public static final String ROUTE_ENRICHED = "route.enriched";

    // остановки
    public static final String HALT_CREATED = "halt.created";
    public static final String HALT_PATCHUPDATED = "halt.patchupdated";

    // паттерны
    public static final String ALL_CITY_EVENTS = "city.*";
    public static final String ALL_ROUTE_EVENTS = "route.*";
    public static final String ALL_HALT_EVENTS = "halt.*";
    public static final String ALL_EVENTS = "#";
}
