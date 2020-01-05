package top.nilaoda.environment.server;

import top.nilaoda.environment.bean.Environment;
import top.nilaoda.environment.util.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class DBStoreImpl implements DBStore, ConfigurationAWare {
    private Log logger;
    private String backFile = "";
    private Configuration configuration;

    @Override
    public void saveDb(Collection<Environment> coll) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int saveCount = 0; //正确存了多少数据
        try {
            //恢复未入库成功的数据
            File bf = new File(backFile);
            if (bf.exists()) {
                Backup backup = configuration.getBackup();
                Collection<Environment> lastColl = (Collection<Environment>) backup.load(backFile);
                coll.addAll(lastColl);
            }
            bf.delete();

            //遍历存到数据库中
            logger.info("开始插入数据");
            String lastDay = "";
            int count = 1;
            String sql = "";
            conn = JDBCUtils.getConnection();
            //关闭自动提交
            conn.setAutoCommit(false);

            for (Environment e : coll) {
                //获取数据是几号的
                String day = new SimpleDateFormat("dd").format(e.getGather_date());
                //赋初值
                if (lastDay.equals("")) {
                    lastDay = day;
                    sql = "insert into e_detail_" + day + " values (?,?,?,?,?,?,?,?,?,?)";
                    logger.debug(sql);
                    pstmt = conn.prepareStatement(sql);
                }

                //如果不是同一天，则使用新的ps执行批处理
                if (!lastDay.equals(day)) {
                    //执行剩余批处理
                    int[] numArr = pstmt.executeBatch();
                    conn.commit();
                    saveCount += numArr.length;
                    count = 1;
                    sql = "insert into e_detail_" + day + " values (?,?,?,?,?,?,?,?,?,?)";
                    logger.debug(sql);
                    pstmt = conn.prepareStatement(sql);
                    lastDay = day;
                }

                pstmt.setString(1, e.getName());
                pstmt.setString(2, e.getSrcId());
                pstmt.setString(3, e.getDstId());
                pstmt.setString(4, e.getDevId());
                pstmt.setString(5, e.getSensorAddress());
                pstmt.setInt(6, e.getCount());
                pstmt.setString(7, e.getCmd());
                pstmt.setInt(8, e.getStatus());
                pstmt.setFloat(9, e.getData());
                pstmt.setTimestamp(10, e.getGather_date());
                //添加到批处理
                pstmt.addBatch();
                //计数
                count++;
                //每20条执行一次
                if (count % 20 == 0) {
                    int[] numArr = pstmt.executeBatch();
                    conn.commit();
                    saveCount += numArr.length;
                }
            }
            //执行批处理
            int[] numArr = pstmt.executeBatch();
            //提交事务
            conn.commit();
            //统计正确提交多少条数据
            saveCount += numArr.length;
            logger.info("已插入");
        } catch (Exception e) {
            logger.error("异常，回滚事务");
            if (conn != null)
                conn.rollback(); //回滚
            logger.info("备份数据...");
            Backup backup = configuration.getBackup();
            Collection<Environment> backData = new ArrayList<>();
            for (int i = saveCount; i < coll.size(); i++) {
                backData.add(((List<Environment>) coll).get(i));
            }
            backup.backup(backFile, backData);
            logger.info("备份成功:" + backFile);
            e.printStackTrace();
        } finally {
            JDBCUtils.close(pstmt, conn);
        }
    }

    @Override
    public void init(Properties properties) throws Exception {
        backFile = properties.getProperty("client_backupFile");
        logger = configuration.getLogger(); //依赖注入
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
