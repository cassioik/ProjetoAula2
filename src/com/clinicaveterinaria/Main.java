package com.clinicaveterinaria;

import com.clinicaveterinaria.dao.PessoaDAO;
import com.clinicaveterinaria.model.Pessoa;

public class Main {
	public static void main(String[] args) {
//		try{
//			TipoAnimal t = new TipoAnimal();
//			t.setNomeRaca("Peixe");
//			t.setDescricao("Tipo de Peixe");
//			
//			Pessoa p = new Pessoa();
//			p.setCpf(555556L);
//			p.setNome("Carlos");
//			p.setNascimento(new Date());
//			
//			Animal a = new Animal();
//			a.setDono(p);
//			a.setNascimento(new Date());
//			a.setNome("Pexinho");
//			a.setTipoAnimal(t);
//			
//			AnimalDAO animalDAO = new AnimalDAO();
//			animalDAO.inserirComRelacionamentos(a);
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
//		try{
//			PessoaDAO pessoaDAO = new PessoaDAO();
//			TipoAnimalDAO tipoAnimalDAO = new TipoAnimalDAO();
//			AnimalDAO animalDAO = new AnimalDAO();
//			
//			Pessoa p = new Pessoa();
//			p.setNome("Cassio");
//			p.setCpf(12345678901L);
//			p.setNascimento(new Date());
//			pessoaDAO.inserir(p);
//			
//			TipoAnimal t = tipoAnimalDAO.buscar(0);
//			
//			Animal a = new Animal();
//			a.setTipoAnimal(t);
//			a.setDono(p);
//			a.setNome("Tobby");
//			a.setNascimento(new Date());
//			animalDAO.inserir(a);
//			
//			Animal a2 = new Animal();
//			a2.setTipoAnimal(t);
//			a2.setDono(p);
//			a2.setNome("Teka");
//			a2.setNascimento(new Date());
//			animalDAO.inserir(a2);	
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
		try{
			PessoaDAO pessoaDAO = new PessoaDAO();
			
			Pessoa p = new Pessoa();
			p = pessoaDAO.buscarEager(4);
			
			pessoaDAO.removerComRelacionamentos(p);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}