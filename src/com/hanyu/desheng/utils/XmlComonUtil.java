package com.hanyu.desheng.utils;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hanyu.desheng.base.BaseObj;

public class XmlComonUtil {
	private static String encode = "utf-8";

	public static XmlPullParser pullParser;

	static {

		try {
			pullParser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @description :解析节点中的内容，封装成对象模型。
	 * 
	 * @author : kwzhang
	 * 
	 * @create :2013-2-28
	 * 
	 * @param in
	 * 
	 * @param obj
	 * 
	 * @throws Exception
	 * 
	 * @return :void
	 */

	public static <T extends BaseObj> void streamText2Model(InputStream in,
			T obj) throws Exception {

		pullParser.setInput(in, encode);

		int eventType = pullParser.getEventType();

		String[] nodes = obj.getNodes();

		String nodeName = null;

		boolean success = true;

		while (eventType != XmlPullParser.END_DOCUMENT && success) {

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:

				break;

			case XmlPullParser.START_TAG:

				nodeName = pullParser.getName();

				break;

			case XmlPullParser.TEXT:

				if ("IsLog".equals(nodeName)
						&& pullParser.getText().equals("false")) {

					success = false;

					break;

				}

				for (int i = 0; i < nodes.length; i++) {

					if (nodes[i].equals(nodeName)) {

						obj.setParamater(nodeName, pullParser.getText());

					}

				}

				break;

			case XmlPullParser.END_TAG:

				break;

			}

			eventType = pullParser.next();

		}

	}

}
