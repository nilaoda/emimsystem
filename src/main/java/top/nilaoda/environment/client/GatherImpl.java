package top.nilaoda.environment.client;

import top.nilaoda.environment.bean.Environment;
import top.nilaoda.environment.util.Configuration;
import top.nilaoda.environment.util.ConfigurationAWare;
import top.nilaoda.environment.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * @author nilaoda
 * @version 1.0
 * @description 实现Gather接口，重写方法
 * @date 2019/10/29
 * @time 11:48
 */

public class GatherImpl implements Gather, ConfigurationAWare {
    private Configuration configuration;
    private Log logger;
    private String filePath = "";
    private String backFile = "";

    //解析本地文件 提取内容，封装为Environment对象，添加到Collection中，最终返回
    @Override
    public Collection<Environment> gather() throws Exception {
        Collection<Environment> environments = new ArrayList<>();
        BufferedReader br = null;
        int countCO2 = 0;
        int countTemperature = 0;
        int countHumidity = 0;
        int countLux = 0;
        long countLine = 0;

        try {
            br = new BufferedReader(new FileReader(filePath));
            String line;
            if (new File(backFile).exists()) {
                //从本地文件读取行号
                logger.info("从本地文件读取上次读取位置");
                long lnum = (long) configuration.getBackup().load(backFile);
                logger.info("将跳转到:" + lnum);
                while (countLine < lnum) {
                    br.readLine();
                    countLine++;
                }
            }

            while ((line = br.readLine()) != null) {
                countLine++;
                if (line.equals(""))
                    continue;
                String[] arr = line.split("[|]");
                String srcId = arr[0]; //发送端id
                String dstId = arr[1]; //树莓派系统id
                String devId = arr[2]; //区域模块id
                String sensorAddress = arr[3]; //传感器地址
                int count = Integer.parseInt(arr[4]); //传感器个数
                String cmd = arr[5]; //指令标号 3接收 16发送
                float data = 0; //16进制数据
                float temperature = -1; //温度
                float humidity = -1; //湿度
                int status = Integer.parseInt(arr[7]); //状态 1为成功
                String name = "";
                if (sensorAddress.equals("16")) {
                    //分析温度和湿度
                    temperature = (float) (Integer.parseInt(arr[6].substring(0, 4), 16) * 0.00268127 - 46.85);
                    humidity = (float) (Integer.parseInt(arr[6].substring(4, 8), 16) * 0.00190735 - 6);
                    countHumidity++;
                    countTemperature++;
                } else if (sensorAddress.equals("256")) {
                    name = "光照强度";
                    data = Long.parseLong(arr[6], 16);
                    countLux++;
                } else if (sensorAddress.equals("1280")) {
                    name = "二氧化碳";
                    data = Long.parseLong(arr[6], 16);
                    countCO2++;
                }
                Timestamp gather_date = new Timestamp(Long.parseLong(arr[8])); //采集时间

                //加入集合
                if (temperature == -1 && humidity == -1)
                    environments.add(new Environment(name, srcId, dstId, devId, sensorAddress, count, cmd, status, data, gather_date));
                if (temperature != -1)
                    environments.add(new Environment("温度", srcId, dstId, devId, sensorAddress, count, cmd, status, temperature, gather_date));
                if (humidity != -1)
                    environments.add(new Environment("湿度", srcId, dstId, devId, sensorAddress, count, cmd, status, humidity, gather_date));

            }
        } catch (Exception e) {
            //处理异常
            logger.error("发生异常");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //记录行号
            logger.info("将本次读取行号(" + countLine + ")记录到本地文件");
            configuration.getBackup().backup(backFile, countLine);
        }

        logger.debug("温度数据：" + countTemperature + "条；湿度数据：" + countHumidity + "条；" +
                "二氧化碳数据：" + countCO2 + "条；光照强度数据：" + countLux + "条.");
        return environments;
    }

    @Override
    public void init(Properties properties) throws Exception {
        filePath = properties.getProperty("gather_src-file");
        backFile = properties.getProperty("gather_backupFile");
        logger = configuration.getLogger();
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
