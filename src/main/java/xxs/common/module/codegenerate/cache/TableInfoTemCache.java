package xxs.common.module.codegenerate.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import xxs.common.module.codegenerate.LoadTableService;
import xxs.common.module.codegenerate.model.TableInfo;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 表信息临时缓存
 * @author xs
 */
public class TableInfoTemCache {
    private Map<String, TableInfo> cacheMap = new HashMap<>();
    private LoadTableService loadTableService;

    public TableInfoTemCache(LoadTableService loadTableService) {
        this.loadTableService = loadTableService;
    }

    public TableInfo getTableInfo(String tableName) throws SQLException {
        if (StringUtils.isEmpty(tableName)) {
            return null;
        }
        TableInfo tableInfo = cacheMap.get(tableName);
        if (tableInfo != null) {
            return tableInfo;
        }
        Map<String, TableInfo> tableInfoMap = loadTableService.loadTables(tableName);
        if (CollectionUtils.isEmpty(tableInfoMap)) {
            return null;
        }
        cacheMap.putAll(tableInfoMap);
        return tableInfoMap.get(tableName);
    }
}
