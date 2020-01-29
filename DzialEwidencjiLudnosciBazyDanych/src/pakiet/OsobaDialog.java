package pakiet;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class OsobaDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	Osoba osoba;

	JLabel imieLabel = new JLabel("Imiê:\t ");
	JLabel drugieImieLabel = new JLabel("Drugie imiê: ");
	JLabel nazwiskoLabel = new JLabel("Nazwisko: ");
	JLabel nazwiskoRodoweLabel = new JLabel("Nazwisko Rodowe: ");
	JLabel peselLabel = new JLabel("Numer PESEL: ");
	JLabel plecLabel = new JLabel("P³eæ:\t ");

	JTextField imieField = new JTextField(23);
	JTextField drugieImieField = new JTextField(23);
	JTextField nazwiskoField = new JTextField(23);
	JTextField nazwiskoRodoweField = new JTextField(23);
	JTextField peselField = new JTextField(23);
	JTextField plecField = new JTextField(23);

	JButton zapiszButton = new JButton("Zapisz");
	JButton wyjdzButton = new JButton("Wyjdz");

	DelDAO dataAccess;

	OsobaDialog(Window parent, Osoba osoba, boolean czyEdytuje, DelDAO dataAccess) {
		super(parent, Dialog.ModalityType.DOCUMENT_MODAL); // !!!!!!!!!!!!!!!!!
		this.osoba = osoba;
		this.dataAccess = dataAccess;
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if (osoba == null) {
			this.setTitle("Tworzenie nowej osoby");
			imieField.setEditable(true);
			drugieImieField.setEditable(true);
			nazwiskoField.setEditable(true);
			nazwiskoRodoweField.setEditable(true);
			peselField.setEditable(true);
			plecField.setEditable(true);

		} else {
			imieField.setText(osoba.getImie());
			drugieImieField.setText(osoba.getDrugieImie());
			nazwiskoField.setText(osoba.getNazwisko());
			nazwiskoRodoweField.setText(osoba.getNazwiskoRodowe());
			peselField.setText(String.valueOf(osoba.getPesel()));
			plecField.setText(osoba.getPlec());
			if (czyEdytuje) {
				this.setTitle("Edytowanie osoby " + osoba.getImie() + " " + osoba.getNazwisko());
				imieField.setEditable(true);
				drugieImieField.setEditable(true);
				nazwiskoField.setEditable(true);
				nazwiskoRodoweField.setEditable(true);
				peselField.setEditable(true);
				plecField.setEditable(true);
			} else {
				this.setTitle("Przegladanie osoby " + osoba.getImie() + " " + osoba.getNazwisko());
				imieField.setEditable(false);
				drugieImieField.setEditable(false);
				nazwiskoField.setEditable(false);
				nazwiskoRodoweField.setEditable(false);
				peselField.setEditable(false);
				plecField.setEditable(false);
			}
		}
		this.setSize(300, 400);
		JPanel dialogPanel = new JPanel();
		dialogPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		dialogPanel.add(imieLabel);
		dialogPanel.add(imieField);
		dialogPanel.add(drugieImieLabel);
		dialogPanel.add(drugieImieField);
		dialogPanel.add(nazwiskoLabel);
		dialogPanel.add(nazwiskoField);
		dialogPanel.add(nazwiskoRodoweLabel);
		dialogPanel.add(nazwiskoRodoweField);
		dialogPanel.add(peselLabel);
		dialogPanel.add(peselField);
		dialogPanel.add(plecLabel);
		dialogPanel.add(plecField);

		if (czyEdytuje) {
			dialogPanel.add(zapiszButton);
		}
		
		dialogPanel.add(wyjdzButton);
		zapiszButton.addActionListener(this);
		wyjdzButton.addActionListener(this);
		this.setResizable(false);
		this.setContentPane(dialogPanel);
		dialogPanel.setVisible(true);
		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if (source == wyjdzButton) {
			this.dispose();
		}

		if (source == zapiszButton) {
			try {
				if (osoba == null) {
					if (plecField.getText() != null) {
						if (!plecField.getText().toLowerCase().equals("k")
								|| !plecField.getText().toLowerCase().equals("m"));
						else
							throw new DelException("P³eæ mo¿e przymowaæ wartoœci wy³¹cznie: 'K','k','M' lub 'm'! ");
					}
					osoba = new Osoba(imieField.getText(), drugieImieField.getText(), nazwiskoField.getText(),
							nazwiskoRodoweField.getText(), plecField.getText(), Long.parseLong(peselField.getText()));
					this.dataAccess.nowaOsoba(osoba);
					this.dispose();
				} else {
					long staryPesel = osoba.getPesel();
					osoba = new Osoba(imieField.getText(), drugieImieField.getText(), nazwiskoField.getText(),
							nazwiskoRodoweField.getText(), plecField.getText(), Long.parseLong(peselField.getText()));
					dataAccess.edytujOsobe(osoba, staryPesel);
					this.dispose();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Podano zle sformatowany pesel", "B³¹d", JOptionPane.ERROR_MESSAGE);
				this.dispose();
			} catch (DelException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "B³¹d", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				this.dispose();
			}

//			else {
//				try {
//					long staryPesel = osoba.getPesel();
//					osoba = new Osoba(imieField.getText(), drugieImieField.getText(), nazwiskoField.getText(),
//							nazwiskoRodoweField.getText(), plecField.getText(), Long.parseLong(peselField.getText()));
//					dataAccess.edytujOsobe(osoba, staryPesel);
//				} catch (NumberFormatException e) {
//					JOptionPane.showMessageDialog(this, "Podano zle sformatowany pesel", "B³¹d", JOptionPane.ERROR_MESSAGE);
//					this.dispose();
//				} catch (DelException e) {
//					JOptionPane.showMessageDialog(this, e.getMessage(), "B³¹d", JOptionPane.ERROR_MESSAGE);
//					e.printStackTrace();
//					this.dispose();
//
//				}
//				this.dispose();
//			}
		}
	}

	public Osoba zwrocOsobe() {
		return this.osoba;
	}

}
