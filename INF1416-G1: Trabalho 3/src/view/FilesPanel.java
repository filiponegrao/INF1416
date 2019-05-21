package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import controller.DBManager;
import controller.FileController;

public class FilesPanel extends JPanel {

	private NavigationView navigation;
	private JTable table;
	private JButton backButton;

	private JLabel uploadLabel;
	private JLabel uploadPath;
	private JButton uploadButton;
	private JButton listButton;
	
	private String path = "";

	public FilesPanel(NavigationView navigation, int x, int y, int w, int h) {
		this.navigation = navigation;
		this.setBounds(x, y, w, h);

		DBManager.insereRegistro(8001);

		this.setBounds(x, y, w, h);

		setLayout(null);

		int margin = 20;
		int objectWidth = w-(margin*2);
		int objectHeight = 30;

		// Label username
		this.uploadLabel = new JLabel("Faça o upload do seu certificado");
		this.uploadLabel.setBounds(margin, margin, objectWidth, objectHeight);
		this.add(this.uploadLabel);

		int pathOrigin = margin + objectHeight;

		// Label username
		this.uploadPath = new JLabel("Caminho não especifcado.");
		this.uploadPath.setBounds(margin, pathOrigin, objectWidth, objectHeight);
		this.add(this.uploadPath);

		int uploadOrigin = pathOrigin + objectHeight;

		// Uploado key button
		this.uploadButton = new JButton("Selecionar arquivo");
		this.uploadButton.setBounds(margin, uploadOrigin, objectWidth/2, objectHeight);
		this.uploadButton.addActionListener(this.selectFile());
		this.add(this.uploadButton);
		
		this.listButton = new JButton("Listar");
		this.listButton.setBounds(objectWidth/2+margin, uploadOrigin, objectWidth/2, objectHeight);
		this.listButton.addActionListener(this.listIndex());
		this.add(this.listButton);

		String[] columnNames = {"Nome codigo","Nome secreto", "Dono", "Grupo"};
		Object[][] data = {};
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

		this.table = new JTable(tableModel) {
			public boolean isCellEditable(int nRow, int nCol) {
				return false;
			}
		};

		int tableHeight = h - uploadOrigin - objectHeight - margin*4;
		int tableOrigin = uploadOrigin + objectHeight + margin;

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(margin, tableOrigin, objectWidth, tableHeight);
		table.setFillsViewportHeight(true);



		int buttonOrigin = h - objectHeight - margin;
		// Botao de voltar
		this.backButton = new JButton("Voltar");
		this.backButton.setBounds(margin, buttonOrigin, objectWidth/2, objectHeight);
		this.backButton.addActionListener(this.back());
		this.add(this.backButton);

		this.add(table.getTableHeader());
		this.add(scrollPane);

	}
	
	public ActionListener selectFile() {
		 return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JFileChooser fileChooser = new JFileChooser();
			     fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				 if(fileChooser.showOpenDialog(uploadButton) == JFileChooser.APPROVE_OPTION) {
						path = fileChooser.getSelectedFile().getPath();
					 	uploadPath.setText(path);
				 }	 				
			}
		};
	}

	public ActionListener back() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigation.selectMenu();			
			}
		};
	}
	
	public ActionListener listIndex() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[][] index = FileController.sharedInstance().getFilesIndex(path);
					System.out.println(index);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Erro ao ler arquivo de index");
					e1.printStackTrace();
				}	
			}
		};
	}

}
