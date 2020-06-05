package com.xx.tool.vo.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.xx.tool.vo.bean.DBConfig;

public class XMLUtil {

	private static String filePath = "db.xml";

	/**
	 * ∂¡»°≈‰÷√
	 *
	 * @return
	 * @throws JAXBException
	 */
	public static DBConfig loadDBConfigFromFile() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(DBConfig.class);
		Unmarshaller um = context.createUnmarshaller();

		// Reading XML from the file and unmarshalling.
		DBConfig config = (DBConfig) um.unmarshal(new File(filePath));

		return config;
	}

	/**
	 * Saves the current person data to the specified file.
	 *
	 * @throws JAXBException
	 */
	public static void saveDBConfigToFile(DBConfig config) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(DBConfig.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		m.marshal(config, new File(filePath));
	}

	public static void main(String[] args) throws JAXBException {
//		DBConfig config = new DBConfig();
//		saveDBConfigToFile(file, config);
		DBConfig config = loadDBConfigFromFile();
		System.out.println(config);
	}
}
