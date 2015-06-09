package com.xujun.util;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/***
 * 接口 urls
* @ClassName: URLs
* @Description: TODO(这里用一句话描述这个类的作用)
* @author xujunwu
* @date 2013-5-11 下午2:39:25
*
 */
public class URLs implements Serializable {
	
	public final static String HOST = "y.mchome.cn";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;

    public final static String IMAGE_URL="http://app.woicar.cn:8089/nadmin/images/";
	
	public final static String LOGIN_VALIDATE_HTTP = HTTP + HOST + URL_SPLITTER + "index.php?/ums/login";
    public final static String REGISTER_USER=HTTP + HOST + URL_SPLITTER + "index.php?/ums/register";
    public final static String LOGIN_VALIDATE_HTTPS = HTTPS + HOST + URL_SPLITTER + "action/api/login_validate";

    public final static String INIT_CONFIG_URL = HTTP + HOST + URL_SPLITTER + "index.php?/ums/getConfig";
    public final static String INFO_GET_URL= HTTP + HOST + URL_SPLITTER + "index.php?/ums/getInfo";

    public final static String WARN_SYNC_URL=HTTP+HOST+URL_SPLITTER+"index.php?/ums/syncwarns";
    public final static String WEIGHT_HIS_SYNC_URL=HTTP+HOST+URL_SPLITTER+"index.php?/ums/syncweighthis";
    public final static String WEIGHT_SYNC_URL=HTTP+HOST+URL_SPLITTER+"index.php?/ums/syncweight";
    public final static String ACCOUNT_SYNC_URL=HTTP+HOST+URL_SPLITTER+"index.php?/ums/syncaccount";

    public final static String SYNC_WEIGHT_HIS_URL=HTTP+HOST+URL_SPLITTER+"index.php?/ums/getWeightHiss";

    public final static String SYNC_WEIGHT_URL=HTTP+HOST+URL_SPLITTER+"index.php?/ums/getWeights";

    public final static String CATEGORY_LIST_URL=URL_API_HOST+"index.php/ums/index";

    public final static String CATEGORY_CONTENT_URL=URL_API_HOST+"index.php/ums/getInfo/";

    public final static String INPUTS_LIST = URL_API_HOST+"index.php/ums/getInputs";

	public final static String ADS_LIST = URL_API_HOST+"index.php/ums/ads";

	public final static String UPDATE_PASS = URL_API_HOST+"index.php/ums/upass";

	public final static String ADD_SERVICE = URL_API_HOST+"index.php/ums/addService";

	public final static String ADD_TRAVEL = URL_API_HOST+"index.php/ums/addTravel";
	public final static String ADD_BUYCAR = URL_API_HOST+"index.php/ums/addBuyCar";
	public final static String ADD_SALECAR = URL_API_HOST+"index.php/ums/addSaleCar";
	public final static String CHANGE_CAR_INFO = URL_API_HOST+"index.php/ums/changeCar";

	public final static String MESSAGES_LIST = URL_API_HOST+"index.php/ums/messages";

	public final static String RATING = URL_API_HOST+"index.php/ums/rating";
    public final static String CATEGORY=URL_API_HOST+"index.php/ums/getCategory";

    public final static String ADD_NOTIFY_SET=URL_API_HOST+"index.php/ums/addNotify";


	public final static String SELLERS_LIST = URL_API_HOST+"index.php/ums/store";

    public final static String PDFS_LIST = URL_API_HOST+"index.php/ums/pdfs";

    public final static String EDAIJIA_URL = "http://open.d.api.edaijia.cn";


    public final static String VERSION = URL_API_HOST+"index.php/ums/version";

	public final static String FEEDBACK = URL_API_HOST+"index.php/ums/feedback";

	public final static String APPINFO = URL_API_HOST+"index.php/ums/getAppInfo";
	public final static String TOP_APPINFO=URL_API_HOST+"index.php/ums/getTopApps";

	public final static String DICTS_LIST = URL_API_HOST+"index.php/ums/getDicts";
	public final static String COUPON_LIST = URL_API_HOST+"index.php/ums/coupons";
	
	
	
	public final static String UPDATE_VERSION = URL_API_HOST+"MobileAppVersion.json";
	
	private final static String URL_HOST = "penshine.com";
	private final static String URL_WWW_HOST = "www."+URL_HOST;
	private final static String URL_MY_HOST = "my."+URL_HOST;
	
