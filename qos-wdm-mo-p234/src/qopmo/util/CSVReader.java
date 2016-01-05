package qopmo.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

	private static String DIR_SOLICITUDES = "solicitudes";
	private static String DIR_REDES = "redes";
	private static String DIR_HOME_LINUX = "/home/sergio/workspace/qos-wdm/datos/";
	private static String DIR_HOME_WINDOWS = "C:\\Users\\mrodas\\workspace\\qos-wdm\\datos\\";

	private static String csvSplitBy = ";";

	public List<List<Long>> leerSolicitudes(String nombre) {
		String archivo = "";
		String OS = System.getProperty("os.name").toLowerCase();
		FileReader lector = null;
		BufferedReader buffer = null;
		List<List<Long>> lista = new ArrayList<List<Long>>();
		;
		String line = "";

		if (OS.matches("windows.*"))
			archivo = DIR_HOME_WINDOWS + DIR_SOLICITUDES + "\\" + nombre;
		else
			archivo = DIR_HOME_LINUX + DIR_SOLICITUDES + "/" + nombre;

		try {
			lector = new FileReader(archivo);
			buffer = new BufferedReader(lector);
			line = buffer.readLine(); // cabecera
			line = buffer.readLine();

			while (line != null) {
				String[] valores = line.split(csvSplitBy);
				List<Long> valoresN = new ArrayList<Long>();
				valoresN.add(Long.valueOf(valores[0].trim()));
				// System.out.print("O:"+valoresN.get(0));
				valoresN.add(Long.valueOf(valores[1].trim()));
				// System.out.print("D:"+valoresN.get(1));
				valoresN.add(Long.valueOf(valores[2].trim()));
				// System.out.println("PR:"+valoresN.get(2));

				lista.add(new ArrayList<Long>(valoresN));
				line = buffer.readLine();
			}
			buffer.close();
			lector.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lista;
	}

	public List<List<Integer>> leerRed(String nombre) {
		String archivo = "";
		String OS = System.getProperty("os.name").toLowerCase();
		FileReader lector = null;
		BufferedReader buffer = null;
		List<List<Integer>> lista = new ArrayList<List<Integer>>();
		String line = "";
		List<Integer> cabecera = new ArrayList<Integer>();

		if (OS.matches("windows.*"))
			archivo = DIR_HOME_WINDOWS + DIR_REDES + "\\" + nombre;
		else
			archivo = DIR_HOME_LINUX + DIR_REDES + "/" + nombre;

		try {
			lector = new FileReader(archivo);
			buffer = new BufferedReader(lector);
			line = buffer.readLine(); // primeraCabecera
			line = buffer.readLine(); // NroNodos
			Integer nodos = Integer.valueOf(line);
			cabecera.add(nodos);
			line = buffer.readLine(); // NroEnlaces
			Integer enlaces = Integer.valueOf(line);
			cabecera.add(enlaces);
			lista.add(cabecera);
			line = buffer.readLine(); // segundaCabecera
			line = buffer.readLine();

			while (line != null) {
				String[] valores = line.split(csvSplitBy);
				List<Integer> valoresN = new ArrayList<Integer>();
				valoresN.add(Integer.valueOf(valores[0].trim()));
				valoresN.add(Integer.valueOf(valores[1].trim()));
				valoresN.add(Integer.valueOf(valores[2].trim()));
				valoresN.add(Integer.valueOf(valores[3].trim()));
				valoresN.add(Integer.valueOf(valores[4].trim()));
				lista.add(new ArrayList<Integer>(valoresN));
				// System.out.print(valoresN.get(0)+"-");
				// System.out.print(valoresN.get(1)+"-");
				// System.out.print(valoresN.get(2)+"-");
				// System.out.print(valoresN.get(3)+"-");
				// System.out.println(valoresN.get(4)+"-");
				line = buffer.readLine();
			}
			buffer.close();
			lector.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lista;
	}

}
