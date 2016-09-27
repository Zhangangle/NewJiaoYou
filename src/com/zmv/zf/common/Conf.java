package com.zmv.zf.common;

public class Conf {

	/** 后台服务器URL地址 ***/
	// public static final String URL="http://114.215.133.82:8000/";//外网测试地址
//	public static final String URL = "http://api.347.cc/"; // 外网服务器
	// public static final String URL = "http://10.0.0.190:8000/"; // 内网测试服务器
//	public static final String APPKEY = "32f31eff6157";// 27fe7909f8e8
//	// 填写从短信SDK应用后台注册得到的APPSECRET
//	public static final String APPSECRET = "d3b11bc0661b33924e81eec447723582";// 3c5264e7e05b8860a9b98b34506cfa6e

	// 版本更新地址
//	public static final String UPDATE_SERVERURL = "http://up.347.cc/wzmup.txt";
//	//sdk版本
//	public static final String SDK_VERSION="20160113";
	public static String CID = "";
	public static boolean VIP = false;
	public static String UID = "";
	public static String NICK = "";
	public static String PublicNetwork = "";
	public static String IMEI = ""; // 设备唯一码
	public static String IMSI = ""; // 设备唯一码
	public static String version = ""; // 软件版本
//	public static String SIM = ""; // SIM类型
//	public static String Model = ""; // 手机型号
//	public static String Address = "";// 地址
	public static String OPUID = "";
	public static int OPEN = 0;

//	public static int operators = 1;
//	public static String filename = "";
	// 屏幕密度
	public static int width = 0;
	public static int height = 0;
//	public static final String PAY_SERVERURL = "http://c.juzixiangshui.com/json/1003_sdk_v2.json";// http://c.juzixiangshui.com/json/1003_sdk_v2.json
//	public static final String PAY_PROVINCEURL = "http://s.juzixiangshui.com/sdk/priority";
	// 支付宝支付
//	public static final String RSA_PRIVATE = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANPUXmMn8YwXr0qlArE6Es6gCvczTULufIYBavPzJEZ5aMF0iDWAqWYTWANuAywN2z0/F9rZ9UGjYg64KleyFkD5R1WCI3k6V3hTAp+5XQFWPsQ8WDEuCodrv2xqefS54RjxoEfgNGiUnNmgejks2VvxA0J/ZeU8yNVmBwctzK0rAgMBAAECgYAlDdkU7z/JibsYC5VU+xufGU/Rvh+dvan6pvTWh4mo6kPw0zmgYkk/mdKjhvL3GdVn+Ulq9wV33eShPoXpoTVTBsxy8pyJuAhjF+gbuPIRW9X8qcmHQdyP0PzqTZ4+TpF31mt2jUJ6vavVZ2jZJuLDaD1uOnidpbLzSyk31bOMAQJBAOzwFVIesGj8hfbvf64x2G6+0kBmWgBKKwWVzAUHmtz+64B90NUZkxReuwJphMsfzeeKOIgFFNpCXI06Ad00c8ECQQDk3ylXgs/ckB0Mry5P0W2NHpJp2jxlOZ/Nq/b7rQm4zU++l+c3kI/TWE/Z82Tj3tc3AkX+XwZfp1IfcapodSvrAkEAgS/CID1VaFjPXj1le3/4ByIKF0z3I5NM88WiMNfdq0Ne+ncfJyHDnOatxnnsCqhzpxwpCJjkrsBiICqRm5+ewQJBAL9WRZSwep/1JyQEjB4UgpaYuWSfGyCxBQUHv8p1xgvmMMWw7o8IXwUb7l3SWkUadie50dQvkv+CvWgmOYVsd5MCQQDpzbxh6o6dJYVKpBX2GPYmanMbkcJULtG5BtT+sr0V2IUFLyCuCoSSE2k7zRg49eJduYrDU2k3XaWpdoTxCOEG";
//	public static final String RSA_PRIVATE  = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANugVV+ud+jcOp1AHIuvlH2B7jw9+JkifRBlidBWJW22vuCRjvgp+Qdrgkl6KQFCz5CxWVdB3qhQhzj1DddLsMz0BdZB+I9CkXCdjf02e2r2nyJ+BXukyh9MRWAO+7skl6kZQttDS6OzArQ/QCOplMj6wwaPCVuKgq4q0tprnuSDAgMBAAECgYEAk7vCMd52IlkhxG0/xmaRB+vCiRtWggvE/KaQkWehPa/TrdO7zArPzMHwneRieMqsPLRPWHaig63Hh/SvHsd3YObrp4TQGqmfgPBWPFnU4y8WOSHp+Vbch9yUTJaG3agiFIo9XbK/tKdHhZgbkOA3in9qx+G4MeUgMskdnKh+HSECQQD3UVfHlGqoJ4CZsty9kJp0LrLUlDWtyGtq5jkb7Lj+avtSc92C3OYHLS+9klCnV4Bnv6KLS/OTEmyzaPeJZY7tAkEA41YgTrlZBQT+z/3lTyssqBuDDome+J8s3tyx3KttYIyJ1yZ4O/nn1i2qC7fyNKpw7ZLCpOXfSHhc6i7IcARjLwJAbFfgInhsOPoFJk/qz8iXQSpsASBkW5sfI5dzkT4k1J+9NaO4eGmZA6/R6DQ+zPBSOiEDbg55yAYcRIRJXJF3CQJBAKtxeEvmW8v9SG1y/EwIM1VT6jI8dzboU95+cWQJpCL72bf3uxUAEmbjaSoNPwuYvjFsTH4vHYR5wRiaz24q4OECQDJ6l3i7i3EgegyOho26lykRvUYGzCcsnUpFGyDXyLv0SCDH+uYipp2MMj0OnipYAWsszm8GRvSM0HmRMT0ZTMM=";
//	public static final String RSA_PRIVATE  = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALVfXwt4Xe+H4I1DePHqRzd4/36t9Jg3c7CT6DMc4NDf+k43f7Ui9OPGcq2BjpxZfoeDjiV9PIsPvpkhi5r7EPmr6krrmLvBIxtNKCc80G/6w8jrvEvs7izB5HPc8+gSYzBoh7M1nVGd+6gpFNvqFGa4oWFXm6zX93DW++CnzMEZAgMBAAECgYAfaehVXUCv1xHuhawyAkjZc8yUzV5a6ufPwp5qE+tgYhrBZ11oKyjNNrbwFEt2Cw4ePIvnNFzDHTP+48+BRVD4Eq2OQ3clChf1ejIM7R/8RKHS9+igsTsvRTwjlKpuyTml9xqfO7XkrqmuFi2Oxxup3kMYwOsUmU1KuCAU6Z0UsQJBAONWCAqn//7HkFmheIvA2/3Gz53uuyBi82KSt382AQT6ybC9FdOmQ4BxE1VE5s3XTYhmczpmmVheQ3L5blLoC70CQQDMPbn1XT9Z3oSnzbMEk69mGyLl16flg0vyVWTAepgOgMykERusI6gBS/UE/G7hxWjkhTJzF9u3TpI9FmH3BxKNAkBZtQglVFfzLMKsa8hKtYbZ5Irm0l6rEADko+qe2yuZ0HCoOmUTiKdYVsZ/PlopXc1thqfvoq7eLQxGoWDEfGatAkEAmRQqrJyXPuRoyNyDsIqAjCcKoPph/MPLyHX4bct07Gnc95tbAko3QcGMg5AwC0fKtnyes2TOaTWape6AvoWugQJBAMjNJqIPq7oiB/zW6NlHxGeqjN10k5YTM1Ro9yXuHhimHSMRF62iSRnnE4T0m8OiOs85RlvGH88bxJzVAuEdLcw=";

	
	// 掌支付
//	public static final String ZHANGZHIFU_APPKEY = "98CB32860DC04054A795FC264D209C6E";
//	public static final String ZHANGZHIFU_CHANNELID = "1000100020000213";
//	public static final String ZHANGZHIFU_APPID = "1121";
//	public static final String ZHANGZHIFU_APPKQD = "zyap1121_13407_100";//推广:"zyap1121_13407_100";报备:zyap1121_11875_100
//	public static final String ZHANGZHIFU_APPNAME = "情趣交友";
//	public static String ZHANGYOU_APPVERSION = "";
//	public static String ZHANGYOU_POINTID1 = "5255";
//	public static String ZHANGYOU_POINTTITLE1 = "警告";
//	public static String ZHANGYOU_POINTMSG1 = "警告仅需X.XX元，即可拥有！";
//	public static String ZHANGYOU_POINTID2 = "5256";
//	public static String ZHANGYOU_POINTTITLE2 = "每日礼包";
//	public static String ZHANGYOU_POINTMSG2 = "每日礼包仅需X.XX元，即可拥有！";
//	public static String ZHANGYOU_POINTID3 = "5257";
//	public static String ZHANGYOU_POINTTITLE3 = "视频点播";
//	public static String ZHANGYOU_POINTMSG3 = "视频点播仅需X.XX元，即可拥有！";
}
