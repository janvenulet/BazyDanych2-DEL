package pakiet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

enum TrybDostepu {
	PRACOWNIK("Pracownik"), KLIENT("Klient"), LOGOWANIE("Logowanie");

	private String trybDostepu;

	TrybDostepu(String tryb) {
		this.trybDostepu = tryb;
	}

	@Override
	public String toString() {
		return this.trybDostepu;
	}
}

public class DelDAO {

	static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	static final String DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
	private final static String DBUSER_LOGOWANIE = "LOGOWANIE";
	private final static String DBPASS_LOGOWANIE = "LOGPASS";
	private final static String DBUSER_KLIENCI = "KLIENCI";
	private final static String DBPASS_KLIENCI = "KLIPASS";
	private final static String DBUSER_PRACOWNICY = "PRACOWNICY";
	private final static String DBPASS_PRACOWNICY = "PRACPASS";
	private Connection connection = null;
	private Statement statement = null;
	private String query;
	private TrybDostepu trybDostepu = null;

	DelDAO() {
		this.trybDostepu = TrybDostepu.LOGOWANIE;
	}

	public boolean logowanie(String login, String haslo) {
		if (trybDostepu == TrybDostepu.LOGOWANIE) {

			// System.out.println(DB_URL);
			query = "SELECT ID_UZYTKOWNIKA, HASLO, TRYB_DOSTEPU FROM UZYTKOWNICY";
			try {
				Class.forName(JDBC_DRIVER);
				connection = DriverManager.getConnection(DB_URL, DBUSER_LOGOWANIE, DBPASS_LOGOWANIE);
				statement = connection.createStatement();
				ResultSet result = statement.executeQuery(query);
				while (result.next()) {
					String loginTmp = result.getString("ID_UZYTKOWNIKA");
					String hasloTmp = result.getString("HASLO");
					if (login.equals(loginTmp) && haslo.equals(hasloTmp)) { // zalogowa³ siê
						if (result.getInt("TRYB_DOSTEPU") == 1) {
							this.trybDostepu = TrybDostepu.PRACOWNIK;
						} else
							this.trybDostepu = TrybDostepu.KLIENT; // domyœlnie klient
						return true;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException se) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public TrybDostepu getTrybDostepu() {
		return this.trybDostepu;
	}

	public void nowyWniosek(long pesel, int rodzaj, String uzytkownik) throws DelException {
		query = "INSERT INTO Wnioski (id_wniosku, nr_pesel, status, id_rodzaj_wniosku ,id_uzytkownika, data_zlozenia)"
				+ " VALUES (WNIOSKI_SEQ.nextval, " + Long.toString(pesel) + ", \'n\', " + Integer.toString(rodzaj)
				+ ", \'" + uzytkownik + "\' , current_date)";
		System.out.println(query);
		try {
			Class.forName(JDBC_DRIVER);
			if (this.trybDostepu == TrybDostepu.KLIENT)
				connection = DriverManager.getConnection(DB_URL, DBUSER_KLIENCI, DBPASS_KLIENCI);
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			throw new DelException(
					"Najprawdopodobniej nie ma podanego peselu w bazie, lub nast¹pi³ problem z po³¹czeniem!");
		} catch (ClassNotFoundException e) {
			throw new DelException("Nie znaleziono odpowiedniego sterownika JDBC_DRIVER!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public ArrayList<ArrayList<Object>> sprawdzStatus(String uzytkownik) throws DelException {
		query = "SELECT * FROM WNIOSKI WHERE ID_UZYTKOWNIKA = " + "\'" + uzytkownik + "\'";
		System.out.println(query);
		ResultSet result = null;
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		try {
			Class.forName(JDBC_DRIVER);
			if (this.trybDostepu == TrybDostepu.KLIENT)
				connection = DriverManager.getConnection(DB_URL, DBUSER_KLIENCI, DBPASS_KLIENCI);
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			while (result.next()) {
				String IDTmp = result.getString("ID_WNIOSKU");
				String loginTmp = result.getString("ID_UZYTKOWNIKA");
				Long peselTmp = result.getLong("NR_PESEL");
				String statusTmp = result.getString("STATUS");
				ArrayList<Object> tmpList = new ArrayList<Object>();
				tmpList.add(IDTmp);
				tmpList.add(loginTmp);
				tmpList.add(peselTmp);
				tmpList.add(statusTmp);
				data.add(tmpList);
			}
		} catch (SQLException e) {
			throw new DelException("Wyst¹pi³ b³¹d podczas po³¹czenia z baz¹!");
		} catch (ClassNotFoundException e) {
			throw new DelException("Nie znaleziono odpowiedniego sterownika JDBC_DRIVER!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return data;
	}

	public Osoba szukajOsoby(long pesel) throws DelException {
		query = "SELECT * FROM OSOBY WHERE NR_PESEL = " + String.valueOf(pesel);
		System.out.println(query);
		Osoba osobaWynik = new Osoba();
		try {
			Class.forName(JDBC_DRIVER);

			if (this.trybDostepu == TrybDostepu.KLIENT)
				connection = DriverManager.getConnection(DB_URL, DBUSER_KLIENCI, DBPASS_KLIENCI);
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				Long peselTmp = result.getLong("NR_PESEL");
				String imieTmp = result.getString("IMIE");
				String drugieImieTmp = result.getString("DRUGIE_IMIE");
				String nazwiskoTmp = result.getString("NAZWISKO");
				String nazwiskoRodoweTmp = result.getString("NAZWISKO_RODOWE");
				String plecTmp = result.getString("PLEC");
				osobaWynik.setPesel(peselTmp);
				osobaWynik.setImie(imieTmp);
				osobaWynik.setDrugieImie(drugieImieTmp);
				osobaWynik.setNazwisko(nazwiskoTmp);
				osobaWynik.setNazwiskoRodowe(nazwiskoRodoweTmp);
				osobaWynik.setPlec(plecTmp);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DelException(
					"Najprawdopodobniej nie ma podanego peselu w bazie, lub nast¹pi³ problem z po³¹czeniem!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return osobaWynik;
	}

	public ArrayList<ArrayList<Object>> wyswietlWnioski(String status) throws DelException {
		if (status == null)
			query = "SELECT * FROM WNIOSKI";
		else
			query = "SELECT * FROM WNIOSKI WHERE STATUS = " + "\'" + status + "\'";
		System.out.println(query);
		ResultSet result = null;
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		try {
			Class.forName(JDBC_DRIVER);
			if (this.trybDostepu == TrybDostepu.KLIENT)
				connection = DriverManager.getConnection(DB_URL, DBUSER_KLIENCI, DBPASS_KLIENCI);
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			while (result.next()) {
				String IDTmp = result.getString("ID_WNIOSKU");
				String loginTmp = result.getString("ID_UZYTKOWNIKA");
				Long peselTmp = result.getLong("NR_PESEL");
				String statusTmp = result.getString("STATUS");
				ArrayList<Object> tmpList = new ArrayList<Object>();
				tmpList.add(IDTmp);
				tmpList.add(loginTmp);
				tmpList.add(peselTmp);
				tmpList.add(statusTmp);
				data.add(tmpList);
			}
		} catch (SQLException e) {
			throw new DelException("Wyst¹pi³ b³¹d podczas po³¹czenia z baz¹!");
		} catch (ClassNotFoundException e) {
			throw new DelException("Nie znaleziono odpowiedniego sterownika JDBC_DRIVER!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return data;
	}

	public void rozpatrzWniosek(int idWniosku, String status) throws DelException {
		query = "UPDATE WNIOSKI SET STATUS = \'" + status + "\' WHERE ID_WNIOSKU = " + String.valueOf(idWniosku);
		System.out.println(query);
		try {
			Class.forName(JDBC_DRIVER);
			if (this.trybDostepu == TrybDostepu.KLIENT)
				return; // tu mo¿e byæ problem
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			throw new DelException("Wyst¹pi³ b³¹d podczas po³¹czenia z baz¹!");
		} catch (ClassNotFoundException e) {
			throw new DelException("Nie znaleziono odpowiedniego sterownika JDBC_DRIVER!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void nowaOsoba(Osoba osoba) throws DelException {
		query = "INSERT INTO OSOBY (nr_pesel, imie, drugie_imie, nazwisko, nazwisko_rodowe, plec) values ("
				+ String.valueOf(osoba.getPesel()) + ", \'" + osoba.getImie() + "\', \'" + osoba.getDrugieImie()
				+ "\', \'" + osoba.getNazwisko() + "\', \'" + osoba.getNazwiskoRodowe() + "\', \'" + osoba.plec
				+ "\' )";
		System.out.println(query);
		try {
			Class.forName(JDBC_DRIVER);
			if (this.trybDostepu == TrybDostepu.KLIENT)
				return; // tu mo¿e byæ problem
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			throw new DelException("Prawdopodobnie podano ju¿ istniej¹cy numer PESEL!");
		} catch (ClassNotFoundException e) {
			throw new DelException("Nie znaleziono odpowiedniego sterownika JDBC_DRIVER!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void edytujOsobe(Osoba osoba, long peselStary) throws DelException {
		query = "UPDATE OSOBY SET NR_PESEL = " + String.valueOf(osoba.getPesel()) + ", IMIE = \'" + osoba.getImie()
				+ "\', DRUGIE_IMIE = \'" + osoba.getDrugieImie() + "\', NAZWISKO = \'" + osoba.getNazwisko()
				+ "\', NAZWISKO_RODOWE = \'" + osoba.getNazwiskoRodowe() + "\', PLEC = \'" + osoba.getPlec()
				+ "\' WHERE NR_PESEL = " + String.valueOf(peselStary);
		System.out.println(query);
		try {
			Class.forName(JDBC_DRIVER);
			if (this.trybDostepu == TrybDostepu.KLIENT)
				return; // tu mo¿e byæ problem
			else if (this.trybDostepu == TrybDostepu.PRACOWNIK)
				connection = DriverManager.getConnection(DB_URL, DBUSER_PRACOWNICY, DBPASS_PRACOWNICY);
			statement = connection.createStatement();
			statement.executeUpdate(query);
//			query = "INSERT INTO OSOBY (nr_pesel, imie, drugie_imie, nazwisko, nazwisko_rodowe, plec) values ("
//					+ String.valueOf(osoba.getPesel()) + ", \'" + osoba.getImie() + "\', \'" + osoba.getDrugieImie()
//					+ "\', \'" + osoba.getNazwisko() + "\', \'" + osoba.getNazwiskoRodowe() + "\', \'" + osoba.plec
//					+ "\' )";
//			statement.executeUpdate(query);
			System.out.println(query);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DelException("Prawdopodobnie podano ju¿ istniej¹cy numer PESEL!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new DelException("Nie znaleziono odpowiedniego sterownika JDBC_DRIVER!");
		}
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se) {
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}