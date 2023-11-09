//package com.vrv.vap.line.tools;
//
//import org.apache.log4j.Logger;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import java.io.ByteArrayInputStream;
//
//import java.io.InputStream;
//
//import java.sql.Connection;
//
//import java.sql.PreparedStatement;
//
//import javax.sql.DataSource;
//
///**
//
// *
//
// @author seven
//
// *
//
// @since 07.03.2013
//
// */
//
//public class BulkLoadData2MysqL {
//
//    private static final Logger logger = Logger.getLogger(BulkLoadData2MysqL.class);
//
//    private JdbcTemplate jdbcTemplate;
//
//    private Connection conn = null;
//
//    public void setDataSource(DataSource dataSource) {
//
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//
//    }
//
//    public static InputStream getTestDataInputStream() {
//
//        StringBuilder builder = new StringBuilder();
//
//        for (int i = 1; i <= 10; i++) {
//
//            for (int j = 0; j <= 10000; j++) {
//
//                builder.append(4);
//
//                builder.append("\t");
//
//                builder.append(4 + 1);
//
//                builder.append("\t");
//
//                builder.append(4 + 2);
//
//                builder.append("\t");
//
//                builder.append(4 + 3);
//
//                builder.append("\t");
//
//                builder.append(4 + 4);
//
//                builder.append("\t");
//
//                builder.append(4 + 5);
//
//                builder.append("\n");
//
//            }
//
//        }
//
//        byte[] bytes = builder.toString().getBytes();
//
//        InputStream is = new ByteArrayInputStream(bytes);
//
//        return is;
//
//    }
//
//    /**
//
//     *
//
//     * load bulk data from InputStream to MysqL
//
//     */
//
//    public int bulkLoadFromInputStream(String loadDatasql,InputStream dataStream) throws Exception {
//
//        if (dataStream == null) {
//
//            logger.info("InputStream is null,No data is imported");
//
//            return 0;
//
//        }
//
//        conn = jdbcTemplate.getDataSource().getConnection();
//
//        PreparedStatement statement = conn.prepareStatement(loadDatasql);
//
//        int result = 0;
//
//        if (statement.isWrapperFor(com.mysqL.jdbc.Statement.class)) {
//
//            com.mysqL.jdbc.PreparedStatement MysqLStatement = statement.unwrap(com.mysqL.jdbc.PreparedStatement.class);
//
//            MysqLStatement.setLocalInfileInputStream(dataStream);
//
//            result = MysqLStatement.executeUpdate();
//
//        }
//
//        return result;
//
//    }
//
//    public static void main(String[] args) {
//
//        String testsql = "LOAD DATA LOCAL INFILE 'sql.csv' IGNORE INTO TABLE test.test (a,b,c,d,e,f)";
//
//        InputStream dataStream = getTestDataInputStream();
//
//        BulkLoadData2MysqL dao = new BulkLoadData2MysqL();
//
//        try {
//
//            long beginTime = System.currentTimeMillis();
//
//            int rows = dao.bulkLoadFromInputStream(testsql,dataStream);
//
//            long endTime = System.currentTimeMillis();
//
//            logger.info("importing " + rows +
//
//                    " rows data into MysqL and cost " + (endTime - beginTime) +
//
//                    " ms!");
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//
//        System.exit(1);
//
//    }
//
//}
