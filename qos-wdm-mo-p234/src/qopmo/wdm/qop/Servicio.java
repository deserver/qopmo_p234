package qopmo.wdm.qop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import qopmo.wdm.Camino;
import qopmo.wdm.CanalOptico;
import qopmo.wdm.Enlace;
import qopmo.wdm.Nodo;
import qopmo.wdm.Salto;

@Entity
public class Servicio implements Comparable<Servicio> {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	private Solicitud solicitud;

	@OneToOne(cascade = CascadeType.ALL)
	private Camino primario;

	@OneToOne(cascade = CascadeType.ALL)
	private Camino alternativo;

	private double pFalla;

	private double pRecuperacion = 0.00;

	public void setpRecuperacion(double pRecuperacion) {
		this.pRecuperacion = pRecuperacion;
	}

	@Transient
	private Map<Enlace, Set<Servicio>> serviciosSolapados;

	public Map<Enlace, Set<Servicio>> getServiciosSolapados() {
		return serviciosSolapados;
	}

	public Set<Servicio> getServiciosSolapados(Enlace e) {
		Set<Servicio> servicios = this.getServiciosSolapados().get(e);
		if (servicios == null)
			servicios = new HashSet<Servicio>();
		return servicios;
	}

	public void setServiciosSolapados(
			Map<Enlace, Set<Servicio>> serviciosSolapados) {
		this.serviciosSolapados = serviciosSolapados;
	}

	/**
	 * Calcula la Probabilidad de Recuperación (Pr) del Servicio.
	 */
	public void calcularProbabilidad() {
		double prob = 0.0;
		if (this.primario == null) {
			return;
		}
		Camino c = this.alternativo;
		if (c == null || c.getDestino() == null)
			return;

		// para cada enlace e del primario
		for (Enlace e : this.primario.getEnlaces()) {
			double respuesta = 1.0;
			// Para cada Servicio-Solapado del enlace e con el camino primario
			for (Servicio s : this.getServiciosSolapados(e)) {
				if (s == null)
					continue;

				Nivel n = s.getSolicitud().getNivel();
				if (s.getAlternativo() == null || n.esBronce())
					continue;

				respuesta += c.contadorIguales(s.getAlternativo(), true);
			}
			prob += 1 / respuesta;
		}

		double n = this.primario.getEnlaces().size();
		this.pRecuperacion = prob / n;
	}

	/**
	 * Función que retorno la diferencia entre probabilidad del nivel Solicitado
	 * y la Probabilidad calculada, considerando los niveles.
	 * 
	 * @param nivel
	 * @return
	 */
	public int evaluarNivel() {
		int retorno = 0;
		double probabilidad1 = this.getpRecuperacion();
		Nivel miNivel = this.solicitud.getNivel();

		retorno = miNivel.diferencia(probabilidad1);

		return retorno;
	}

	@Transient
	private List<Camino> alternativoLink;

	@Transient
	private boolean disponible = false;

	public static String BUFFER_DEBUG = "";

	public Servicio() {
	}

	/**
	 * Constructor principal
	 * 
	 * @param solicitud
	 *            Solicitud a la que se desea proveerle un servicio
	 */
	public Servicio(Solicitud solicitud) {
		super();
		this.solicitud = solicitud;
		this.serviciosSolapados = new HashMap<Enlace, Set<Servicio>>();
	}

	public Servicio(Camino primario, Camino alternativo, Solicitud solicitud) {
		super();
		this.primario = primario;
		this.alternativo = alternativo;
		this.solicitud = solicitud;
		this.alternativoLink = new ArrayList<Camino>();
		this.serviciosSolapados = new HashMap<Enlace, Set<Servicio>>();
	}

	public Servicio(Servicio s) {
		this.solicitud = s.solicitud;
		this.primario = new Camino(s.getPrimario());
		if (s.solicitud.getNivel().esBronce()) {
			this.alternativo = null;
			this.alternativoLink = null;
		} else {
			this.alternativo = new Camino(s.getAlternativo());
			this.alternativoLink = new ArrayList<Camino>();
		}

		this.serviciosSolapados = new HashMap<Enlace, Set<Servicio>>();
		this.pRecuperacion = s.getpRecuperacion() / 100.0;
	}

