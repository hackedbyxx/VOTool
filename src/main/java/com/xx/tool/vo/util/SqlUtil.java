package com.xx.tool.vo.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author SN78083
 *
 */
public class SqlUtil {

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

	/*public static void main(String[] args) throws Exception {
//		getSegmentsWithType(sql);
		Pattern r = Pattern.compile(" \\S*;");
		String s1 = "private Date field0003;  //";
		Matcher m = r.matcher(s1);
	    if (m.find( )) {
	    	System.out.println(m.group());
	    }
	}*/

	/**
	 * �����ݿ��в�ѯ���Ͳ����
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static String getSegmentsWithType(String sql) throws Exception {
		sql = sql.toLowerCase();
		sql = sql.replaceAll("--[\\s\\S]*?\n", "\n");//ȥ��ע��
		// ��ȡ���б�
		String allTable = sql.replaceAll("[\\s\\S]*from", "").replaceAll("where[\\s\\S]*", "");
		String[] tables = allTable.split(",");
		Map<String, String> tableName = new HashMap<>();
		for (String table : tables) {//��ȡ���б�
			String[] strs = table.split(" ");
			String tName = null;// ����
			String alias = null;// ����
			for (String str2 : strs) {
				str2 = str2.replaceAll("\\s*", "");//ȥ������Ŀո�
				if (!StringUtil.isBlank(str2)) {//������ǰ�������ں�
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
			if(alias!=null) {//�б�������ȥ
				tableName.put(alias, tName);
			}
		}
		List<String> result = new ArrayList<>();
		// ��ȡ�����ֶ�
		String allSegment = sql.replaceAll("from[\\s\\S]*", "").replace("select", "");
		String[] segments = allSegment.split(",");// ����
		Map<String, List<String>> tableColumns = new HashMap<>();//�洢���������
		Map<String,String> columnAliasMap = new HashMap<>();//�洢�����ֶκ��ֶα���
		for (String segment : segments) {
			String[] strs = segment.split(" ");
			String key = null;//�����
			String value = null;//����
			String column = null;//�ֶ���
			String columnAlias = null;//�ֶα���
			for (String str : strs) {
				str = str.replaceAll("\\s*", "");// ȥ�����ַ�
				if (!StringUtil.isBlank(str)&&!str.contains("--")) {//����--��ͷע��������
					if (key == null) {
						column = str;
						String[] kv = str.split("\\.");
						key = kv[0];
						value = kv[1];
					}else {//�ֶα����洢
						columnAliasMap.put(column, str);
						/*if(columnAliasMap.get(value)!=null&&columnAliasMap.get(value).equals(columnAlias)) {//����Ѿ�
							
						}*/
					}
				}
			}
			//��ӱ���
			if(value != null) {
				if(tableName.get(key)!=null) {//����б������ȡʵ�ʱ���
					key = tableName.get(key);
				}
				List<String> list = tableColumns.get(key);
				if (list == null) {
					list = new ArrayList<>();
				}
				if(list.contains(value)) {//��������ظ���ʹ�ñ���
					Set<Entry<String, String>> entrySet = columnAliasMap.entrySet();
					Iterator<Entry<String, String>> iterator = entrySet.iterator();
					while(iterator.hasNext()) {
						Entry<String, String> entry = iterator.next();
						if(entry.getKey().matches("[\\s\\S]*\\."+value)) {//������ϱ���������ǰ׺
							result.add("private "+value+" "+StringUtil.underline2Camel(entry.getValue(), true) + ";  //");
							//�ر�ע�⣺����ʹ��map.remove(name)  ����ᱨͬ���Ĵ���
							//columnAliasMap.remove(entry.getKey());//�Ƴ��Ѿ���ӵģ������ظ�
							iterator.remove();
						}
					}
					
				}else {
					list.add(value);
					tableColumns.put(key, list);
				}
			}
		}
		Set<Entry<String, List<String>>> entrySet = tableColumns.entrySet();

		for (Entry<String, List<String>> entry : entrySet) {
			String table = tableName.get(entry.getKey()) == null ? entry.getKey() : tableName.get(entry.getKey());
			List<String> cols = getColumnBy(table, entry.getValue());
			result.addAll(cols);
		}
		String resultStr = "";
		Collections.sort(result,new Comparator<String>() {//�����ֶ�������
			@Override
			public int compare(String s1, String s2) {
				s1 = SqlUtil.getField(s1);
				s2 = SqlUtil.getField(s2);
				return s1.compareTo(s2);
			}
		});
		for (String str : result) {
			resultStr += str + "\r\n\r\n";
		}
		return resultStr;
	}
	
	
	public static String getField(String str) {
		Pattern r = Pattern.compile(" \\S*;");
//		String s1 = "private Date field0003;  //";
		Matcher m = r.matcher(str);
	    if (m.find( )) {
	    	return m.group(0).replaceAll(" ", "");
	    }
	    return "";
	}

	/**
	 * ���ݱ����������ֶ�����field
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
		sql.append("     'CHAR', 'String ', 'DATE', 'Date ','TIMESTAMP(6)','Date ', ");
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

		Connection conn = DBUtil.getJDBCConn();
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
		sql.append("     'CHAR', 'String ', 'DATE', 'Date ','TIMESTAMP(6)','Date ', ");
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

		Connection conn = DBUtil.getJDBCConn();
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

	public static String getSegmentsWithoutType(String sql) {
		StringBuilder sb = new StringBuilder();
		sql = sql.toLowerCase();
		String segment = sql.replaceAll("from[\\s\\S]*", "").replace("select", "");
		String[] segments = segment.split(",");// ����
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
			key = key.replaceAll(".*\\.", "");// ȥ�������
			key = StringUtil.underline2Camel(key, true);
			sb.append("private String ").append(key).append(";\r\n");
			map.put(key, value);
			key = null;
			// str = str.replaceAll("\\s*", "");// ȥ���հ��ַ���
			// str = str.replaceAll(".*\\.", "");//ȥ�������
			System.out.println(str);
		}
		return sb.toString();
	}

	

	//sqlתStringBuilder
	public static String sqlTOStringBuilder(String sql) {
		if (sql == null || "".equals(sql)) {
			return "";
		}
		
		sql = sql.replaceAll("--[\\s\\S]*?\n", "\n");//ȥ��ע��
		
		String sqlBuf = "";

		String Buf = "StringBuilder sql = new StringBuilder();";
		//�ָ��ַ���
		String[] newSrc = sql.split("\n");

		int len = newSrc.length;
		for (int i = 0; i < len; i++) {
			String temp = newSrc[i];
			int maxlengthtemp = 0;
			for (int j = 0; j < len; j++) {
				if (newSrc[j].length() > maxlengthtemp) {
					maxlengthtemp = newSrc[j].length();
				}
			}
			if (i < len) {
				sqlBuf += "sql.append(\" " + temp;
				for (int k = 0; k < maxlengthtemp - newSrc[i].length() + 3; k++) {
					sqlBuf += " ";
				}
				sqlBuf += " \")" + ";" + "\n";
			}
		}
		return Buf + "\n" + sqlBuf;
	}

	//StringBuilderתsql
	public static String StringBuilderToString(String str) {
		if (str == null || "".equals(str)) {
			return "";
		}
		String result = "";
		//�ָ��ַ���
		String[] newSrc = str.split("\n");
		for (int i = 0; i < newSrc.length; i++) {
			//String temp = newSrc[i].replaceAll("sql.append\\(\"", "");
			String temp = newSrc[i].replaceAll("[\\s\\S]*\\(\"", "");
			String temp1 = temp.replaceAll("\"\\);", "");
			result += temp1 + "\n\r";
		}
		return result;
	}

	//VO��װ
	public static String setVO(String sql){
		// ��ȡ�����ֶ�
		String segment = sql.replaceAll("from[\\s\\S]*", "").replace("select", "");
		String[] segments = segment.split(",");// ����

		for (String str : segments) {
			String[] strs = str.split(" ");
			String key = null;
			String value = null;
			for (String str2 : strs) {
				str2 = str2.replaceAll("\\s*", "");// ȥ�����ַ�
				if (!StringUtil.isBlank(str2)) {
					if (key == null) {
						String[] kv = str2.split("\\.");
						// key = str.replaceAll(".*\\.", "");// ȥ�������
						value = kv[1];
					}
				}
			}
		}
		return null;
	}
}