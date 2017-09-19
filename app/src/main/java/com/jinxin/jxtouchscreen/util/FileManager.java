package com.jinxin.jxtouchscreen.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.jinxin.jxtouchscreen.app.BaseApp;
import com.jinxin.jxtouchscreen.constant.NetConstant;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {
	private static FileManager instance = null;
	private String SDPath = "";// sd卡路径
	// ///固定文件夹名/////////////////////
	public static final String PROJECT_NAME = "/jxtouchscreen";// 项目文件夹名称
	public static final String CACHE = "/cache";// 缓存区
	private static final String LASTING = "/lasting";// 长存区
	private static final String IMAGES = "/images";// 图片区
	private static final String FILES = "/bin";// 文件区
	public static final String LRC = "/lrc";// 歌词目录
	public static final String VOICE = "/voice";// 语音缓存目录
	
	// 底层目录/////
	public static final String IMAGE_CACHE= "/image_cache";// 图片缓存文件名
	public static final String APK = "/apk/";// apk
	private static final String SITE = "/site/";// 设置
	private static final String VEIN = "/vein/";// 静脉纹
	
	// 后缀名///
	private static final String IMAGE_SUFFIX = ".pg";// 图片后缀
	private static final String FILE_SUFFIX = ".cfg";// 文件后缀
	// 输出LOG///
	private static final String LOG_SUFFIX = "/log.txt";// LOG文件
	
	//APK名字
	public static final String APK_NAME = "JxSmartBox.apk";
	// /////文件类型划分////////////////////////////////
	

	// ////文件,图片地址划分///////////////////////////////
	

	// //////////////////////////////////
	public FileManager() {
		this.SDPath = this.getSDPath();
	}

	public static FileManager instance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}
	private String getFilePath(int type) {
		String _path = SDPath + PROJECT_NAME;
		return _path;
	}
	/**
	 * log文件地址
	 * 
	 * @return
	 */
	private String getLogPath() {
		String _path = SDPath + PROJECT_NAME + CACHE + LOG_SUFFIX;
		return _path;
	}
	
	public String getImageStoragePath() {
		String _path = SDPath + PROJECT_NAME + IMAGES;
		return _path;
	}
	/**
	 * APK文件地址
	 * 
	 * @return
	 */
	public String getAPKPath() {
		String _path = SDPath + PROJECT_NAME + CACHE + APK+APK_NAME;
		return _path;
	}
	
	/**
	 * 语音缓存
	 * @return
	 */
	public String getVoicePath(){
		String _path = SDPath + PROJECT_NAME + CACHE + VOICE;
		return _path;
	}

	/**
	 * 打印异常
	 * 
	 * @param ex
	 *            写入异常
	 * @param isClear
	 *            是否清空以往信息
	 */
	public void writeExceptionLog(Throwable ex,boolean isClear){
		if (ex == null)return;
		final StackTraceElement[] stack = ex.getStackTrace(); 
        final String message = ex.getMessage(); 
        FileOutputStream fos = null;
        String _path = this.getLogPath();
        try{
        if(isClear){
        	fos = new FileOutputStream(FileUtil.reBuildFile(_path));
        }else{
        	FileUtil.checkDirectory(_path);
        	fos = new FileOutputStream(_path,true);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss");       
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间       
        String _time = formatter.format(curDate);       
        fos.write(_time.getBytes());
        fos.write("\r\n".getBytes());
        fos.write(message.getBytes()); 
        fos.write("\r\n".getBytes());
        for (int i = 0; i < stack.length; i++) { 
            fos.write(stack[i].toString().getBytes()); 
            fos.write("\r\n".getBytes());
        } 
        fos.write("------------------------分割线------------------------\r\n".getBytes());
        fos.flush(); 

        }catch (IOException e) {
			// TODO Auto-generated catch block
        	L.e("打印异常", "打印异常失败！");
		} finally {
			FileUtil.closeFileOutputStream(fos);
		}
	}
	/**
	 * 图片缓存路径（Afinal使用）
	 * @param saveType 缓存类型（0临时存储区1永久存储区）
	 * @return
	 */
	public String getCacheImagePath(int saveType){
		String _path = SDPath + PROJECT_NAME;
		switch(saveType){
			case 0:
				_path += CACHE;
				break;
			case 1:
				_path += LASTING;
			}
		return _path+IMAGE_CACHE;
	}
	/**
	 * 封装网络图片地址
	 * @param url
	 * @return
	 */
	public String createImageUrl(String url) {
		if(TextUtils.isEmpty(url))return null;
		StringBuffer _sbf = new StringBuffer();
		_sbf.append(NetConstant.HTTP_ICON_PATH);
    	String headUrl = url.substring(0, url.lastIndexOf("/")+1);
    	String nameUrl = url.substring(url.lastIndexOf("/")+1);
    	_sbf.append(headUrl);
    	_sbf.append(Uri.encode(nameUrl, "utf-8"));
		if(!TextUtils.isEmpty(url) && !url.contains(".")){
			_sbf.append(".png");
		}
		return _sbf.toString();
	}
/**
 * 根据图片网络地址自动生成本地文件地址
 * @param url 图片的服务器地址
 * @param saveType 缓存类型（0临时存储1永久存储）
 * @param isHideSuffix 是否隐藏图片（修改图片后缀）
 * @return
 */
	public String getImagePath(String url,int saveType,boolean isHide) {
		// TODO Auto-generated method stub
		if(url == null || url.length() <= 0)return null;
		if(this.SDPath == null || this.SDPath.length() <= 0)return null;//SD卡不存在
		String _path = SDPath + PROJECT_NAME;
		switch(saveType){
		case 0:
			_path += CACHE;
			break;
		case 1:
			_path += LASTING;
		}
		String[] _strs = splitImgURL(url);
		if(_strs == null)return null;
		_path += _strs[0];
		_path += _strs[1];
		if(isHide){
			_path += IMAGE_SUFFIX;
		}else{
			_path += _strs[2];
		}
		return _path;
	}
	/**
	 * 拆分图片URL（拆分生成图片的本地文件目录结构）
	 * Str[0]图片中间路径;str[1]名字;str[2]后缀
	 * @param url <目前结构：dat_client_welcome_path/2013-10-25/201310251447390718_4368_DSC00197.JPG>
	 */
	private String[] splitImgURL(String url){
		String[] _strs = null;
		if(url == null || url.length() <= 0)return _strs;
		_strs = new String[3];
		_strs[0] = "/"+url.substring(0, url.lastIndexOf("/")+1);
		_strs[1] = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
		_strs[2] = url.substring(url.lastIndexOf("."),url.length());
		return _strs;
	}
	/**
	 * 根据文件网络地址自动生成本地文件地址
	 * @param url 文件的服务器地址
	 * @param saveType 缓存类型（0临时存储1永久存储）
	 * @param isHideSuffix 是否隐藏文件（修改文件后缀）
	 * @return
	 */
		public String getFilePath(String url,int saveType,boolean isHide) {
			// TODO Auto-generated method stub
			if(url == null || url.length() <= 0)return null;
			if(this.SDPath == null || this.SDPath.length() <= 0)return null;//SD卡不存在
			String _path = SDPath + PROJECT_NAME;
			switch(saveType){
			case 0:
				_path += CACHE;
				break;
			case 1:
				_path += LASTING;
			}
			String[] _strs = splitFileURL(url);
			if(_strs == null)return null;
			_path += _strs[0];
			_path += _strs[1];
			if(isHide){
				_path += FILE_SUFFIX;
			}else{
				_path += _strs[2];
			}
			return _path;
		}
		

		/**
		 * 根据用户ID创建用户静脉纹身份信息文档
		 * @param user 用户ID
		 * @return
		 */
		public String getVeinFilePath(String user) {
			// TODO Auto-generated method stub
			if(user == null || user.length() <= 0)return null;
			if(this.SDPath == null || this.SDPath.length() <= 0)return null;//SD卡不存在
			String _path = SDPath + PROJECT_NAME;
			FileUtil.checkDirectory(_path);
			_path += FILES;
			FileUtil.checkDirectory(_path);
			_path += VEIN;
			FileUtil.checkDirectory(_path);
			_path += user + "/";
			FileUtil.checkDirectory(_path);
			return _path;
		}
		/**
		 * 拆分文件URL（拆分生成图片的本地文件目录结构）
		 * Str[0]图片中间路径;str[1]名字;str[2]后缀
		 * @param url <目前结构：dat_client_welcome_path/2013-10-25/201310251447390718_4368_DSC00197.JPG>
		 */
		private String[] splitFileURL(String url){
			String[] _strs = null;
			if(url == null || url.length() <= 0)return _strs;
			_strs = new String[3];
			_strs[0] = "/"+url.substring(0, url.lastIndexOf("/")+1);
			_strs[1] = url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("."));
			_strs[2] = url.substring(url.lastIndexOf("."),url.length());
			return _strs;
		}