	/**
	 * Constructor usado para crear hijos
	 * 
	 * @param primario
	 * @param alternativo
	 * @param nivel
	 */
	public Servicio(Camino primario, Camino alternativo, Nivel nivel) {
		super();
		this.primario = primario;
		this.alternativo = alternativo;
		this.solicitud = new Solicitud(primario.getOrigen(),
				primario.getDestino(), nivel);
		this.serviciosSolapados = new HashMap<Enlace, Set<Servicio>>();
	}

	/**
	 * Getter de la solicitud
	 * 
	 * @return Solicitud
	 */
	public Solicitud getSolicitud() {
		return solicitud;
	}

	/**
	 * Obtiene la probabilidad de falla del servicio
	 * 
	 * @return Probabilidad de Falla
	 */
	public double getpFalla() {
		return ((int) (pFalla * 10000.0)) / 100.0;
	}

	/**
	 * Obtiene la probabilidad de recuperacion del servicio.
	 * 
	 * @return Probabilidad de servicio
	 */
	public double getpRecuperacion() {
		return pRecuperacion * 100.0;
	}

	/**
	 * Funcion de Simulacion, retorna true si el servicio esta disponible
	 * 
	 * @return Disponibildad del servicio
	 */
	public boolean estaDisponible() {
		return disponible;
	}

	/**
	 * Setter de la disponibilidad del servicio
	 * 
	 * @param disponible
	 */
	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSolicitud(Solicitud solicitud) {
		this.solicitud = solicitud;
	}

	public List<Camino> getAlternativoLink() {
		return alternativoLink;
	}

	public void setAlternativoLink(List<Camino> alternativoLink) {
		this.alternativoLink = alternativoLink;
	}

	public Camino getPrimario() {
		return primario;
	}

	public void setPrimario(Camino primario) {
		this.primario = primario;
	}

	public Camino getAlternativo() {
		return alternativo;
	}

	public void setPrimario() {

		boolean estaEnlazado = true;
		// Si se da esta condición hay error
		if (primario == null)
			return;

		if (solicitud.getNivel() != Nivel.Bronce) {
			estaEnlazado = primario.setEnlaces();
		} else {
			estaEnlazado = primario.setReservas(this,
					Exclusividad.SinReservasBronce);
		}

		// El camino calculado no puede ser utilizado por falta de recursos.
		if (!estaEnlazado) {
			// this.primario.desbloquearCanales();
			this.primario = null;
			// mrodas - 16-08-2013 - Para evitar problemas de referencias.
			this.alternativo = null;
			this.pRecuperacion = 0;
		}
	}

	public boolean setAlternativo() {
		boolean estaEnlazado = true;

		if (solicitud.getEsquema() != EsquemaRestauracion.Link) {
			if (primario == null || alternativo == null) {
				return false;
			}

			if (solicitud.getNivel().esOro()) {
				estaEnlazado = alternativo.setEnlaces();
			} else {
				estaEnlazado = alternativo.setReservas(this,
						Exclusividad.NoExclusivo);
			}
		} else {
			if (primario == null || alternativoLink == null)
				return false;

			if (solicitud.getNivel().esOro()) {
				for (Camino alternativoAux : this.alternativoLink) {
					estaEnlazado = alternativoAux.setEnlaces();
					if (!estaEnlazado) {
						this.desbloquearAlternativoLink();
						alternativoLink = null;
						return false;
					}
				}
			} else {
				for (Camino alternativoAux : this.alternativoLink) {
					estaEnlazado = alternativoAux.setReservas(this,
							Exclusividad.NoExclusivo);
				}

			}
		}
		// El camino calculado no puede ser utilizado por falta de recursos.
		if (!estaEnlazado) {
			if (solicitud.getEsquema() != EsquemaRestauracion.Link) {
				// this.alternativo.desbloquearCanales();
				this.alternativo = null;
				this.alternativoLink = null;
				// this.primario.desbloquearCanales();
				this.primario.desbloquearEnlaces();
				this.primario = null;
				this.pRecuperacion = 0;
			} else {
				for (Camino c : this.alternativoLink)
					c.desbloquearCanales();
				this.alternativo = null;
				this.alternativoLink = null;
				this.primario = null;
				this.pRecuperacion = 0;
			}
		}

		return estaEnlazado;
	}

