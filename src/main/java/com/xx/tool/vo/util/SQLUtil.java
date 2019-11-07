package com.xx.tool.vo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author SN78083
 *
 */
public class SQLUtil {

	private static String sql = "select sfi.id,\r\n" + 
			"       sfi.ORGANIZATION_ID,\r\n" + 
			"       ood.ORGANIZATION_CODE,\r\n" + 
			"       msi.segment1 item_code,\r\n" + 
			"       sfi.FC_NAME,\r\n" + 
			"       sfi.FC_QTY,\r\n" + 
			"       sfi.LAST_UPDATE_DATE\r\n" + 
			"  from sccmes.scc_fc_import              sfi,\r\n" + 
			"       inv.mtl_system_items_b            msi,\r\n" + 
			"       bom.cst_item_costs                cic,\r\n" + 
			"       apps.org_organization_definitions ood\r\n" + 
			" where 1 = 1\r\n" + 
			"   and msi.organization_id = ood.ORGANIZATION_ID\r\n" + 
			"   and cic.cost_type_id = 1\r\n" + 
			"   and msi.inventory_item_id = cic.inventory_item_id\r\n" + 
			"   and msi.organization_id = cic.organization_id\r\n" + 
			"   and sfi.inventory_item_id = msi.inventory_item_id\r\n" + 
			"   and sfi.organization_id = msi.organization_id";

	public static void main(String[] args) throws Exception {
		getSegmentsWithType(sql);
	}

