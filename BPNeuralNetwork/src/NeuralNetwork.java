import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *author: christ
 *data: 2015年10月18日上午9:26:23
 *function:
 */

public class NeuralNetwork {
	private final double ep = Math.pow(10, 0);
	private final double ep_1 = Math.pow(10, -4);
	private Random make_random;
	/**学习效率*/
	private double learningrate;
	/**正则化参数（平滑参数）*/
	private double momentum;
	/**训练集各个属性值X，一共5000条，每条400个属性*/
	private List<double[]>train_dataX;
	/**训练集各条数据的分类属性值Y，一共5000条，每条1个属性,每个条y值都是一个向量数组，数组长度是分的类的个数*/
	private List<double[]>train_dataY;
	/**theta值，L层则有L-1个数组，因为最后一层不用训练theta*/
	private List<List<Double>>[]theta;
	/**每一层每一个theta的偏导数*/
	private List<List<Double>>[]partial_theta;
	/**神经网络层数*/
	private int L;
	/**每层的单元个数*/
	private int[] unitsNum;
	/**向前传播算法后每一层的a值*/
	private List<double[]>levelValue;
	/**向后传播每一层的derta,levelDerta.get(0)是最后一层的误差*/
	private List<double[]>levelDerta;
	public NeuralNetwork(List<double[]>train_dataX,List<double[]>train_dataY,int []unitsNum,double l_rate,double momentum){
		this.train_dataX = train_dataX;
		this.train_dataY = train_dataY;
		this.L = unitsNum.length;
		this.learningrate = l_rate;
		this.momentum = momentum;
		theta = new ArrayList[L-1];
		this.unitsNum = unitsNum;
		for(int i = 0; i < L-1 ; i++){
			theta[i] = new ArrayList<List<Double>>();
		}
		make_random = new Random();
	}
	
	public List<List<Double>>[] getTheta() {
		return theta;
	}

