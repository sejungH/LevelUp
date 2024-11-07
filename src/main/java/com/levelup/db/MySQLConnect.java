package com.levelup.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;

public class MySQLConnect {

	private JavaPlugin plugin;

	private static final String HOST = "huni1011.kro.kr";
	private static final int PORT = 3306;
	private static final String DATABASE = "levelup_db";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "huni1011";

	private Connection connection;

	public MySQLConnect(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public int openConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				return 0; // 이미 연결되어 있음
			}

			synchronized (this) {
				if (connection != null && !connection.isClosed()) {
					return 0; // 이미 연결되어 있음
				}
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true", USERNAME,
						PASSWORD);
				plugin.getLogger().info("데이터베이스에 정상적으로 연결되었습니다");
				return 0;
			}
		} catch (Exception e) {
            plugin.getLogger().warning("데이터베이스 연결에 실패했습니다: " + e.getMessage());
			e.printStackTrace();
			return 1;
		}
	}
	
	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				this.openConnection();
			}
			if (!isConnectionAlive()) {
				this.closeConnection();
				this.openConnection();
			}
			return this.connection;
			
		} catch (SQLException e) {
			plugin.getLogger().warning("데이터베이스 연결을 확인하는 중 오류가 발생했습니다: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public int closeConnection() {
		try {
			connection.close();
			plugin.getLogger().info("데이터베이스 연결이 종료되었습니다");

			return 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public boolean isConnectionAlive() {
		try (Statement stmt = connection.createStatement()) {
			stmt.executeQuery("SELECT 1");
			return true;
			
		} catch (SQLException e) {
			plugin.getLogger().warning("데이터베이스 연결 상태를 확인할 수 없습니다: " + e.getMessage());
			return false;
		}
	}
}
