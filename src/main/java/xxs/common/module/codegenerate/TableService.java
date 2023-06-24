package xxs.common.module.codegenerate;

import xxs.common.module.codegenerate.model.SearchColumnInfo;
import xxs.common.module.codegenerate.model.TableInfo;

import java.sql.*;
import java.util.*;

/**
 * 表信息获取服务
 *
 * @author xxs
 */
public interface TableService {
    /**
     * 加载表信息
     *
     * @param tableNames
     * @return
     */
    public Map<String, TableInfo> loadTables(String tableNames) throws Exception;

    /**
     * 加载表信息
     *
     * @param tableNames
     * @param replaceTablePre 移除生成的驼峰表名前缀
     * @return
     * @throws SQLException
     */
    public Map<String, TableInfo> loadTables(String tableNames, String replaceTablePre) throws Exception;

    /**
     * 根据查询sql得到查询的sql的列的信息
     *
     * @param sql
     * @return
     */
    public List<SearchColumnInfo> getSearchColumnInfoBySearchSql(String sql);
}
