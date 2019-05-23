package view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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

	int tableHeight;
	int tableOrigin;
	int margin;
	int objectWidth;

	private int selectedIndex;

	public FilesPanel(NavigationView navigation, int x, int y, int w, int h) {
		this.navigation = navigation;
		this.setBounds(x, y, w, h);

		DBManager.insereRegistro(8001);

		this.setBounds(x, y, w, h);

		setLayout(null);

		this.margin = 20;
		this.objectWidth = w-(margin*2);
		int objectHeight = 30;

		// Label username
		this.uploadLabel = new JLabel("Escolha a pasta que será utilizada:");
		this.uploadLabel.setBounds(margin, margin, objectWidth, objectHeight);
		this.add(this.uploadLabel);

		int pathOrigin = margin + objectHeight;

		// Label username
		this.uploadPath = new JLabel("Caminho não especifcado.");
		this.uploadPath.setBounds(margin, pathOrigin, objectWidth, objectHeight);
		this.add(this.uploadPath);

		int uploadOrigin = pathOrigin + objectHeight;

		// Uploado key button
		this.uploadButton = new JButton("Selecionar pasta");
		this.uploadButton.setBounds(margin, uploadOrigin, objectWidth, objectHeight);
		this.uploadButton.addActionListener(this.buttonFolderClicked());
		this.add(this.uploadButton);

		int listOrigin = uploadOrigin + objectHeight + margin;

		// List button
		this.listButton = new JButton("Listar");
		this.listButton.setBounds(margin, listOrigin, objectWidth, objectHeight);
		this.listButton.addActionListener(this.buttonListClicked());
		this.add(this.listButton);

		this.tableHeight = h - uploadOrigin - objectHeight - margin*4;
		this.tableOrigin = uploadOrigin + objectHeight + margin;		


		int buttonOrigin = h - objectHeight - margin;
		// Botao de voltar
		this.backButton = new JButton("Voltar");
		this.backButton.setBounds(margin, buttonOrigin, objectWidth/2, objectHeight);
		this.backButton.addActionListener(this.buttonBackClicked());
		this.add(this.backButton);

	}

	public void addTable(String[][] data) {

		String[] columnNames = {"Nome codigo","Nome secreto", "Dono", "Grupo"};
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);

		this.table = new JTable(tableModel);
		
		ListSelectionListener listener = new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	            // do some actions here, for example
	            // print first column value from selected row
	        	if (selectedIndex != table.getSelectedRow()) {
	        		String fileName = table.getValueAt(table.getSelectedRow(), 0).toString();
	        		String fileSecret = table.getValueAt(table.getSelectedRow(), 1).toString();
	        		
	        		checkAndOpenFile(fileName, fileSecret);
	        	}
	        	selectedIndex = table.getSelectedRow();
	        }
	    };
	    
		table.getSelectionModel().addListSelectionListener(listener);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(margin, tableOrigin, objectWidth, tableHeight);
		table.setFillsViewportHeight(true);

		this.add(table.getTableHeader());
		this.add(scrollPane);

		this.remove(this.listButton);
	}
	
	public void checkAndOpenFile(String fileName, String fileSecret) {
		
	}

	public ActionListener buttonFolderClicked() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File("./tokens/"));

				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if(fileChooser.showOpenDialog(uploadButton) == JFileChooser.APPROVE_OPTION) {
					path = fileChooser.getSelectedFile().getPath();
					uploadPath.setText(path);
				}	 				
			}
		};
	}

	public ActionListener buttonBackClicked() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigation.selectMenu();			
			}
		};
	}

	public ActionListener buttonListClicked() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[][] data = FileController.sharedInstance().getFilesIndex(path);

					addTable(data);

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Erro ao ler arquivo de index");
					e1.printStackTrace();
				}	
			}
		};
	}

}
