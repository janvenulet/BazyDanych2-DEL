package pakiet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ClientPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	MainWindow parent;
	JButton wniosekButton = new JButton("Z≥Ûø nowy wniosek");
	JButton wyszukajButton = new JButton("Wyszukaj osobÍ");
	JButton statusButton = new JButton("Sprawdz status swoich wnioskow");
	JButton wyjdzButton = new JButton("Wyjdz");
	static Object[] columnNames = { "ID Wniosku", "Wnioskodawca", "Pesel", "Status" };
	static Object[][] tableData = null;
	DefaultTableModel defaultTableModel = new DefaultTableModel(columnNames, 0);
	JTable tablica = new JTable(defaultTableModel);
	JScrollPane scrollPane = new JScrollPane(tablica);
	DelDAO dataAccess;

	public ClientPanel(MainWindow parent, DelDAO dataAccess) {
		super();
		this.parent = parent;
		this.dataAccess = dataAccess;
		this.add(wniosekButton);
		this.add(statusButton);
		this.add(wyszukajButton);
		this.add(wyjdzButton);
		scrollPane.setPreferredSize(new Dimension(500, 480));
		scrollPane.setBorder(BorderFactory.createTitledBorder("Wyniki wyszukiwania:"));
		this.add(scrollPane);
		wyszukajButton.addActionListener(this);
		wyjdzButton.addActionListener(this);
		wniosekButton.addActionListener(this);
		statusButton.addActionListener(this);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		clearTable();
		if (source == wyjdzButton) {
			parent.zamknij();
		}
		if (source == statusButton) {
			ArrayList<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();
			try {
				dataList = this.dataAccess.sprawdzStatus(parent.loginUzytkownika);
			} catch (DelException e) {
				e.printStackTrace();
			}
			Object[][] data = new Object[dataList.size()][3];
			int i = 0;
			for (ArrayList<Object> list : dataList) {
				Object[] row = list.toArray(new Object[list.size()]);
				data[i] = row;
				i++;
				defaultTableModel.addRow(row);
			}
		}

		if (source == wniosekButton)

		{
			long peselInt;
			int rodzajInt;
			String peselString = JOptionPane.showInputDialog(this,
					"Podaj numer PESEL osoby, ktÛrej wniosek bÍdzie dotyczy≥", "Z≥Ûø wniosek",
					JOptionPane.QUESTION_MESSAGE);
			if (peselString == null || peselString.equals(""))
				return;
			try {
				peselInt = Long.parseLong(peselString);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "èle sformatowano podany PESEL", "B£•D!",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			Object[] opcje = { "Zmiana nazwiska osoby", "Zmiana imienia osoby", "Zmiana drugiego imienia osoby" };
			String rodzajString = (String) JOptionPane.showInputDialog(this, "Wybierz rodzaj skladanego wniosku",
					"Rodzaj wniosku", JOptionPane.PLAIN_MESSAGE, null, opcje, 0);
			switch (rodzajString) {
			case "Zmiana nazwiska osoby":
				rodzajInt = 1;
				break;
			case "Zmiana imienia osoby":
				rodzajInt = 2;
				break;
			case "Zmiana drugiego imienia osoby":
				rodzajInt = 3;
				break;
			default:
				rodzajInt = 1;
			}
			try {
				this.dataAccess.nowyWniosek(peselInt, rodzajInt, parent.loginUzytkownika);
			} catch (DelException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "DEL EXCEPTION!", JOptionPane.ERROR_MESSAGE);
			}
		}

		if (source == wyszukajButton) {
			String peselString = JOptionPane.showInputDialog(this, "Podaj numer PESEL szukanej osoby: ",
					"Wyszukaj osobÍ", JOptionPane.QUESTION_MESSAGE);
			if (peselString == null || peselString.equals(""))
				return;
			long pesel;
			try {
				pesel = Long.parseLong(peselString);
				Osoba osobaWynik = dataAccess.szukajOsoby(pesel);
				new OsobaDialog(parent,osobaWynik, false, dataAccess); // false odnoúnie nie edytuje
			} catch (NumberFormatException e) {
			} catch (DelException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "DEL EXCEPTION!", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	public void clearTable() {
		int i = defaultTableModel.getRowCount();
		while (0 < defaultTableModel.getRowCount()) {
			i--;
			defaultTableModel.removeRow(i);
		}
	}

}
