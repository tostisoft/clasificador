package com.contpaqi.clasificador.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

	private String user;
	private String passwd;
	private String ipServer;
	private Connection connection;

	public DBConnection() {
		this.user = "sqlserver";
		this.passwd = "sqlserver2";
		this.ipServer = "192.168.30.226";
	}

	public DBConnection(String ipServer) {
		this.ipServer = ipServer;
		this.user = "sqlserver";
		this.passwd = "sqlserver2";
	}

	public DBConnection(String user, String passwd) {
		this.user = user;
		this.passwd = passwd;
	}

	public void openConnection() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			connection = DriverManager.getConnection(
					"jdbc:sqlserver://" + this.ipServer + ":1433;databaseName=ADD_Catalogos", this.user, this.passwd);
			System.out.println("Conectado");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
				System.out.println("Conexion cerrada");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ResultSet query(String text) {
		String sql = "select * from c_ClaveProdServ where c_ClaveProdServ.Valor like '%" + text + "%';";
		Statement statement;
		ResultSet rs = null;
		try {
			statement = this.connection.createStatement();
			rs = statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean empty = false;
		try {
			empty = rs.isBeforeFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (empty == true) ? rs : null;
	}

	public ResultSet getItems(int codigo) {
		String sql = "select * from c_ClaveProdServ where Codigo >= " + codigo + " and Codigo <" + (codigo + 100) + ";";
		Statement statement;
		ResultSet rs = null;
		try {
			statement = this.connection.createStatement();
			rs = statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean empty = false;
		try {
			empty = rs.isBeforeFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (empty == true) ? rs : null;
	}

	public String getItem(int codigo) {
		String sql = "select * from c_ClaveProdServ where Codigo =" + codigo + ";";
		Statement statement;
		ResultSet rs = null;
		String value = "";

		try {
			statement = this.connection.createStatement();
			rs = statement.executeQuery(sql);
			if (rs.isBeforeFirst()) {
				rs.next();
				value = rs.getString("Valor");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return value;
	}
}
