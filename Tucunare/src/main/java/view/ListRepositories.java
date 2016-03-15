package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import model.CustomTableModel;

public class ListRepositories implements ActionListener{

	private JFrame frame;
	private JTable table;
	private JButton btnCancelar;
	private JButton btnProcessar;
	private String selectedRepositories = "";

	public static void main(String[] args) {
		Object[][] data = { {"1", "bugpredict", "Limeira","10"},
				{"2", "katello", "maria","200"},
				{"3", "akka", "joao","50"}};
		
		new ListRepositories(data);
	}

	public ListRepositories(Object[][] data) {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		CustomTableModel model = new CustomTableModel(data);
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table);

		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		btnProcessar = new JButton("Processar");
		btnProcessar.addActionListener(this);
		panel.add(btnProcessar);

		btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(this);
		panel.add(btnCancelar);

		frame.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancelar)
			frame.dispose();
		else
			if (e.getSource() == btnProcessar){
				CustomTableModel ctm = (CustomTableModel) table.getModel(); 
				selectedRepositories = ctm.getSelectedRepositories(table.getSelectedRows());
			}
	}

}
