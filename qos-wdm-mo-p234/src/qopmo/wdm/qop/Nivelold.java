package qopmo.wdm.qop;

public enum Nivelold {
	Oro(101), Plata0(100), Plata1(75), Plata2(50), Plata3(25), Bronce(0);

	private double recuperacion;

	private Nivelold(double recuperacion) {
		this.recuperacion = recuperacion;
	}

	/**
	 * Obtiene el limite inferior de Probabilidad de Recuperación establecido
	 * para este Nivel.
	 * 
	 * @return
	 */
	public double getRecuperacion() {
		return this.recuperacion;
	}

	/**
	 * Función que retorna si el nivel es de Oro o no.
	 */
	public boolean esOro() {
		boolean retorno = false;
		if (this.ordinal() == Nivelold.Oro.ordinal()) // Oro
			retorno = true;
		return retorno;		
	}
	
	/**
	 * Función que retorna si el nivel es de algún nivel de Plata o no.
	 */
	public boolean esPlata() {
		boolean retorno = true;
		//System.out.print("ESPLATA: "+ this.ordinal());
		//System.out.println(" ORO: "+ Nivel.Bronce.ordinal());
		if (this.ordinal() == Nivelold.Oro.ordinal()) // Oro
			retorno = false;
		else if (this.ordinal() == Nivelold.Bronce.ordinal()) // Bronce
			retorno = false;

		return retorno;
	}
	
	/**
	 * Función que retorna si el nivel es de algún nivel de Bronce o no.
	 */	
	public boolean esBronce() {
		boolean retorno = false;
		if (this.ordinal() == Nivelold.Bronce.ordinal()) // Bronce
			retorno = true;
		return retorno;
	}
	
	/**
	 * Obtiene la diferencia de niveles con una probabilidad ingresada.
	 * 
	 * @param valor
	 * @return
	 */
	public int diferencia(double valor) {
		int retorno = 0;
		int propuestaNivelOrdinal = calcularNivel(valor);
		int esteNivelOrdinal = calcularNivel(this.getRecuperacion());
		retorno = esteNivelOrdinal - propuestaNivelOrdinal;

		if (retorno >= 0)
			retorno = 0;
		else
			retorno *= -1;

		return retorno;
	}

	private int calcularNivel(double valor) {
		int retorno;
		if (valor == 101.0)
			retorno = 0;
		else if (valor == 100.0)
			retorno = 1;
		else if (valor >= 75.0)
			retorno = 2;
		else if (valor >= 50.0)
			retorno = 3;
		else if (valor >= 25.0)
			retorno = 4;
		else
			retorno = 5;
		return retorno;

	}
	/**
	 * Obtiene la diferencia de niveles con una probabilidad ingresada.
	 * 
	 * @param valor
	 * @return
	 */
	public double ganancia(double valor) {
		double retorno = 0.0;
		retorno = valor - this.getRecuperacion();
		//System.out.println("--" + retorno+"--"+valor);
		if (retorno < 0.0)
			retorno = 0.0; 

		return retorno;
	}
}