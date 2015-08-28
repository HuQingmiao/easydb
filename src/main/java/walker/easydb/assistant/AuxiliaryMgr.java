package walker.easydb.assistant;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * EasyDB的辅助管理器, 目前只是提供清空文件存放目录的方法
 * 
 * @author HuQingmiao
 * 
 */
public class AuxiliaryMgr {

	private static Logger log = LogFactory.getLogger(AuxiliaryMgr.class);

	private AuxiliaryMgr() {
	}

	/**
	 * 清空基本文件目录
	 * 
	 * @throws IOException
	 * 
	 */
	public static void clearBaseFileDirc() {

		// 取临时文件
		String baseFileDirc = EasyConfig.getProperty("baseFileDirc");
		File dirc = new File(baseFileDirc);

		// 逐个删除
		File[] files = dirc.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		log.info("Deleted All files in the base file dircotry. ");
	}

	/**
	 * 当文件占用的总空间达到参数指定的字节数时, 才清空基本文件目录.
	 * 
	 * @param maxSpaceSize
	 *            允许基本文件目录占用的最大字节数
	 * 
	 * @throws IOException
	 */
	public static void clearBaseFileDirc(int maxSpaceSize) {

		// 取临时文件
		String baseFileDirc = EasyConfig.getProperty("baseFileDirc");
		File dirc = new File(baseFileDirc);

		// 计算该目录下各文件占用的空间大小
		File[] files = dirc.listFiles();
		int size = 0;
		for (int i = 0; i < files.length; i++) {
			size += files[i].length();
		}

		if (size >= maxSpaceSize) {
			// 逐个删除
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
		log.info("Deleted All files in the base file dircotry. ");
	}

	public static void main(String[] args) {
		clearBaseFileDirc(100000);
	}

}