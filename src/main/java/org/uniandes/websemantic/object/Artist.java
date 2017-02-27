package org.uniandes.websemantic.object;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Artist {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
 
	private String name,anioNacimiento,anioMuerte,movimiento,nacionalidad,
	paismuerte,tipodearte,influenciadopor,influencioa,url;

	
	public Artist() {
		super();
	}

	public Artist(String name, String anioNacimiento, String anioMuerte, String movimiento, String nacionalidad,
			String paismuerte, String tipodearte, String influenciadopor, String influencioa, String url) {
		super();
		this.name = name;
		this.anioNacimiento = anioNacimiento;
		this.anioMuerte = anioMuerte;
		this.movimiento = movimiento;
		this.nacionalidad = nacionalidad;
		this.paismuerte = paismuerte;
		this.tipodearte = tipodearte;
		this.influenciadopor = influenciadopor;
		this.influencioa = influencioa;
		this.url = url;
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

	@Override
	public String toString() {
		return "Artist [name=" + name + ", anioNacimiento=" + anioNacimiento + ", anioMuerte=" + anioMuerte
				+ ", movimiento=" + movimiento + ", nacionalidad=" + nacionalidad + ", paismuerte=" + paismuerte
				+ ", tipodearte=" + tipodearte + ", influenciadopor=" + influenciadopor + ", influencioa=" + influencioa
				+ ", url=" + url + "]";
	}
	
	
}
