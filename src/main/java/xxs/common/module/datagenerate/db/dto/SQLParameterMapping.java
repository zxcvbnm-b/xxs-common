package xxs.common.module.datagenerate.db.dto;

import java.util.function.Function;

/**
 * SQL参数映射：比如 1，xxx，2，xxx，3，xxx
 *
 * @author xxs
 */
public class SQLParameterMapping {
    /**
     * 下标 从1开始
     */
    private int index;
    /**
     * 列的信息
     */
    private TableColumnInfo tableColumnInfo;
    /**
     * 获取值的回调
     */
    private Function<TableColumnInfo, Object> parameterCallBack;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TableColumnInfo getTableColumnInfo() {
        return tableColumnInfo;
    }

    public void setTableColumnInfo(TableColumnInfo tableColumnInfo) {
        this.tableColumnInfo = tableColumnInfo;
    }

    public Function<TableColumnInfo, Object> getParameterCallBack() {
        return parameterCallBack;
    }

    public void setParameterCallBack(Function<TableColumnInfo, Object> parameterCallBack) {
        this.parameterCallBack = parameterCallBack;
    }
}