	/**
	 * 随机初始化每层的theta值
	 */
	public void randomInitTheta(){
		for(int level = 0; level < L-1; level++){
			for(int row = 0; row < unitsNum[level+1]; row++){
				List<Double>row_theta = new ArrayList<Double>();
				for(int col = 0; col < unitsNum[level]+1; col++){
					double random = make_random.nextDouble();
					double random_theta = random * 2*ep - ep;
					row_theta.add(random_theta);
				}
				theta[level].add(row_theta);
			}
		}
	}
	/**
	 * 
	 * @return 对每一层中每一个theta的偏导数
	 * @throws Exception 
	 */
	public void calculatePartialValue() throws Exception{
		/**临时存放本次计算出的偏导数*/
		List<List<Double>>[]temp_partial_theta = new ArrayList[L-1];
		int datasize = train_dataX.size();
		for(int i = 0; i < L-1 ; i++){
			temp_partial_theta[i] = new ArrayList<List<Double>>();
			//为每层的theta的偏导数赋初值0
			for(int row = 0; row < unitsNum[i+1]; row++){
				List<Double>temp = new ArrayList<Double>();
				for(int col = 0; col < unitsNum[i] +1; col++){
					temp.add(0.0);
				}
				temp_partial_theta[i].add(temp);
			}
		}
		//遍历每一条数据
		for(int i = 0; i < datasize; i++){
			forwardAlgorithm(train_dataX.get(i));
			errorDerta(i);
			//更新每一个theta的偏导数
			for(int level = 0; level < L-1; level++){
				for(int row = 0; row < unitsNum[level+1]; row++){
					for(int col = 0; col < unitsNum[level]+1; col++){
						if(col == 0){
							double temp = temp_partial_theta[level].get(row).get(col) + 1.0 * levelDerta.get(L-1-(level+1))[row];
							temp_partial_theta[level].get(row).set(col, temp);
						}else{
							double temp = temp_partial_theta[level].get(row).get(col) + levelValue.get(level)[col-1]*levelDerta.get(L-1-(level+1))[row];//最后一层的误差是存在levelDerta(0)中
							temp_partial_theta[level].get(row).set(col, temp);
						}
					}
				}
			}
			//System.out.println("正在计算第" + i + "条数据训练出的偏导数");
		}
		for(int level = 0; level < L-1; level++){
			for(int row = 0; row < unitsNum[level+1]; row++){
				for(int col = 0; col < unitsNum[level]+1; col++){
					if(col == 0){
						double temp = temp_partial_theta[level].get(row).get(col) / datasize;
						temp_partial_theta[level].get(row).set(col, temp);
					}else{
						double temp = temp_partial_theta[level].get(row).get(col)/datasize + momentum * theta[level].get(row).get(col);
						temp_partial_theta[level].get(row).set(col, temp);
					}
				}
			}
		}
		partial_theta = temp_partial_theta;
	}
	public double sigmoid(double z){
		return 1/(1+Math.exp(-z));
	}
	/**
	 * 向前传播算法，求level层的a值
	 * @param input_i 输入记录
	 * @param level 计算第level层 从0层起
	 * @return 计算出level层的a值保存在double数组中
	 */
	public double[] nextPropagation(double[] input_i,int level){
		double[] levelValue = new double[unitsNum[level]]; 
		/**上一层*/
		int last_level = level - 1;
		/**level层的unit个数*/
		int units_num = unitsNum[level];
		double additionBais = 1.0;
		/**计算每个单元*/
		for(int i = 0; i < units_num; i++){
			double z = theta[last_level].get(i).get(0) * additionBais;
			for(int j = 1; j < unitsNum[last_level]+1; j++){  // ？
				z += theta[last_level].get(i).get(j) * input_i[j-1];
			}
			levelValue[i] = sigmoid(z);
		}
		return levelValue;
	}
	/**
	 * 向前传播算法，同时将每一层的添加偏移量的a0的a值保存在levelValue中
	 * @param input_i 输入原始数据
	 * @return 最终向前传播计算出的h(x)的值，存在double数组中
	 */
	public double[] forwardAlgorithm(double[] input_i){
		List<double[]>temp_levelValue = new ArrayList<double[]>();
		temp_levelValue.add(input_i);  //输入层值
		//从第一层开始算（隐藏层）
		int level = 1;
		double[] temp_value = nextPropagation(input_i, level);
		temp_levelValue.add(temp_value);  //第一个隐藏层值(添加了偏移量之后的值)
		level++;
		//比如说一共四层，第0层为输入层，从第1层开始计算
		while(level < L){
			temp_value = nextPropagation(temp_value, level);
			temp_levelValue.add(temp_value);
			level++;
		}
		levelValue = temp_levelValue;
		return temp_value;
	}
	/**
	 * 两个数组点乘
	 * @param one
	 * @param two
	 * @return 
	 * @throws Exception 
	 */
	public double[] ArraydotMulti(double[] one,double[] two) throws Exception{
		if(one.length != two.length){
			throw new Exception("点乘的两个数组长度不相等");
		}
		double[] result = new double[one.length];
		for(int i = 0; i < one.length; i++){
			result[i] = one[i] * two[i];
		}
		return result;
	} 
	/**
	 * 
	 * @param a 某一隐藏层的a值
	 * @return 添加额外的偏移量
	 */
	public double[] additionBias(double []a){
		double[] b = new double[a.length + 1];
		b[0] = 1;
		for(int i = 1; i < b.length; i++){
			b[i] = a[i-1];
		}
		return b;
	}
	/**
	 * 1减去数组
	 * @param one
	 * @return
	 */
	public double[] OneMinusArray(double[] one){
		double[] result = new double[one.length];
		for(int i = 0; i < one.length; i++){
			result[i] = 1d - one[i];
		}
		return result;
	}
	/**
	 * 向量的减法
	 * @param one
	 * @param two
	 * @return
	 * @throws Exception 
	 */
	public double[] ArrayminusArray(double[] one, double[] two) throws Exception{
		if(one.length != two.length){
			throw new Exception("减法数组不对等");
		}
		double[] result = new double[one.length];
		for(int i = 0; i < one.length; i++){
			result[i] = one[i] - two[i];
		}
		return result;
	}
	/**
	 * 向量点积
	 * @throws Exception 
	 */
	public double ArrayMultiArray(double[]a, double[]b) throws Exception{
		if(a.length!=b.length){
			throw new Exception("内积长度不等");
		}
		double result =0.0;
		for(int i = 0; i < a.length; i++){
			result += a[i] * b[i];
		}
		return result;
	}
	/**
	 * 向量log
	 */
	public double[] logArray(double[]a){
		double[] result = new double[a.length];
		for(int i = 0; i < a.length; i++){
			result[i] = Math.log(a[i] + Double.MIN_VALUE);
		}
		return result;
	}
	/**
	 * 计算level层的误差
	 * @param level 第level层 （level != L）
	 * @param nextderta 下一层的误差值，因为是向后传播
	 * @param level层的a值
	 * @return
	 * @throws Exception 
	 */
	public double[] calculate_derta(int level,double []nextderta,double []level_a) throws Exception{
		int theta_num = unitsNum[level] + 1;//+1 因为有个额外变量
		double[] temp_first = new double[theta_num-1];
		for(int i = 1; i < theta_num; i++){  //不计算偏移量的误差
			double z = 0;
			for(int j = 0; j < unitsNum[level+1]; j++){
				z += theta[level].get(j).get(i) * nextderta[j];
			}
			temp_first[i-1] = z;
		}
		double[] oneMinuslevel_a = OneMinusArray(level_a);
		double[] temp_second = new double[theta_num];
		temp_second = ArraydotMulti(level_a, oneMinuslevel_a);
		return ArraydotMulti(temp_first, temp_second);
		
	}
	/**
	 * 计算每一层的误差，除了第一层输入层 levelDerta.get(0)保存的是最后一层的误差
	 * @param i 第i条数据，用于找出第i条数据对应的输出向量,从第0条开始
	 * @return
	 * @throws Exception 
	 */
	public void errorDerta(int i) throws Exception{
		List<double[]>temp_levelDerta = new ArrayList<double[]>();
		int level = L-1; //从最后一层输出层开始计算
		double[] tempderta = ArrayminusArray(levelValue.get(level), train_dataY.get(i));
		temp_levelDerta.add(tempderta);
		level--;
		while(level > 0){
			tempderta = calculate_derta(level, tempderta, levelValue.get(level));
			temp_levelDerta.add(tempderta);
			level--;
		}
		levelDerta = temp_levelDerta;
	}
	