//	/**
//	 * 删除所有缓存图
//	 * 
//	 * @return
//	 */
//	public boolean deleteAllImages() {
//		return FileUtil.deleteAllFileFromFolder(SDPath + PROJECT_NAME + CACHE + IMAGES);
//	}

	/**
	 * 判断SDCard是否存在(存在返回SD路径，不存在返回null)
	 * 
	 * @return
	 */
//	public String getSDPath() {
//		File sdDir = null;
//		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//		if (sdCardExist) {
//			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
//			return sdDir.toString();
//		} else {
//			Logger.error("SDCARD", "sdcard不存在");
//			return null;
//		}
//
//	}
	
	/**
	 * 判断SDCard是否存在(存在返回SD路径，不存在返回安装目录)
	 * 
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		} else {
			String _dir = BaseApp.getInstance().getCacheDir().getAbsolutePath();
			L.e("SDCARD", "sdcard不存在,改安装目录："+_dir);
			return _dir;
		}

	}

	/**
	 * 获取文件图片
	 * 
	 * @param imagePath
	 * @return
	 */
	public Bitmap getImage(String imagePath) {
		if(imagePath == null || imagePath.length() <= 0)return null;
		return FileUtil.getImage(imagePath);
	}

//	public String writeImage(String imageName, InputStream is, int type, boolean isHD) {
//		if (imageName == null || imageName.equals(""))
//			return null;
//		String _path = null;
//		if (this.getImagePath(type) == null)
//			return null;
//		if (isHD) {
//			_path = this.getImagePath(type) + imageName + "_L" + IMAGE_SUFFIX;
//		} else
//			_path = this.getImagePath(type) + imageName + IMAGE_SUFFIX;
//		if (FileUtil.FileOutputStream(_path, is)) {
//			return _path;
//		} else {
//			return null;
//		}
//	}

