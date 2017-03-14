package org.uniandes.websemantic.page;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class Wga {

	private static String pagina = "http://www.wga.hu";
	
	public void crawling(){

		String url = "/cgi-bin/artist.cgi?letter=";
		Document doc;
		try {
			char letra = 'a';
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("artistasFinal.txt"), "UTF-8"));
			BufferedWriter bwObras = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("obras.txt"), "UTF-8"));
			do {
				doc = Jsoup.connect(pagina+url+letra).get();
				List<Element> urls = doc.select("a[href]");
				for (Element el : urls) {
					if(!el.attr("href").contains("www")){
						Document paginador = Jsoup.connect(pagina+el.attr("href")).get();
						Iterator<Element> artistas = paginador.getElementsByClass("PAGENUM").select("tr").iterator();
						while(artistas.hasNext()){
							Element e = artistas.next();
							List<Node> nodos = e.childNodes();
							if (!nodos.isEmpty() && nodos.size()==5 && !nodos.get(1).childNode(0).childNode(0).toString().contains("ARTIST")) {
								StringBuffer registro = new StringBuffer();
//								String nombreArtista = nodos.get(1).childNode(0).childNode(0).toString();
								if(nodos.get(1).childNode(0).attr("href").isEmpty() && !nodos.get(1).hasAttr("width")){
									String elementoA = nodos.get(1).toString().split("<a href=")[1];
									String urlCalculada = !elementoA.isEmpty() ? elementoA.split("\"")[1] : "";
//									bwObras = obtenerObras(urlCalculada, nombreArtista, "", bwObras);
									registro.append(urlCalculada);
								} else {
									registro.append(nodos.get(1).childNode(0).attr("href"));
//									bwObras = obtenerObras(nodos.get(1).childNode(0).attr("href"), nombreArtista, "", bwObras);
								}
								registro.append(";");
								registro.append(nodos.get(1).childNode(0).childNode(0).toString()) ;
								registro.append(";");
								System.out.println(nodos.get(1).childNode(0).childNode(0).toString());
								if (nodos.get(2).childNode(0).toString().contains("-")) {
									registro.append(nodos.get(2).childNode(0).toString().split("-")[0].trim().replace("(", ""));
									registro.append(";");
									System.out.println(nodos.get(2).childNode(0).toString());
									registro.append(nodos.get(2).childNode(0).toString().split("-")[1].trim().replace(")", ""));
									registro.append(";");
									System.out.println(nodos.get(2).childNode(0).toString());
								} else {
									registro.append(nodos.get(2).childNode(0).toString());
									registro.append(";");
									System.out.println(nodos.get(2).childNode(0).toString());
									registro.append(nodos.get(2).childNode(0).toString());
									registro.append(";");
									System.out.println(nodos.get(2).childNode(0).toString());
								}
								registro.append(nodos.get(3).childNode(0).toString());
								registro.append(";");
								System.out.println(nodos.get(3).childNode(0).toString());
								registro.append(nodos.get(4).childNode(0).toString());
								registro.append(";");
								System.out.println(nodos.get(4).childNode(0).toString());
								//
								bw.write(registro.toString().replace("<b>", "").replace("</b>", ""));
								bw.newLine();
								
							}
							
						}
					}
				}
				letra ++;
			} while (letra <= 'z');
		
			bw.flush();
            bw.close();
            
            bwObras.flush();
            bwObras.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedWriter obtenerObras(String urlCalculada, String nombreArtista, String nombreSubgrupo, BufferedWriter bwObras) throws IOException {
		Document obras = Jsoup.connect(urlCalculada).get();
		Iterator<Element> obraLst = obras.getElementsByTag("table").select("tr").iterator();
		obras.toString().replace("<br>", "");
		
		while (obraLst.hasNext()) {
			
			Element el = obraLst.next();
			StringBuffer registroObra = new StringBuffer();
			for(int i=0; i<el.childNodes().size(); i++){
				List<Node> n = el.childNodes();
				
				if (!n.get(i).childNodes().isEmpty()) {
					registroObra.append(urlCalculada);
					registroObra.append(";");
					registroObra.append(nombreArtista);
					registroObra.append(";");
					registroObra.append(nombreSubgrupo);
					registroObra.append(";");
					for(Node nInterior : n.get(i).childNodes()){
						registroObra.append(nInterior.attr("href"));
						registroObra.append(";");
						if(nInterior.hasAttr("href") && nInterior.attr("href").contains("/")){
							registroObra.append(nInterior.attr("href"));
//							System.out.println( elemento + nInterior.attr("href"));
							registroObra.append(";");
							continue;
						} else if (!nInterior.toString().trim().equals("<br>") && !nInterior.toString().trim().equals("")) {
							String dato = nInterior.toString();
							//se quita salto de linea
							dato.replaceAll("\n", "");
							// se quita <br>
							dato.replaceAll("<br>", ";");
							registroObra.append(dato);
//							System.out.println(elemento + nInterior.toString().replaceAll("\n", ""));
						}
					}
					
				}
				
			}
			if(!registroObra.toString().contains("biograph") && !registroObra.toString().contains("font")){
				bwObras.write(registroObra.toString());
				bwObras.newLine();
				System.out.println("nueva linea");
			}
		}
		return bwObras;
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public void descargarPostcards() throws IOException{
		String url = "/support/post/sendcard.html";
		Document doc;
		String nombreGrupo = "";
		BufferedWriter bwGrupos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("grupoObras.txt"), "UTF-8"));
		try {
			doc = Jsoup.connect(pagina+url).get();
			List<Element> urls = doc.getElementsByTag("ul");
			for (int i=1; i<urls.size(); i++) {
				if(i%2!=0){
					Iterator<Element> grupos = urls.get(i).getElementsByTag("li").iterator();
					System.out.println(nombreGrupo);
					while(grupos.hasNext()){
						Element subgrupo = grupos.next();
						nombreGrupo = !subgrupo.childNode(0).hasAttr("href") ? subgrupo.childNode(0).toString() : nombreGrupo;
						if (!subgrupo.childNode(0).attr("href").isEmpty()) {
							String urlSubGrupo = pagina + subgrupo.childNode(0).attr("href"); 
							System.out.println(urlSubGrupo);
							bwGrupos = obtenerObras(urlSubGrupo, nombreGrupo, subgrupo.childNode(0).toString(), bwGrupos);
							
						}
						
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bwGrupos.flush();
			bwGrupos.close();
		}
	}
	
	public void descargarLugares() throws IOException{
		String url = "/database/database.html";
		Document doc;
		BufferedWriter bwLugares = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("lugares.txt"), "UTF-8"));
		try {
			doc = Jsoup.connect(pagina+url).get();
			List<Element> lugares = doc.getElementsByTag("table");
			Iterator<Element> linksLugares = lugares.get(0).getElementsByTag("li").iterator();
			int i = 0;
			while (i == 0) {
				Element link = linksLugares.next();
				link.childNodes();
				for(Node e : link.childNodes()){
					if(e.hasAttr("href")){
						System.out.println(e.toString());
						// lista museos
						// lista iglesias
						consultarLugares(e.attr("href").toString());
					}
				}
				i++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bwLugares.flush();
			bwLugares.close();
		}
	}

	private void consultarLugares(String string) {
		try {
//			doc = Jsoup.connect(pagina+string).get();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}