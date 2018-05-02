package com.zzg.mybatis.generator.util;

import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.DbType;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Owen on 6/12/16.
 */
public class DbUtil {

    private static final Logger _LOG = LoggerFactory.getLogger(DbUtil.class);
    private static final int DB_CONNECTION_TIMEOUTS_SECONDS = 1;

    private static final Map<String, String> JDBC_JAVA_TYPE = new HashMap<>();
    static {
        JDBC_JAVA_TYPE.put("TINYINT", "Integer");
//        JDBC_JAVA_TYPE.put("BIT", "Integer");
        JDBC_JAVA_TYPE.put("DECIMAL", "BigDecimal");
    }

    public static Connection getConnection(DatabaseConfig config) throws ClassNotFoundException, SQLException {
		DriverManager.setLoginTimeout(DB_CONNECTION_TIMEOUTS_SECONDS);
		DbType dbType = DbType.valueOf(config.getDbType());
		Class.forName(dbType.getDriverClass());
        String url = getConnectionUrlWithSchema(config);
        _LOG.info("getConnection, connection url: {}", url);
        return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
    }

    public static List<String> getTableNames(DatabaseConfig config) throws Exception {
        String url = getConnectionUrlWithSchema(config);
        _LOG.info("getTableNames, connection url: {}", url);
        Connection conn = DriverManager.getConnection(url, config.getUsername(), config.getPassword());
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, config.getUsername().toUpperCase(), null, null);
        List<String> tables = new ArrayList<>();
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }

    public static List<UITableColumnVO> getTableColumns(DatabaseConfig dbConfig, String tableName) throws Exception {
        String url = getConnectionUrlWithSchema(dbConfig);
        _LOG.info("getTableColumns, connection url: {}", url);
		Connection conn = getConnection(dbConfig);
		DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getColumns(null, null, tableName, null);
        List<UITableColumnVO> columns = new ArrayList<>();
        while (rs.next()) {
            UITableColumnVO columnVO = new UITableColumnVO();
            String columnName = rs.getString("COLUMN_NAME");
            String typeName = rs.getString("TYPE_NAME");
            columnVO.setColumnName(columnName);
            columnVO.setJdbcType(typeName);
            typeName = typeName.replaceAll("UNSIGNED", "").trim();
            if (null != JDBC_JAVA_TYPE.get(typeName)) {
                columnVO.setJavaType(JDBC_JAVA_TYPE.get(typeName));
            }
            columns.add(columnVO);
        }
        return columns;
    }

    public static String getConnectionUrlWithSchema(DatabaseConfig dbConfig) throws ClassNotFoundException {
		DbType dbType = DbType.valueOf(dbConfig.getDbType());
		String connectionUrl = String.format(dbType.getConnectionUrlPattern(), dbConfig.getHost(), dbConfig.getPort(), dbConfig.getSchema(), dbConfig.getEncoding());
        _LOG.info("getConnectionUrlWithSchema, connection url: {}", connectionUrl);
        return connectionUrl;
    }

}
