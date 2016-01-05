package qopmo.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {

	List<List<String>> valores;
	List<String> mejor;
	//private static String DIR_HOME_LINUX = "/home/sergio/workspace/qos-wdm/datos/";
	private static String DIR_HOME_LINUX = "/home/sergio/Documentos/Resultados-Tesis/prueba234/";
	private static String DIR_HOME_WINDOWS = "C:\\Users\\mrodas\\workspace\\qos-wdm\\datos\\";
	private static String DIR_CASOS = "solicitudes";

	public CSVWriter() {
		valores = new ArrayList<List<String>>();
	}

	public List<List<String>> getValores() {
		return valores;
	}

	public void setValores(List<List<String>> valores) {
		this.valores = valores;
	}

	public void addValor(List<String> valor) {
		this.valores.add(valor);
	}

	/**
	 * Función para Generar los valores resultantes de la evolución. Guarda el
	 * mejor de cada generación.
	 * 
	 * @param nombre
	 * @param cantidad
	 */
	public void generateCsvFile(String nombre, int cantidad) {
		if (nombre == null)
			nombre = "output2.csv";

		/*
		 * Se agregan los valores de las N generaciones de una corrida.
		 */
		String OS = System.getProperty("os.name").toLowerCase();
		String dir = "";
		if (OS.matches("windows.*"))
			dir = DIR_HOME_WINDOWS;
		else
			dir = DIR_HOME_LINUX;

		/*
		 * String csv = dir + nombre + ".csv"; try { FileWriter writer = new
		 * FileWriter(csv);
		 * 
		 * // Se agrega el titulo writer.append("Generación");
		 * writer.append(';'); writer.append("Costo"); writer.append(';');
		 * writer.append("Fallas Oro Primario"); writer.append(';');
		 * writer.append("Fallas Oro Alternativo"); writer.append('\n');
		 * 
		 * this.writeAll(writer, this.valores);
		 * 
		 * writer.flush(); writer.close(); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */

		/*
		 * Se agrega el mejor de las N generaciones.
		 */
		this.mejor = valores.get(valores.size() - 1);

		String mejores = dir;

		if (nombre.contains("Link")) {
			mejores += "Link_Bests_" + cantidad + ".csv";
			this.mejor.set(0, "Link_Best_" + this.mejor.get(0));
		} else if (nombre.contains("Segment")) {
			mejores += "Segment_Bests_" + cantidad + ".csv";
			this.mejor.set(0, "Segment_Best_" + this.mejor.get(0));
		} else if (nombre.contains("Path")) {
			mejores += "Path_Bests_" + cantidad + ".csv";
			this.mejor.set(0, "Path_Best_" + this.mejor.get(0));
		} else {
			mejores += "ERROR_Bests_10.csv";
			this.mejor.set(0, "");
		}

		try {
			FileWriter writer = new FileWriter(mejores, true);

			// Se agrega el titulo
			if (nombre.contains("1_")) {
				writer.append("Corrida");
				writer.append(';');
				writer.append("Costo");
				writer.append(';');
				writer.append("Diferencia de Niveles");
				writer.append(';');
				writer.append("Ganancia de Niveles");
				writer.append(';');
				writer.append("Fallas Oro");
				writer.append(';');
				writer.append("Fallas Plata");
				writer.append(';');
				writer.append("Fallas Bronce");
				writer.append('\n');
			}

			this.write(writer, this.mejor);

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Función para Generar los valores resultantes de la evolución. Guarda el
	 * mejor de cada generación.
	 * 
	 * @param nombre
	 * @param cantidad
	 */
	public void guardarMejores(String nombre, int indice) {

		/*
		 * Se agregan los valores de las N generaciones de una corrida.
		 */
		String OS = System.getProperty("os.name").toLowerCase();
		String dir = "";
		if (OS.matches("windows.*"))
			dir = DIR_HOME_WINDOWS;
		else
			dir = DIR_HOME_LINUX;

		/*
		 * Se agrega el mejor de las N generaciones.
		 */
		this.mejor = valores.get(valores.size() - 1);

		String mejores = dir;

		mejores += nombre;
		this.mejor.set(0, "Segment_Best_" + this.mejor.get(0));

		try {
			FileWriter writer = new FileWriter(mejores, true);

			// Se agrega el titulo
			if ( indice == 1) {
				writer.append("Corrida");
				writer.append(';');
				writer.append("Costo");
				writer.append(';');
				writer.append("Diferencia de Niveles");
				writer.append(';');
				writer.append("Ganancia de Niveles");
				writer.append(';');
				writer.append("Fallas Oro");
				writer.append(';');
				writer.append("Fallas Plata");
				writer.append(';');
				writer.append("Fallas Bronce");
				writer.append('\n');
			}

			this.write(writer, this.mejor);

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Función para generar los casos de prueba de la prueba 4 (última).
	 * 
	 * @param nombre
	 */
	public void generarCasoPrueba(String nombre) {
		String OS = System.getProperty("os.name").toLowerCase();
		String dir = "";
		if (OS.matches("windows.*"))
			dir = DIR_HOME_WINDOWS + DIR_CASOS + "\\";
		else
			dir = DIR_HOME_LINUX + DIR_CASOS + "/";
		String archivo = dir + nombre;

		try {
			FileWriter writer = new FileWriter(archivo, false);

			// Se agrega el titulo
			writer.append("Origen");
			writer.append(';');
			writer.append("Destino");
			writer.append(';');
			writer.append("Nivel");
			writer.append('\n');

			if (this.valores != null)
				writeAll(writer, this.valores);

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeAll(FileWriter writer, List<List<String>> valores)
			throws IOException {

		for (List<String> s : valores) {
			write(writer, s);
		}
	}

	private void write(FileWriter writer, List<String> list) throws IOException {
		int i = 1;
		for (String valor : list) {
			writer.append(valor);
			if (i < list.size())
				writer.append(';');
			else
				writer.append('\n');
			i++;
		}
	}
}
