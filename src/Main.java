import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

class Nm {
	static long tmp = Long.MAX_VALUE;
	static final int K_MAX = 3000;
	static final double E_MAX = 0.1;
	static final double EE_TA = 0.1;
	private double w;
	private double b;
	private double[] d;
	private double[] x;
	private int count = 0;
	
	public static double xorShift() {
		tmp = tmp ^ (tmp << 7);
		return (tmp = tmp ^ (tmp >> 9)) / (double) Long.MAX_VALUE;
	}
	
	public double diff() {
		double tmp = 0.;
		for (int i = 0; i < d.length; ++i) {
			tmp += Math.pow(d[i] - predict(x[i]), 2);
		}
		return Math.sqrt(tmp / d.length);
	}
	
	public boolean learn(int index) {
		// double rand = xorShift();
		double diff = d[index] - predict(x[index]);
		w += EE_TA * diff * x[index];
		b += EE_TA * diff;
		++count;
		return diff() <= E_MAX;
	}
	
	public boolean learn(int index, StringBuilder sb) {
		double rand = xorShift();
		double diff = d[index] - predict(x[index]);
		w += EE_TA * EE_TA * x[index];
		b += EE_TA * diff;
		++count;
		double tmpDiff = diff();
		sb.append(count);
		sb.append(", ");
		sb.append(tmpDiff);
		sb.append('\n');
		return tmpDiff <= E_MAX;
	}
	
	public double predict(double x) {
		return w * x + b;
	}
	
	public Nm(double[] d, double[] x) {
		w = (double) Nm.xorShift() / 1000.;
		b = (double) Nm.xorShift() / 1000.;
		this.d = d;
		this.x = x;
	}
	
	public Nm(double[] d, double[] x, double w, double b) {
		this.w = w;
		this.b = b;
		this.d = d;
		this.x = x;
	}
	
	public void setB(double b) {
		this.b = b;
	}
	
	public void setW(double w) {
		this.w = w;
	}
	
	public double getB() {
		return b;
	}
	
	public double getW() {
		return w;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
}

public class Main {
	private static final double X[] = {0.1, 0.3, 0.5, 0.7, 0.9};
	private static final double D[] = {0.3381, 0.6658, 0.8627, 1.2297, 1.6574};
	
	public static void kadai1() throws FileNotFoundException, IOException {
		Nm nm = new Nm(D, X);
		Nm nm2 = new Nm(D, X);
		nm2.setW(nm.getW());
		nm2.setB(nm.getB());
		System.out.printf("結合係数wの初期値: %.20f\n", nm.getW());
		System.out.printf("バイアスbの初期値: %.20f\n", nm.getB());
		StringBuilder sb = new StringBuilder();
		StringBuilder sbCsv = new StringBuilder();
		FileOutputStream fo = new FileOutputStream("./1.csv");
		sbCsv.append("Iteration,平均二乗誤差\n");
		for (int i = 0; nm.getCount() < nm.K_MAX; ++i) {
			sbCsv.append(i);
			sbCsv.append(',');
			sbCsv.append(nm.diff());
			sbCsv.append('\n');
			if (nm.learn(i % D.length, sb)) {
				sbCsv.append(i + 1);
				sbCsv.append(',');
				sbCsv.append(nm.diff());
				sbCsv.append('\n');
				System.out.println("Sucess: 入力パターン1では" + (nm.getCount() + 1) + "回学習しました");
				break;
			}
		}
		fo.write(sbCsv.toString().getBytes("Windows-31J"));
		fo = new FileOutputStream("./2.csv");
		StringBuilder sb2 = new StringBuilder();
		sbCsv = new StringBuilder();
		sbCsv.append("Iteration,平均二乗誤差\n");
		for (int i = 0; nm2.getCount() < nm2.K_MAX; ++i) {
			sbCsv.append(i);
			sbCsv.append(',');
			sbCsv.append(nm2.diff());
			sbCsv.append('\n');
			if (nm2.learn(D.length - i % D.length - 1, sb2)) {
				sbCsv.append(i + 1);
				sbCsv.append(',');
				sbCsv.append(nm2.diff());
				sbCsv.append('\n');
				System.out.println("Sucess: 入力パターン2では" + (nm2.getCount()  + 1) + "回学習しました");
				break;
			}
		}
		fo.write(sbCsv.toString().getBytes("Windows-31J"));
		System.out.println("入力パターン1");
		for (int i = 0; i < X.length; ++i) {
			double tmp = nm.predict(X[i]);
			System.out.printf("予測: %.5f", tmp);
			System.out.printf(", 期待される値: %.5f", D[i]);
			System.out.printf(", 誤差: %.5f\n", D[i] - tmp);
		}
		System.out.println("平均二乗誤差: " + nm.diff());
		System.out.println("入力パターン2");
		for (int i = 0; i < X.length; ++i) {
			double tmp = nm2.predict(X[i]);
			System.out.printf("予測: %.5f", tmp);
			System.out.printf(", 期待される値: %.5f", D[i]);
			System.out.printf(", 誤差: %.5f\n", D[i] - tmp);
		}
		System.out.println("平均二乗誤差: " + nm2.diff());
		// System.out.println(sb.toString());
	}
	
	public static void kadai2() throws FileNotFoundException, IOException {
		Nm nm = new Nm(D, X);
		nm.setB(0.0009);
		nm.setW(0.0001);
		System.out.printf("結合係数wの初期値: %.20f\n", nm.getW());
		System.out.printf("バイアスbの初期値: %.20f\n", nm.getB());
		StringBuilder sb = new StringBuilder();
		StringBuilder sbCsv = new StringBuilder();
		FileOutputStream fo = new FileOutputStream("./1.csv");
		sbCsv.append("Iteration,平均二乗誤差\n");
		for (int i = 0; nm.getCount() < nm.K_MAX; ++i) {
			sbCsv.append(i);
			sbCsv.append(',');
			sbCsv.append(nm.diff());
			sbCsv.append('\n');
			if (nm.learn(i % D.length, sb)) {
				sbCsv.append(i + 1);
				sbCsv.append(',');
				sbCsv.append(nm.diff());
				sbCsv.append('\n');
				System.out.println("Sucess: " + (nm.getCount() + 1) + "回学習しました");
				break;
			}
		}
		fo.write(sbCsv.toString().getBytes("Windows-31J"));
		System.out.println("入力パターン1");
		for (int i = 0; i < X.length; ++i) {
			double tmp = nm.predict(X[i]);
			System.out.printf("予測: %.5f", tmp);
			System.out.printf(", 期待される値: %.5f", D[i]);
			System.out.printf(", 誤差: %.5f\n", D[i] - tmp);
		}
		System.out.println("平均二乗誤差: " + nm.diff());
	}
	
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < Math.random() * 10000; ++i) {
			Nm.xorShift();
		}
		
		// kadai1();
		// kadai2();
	}
}

