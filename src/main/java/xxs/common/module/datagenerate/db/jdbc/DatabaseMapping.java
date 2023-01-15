package xxs.common.module.datagenerate.db.jdbc;

import com.alibaba.druid.DbType;

/*druid和数据库连接获取到的映射关系*/
public enum DatabaseMapping {
    MYSQL("MySQL", DbType.mysql);
    private String sourceDatabaseName;
    private DbType druidDatabaseType;

    DatabaseMapping(String sourceDatabaseName, DbType druidDatabaseType) {
        this.sourceDatabaseName = sourceDatabaseName;
        this.druidDatabaseType = druidDatabaseType;
    }
    //
    public static DbType getDruidDruidDatabaseType(String sourceDatabaseName){
        DatabaseMapping[] values = values();
        for (DatabaseMapping item : values) {
            if(item.sourceDatabaseName.equals(sourceDatabaseName)){
               return  item.druidDatabaseType;
            }
        }
        throw new RuntimeException("和druid的映射不存在，请添加映射");
    }
    public String getSourceDatabaseName() {
        return sourceDatabaseName;
    }

    public void setSourceDatabaseName(String sourceDatabaseName) {
        this.sourceDatabaseName = sourceDatabaseName;
    }

    public DbType getDruidDatabaseType() {
        return druidDatabaseType;
    }

    public void setDruidDatabaseType(DbType druidDatabaseType) {
        this.druidDatabaseType = druidDatabaseType;
    }

}
