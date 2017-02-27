package org.uniandes.websemantic.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.uniandes.websemantic.object.Artist;

public class ArtistFile {

	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";

	CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);		// TODO Auto-generated method stub
	private static final Object [] FILE_HEADER = {"nombre artista","año nacimiento","año muerte",
			"movimiento","pais nacionalidad","pais muerte","tipo de arte","influenciado por","influencio a","url"};

	CSVPrinter csvFilePrinter = null;

	FileWriter fileWriter = null;		

	public ArtistFile(String pageName){		
		init(pageName);
	}

	public ArtistFile(String pageName, Set<Artist> artistList) {
		try {
			init(pageName);
			for (Artist artist : artistList) {
				escribirArtista(artist);
			}
			closeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inicializa las variables
	 * @param pageName
	 */
	private void init(String pageName) {
		//Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		try {
			//initialize FileWriter object
			fileWriter = new FileWriter("artist"+pageName+".csv");

			//initialize CSVPrinter object 
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			//Create CSV file header
			csvFilePrinter.printRecord(FILE_HEADER);

			System.out.println("CSV file was created successfully "+pageName+"!!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
			closeFile();
		}
	}


	/**
	 * Escribe en el archivo de artistas la informacion del artista
	 * @param artist datos del artista
	 * @throws IOException en caso que existan problemas con el lector del artista
	 */
	public void escribirArtista(Artist artist) throws IOException{
		List<String> artistDataRecord = new ArrayList<String>();
		artistDataRecord.add(artist.getName());
		artistDataRecord.add(artist.getAnioNacimiento());
		artistDataRecord.add(artist.getAnioMuerte());
		artistDataRecord.add(artist.getMovimiento());
		artistDataRecord.add(artist.getNacionalidad());
		artistDataRecord.add(artist.getPaismuerte());
		artistDataRecord.add(artist.getTipodearte());
		artistDataRecord.add(artist.getInfluenciadopor());
		artistDataRecord.add(artist.getInfluencioa());
		artistDataRecord.add(artist.getUrl());
		csvFilePrinter.printRecord(artistDataRecord);
	}

	public void closeFile(){
		try {
			fileWriter.flush();
			fileWriter.close();
			csvFilePrinter.close();
		} catch (IOException e) {
			System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
			e.printStackTrace();
		}

	}
}
