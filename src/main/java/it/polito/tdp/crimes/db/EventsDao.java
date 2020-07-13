package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.crimes.model.Event;
import it.polito.tdp.crimes.model.Giornata;


public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<String> getReati(){
		String sql = "SELECT DISTINCT offense_category_id AS off " + 
				"FROM denver_crimes.`events` " + 
				"ORDER BY off" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<String>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getString("off"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	
	public List<Giornata> getDate(){
		String sql = "SELECT DAY(e.reported_date) AS giorno, MONTH(e.reported_date) AS mese, YEAR(e.reported_date) AS anno " + 
				"FROM denver_crimes.`events` e " + 
				"GROUP BY anno,mese,giorno" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Giornata> list = new ArrayList<Giornata>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(new Giornata(res.getInt("anno"), res.getInt("mese"), res.getInt("giorno")));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

	}

	public List<String> getVertici(String reato, Giornata giorno) {
		String sql = "SELECT DISTINCT e.offense_type_id AS tipo " + 
				"FROM denver_crimes.`events` e " + 
				"WHERE offense_category_id = ? " + 
				"AND DAY(reported_date) = ? " + 
				"AND MONTH(e.reported_date) = ? " + 
				"AND YEAR(e.reported_date) = ? " ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> list = new ArrayList<String>() ;
			
			st.setString(1, reato);
			st.setInt(2, giorno.getGiorno());
			st.setInt(3, giorno.getMese());
			st.setInt(4, giorno.getAnno());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getString("tipo"));
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public Integer getPeso(String tipo1, String tipo2) {
		String sql = "SELECT e1.offense_type_id AS o1, e2.offense_type_id AS o2, COUNT(DISTINCT e1.precinct_id) AS CNT " + 
				"FROM denver_crimes.`events` e1, denver_crimes.`events` e2 " + 
				"WHERE e1.offense_type_id = ? " + 
				"AND e2.offense_type_id = ? " + 
				"AND e1.precinct_id = e2.precinct_id " + 
				"GROUP BY o1, o2" ;
		try {
			Connection conn = DBConnect.getConnection() ;
			Integer peso = 0;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setString(1, tipo1);
			st.setString(2, tipo2);
			
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				peso = res.getInt("CNT");
			}
			
			conn.close();
			return peso;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
}
