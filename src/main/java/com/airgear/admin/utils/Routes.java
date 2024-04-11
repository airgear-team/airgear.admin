package com.airgear.admin.utils;

public final class Routes {

    private Routes() {
        throw new AssertionError("non-instantiable class");
    }

    public static final String USERS = "/users";

    public static final String USERS_ADMINS = USERS + "/admins";

    public static final String GOODS = "/goods";

    public static final String GOODS_ADMINS = GOODS + "/admins";

}
