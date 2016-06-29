import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *author: christ
 *data: 2015年10月19日下午9:18:52
 *function:
 */

public class Main {
	public static void main(String[] args) throws Exception {
		Random random = new Random(); 
		//训练数据集 1000个数据
		List<Integer> list = new ArrayList<Integer>();  
        for (int i = 0; i != 1000; i++) {  
            int value = random.nextInt();  
            list.add(value);
        }  
        List<double[]>train_dataY = new ArrayList<double[]>();
        List<double[]>train_dataX = new ArrayList<double[]>();
        for (int value : list) {  
            double[] real = new double[4];  
            if (value >= 0)  
                if ((value & 1) == 1)  
                    real[0] = 1;  
                else  
                    real[1] = 1;  
            else if ((value & 1) == 1)  
                real[2] = 1;  
            else  
                real[3] = 1;  
            train_dataY.add(real);
            double[] binary = new double[32];  
            int index = 31;  
            do {  
                binary[index--] = (value & 1);  
                value >>>= 1;  
            } while (value != 0);
            train_dataX.add(binary);
        }
        int[] unitsNum = {32,15,15,4};
		NeuralNetwork network = new NeuralNetwork(train_dataX, train_dataY, unitsNum, 0.25,0.01);
		network.grad_des();
		
		while (true) {  
            byte[] input = new byte[10];  
            System.in.read(input);  
            Integer value = Integer.parseInt(new String(input).trim());  
            int rawVal = value;  
            double[] binary = new double[32];  
            int index = 31;  
            do {  
                binary[index--] = (value & 1);  
                value >>>= 1;  
            } while (value != 0);  
  
            double[] result = network.application(binary);  
  
            double max = -Integer.MIN_VALUE;  
            int idx = -1;  
  
            for (int i = 0; i != result.length; i++) {  
            	System.out.println(result[i]);
                if (result[i] > max) {  
                    max = result[i];  
                    idx = i;  
                }  
            }  
  
            switch (idx) {  
            case 0:  
                System.out.format("%d是一个正奇数\n", rawVal);  
                break;  
            case 1:  
                System.out.format("%d是一个正偶数\n", rawVal);  
                break;  
            case 2:  
                System.out.format("%d是一个负奇数\n", rawVal);  
                break;  
            case 3:  
                System.out.format("%d是一个负偶数\n", rawVal);  
                break;  
            }  
        }  
	}
}

