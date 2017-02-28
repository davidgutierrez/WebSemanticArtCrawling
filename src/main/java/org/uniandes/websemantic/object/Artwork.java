package org.uniandes.websemantic.object;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Artwork {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre,tipo,autor,ano,tecnica,precio,tamano,museo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Artist_Id", nullable = false)
	private Artist artist;
	
	public Artwork() {
		super();
	}

	public Artwork(String nombre, String tipo, String autor, String ano, String tecnica, String precio, String tamano,
			String museo) {
		super();
		this.nombre = nombre;
		this.tipo = tipo;
		this.autor = autor;
		this.ano = ano;
		this.tecnica = tecnica;
		this.precio = precio;
		this.tamano = tamano;
		this.museo = museo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getTecnica() {
		return tecnica;
	}

	public void setTecnica(String tecnica) {
		this.tecnica = tecnica;
	}

	public String getPrecio() {
		return precio;
	}

	public void setPrecio(String precio) {
		this.precio = precio;
	}

	public String getTamano() {
		return tamano;
	}

	public void setTamano(String tamano) {
		this.tamano = tamano;
	}

	public String getMuseo() {
		return museo;
	}

	public void setMuseo(String museo) {
		this.museo = museo;
	}

	public Artist getArtist() {
		return artist;
	}

	public void setArtist(Artist artist) {
		this.artist = artist;
	}
	
}
