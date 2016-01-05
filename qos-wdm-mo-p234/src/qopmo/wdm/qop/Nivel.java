package qopmo.wdm.qop;

public enum Nivel {
	// Niveles Prueba 2.
	Oro(101), Plata0(100), Plata1(90), Plata2(80), Plata3(70),Plata4(60), Plata5(50), Plata6(40), Plata7(30), Plata8(20), Plata9(10), Plata10(0), Bronce(0);
	// Niveles Prueba 3.
	//Oro(101), Plata0(100), Plata1(75), Plata2(50), Plata3(25), Bronce(0);
	// Niveles Prueba 4.
	//Oro(101), Plata0(100), Plata1(90), Plata2(80), Plata3(70),Plata4(60), Plata5(50), Plata6(40), Plata7(30), Plata8(20), Plata9(10), Bronce(0);

	private double recuperacion;

	private Nivel(double recuperacion) {
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
		if (this.ordinal() == Nivel.Oro.ordinal()) // Oro
			retorno = true;
		return retorno;
	}

	/**
	 * Función que retorna si el nivel es de algún nivel de Plata o no.
	 */
	public boolean esPlata() {
		boolean retorno = true;
		if (this.ordinal() == Nivel.Oro.ordinal()) // Oro
			retorno = false;
		else if (this.ordinal() == Nivel.Bronce.ordinal()) // Bronce
			retorno = false;

		return retorno;
	}

	/**
	 * Función que retorna si el nivel es de algún nivel de Bronce o no.
	 */
	public boolean esBronce() {
		boolean retorno = false;
		if (this.ordinal() == Nivel.Bronce.ordinal()) // Bronce
			retorno = true;
		return retorno;
	}

	/**
	 * Obtiene la diferencia de niveles con una probabilidad ingresada.
	 * 
	 * @param valor
	 * @return
	 */
	public int diferencia(double pRecuperacion) {
		int retorno = 0;
		
		if (this.esOro())
			pRecuperacion += 1;
		
		int nivelSolicitado = obtenerNivel(pRecuperacion);
		int nivelLocal = obtenerNivel(this.getRecuperacion());
		retorno = nivelLocal - nivelSolicitado;

		// Si nivelLocal >= nivelSolicitado
		if (retorno >= 0)
			retorno = 0;
		else
			retorno *= -1;

		return retorno;
	}

	/*
	 * Función para obtener el nivel del valor obtenido. El primero es 1 y va
	 * aumentando de a 1.
	 */
	private int obtenerNivel(double pRecuperacion) {
		int retorno = 0;

		for (Nivel n : values()) {
			if ((pRecuperacion < n.getRecuperacion())) {
				retorno++;
			}
		}

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

		if (retorno < 0.0)
			retorno = 0.0;

		return retorno;
	}
}