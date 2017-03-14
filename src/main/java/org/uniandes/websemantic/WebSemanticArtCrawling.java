package org.uniandes.websemantic;

import java.io.IOException;

import org.uniandes.websemantic.page.Artnet;
import org.uniandes.websemantic.page.Wga;

public class WebSemanticArtCrawling {

	public static void main(String[] args) {
		Artnet.crawling();
		Wga wga = new Wga();
		wga.crawling();
		WikiArt.crawling();
		try {
			wga.descargarPostcards();
			wga.descargarLugares();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	

}
