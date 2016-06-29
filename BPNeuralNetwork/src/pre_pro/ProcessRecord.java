package pre_pro;

import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *author: zhangbo
 *data: 2015年10月18日上午10:24:30
 *function:
 */

public class ProcessRecord {
	/**阻塞队列*/
	private BlockingQueue<String>queue;
	
	public ProcessRecord(BlockingQueue<String>queue){
		this.queue = queue;
	}
	
	public void pro_record(String line){
		Pattern pattern = Pattern.compile("\\s+");
		Matcher matcher_first = pattern.matcher(line);
		String temp_result = matcher_first.replaceFirst("");
		Matcher matcher_second = pattern.matcher(temp_result);
		String result = matcher_second.replaceAll("\t");
		try {
			queue.put(result.toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

