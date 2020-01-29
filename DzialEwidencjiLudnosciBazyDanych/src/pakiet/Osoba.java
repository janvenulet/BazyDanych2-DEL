package pakiet;

public class Osoba {
	String imie;
	String drugieImie;
	String nazwisko;
	String nazwiskoRodowe;
	String plec;
	long pesel;
	
	
	public Osoba(String imie,String drugieImie,String nazwisko,String nazwiskoRodowe,String plec,long pesel) {
		this.setImie(imie);
		this.setDrugieImie(drugieImie);
		this.setNazwisko(nazwisko);
		this.setNazwiskoRodowe(nazwiskoRodowe);
		this.setPlec(plec);
		this.setPesel(pesel);
	}
	
	public Osoba() {
		
	}
	
	public String getImie() {
		return imie;
	}
	public void setImie(String imie) {
		this.imie = imie;
	}
	public String getDrugieImie() {
		return drugieImie;
	}
	public void setDrugieImie(String drugieImie) {
		this.drugieImie = drugieImie;
	}
	public String getNazwisko() {
		return nazwisko;
	}
	public void setNazwisko(String nazwisko) {
		this.nazwisko = nazwisko;
	}
	public String getNazwiskoRodowe() {
		return nazwiskoRodowe;
	}
	public void setNazwiskoRodowe(String nazwiskoRodowe) {
		this.nazwiskoRodowe = nazwiskoRodowe;
	}
	public long getPesel() {
		return pesel;
	}
	public void setPesel(long pesel) {
		this.pesel = pesel;
	}
	public String getPlec() {
		return plec;
	}
	public void setPlec(String plec) {
		this.plec = plec;
	}
}
