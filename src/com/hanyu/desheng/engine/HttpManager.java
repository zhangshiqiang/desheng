package com.hanyu.desheng.engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.hanyu.desheng.GlobalParams;
import com.hanyu.desheng.utils.MyLogUtils;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

/**
 * 联网工具
 * 
 * @author sto_LiHui
 * 
 */
public class HttpManager {
	private HttpClient client;
	private HttpPost post;
	private HttpGet get;

	private static Header[] headers;

	static {
		headers = new Header[1];
		headers[0] = new BasicHeader("Phone", "Android");
	}

	public HttpManager() {
		// 初始化client
		// 如果是wap方式联网，需要设置代理信息
		client = new DefaultHttpClient();
		// 响应超时时间为5秒
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
		if (GlobalParams.isWap) {
			// 封装代理参数
			@SuppressWarnings("deprecation")
			HttpHost host = new HttpHost(android.net.Proxy.getDefaultHost(),
					android.net.Proxy.getDefaultPort());
			HttpParams params = client.getParams();// map
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, host);

		}
	}

	/**
	 * get请求
	 * 
	 * @param uri
	 *            :自己拼装好get参数的字符串: "http://test.com/api_v270?userId=" + userId +
	 *            "&version=" + version;
	 * @return json字符串
	 */
	public String sendGet(String uri) {
		get = new HttpGet(uri);
		get.setHeaders(headers);
		try {
			HttpResponse response = client.execute(get);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				return EntityUtils.toString(response.getEntity(),
						ConstantValue.ENCODING);
			} else {
				MyLogUtils.error("访问失败--状态码："
						+ response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}

		return null;
	}

	/**
	 * get请求
	 * 
	 * @param uri
	 * @return 流
	 */
	public InputStream sendGetI(String uri) {
		get = new HttpGet(uri);
		get.setHeaders(headers);
		try {
			HttpResponse response = client.execute(get);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				return response.getEntity().getContent();
			} else {
				MyLogUtils.error("访问失败--状态码："
						+ response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}
		return null;

	}

	/**
	 * psot请求
	 * 
	 * @param uri
	 * @return 流
	 */
	public InputStream sendPostI(String uri, Map<String, String> params) {
		post = new HttpPost(uri);
		post.setHeaders(headers);
		try {
			if (params != null && params.size() > 0) {
				List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				for (Map.Entry<String, String> item : params.entrySet()) {
					BasicNameValuePair pair = new BasicNameValuePair(
							item.getKey(), item.getValue());
					parameters.add(pair);
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						parameters, ConstantValue.ENCODING);
				post.setEntity(entity);// 设置需要传递的数据
			}
			HttpResponse response = client.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				return response.getEntity().getContent();
			} else {
				MyLogUtils.error("访问失败--状态码："
						+ response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}
		return null;

	}

	/**
	 * post请求
	 * 
	 * @param uri
	 * @param data
	 *            xml或者json字符串
	 * @return
	 */
	public String sendPost(String uri, String data) {
		post = new HttpPost(uri);
		post.setHeaders(headers);
		try {
			StringEntity entity = new StringEntity(data, ConstantValue.ENCODING);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				// InputStream inputStream = response.getEntity().getContent();
				return EntityUtils.toString(response.getEntity(),
						ConstantValue.ENCODING);
			} else {
				MyLogUtils.error("访问失败--状态码："
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}
		return null;

	}

	/**
	 * post请求
	 * 
	 * @param uri
	 * @param params
	 *            参数集合
	 * @return json字符串
	 */
	public String sendPost(String uri, Map<String, String> params) {
		post = new HttpPost(uri);
		post.setHeaders(headers);
		try {
			if (params != null && params.size() > 0) {
				List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				for (Map.Entry<String, String> item : params.entrySet()) {
					BasicNameValuePair pair = new BasicNameValuePair(
							item.getKey(), item.getValue());
					parameters.add(pair);
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						parameters, ConstantValue.ENCODING);
				post.setEntity(entity);// 设置需要传递的数据
			}
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				// return reponse.getEntity().getContent();
				return EntityUtils.toString(response.getEntity(),
						ConstantValue.ENCODING);
			} else {
				MyLogUtils.error("访问失败--状态码："
						+ response.getStatusLine().getStatusCode());

			}
		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}
		return null;
	}

	/**
	 * 文件上传 post
	 * 
	 * @param uri
	 * @param params
	 * @param fileMaps
	 * @return
	 */
	public String upLoad(String uri, Map<String, String> params,
			Map<String, FileBody> fileMaps) {
		post = new HttpPost(uri);
		post.setHeaders(headers);
		try {
			MultipartEntity mpEntity = new MultipartEntity();
			if (params != null && params.size() > 0) {
				for (Map.Entry<String, String> item : params.entrySet()) {
					StringBody par = new StringBody(item.getValue());
					mpEntity.addPart(item.getKey(), par);
				}

			}

			if (fileMaps != null && fileMaps.size() > 0) {
				for (Map.Entry<String, FileBody> entry : fileMaps.entrySet()) {
					mpEntity.addPart(entry.getKey(), entry.getValue());
				}
			}
			post.setEntity(mpEntity);// 设置需要传递的数据
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(),
						ConstantValue.ENCODING);
			} else {
				MyLogUtils.error("访问失败--状态码："
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}
		return null;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            访问的地址
	 * @param content
	 *            访问带过去的参数 json格式
	 * @return
	 */
	public String postContentToServer(String url, String content) {
		StringEntity entity = null;
		try {
			entity = new StringEntity(content, ConstantValue.ENCODING);
			entity.setContentType("application/json");
			post = new HttpPost(url);
			post.addHeader("Content-Type", "application/json; charset=utf-8");
			post.setEntity(entity);

			HttpResponse httpResponse = null;

			httpResponse = client.execute(post);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				return EntityUtils.toString(httpResponse.getEntity(),
						ConstantValue.ENCODING);

			}

		} catch (Exception e) {
			e.printStackTrace();
			MyLogUtils.error("访问异常：" + e.getMessage());
		}

		return null;
	}

}
