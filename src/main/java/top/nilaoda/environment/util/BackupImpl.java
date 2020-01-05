package top.nilaoda.environment.util;

import java.io.*;
import java.util.Properties;

/**
 * @author nilaoda
 * @version 1.0
 * @description 1.入库异常 备份尚未入库的数据（size-len） 2.每隔一段时间读取同一个采集文件，跳过之前读过的数据(记录读取位置)
 * @date 2019/10/31
 * @time 14:21
 */

public class BackupImpl implements Backup {
    private String backPath = "src/main/resources"; //默认值

    @Override
    public void init(Properties properties) {
        backPath = properties.getProperty("backup_backupPath");
    }

    @Override
    public void backup(String fileName, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(backPath, fileName).getPath()))) {
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object load(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(backPath, fileName).getPath()))) {
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteBackup(String fileName) {
        File f = new File(new File(backPath, fileName).getPath());
        if (f.exists())
            f.delete();
    }
}
