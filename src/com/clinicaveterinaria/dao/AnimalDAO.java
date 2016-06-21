package com.clinicaveterinaria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.clinicaveterinaria.jdbc.ConnectionFactory;
import com.clinicaveterinaria.model.Animal;
import com.clinicaveterinaria.model.Pessoa;
import com.clinicaveterinaria.model.TipoAnimal;

public class AnimalDAO implements IGenericDAO<Animal, Integer>{

	private ConnectionFactory connectionFactory = new ConnectionFactory(); 
	
	@Override
	public Animal buscar(Integer id) throws Exception {
		Animal retorno = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try{
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT tipo_id, pessoa_id, nome, nascimento FROM ANIMAL"
					+ " WHERE animal_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			
			if(rs.next()){
				retorno = new Animal();
				retorno.setId(id);
				retorno.setNome(rs.getString("nome"));
				retorno.setNascimento(rs.getDate("nascimento"));
				
				Pessoa dono = new Pessoa();
				dono.setId(rs.getInt("pessoa_id"));
				retorno.setDono(dono);
				
				TipoAnimal tipo = new TipoAnimal();
				tipo.setId(rs.getInt("tipo_id"));
				retorno.setTipoAnimal(tipo);
				if(rs.next()){
					throw new Exception("Há um problema com o banco.");
				}
			}
			return retorno;
		}catch(Exception e){
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		}finally{
			try{
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				//tratar melhor, pois se connection lançar uma exceção, rs
				//e statement não fecham
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}
	
	public void carregarEntidadesInternas(Animal animal) throws Exception{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		
		try{
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT p.pessoa_id, p.cpf, p.nome, p.nascimento,"
					+ " t.tipoanimal_id, t.nomeraca, t.descricao"
					+ " FROM PESSOA p"
					+ " INNER JOIN ANIMAL a ON (p.pessoa_id = a.pessoa_id)"
					+ " INNER JOIN TIPOANIMAL t ON (t.tipoanimal_id = a.tipo_id)"
					+ " WHERE a.animal_id = ?";
			
			statement = connection.prepareStatement(sql);
			statement.setInt(1, animal.getId());
			rs = statement.executeQuery();
			
			if(rs.next()){
				Pessoa dono = new Pessoa();
				dono.setId(rs.getInt("pessoa_id"));
				dono.setCpf(rs.getLong("cpf"));
				dono.setNome(rs.getString("nome"));
				dono.setNascimento(rs.getDate("nascimento"));
				animal.setDono(dono);
				
				TipoAnimal tipo = new TipoAnimal();
				tipo.setId(rs.getInt("tipoanimal_id"));
				tipo.setNomeRaca(rs.getString("nomeraca"));
				tipo.setDescricao(rs.getString("descricao"));
				animal.setTipoAnimal(tipo);
			}
			return;
		}catch(Exception e){
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		}finally{
			try{
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				//tratar melhor, pois se connection lançar uma exceção, rs
				//e statement não fecham
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}
	
	@Override
	public void inserir(Animal animal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		try{
			connection = connectionFactory.getConnection();
			
	        String sql = "insert into animal" +
	                " (tipo_id,pessoa_id,nome,nascimento)" +
	                " values (?,?,?,?)";
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	        statement.setInt(1, animal.getTipoAnimal().getId());
	        statement.setInt(2, animal.getDono().getId());
	        statement.setString(3, animal.getNome());
	        if(animal.getNascimento() != null)
	        	statement.setDate(4, new java.sql.Date(animal.getNascimento().getTime()));
	        else
	        	statement.setDate(4, null);

	        statement.execute();
	        generatedKeys = statement.getGeneratedKeys();
	        if(generatedKeys.next())
	        	animal.setId(generatedKeys.getInt(1));
	        else
	        	throw new Exception("Erro ao gravar entidade");
			
		}catch(SQLException sqle){
			throw new RuntimeException(sqle);
		}finally{
			try{
				if(generatedKeys != null)
					generatedKeys.close();
				if(statement != null)
					statement.close();
				if(connection != null)
					connection.close();
				//tratar melhor, pois se connection lançar uma exceção, rs
				//e statement não fecham
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	@Override
	public List<Animal> listar() throws Exception {
		List<Animal> animais = new ArrayList<Animal>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try{
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT animal_id, tipo_id, pessoa_id, nome, nascimento FROM animal";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			
			while(rs.next()){
				Animal a = new Animal();
				a.setId(rs.getInt("animal_id"));
				a.setNome(rs.getString("nome"));
				a.setNascimento(rs.getDate("nascimento"));
				
				Pessoa dono = new Pessoa();
				dono.setId(rs.getInt("pessoa_id"));
				a.setDono(dono);

				TipoAnimal tipo = new TipoAnimal();
				tipo.setId(rs.getInt("tipo_id"));
				a.setTipoAnimal(tipo);
				
				animais.add(a);
			}
			return animais;
		}catch(Exception e){
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		}finally{
			try{
				if(connection != null)
					connection.close();
				if(rs != null)
					rs.close();
				if(statement != null)
					statement.close();
				//tratar melhor, pois se connection lançar uma exceção, rs
				//e statement não fecham
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	@Override
	public void atualizar(Animal animal) throws Exception {
		String sql = "UPDATE animal SET tipo_id = ?, pessoa_id = ?, nome = ?, nascimento = ?" +
	             " WHERE animal_id = ?";
	     Connection connection = null;
	     PreparedStatement statement = null;
			try{
				connection = connectionFactory.getConnection();
	         statement = connection.prepareStatement(sql);
	         statement.setInt(1, animal.getTipoAnimal().getId());
	         statement.setInt(2, animal.getDono().getId());
	         statement.setString(3, animal.getNome());
	         if(animal.getNascimento() != null)
	        	statement.setDate(4, new java.sql.Date(animal.getNascimento().getTime()));
	        else
	        	statement.setDate(4, null);
	         statement.setLong(5, animal.getId());
	         statement.executeUpdate();
	     } catch (SQLException e) {
	         throw new RuntimeException(e);
	     }finally{
	    	 try{
				if(connection != null)
					connection.close();
				if(statement != null)
					statement.close();
				//tratar melhor, pois se connection lançar uma exceção, rs
				//e statement não fecham
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	@Override
	public void remover(Animal animal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = connectionFactory.getConnection();
	         statement = connection.prepareStatement("DELETE FROM animal WHERE animal_id = ?");
	         statement.setInt(1, animal.getId());
	         statement.executeUpdate();
	     } catch (SQLException e) {
	         throw new RuntimeException(e);
	     }finally{
	    	 try{
				if(connection != null)
					connection.close();
				if(statement != null)
					statement.close();
				//tratar melhor, pois se connection lançar uma exceção statement não fecha
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}
	
	public void inserirComRelacionamentos(Animal animal) throws Exception{
		Connection connection = null;
		PreparedStatement statementTipo = null;
		PreparedStatement statementPessoa = null;
		PreparedStatement statementAnimal = null;
		ResultSet generatedKeys = null;
		try{
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(false);
			
	        String sql = "insert into tipoanimal" +
	                " (nomeraca,descricao)" +
	                " values (?,?)";
	        statementTipo = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	        statementTipo.setString(1, animal.getTipoAnimal().getNomeRaca());
	        statementTipo.setString(2, animal.getTipoAnimal().getDescricao());
	        statementTipo.execute();
	        generatedKeys = statementTipo.getGeneratedKeys();
	        if(generatedKeys.next())
	        	animal.getTipoAnimal().setId(generatedKeys.getInt(1));
	        else
	        	throw new Exception("Erro ao gravar entidade");
	        generatedKeys.close();
	        
	        sql = "insert into pessoa" +
	                " (cpf,nome,nascimento)" +
	                " values (?,?,?)";
	        statementPessoa = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statementPessoa.setLong(1, animal.getDono().getCpf());
	        statementPessoa.setString(2, animal.getDono().getNome());
	        if(animal.getDono().getNascimento() != null)
	        	statementPessoa.setDate(3, 
	        			new java.sql.Date(animal.getDono().getNascimento().getTime()));
	        else
	        	statementPessoa.setDate(3, null);
	        statementPessoa.execute();
	        generatedKeys = statementPessoa.getGeneratedKeys();
	        if(generatedKeys.next())
	        	animal.getDono().setId(generatedKeys.getInt(1));
	        else
	        	throw new Exception("Erro ao gravar entidade");
	        generatedKeys.close();         
	        
	        sql = "insert into animal" +
	                " (tipo_id,pessoa_id,nome, nascimento)" +
	                " values (?,?,?,?)";
	        
	        statementAnimal = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        statementAnimal.setInt(1, animal.getTipoAnimal().getId());
	        statementAnimal.setInt(2, animal.getDono().getId());
	        statementAnimal.setString(3, animal.getNome());
	        if(animal.getNascimento() == null)
	        	statementAnimal.setDate(4, null);
	        else
	        	statementAnimal.setDate(4, 
	        			new java.sql.Date(animal.getNascimento().getTime()));
	        statementAnimal.execute();
	        generatedKeys = statementAnimal.getGeneratedKeys();
	        if(generatedKeys.next())
	        	animal.setId(generatedKeys.getInt(1));
	        else
	        	throw new Exception("Erro ao gravar entidade");
	        generatedKeys.close();
	        connection.commit();
		}catch(SQLException sqle){
			connection.rollback();
			throw new RuntimeException(sqle);
		}finally{
			try{
				if(statementPessoa != null)
					statementPessoa.close();
				if(statementTipo != null)
					statementTipo.close();
				if(connection != null)
					connection.close();
			}catch(Exception e){
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}
}