	public void fijarRecursos() {
		if (primario == null)
			return;

		if (solicitud.getNivel() != Nivel.Bronce) {
			primario.fijarEnlaces();
		} else {
			primario.fijarReservas(this);
		}

		if (alternativo == null)
			return;

		if (solicitud.getNivel() == Nivel.Oro) {
			alternativo.fijarEnlaces();
		} else {
			alternativo.fijarReservas(this);
		}
	}

	/**
	 * Función para liberar Recursos utilizados por los caminos del Servicio.
	 */
	public void liberarRecursos() {
		if (primario == null)
			return;

		// Se libera Recursos del Primario.
		if (solicitud.getNivel() != Nivel.Bronce) {
			primario.desbloquearEnlaces();
		} else {
			primario.eliminarReservas(this);
		}

		if (alternativo == null)
			return;

		// Se libera Recursos del Secundario.
		if (solicitud.getNivel() == Nivel.Oro) {
			alternativo.desbloquearEnlaces();
		} else {
			alternativo.eliminarReservas(this);
		}
	}

	public void setAlternativo(Camino alternativo) {
		this.alternativo = alternativo;
	}

	public void buscarAlternativo() {
		if (primario == null) {
			alternativo = null;
			alternativoLink = null;
			return;
		}
		if (solicitud.getEsquema() == EsquemaRestauracion.FullPath) {
			primario.bloquearNodos();
			primario.getDestino().desbloquear();
			alternativo = primario.getOrigen().dijkstra(primario.getDestino(),
					solicitud.getExclusividadAlternativo());
			primario.desbloquearNodos();
		} else if (solicitud.getEsquema() == EsquemaRestauracion.Segment) {
			primario.bloquearCanales();
			alternativo = primario.getOrigen().dijkstra(primario.getDestino(),
					solicitud.getExclusividadAlternativo());
			primario.desbloquearCanales();
		} else if (solicitud.getEsquema() == EsquemaRestauracion.Link) {

			alternativoLink = new ArrayList<Camino>();
			for (Salto s : primario.getSaltos()) {
				CanalOptico co = s.getCanal();
				Nodo A = co.getExtremoA();
				Nodo B = co.getExtremoB();
				co.bloquear();
				Camino c = null;
				c = A.dijkstra(B, solicitud.getExclusividadAlternativo());
				if (c == null) {
					this.desbloquearAlternativoLink();
					this.alternativoLink = null;
					break;
				}
				alternativoLink.add(c);
				co.desbloquear();
			}
		}

	}

	private void desbloquearAlternativoLink() {
		for (Camino c : this.alternativoLink) {
			c.desbloquearCanales();
		}
	}

