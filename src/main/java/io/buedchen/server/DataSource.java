package io.buedchen.server;

import org.apache.commons.dbcp2.BasicDataSource;

public class DataSource {

    private static final String DRIVER_CLASS_NAME = "org.h2.Driver";

    private static final String DB_URL = "jdbc:h2:~/test5";

    private static final String DB_USER = "sa";

    private static final String DB_PASSWORD = "";

    private static final int CONN_POOL_SIZE = 5;
    private BasicDataSource bds = new BasicDataSource();

    private DataSource() {

        bds.setDriverClassName(DRIVER_CLASS_NAME);
        bds.setUrl(DB_URL);
        bds.setUsername(DB_USER);
        bds.setPassword(DB_PASSWORD);
        bds.setInitialSize(CONN_POOL_SIZE);

    }

    public static DataSource getInstance() {

        return DataSourceHolder.INSTANCE;

    }

    public BasicDataSource getBds() {

        return bds;
    }

    public void setBds(BasicDataSource bds) {

        this.bds = bds;
    }

    private static class DataSourceHolder {

        private static final DataSource INSTANCE = new DataSource();

    }
}

