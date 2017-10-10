package com.contpaqi.clasificador.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.contpaqi.clasificador.naiveBayes.Classifier;
import com.contpaqi.clasificador.dbConnection.DBConnection;

public class ClassWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					if (args.length != 0) {
						ClassWindow window = new ClassWindow(args[0]);
						window.frame.setVisible(true);
					} else {
						System.err.println("Debe especificar la IP del servidor SQL");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClassWindow(String ip) {
		initialize();
		initComponents();
		initElements(ip);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 850, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
	}

	private void initComponents() {
		this.jfchModel = new JFileChooser();
		this.jfchModel.setFileFilter(new FileNameExtensionFilter("Modelo de clasificador", "bin"));
		this.jfchTrain = new JFileChooser();
		this.jfchTrain.setFileFilter(new FileNameExtensionFilter("Archivo de entrenamiento", "train"));
		this.contentPanel = new JPanel();
		this.contentPanel.setLayout(null);

		this.jtxfInput = new JTextField();
		this.jtxfInput.setSize(300, 20);
		this.jtxfInput.setLocation(10, 10);

		this.jtxaResult = new JTextArea();
		this.jscpTextResult = new JScrollPane(this.jtxaResult);
		this.jscpTextResult.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.jscpTextResult.setSize(450, 130);
		this.jscpTextResult.setLocation(10, 105);

		this.lstModelClasses = new DefaultListModel<String>();
		this.jlstClasses = new JList<String>(this.lstModelClasses);
		this.jlstClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jscpModelClasses = new JScrollPane(this.jlstClasses);
		this.jscpModelClasses.setSize(300, 130);
		this.jscpModelClasses.setLocation(520, 110);

		this.lstModelClassResult = new DefaultListModel<String>();
		this.jlstClassResult = new JList<String>(this.lstModelClassResult);
		this.jlstClassResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.jscpModelClassResult = new JScrollPane(this.jlstClassResult);
		this.jscpModelClassResult.setSize(450, 60);
		this.jscpModelClassResult.setLocation(10, 40);

		this.jlstClassResult.addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if (!e.getValueIsAdjusting()) {
					@SuppressWarnings("unchecked")
					JList<String> source = (JList<String>) e.getSource();
					if (!source.isSelectionEmpty())
						getCategoriesFromDB(source.getSelectedValue().toString());
				}
			}
		});

		this.jbttFindClass = new JButton("Buscar");
		this.jbttFindClass.setSize(100, 20);
		this.jbttFindClass.setLocation(520, 10);
		this.jbttFindClass.setEnabled(false);
		this.jbttFindClass.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				buscarActionPerformed(e);
			}
		});

		this.jbttLoadModel = new JButton("Cargar modelo");
		this.jbttLoadModel.setSize(150, 20);
		this.jbttLoadModel.setLocation(630, 10);
		this.jbttLoadModel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadModelActionPerformed(e);
			}
		});

		this.jbttListarClases = new JButton("Listar clases");
		this.jbttListarClases.setSize(150, 20);
		this.jbttListarClases.setLocation(630, 40);
		this.jbttListarClases.setEnabled(false);
		this.jbttListarClases.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				listarClasesActionPerformed(e);
			}
		});

		this.jbttEntrenar = new JButton("Entrenar");
		this.jbttEntrenar.setSize(100, 20);
		this.jbttEntrenar.setLocation(520, 40);
		this.jbttEntrenar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				entrenarActionPerformed(e);
			}
		});

		this.jbttSaveModel = new JButton("Salvar");
		this.jbttSaveModel.setSize(100, 20);
		this.jbttSaveModel.setLocation(520, 70);
		this.jbttSaveModel.setEnabled(false);
		this.jbttSaveModel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				saveModelActionPerformed(e);
			}
		});

		this.contentPanel.add(this.jtxfInput);
		this.contentPanel.add(this.jscpTextResult);
		this.contentPanel.add(this.jbttFindClass);
		this.contentPanel.add(this.jbttLoadModel);
		this.contentPanel.add(this.jbttEntrenar);
		this.contentPanel.add(this.jbttListarClases);
		this.contentPanel.add(this.jscpModelClasses);
		this.contentPanel.add(this.jscpModelClassResult);
		this.contentPanel.add(this.jbttSaveModel);

		this.frame.setContentPane(this.contentPanel);
	}

	private void initElements(String ip) {
		this.connection = new DBConnection(ip);
		this.connection.openConnection();
		this.clasificador = new Classifier();
	}

	private void buscarActionPerformed(ActionEvent event) {
		this.lstModelClassResult.clear();
		for (String item : this.clasificador.findClasses(jtxfInput.getText())) {
			this.lstModelClassResult.addElement(item);
		}
	}

	private void getCategoriesFromDB(String codigo) {
		this.jtxaResult.setText("");
		ResultSet resultado = connection.getItems(Integer.parseInt(codigo.trim()));
		String cadenaTmp = "";
		try {
			while (resultado.next()) {
				// System.out.println("Elemento : " + resultado.getInt("Codigo") + " " +
				// resultado.getString("Valor"));
				cadenaTmp += resultado.getInt("Codigo") + " " + resultado.getString("Valor") + "\n";

			}

			this.jtxaResult.setText(cadenaTmp);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void loadModelActionPerformed(ActionEvent event) {
		int returnValue = this.jfchModel.showOpenDialog(this.frame);
		File fileModel = null;
		if (returnValue != JFileChooser.APPROVE_OPTION) {
			fileModel = new File(
					"/home/tostisoft/opennlp-models/myModels" + File.separator + "es-sat-classifier-naive.bin");
		} else {
			fileModel = jfchModel.getSelectedFile();
		}

		if (!fileModel.exists()) {
			JOptionPane.showMessageDialog(this.frame, "Modelo no se encuentra, debe entrenar un nuevo modelo",
					"Warning!!!", JOptionPane.WARNING_MESSAGE);
		} else {
			try {
				clasificador.loadModel(fileModel);
				JOptionPane
						.showMessageDialog(this.frame,
								"Modelo cargado!!" + "/home/tostisoft/opennlp-models/myModels" + File.separator
										+ "es-sat-classifier-naive.bin",
								"Information!!", JOptionPane.INFORMATION_MESSAGE);
				this.jbttFindClass.setEnabled(true);
				this.jbttListarClases.setEnabled(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void entrenarActionPerformed(ActionEvent e) {
		if (this.clasificador == null) {
			this.clasificador = new Classifier();
		}
		if (this.jfchTrain.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
			this.clasificador.train(this.jfchTrain.getSelectedFile());
			JOptionPane.showMessageDialog(this.frame, "Modelo generado!!", "Information!!",
					JOptionPane.INFORMATION_MESSAGE);
			this.jbttFindClass.setEnabled(true);
			this.jbttListarClases.setEnabled(true);
			this.jbttSaveModel.setEnabled(true);
		}
	}

	private void listarClasesActionPerformed(ActionEvent e) {
		this.lstModelClasses.clear();
		for (String item : this.clasificador.listTokens()) {
			System.out.println("Elemento a desplegar " + item.trim());
			try {
				this.lstModelClasses.addElement(item + "-" + connection.getItem(Integer.parseInt(item.trim())));
			} catch (NumberFormatException ex) {
				System.err.println("Error al convertir "+item +" " + e.toString());
			}
		}
		System.out.println("Total de clases " + this.lstModelClasses.getSize());
	}

	private void saveModelActionPerformed(ActionEvent evt) {
		try {
			if (this.jfchModel.showSaveDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
				this.clasificador.saveModel(this.jfchModel.getSelectedFile());
				JOptionPane.showMessageDialog(this.frame,
						"Modelo guardado!!" + this.jfchModel.getSelectedFile().getAbsolutePath(), "Information!!",
						JOptionPane.INFORMATION_MESSAGE);
				this.jbttSaveModel.setEnabled(false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JTextField jtxfInput;
	private JTextArea jtxaResult;
	private JScrollPane jscpTextResult;
	private JList<String> jlstClasses;
	private DefaultListModel<String> lstModelClasses;
	private JScrollPane jscpModelClasses;

	private JList<String> jlstClassResult;
	private DefaultListModel<String> lstModelClassResult;
	private JScrollPane jscpModelClassResult;

	private JButton jbttFindClass;
	private JButton jbttLoadModel;
	private JButton jbttEntrenar;
	private JButton jbttListarClases;
	private JButton jbttSaveModel;
	private JPanel contentPanel;
	private DBConnection connection;
	private Classifier clasificador;

	private JFileChooser jfchModel;
	private JFileChooser jfchTrain;
}
