package qopmo.util;

import java.util.Random;

public class MiRandom {

	private int minimo = 0;
	private int maximo = 100;
	private int total = maximo - minimo;
	private int medio = (maximo + minimo) / 2;
	private double valor;
	private double desviacionEstandard;

	public MiRandom() {
		Random r = new Random(System.currentTimeMillis());
		valor = r.nextGaussian();
	}

	public MiRandom(double desvStd) {
		Random r = new Random(System.nanoTime());
		valor = r.nextInt(getMaximo());
		if (valor < getMedio())
			valor -= getMedio();
		desviacionEstandard = desvStd/getTotal();
	}

	public MiRandom(int min, int max, double desvStd) {
		setMinimo(min);
		setMaximo(max);
		setMedio();
		setTotal();
		Random r = new Random(System.nanoTime());
		valor = r.nextInt(getMaximo());
		if (valor < getMedio())
			valor -= getMedio();
		desviacionEstandard = desvStd/getTotal();
	}

	public int getMinimo() {
		return minimo;
	}

	public void setMinimo(int minimo) {
		this.minimo = minimo;
	}

	public int getMaximo() {
		return maximo;
	}

	public void setMaximo(int maximo) {
		this.maximo = maximo;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public double getDesviacionEstandard() {
		return desviacionEstandard;
	}

	public void setDesviacionEstandard(int desviacionEstandard) {
		this.desviacionEstandard = desviacionEstandard;
	}

	public int getMedio() {
		return medio;
	}

	public void setMedio() {
		medio = (getMaximo() + getMinimo()) / 2;
	}

	public void setMedio(int medio) {
		this.medio = medio;
	}

	public void setTotal() {
		this.total = getMaximo() - getMinimo();
	}

	public double getTotal() {
		return total;
	}

	public double obtenerRandom() {
		double retorno = getValor() * getDesviacionEstandard() + getMedio();
		return retorno;
	}

	public double random() {
		double retorno = getMinimo();
		retorno += Math.random() * (getMaximo() - getMinimo());
		return Math.round(retorno);
	}

	public double random2() {
		double retorno = getMinimo();
		retorno += Math.random() * (getMaximo() - getMinimo());
		retorno *= getDesviacionEstandard();
		return Math.round(retorno);
	}

	public double acotar() {
		double retorno = getValor();
		retorno = retorno * (getMaximo() - getMinimo());
		return retorno;
	}

	public void recalcular() {
		Random r = new Random(System.currentTimeMillis());
		valor = r.nextGaussian();

	}

}
