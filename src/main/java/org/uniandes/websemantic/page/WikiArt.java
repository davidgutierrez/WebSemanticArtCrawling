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
import org.uniandes.websemantic.object.Artwork;

public class WikiArt {

	private static String pagina = "http://www.wikiart.org";
	//Paginas ya vistitadas
	private static Set<String> paginas;
	
	public static void crawling(){
		Set<Artist> artistList = new HashSet<Artist>();
		paginas = paginasYavisitadas();
		Document doc;
		try {
			String url="/es/alphabet/";			
			// need http protocol
			doc = Jsoup.connect(pagina+url).get();
			paginas.add(url);
			// get all links
			paginas.add("https://www.wikiart.org/es/App/WikiAccount/Login?returnUrl=%2Fes%2Falphabet");
			Elements links = doc.select("a[href^="+url+"]");
			for (Element link : links) {
				url = link.attr("href");
				String titulo = link.text();
				if(!paginas.contains(url)){
					artistList = subPage(url,titulo);
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
			String urlArtista =((Artist) artist).getUrl().replace("https://www.wikiart.org/es/","");
			
			paginas.add(urlArtista);
		}
		return paginas;
	}

	private static Set<Artist> subPage(String url, String nombre) throws IOException {
		String uri = pagina+fixUrl(url);
		Document doc = Jsoup.connect(uri).get(); 
		Set<Artist> artistList = new HashSet<Artist>();
		paginas.add(url);
		
		Elements ListaArtista = doc.select("ul.artists-list > li");
		for (Element artist : ListaArtista) {
			Element artista = artist.select("a[href]").get(0);
			
			if(!paginas.contains(artista.attr("href").replace("/es/", ""))){
				try
				{
					Document paginaArtista = Jsoup.connect(pagina + artista.attr("href")).get();
					
					Artist artis = pageArtist(paginaArtista,nombre,uri);
					HibernateSession.getInstance().save(artis);
					pageArtworks(paginaArtista,artis);
				}catch (Exception e) {
				     System.out.println("Time Out en la obra");
				}
				finally
				{
//					System.err.println("Time Out en el artista");
				}
				
			}
			
		}
		
		return artistList;
	}

	private static void pageArtworks(Document doc, Artist artist) throws IOException {
		Elements artworks = doc.select("ul > li.loader");
		
		for (Element art : artworks) {
			Element arte = art.select("ul.title > li").first();
			
			Element obra = arte.select("a[href]").get(0);
			try
			{
				
				Document paginaArte = Jsoup.connect(pagina + obra.attr("href")).get();			
				scrapingArtWorks(paginaArte, artist);
				
			}catch (Exception e) {
			     System.out.println("Time Out en la obra");
			}
			finally
			{
//				System.err.println("");
			}
			
		}
	}
	
	
	/**
	 * Realiza el scraping de la obra de arte
	 * @param paginaArte
	 * @param artist 
	 */
	private static void scrapingArtWorks(Document paginaArte, Artist artist) {
		
		Artwork artwork = new Artwork();
		artwork.setArtist(artist);
		String nameArt = getNameArte(paginaArte);
		artwork.setNombre(nameArt);
		artwork.setPrecio(getPrecioArte(paginaArte));
		artwork.setMuseo(getMuseo(paginaArte));
		artwork.setTecnica(getTecnica(paginaArte));
		artwork.setTipo(getTipo(paginaArte));
		artwork.setEstilo(getEstilo(paginaArte));
		artwork.setGenero(getGenero(paginaArte));
		artwork.setAno(getAno(paginaArte));
		artwork.setUrl(paginaArte.baseUri());
		artwork.setUrlImg(getUrlImg(paginaArte));
		HibernateSession.getInstance().save(artwork);
		
	}


	private static String getUrlImg(Document paginaArte) {
		
		Elements urlImg = paginaArte.select("meta[property=og:image]");
		
		String urlStrig = urlImg.attr("content");
		if(urlImg.isEmpty()){
			System.err.println("error url Imagen");
		}
		
		return urlStrig;
	}


	private static String getUrl(Document paginaArte) {
		String url = paginaArte.select("a[href*=wikipedia.org]").text();
		
		if(url.isEmpty()){
			System.err.println("error url");
		}
		
		return url;
	}


	private static String getAno(Document paginaArte) {
		String ano = paginaArte.select("span[itemprop=dateCreated]").text();
		if(ano.isEmpty())
			System.err.println("error Fecha Obra");
		
		return ano;
	}


	private static String getGenero(Document paginaArte) {
		String genero = paginaArte.select("span[itemprop=genre]").text();
		if(genero.isEmpty())
			System.err.println("error Genero");
		
		return genero;
	}


	private static String getEstilo(Document paginaArte) {
		String estilo = paginaArte.select("a[href*=/es/paintings-by-style/]").text();
		
		if(estilo.isEmpty())
			System.err.println("error Estilo");
		
		return estilo;
	}


	private static String getTipo(Document paginaArte) {
		// TODO Auto-generated method stub
		return null;
	}


	private static String getTecnica(Document paginaArte) {
		// TODO Auto-generated method stub
		return null;
	}


	private static String getMuseo(Document paginaArte) {
		// TODO Auto-generated method stub
		return "";
	}


	private static String getPrecioArte(Document paginaArte) {
		// No hay precio
		return "";
	}


	private static String getNameArte(Document paginaArte) {
		String name = paginaArte.select("h1").first().text();
		if(name.isEmpty())
			System.err.println("error nombre Obra");
		return name;		
	}


	/**
	 * corrige errores de tildes
	 * @param url
	 * @return
	 */
	private static String fixUrl(String url) {
		//		if(url.contains("uecker")) //ä
		//			System.err.println("reivsar");
		if(url.contains("%C3%B8"))
			url = url.replace("%C3%B8","ø");
		
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


	private static Artist pageArtist(Document doc,String titulo,String uri) throws IOException {
		Artist artista = new Artist();
		String name = getArtistName(doc);
		artista.setName(name);
		artista.setNacionalidad(getNationality(doc));
		artista.setAnioNacimiento(getAnioNacimiento(doc));
		artista.setAnioMuerte(getAnioMuerte(doc));
		artista.setPaismuerte(getPaisMuerte(doc));
		artista.setLugarnacimiento(getLugarNacimiento(doc));
		artista.setMovimiento(getMovimiento(doc));
		artista.setTipodearte(getTipoArte(doc));
		artista.setUrl(doc.baseUri());
		artista.setUrlWiki(getUrlWiki(doc));
		artista.setArtInstitution(getArtInstitution(doc));
		artista.setEscuela(getEscuela(doc));
		artista.setInfluenciadopor(getInfluenciadoPor(doc));
		artista.setInfluencioa(getInfluencioA(doc));
		System.out.println(artista);
		
		return artista;
	}

	private static String getInfluencioA(Document doc) {
		String influencia = "";
		Elements links = doc.select("span:contains(Influenced on)").nextAll();
		for (Element element : links) {
			influencia=influencia+"|"+element.text();	
		}
		
		if(influencia.isEmpty()){
			System.err.println("error Influenciado por");
		}
		return influencia;
	}
 

	private static String getInfluenciadoPor(Document doc) {
		String influencia = "";
		Elements links = doc.select("span:contains(Influenced by)").nextAll();
		for (Element element : links) {
			influencia=influencia+"|"+element.text();	
		}
		
		if(influencia.isEmpty()){
			System.err.println("error Influenciado por");
		}
		return influencia;
	}


	private static String getEscuela(Document doc) {
		String tipoArte = "";
		Elements links = doc.select("a[href*=artists-by-painting-school/]");
		for (Element element : links) {
			tipoArte=tipoArte+"|"+element.text();	
		}
		
		if(tipoArte.isEmpty()){
			System.err.println("error Escuela");
		}
		return tipoArte;
	}


	private static String getArtInstitution(Document doc) {
		String tipoArte = "";
		Elements links = doc.select("a[href*=artists-by-art-institution/]");
		for (Element element : links) {
			tipoArte=tipoArte+"|"+element.text();	
		}
		
		if(tipoArte.isEmpty()){
			System.err.println("error Institution");
		}
		return tipoArte;
	}


	private static String getLugarNacimiento(Document doc) {
		String lugarNac = doc.select("span[itemprop=birthPlace]").text();
		
		if(lugarNac.isEmpty()){
			System.err.println("error Lugar Nacimiento");
		}
		return lugarNac;
	}


	/**
	 * Obtiene el tipo de arte del Artista
	 * @param doc
	 * @return
	 */
	private static String getTipoArte(Document doc) {
		String tipoArte = "";
		Elements links = doc.select("a[href*=artists-by-field/]");
		for (Element element : links) {
			tipoArte=tipoArte+"|"+element.text();	
		}
		
		if(tipoArte.isEmpty()){
			System.err.println("error Movimiento");
		}
		return tipoArte;
	}


	/**
	 * Obtiene el pais de muerte del artista
	 * @param doc
	 * @return
	 */
	private static String getPaisMuerte(Document doc) {
		String paisMuerte = doc.select("span[itemprop=deathPlace]").text();
		
		if(paisMuerte.isEmpty()){
			System.err.println("error pais muerte");
		}
		return paisMuerte;
	}


	/**
	 * Obtiene el movimiento del Artista
	 * @param doc
	 * @return
	 */
	private static String getMovimiento(Document doc) {
		String movimiento = "";
		Elements links = doc.select("a[href*=/es/artists-by-art-movement/]");
		for (Element element : links) {
		movimiento=movimiento+"|"+element.text();	
		}
		
		if(movimiento.isEmpty()){
			System.err.println("error Movimiento");
		}
		return movimiento;
	}


	/**
	 * Obtiene la url de wikipedia del artista
	 * @param doc
	 * @return
	 */
	private static String getUrlWiki(Document doc) {
		String wikiUrl = doc.select("a[href*=wikipedia.org]").text();
		
		if(wikiUrl.isEmpty()){
			System.err.println("error url wikipedia");
		}
		
		if(wikiUrl.length()>254){
			
			System.err.println("error url wikipedia larga");
			return null;
		}
			
		return wikiUrl;
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
		
		return anioMuerte;	
		}

	/**
	 * Obtiene el año de nacimiento del artista
	 * @param doc
	 * @return
	 */
	private static String getAnioNacimiento(Document doc) {
		
		String anioNacimiento;
		anioNacimiento = doc.select("span[itemprop=birthDate]").text();
		
		if(anioNacimiento.isEmpty()){
			System.err.println("error Año de Nacimiento");
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
			System.err.println("error nacionalidad");
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
			System.err.println("error Nombre Autor");
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