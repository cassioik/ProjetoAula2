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

public class TipoAnimalDAO implements IGenericDAO<TipoAnimal, Integer>{
	
	private ConnectionFactory connectionFactory = new ConnectionFactory(); 

	@Override
	public void inserir(TipoAnimal tipoAnimal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		try{
			connection = connectionFactory.getConnection();
			
	        String sql = "INSERT INTO tipoanimal" +
	                " (nomeraca,descricao)" +
	                " values (?,?)";
	        statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	        statement.setString(1, tipoAnimal.getNomeRaca());
	        statement.setString(2, tipoAnimal.getDescricao());

	        statement.execute();
	        generatedKeys = statement.getGeneratedKeys();
	        if(generatedKeys.next())
	        	tipoAnimal.setId(generatedKeys.getInt(1));
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
	public List<TipoAnimal> listar() throws Exception {
		List<TipoAnimal> tiposAnimais = new ArrayList<TipoAnimal>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try{
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT tipoanimal_id, nomeraca, descricao FROM tipoanimal";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			
			while(rs.next()){
				TipoAnimal t = new TipoAnimal();
				t.setId(rs.getInt("tipoanimal_id"));
				t.setNomeRaca(rs.getString("nomeraca"));
				t.setDescricao(rs.getString("descricao"));
				
				tiposAnimais.add(t);
			}
			return tiposAnimais;
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
	public TipoAnimal buscar(Integer id) throws Exception {
		TipoAnimal retorno = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try{
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT nomeraca, descricao FROM tipoanimal"
					+ " WHERE tipoanimal_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			
			if(rs.next()){
				retorno = new TipoAnimal();
				retorno.setId(id);
				retorno.setNomeRaca(rs.getString("nomeraca"));
				retorno.setDescricao(rs.getString("descricao"));
				
				if(rs.next()){
					throw new Exception("H� um problema com o banco.");
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

	public TipoAnimal buscarEager(Integer id) throws Exception{
		TipoAnimal retorno = null;
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try{
			connection = connectionFactory.getConnection();
			
			String sql = "SELECT t.nomeraca, t.descricao,"
					+ " a.animal_id, a.pessoa_id, a.nome nomeAnimal, a.nascimento nascAnimal,"
					+ " p.pessoa_id, p.cpf, p.nome nomePessoa, p.nascimento nascPessoa"
					+ " FROM tipoanimal t"
					+ " LEFT JOIN animal a ON (a.tipo_id = t.tipoanimal_id)"
					+ " LEFT JOIN pessoa p ON (p.pessoa_id = a.pessoa_id)"
					+ " WHERE t.tipoanimal_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();
			
			if(rs.next()){
				retorno = new TipoAnimal();
				retorno.setId(id);
				retorno.setNomeRaca(rs.getString("nomeraca"));
				retorno.setDescricao(rs.getString("descricao"));
				
				List<Animal> animais= new ArrayList<Animal>();
				rs.getInt("animal_id");//faz uma leitura no id
			    if(!rs.wasNull()){
					do{
						Animal a = new Animal();
						a.setId(rs.getInt("animal_id"));
						a.setNome(rs.getString("nomeAnimal"));
						a.setTipoAnimal(retorno);
						a.setNascimento(rs.getDate("nascAnimal"));
						
						Pessoa dono = new Pessoa();
						dono.setId(rs.getInt("pessoa_id"));
						dono.setCpf(rs.getLong("cpf"));
						dono.setNome(rs.getString("nomePessoa"));
						dono.setNascimento(rs.getDate("nascPessoa"));
						a.setDono(dono);
						
						animais.add(a);
					}while(rs.next());
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
	
	@Override
	public void atualizar(TipoAnimal tipoAnimal) throws Exception {
		String sql = "UPDATE tipoanimal SET nomeraca = ?, descricao = ?" +
	             " WHERE tipoanimal_id = ?";
	     Connection connection = null;
	     PreparedStatement statement = null;
			try{
				connection = connectionFactory.getConnection();
	         statement = connection.prepareStatement(sql);
	         statement.setString(1, tipoAnimal.getNomeRaca());
	         statement.setString(2, tipoAnimal.getDescricao());
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
	public void remover(TipoAnimal tipoAnimal) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		try{
			connection = connectionFactory.getConnection();
	         statement = connection.prepareStatement("DELETE FROM tipoanimal WHERE tipoanimal_id = ?");
	         statement.setInt(1, tipoAnimal.getId());
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

}
