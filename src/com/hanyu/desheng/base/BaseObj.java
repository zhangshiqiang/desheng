package com.hanyu.desheng.base;

import java.lang.reflect.Field;

public abstract class BaseObj {

	public abstract String[] getNodes();

	public void setParamater(String tag, Object value) {

		try {

			Field field = getClass().getField(tag);

			field.setAccessible(true);

			field.set(this, value);

		} catch (SecurityException e) {

			e.printStackTrace();

		} catch (NoSuchFieldException e) {

			e.printStackTrace();

		} catch (IllegalArgumentException e) {

			e.printStackTrace();

		} catch (IllegalAccessException e) {

			e.printStackTrace();

		}

	}

}
