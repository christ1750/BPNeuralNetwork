package pre_pro;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *author: zhangbo
 *data: 2015年10月18日上午11:11:40
 *function:
 */

public class Main {
	public static void main(String[]args){
		BlockingQueue<String>queue = new LinkedBlockingQueue<String>();
		File fileinput = new File("./ex4data1Y.txt");
		File fileoutput = new File("./inputY.txt");
		Runnable readThread = new readfilerun(fileinput,queue);
		Runnable saveThread = new savefilerun(fileoutput, queue);
		
		Thread read_thread = new Thread(readThread);
		Thread save_thread = new Thread(saveThread);
		
		read_thread.start();
		save_thread.start();
		
		try {
			read_thread.join();
			save_thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("done!");
	}
}

