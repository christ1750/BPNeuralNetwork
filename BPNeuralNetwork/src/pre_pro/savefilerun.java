package pre_pro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;

/**
 *author: zhangbo
 *data: 2015年10月18日上午11:01:31
 *function:
 */

public class savefilerun implements Runnable{

	private File file;
	private BlockingQueue<String>queue;
	
	public savefilerun(File file,BlockingQueue<String>queue){
		this.file = file;
		this.queue = queue;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		savefile();
	}

	public void savefile(){
		FileOutputStream fos;
		BufferedWriter writer;
		try {
			fos = new FileOutputStream(file);
			writer = new BufferedWriter(new OutputStreamWriter(fos,"utf-8"));
			String record = queue.take();
			while(record != "end"){
				writer.write(record.toString());
				writer.newLine();
				record = queue.take();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