	/**
	 * 从数据库中查询类型并输出
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static String getSegmentsWithType(String sql) throws Exception {
		sql = sql.toLowerCase();
		// 获取所有表
		String allTable = sql.replaceAll("[\\s\\S]*from", "").replaceAll("where[\\s\\S]*", "");
		String[] tables = allTable.split(",");
		Map<String, String> tableName = new HashMap<>();
		for (String table : tables) {
			String[] strs = table.split(" ");
			String tName = null;// 表名
			String alias = null;// 别名
			for (String str2 : strs) {
				str2 = str2.replaceAll("\\s*", "");
				if (!StringUtil.isBlank(str2)) {
					if (tName == null) {
						tName = str2;
					} else {
						alias = str2;
					}
				}
			}
			if(tName.contains(".")) {
				tName = tName.split("\\.")[1];
			}
			tableName.put(alias, tName);
			/*
			 * if(value!=null) {//有别名 tableName.add(key); }else {//表无别名 tableName.add(key);
			 * }
			 */
		}
		// 获取所有字段
		String segment = sql.replaceAll("from[\\s\\S]*", "").replace("select", "");
		String[] segments = segment.split(",");// 分组
		Map<String, List<String>> map = new HashMap<>();
		for (String str : segments) {
			String[] strs = str.split(" ");
			String key = null;
			String value = null;
			for (String str2 : strs) {
				str2 = str2.replaceAll("\\s*", "");// 去除空字符
				if (!StringUtil.isBlank(str2)) {
					if (key == null) {
						String[] kv = str2.split("\\.");
						// key = str.replaceAll(".*\\.", "");// 去除表别名
						key = kv[0];
						value = kv[1];
					}
				}
			}
			if(value != null) {
				List<String> list = map.get(key);
				if (list == null) {
					list = new ArrayList<>();
					list.add(value);

				} else {
					list.add(value);
				}
				map.put(key, list);
			}
			key = null;
		}
		Set<Entry<String, List<String>>> entrySet = map.entrySet();

		List<String> result = new ArrayList<>();
		for (Entry<String, List<String>> entry : entrySet) {
			String table = tableName.get(entry.getKey()) == null ? entry.getKey() : tableName.get(entry.getKey());
			List<String> cols = getColumnBy(table, entry.getValue());
			result.addAll(cols);
		}
		String resultStr = "";
		for (String str : result) {
			resultStr += str + "\r\n";
		}
		return resultStr;
	}

	/**
	 * 根据表名和所需字段生成field
	 * @param tableName
	 * @param cols
	 * @return
	 * @throws Exception
	 */
	public static List<String> getColumnBy(String tableName, List<String> cols) throws Exception {
		tableName = tableName.toUpperCase();
		StringBuilder sql = new StringBuilder();
		sql.append(" select                                      			  ");
		sql.append("   'private ' || DECODE(                     			  ");
		sql.append("     T.DATA_TYPE, 'VARCHAR2', 'String ',     			  ");
		sql.append("     'CHAR', 'String ', 'DATE', 'Date ',     			  ");
		sql.append("     'NUMBER', 'Number ', 'INT', 'INT',      			  ");
		sql.append("     'LONG', 'Long', 'FLOAT', 'Float '       			  ");
		sql.append("   ) || substr(                                           ");
		sql.append("     lower(t.COLUMN_NAME),                                ");
		sql.append("     0,                                                   ");
		sql.append("     case when instr(                                     ");
		sql.append("       lower(t.COLUMN_NAME),                              ");
		sql.append("       '_'                                                ");
		sql.append("     ) > 0 then instr(                                    ");
		sql.append("       lower(t.COLUMN_NAME),                              ");
		sql.append("       '_'                                                ");
		sql.append("     ) -1 else length(                                    ");
		sql.append("       lower(t.COLUMN_NAME)                               ");
		sql.append("     ) end                                                ");
		sql.append("   ) || REGEXP_REPLACE(                                   ");
		sql.append("     INITCAP(                                             ");
		sql.append("       substr(                                            ");
		sql.append("         lower(t.COLUMN_NAME),                            ");
		sql.append("         case when instr(                                 ");
		sql.append("           lower(t.COLUMN_NAME),                          ");
		sql.append("           '_'                                            ");
		sql.append("         ) > 0 then instr(                                ");
		sql.append("           lower(t.COLUMN_NAME),                          ");
		sql.append("           '_'                                            ");
		sql.append("         ) + 1 else length(                               ");
		sql.append("           lower(t.COLUMN_NAME)                           ");
		sql.append("         ) + 1 end,                                       ");
		sql.append("         length(                                          ");
		sql.append("           lower(t.COLUMN_NAME)                           ");
		sql.append("         )                                                ");
		sql.append("       )                                                  ");
		sql.append("     ),                                                   ");
		sql.append("     '(\\w)[_]',                                           ");
		sql.append("     '\\1'                                                 ");
		sql.append("   ) || ';  //' || c.comments col                         ");
		sql.append(" from                                                     ");
		sql.append("   all_tab_cols t,                                        ");
		sql.append("   all_col_comments c                                     ");
		sql.append(" where                                                    ");
		sql.append("   t.TABLE_NAME = '"+tableName+"'     					  ");
		sql.append("   and c.table_name = t.TABLE_NAME                        ");
		sql.append("   and c.column_name = t.COLUMN_NAME                      ");

		for (int i = 0; i < cols.size(); i++) {
			String col = cols.get(i).toUpperCase();
			if (i == 0) {
				sql.append(" and (c.column_name = '" + col + "' ");
			}

			if (i > 0 && i < cols.size()) {
				sql.append("    or c.column_name = '" + col + "' ");
			}
			if (i == cols.size() - 1) {
				sql.append(" )");
			}
		}

		Connection conn = getJDBCConn();
		List<String> list = new ArrayList<>();
		try {
			Statement stat = conn.createStatement();
			ResultSet result = stat.executeQuery(sql.toString());
			while (result.next()) {
				String column = result.getString(1);
				list.add(column);
				System.out.println(column);
			}
			stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static String getColumnByTableName(String tableName) throws Exception {
		tableName = tableName.toUpperCase();
		StringBuilder sql = new StringBuilder();
		sql.append(" select                                      			  ");
		sql.append("   'private ' || DECODE(                     			  ");
		sql.append("     T.DATA_TYPE, 'VARCHAR2', 'String ',     			  ");
		sql.append("     'CHAR', 'String ', 'DATE', 'Date ',     			  ");
		sql.append("     'NUMBER', 'Number ', 'INT', 'INT',      			  ");
		sql.append("     'LONG', 'Long', 'FLOAT', 'Float '       			  ");
		sql.append("   ) || substr(                                           ");
		sql.append("     lower(t.COLUMN_NAME),                                ");
		sql.append("     0,                                                   ");
		sql.append("     case when instr(                                     ");
		sql.append("       lower(t.COLUMN_NAME),                              ");
		sql.append("       '_'                                                ");
		sql.append("     ) > 0 then instr(                                    ");
		sql.append("       lower(t.COLUMN_NAME),                              ");
		sql.append("       '_'                                                ");
		sql.append("     ) -1 else length(                                    ");
		sql.append("       lower(t.COLUMN_NAME)                               ");
		sql.append("     ) end                                                ");
		sql.append("   ) || REGEXP_REPLACE(                                   ");
		sql.append("     INITCAP(                                             ");
		sql.append("       substr(                                            ");
		sql.append("         lower(t.COLUMN_NAME),                            ");
		sql.append("         case when instr(                                 ");
		sql.append("           lower(t.COLUMN_NAME),                          ");
		sql.append("           '_'                                            ");
		sql.append("         ) > 0 then instr(                                ");
		sql.append("           lower(t.COLUMN_NAME),                          ");
		sql.append("           '_'                                            ");
		sql.append("         ) + 1 else length(                               ");
		sql.append("           lower(t.COLUMN_NAME)                           ");
		sql.append("         ) + 1 end,                                       ");
		sql.append("         length(                                          ");
		sql.append("           lower(t.COLUMN_NAME)                           ");
		sql.append("         )                                                ");
		sql.append("       )                                                  ");
		sql.append("     ),                                                   ");
		sql.append("     '(\\w)[_]',                                           ");
		sql.append("     '\\1'                                                 ");
		sql.append("   ) || ';  //' || c.comments col                         ");
		sql.append(" from                                                     ");
		sql.append("   all_tab_cols t,                                        ");
		sql.append("   all_col_comments c                                     ");
		sql.append(" where                                                    ");
		sql.append("   t.TABLE_NAME = '"+tableName+"'     					  ");
		sql.append("   and c.table_name = t.TABLE_NAME                        ");
		sql.append("   and c.column_name = t.COLUMN_NAME                      ");

		Connection conn = getJDBCConn();
		StringBuilder resultSB = new StringBuilder();
		try {
			Statement stat = conn.createStatement();
			ResultSet result = stat.executeQuery(sql.toString());
			while (result.next()) {
				String column = result.getString(1);
				resultSB.append(column).append("\r\n");
				System.out.println(column);
			}
			stat.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSB.toString();
	}

	/**
	 * 获取connection
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Connection getJDBCConn() throws Exception {
		Connection conn = null;
		Properties props = getProperties("jdbc.properties");
		Class.forName(props.getProperty("jdbc.class"));
		String url = props.getProperty("jdbc.url");
		conn = DriverManager.getConnection(url, props.getProperty("jdbc.user"), props.getProperty("jdbc.password"));

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
			in = SQLUtil.class.getClassLoader().getResourceAsStream(filePath);
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

	public static String getSegmentsWithoutType(String sql) {
		StringBuilder sb = new StringBuilder();
		sql = sql.toLowerCase();
		String segment = sql.replaceAll("from[\\s\\S]*", "").replace("select", "");
		String[] segments = segment.split(",");// 分组
		Map<String, String> map = new HashMap<>();
		for (String str : segments) {
			String[] strs = str.split(" ");
			String key = null;
			String value = null;
			for (String str2 : strs) {
				str2 = str2.replaceAll("\\s*", "");
				if (!StringUtil.isBlank(str2)) {
					if (key == null) {
						key = str2;
					} else {
						value = str2;
					}
				}
			}
			key = key.replaceAll(".*\\.", "");// 去除表别名
			key = underline2Camel(key, true);
			sb.append("private String ").append(key).append(";\r\n");
			map.put(key, value);
			key = null;
			// str = str.replaceAll("\\s*", "");// 去除空白字符串
			// str = str.replaceAll(".*\\.", "");//去除表别名
			System.out.println(str);
		}
		return sb.toString();
	}

	/**
	 * 下划线转驼峰法
	 * 
	 * @param line       源字符串
	 * @param smallCamel 大小驼峰,是否为小驼峰
	 * @return 转换后的字符串
	 */
	public static String underline2Camel(String line, boolean smallCamel) {
		if (line == null || "".equals(line)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
					: Character.toUpperCase(word.charAt(0)));
			int index = word.lastIndexOf('_');
			if (index > 0) {
				sb.append(word.substring(1, index).toLowerCase());
			} else {
				sb.append(word.substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * 驼峰法转下划线
	 * 
	 * @param line 源字符串
	 * @return 转换后的字符串
	 */
	public static String camel2Underline(String line) {
		if (line == null || "".equals(line)) {
			return "";
		}
		line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			String word = matcher.group();
			sb.append(word.toUpperCase());
			sb.append(matcher.end() == line.length() ? "" : "_");
		}
		return sb.toString();
	}

}
