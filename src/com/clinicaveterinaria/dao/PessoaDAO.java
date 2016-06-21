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

public class PessoaDAO implements IGenericDAO<Pessoa, Integer> {

	private ConnectionFactory connectionFactory = new ConnectionFactory();

	public List<Pessoa> listar() throws Exception {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT pessoa_id, cpf, nome, nascimento FROM PESSOA";
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			while (rs.next()) {
				Pessoa p = new Pessoa();
				p.setId(rs.getInt("pessoa_id"));
				p.setCpf(rs.getLong("cpf"));
				p.setNome(rs.getString("nome"));
				p.setNascimento(rs.getDate("nascimento"));

				pessoas.add(p);
			}
			return pessoas;
		} catch (Exception e) {
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		} finally {
			try {
				if (connection != null)
					connection.close();
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				// tratar melhor, pois se connection lançar uma exceção, rs
				// e statement não fecham
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public Pessoa buscarEager(Integer id) throws Exception {
		Pessoa retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT p.cpf, p.nome, p.nascimento,"
					+ " a.animal_id, a.nome nomeAnimal, a.nascimento nascAnimal" + " FROM PESSOA p"
					+ " LEFT JOIN ANIMAL a ON (p.pessoa_id = a.pessoa_id)" + " WHERE p.pessoa_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new Pessoa();
				retorno.setId(id);
				retorno.setCpf(rs.getLong("cpf"));
				retorno.setNome(rs.getString("nome"));
				retorno.setNascimento(rs.getDate("nascimento"));

				List<Animal> animais = new ArrayList<Animal>();
				rs.getInt("animal_id");// faz uma leitura no id
				if (!rs.wasNull()) {
					do {
						Animal a = new Animal();
						a.setId(rs.getInt("animal_id"));
						a.setNome(rs.getString("nomeAnimal"));
						a.setTipoAnimal(null);
						a.setNascimento(rs.getDate("nascAnimal"));
						a.setDono(retorno);
						animais.add(a);
					} while (rs.next());
				}
				retorno.setAnimais(animais);
			}
			return retorno;
		} catch (Exception e) {
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public Pessoa buscar(Integer id) throws Exception {
		Pessoa retorno = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "SELECT cpf, nome, nascimento FROM PESSOA" + " WHERE pessoa_id = ?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				retorno = new Pessoa();
				retorno.setId(id);
				retorno.setCpf(rs.getLong("cpf"));
				retorno.setNome(rs.getString("nome"));
				retorno.setNascimento(rs.getDate("nascimento"));
				if (rs.next()) {
					throw new Exception("Há um problema com o banco.");
				}
			}
			return retorno;
		} catch (Exception e) {
			throw new Exception("Ocorreu um erro ao executar a consulta", e);
		} finally {
			try {
				if (connection != null)
					connection.close();
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				// tratar melhor, pois se connection lançar uma exceção, rs
				// e statement não fecham
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public void inserir(Pessoa pessoa) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet generatedKeys = null;
		try {
			connection = connectionFactory.getConnection();

			String sql = "insert into pessoa" + " (cpf,nome,nascimento)" + " values (?,?,?)";
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			statement.setLong(1, pessoa.getCpf());
			statement.setString(2, pessoa.getNome());
			if (pessoa.getNascimento() != null)
				statement.setDate(3, new java.sql.Date(pessoa.getNascimento().getTime()));
			else
				statement.setDate(3, null);

			statement.execute();
			generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next())
				pessoa.setId(generatedKeys.getInt(1));
			else
				throw new Exception("Erro ao gravar entidade");

		} catch (SQLException sqle) {
			throw new RuntimeException(sqle);
		} finally {
			try {
				if (generatedKeys != null)
					generatedKeys.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
				// tratar melhor, pois se connection lançar uma exceção, rs
				// e statement não fecham
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public void atualizar(Pessoa pessoa) throws Exception {
		String sql = "update pessoa set nome = ? ,cpf = ?,nascimento = ?" + " where pessoa_id=?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement(sql);
			statement.setString(1, pessoa.getNome());
			statement.setLong(2, pessoa.getCpf());
			if (pessoa.getNascimento() != null)
				statement.setDate(3, new java.sql.Date(pessoa.getNascimento().getTime()));
			else
				statement.setDate(3, null);
			statement.setLong(4, pessoa.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (connection != null)
					connection.close();
				if (statement != null)
					statement.close();
				// tratar melhor, pois se connection lançar uma exceção, rs
				// e statement não fecham
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public void remover(Pessoa pessoa) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = connectionFactory.getConnection();
			statement = connection.prepareStatement("delete from pessoa where pessoa_id=?");
			statement.setLong(1, pessoa.getId());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (connection != null)
					connection.close();
				if (statement != null)
					statement.close();
				// tratar melhor, pois se connection lançar uma exceção
				// statement não fecha
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}

	public void removerComRelacionamentos(Pessoa pessoa) throws Exception {
		Connection connection = null;
		PreparedStatement statementAnimal = null;
		PreparedStatement statementPessoa = null;
		try {
			connection = connectionFactory.getConnection();
			connection.setAutoCommit(false);
			
			for (Animal a : pessoa.getAnimais()){
				statementAnimal = connection.prepareStatement("DELETE FROM animal WHERE animal_id=?");
				statementAnimal.setInt(1, a.getId());
				statementAnimal.executeUpdate();
			}
			
			statementPessoa = connection.prepareStatement("DELETE FROM pessoa WHERE pessoa_id=?");
			statementPessoa.setLong(1, pessoa.getId());
			statementPessoa.executeUpdate();

			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new RuntimeException(e);
		} finally {
			try {
				if (connection != null)
					connection.close();
				if (statementPessoa != null)
					statementPessoa.close();
				// tratar melhor, pois se connection lançar uma exceção
				// statement não fecha
			} catch (Exception e) {
				throw new Exception("Ocorreu um erro ao executar a consulta", e);
			}
		}
	}
}