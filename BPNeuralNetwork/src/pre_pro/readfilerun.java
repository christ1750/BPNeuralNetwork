package pre_pro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;

/**
 *author: zhangbo
 *data: 2015年10月18日上午10:52:35
 *function:
 */

public class readfilerun implements Runnable{
	private File file;
	private BlockingQueue<String>queue;
	private ProcessRecord process;
	public readfilerun(File file,BlockingQueue<String>queue) {
		// TODO Auto-generated constructor stub
		this.file = file;
		this.queue = queue;
		process = new ProcessRecord(queue);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		readfile();
		
		
	}
	public void readfile(){
		FileInputStream fis;
		BufferedReader reader;
		try {
			fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis,"utf-8"));
			String line = reader.readLine();
			while(line != null){
				process.pro_record(line);
				line = reader.readLine();
			}
			reader.close();
			queue.put("end");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

