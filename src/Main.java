import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Main {
	private static Hashtable<String, String> teamTable = new Hashtable<String, String>();
	private static int i, blankPg, firstRun, zeroResults,rankMinVal,rankMaxVal,buyMinVal,buyMaxVal,sellMinVal,sellMaxVal,errorExists;
	private static String sourceCode, perDifStr;
	private static ArrayList<Element> allMatches = new ArrayList<Element>();
	private static ArrayList<PlayerCard> allCards = new ArrayList<PlayerCard>();
	private static List<Element> allMatchesOnPage = new ArrayList<Element>();
	private static String[] columnNames = {"Ovr", "Name", "Team","Series" ,"Buy Now", "Sell Now", "Buy - Sell (w/Tax)", "% Difference","Pg #"};
	private static String[] comboItems = {"Live Series"};
	private static Object[][] data ={};
    private static DefaultTableModel model = new DefaultTableModel(data,columnNames);
    private static JTable table;
    private static JPanel filterPanel,rankPanel,buyPanel,sellPanel,mainPanel,bottomPanel, seriesPanel;
    private static Float perDif;
    private static float percentDiff;
    private static JFrame frame;
    private static JLabel lastUpdated, updatingLabel,errorLabel;
    private static BufferedReader in;
    private static JButton addButton, filterButton, resetFilterButton, quickRefreshButton;
    private static JTextField rankMin,rankMax,buyMin,buyMax,sellMin,sellMax;
    private static Border redBorder, defBorder;
    private static TableRowSorter<DefaultTableModel> rowSorter;
    private static JComboBox seriesCombo;
    //private static int totalDifference;
    
    public static void main(String[] args) throws IOException{
			teamTable.put("ari","Diamondbacks"); teamTable.put("atl","Braves"); teamTable.put("bal","Orioles"); teamTable.put("bos","Red Sox"); teamTable.put("chc","Cubs"); teamTable.put("cin","Reds"); teamTable.put("cle","Indians"); teamTable.put("col","Rockies"); teamTable.put("cws","White Sox"); teamTable.put("det","Tigers"); teamTable.put("fa","Free Agent"); teamTable.put("hou","Astros"); teamTable.put("kc","Royals"); teamTable.put("laa","Angels"); teamTable.put("lad","Dodgers"); teamTable.put("mia","Marlins"); teamTable.put("mil","Brewers"); teamTable.put("min","Twins"); teamTable.put("nym","Mets"); teamTable.put("nyy","Yankees"); teamTable.put("oak","Athletics"); teamTable.put("phi","Phillies"); teamTable.put("pit","Pirates"); teamTable.put("sd","Padres"); teamTable.put("sea","Mariners"); teamTable.put("sf","Giants"); teamTable.put("stl","Cardinals"); teamTable.put("tb","Rays"); teamTable.put("tex","Rangers"); teamTable.put("wsh","Nationals"); teamTable.put("tor","Blue Jays");
			blankPg=2000;
			//totalDifference=0;
			try {
				setTrustAllCerts();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
			        
					try
			        {
			            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			        }
			        catch(Exception e)
			        {
			            e.printStackTrace();
			        }
					
					JPanel topPanel = new JPanel();
					topPanel.setLayout(new GridLayout(2,2));
					JPanel tablePanel = new JPanel();
					addButton = new JButton("Refresh");
					quickRefreshButton = new JButton("Quick Refresh");
	    		    quickRefreshButton.setEnabled(false);

					filterButton = new JButton("Filter");
					resetFilterButton = new JButton("Reset Filter");
					//JButton removeButton = new JButton("Remove");
			        addButton.setBounds(50,50,90, 50);
			        filterButton.setBounds(50,50,90, 50);  
					
			        

					frame = new JFrame("MLB The Show 17 Market Assistant v1.0");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

					frame.setSize(1200,560);
					//930 521
				    table = new JTable(model){
			            
				    	DefaultTableCellRenderer renderRight = new DefaultTableCellRenderer();
				    	
						private static final long serialVersionUID = 1L;
					    { // initializer block
					        renderRight.setHorizontalAlignment(SwingConstants.LEFT);
					    }
					    @Override
					    public Class<?> getColumnClass(int columnIndex) {
					        /*if (columnIndex == 0 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5 ){
					        	
					        	return Integer.class;
					        }
					        else{
					        	return String.class;
					        }
					    	*/
					    	
					    	switch (columnIndex) {
			                    case 0:
			                        return Integer.class;
			                    case 3:
			                        return Integer.class;
			                    case 4:
			                        return Integer.class;
			                    case 5:
			                        return Integer.class;
			                    case 6:
			                        return String.class;
			                    default:
			                        return String.class;
					    		}
		            
					    }
					    public boolean isCellEditable(int row, int col) {
					         return false;
					    }
					    
					    @Override
					    public TableCellRenderer getCellRenderer (int arg0, int arg1) {
					        return renderRight;
					    }


				    };
				    
				      addButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	
				        	 try {
								model.setRowCount(0);
								allMatches = new ArrayList<Element>();
								allCards = new ArrayList<PlayerCard>();
								allMatchesOnPage = new ArrayList<Element>();
								refreshMethod();
							    DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
							    //get current date time with Date()
							   	long date = System.currentTimeMillis();
							    String dateString = dateFormat.format(date);
								lastUpdated.setText("Last Updated: " + dateString);
								
							} catch (IOException e1) {
								e1.printStackTrace();
							} 
				        	 

				         }
				      });
				      quickRefreshButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	 ArrayList<Integer> pgNums = new ArrayList<Integer>();
				        	 //System.out.println("5");
				             
				        	 listLoop: for(int row = 0;row < table.getModel().getRowCount();row++) {
				                 //System.out.println(table.getModel().getValueAt(row, 1));
				                 
				        		 try{
				        		 pgNums.add((Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), 8));
				        		 }
				        		 catch(ArrayIndexOutOfBoundsException h){
				        			 break listLoop;
				        		 }
				            	 //System.out.println((Integer) table.getModel().getValueAt(table.convertRowIndexToModel(row), 8));
				            	 //System.out.println(row);
				            }
			                 HashSet <Integer> h = new HashSet<Integer>(pgNums);
			                 pgNums.clear();
			                 pgNums.addAll(h);
			                 Collections.sort(pgNums);
			                 
							model.setRowCount(0);
							allMatches = new ArrayList<Element>();
							allCards = new ArrayList<PlayerCard>();
							allMatchesOnPage = new ArrayList<Element>();
				        	rowSorter.setRowFilter(null);

			                 
			                 try {
								quickRefreshMethod(pgNums);
								  List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(6);
								  
								  rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, rankMinVal-1 ,0));
								  rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, rankMaxVal+1 ,0));
								  rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, sellMinVal-1 ,5));
								  rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, sellMaxVal+1 ,5));
								  rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, buyMinVal-1 ,4));
								  rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, buyMaxVal+1 ,4));
								  //sorter.setRowFilter(RowFilter.numberFilter(ComparisonType.AFTER, 80 ,0));
								  //sorter.setRowFilter(RowFilter.numberFilter(ComparisonType.BEFORE, 90 ,0));
								  rowSorter.setRowFilter(RowFilter.andFilter(rfs));
							} catch (IOException e1) {
								
								//e1.printStackTrace();
							}
				         }
				      });
				      filterButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	 int errorExists = 0;

				        	 
				        	 if(sellMin.getText().equals("")){
				        		 sellMin.setText("0");
				        	 }
				        	 
				        	 if(sellMax.getText().equals("")){
				        		 sellMax.setText("9999999");

				        	 }
				        	 if(buyMin.getText().equals("")){
				        		 buyMin.setText("0");

				        	 }
				        	 if(buyMax.getText().equals("")){
				        		 buyMax.setText("9999999");

				        	 }
				        	 if(rankMin.getText().equals("")){
				        		 rankMin.setText("0");

				        	 }
				        	 if(rankMax.getText().equals("")){
				        		 rankMax.setText("99");
				        	 }

				        	 sellMinVal=Integer.parseInt(sellMin.getText());
				        	 sellMaxVal=Integer.parseInt(sellMax.getText());
				        	 rankMinVal=Integer.parseInt(rankMin.getText());
				        	 rankMaxVal=Integer.parseInt(rankMax.getText());
				        	 buyMinVal=Integer.parseInt(buyMin.getText());
				        	 buyMaxVal=Integer.parseInt(buyMax.getText());
				        	 errorLabel.setVisible(false);
				        	 
			        		 buyMin.setBorder(defBorder);
			        		 buyMax.setBorder(defBorder);
			        		 sellMin.setBorder(defBorder);
			        		 sellMax.setBorder(defBorder);
			        		 rankMin.setBorder(defBorder);
			        		 rankMax.setBorder(defBorder);
			        		 
				        	 if(sellMinVal>sellMaxVal){
				        		 sellMin.setBorder(redBorder);
				        		 sellMax.setBorder(redBorder);
				        		 errorExists = 1;
				        	 }
				        	 if(buyMinVal>buyMaxVal){
				        		 buyMin.setBorder(redBorder);
				        		 buyMax.setBorder(redBorder);
				        		 errorExists = 1;

				        	 }
				        	 if(rankMinVal>rankMaxVal){
				        		 rankMin.setBorder(redBorder);
				        		 rankMax.setBorder(redBorder);
				        		 errorExists = 1;
				        	 }
				        	 
				        	 if(errorExists==0){
				        	      List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(6);
				        	      
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, rankMinVal-1 ,0));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, rankMaxVal+1 ,0));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, sellMinVal-1 ,5));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, sellMaxVal+1 ,5));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.AFTER, buyMinVal-1 ,4));
				        	      rfs.add(RowFilter.numberFilter(ComparisonType.BEFORE, buyMaxVal+1 ,4));
				        	      //sorter.setRowFilter(RowFilter.numberFilter(ComparisonType.AFTER, 80 ,0));
				        	      //sorter.setRowFilter(RowFilter.numberFilter(ComparisonType.BEFORE, 90 ,0));
				        	      rowSorter.setRowFilter(RowFilter.andFilter(rfs));
				        	      
				        	 }
				        	 if(errorExists==1){
				        		 errorLabel.setVisible(true);
				        	 }
				         }
				      });
				      
				      resetFilterButton.addActionListener(new ActionListener()
				      {
				         public void actionPerformed(ActionEvent e)
				         {
				        	  rowSorter.setRowFilter(null);
				         }
				      });
				      
				    topPanel.add(addButton);
				    topPanel.add(quickRefreshButton);
				    topPanel.add(filterButton);
				    topPanel.add(resetFilterButton);

				    mainPanel = new JPanel();
				    
				    filterPanel = new JPanel();
				    
				    bottomPanel = new JPanel();
				    bottomPanel.setPreferredSize(new Dimension(1200,20));
				    rankPanel = new JPanel();
				    rankPanel.setBorder(BorderFactory.createTitledBorder("Ovr"));
				    
				    buyPanel = new JPanel();
				    buyPanel.setBorder(BorderFactory.createTitledBorder("Buy Now"));
				    
				    sellPanel = new JPanel();
				    sellPanel.setBorder(BorderFactory.createTitledBorder("Sell Now"));
				    
				    seriesPanel = new JPanel();
				    seriesPanel.setBorder(BorderFactory.createTitledBorder("Card Series"));
				    seriesCombo = new JComboBox<String>(comboItems);
				    seriesPanel.add(seriesCombo);
				    
				    
				    
				    filterPanel.add(rankPanel);
				    
				    
				    JPanel donationPanel = new JPanel();
				    donationPanel.setLayout(new BorderLayout());
				    
				    
				    rankMin = new JTextField("0");
				    rankMax = new JTextField("99");
				    
				    redBorder=BorderFactory.createLineBorder(Color.red);
				    defBorder=rankMin.getBorder();
				    
				    rankMin.setPreferredSize(new Dimension(30,25));
				    rankMax.setPreferredSize(new Dimension(30,25));
				    
				    JLabel rankMinLabel = new JLabel("Min:");
				    JLabel rankMaxLabel = new JLabel("Max:");
				    JLabel rankDashLabel = new JLabel(" - ");

				    rankPanel.add(rankMinLabel);
				    rankPanel.add(rankMin);
				    rankPanel.add(rankDashLabel);
				    rankPanel.add(rankMaxLabel);
				    rankPanel.add(rankMax);
				   
				    
				    filterPanel.add(buyPanel);
				    buyMin = new JTextField("0");
				    buyMax = new JTextField("9999999");
				    
				    
				    
				    
				    buyMin.setPreferredSize(new Dimension(80,25));
				    buyMax.setPreferredSize(new Dimension(80,25));

				    JLabel buyMinLabel = new JLabel("Min:");
				    JLabel buyMaxLabel = new JLabel("Max:");
				    JLabel buyDashLabel = new JLabel(" - ");
				    
				    buyPanel.add(buyMinLabel);
				    buyPanel.add(buyMin);
				    buyPanel.add(buyDashLabel);
				    buyPanel.add(buyMaxLabel);
				    buyPanel.add(buyMax);
				    
				    filterPanel.add(sellPanel);
				    filterPanel.add(seriesPanel);
				    sellMin = new JTextField("0");
				    sellMax = new JTextField("9999999");

				    
				    sellMin.setPreferredSize(new Dimension(80,25));
				    sellMax.setPreferredSize(new Dimension(80,25));
				    
				    JLabel sellMinLabel = new JLabel("Min:");
				    JLabel sellMaxLabel = new JLabel("Max:");
				    JLabel sellDashLabel = new JLabel(" - ");
				    
				    sellPanel.add(sellMinLabel);
				    sellPanel.add(sellMin);
				    sellPanel.add(sellDashLabel);
				    sellPanel.add(sellMaxLabel);
				    sellPanel.add(sellMax);
				    
				    MyDocumentFilter documentFilter = new MyDocumentFilter();

			        ((AbstractDocument)buyMin.getDocument()).setDocumentFilter(documentFilter);  
			        ((AbstractDocument)buyMax.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)rankMin.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)rankMax.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)sellMin.getDocument()).setDocumentFilter(new MyDocumentFilter());  
			        ((AbstractDocument)sellMax.getDocument()).setDocumentFilter(new MyDocumentFilter());  

				    
				    
				    
				    
				    
				    JScrollPane scrollPane = new JScrollPane(table);
				    scrollPane.setPreferredSize(new Dimension(1095, 400));
				    table.getColumnModel().getColumn(0).setPreferredWidth(100);
				    table.getColumnModel().getColumn(1).setPreferredWidth(390);
				    table.getColumnModel().getColumn(2).setPreferredWidth(350);
				    table.getColumnModel().getColumn(3).setPreferredWidth(200);
				    table.getColumnModel().getColumn(4).setPreferredWidth(200);
				    table.getColumnModel().getColumn(5).setPreferredWidth(200);
				    table.getColumnModel().getColumn(6).setPreferredWidth(280);
				    table.getColumnModel().getColumn(7).setPreferredWidth(270);
				    table.getColumnModel().getColumn(8).setPreferredWidth(100);


				    
				    lastUpdated = new JLabel("Last Updated: Never");
				    updatingLabel = new JLabel("Updating: Page 0 of 0");
				    errorLabel = new JLabel("Error: Min is higher than Max");
				    JLabel donationLabel = new JLabel("This is a free program. If you like it, donate to PayPal: AnthoneyKalasho@Gmail.com");
				    donationPanel.add(donationLabel,BorderLayout.CENTER);
				    errorLabel.setForeground(Color.red);
				    errorLabel.setVisible(false);
				    bottomPanel.setLayout(new BorderLayout());
				    updatingLabel.setBorder(new EmptyBorder( 3,50, 3, 3 ));
				    lastUpdated.setBorder(new EmptyBorder( 3, 3, 3, 50 ));
				    errorLabel.setBorder(new EmptyBorder( 3, 260, 3, 0 ));
				    donationLabel.setBorder(new EmptyBorder( 3, 3, 3, 3 ));
				    bottomPanel.add(updatingLabel, BorderLayout.WEST);
				    bottomPanel.add(lastUpdated,BorderLayout.EAST);
				    bottomPanel.add(errorLabel,BorderLayout.CENTER);

				    //bottomPanel.setPreferredSize(preferredSize)
				    
				    //updatingLabel.setLocation(0,0);

				    //RowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
				    //table.setRowSorter(sorter);
				    table.setAutoCreateRowSorter(true);
				    //@SuppressWarnings("unchecked")
				    rowSorter = (TableRowSorter<DefaultTableModel>)table.getRowSorter();
				    rowSorter.setComparator(4, new Comparator<Integer>() {

				            @Override
				            public int compare(Integer o1, Integer o2)
				            {
				                return o2 - o1;
				            }

				        });
				    rowSorter.setComparator(0, new Comparator<Integer>() {

			            @Override
			            public int compare(Integer o1, Integer o2)
			            {
			                return o2 - o1;
			            }

			        });
				    rowSorter.setComparator(5, new Comparator<Integer>() {

			            @Override
			            public int compare(Integer o1, Integer o2)
			            {
			                return o2 - o1;
			            }

			        });
				    rowSorter.setComparator(6, new Comparator<Float>() {

			            @Override
			            public int compare(Float o1, Float o2)
			            {
			                return (int) (o2 - o1);
			            }

			        });
				    rowSorter.setComparator(7, new Comparator<String>() {

			            @Override
			            public int compare(String o1,String o2)
			            {
				           /*
			            	if(o1 == null && o2 == null){
				        	   System.out.println("bothNull");
				        	   return 0;
				           }
				           if(o1 == null && !(o2 == null)){
				        	   System.out.println("oneNull");
				        	   return -1;
				           }
				           if(!(o1 == null) && o2 == null){
				        	   System.out.println("oneNull");
				        	   return 1;
				           }
				           
			            	System.out.println("o1 = " + o1);
			            	System.out.println("o2 = " + o2);
			            	System.out.println("-");
			            	if(o1.equals(null)){
			            		System.out.println("NaN encountered");
			            	}
			            	if(o2.equals(null)){
			            		System.out.println("NaN encountered");
			            	}
				           System.out.println("noNull");
				           return (int) (o2 - o1);
				           */
			               if(o1 == "N/A" && o2 == "N/A"){
					           return 0;
					       }
				           if(o1 == "N/A" && !(o2 == "N/A")){
				        	   return 1;
				           }
				           if(!(o1 == "N/A") && o2 == "N/A"){
				        	   return -1;
				           }
					       if((Float.parseFloat(o1))==(Float.parseFloat(o2))){
					    	   return 0;
					       }
					       if((Float.parseFloat(o1))>(Float.parseFloat(o2))){
					    	   return -1;
					       }
			            	   return 1;
			            }

			        });
				    rowSorter.setComparator(8, new Comparator<Integer>() {

			            @Override
			            public int compare(Integer o1, Integer o2)
			            {
			                return o2 - o1;
			            }

			        });
				    tablePanel.add(scrollPane);
				    //frame.add(new JScrollPane(table));
				    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					
				    
					
		
					mainPanel.add(topPanel,BorderLayout.NORTH);
					//topPanel.setAlignmentX(BorderLayout.WEST);
					//filterPanel.setAlignmentX(BorderLayout.WEST);
					
					mainPanel.add(filterPanel, BorderLayout.NORTH);
					mainPanel.add(tablePanel, BorderLayout.CENTER);
					//mainPanel.add(bottomPanel, BorderLayout.PAGE_END);
					//mainPanel.add(lastUpdated);
					//BorderLayout bottomLayout = new BorderLayout();
					mainPanel.add(bottomPanel);
					mainPanel.add(donationPanel);
					//bottomPanel.setLayout(bottomLayout);
					//bottomLayout.setHgap(0);
					//mainPanel.setLayout(new FlowLayout());
					
					mainPanel.revalidate();
					frame.add(mainPanel);
				    frame.revalidate();
				    //frame.setLayout(new FlowLayout());
				    frame.setResizable(false);
				    
					frame.setVisible(true);
				};
			});
	    
	    
	    
	    
		
	}
	
	public static PlayerCard toPlayerCard(Element match, int pgNum){
		String cardName = "blankName",cardTeam="blank";
		int cardRank = 0, cardBuy = 0, cardSell = 0, cardID = 0;
		
		cardName = match.select("td > a[href]").text();
		cardRank = Integer.parseInt(match.select("td:nth-of-type(4)").text());
		Elements buySell = match.select("td > a[cardid]");
		cardBuy = Integer.parseInt(buySell.get(0).text());
		cardSell = Integer.parseInt(buySell.get(1).text());
		cardTeam = match.select("td:nth-of-type(2)").text().toLowerCase();
		
		
		cardTeam =  teamTable.get(cardTeam).toString();
		PlayerCard outputCard = new PlayerCard(cardRank, cardName,"Live Series", cardBuy, cardSell, cardID, cardTeam, pgNum);
		return outputCard;
	}
	
	public static void refreshMethod() throws IOException{
		i=1;
		zeroResults=0;
		
        final Timer t = new Timer(0,null);
        t.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
	        	sourceCode="";
	        	allMatchesOnPage.clear();
	        	addButton.setEnabled(false);
	        	filterButton.setEnabled(false);
	        	resetFilterButton.setEnabled(false);
    		    quickRefreshButton.setEnabled(false);

	  			try{
			    Document doc = Jsoup.connect("https://www.daddyleagues.com/dd/17/players?name=&position=all&team=all&series=1337&page="+i).get();
			    Elements matches = doc.select("tr[class=tbdy1]");
  			    if(matches.size()==0){
  					//System.out.println("Page "+i+" is blank.");
  					blankPg=i-1;
          		    zeroResults=1;
					for(PlayerCard tempCard : allCards){
						float difAfterTax = (float)((((double)tempCard.getBuyNow())-((double)tempCard.getBuyNow())* .1)-(double)tempCard.getSellNow());
						difAfterTax = round(difAfterTax,2);
						percentDiff = (float)(((double)difAfterTax/(double)tempCard.getSellNow())*100);
						percentDiff = round(percentDiff,2);
						perDif = (Float)percentDiff;
						perDifStr = Float.toString(perDif);
						if (tempCard.getBuyNow()==0){
							perDifStr = "N/A";
						}
						if (tempCard.getSellNow()==0) {
							perDifStr = "N/A";
						}	
						model.addRow(new Object[]{(Integer)tempCard.getCardRank(),tempCard.getName(),tempCard.getTeam(),"Live Series",(Integer)tempCard.getBuyNow(),(Integer)tempCard.getSellNow(),difAfterTax,perDifStr,tempCard.getPg()});		
					}
  					
  					addButton.setEnabled(true);
  		        	filterButton.setEnabled(true);
  		        	resetFilterButton.setEnabled(true);
  		        	//System.out.println(totalDifference);          		    
  			    }
			    
  					
	  			if(zeroResults==1){
	  				addButton.setEnabled(true);
  		        	filterButton.setEnabled(true);
  		        	resetFilterButton.setEnabled(true);
  	    		    quickRefreshButton.setEnabled(true);

  					firstRun=1;
  					t.stop();
	  			}
           


	  		allMatchesOnPage.addAll(matches);

  			if(firstRun==0){
  				updatingLabel.setText("Updating: Page "+(i-1)+" of ?");

  			}
  			else {
  				updatingLabel.setText("Updating: Page "+(i-1)+" of " + (blankPg));
  			}
  			updatingLabel.revalidate();
  			//System.out.println(i);
  			allMatches.addAll(allMatchesOnPage);
	  		}
	  		catch(IOException g){
	  			g.printStackTrace();
	  		}
        	  
  		    for(Element currentMatch : allMatchesOnPage){
				PlayerCard tempCard=toPlayerCard(currentMatch,i);
				/*
				float difAfterTax = (float)((((double)tempCard.getBuyNow())-((double)tempCard.getBuyNow())* .1)-(double)tempCard.getSellNow());
				difAfterTax = round(difAfterTax,2);
				percentDiff = (float)(((double)difAfterTax/(double)tempCard.getSellNow())*100);
				percentDiff = round(percentDiff,2);
				perDif = (Float)percentDiff;
				perDifStr = Float.toString(perDif);
				if (tempCard.getBuyNow()==0){
					perDifStr = "N/A";
				}
				if (tempCard.getSellNow()==0) {
					perDifStr = "N/A";
				}
				*/
				//model.addRow(new Object[]{(Integer)tempCard.getCardRank(),tempCard.getName(),tempCard.getTeam(),"Live Series",(Integer)tempCard.getBuyNow(),(Integer)tempCard.getSellNow(),difAfterTax,perDifStr,tempCard.getPg()});
				allCards.add(tempCard);
				//totalDifference=totalDifference+(int)difAfterTax;
			}
        	  
        	  
        	  
        	  if(i==blankPg + 1){
      		    quickRefreshButton.setEnabled(true);
    		    addButton.setEnabled(true);
	        	filterButton.setEnabled(true);
	        	resetFilterButton.setEnabled(true);
        		t.stop();
        	  }
        	  i++;
          }
        });
        
        t.start();
        

		
		
		
	}
	public static void quickRefreshMethod(final ArrayList<Integer> pgList) throws IOException{
		i=0;
		zeroResults=0;
		
        final Timer t = new Timer(0,null);
        t.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
	        	sourceCode="";
	        	allMatchesOnPage.clear();
	        	addButton.setEnabled(false);
	        	filterButton.setEnabled(false);
	        	resetFilterButton.setEnabled(false);
    		    quickRefreshButton.setEnabled(false);

	  			try{
	        	//URL yahoo = new URL("http://theshownation.com/market?page=" + i);
		        //URL yahoo = new URL("http://theshownation.com/market?page="+pgList.get(i)+"&series_id=1337");
	  			//https://www.daddyleagues.com/dd/17/players?name=&position=all&team=all&series=1337&page=3
				Document doc = Jsoup.connect("https://www.daddyleagues.com/dd/17/players?name=&position=all&team=all&series=1337&page="+pgList.get(i)).get();
			    Elements matches = doc.select("tr[class=tbdy1]");
  			    if(matches.size()==0){
  					//System.out.println("Page "+i+" is blank.");
  					blankPg=i-1;
          		    zeroResults=1;

  					
  					addButton.setEnabled(true);
  		        	filterButton.setEnabled(true);
  		        	resetFilterButton.setEnabled(true);
  		        	//System.out.println(totalDifference);
  			    }
	  	
  					
	  			if(zeroResults==1){
	  				addButton.setEnabled(true);
  		        	filterButton.setEnabled(true);
  		        	resetFilterButton.setEnabled(true);
  	    		    quickRefreshButton.setEnabled(true);

  					firstRun=1;
  					t.stop();
	  			}           

  		
  			allMatchesOnPage.addAll(matches);
  			
  			if(firstRun==0){
  				updatingLabel.setText("Updating: Page "+i+" of ?");

  			}
  			else {
  				updatingLabel.setText("Updating: Page "+(i+1)+" of " + pgList.size());
  			}
  			updatingLabel.revalidate();
  			//System.out.println(i);
  			allMatches.addAll(allMatchesOnPage);
	  		}
	  		catch(IOException g){
	  			g.printStackTrace();
	  		}
        	  
  		    for(Element currentMatch : allMatchesOnPage){
				
				PlayerCard tempCard=toPlayerCard(currentMatch,pgList.get(i));
				/*
				float difAfterTax = (float)((((double)tempCard.getBuyNow())-((double)tempCard.getBuyNow())* .1)-(double)tempCard.getSellNow());
				difAfterTax = round(difAfterTax,2);
				percentDiff = (float)(((double)difAfterTax/(double)tempCard.getSellNow())*100);
				percentDiff = round(percentDiff,2);
				perDif = (Float)percentDiff;
				perDifStr = Float.toString(perDif);
				if (tempCard.getBuyNow()==0){
					perDifStr = "N/A";
				}
				if (tempCard.getSellNow()==0) {
					perDifStr = "N/A";
				}
				*/
				//model.addRow(new Object[]{(Integer)tempCard.getCardRank(),tempCard.getName(),tempCard.getTeam(),"Live Series",(Integer)tempCard.getBuyNow(),(Integer)tempCard.getSellNow(),difAfterTax,perDifStr,tempCard.getPg()});
				allCards.add(tempCard);
				//totalDifference=totalDifference+(int)difAfterTax;
			}
        	  
        	  
        	  
        	  if(i==(pgList.size()-1)){
      		    quickRefreshButton.setEnabled(true);
    		    addButton.setEnabled(true);
	        	filterButton.setEnabled(true);
	        	resetFilterButton.setEnabled(true);
	        	
				for(PlayerCard tempCard : allCards){
					float difAfterTax = (float)((((double)tempCard.getBuyNow())-((double)tempCard.getBuyNow())* .1)-(double)tempCard.getSellNow());
					difAfterTax = round(difAfterTax,2);
					percentDiff = (float)(((double)difAfterTax/(double)tempCard.getSellNow())*100);
					percentDiff = round(percentDiff,2);
					perDif = (Float)percentDiff;
					perDifStr = Float.toString(perDif);
					if (tempCard.getBuyNow()==0){
						perDifStr = "N/A";
					}
					if (tempCard.getSellNow()==0) {
						perDifStr = "N/A";
					}	
					model.addRow(new Object[]{(Integer)tempCard.getCardRank(),tempCard.getName(),tempCard.getTeam(),"Live Series",(Integer)tempCard.getBuyNow(),(Integer)tempCard.getSellNow(),difAfterTax,perDifStr,tempCard.getPg()});		
				}
	        	
	        	
	        	
	        	
        		t.stop();
        	  }
        	  i++;
          }
        });
        
        t.start();
        

		
		
		
	}
	public static float round(float number, int scale) {
	    int pow = 10;
	    for (int i = 1; i < scale; i++)
	        pow *= 10;
	    float tmp = number * pow;
	    return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
	}
	
	private static void setTrustAllCerts() throws Exception
	{
	    TrustManager[] trustAllCerts = new TrustManager[]{
	        new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType ) { }
	            public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType ) { }
	        }
	    };

	    // Install the all-trusting trust manager
	    try {
	        SSLContext sc = SSLContext.getInstance( "SSL" );
	        sc.init( null, trustAllCerts, new java.security.SecureRandom() );
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        HttpsURLConnection.setDefaultHostnameVerifier( 
	            new HostnameVerifier() {
	                public boolean verify(String urlHostName, SSLSession session) {
	                    return true;
	                }
	            });
	    }
	    catch ( Exception e ) {
	        //We can not recover from this exception.
	        e.printStackTrace();
	    }
	}

}