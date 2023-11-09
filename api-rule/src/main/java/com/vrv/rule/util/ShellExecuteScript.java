package com.vrv.rule.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import org.apache.log4j.Logger;

/**
 * java调用shell脚本工具类;
 * 
 * @author wd-pc
 *
 */

public class ShellExecuteScript {

	//private static Logger logger = Logger.getLogger(ShellExecuteScript.class);
	private static ExecutorService cacheThreadPool = Executors.newCachedThreadPool();
	/**
	 * java执行shell执行命令行,返回对应的数据
	 * 
	 * @param cmd
	 * @return
	 */
	public static List<String> executeCmd(String cmd) {
		List<String> stringList = new ArrayList<>();
		try {
			Process pro = Runtime.getRuntime().exec(cmd);
			int exitValue = pro.waitFor();
			if (0 == exitValue) {//等于0表示脚本能够正确执行
				InputStream in = pro.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = read.readLine()) != null) {
					System.out.println("message:"+line);
					//logger.info("message:"+line);
					stringList.add(line);
				}
				in.close();  
				read.close();
			}
		} catch (Exception e) {
             // logger.error("执行shell脚本异常", e);
              System.out.println("执行shell脚本异常");
              throw new RuntimeException(e.getMessage());
		}
		return stringList;
	}
	
	
	/**
	 * java调用shell执行是否返回正确结果
	 * @param cmd
	 * @return
	 */
	public static boolean executeShellByResult(String cmd) {
		try {
			Process pro = Runtime.getRuntime().exec(cmd);
			int exitValue = pro.waitFor(); 
			if(exitValue==0){ //等于0表示脚本能够正确执行
				InputStream in = pro.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = read.readLine()) != null) {
                   // logger.info("log info:"+line);
					  System.out.println("log info:"+line);
				}
			}
		} catch (Exception e) {
			//logger.error("执行shell脚本异常", e);
			System.out.println("执行shell脚本异常");
			return false;
		}
		return true;
	}
	
	

	/**
	 * 给对应的文件赋予权限
	 * 
	 * @param shell_file_dir
	 * @param running_shell_file
	 * @return
	 */
	public static int settingPrivilege(String shell_file_dir, String running_shell_file) {
		int rc = 0;
		File tempFile = new File(Path.combine(shell_file_dir, running_shell_file));
		ProcessBuilder builder = new ProcessBuilder("/bin/chmod", "755", tempFile.getPath());
		Process process;
		try {
			process = builder.start();
			rc = process.waitFor();
			if (rc == 0) {
				// logger.info("文件赋予对应的权限");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return rc;
	}

	/**
	 * 执行shell脚本(无参数执行)
	 * 
	 * @param shell_file_dir
	 * @param running_shell_file
	 * @return
	 */
	public static boolean executeShellScript(String shell_file_dir, String running_shell_file) {

		ProcessBuilder pb = new ProcessBuilder("./" + running_shell_file);
		pb.directory(new File(shell_file_dir));
		int runningStatus = 0;
		String s = null;
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((s = stdInput.readLine()) != null) {
				// logger.info(s);
				// System.out.println(s);
			}
			while ((s = stdError.readLine()) != null) {
				// logger.info(s);
				// System.out.println(s);
			}
			try {
				runningStatus = p.waitFor();
				if (runningStatus == 0) {
					// logger.info("脚本调用正常");
				}
			} catch (InterruptedException e) {
			}

		} catch (IOException e) {
		}
		if (runningStatus != 0) {
			return false;
		}
		return true;
	}

	/**
	 * 执行shell脚本(有参数执行)
	 * 
	 * @param shell_file_dir
	 * @param running_shell_file
	 * @param params
	 * @return
	 */
	public static boolean executeShellScript(String shell_file_dir, String running_shell_file, String[] params) {
		ProcessBuilder pb = new ProcessBuilder("./" + running_shell_file, params[0], params[1], params[2], params[3],
				params[4], params[5], params[6]);
		pb.directory(new File(shell_file_dir));
		int runningStatus = 0;
		String s = null;
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((s = stdInput.readLine()) != null) {
				// logger.info(s);
				System.out.println(s);
			}
			while ((s = stdError.readLine()) != null) {
				// logger.info(s);
				System.out.println(s);
			}
			try {
				runningStatus = p.waitFor();
				if (runningStatus == 0) {
					return true;
				}
			} catch (InterruptedException e) {
			}

		} catch (IOException e) {
		}
		return false;
	}

	public static void main(String[] args) {
		String cmd = "ifconfig | grep HWaddr | awk -F 'HWaddr ' '{print $2}'";
		List<String> list = ShellExecuteScript.executeCmd(cmd);
		for (String string : list) {
			System.out.println(string);
		}
        //TODO 测试启动分析引擎
//		String main_class = "com.vrv.rule.analysisResult.AnalysisDemo.WikipediaAnalysis";
//		String flink_jar_path = "/usr/local/flink/flink-1.6.1/api-rule-1.0.jar";
//		String cmd_path = "/usr/local/flink/flink-1.6.1/bin/flink"+" run -c "+main_class+" "+flink_jar_path;
//		cacheThreadPool.execute(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				boolean result = ShellExecuteScript.executeShellByResult(cmd_path);
//				System.out.println("result:"+result);	
//			}
//		});
//		System.out.println("complete!");
//		String cmd_path = "/usr/local/flink/flink-1.6.1//bin/flink"+" "+"list";
//		List<String> list = ShellExecuteScript.executeCmd(cmd_path);
//		String jobId = getJobId("Flink Streaming Job", list);
//		System.out.println("jobId:"+jobId);
//		String cancel_job_command = "/usr/local/flink/flink-1.6.1/bin/flink"+" cancel "+jobId;
//		ShellExecuteScript.executeShellByResult(cancel_job_command);
//		System.out.println("complete!!!");
	}
	
	private static String getJobId(String job_name, List<String> list) {
		List<Integer> count = new ArrayList<>();
		String job_Id = null;
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			if(str.startsWith("---")){
				count.add(i); //在---虚线之间的为对应的job的位置
			}
		}
		if(count.size()==2){
			list = list.subList(count.get(0), count.get(1));//在---虚线之间的为对应的job的内容
			for (String  job_content : list) {
				if(job_content.contains(job_name)){ //job_content当中是否包含对应的job_name
					String[] split = job_content.split(" : ");
					if(split.length==3){
						job_Id = split[1];
					}
					break;
				}
			}
		}
		return job_Id;
	}
	

}