	public void randomizar() {
		liberarRecursos();

		if (primario == null)
			return;
		Camino original = primario;

		/*
		 * Se busca un subcamino de distancia entre 1 y el 40% de longitud del
		 * camino original (4 saltos en promedio). El subcamino tendra una
		 * distancia aleatoria (subcaminoDistancia). Y el nodo origen tambien
		 * sera aleatorio.
		 * 
		 * El camino nuevo tiene tres partes : Parte A : Origen - Medio1 Parte B
		 * : Medio1 - Medio2 (Subcamino nuevo donde no se utilizan los nodos
		 * intermedios del camino original) Parte C : Medio2 - Fin
		 * 
		 * El nuevo subcamino nuevo se hallar� bloqueando los canales originales
		 * del sub camino, y buscando otro camino optimo. Luego se desbloquearan
		 * los canales originales del sub camino.
		 */
		double cantSaltos = original.getSaltos().size();
		int subCaminoDistancia = 1 + (int) (Math.random() * cantSaltos * 0.85);
		int nodoIndex = (int) (Math.random() * (cantSaltos - (double) subCaminoDistancia));

		Iterator<Salto> iterSaltos = original.getSaltos().iterator();

		/*
		 * Se crea primeramente la parte A del camino nuevo
		 */
		Camino caminoMutante = new Camino(original.getOrigen());
		Nodo actual = original.getOrigen();
		actual.bloquear();
		while (nodoIndex > 0) {
			Salto salto = iterSaltos.next();
			caminoMutante.addSalto(new Salto(salto.getSecuencia(), salto
					.getCanal()));
			actual = salto.getCanal().getOtroExtremo(actual);
			actual.bloquear();
			nodoIndex--;
		}
		Nodo medio1 = actual;
		Camino subCaminoViejo = new Camino(medio1);

		/*
		 * Se bloquean los canales intermedios entre Medio1 y Medio2
		 */
		int secuencia = 1;
		while (subCaminoDistancia > 0) {
			CanalOptico canal = iterSaltos.next().getCanal();
			actual = canal.getOtroExtremo(actual);

			subCaminoDistancia--;

			canal.bloquear();
			subCaminoViejo.addSalto(new Salto(secuencia++, canal));
		}
		Nodo medio2 = actual;
		medio2.desbloquear();

		Camino parteC = new Camino(actual);
		secuencia = 1;
		while (iterSaltos.hasNext()) {
			CanalOptico canal = iterSaltos.next().getCanal();
			actual = canal.getOtroExtremo(actual);
			actual.bloquear();
			parteC.addSalto(new Salto(secuencia++, canal));
		}
		parteC.getOrigen().desbloquear();

		/* Se calcula la parte B del camino nuevo */
		Camino subCaminoNuevo = medio1.dijkstra(medio2,
				solicitud.getExclusividadPrimario());
		subCaminoViejo.desbloquearCanales();
		primario.desbloquearNodos();
		/*
		 * Si no se puede encontrar un camino alternativo sin utilizar los
		 * canales originales, se ignora la mutacion.
		 */
		if (subCaminoNuevo == null) {
			/*
			 * Se vuelve a fijar los recursos de los caminos primario y
			 * alternativo originales.
			 */

			fijarRecursos();

			return;
		}

		caminoMutante.anexar(subCaminoNuevo);
		caminoMutante.anexar(parteC);

		primario = caminoMutante;
		setPrimario();

		if (solicitud.getNivel() != Nivel.Bronce) {
			buscarAlternativo();
			setAlternativo();
		} else {
			alternativo = null;
		}
	}

	public void random() {
		Nodo origen = solicitud.getOrigen();
		Nodo destino = solicitud.getDestino();
		Exclusividad e = solicitud.getExclusividadPrimario();
		
		if (origen != null && destino != null){
			primario = origen.dijkstra(destino, e);
			setPrimario();
		}

		if (solicitud.getNivel() != Nivel.Bronce) {
			buscarAlternativo();
			setAlternativo();
		} else {
			alternativo = null;
			alternativoLink = null;
		}

		this.randomizar();
		this.limpiar();
	}

	public void extremos() {
		Nodo origen = solicitud.getOrigen();
		Nodo destino = solicitud.getDestino();
		Exclusividad e = solicitud.getExclusividadPrimario();

		//if (origen != null && destino != null){
			primario = origen.dijkstra(destino, e);
			setPrimario();
		//}

		if (solicitud.getNivel() != Nivel.Bronce) {
			buscarAlternativo();
			setAlternativo();
		} else {
			alternativo = null;
			alternativoLink = null;
		}

		this.limpiar();
	}

	/**
	 * Procedimiento para poner null o el primario o el alternativo al no
	 * encontrar alguno.
	 */
	public void limpiar() {
		// Limpieza del Alternativo.
		if (this.primario == null && this.alternativo != null) {
			this.alternativo = null;
			this.alternativoLink = null;
		}
		// Limpieza del Primario
		if (this.solicitud.getEsquema() != EsquemaRestauracion.Link) {
			if (this.solicitud.getNivel() != Nivel.Bronce)
				if (this.alternativo == null && this.primario != null) {
					this.primario = null;
				}
		} else { // Link
			if (this.alternativoLink == null)
				this.primario = null;
		}
	}

	/**
	 * Función que controla si tiene o no Alternativo, para el caso de oro. Si
	 * (no es oro) y si (es oro y tiene alternativo) retorna true.
	 * 
	 * @return
	 */
	public boolean oroTieneAlternativo() {
		boolean respuesta = true;

		if (this.solicitud.getNivel() == Nivel.Oro)
			if (this.alternativo == null)
				respuesta = false;

		if (this.solicitud.getEsquema() == EsquemaRestauracion.Link)
			if (this.alternativoLink == null) {
				respuesta = false;
			} else
				respuesta = true;
		return respuesta;
	}

