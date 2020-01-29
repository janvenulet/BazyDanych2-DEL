package pakiet;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow extends JFrame{

	private static final long serialVersionUID = 1L;

	MainPanel mainPanel;
	public String loginUzytkownika; 
	
	public MainWindow() {
		super("Dzial Ewidencji Ludnosci 5.0 - Venko Solutions Inc. ");
		this.setSize(new Dimension(200,120));
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainPanel = new MainPanel(this);
		this.setContentPane(mainPanel);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new MainWindow();
	}
	
	public void zamknij() {
		this.dispose();
	}

}
