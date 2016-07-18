package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class AboutWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AboutWindow(){
		setTitle("Sobre");
		getContentPane().setLayout(new BorderLayout());

		JPanel painelOeste = new JPanel();
		painelOeste.setLayout(new BorderLayout(0, 0));

		JLabel lb = new JLabel(getImage());
		painelOeste.add(lb);
		getContentPane().add(painelOeste, BorderLayout.WEST);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JTextPane txtFerramenta = new JTextPane();
		txtFerramenta.setEditable(false);
		txtFerramenta.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		txtFerramenta.setText("Ferramenta para tratamento e recuperação de informações contidas em repositórios de dados.\r\n\r\nTrabalho de Conclusão de Curso - II");
		panel.add(txtFerramenta, BorderLayout.CENTER);

		JTextPane txtDesen = new JTextPane();
		txtDesen.setEditable(false);
		txtDesen.setFont(new Font("Times New Roman", Font.ITALIC, 14));
		txtDesen.setText("Desenvolvedores\r\nManoel Limeira de Lima Júnior\r\nVitor Lucas Pires Cordovil");
		panel.add(txtDesen, BorderLayout.SOUTH);
		
		StyledDocument docTextFerramenta = txtFerramenta.getStyledDocument();
		SimpleAttributeSet justificado = new SimpleAttributeSet();
		StyleConstants.setAlignment(justificado, StyleConstants.ALIGN_JUSTIFIED);
		docTextFerramenta.setParagraphAttributes(0, docTextFerramenta.getLength(), justificado, false);
		
		StyledDocument docTxtDesen = txtDesen.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		docTxtDesen.setParagraphAttributes(0, docTxtDesen.getLength(), center, false);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
	}

	private ImageIcon getImage() {
		String diretorio = "/view/lucas.jpg";

		try {

			java.net.URL resource = getClass().getResource(diretorio);
			File file = new File(resource.toURI());
			//setando o icone
			ImageIcon logo = new ImageIcon(file.getPath());

			return logo;  

		} catch (URISyntaxException ex) {
			System.err.println("Erro ao tentar recuperar a imagem de perfil.");
		}
		return new ImageIcon();
	}
}