	public void updataTheta(){
		for(int level = 0; level < L-1; level++){
			for(int row = 0; row < unitsNum[level+1]; row++){
				for(int col = 0; col < unitsNum[level]+1; col++){
					double temp = theta[level].get(row).get(col) - learningrate * partial_theta[level].get(row).get(col);
					theta[level].get(row).set(col, temp);
				}
			}
		}
	}
	/**
	 * 计算代价函数
	 * @throws Exception 
	 */
	public double costFunction() throws Exception{
		int datasize = train_dataX.size();
		double cost = 0.0;
		for(int i = 0; i < datasize; i++){
			forwardAlgorithm(train_dataX.get(i));
			double first = ArrayMultiArray(train_dataY.get(i),logArray(levelValue.get(L-1)));
			double second = ArrayMultiArray(OneMinusArray(train_dataY.get(i)), logArray(OneMinusArray(levelValue.get(L-1))));
			double sum = first + second;
			cost += sum;
		}
		double bias = 0.0;
		for(int i = 0; i < L-1; i++){
			for(int j = 0; j < unitsNum[i+1]; j++){
				for(int k = 1; k < unitsNum[i] + 1; k++){
					bias += Math.pow(theta[i].get(j).get(k), 2);
				}
			}
		}
		return (-cost/datasize)+ (momentum * bias)/(2*datasize);
	}
	
	public void check_grad() throws Exception{
		for(int i = 0; i < L-1; i++){
			for(int j = 0; j < unitsNum[i+1]; j++){
				for(int k = 1; k < unitsNum[i] + 1; k++){
					double temp = theta[i].get(j).get(k);   //暂时取出theta值
					theta[i].get(j).set(k, temp + ep_1);
					double first = costFunction(); //更新theta后的j函数
					theta[i].get(j).set(k, temp - ep_1);
					double second = costFunction();
					double check_partialValue = (first - second) / (2*ep_1);
					theta[i].get(j).set(k, temp);
					System.out.print(check_partialValue + "," + partial_theta[i].get(j).get(k) + "\t");
				}
				System.out.println();
			}
			System.out.println("========================================================");
		}
	}
	/**
	 * 梯度下降算法
	 * @throws Exception 
	 */
	public void grad_des() throws Exception{
		boolean ischeck = false;
		randomInitTheta();
		//学习200次
		for(int i = 0; i < 1000; i++){
			System.out.println("===============" + " " + "第" + i + "次学习" + " " + "===============");
			calculatePartialValue();
			if(!ischeck){
				check_grad();
				ischeck = true;
			}
			updataTheta();
		}
		
	}
	
	public double[] application(double[] test) throws Exception{
		forwardAlgorithm(test);
		//System.out.println(costFunction());
		return levelValue.get(L-1);
	}
	
}

