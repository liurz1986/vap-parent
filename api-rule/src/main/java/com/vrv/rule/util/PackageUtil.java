package com.vrv.rule.util;
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月10日 下午5:07:39 
* 类说明    package子类遍历
*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * package扫描工具类
 * @author wd-pc
 *
 */

public class PackageUtil {

	private static Logger logger = LoggerFactory.getLogger(PackageUtil.class);

	/**
	 * 获取某包下（包括该包的所有子包）所有类(获得所有的实例)
	 * @param packageName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public static List<Class<?>> getClassName(String packageName) throws IOException, ClassNotFoundException {
		return getClassName(packageName, true);
	}
	
	/**
	 * 获取某包下所有类
	 * @param packageName 包名
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
    public static List<Class<?>> getClassName(String packageName, boolean childPackage) throws IOException, ClassNotFoundException {
        List<Class<?>> fileNames = new ArrayList<>();
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> urls = loader.getResources(packagePath);
        while (urls.hasMoreElements()) {
            url = urls.nextElement();
            if (url == null)
                continue;
            String type = url.getProtocol();
            if (type.equals("file")) {
            	fileNames.addAll(getClassNameByFile(url.getPath(), childPackage));
            } 
        }
        return fileNames;
    }
    
    
    private static List<Class<?>> getClassNameByFile(String filePath, boolean childPackage) throws UnsupportedEncodingException, ClassNotFoundException {
        List<Class<?>> myClassName = new ArrayList<>();
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        if (childFiles == null)
            return null;
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                if (!childFilePath.endsWith("$1.class")) {
                    childFilePath = childFilePath.substring(childFilePath.lastIndexOf("\\classes\\")+9, childFilePath.lastIndexOf("."));
                    childFilePath = childFilePath.replace("\\", ".");
                	Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(childFilePath);
                    myClassName.add(c);
                }
            }
        }
        return myClassName;
    }

    
    
    
    public static List<String> getClassNameByJar(String jarPath, boolean childPackage) throws UnsupportedEncodingException {
        List<String> myClassName = new ArrayList<String>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("\\"));
        //jarFilePath = UrlDecode.getURLDecode(jarFilePath);
        String packagePath = jarInfo[1].substring(1);
        getClassNameWithJar(childPackage, myClassName, jarFilePath, packagePath);
        return myClassName;
    }

	public static void getClassNameWithJar(boolean childPackage, List<String> myClassName, String jarFilePath,
			String packagePath) {
		try {
            @SuppressWarnings("resource")
			JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    } else {
                        int index = entryName.lastIndexOf("/");
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
                            myClassName.add(entryName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            //SystemLog.Log(LogType.systemInfo, e.getMessage(), e);
        }
	}
    
    /**
     *  从所有jar中搜索该包，并获取该包下所有类
     * @param urls    URL集合
     * @param packagePath 包路径
     * @param childPackage
     * @return
     * @throws UnsupportedEncodingException
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) throws UnsupportedEncodingException {
        
    	List<String> myClassName = new ArrayList<String>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

    public void test() {
    	URL resource = this.getClass().getClassLoader().getResource("");
    	String path = resource.getPath();
    	System.out.println("path:"+path);
    	this.getClass();
    }
    
    
    public static void main(String[] args) throws Exception, ClassNotFoundException {
         PackageUtil packageUtil = new PackageUtil();
         packageUtil.test();
//    	List<Class<?>> classNames = getClassName("com.vrv.rule.ruleInfo");
//    	for (Class<?> class1 : classNames) {
//			System.out.println(class1);
//		}
    	
//    	String str = "D:\\code\\vap\\api-rule\\target\\classes\\com\\vrv\\rule\\ruleInfo\\honeypot\\HoneyPotIntrusionMainFun.class";
//    	str = str.substring(str.lastIndexOf("\\classes\\")+9, str.lastIndexOf("."));
//    	str = str.replace("\\", ".");
//    	System.out.println(str);
    	
    	
    	
	}
    
    
    
}
