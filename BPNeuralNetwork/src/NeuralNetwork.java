import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *author: christ
 *data: 2015��10��18������9:26:23
 *function:
 */

public class NeuralNetwork {
	private final double ep = Math.pow(10, 0);
	private final double ep_1 = Math.pow(10, -4);
	private Random make_random;
	/**ѧϰЧ��*/
	private double learningrate;
	/**���򻯲�����ƽ��������*/
	private double momentum;
	/**ѵ������������ֵX��һ��5000����ÿ��400������*/
	private List<double[]>train_dataX;
	/**ѵ�����������ݵķ�������ֵY��һ��5000����ÿ��1������,ÿ����yֵ����һ���������飬���鳤���Ƿֵ���ĸ���*/
	private List<double[]>train_dataY;
	/**thetaֵ��L������L-1�����飬��Ϊ���һ�㲻��ѵ��theta*/
	private List<List<Double>>[]theta;
	/**ÿһ��ÿһ��theta��ƫ����*/
	private List<List<Double>>[]partial_theta;
	/**���������*/
	private int L;
	/**ÿ��ĵ�Ԫ����*/
	private int[] unitsNum;
	/**��ǰ�����㷨��ÿһ���aֵ*/
	private List<double[]>levelValue;
	/**��󴫲�ÿһ���derta,levelDerta.get(0)�����һ������*/
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
	 * �����ʼ��ÿ���thetaֵ
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
	 * @return ��ÿһ����ÿһ��theta��ƫ����
	 * @throws Exception 
	 */
	public void calculatePartialValue() throws Exception{
		/**��ʱ��ű��μ������ƫ����*/
		List<List<Double>>[]temp_partial_theta = new ArrayList[L-1];
		int datasize = train_dataX.size();
		for(int i = 0; i < L-1 ; i++){
			temp_partial_theta[i] = new ArrayList<List<Double>>();
			//Ϊÿ���theta��ƫ��������ֵ0
			for(int row = 0; row < unitsNum[i+1]; row++){
				List<Double>temp = new ArrayList<Double>();
				for(int col = 0; col < unitsNum[i] +1; col++){
					temp.add(0.0);
				}
				temp_partial_theta[i].add(temp);
			}
		}
		//����ÿһ������
		for(int i = 0; i < datasize; i++){
			forwardAlgorithm(train_dataX.get(i));
			errorDerta(i);
			//����ÿһ��theta��ƫ����
			for(int level = 0; level < L-1; level++){
				for(int row = 0; row < unitsNum[level+1]; row++){
					for(int col = 0; col < unitsNum[level]+1; col++){
						if(col == 0){
							double temp = temp_partial_theta[level].get(row).get(col) + 1.0 * levelDerta.get(L-1-(level+1))[row];
							temp_partial_theta[level].get(row).set(col, temp);
						}else{
							double temp = temp_partial_theta[level].get(row).get(col) + levelValue.get(level)[col-1]*levelDerta.get(L-1-(level+1))[row];//���һ�������Ǵ���levelDerta(0)��
							temp_partial_theta[level].get(row).set(col, temp);
						}
					}
				}
			}
			//System.out.println("���ڼ����" + i + "������ѵ������ƫ����");
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
	 * ��ǰ�����㷨����level���aֵ
	 * @param input_i �����¼
	 * @param level �����level�� ��0����
	 * @return �����level���aֵ������double������
	 */
	public double[] nextPropagation(double[] input_i,int level){
		double[] levelValue = new double[unitsNum[level]]; 
		/**��һ��*/
		int last_level = level - 1;
		/**level���unit����*/
		int units_num = unitsNum[level];
		double additionBais = 1.0;
		/**����ÿ����Ԫ*/
		for(int i = 0; i < units_num; i++){
			double z = theta[last_level].get(i).get(0) * additionBais;
			for(int j = 1; j < unitsNum[last_level]+1; j++){  // ��
				z += theta[last_level].get(i).get(j) * input_i[j-1];
			}
			levelValue[i] = sigmoid(z);
		}
		return levelValue;
	}
	/**
	 * ��ǰ�����㷨��ͬʱ��ÿһ������ƫ������a0��aֵ������levelValue��
	 * @param input_i ����ԭʼ����
	 * @return ������ǰ�����������h(x)��ֵ������double������
	 */
	public double[] forwardAlgorithm(double[] input_i){
		List<double[]>temp_levelValue = new ArrayList<double[]>();
		temp_levelValue.add(input_i);  //�����ֵ
		//�ӵ�һ�㿪ʼ�㣨���ز㣩
		int level = 1;
		double[] temp_value = nextPropagation(input_i, level);
		temp_levelValue.add(temp_value);  //��һ�����ز�ֵ(�����ƫ����֮���ֵ)
		level++;
		//����˵һ���Ĳ㣬��0��Ϊ����㣬�ӵ�1�㿪ʼ����
		while(level < L){
			temp_value = nextPropagation(temp_value, level);
			temp_levelValue.add(temp_value);
			level++;
		}
		levelValue = temp_levelValue;
		return temp_value;
	}
	/**
	 * ����������
	 * @param one
	 * @param two
	 * @return 
	 * @throws Exception 
	 */
	public double[] ArraydotMulti(double[] one,double[] two) throws Exception{
		if(one.length != two.length){
			throw new Exception("��˵��������鳤�Ȳ����");
		}
		double[] result = new double[one.length];
		for(int i = 0; i < one.length; i++){
			result[i] = one[i] * two[i];
		}
		return result;
	} 
	/**
	 * 
	 * @param a ĳһ���ز��aֵ
	 * @return ��Ӷ����ƫ����
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
	 * 1��ȥ����
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
	 * �����ļ���
	 * @param one
	 * @param two
	 * @return
	 * @throws Exception 
	 */
	public double[] ArrayminusArray(double[] one, double[] two) throws Exception{
		if(one.length != two.length){
			throw new Exception("�������鲻�Ե�");
		}
		double[] result = new double[one.length];
		for(int i = 0; i < one.length; i++){
			result[i] = one[i] - two[i];
		}
		return result;
	}
	/**
	 * �������
	 * @throws Exception 
	 */
	public double ArrayMultiArray(double[]a, double[]b) throws Exception{
		if(a.length!=b.length){
			throw new Exception("�ڻ����Ȳ���");
		}
		double result =0.0;
		for(int i = 0; i < a.length; i++){
			result += a[i] * b[i];
		}
		return result;
	}
	/**
	 * ����log
	 */
	public double[] logArray(double[]a){
		double[] result = new double[a.length];
		for(int i = 0; i < a.length; i++){
			result[i] = Math.log(a[i] + Double.MIN_VALUE);
		}
		return result;
	}
	/**
	 * ����level������
	 * @param level ��level�� ��level != L��
	 * @param nextderta ��һ������ֵ����Ϊ����󴫲�
	 * @param level���aֵ
	 * @return
	 * @throws Exception 
	 */
	public double[] calculate_derta(int level,double []nextderta,double []level_a) throws Exception{
		int theta_num = unitsNum[level] + 1;//+1 ��Ϊ�и��������
		double[] temp_first = new double[theta_num-1];
		for(int i = 1; i < theta_num; i++){  //������ƫ���������
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
	 * ����ÿһ��������˵�һ������� levelDerta.get(0)����������һ������
	 * @param i ��i�����ݣ������ҳ���i�����ݶ�Ӧ���������,�ӵ�0����ʼ
	 * @return
	 * @throws Exception 
	 */
	public void errorDerta(int i) throws Exception{
		List<double[]>temp_levelDerta = new ArrayList<double[]>();
		int level = L-1; //�����һ������㿪ʼ����
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
	 * ������ۺ���
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
					double temp = theta[i].get(j).get(k);   //��ʱȡ��thetaֵ
					theta[i].get(j).set(k, temp + ep_1);
					double first = costFunction(); //����theta���j����
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
	 * �ݶ��½��㷨
	 * @throws Exception 
	 */
	public void grad_des() throws Exception{
		boolean ischeck = false;
		randomInitTheta();
		//ѧϰ200��
		for(int i = 0; i < 1000; i++){
			System.out.println("===============" + " " + "��" + i + "��ѧϰ" + " " + "===============");
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

