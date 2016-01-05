package qopmo.wdm;

public class NodoDijkstra implements Comparable<NodoDijkstra> {
	private final Nodo nodo;
	private Camino camino;
	private int distancia = Integer.MAX_VALUE;

	public NodoDijkstra(Nodo nodo, int distancia) {
		this.nodo = nodo;
		this.distancia = distancia;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Nodo) {
			return ((Nodo) o).getLabel().equalsIgnoreCase(nodo.getLabel());
		}

		if (o instanceof NodoDijkstra) {
			return ((NodoDijkstra) o).nodo.getLabel().equalsIgnoreCase(
					nodo.getLabel());
		}

		return false;
	}

	@Override
	public int compareTo(NodoDijkstra arg0) {
		NodoDijkstra b = (NodoDijkstra) arg0;

		return this.distancia - b.distancia;
	}

	public int getDistancia() {
		return this.distancia;
	}

	public Nodo getNodo() {
		return nodo;
	}

	public Camino getCamino() {
		return camino;
	}

	public void setCamino(Camino camino) {
		this.camino = camino;
	}
}
