package com.xx.vo.tool.util;

import com.xx.vo.tool.bean.DBConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {

	/**
	 * 获取connection
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getJDBCConn() throws Exception {
		Connection conn = null;
//		Properties props = getProperties("jdbc.properties");
//		Class.forName(props.getProperty("jdbc.class"));
//		String url = props.getProperty("jdbc.url");
//		conn = DriverManager.getConnection(url, props.getProperty("jdbc.user"), props.getProperty("jdbc.password"));
		DBConfig dbConfig = XMLUtil.loadDBConfigFromFile();
		Class.forName(dbConfig.getJdbcClass());
		conn = DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUser(), dbConfig.getPassword());
		return conn;
		// static Connection getConnection(String url, String user, String password)
		// 返回值是java.sql.Connection接口的实现类
	}

	/**
	 * 读取配置文件的信息
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static Properties getProperties(String filePath) throws Exception {
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = SqlUtil.class.getClassLoader().getResourceAsStream(filePath);
			if (in != null) {
				prop.load(new InputStreamReader(in, "UTF-8"));
			}
		} catch (Exception e) {
			System.err.println("读取Properties配置文件信息失败" + e);
			throw new Exception("读取Properties配置文件信息失败");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.err.println("关闭输入流失败" + e);
				}
			}
		}
		return prop;
	}

}
