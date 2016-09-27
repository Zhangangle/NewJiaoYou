package com.zmv.zf.utils;

import java.io.File;
import java.io.FileWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

/**
 * <h3>用作系统抛出FC错误时,进行处理</h3><br/>
 * <h4>1.使用方式:</h4><br/>
 * ① 编写自定义Application 继承 Application并实现onCreate()方法<br/>
 * ② 在onCreate()方法中实现类似如下方法.例:<br/>
 * a.实例化<br/>
 * CatchHandler catchHandler = CatchHandler.getInstance();<br/>
 * b.初始化<br/>
 * catchHandler.init(getApplicationContext());<br/>
 * c.设置处理参数<br/>
 * catchHandler.setParam(int,String,String,String,String);<br/>
 * ③ 在Appmanifest文件中添加Application name属性为自定义Application<br/>
 * <h4>2.注意事项:</h4><br/>
 * ① 因为需要将错误信息写入文件,所以,Appmanifest文件中需添加WRITE_EXTERNAL_STORAGE权限<br/>
 * ② 因为需要将错误信息文件上传到服务器,所以,Appmanifest文件中需添加INTERNET权限<br/>
 * 
 * @author Jacky
 * @since 2014-9-1
 * @version 1.0.<br/>
 *          修改Toast显示时间通过setParam传参
 **/
public class CatchHandlerUtils implements UncaughtExceptionHandler {

	private static String LOG = "catchExceptionLog";// 异常日志

	private static CatchHandlerUtils instance; // 单例引用
	private Context context;
	private int catchCase;// 选择的处理方式
	Class<? extends Service> classname;
	String path;
	/**
	 * 处理方式1:仅保存文件
	 **/
	public static int CATCHMETHOD_SAVEFILE = 1;

	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private CatchHandlerUtils() {
	}

	public synchronized static CatchHandlerUtils getInstance() { // 同步方法，以免单例多线程环境下出现异常
		if (instance == null) {
			instance = new CatchHandlerUtils();
		}
		return instance;
	}

	/**
	 * 初始化，把当前对象设置成UncaughtExceptionHandler处理器
	 * 
	 * @param ctx
	 *            上下文对象
	 **/
	public void init(Context context) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		this.context = context;
	}

	public void initAndService(Context context, Class<? extends Service> aclass) {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		this.context = context;
		classname = aclass;

	}

	/**
	 * 设置报错处理参数
	 * 
	 * @param catchCase
	 *            选择的处理方式 1:仅保存文件;2:保存文件并上传到服务器
	 * @param filePath
	 *            文件保存路径(必传)
	 * @param fileName
	 *            文件名称(必传)
	 * @param serverUrl
	 *            服务器URL 可为空字符串或null;当catchCase为2时,不得为空字符串或null
	 * @param dialogText
	 *            Toast提示内容(必传)
	 * @param toashShowTime
	 *            Toast提示时间
	 * @since 2014-9-1 1.0
	 * @author Jacky
	 **/
	/*
	 * @param applicationName Application的类名(必传)
	 * 
	 * @param showMethod Application中定义的提示方法(必传)
	 */
	public void setParam(int catchCase, String path) {
		this.catchCase = catchCase;
		this.path = path;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {// 出现异常时调用

		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {

			// Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
			// mHomeIntent.addCategory(Intent.CATEGORY_HOME);
			// mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
			// | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			// context.startActivity(mHomeIntent);
			if (classname != null) {
				context.stopService(new Intent(context, classname));
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);

		}

	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 报错时错误信息处理方式
		switch (getCatchCase()) {
		case 1:// CATCHMETHOD_SAVEANDUPLOAD
			saveExceptionMessageToFile(ex);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 将错误信息保存至文件
	 * 
	 * @param filePath
	 *            文件保存路径
	 * @param fileName
	 *            文件名称
	 * @param exception
	 *            异常信息
	 * @since 2014-9-1 1.0
	 * @author Jacky
	 **/
	private void saveExceptionMessageToFile(Throwable exception) {

		FileWriter fw = null;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
			String fileName = "l" + df.format(new Date());

			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志时间
			// 创建文件夹
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 创建文件 并获取文件写入流
			File file = new File(dir, fileName);
			fw = new FileWriter(file, true);// true代表往后追加的模式

			fw.write("-----------Data----------\n");
			fw.write("Time:" + df2.format(new Date()) + "\n");
			// 获取异常详细信息
			StackTraceElement[] stacks = exception.getStackTrace();
			for (int i = 0; i < stacks.length; i++) {
				fw.write("----------------Exception----------------\n");
				fw.write("  ClassName:" + stacks[i].getClassName() + "\n");
				fw.write("-----------------------------------------\n");
				fw.write("  FileName:" + stacks[i].getFileName() + "\n");
				fw.write("-----------------------------------------\n");
				fw.write("  LineNumber:" + stacks[i].getLineNumber() + "\n");
				fw.write("-----------------------------------------\n");
				fw.write("  MethodName:" + stacks[i].getMethodName() + "\n");
				fw.write("-----------------------------------------\n");
				fw.write("  Description:" + exception.toString() + "\n");
				fw.write("-------------------end-------------------\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null) {
					fw.close();
				}
			} catch (Exception e2) {
			}
		}

	}

	/*-------------------------------------------*/

	public int getCatchCase() {
		return catchCase;
	}

	public Context getContext() {
		return context;
	}

}