//	public boolean deletAllImages(int type) {
		// TODO Auto-generated method stub
//		return false;//FileUtil.deleteAllFileFromFolder(this.getImagePath(type));
//	}

//	@Deprecated
//	public String addFile(String fileName, InputStream is, int type) {
		// TODO Auto-generated method stub
//		return null;
//	}

//	@Deprecated
//	public InternetTask savePlusFile(Object vo, int type) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	public String writeFile(String fileName, InputStream is, int type) {
//		// TODO Auto-generated method stub
//		if (fileName == null || fileName.equals(""))
//			return null;
//		String _path = null;
//		if (this.getFilePath(type) == null)
//			return null;
//		_path = this.getFilePath(type) + fileName + FILE_SUFFIX;
//		if (FileUtil.FileOutputStream(_path, is)) {
//			return _path;
//		} else {
//			return null;
//		}
//	}
//	public String writeAPK(String path, InputStream is) {
//		// TODO Auto-generated method stub
//		if (path == null || path.equals(""))
//			return null;
//		if (FileUtil.FileOutputStream(path, is)) {
//			return path;
//		} else {
//			return null;
//		}
//	}

//	public InternetTask saveOverlayFiles(Context context, List<Object> list, int type) {
//		// TODO Auto-generated method stub
//		if (list == null || list.size() <= 0)
//			return null;
//		InternetTask _task = null;
//		
//		return _task;
//	}

//	public InternetTask LoadFiles(Context context, int type) {
		// TODO Auto-generated method stub
		// if (fileName == null || fileName.equals(""))
		// return null;
		// String _path = null;
		// if(this.getFilePath(type) == null)return null;
		// _path = this.getFilePath(type) + fileName + FILE_SUFFIX;
		// load不同的文件task
//		InternetTask _task = null;

//		return _task;
//	}

//	@Deprecated
//	public InternetTask LoadFile(String fileName, int type) {
//		// TODO Auto-generated method stub
//		if (fileName == null || fileName.equals(""))
//			return null;
//		String _path = null;
//		if (this.getFilePath(type) == null)
//			return null;
//		_path = this.getFilePath(type) + fileName + FILE_SUFFIX;
//		
//		return null;
//	}

//	public int getFileCount(int type) {
//		// TODO Auto-generated method stub
//		return (getFileList(type)).size();
//	}

//	public List<File> getFileList(int type) {
//		// TODO Auto-generated method stub
//		ArrayList<File> fileList = new ArrayList<File>();
//		FileUtil.getFiles(fileList, this.getFilePath(type));
//		return fileList;
//	}

//	public boolean deleteAllFiles(int type) {
//		// TODO Auto-generated method stub
//		return FileUtil.deleteAllFileFromFolder(this.getFilePath(type));
//	}
//	public boolean deleteFile(String name,int type) {
//		// TODO Auto-generated method stub
//		String _path = this.getFilePath(type)+name+FILE_SUFFIX;
//		try {
//			FileUtil.delFile(_path);
//			return true;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//	}
//	public boolean deleteImage(String name,int type,boolean isHD) {
		// TODO Auto-generated method stub
//		String _path = null;
//		if(isHD){
//			_path = this.getImagePath(type)+name+"L"+IMAGE_SUFFIX;
//		}else{
//			_path = this.getImagePath(type)+name+IMAGE_SUFFIX;
//		}
//		try {
//			FileUtil.delFile(_path);
//			return true;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//	}
//	public InternetTask downLoadImage(Context context, Object vo, int type, boolean isHD) {
		// TODO Auto-generated method stub
//		InternetTask _task = null;
		
//		return _task;
//	}

//	public InternetTask copeFileAndImage(Context context, Object vo, int type) {
		// TODO Auto-generated method stub
//		if (vo == null)
//			return null;
//		int favouriteType = -1;//收藏的类型
//		InternetTask _task = null;
		// 调用收藏的task
//		return _task;
//	}

//	public InternetTask deleteFileAndImage(Context context, Object vo, int type) {
		// TODO Auto-generated method stub
//		if (vo == null)
//			return null;
//		int favouriteType = -1;//收藏的类型
//		InternetTask _task = null;
//		return _task;
//	}

//	public InternetTask clearCache(Context context) {
		// TODO Auto-generated method stub
//		InternetTask _task = new ClearCacheTask(context, null);
//		return _task;
//		return null;
//	}
}

