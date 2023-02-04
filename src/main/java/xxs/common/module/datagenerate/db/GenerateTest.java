package xxs.common.module.datagenerate.db;

import xxs.common.module.datagenerate.db.jdbc.JdbcBatchExecutor;

public class GenerateTest {
    public static void main(String[] args) throws Exception {
        String sql="CREATE TABLE `bus` (\n" +
                "  `bid` int(11) NOT NULL ,\n" +
                "  `bname` varchar(255) DEFAULT NULL,\n" +
                "  `buspath` longtext,\n" +
                "  `endDate` varchar(255) DEFAULT NULL,\n" +
                "  `startDate` varchar(255) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`bid`)\n" +
                ") ENGINE=InnoDB  DEFAULT CHARSET=utf8";
        JdbcBatchExecutor jdbcBatchExecutor=new JdbcBatchExecutor(sql);
        jdbcBatchExecutor.executor();
    }
}
