package com.jinxin.jxtouchscreen.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件操作工具类
 * 
 * @author zj
 */
public class FileUtil {
	/**
	 * 写入指定路径文件
	 * 
	 * @param path
	 *            文件路径
	 * @param is
	 * @return
	 */
	public static boolean FileOutputStream(String path, InputStream is) {
		boolean success = false;
		if (is == null)
			return success;
		if (path == null || path.trim().equals(""))
			return success;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(reBuildFile(path));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			success = true;
		} catch (IOException ex) {
			success = false;
		} finally {
			closeFileOutputStream(fos);
			closeInputStream(is);
		}
		return success;
	}

	/**
	 * 数据流转字节数组
	 * 
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream is) {
		if (is == null)
			return null;
		byte[] buffer = new byte[1024];
		int len = -1;
		byte[] data = null;
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		try {
			while ((len = is.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			data = outSteam.toByteArray();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			L.e("FileUtil", ex.toString());
			data = null;
		} finally {
			closeByteArrayOutputStream(outSteam);
			closeInputStream(is);
		}
		return data;
	}

	/**
	 * 删除指定目录下所有文件(未删除文件夹)
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteAllFileFromFolder(String path) {
		boolean success = false;
		if (path == null || path.trim().equals(""))
			return success;
		ArrayList<File> _lists = new ArrayList<File>();
		getFiles(_lists, path);
		for (int i = 0; i < _lists.size(); i++) {
			File _file = _lists.get(i);
			if (_file.exists()) {
				_file.delete();
			}
		}
		success = true;
		return success;
	}

	/**
	 * 删除目录下最新创建文件
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteLastFromFolder(String path) {
		boolean success = false;
		if (path == null || path.trim().equals(""))
			return success;
		try {
			ArrayList<File> _lists = new ArrayList<File>();
			getFiles(_lists, path);
			File latestSavedFile = _lists.get(0);
			if (latestSavedFile.exists()) {
				for (int i = 1; i < _lists.size(); i++) {
					File nextFile = _lists.get(i);
					if (nextFile.lastModified() > latestSavedFile
							.lastModified()) {
						latestSavedFile = nextFile;
					}
				}
				success = latestSavedFile.delete();
			}
		} catch (Exception ex) {
			L.e("FileUtil", ex.toString());
		}
		return success;
	}

	/**
	 * 递归获取指定目录下得所有文件（全部存放在list一级列表）
	 * 
	 * @param fileList
	 * @param path
	 */
	public static void getFiles(ArrayList<File> fileList, String path) {
		if (path != null && !path.trim().equals("")) {
			File _file = null;
			_file = new File(path);
			if (_file != null && _file.exists()) {
				File[] allFiles = _file.listFiles();
				if (allFiles != null) {
					for (int i = 0; i < allFiles.length; i++) {
						File file = allFiles[i];
						if (file.isFile()) {
							fileList.add(file);
						} else if (!file.getAbsolutePath()
								.contains(".thumnail")) {
							getFiles(fileList, file.getAbsolutePath());
						}
					}
				}
			}
		}
	}

	/**
	 * 检测目录是否存在（若不存在则创建该目录）
	 * 
	 * @param path
	 */
	public static void checkDirectory(String path) {
		if (path == null || path.trim().equals(""))
			return;
		String directory = path.substring(0, path.lastIndexOf("/"));
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 文件重建
	 * 
	 * @param path
	 * @return
	 */
	public static File reBuildFile(String path) {
		if (path == null || path.trim().equals(""))
			return null;
		checkDirectory(path);
		File file = new File(path);
		try {
			if (file.exists())
				file.delete();
			file.createNewFile();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			L.e("----->", "文件重建错误" + ex.getMessage());
			L.e("FileUtil", ex.toString());
			return null;
		}
		return file;
	}

	public static void closeFileOutputStream(FileOutputStream fos) {
		if (fos != null) {
			try {
				fos.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				L.e("FileUtil", ex.toString());
			}
		}
	}

	public static void closeFileInputStream(FileInputStream fis) {
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				L.e("FileUtil", ex.toString());
			}
		}
	}

	public static void closeInputStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				L.e("FileUtil", ex.toString());
			}
		}
	}

	private static void closeOutputStream(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				L.e("FileUtil", ex.toString());
			}
		}
	}

	private static void closeByteArrayOutputStream(ByteArrayOutputStream baos) {
		if (baos != null) {
			try {
				baos.close();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				L.e("FileUtil", ex.toString());
			}
		}
	}

	/**
	 * 获取文件图片
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getImage(String imagePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false; // 设为 false
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		return bitmap;
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int be = 1;
		if (width == 0 || height == 0) {// 宽高传入为0，不进行缩放计算
			be = 1;
		} else {
			int h = options.outHeight;
			int w = options.outWidth;
			int beWidth = w / width;
			int beHeight = h / height;

			if (beWidth < beHeight) {
				be = beWidth;
			} else {
				be = beHeight;
			}
			if (be <= 0) {
				be = 1;
			}
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		if (width == 0 || height == 0) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, options.outWidth,
					options.outHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} else {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 *            原文件
	 * @param targetFile
	 *            目标文件
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir
	 *            原目录
	 * @param targetDir
	 *            目标目录
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 
	 * @param srcFileName
	 *            原文件
	 * @param destFileName
	 *            目标文件
	 * @param srcCoding
	 *            原文件格式
	 * @param destCoding
	 *            目标文件格式
	 * @throws IOException
	 */
	public static void copyFile(File srcFileName, File destFileName,
			String srcCoding, String destCoding) throws IOException {// 把文件转换为GBK文件
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					srcFileName), srcCoding));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(destFileName), destCoding));
			char[] cbuf = new char[1024 * 5];
			int len = cbuf.length;
			int off = 0;
			int ret = 0;
			while ((ret = br.read(cbuf, off, len)) > 0) {
				off += ret;
				len -= ret;
			}
			bw.write(cbuf, 0, off);
			bw.flush();
		} finally {
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
		}
	}

	/**
	 * 删除文件夹目录下的所有文件
	 * 
	 * @param filepath
	 *            文件夹目录
	 * @throws IOException
	 */
	public static void delFiles(String filepath) throws IOException {
		File f = new File(filepath);// 定义文件路径
		if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
			if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
				f.delete();
			} else {// 若有则把文件放进数组，并判断是否有下级目录
				File delFile[] = f.listFiles();
				int i = f.listFiles().length;
				for (int j = 0; j < i; j++) {
					if (delFile[j].isDirectory()) {
						delFiles(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
					}
					delFile[j].delete();// 删除文件
				}
			}
		}
	}
	/**
	 * 删除指定文件
	 * @param filepath
	 * @throws IOException
	 */
	public static void delFile(String filepath)throws IOException{
		File f = new File(filepath);// 定义文件路径
		if(f.exists() && !f.isDirectory()){// 判断是文件还是目录
			f.delete();
		}
	}

	/**
	 * 读取文件中内容为字符串
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readFileToString(String path) throws IOException {
		String resultStr = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			byte[] inBuf = new byte[2000];
			int len = inBuf.length;
			int off = 0;
			int ret = 0;
			while ((ret = fis.read(inBuf, off, len)) > 0) {
				off += ret;
				len -= ret;
			}
			resultStr = new String(
					new String(inBuf, 0, off, "utf-8").getBytes());
		} finally {
			if (fis != null)
				fis.close();
		}
		return resultStr;
	}

	/**
	 * 文件转成字节数组
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileToBytes(String path) throws IOException {
		byte[] b = null;
		InputStream is = null;
		File f = new File(path);
		try {
			is = new FileInputStream(f);
			b = new byte[(int) f.length()];
			is.read(b);
		} finally {
			if (is != null)
				is.close();
		}
		return b;
	}

	/**
	 * 将byte写入文件中（文件重置）
	 * 
	 * @param fileByte
	 * @param filePath
	 * @throws IOException
	 */
	public static void byteToFile(byte[] fileByte, String filePath)
			throws IOException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(reBuildFile(filePath));
			os.write(fileByte);
			os.flush();
		} finally {
			if (os != null)
				os.close();
		}
	}

	// /**
	// * 将目录文件打包成zip
	// *
	// * @param srcPathName
	// * @param zipFilePath
	// * @return 成功打包true 失败false
	// */
	// public static boolean compress(String srcPathName, String zipFilePath) {
	// if (strIsNull(srcPathName) || strIsNull(zipFilePath))
	// return false;
	//
	// File zipFile = new File(zipFilePath);
	// File srcdir = new File(srcPathName);
	// if (!srcdir.exists())
	// return false;
	// Project prj = new Project();
	// Zip zip = new Zip();
	// zip.setProject(prj);
	// zip.setDestFile(zipFile);
	// FileSet fileSet = new FileSet();
	// fileSet.setProject(prj);
	// fileSet.setDir(srcdir);
	// zip.addFileset(fileSet);
	// zip.execute();
	// return zipFile.exists();
	// }
	/**
	 * 将一个字符串转化为输入流
	 */
	public static InputStream getStringStream(String sInputString) {
		if (sInputString != null && !sInputString.trim().equals("")) {
			try {
				ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(
						sInputString.getBytes());
				return tInputStringStream;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 将一个输入流转化为字符串
	 */
	public static String getStreamString(InputStream tInputStream) {
		if (tInputStream != null) {
			try {
				BufferedReader tBufferedReader = new BufferedReader(
						new InputStreamReader(tInputStream));
				StringBuffer tStringBuffer = new StringBuffer();
				String sTempOneLine = "";
				while ((sTempOneLine = tBufferedReader.readLine()) != null) {
					tStringBuffer.append(sTempOneLine);
				}
				return tStringBuffer.toString();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 文件格式转换
	 * @param oldFile
	 * @param oldCharset
	 * @param newFlie
	 * @param newCharset
	 */
	public static void convertToUTF(String oldFile, String oldCharset,
            String newFlie, String newCharset) {
        BufferedReader bin;
        FileOutputStream fos;
        StringBuffer content = new StringBuffer();
        try {
            System.out.println("the old file is :"+oldFile);
            System.out.println("The oldCharset is : "+oldCharset);
            bin = new BufferedReader(new InputStreamReader(new FileInputStream(
                    oldFile), oldCharset));
            String line = null;
            while ((line = bin.readLine()) != null) {
                content.append(line);
                content.append(System.getProperty("line.separator"));
            }
            bin.close();
            File dir = new File(newFlie.substring(0, newFlie.lastIndexOf("/")));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(newFlie);
            Writer out = new OutputStreamWriter(fos, newCharset);
            out.write(content.toString());
            out.close();
            fos.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 遍历目录下的所有文件
     * @param strPath
     * @param filelist
     * @param regex
     */
    public static void fetchFileList(String strPath, List<String> filelist,
            final String regex) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();
        Pattern p = Pattern.compile(regex);
        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                fetchFileList(files[i].getAbsolutePath(), filelist, regex);
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                Matcher m = p.matcher(strFileName);
                if (m.find()) {
                    filelist.add(strFileName);
                }
            }
        }
    }

}