	private final static String URL_TYPE_NEWS = URL_WWW_HOST + URL_SPLITTER + "news" + URL_SPLITTER;
	private final static String URL_TYPE_SOFTWARE = URL_WWW_HOST + URL_SPLITTER + "p" + URL_SPLITTER;
	private final static String URL_TYPE_QUESTION = URL_WWW_HOST + URL_SPLITTER + "question" + URL_SPLITTER;
	private final static String URL_TYPE_BLOG = URL_SPLITTER + "blog" + URL_SPLITTER;
	private final static String URL_TYPE_TWEET = URL_SPLITTER + "tweet" + URL_SPLITTER;
	private final static String URL_TYPE_ZONE = URL_MY_HOST + URL_SPLITTER + "u" + URL_SPLITTER;
	private final static String URL_TYPE_QUESTION_TAG = URL_TYPE_QUESTION + "tag" + URL_SPLITTER;
	
	public final static int URL_OBJ_TYPE_OTHER = 0x000;
	public final static int URL_OBJ_TYPE_NEWS = 0x001;
	public final static int URL_OBJ_TYPE_SOFTWARE = 0x002;
	public final static int URL_OBJ_TYPE_QUESTION = 0x003;
	public final static int URL_OBJ_TYPE_ZONE = 0x004;
	public final static int URL_OBJ_TYPE_BLOG = 0x005;
	public final static int URL_OBJ_TYPE_TWEET = 0x006;
	public final static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;
	
	private int objId;
	private String objKey = "";
	private int objType;
	
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}
	
	/**
	 * 转化URL为URLs实体
	 * @param path
	 * @return 不能转化的链接返回null
	 */
	public final static URLs parseURL(String path) {
		if(StringUtil.isEmpty(path))return null;
		path = formatURL(path);
		URLs urls = null;
		String objId = "";
		try {
			URL url = new URL(path);
			//站内链接
			if(url.getHost().contains(URL_HOST)){
				urls = new URLs();
				//www
				if(path.contains(URL_WWW_HOST )){
					//新闻  www.oschina.net/news/27259/mobile-internet-market-is-small
					if(path.contains(URL_TYPE_NEWS)){
						objId = parseObjId(path, URL_TYPE_NEWS);
						urls.setObjId(StringUtil.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_NEWS);
					}
					//软件  www.oschina.net/p/jx
					else if(path.contains(URL_TYPE_SOFTWARE)){
						urls.setObjKey(parseObjKey(path, URL_TYPE_SOFTWARE));
						urls.setObjType(URL_OBJ_TYPE_SOFTWARE);
					}
					//问答
					else if(path.contains(URL_TYPE_QUESTION)){
						//问答-标签  http://www.oschina.net/question/tag/python
						if(path.contains(URL_TYPE_QUESTION_TAG)){
							urls.setObjKey(parseObjKey(path, URL_TYPE_QUESTION_TAG));
							urls.setObjType(URL_OBJ_TYPE_QUESTION_TAG);
						}
						//问答  www.oschina.net/question/12_45738
						else{
							objId = parseObjId(path, URL_TYPE_QUESTION);
							String[] _tmp = objId.split(URL_UNDERLINE);
							urls.setObjId(StringUtil.toInt(_tmp[1]));
							urls.setObjType(URL_OBJ_TYPE_QUESTION);
						}
					}
					//other
					else{
						urls.setObjKey(path);
						urls.setObjType(URL_OBJ_TYPE_OTHER);
					}
				}
				//my
				else if(path.contains(URL_MY_HOST)){					
					//博客  my.oschina.net/szpengvictor/blog/50879
					if(path.contains(URL_TYPE_BLOG)){
						objId = parseObjId(path, URL_TYPE_BLOG);
						urls.setObjId(StringUtil.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_BLOG);
					}
					//动弹  my.oschina.net/dong706/tweet/612947
					else if(path.contains(URL_TYPE_TWEET)){
						objId = parseObjId(path, URL_TYPE_TWEET);
						urls.setObjId(StringUtil.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_TWEET);
					}
					//个人专页  my.oschina.net/u/12
					else if(path.contains(URL_TYPE_ZONE)){
						objId = parseObjId(path, URL_TYPE_ZONE);
						urls.setObjId(StringUtil.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_ZONE);
					}
					else{
						//另一种个人专页  my.oschina.net/dong706
						int p = path.indexOf(URL_MY_HOST+URL_SPLITTER) + (URL_MY_HOST+URL_SPLITTER).length();
						String str = path.substring(p);
						if(!str.contains(URL_SPLITTER)){
							urls.setObjKey(str);
							urls.setObjType(URL_OBJ_TYPE_ZONE);
						}
						//other
						else{
							urls.setObjKey(path);
							urls.setObjType(URL_OBJ_TYPE_OTHER);
						}
					}
				}
				//other
				else{
					urls.setObjKey(path);
					urls.setObjType(URL_OBJ_TYPE_OTHER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			urls = null;
		}
		return urls;
	}

	/**
	 * 解析url获得objId
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type){
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		}else{
			objId = str;
		}
		return objId;
	}
	
	/**
	 * 解析url获得objKey
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type){
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains("?")){
			tmp = str.split("?");
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		return objKey;
	}
	
	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}	
}
