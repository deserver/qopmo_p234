package qopmo.wdm;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import qopmo.wdm.qop.Exclusividad;
import qopmo.wdm.qop.Servicio;

@Entity
public class Salto implements Comparable<Salto> {

	@Transient
	private CanalOptico canal;

	@ManyToOne(cascade = CascadeType.ALL)
	private Enlace enlace;

	private int secuencia;

	@Id
	@GeneratedValue
	private long id;

	public Salto() {
	}

	public Salto(int secuencia, CanalOptico c) {
		this.secuencia = secuencia;
		this.canal = c;
	}
	public Salto (Salto s){
		this.secuencia = s.getSecuencia();
		this.canal = s.getCanal();
		this.enlace = s.getEnlace();
	}

	/*
	 * GETTERS Y SETTERS
	 */
	public int getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CanalOptico getCanal() {
		return canal;
	}

	public void setCanal(CanalOptico c) {
		this.canal = c;
		this.enlace = null;
	}

	public Enlace getEnlace() {
		return enlace;
	}

	public void setEnlace(Enlace enlace) {
		this.enlace = enlace;
	}

	/*
	 * FUNCIONES AUXILIARES
	 */

	/**
	 * Función para obtener y bloquear un enlace específico del canal
	 * relacionado.
	 * 
	 * @param ldO
	 * @return
	 */
	public int setEnlace(int ldO) {
		if (ldO < 0)
			this.enlace = canal.getEnlaceLibre(Exclusividad.Exclusivo);
		else
			this.enlace = canal.getEnlaceLibre(Exclusividad.Exclusivo, ldO);

		// En el caso que no existan mas recursos disponibles el enlace es null.
		int retorno = -5;
		if (this.enlace != null) {
			this.enlace.bloquear();
			retorno = this.enlace.getLongitudDeOnda();
		}

		return retorno;
	}

	/**
	 * Función que desbloquea el enlace de este Salto.
	 */
	public void desenlazar() {
		if (this.enlace != null) {
			this.enlace.desbloquear();
		}
	}

	/**
	 * Función para obtener y reservar un enlace específico del canal
	 * relacionado.
	 * 
	 * @param ldO
	 * @param servicio
	 * @param exclusividad
	 * @return
	 */
	public int setReserva(int ldO, Servicio servicio, Exclusividad exclusividad) {
		if (ldO < 0)
			this.enlace = canal.getEnlaceLibre(exclusividad);
		else
			this.enlace = canal.getEnlaceLibre(exclusividad, ldO);

		// En el caso que no existan mas recursos disponibles el enlace es null.
		int retorno = -5;
		if (this.enlace != null) {
			this.enlace.reservar(servicio);
			retorno = this.enlace.getLongitudDeOnda();
		}
		return retorno;
	}

	/**
	 * Función para comparar el orden de 2 Saltos. Se controla que este salto
	 * sea menor, igual o mayor que el Salto b recibido como parametro.
	 */
	@Override
	public int compareTo(Salto b) {
		return this.secuencia - b.secuencia;
	}

	/**
	 * Se utiliza de hashCode la Secuencia.
	 * mrodas 18/07/2013 Se realizó el cambio
	 */
	@Override
	public int hashCode() {
		int s = this.secuencia;
		int c = this.canal.hashCode();
		return (s + c);
	}

	/**
	 * Se tiene un toString personalizado. Se muestra: id, secuencia, canal y
	 * enlace.
	 */
	@Override
	public String toString() {
		return "Salto [id=" + id + ", secuencia=" + secuencia + ", canal="
				+ canal + ", enlace=" + enlace + "]";
	}
}
