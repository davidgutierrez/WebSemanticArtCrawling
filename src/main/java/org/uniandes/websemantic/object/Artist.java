package org.uniandes.websemantic.object;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Artist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
 
	private String name,anioNacimiento,anioMuerte,movimiento,nacionalidad, lugarnacimiento,
	paismuerte,tipodearte,url,escuela, artInstitution;
	
	@Column(length=2048)
	private String influenciadopor;
	
	@Column(length=2048)
	private String influencioa;
	
	@Column(length=2048)
	private String urlWiki;
		

	public String getUrlWiki() {
		return urlWiki;
	}

	public void setUrlWiki(String urlWiki) {
		this.urlWiki = urlWiki;
	}

	@ElementCollection(targetClass=Artwork.class)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "artist")
	private Set<Artwork> artworks;
	public Artist() {
		super();
		artworks = new HashSet<Artwork>();
	}

	
	public Artist(Long id, String name, String anioNacimiento, String anioMuerte, String movimiento,
			String nacionalidad, String lugarnacimiento, String paismuerte, String tipodearte, String influenciadopor,
			String influencioa, String url, String urlWiki, String escuela, Set<Artwork> artworks) {
		super();
		this.id = id;
		this.name = name;
		this.anioNacimiento = anioNacimiento;
		this.anioMuerte = anioMuerte;
		this.movimiento = movimiento;
		this.nacionalidad = nacionalidad;
		this.lugarnacimiento = lugarnacimiento;
		this.paismuerte = paismuerte;
		this.tipodearte = tipodearte;
		this.influenciadopor = influenciadopor;
		this.influencioa = influencioa;
		this.url = url;
		this.urlWiki = urlWiki;
		this.escuela = escuela;
		this.artworks = artworks;
	}

	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAnioNacimiento() {
		return anioNacimiento;
	}

	public void setAnioNacimiento(String anioNacimiento) {
		this.anioNacimiento = anioNacimiento;
	}

	public String getAnioMuerte() {
		return anioMuerte;
	}

	public void setAnioMuerte(String anioMuerte) {
		this.anioMuerte = anioMuerte;
	}

	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getPaismuerte() {
		return paismuerte;
	}

	public void setPaismuerte(String paismuerte) {
		this.paismuerte = paismuerte;
	}

	public String getTipodearte() {
		return tipodearte;
	}

	public void setTipodearte(String tipodearte) {
		this.tipodearte = tipodearte;
	}

	public String getInfluenciadopor() {
		return influenciadopor;
	}

	public void setInfluenciadopor(String influenciadopor) {
		this.influenciadopor = influenciadopor;
	}

	public String getInfluencioa() {
		return influencioa;
	}

	public void setInfluencioa(String influencioa) {
		this.influencioa = influencioa;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<Artwork> getArtworks() {
		return artworks;
	}

	public void setArtworks(Set<Artwork> artworks) {
		this.artworks = artworks;
	}

	@Override
	public String toString() {
		return "Artist [id=" + id + ", name=" + name + ", anioNacimiento=" + anioNacimiento + ", anioMuerte="
				+ anioMuerte + ", movimiento=" + movimiento + ", nacionalidad=" + nacionalidad + ", paismuerte="
				+ paismuerte + ", tipodearte=" + tipodearte + ", influenciadopor=" + influenciadopor + ", influencioa="
				+ influencioa + ", url=" + url + ", urlWiki=" + urlWiki + ", artworks=" + artworks + "]";
	}

	public String getLugarnacimiento() {
		return lugarnacimiento;
	}

	public void setLugarnacimiento(String lugarnacimiento) {
		this.lugarnacimiento = lugarnacimiento;
	}

	public String getEscuela() {
		return escuela;
	}

	public void setEscuela(String escuela) {
		this.escuela = escuela;
	}

	public String getArtInstitution() {
		return artInstitution;
	}

	public void setArtInstitution(String artInstitution) {
		this.artInstitution = artInstitution;
	}

	
	
	
}
