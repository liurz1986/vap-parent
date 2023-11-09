package com.vrv.vap.monitor.server.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ShellExecuteScript {

	private static Logger logger = LoggerFactory.getLogger(ShellExecuteScript.class);
	
	/**
	 * java执行shell执行命令行,返回对应的数据
	 * 
	 * @param cmd
	 * @return
	 */
	public static List<String> queryExecuteCmd(String cmd) {
		List<String> stringList = new ArrayList<>();
		try {
			Process pro = Runtime.getRuntime().exec(CleanUtil.cleanString(cmd));
			InputStream inputStream = pro.getInputStream();
			InputStream errorStream = pro.getErrorStream();
			List<String> message = getMessage(inputStream);
			List<String> errorMsg = getMessage(errorStream);
			int value = pro.waitFor(1,TimeUnit.MINUTES) ? 0 : 1;
 
			if(value==0) {
				logger.info("message:"+StringUtils.join(message,"\r\n"));
				stringList.addAll(message);
			}else {
 				logger.error("error\r\n:"+StringUtils.join(errorMsg,"\r\n"));
 				stringList.addAll(errorMsg);
			}
			errorStream.close();
			inputStream.close();
		} catch (Exception e) {
              logger.error("执行shell脚本异常", e);
              //System.out.println("执行shell脚本异常");
              throw new RuntimeException(e.getMessage());
		}
		return stringList;
	}
	
	
	public static List<String> querySuccessExecuteCmd(String cmd) {
		List<String> stringList = new ArrayList<>();
		try {

			Process process = Runtime.getRuntime().exec(CleanUtil.cleanString(cmd));
			InputStream inputStream = process.getInputStream();
			InputStream errorStream = process.getErrorStream();

			List<String> message = getMessage(inputStream);
			List<String> errorMsg = getMessage(errorStream);
			int value = process.waitFor(1,TimeUnit.MINUTES) ? 0 : 1;
			if(value==0) {
				if(message!=null) {
					stringList.addAll(message);
				}
			}else {
				 logger.error("执行shell脚本异常", errorMsg);
			}
			if(process!=null) {
				process.destroy();
			}
	
		} catch (Exception e) {
              logger.error("执行shell脚本异常", e);
              //System.out.println("执行shell脚本异常");
              throw new RuntimeException(e.getMessage());
		}
		logger.info("命令执行完成");
		return stringList;
	}
 
	private static List<String> getMessage(final InputStream input) {
 
		if(input==null) {
			return null;
		}
		try {
			FutureTask<List<String>> futureTask = new FutureTask<List<String>>((Callable<List<String>>) () -> {

				// 这里相当于call方法执行体。
				Reader reader = new InputStreamReader(input);
				BufferedReader bf = new BufferedReader(reader);
				List<String> result=new ArrayList<>();
				String line = null;
				try {
					while ((line = bf.readLine()) != null) {
						result.add(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					//bf.close();
					//reader.close();
				}
				return result;
			});

			new Thread(futureTask).start();

			List<String> list = futureTask.get();
			return list;
		} catch (Exception e) {
			return null;
		}

	}
 
	
	/**
	 * 执行命令行
	 * @param cmd
	 * @return
	 */
	public static synchronized boolean executeCmd(String cmd) {
		Process pro;
		StringBuffer buf = new StringBuffer(1000);
		try {
			Thread.sleep(100);
			pro = Runtime.getRuntime().exec(CleanUtil.cleanString(cmd));
			pro.waitFor(1,TimeUnit.MINUTES);
			InputStream in = pro.getInputStream();
			InputStream errorStream = pro.getErrorStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = read.readLine()) != null) {
				buf.append(line);
				buf.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		logger.info(LogForgingUtil.validLog(buf.toString()));
		return true;
	}
 

	/**
	 * 执行shell脚本(无参数执行)
	 * @param shell_file_dir
	 * @param running_shell_file
	 * @return
	 */
	public static boolean executeShellScript(String shell_file_dir,String running_shell_file){
		
		ProcessBuilder pb = new ProcessBuilder(CleanUtil.cleanString("./" + running_shell_file));
		pb.directory(new File(shell_file_dir));
		int runningStatus = 0;
		String s = null;
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((s = stdInput.readLine()) != null) {
				logger.info(s);
				//System.out.println(s);
			}
			while ((s = stdError.readLine()) != null) {
				logger.info(s);
				//System.out.println(s);
			}
			try {
				runningStatus = p.waitFor(1,TimeUnit.MINUTES) ? 0 : 1;
				if(runningStatus==0){
					logger.info("脚本调用正常");
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
 * @param shell_file_dir
 * @param running_shell_file
 * @param params
 * @return
 */
public static boolean executeShellScript(String shell_file_dir,String running_shell_file,String params){
		
		ProcessBuilder pb = new ProcessBuilder(CleanUtil.cleanString("./" + running_shell_file),CleanUtil.cleanString(params));
		pb.directory(new File(shell_file_dir));
		int runningStatus = 0;
		String s = null;
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((s = stdInput.readLine()) != null) {
				logger.info(s);
				//System.out.println(s);
			}
			while ((s = stdError.readLine()) != null) {
				logger.info(s);
				//System.out.println(s);
			}
			try {
				runningStatus = p.waitFor(1,TimeUnit.MINUTES) ? 0 : 1;
				if(runningStatus==0){
					logger.info("脚本调用正常");
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


	/*public static boolean executeSshCmd(String ip, int port, String username, String pwd, String cmd) {
		Connection conn = new Connection(ip, port);
		Session session = null;
		try {
			conn.connect();
			boolean isConn = conn.authenticateWithPassword(username, pwd);
			if (isConn) {
				session = conn.openSession();
				session.execCommand(CleanUtil.cleanString(cmd));
				InputStream is = new StreamGobbler(session.getStdout());
				BufferedReader bfs = new BufferedReader(new InputStreamReader(is));
				while (true) {
					String line = bfs.readLine();
					if (line == null) {
						break;
					}
					System.out.println(line);
				}
				is.close();
				bfs.close();
				session.close();
				conn.close();
			} else {
				return false;
			}
		} catch (Exception e) {
			//log.error("SSH命令操作失败！", e);
			return false;
		}
		return true;
	}*/

	
	/**
	 * 主要用于带多个参数，且参数当中存在空格得执行
	 * @param command
	 * @return
	 */
	public static boolean executeShellByResultArray(String[] command) {
		try {
			Process pro = Runtime.getRuntime().exec(CleanUtil.cleanStrArray(command));
			int exitValue = pro.waitFor(1,TimeUnit.MINUTES) ? 0 : 1;
			System.out.println("shell执行结果："+exitValue);
			if(exitValue==0){ //等于0表示脚本能够正确执行
				InputStream in = pro.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while ((line = read.readLine()) != null) {
                   // logger.info("log info:"+line);
					  System.out.println("log info:"+line);
				}
				in.close();
				read.close();
			}else{
				InputStream errorStream = pro.getErrorStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(errorStream));
				String line = null;
				while ((line = read.readLine()) != null) {
					System.out.println("error message:"+line);
				}
				errorStream.close();
				read.close();
				
				InputStream input = pro.getInputStream();
				BufferedReader inputread = new BufferedReader(new InputStreamReader(input));
				String inputline = null;
				while ((inputline = inputread.readLine()) != null) {
					System.out.println("message:"+inputline);
				}
				input.close();
				inputread.close();
			}
		} catch (Exception e) {
			//logger.error("执行shell脚本异常", e);
			e.printStackTrace();
		    System.out.println("执行shell脚本异常");
			return false;
		}
		return true;
	}

}
