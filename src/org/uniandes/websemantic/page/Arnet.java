package org.uniandes.websemantic.page;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.uniandes.websemantic.file.ArtistFile;
import org.uniandes.websemantic.object.Artist;

public class Arnet {

	private static String pagina = "http://www.artnet.com";
	//Paginas ya vistitadas
	private static Set<String> paginas;
	public static void crawling(){
		Set<Artist> artistList = new HashSet<Artist>();

		paginas = new HashSet<String>();
		Document doc;
		try {
			String url="/artists/";
			// need http protocol
			doc = Jsoup.connect(pagina+url).get();
			paginas.add(url);
			// get all links
			Elements links = doc.select("a[href^="+url+"]");
			for (Element link : links) {
				url = link.attr("href");
				String titulo = link.text();
				if(!paginas.contains(url)){
					artistList = subPage(url,titulo);
					new ArtistFile("artnet"+titulo,artistList);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static Set<Artist> subPage(String url, String nombre) throws IOException {
		Document doc = Jsoup.connect(pagina+fixUrl(url)).get(); 
		Set<Artist> artistList = new HashSet<Artist>();
		paginas.add(url);
		String titulo = doc.title();
		if(titulo.startsWith("Browse Artists Starting with ")){
			new ArtistFile("artnet"+nombre,subPageAlpha(doc));
		}
		else if(titulo.startsWith("Top 300 Artists on artnet - Most Popular Artists"))
			new ArtistFile("artnet300",subPageAlpha(doc));
		else if(titulo.equals("Browse Artists on artnet - Modern and Contemporary Artists")){
			new ArtistFile("artnet"+nombre,subPageAlpha(doc));
		}else{
			artistList.add(pageArtist(doc,nombre));
		}
		return artistList;
	}

	/**
	 * corrige errores de tildes
	 * @param url
	 * @return
	 */
	private static String fixUrl(String url) {
//		if(url.contains("uecker")) //ä
//			System.err.println("reivsar");
		if(url.contains("%c3%a0"))
			url = url.replace("%c3%a0","à");

		if(url.contains("%c3%a3"))
			url = url.replace("%c3%a3","ã");
		
		
		if(url.contains("%c3%a4"))
			url = url.replace("%c3%a4","ä");
		if(url.contains("%c3%af"))
			url = url.replace("%c3%af","ï");
		if(url.contains("%c3%b6"))
			url = url.replace("%c3%b6","ö");
		if(url.contains("%c3%bc"))
			url = url.replace("%c3%bc","ü");

		if(url.contains("%c3%a9"))
			url = url.replace("%c3%a9","é");
		if(url.contains("%c3%ad"))
			url = url.replace("%c3%ad","í");
		if(url.contains("%c3%b3"))
			url = url.replace("%c3%b3","ó");
		return url;
	}


	private static Artist pageArtist(Document doc,String titulo) throws IOException {
		Artist artista = new Artist();
		String name = getArtistName(doc);
		artista.setName(name);
		artista.setNacionalidad(getNationality(doc));
		artista.setAnioNacimiento(getAnioNacimiento(doc));
		artista.setAnioMuerte(getAnioMuerte(doc)); 
		artista.setUrl(doc.baseUri()); 
		System.out.println(artista);
		return artista;
	}

	/**
	 * Obtiene el año de muerte del artista
	 * @param doc
	 * @return
	 */
	private static String getAnioMuerte(Document doc) {
		Elements artist = doc.select("h1.text-center > span");
		artist = artist.select("span.detail > span > time");
		String anioMuerte = doc.select("span[itemprop=deathDate]").text();
		if(anioMuerte.isEmpty()){
			String heading = doc.select("div.headline > h2.detail").text();
			if(heading.contains(","))
				anioMuerte =heading.split(",")[1].replace(")", "").trim();
			if(anioMuerte.contains("–")){
				anioMuerte = anioMuerte.split("–")[1].trim();
			}else{
				anioMuerte="";
			}

		}
		//		if(anioMuerte.isEmpty()){
		//			System.err.println("no muerto?");
		//		}
		return anioMuerte;	}

	/**
	 * Obtiene el año de nacimiento del artista
	 * @param doc
	 * @return
	 */
	private static String getAnioNacimiento(Document doc) {
		Elements artist = doc.select("h1.text-center > span");
		artist = artist.select("span.detail > span");
		Elements time = artist.select("> time");
		String anioNacimiento;
		anioNacimiento = time.select("time[itemprop=birthDate]").text();
		if(anioNacimiento.isEmpty()){
			String heading = doc.select("div.headline > h2.detail").text();
			if(heading.contains(","))
				anioNacimiento =heading.split(",")[1].replace(")", "");
			if(anioNacimiento.contains(" born "))
				anioNacimiento = anioNacimiento.replace(" born ","");
			else if(anioNacimiento.contains("–")){
				anioNacimiento = anioNacimiento.split("–")[0].trim();
			}
		}
		if(anioNacimiento.isEmpty()){
			System.err.println("error");
		}
		return anioNacimiento;
	}


	private static String getNationality(Document doc) {
		String nationality = doc.select("span[itemprop=nationality]").text();
		if(nationality.isEmpty()){
			Elements heading = doc.select("div.headline > h2.detail");
			nationality =heading.text().split(",")[0].replace("(", "");			
		}
		if(nationality.isEmpty()){
			System.err.println("error");
		}
		return nationality;
	}

	/**
	 * Obtiene el nombre del artista
	 * @param doc
	 * @return
	 */
	private static String getArtistName(Document doc) {
		String name = doc.select("span[itemprop=name]").text();
		if(name.isEmpty()){
			Elements heading = doc.select("div.headline > h1.title");
			name =heading.text();			
		}
		if(name.isEmpty())
			System.err.println("error");
		return name;
	}

	private static Set<Artist> subPageAlpha(Document doc) throws IOException {
		Set<Artist> artistList = new HashSet<Artist>();
		Elements links = doc.select("a[href^=/artists/]");
		for (Element link : links) {
			String url = link.attr("href");
			if(!paginas.contains(url)){
				artistList.addAll(subPage(url,link.text()));
				paginas.add(url);
			}
		}
		return artistList;
	}
}
