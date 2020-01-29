package pakiet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MainPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	MainWindow parent;
	JButton zalogujButton = new JButton("Zaloguj");
	JButton wyjdzButton = new JButton("WyjdŸ");
	JLabel loginLabel = new JLabel("Login:");
	JLabel hasloLabel = new JLabel("Has³o:");
	JTextField loginField = new JTextField(10);
	JPasswordField hasloField = new JPasswordField(10);
	DelDAO dataAccess;

	public MainPanel(MainWindow parent) {
		super();
		this.parent = parent;
		dataAccess = new DelDAO();
		this.add(loginLabel);
		this.add(loginField);
		this.add(hasloLabel);
		this.add(hasloField);
		this.add(zalogujButton);
		this.add(wyjdzButton);
		zalogujButton.addActionListener(this);
		wyjdzButton.addActionListener(this);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if (source == wyjdzButton) {
			parent.zamknij();
		}
		String password = new String(hasloField.getPassword());
		if (source == zalogujButton) {
			if (loginField.getText().equals("") ||password.equals(""))
				JOptionPane.showMessageDialog(this, "Has³o i login nie mog¹ byæ puste!", "B£¥D!",
						JOptionPane.ERROR_MESSAGE);
			else if (dataAccess.logowanie(loginField.getText(), password)) {
				JOptionPane.showMessageDialog(this, "Zalogowano jako " + loginField.getText(), "ZALOGOWANO!",
						JOptionPane.OK_CANCEL_OPTION);
				if (dataAccess.getTrybDostepu() == TrybDostepu.KLIENT) {
					this.setVisible(false);
					parent.loginUzytkownika = loginField.getText();
					parent.setSize(new Dimension(700,600));
					parent.setContentPane(new ClientPanel(parent, dataAccess));
				}
				if (dataAccess.getTrybDostepu() == TrybDostepu.PRACOWNIK) {
					this.setVisible(false);
					parent.loginUzytkownika = loginField.getText();
					parent.setSize(new Dimension(700,600));
					parent.setContentPane(new EmployeePanel(parent, dataAccess));
				}
			} else
				JOptionPane.showMessageDialog(this, "Niepoprawny login i has³o", "B£¥D!", JOptionPane.ERROR_MESSAGE);
		}
	}

}
