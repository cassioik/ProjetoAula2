package com.clinicaveterinaria.model;

import java.util.Date;

public class Animal {
	private Integer id;
	private TipoAnimal tipoAnimal;
	private Pessoa dono;
	private String nome;
	private Date nascimento;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public TipoAnimal getTipoAnimal() {
		return tipoAnimal;
	}
	public void setTipoAnimal(TipoAnimal tipoAnimal) {
		this.tipoAnimal = tipoAnimal;
	}
	public Pessoa getDono() {
		return dono;
	}
	public void setDono(Pessoa dono) {
		this.dono = dono;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getNascimento() {
		return nascimento;
	}
	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}
}