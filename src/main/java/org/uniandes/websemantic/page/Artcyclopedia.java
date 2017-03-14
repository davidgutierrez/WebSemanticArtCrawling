package org.uniandes.websemantic.page;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.uniandes.websemantic.hibernate.HibernateSession;
import org.uniandes.websemantic.object.Artist;

public class Artcyclopedia {

	private static String pagina = "http://www.artcyclopedia.com";
	//Paginas ya vistitadas
	private static Set<String> paginas;

	public static void crawling(){

		paginas = paginasYavisitadas();

		Document doc;
		try {
			String url="/alphabetic.html";
			// need http protocol
			doc = Jsoup.connect(pagina+url).get();
			paginas.add(url);
			// get all links
			Elements links = doc.select("a[href*=/artists/]");
			for (Element link : links) {
				url = link.attr("href");
				String titulo = link.text();
				if(!paginas.contains(url)){
					subPage(url,titulo);
				}
			}
			HibernateSession.getInstance().closeSession();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static Set<String> paginasYavisitadas() {
		paginas = new HashSet<String>();
		List<?> listArtist = HibernateSession.getInstance().createCriteria(Artist.class);			
		for (Object artist : listArtist) {
			paginas.add(((Artist) artist).getUrl().replace("http://www.artcyclopedia.com",""));
		}
		return paginas;
	}

	private static void subPage(String url, String nombre) throws IOException {
		System.out.println("URL:" + url);
		Document doc = Jsoup.connect(url).get(); 
		paginas.add(url);
		String titulo = doc.title();
		System.out.println("Titulo: "+titulo);
		if(titulo.startsWith("Artcyclopedia: Artist Names Beginning ")){
			subPageAlpha(doc);
		}
		else if (titulo.endsWith(" Online")){
			System.out.println("\n Artista "+url);

			Artist artist = pageArtist(doc,nombre,url);
			HibernateSession.getInstance().save(artist);
			//pageArtworks(doc,artist);

		}
	}





	private static Artist pageArtist(Document doc,String titulo,String uri) throws IOException {
		Artist artista = new Artist();
		String name = getArtistName(doc);

		artista.setName(name);
		artista.setMovimiento(getMovimiento(doc));
		artista.setNacionalidad(getNationality(doc));
		artista.setAnioNacimiento(getAnioNacimiento(doc));
		artista.setAnioMuerte(getAnioMuerte(doc)); 
		artista.setTipodearte(getTipodeArte(doc));
		artista.setUrl(uri); 
		System.err.println(artista);

		return artista;
	}

	/**#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table:nth-child(10) > tbody > tr:nth-child(1) > td:nth-child(2) > a
	 * Obtiene el a�o de muerte del artista
	 * @param doc
	 * @return
	 */
	private static String getAnioMuerte(Document doc) {

		Element artist = doc.select("#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > font > b").first();

		String [] infoArtista;
		infoArtista = artist.text().split(",");
		String anioMuerte = ""; 
		if (infoArtista.length < 2){
			System.err.println("error");
		}else{
			if (infoArtista[1].contains("-"))
			{
				anioMuerte = infoArtista[1].trim().split("-")[1].replace("]","").replace("ca.", "");
			}else if (!infoArtista[1].contains("born") && infoArtista[1].contains("died"))
			{
				anioMuerte = infoArtista[1].trim().split(" ")[1].replace("]", "").replace("ca.", "");
			}else{
				anioMuerte = "";
			}

		}


		return anioMuerte;
	}

	/**
	 * Obtiene el a�o de nacimiento del artista
	 * @param doc
	 * @return
	 */
	private static String getAnioNacimiento(Document doc) {
		Element artist = doc.select("#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > font > b").first();
		String [] infoArtista;
		infoArtista = artist.text().split(",");
		String anioNacimiento = ""; 
		if (infoArtista.length < 2){
			System.err.println("error");
		}else{
			if (infoArtista[1].contains("born in") && !infoArtista[1].contains("died"))
			{
				anioNacimiento = infoArtista[1].trim().split(" ")[2].replace("]", "");
			}else if (!infoArtista[1].contains("in") && infoArtista[1].contains("born") && !infoArtista[1].contains("died"))
			{
				anioNacimiento = infoArtista[1].trim().split(" ")[1].replace("active","").replace("ca.","").replace("]", "");
			}
			else if(!infoArtista[1].contains("died") && infoArtista[1].contains("-")){
				String [] nacimiento = infoArtista[1].trim().split("-");
				anioNacimiento = nacimiento[0].replace("known","").replace("active","").replace("ca.","").trim();
			}
			else if(infoArtista[1].contains("active") && infoArtista[1].contains("-")){
				anioNacimiento = infoArtista[1].replace("active","").trim().split("-")[0];

			}
		}               

		return anioNacimiento;
	}


	private static String getNationality(Document doc) {

		//String nationality = doc.select("#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > font > b").first().select("a[href]").text();
		Element eNationality = doc.select("#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table:nth-child(10) > tbody > tr > td > a:nth-child(1)").first();
		String nationality = "";

		if(eNationality != null){
			System.out.println(eNationality.text());
			String link = eNationality.attr("href");
			if(link.contains("nationalities")){
				nationality = eNationality.text().replace(" artists", "");
			}
		}
		else{
			System.err.print("error");
		}

		return nationality;
	}

	private static String getMovimiento(Document doc){
		Element movimiento = doc.select("#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > font > b > a").first();
		String name = "";
		if(movimiento != null){
			name = movimiento.text();
			if(name.isEmpty()){

				System.err.println("error");
			}   
		}

		return name;
	}

	/**
	 * Obtiene el nombre del artista
	 * @param doc
	 * @return
	 */
	private static String getArtistName(Document doc) {
		Element eName = doc.select("h1").first();
		String name = "";
		if (eName !=null){

			name = eName.text();

			if(name.isEmpty()){
				name = doc.title().replace(" Online","");
			}
			if(name.isEmpty())
				System.err.println("error");
		}       
		else{
			System.out.println("error");
		}

		return name;
	}

	private static void subPageAlpha(Document doc) throws IOException {
		//	Set<Artist> artistList = new HashSet<Artist>();
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String url = link.attr("href");
			if(!paginas.contains(url) && url.contains("html")){
				subPage(pagina+"/artists/"+url,link.text());
				paginas.add(url);
			}
		}
	}

	private static String getTipodeArte(Document doc) {

		Element eTipo = doc.select("#mainpage > table > tbody > tr > td:nth-child(2) > center > table:nth-child(2) > tbody > tr > td:nth-child(3) > table > tbody > tr > td > table:nth-child(10) > tbody > tr:nth-child(1) > td:nth-child(2) > a").first();

		String nationality = "";

		if(eTipo != null){
			System.out.println(eTipo.text());
			String link = eTipo.attr("href");
			if(link.contains("subject")){
				nationality = eTipo.text();
			}
		}
		else{
			System.err.print("error");
		}

		return nationality;


	}



}