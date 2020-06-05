package com.xx.tool.vo.util;

import com.xx.tool.vo.bean.DBConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	/*public static void main(String[] args) throws SQLException, Exception {
		Statement statement = getJDBCConn().createStatement();
		StringBuilder sql = new StringBuilder();
		sql.append(" select a.cate_main_id,                                                         ");
		sql.append("        a.id,                                                                   ");
		sql.append("        a.item_id,                                                              ");
		sql.append("        scc_mes_iqc.Inspection_Info_By_CateMainId(a.cate_main_id,               ");
		sql.append("                                                  a.id,                         ");
		sql.append("                                                  a.item_id)                    ");
		sql.append("   from (SELECT m.*,                                                            ");
		sql.append("                (SELECT MAX(RT2.TRANSACTION_DATE)                               ");
		sql.append("                   FROM APPS.RCV_TRANSACTIONS RT1, APPS.RCV_TRANSACTIONS RT2    ");
		sql.append("                  WHERE 1 = 1                                                   ");
		sql.append("                    AND RT1.TRANSACTION_ID = m.TRANSACTION_ID                   ");
		sql.append("                    AND RT1.SHIPMENT_LINE_ID = RT2.SHIPMENT_LINE_ID             ");
		sql.append("                    AND RT1.PO_LINE_ID = RT2.PO_LINE_ID                         ");
		sql.append("                    AND RT2.TRANSACTION_TYPE = 'DELIVER'),                      ");
		sql.append("                sius.urgentmtl_number,                                          ");
		sql.append("                cm.category_type cateType2,                                     ");
		sql.append("                scm.project project2,                                           ");
		sql.append("                m.cate_check_level checkLevel2,                                 ");
		sql.append("                p.name                                                          ");
		sql.append("           FROM SCCMES.SCC_IQC_CHECK_MAIN      m,                               ");
		sql.append("                SCCMES.SYS_ORG_ELEMENT         p,                               ");
		sql.append("                sccmes.scc_iqc_urgentmtl_sign  sius,                            ");
		sql.append("                sccmes.scc_iqc_category_main   cm,                              ");
		sql.append("                SCCMES.SCC_IQC_SAMPLE_CFG_MAIN scm                              ");
		sql.append("          where 1 = 1                                                           ");
		sql.append("            and m.RECEIPT_NUM is not null                                       ");
		sql.append("            and m.check_result = 'Y'                                            ");
		sql.append("            and m.last_update_date between sysdate - 1000 and sysdate           ");
		sql.append("            and m.storage_flag = 'S'                                            ");
		sql.append("            and m.cate_sample_project = scm.project                             ");
		sql.append("            and cm.id = m.CATE_MAIN_ID                                          ");
		sql.append("            and scm.id = cm.sample_id                                           ");
		sql.append("            and m.check_by = p.no(+)                                            ");
		sql.append("            and m.item_id = sius.inventory_item_id(+)) a                        ");
		ResultSet result = statement.executeQuery(sql.toString());
		while(result.next()) {
			Object obj = result.getObject(4);
			System.out.println(obj);
		}
	}*/
}
