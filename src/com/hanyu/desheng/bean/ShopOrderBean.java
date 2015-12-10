package com.hanyu.desheng.bean;

import java.io.Serializable;
import java.util.List;

public class ShopOrderBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int code;
	public String msg;
	public Data data;

	public class Data implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public List<Order> order_list;// 总订单列表

	}

	public class Order implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String vono;// 总订单编号
		public String vonum;// 数量
		public String vostate;// 订单状态
		public String vototal;// 总价
		public Object order_goods;// 总订单显示商品列表
		public List<Son> son_order;// 子订单列表

	}

	public class Son implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String son_sn;// 子订单编号
		public List<Goods> order_goods;// 子订单商品列表

		public class Goods implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			public String giimgs;// 商品缩略图
			public String gititle;// 商品名称
			public String osno;// 商品编号
			public String vognum;// 商品数量
			public String vogprice;// 商品价格
		}

	}

}