	/**
	 * Función que controla si tiene o no Alternativo, para el caso de Plata. Si
	 * (no es Plata) y si (es Plata y tiene alternativo) retorna true.
	 * 
	 * @return
	 */
	public boolean plataTieneAlternativo() {
		boolean respuesta = true;
		if (this.solicitud.getNivel().esPlata()) {
			if (this.alternativo == null) {
				respuesta = false;
			}
		}

		if (this.solicitud.getEsquema() == EsquemaRestauracion.Link) {
			if (this.alternativoLink == null) {
				respuesta = false;
			} else
				respuesta = true;
		}
		return respuesta;
	}

	public String imprimirSolapados() {
		String retorno = "SOLAPADOS: \n";
		/*
		 * for (Servicio s : this.serviciosSolapados) { retorno += s.toString()
		 * + ", Cant.: "; retorno += "; \n"; }
		 */
		return retorno;
	}

	/**
	 * Función que controla si tiene o no Primario y/o Alternativo. Si el
	 * Primario o el Alternativo no es nulo entonces se retorno false.
	 * 
	 * @return
	 */
	public boolean esValido() {
		boolean respuesta = true;

		if (this.primario == null)
			respuesta = false;

		if (this.solicitud.getEsquema() != EsquemaRestauracion.Link) {
			if (this.solicitud.getNivel() != Nivel.Bronce) {
				if (this.alternativo == null)
					respuesta = false;
			}
		} else {// es link
			if (this.solicitud.getNivel() != Nivel.Bronce) {
				if (this.alternativoLink == null)
					respuesta = false;
			}
			if (respuesta && this.primario.getSaltos() == null){
				respuesta = false;
			}
		}
		return respuesta;
	}

	/**
	 * Funcion para obtener un servicio sin respuesta (Servicio Nulo)
	 */
	public Servicio servicioNulo() {
		Servicio newServicio = new Servicio(this.getSolicitud());
		newServicio.setPrimario(null);
		newServicio.setAlternativo(null);
		return newServicio;
	}

	@Override
	public String toString() {
		String retorno = "" + solicitud.toString();
		String camino1 = null;
		String camino2 = null;
		String recuperacion = "% Recuperacion:" + this.getpRecuperacion();
		if (primario != null) {
			camino1 = "Primario: [" + primario.toString();
			camino1 += "];";
		} else
			camino1 = "Primario: [Vacio];";

		if (this.solicitud.getEsquema() != EsquemaRestauracion.Link) {
			if (alternativo != null)
				camino2 = "Secundario: [" + alternativo.toString() + "]";
			else
				camino2 = "Secundario: [Vacio] ";

		} else if (this.solicitud.getEsquema() == EsquemaRestauracion.Link) {
			if (alternativoLink != null) {
				camino2 = "Secundario: [";
				for (Camino alternativo : alternativoLink) {
					camino2 += "<" + alternativo.getOrigen();
					camino2 += "," + alternativo.getDestino() + ">";
					camino2 += "{" + alternativo.toString() + "}; ";
				}
				camino2 += "]";
			} else {
				camino2 = "Secundario: [Vacio];";
			}
		}

		if (primario == null && alternativo == null) {
			retorno = "{" + retorno + " SIN SERVICIO }";
		} else {
			retorno = "{" + retorno + " " + recuperacion + " " + camino1 + " "
					+ camino2 + "}";
		}
		return retorno;
	}

	@Override
	public int compareTo(Servicio arg0) {
		return solicitud.compareTo(arg0.solicitud);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((alternativo == null) ? 0 : alternativo.hashCode());
		result = prime * result
				+ ((primario == null) ? 0 : primario.hashCode());
		result = prime * result
				+ ((solicitud == null) ? 0 : solicitud.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Servicio other = (Servicio) obj;
		if (alternativo == null) {
			if (other.alternativo != null)
				return false;
		} else if (!alternativo.equals(other.alternativo))
			return false;
		if (primario == null) {
			if (other.primario != null)
				return false;
		} else if (!primario.equals(other.primario))
			return false;
		if (solicitud == null) {
			if (other.solicitud != null)
				return false;
		} else if (!solicitud.equals(other.solicitud))
			return false;
		return true;
	}

}
