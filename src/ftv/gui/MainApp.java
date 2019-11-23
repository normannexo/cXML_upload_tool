//git version
package ftv.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import ftv.types.InvoiceFile;

public class MainApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel buttonPane;
	private JFileChooser fileChooser;
	private JTable table;
	private ArrayList<InvoiceFile> alFiles = new ArrayList<InvoiceFile>();

	private File[] files;
	protected FileTableModel ftm;
	private JButton btnSend, btnDismiss;
	private String strUrl, strProxy;
	private JLabel label;
	private JPanel southPane;
	private String propertiesPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApp frame = new MainApp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainApp() {
		Properties properties = new Properties();
		BufferedInputStream stream;
		
		try {
			File jarPath=new File(MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	        propertiesPath = jarPath.getParentFile().getAbsolutePath();
	        System.out.println(" propertiesPath-"+propertiesPath);
			stream = new BufferedInputStream(new FileInputStream(propertiesPath + "/cxml_tool.properties"));
			properties.load(stream);
			strUrl = properties.getProperty("url");
			strProxy = properties.getProperty("proxyhost");
			System.out.println(strUrl);
			try {
				new URL(strUrl);
				
			} catch (Exception e) {
				strUrl = "URL nicht korrekt: " + strUrl;
			}
			
			stream.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			strUrl = "keine Propertiesdatei!";
		}
		
		
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setTitle("cXML Upload Tool");
		
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e){
        
        }
		fileChooser = new JFileChooser();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileChooser.setMultiSelectionEnabled(true);
				FileFilter filefilter = new FileNameExtensionFilter("xml-Files", "xml", "cxml", "txt");
				fileChooser.setFileFilter(filefilter);
				fileChooser.showOpenDialog(MainApp.this);
				files = fileChooser.getSelectedFiles();
				fillFiles(files);
				
				
				

			}
		});
		mnNewMenu.add(mntmOpen);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				if (me.getClickCount() == 2) {
					int row = table.rowAtPoint(me.getPoint());
					System.out.println(alFiles.get(row).getFile().getName());
					int col = table.columnAtPoint(me.getPoint());
					Desktop desktop = Desktop.getDesktop();
					File desktopfile;
		        	try {
		        		if (col == 2 || col == 3) {
		        			desktopfile = alFiles.get(row).getResponseFile();
		        		} else {
		        			desktopfile = alFiles.get(row).getFile();
		        		} 
		        		if (desktopfile != null && desktopfile.exists()) {
		        			desktop.open(desktopfile);
		        		}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		contentPane.add(new JScrollPane(table), BorderLayout.CENTER);
		buttonPane = new JPanel();
		label = new JLabel(strUrl);
		southPane = new JPanel();
		southPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		southPane.setLayout(new BorderLayout(0, 0));
		southPane.add(label, BorderLayout.NORTH);
		southPane.add(buttonPane, BorderLayout.SOUTH );
		contentPane.add(southPane, BorderLayout.SOUTH);

		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						send();
					}
				}).start();
			}
			
				
			}
		);
		
		btnDismiss = new JButton("Dismiss Selected");
		btnDismiss.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TableModel tm = table.getModel();
				if (tm instanceof FileTableModel) {
					FileTableModel ftm = (FileTableModel) tm;
					alFiles = ftm.getFiles();
					if (alFiles != null) {
						dismissSent();
					} else {
						return;
					}
				} else {
					return;
				}
			}
		});
		buttonPane.add(btnSend);
		buttonPane.add(btnDismiss);
		checkSend();
		checkDismiss();
		
		new DropTarget(contentPane, new DropTargetListener() {
			@Override
			public void drop(DropTargetDropEvent dtde){
				try {
					Transferable tr = dtde.getTransferable();
					DataFlavor[] flavors = tr.getTransferDataFlavors();
					ArrayList<File> fileNames = new ArrayList<File>();
					for (int i = 0; i < flavors.length; i++) {
						if (flavors[i].isFlavorJavaFileListType()) {
							dtde.acceptDrop(dtde.getDropAction());
							java.util.List<File> fileslist = (java.util.List<File>) tr.getTransferData(flavors[i]);
							File[] files = new File[fileslist.size()];
							for (int k = 0; k< fileslist.size();k++) {
								files[k] = fileslist.get(k);
								System.out.println(fileslist.get(k));
							}
							fillFiles(files);
							dtde.dropComplete(true);
						}
					}
					return;
				} catch (Throwable t) {
					t.printStackTrace();
				}
				dtde.rejectDrop();
			};
			
			
			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragOver(DropTargetDragEvent dtde) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dropActionChanged(DropTargetDragEvent dtde) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void dragExit(DropTargetEvent dte) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void dismissSent() {
		for (int i = alFiles.size() - 1; i >= 0; i--) {
			if (alFiles.get(i).isSelected()) {
				alFiles.remove(i);
			}

		}
		ftm.fireTableDataChanged();
		checkSend();
		checkDismiss();
	}

	public void setCheckboxSent() {
		for (int i = alFiles.size() - 1; i >= 0; i--) {
			if (alFiles.get(i).isSelected()) {
				ftm.setValueAt(true, i, 5);
			}

		}
		ftm.fireTableDataChanged();
		checkSend();
		checkDismiss();
	}

	public void checkSend() {
		if (alFiles == null || alFiles.size() == 0) {
			btnSend.setEnabled(false);
		} else {
			btnSend.setEnabled(true);
		}

	}

	public void checkDismiss() {
		if (alFiles == null || alFiles.size() == 0) {
			btnDismiss.setEnabled(false);
		} else {
			boolean containsSent = false;
			for (InvoiceFile file : alFiles) {
				if (file.isSelected()) {
					containsSent = true;

					break;
				}
			}
			btnDismiss.setEnabled(containsSent);

		}

	}

	void send() {
		TableModel tm = table.getModel();
		if (tm instanceof FileTableModel) {
			FileTableModel ftm = (FileTableModel) tm;
			alFiles = ftm.getFiles();
			if (alFiles != null) {

				for (int i = 0; i < alFiles.size(); i++) {
					try {
						InvoiceFile invFile = alFiles.get(i);
						// ftm.fireTableDataChanged();
						PostCXML.send(invFile, strUrl, strProxy);
						// ftm.fireTableDataChanged();
						// ftm.fireTableCellUpdated(i, 3);
						// ftm.setValueAt("torben", 1, 1);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								ftm.fireTableDataChanged();
							}
						});

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				checkDismiss();
				// int dialogButton = JOptionPane.YES_NO_OPTION;
				// int dialogResult = JOptionPane.showConfirmDialog (null,
				// "Erfolgreich versendete aus Liste
				// entfernen?","Warning",dialogButton);
				//
				// if (dialogResult == JOptionPane.YES_OPTION) {
				// dismissSent();
				// }

			} else {
				return;
			}
		} else {
			return;
		}
	}
	
	void fillFiles(File[] files) {
		alFiles = new ArrayList<InvoiceFile>();
		for (int i = 0; i < files.length; i++) {
			alFiles.add(new InvoiceFile(files[i]));

		}
		ftm = new FileTableModel(alFiles, MainApp.this);
		table.setModel(ftm);
	
		ftm.fireTableDataChanged();
		checkSend();
		
		
	}

}