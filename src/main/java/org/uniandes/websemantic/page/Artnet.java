package org.uniandes.websemantic.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.uniandes.websemantic.file.ArtistFile;
import org.uniandes.websemantic.hibernate.HibernateSession;
import org.uniandes.websemantic.object.Artist;
import org.uniandes.websemantic.object.Artwork;
import org.uniandes.websemantic.object.NoArtist;

public class Artnet {

	private static String pagina = "http://www.artnet.com";
	//Paginas ya vistitadas
	private static Set<String> paginas;

	public static void crawling(){
		paginas = paginasYavisitadas();
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
					subPage(url,titulo);
				}
			}
			HibernateSession.getInstance().closeSession();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createFile();
	}


	private static void createFile() {
		Set<Artwork> artworks = new HashSet<>();
		List<?> listArtist = HibernateSession.getInstance().createCriteria(Artwork.class);			
		for (Object artwork : listArtist) {
			artworks.add(((Artwork) artwork));
		}
			new ArtistFile("artnet",artworks);
	}


	private static Set<String> paginasYavisitadas() {
		paginas = new HashSet<String>();
		List<?> listArtist = HibernateSession.getInstance().createCriteria(Artist.class);			
		for (Object artist : listArtist) {
			paginas.add(((Artist) artist).getUrl().replace("http://www.artnet.com",""));
		}
		listArtist = HibernateSession.getInstance().createCriteria(NoArtist.class);			
		for (Object artist : listArtist) {
			paginas.add(((NoArtist) artist).getUrl().replace("http://www.artnet.com",""));
		}
		return paginas;
	}

	private static void subPage(String url, String nombre) throws IOException {
		url = fixUrl(url);
		if(paginas.contains(url))
			return;
		String uri = pagina+url;
		try {
			Document doc = Jsoup.connect(uri).get(); 
			paginas.add(url);
			String titulo = doc.title();
			if(titulo.startsWith("Browse Artists Starting with ")){
				subPageAlpha(doc);
			}
			else if(titulo.startsWith("Top 300 Artists on artnet - Most Popular Artists"))
				subPageAlpha(doc);
			else if(titulo.equals("Browse Artists on artnet - Modern and Contemporary Artists")){
				subPageAlpha(doc);
			}else{
				Artist artist = pageArtist(doc,nombre,uri);
				save(artist,pageArtworks(doc,artist));
			}

		} catch (Exception e) {
			System.err.println(e.getMessage()+" url: "+url);
		}
		return;
	}

	/**
	 * 
	 * @param artist
	 * @param pageArtworks
	 */
	private static void save(Artist artist, List<Artwork> pageArtworks) {
		if(pageArtworks.isEmpty()){
			NoArtist noArtist = new NoArtist(artist.getUrl());
			HibernateSession.getInstance().save(noArtist);
			System.err.println("NoArtist "+artist.getName());
			return;
		}
		HibernateSession.getInstance().save(artist);
		for (Artwork artwork : pageArtworks) {
			artwork.setArtist(artist);
			HibernateSession.getInstance().save(artwork);			
		}
		System.out.println(artist.getName());
		System.out.println("artworks: "+pageArtworks.size());

	}//saveArtistData


	/**
	 * 
	 * @param doc
	 * @param artist
	 * @return
	 */
	private static List<Artwork> pageArtworks(Document doc, Artist artist) {
		List<Artwork> returnList = new ArrayList<>();
		Elements artworks = doc.select("div.artwork");
		if(!artworks.isEmpty()){
			for (Element arts : artworks) {
				Elements art = arts.select("p");
				Artwork artwork = new Artwork();
				artwork.setArtist(artist);
				String nameArt = art.get(1).text();
				artwork.setNombre(nameArt);
				artwork.setPrecio(art.get(3).text());
				artwork.setMuseo(art.get(2).text());
				Elements artUrl = arts.select("img");
				artwork.setUrlImg(artUrl.attr("src"));
				returnList.add(artwork);
			}	
		}else{
			artworks = doc.select("div.artworkBox.itemBox ");
			for (Element arts : artworks) {
				Elements art = arts.select("p");
				Artwork artwork = new Artwork();
				artwork.setArtist(artist);
				String nameArt = art.get(0).text();
				artwork.setNombre(nameArt);
				Elements artUrl = arts.select("img");
				artwork.setUrlImg(artUrl.attr("src"));
				returnList.add(artwork);
			}	

		}
		if(doc.baseUri().contains("biography"))
			return returnList;
		if(doc.body().text().contains("There are no works by "+artist.getName()+" currently available."))
			return returnList;
		return returnList;

	}


	/**
	 * corrige errores de tildes
	 * @param url
	 * @return
	 */
	private static String fixUrl(String url) {
		
		if(url.contains("%c2%a0"))
			url = url.replace("%c2%a0"," ");
		if(url.contains("%c2%a4"))
			url = url.replace("%c2%a4","¤");
		if(url.contains("%c2%a8"))
			url = url.replace("%c2%a8","¨");
		if(url.contains("%c2%ae"))
			url = url.replace("%c2%ae","®");
		if(url.contains("%c2%b6"))
			url = url.replace("%c2%b6","¶");
		if(url.contains("%c2%b7"))
			url = url.replace("%c2%b7","·");

		if(url.contains("%c3%97"))
			url = url.replace("%c3%97","×");
		if(url.contains("%c3%9f"))
			url = url.replace("%c3%9f","ß");
		if(url.contains("%c3%a0"))
			url = url.replace("%c3%a0","à");
		if(url.contains("%c3%a1"))
			url = url.replace("%c3%a1","á");
		if(url.contains("%c3%a2"))
			url = url.replace("%c3%a2","â");
		if(url.contains("%c3%a3"))
			url = url.replace("%c3%a3","ã");
		if(url.contains("%c3%a4"))
			url = url.replace("%c3%a4","ä");
		if(url.contains("%c3%a5"))
			url = url.replace("%c3%a5","å");
		if(url.contains("%c3%a6"))
			url = url.replace("%c3%a6","æ");
		if(url.contains("%c3%a7"))
			url = url.replace("%c3%a7","ç");
		if(url.contains("%c3%a8"))
			url = url.replace("%c3%a8","è");
		if(url.contains("%c3%a9"))
			url = url.replace("%c3%a9","é");
		if(url.contains("%c3%aa"))
			url = url.replace("%c3%aa","ê");
		if(url.contains("%c3%ab"))
			url = url.replace("%c3%ab","ë");
		if(url.contains("%c3%ac"))
			url = url.replace("%c3%ac","ì");
		if(url.contains("%c3%ad"))
			url = url.replace("%c3%ad","í");
		if(url.contains("%c3%af"))
			url = url.replace("%c3%af","ï");
		if(url.contains("%c3%ae"))
			url = url.replace("%c3%ae","î");
		if(url.contains("%c3%b0"))
			url = url.replace("%c3%b0","ð");
		if(url.contains("%c3%b1"))
			url = url.replace("%c3%b1","ñ");
		if(url.contains("%c3%b2"))
			url = url.replace("%c3%b2","ò");
		if(url.contains("%c3%b3"))
			url = url.replace("%c3%b3","ó");
		if(url.contains("%c3%b4"))
			url = url.replace("%c3%b4","ô");
		if(url.contains("%c3%b5"))
			url = url.replace("%c3%b5","õ");
		if(url.contains("%c3%b6"))
			url = url.replace("%c3%b6","ö");
		if(url.contains("%c3%b8"))
			url = url.replace("%c3%b8","ø");
		if(url.contains("%c3%b9"))
			url = url.replace("%c3%b9","ù");
		if(url.contains("%c3%ba"))
			url = url.replace("%c3%ba","ú");
		if(url.contains("%c3%bc"))
			url = url.replace("%c3%bc","ü");
		if(url.contains("%c3%bd"))
			url = url.replace("%c3%bd","ý");
		if(url.contains("%c3%be"))
			url = url.replace("%c3%be","þ");
		if(url.contains("%c3%bf"))
			url = url.replace("%c3%bf","ÿ");


		if(url.contains("%c5%93"))
			url = url.replace("%c5%93","œ");
		if(url.contains("%c5%a1"))
			url = url.replace("%c5%a1","š");
		if(url.contains("%c5%be"))
			url = url.replace("%c5%be","ž");
		if(url.contains("%e2%84%a2"))
			url = url.replace("%e2%84%a2","™");

		if(url.contains("%c3%bb"))
			return url;
		if(url.contains("%09"))
			return url;
		else if(!url.contains("%e2%80%93") && !url.contains("%09%09%09") && url.contains("%")) //ä
			System.err.println("revisar ");

		return url;
	}


	private static Artist pageArtist(Document doc,String titulo,String uri) throws Exception {
		if(doc.body().text().contains("artnet is temporarily undergoing maintenance."))
			throw new Exception("pagina en mantenimiento. ");
		Artist artista = new Artist();
		String name = getArtistName(doc);
		artista.setName(name);
		artista.setNacionalidad(getNationality(doc));
		artista.setAnioNacimiento(getAnioNacimiento(doc));
		artista.setAnioMuerte(getAnioMuerte(doc)); 
		artista.setUrl(uri); 

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
			System.err.println("error: no nacido");
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
			System.err.println("error: sin nacionalidad");
		}
		return nationality;
	}

	/**
	 * Obtiene el nombre del artista
	 * @param doc
	 * @return
	 * @throws Exception 
	 */
	private static String getArtistName(Document doc) throws Exception {
		String name = doc.select("span[itemprop=name]").text();
		if(name.isEmpty()){
			Elements heading = doc.select("div.headline > h1.title");
			name =heading.text();			
		}
		if(name.isEmpty())
			throw new Exception("error: sin nombre: "+doc.baseUri());
		return name;
	}

	private static void subPageAlpha(Document doc) throws IOException {
		//	Set<Artist> artistList = new HashSet<Artist>();
		Elements links = doc.select("a[href^=/artists/]");
		for (Element link : links) {
			String url = link.attr("href");
			if(!paginas.contains(url)){
				subPage(url,link.text());
				paginas.add(url);
			}
		}
	}



}