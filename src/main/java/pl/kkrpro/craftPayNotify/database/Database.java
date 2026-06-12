package pl.kkrpro.craftPayNotify.database;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pl.kkrpro.craftPayNotify.config.PluginConfig;

import javax.sql.DataSource;

public class Database {

    private final HikariDataSource ds;

    public Database(PluginConfig cfg) {
        var m = cfg.mysql;

        HikariConfig hc = new HikariConfig();
        hc.setDriverClassName("org.mariadb.jdbc.Driver");
        hc.setJdbcUrl("jdbc:mariadb://" + m.host + ":" + m.port + "/" + m.database + "?useUnicode=true&characterEncoding=utf8");
        hc.setUsername(m.user);
        hc.setPassword(m.password);
        hc.setMaximumPoolSize(m.poolSize);
        hc.setPoolName("my-database");
        hc.addDataSourceProperty("cachePrepStmts", "true");
        hc.addDataSourceProperty("prepStmtCacheSize", "250");
        hc.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.ds = new HikariDataSource(hc);
    }

    public DataSource getDataSource() {
        return ds;
    }

    public void close() {
        ds.close();
    }
}
