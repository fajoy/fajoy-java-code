import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
public class MainFrame extends JFrame{
	private JPanel pnlFrame = new JPanel();
	public JButton btn1;
	public JButton btn2;
	public JButton btn3;	
	public JTextField txtField;
	public JTextArea txtArea;
	public JScrollPane scrollPane;
	public JPanel pnlWhiteBorad ; 
	public JPanel selectPnl=null;
	public int sX=0;
	public int sY=0;
	void InitializeComponent(){
		setTitle("mainFrame");
		setSize(750,600);
		addWindowListener(windowAdapter);
		add(pnlFrame);
		pnlFrame.setLayout(null);
		
		pnlWhiteBorad = new JPanel();
		pnlWhiteBorad.setBounds(0, 0, 200, 200);
		
		pnlWhiteBorad.setLayout(null);
		pnlWhiteBorad.setBackground(Color.white);
		scrollPane=new JScrollPane(pnlWhiteBorad,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(0, 0, 500, 300);
		
		
		pnlFrame.add(scrollPane);
		
		Panel pnl = new Panel();
		pnl.setBounds(500, 0, 180, 300);
		pnlFrame.add(pnl);
		pnl.setLayout(new GridLayout(3, 0, 0, 0));
		btn1 = new JButton("Rectangle");
		pnl.add(btn1);		
		btn2 = new JButton("Circle");
		pnl.add(btn2);
		btn3 = new JButton("Juggler");
		pnl.add(btn3);

		txtArea = new JTextArea(8,50);
		JScrollPane scrollPane=new JScrollPane(txtArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(0, 306, 675, 175);
		
		pnlFrame.add(scrollPane);
		
		txtField = new JTextField();
		txtField.setBounds(95, 487, 590, 23);
		pnlFrame.add(txtField);
		
		Label label = new Label("Input");
		label.setBounds(20, 487, 69, 23);
		pnlFrame.add(label);
		
		/*
		SpringLayout sl_contentPane = new SpringLayout();
		sl_contentPane.putConstraint(SpringLayout.WEST, this.scrollPane, 0, SpringLayout.NORTH, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.EAST, this.scrollPane, 500, SpringLayout.NORTH, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.NORTH, this.scrollPane, 0, SpringLayout.NORTH, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, this.scrollPane, 300, SpringLayout.NORTH, pnlFrame);
		
		
		sl_contentPane.putConstraint(SpringLayout.WEST, pnl, 0, SpringLayout.EAST, this.scrollPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, pnl, 0, SpringLayout.EAST, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.NORTH, pnl, 0, SpringLayout.NORTH, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, pnl, 0, SpringLayout.NORTH, scrollPane);
		
		
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, 0, SpringLayout.EAST, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, this.scrollPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, 0, SpringLayout.NORTH, txtField);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, label, 0, SpringLayout.SOUTH, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, pnlFrame);
		
		
		sl_contentPane.putConstraint(SpringLayout.EAST, txtField, 0, SpringLayout.EAST, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, txtField, 0, SpringLayout.SOUTH, pnlFrame);
		sl_contentPane.putConstraint(SpringLayout.WEST, txtField, 0, SpringLayout.EAST, label);
		
		pnlFrame.setLayout(sl_contentPane);
		*/
	}
	public MainFrame(){
		InitializeComponent();
		btn1.addActionListener(actionListener);
		btn2.addActionListener(actionListener);
		btn3.addActionListener(actionListener);
		txtField.addKeyListener(keyAdapter);
	}

	
	
	KeyAdapter keyAdapter=new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          int key = e.getKeyCode();
          if(txtArea.getText().length()>4096)
        	  txtArea.setText("");
          if (key == KeyEvent.VK_ENTER) {
       	   txtArea.append(txtField.getText());
       	   txtArea.append("\n");
				txtField.setText("");
				txtArea.setSelectionStart(txtArea.getText().length());
             }
          }
        };
	WindowAdapter windowAdapter=new WindowAdapter() {
		@Override public void windowClosing(WindowEvent e) {
			System.exit(0);
		};
	};
	ActionListener actionListener=new  ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			JPanel p=null;
			if(e.getSource()==btn1)
				p=new MovePanel(50,50,Color.red);
			if(e.getSource()==btn2)
				p=new MovePanel(100,100,Color.yellow);
			if(e.getSource()==btn3)
				p=new MovePanel(150,150,Color.green);
			pnlWhiteBorad.add(p,0,0);
			pnlWhiteBorad.scrollRectToVisible(p.getBounds());
			scrollPane.repaint();
		}
	};
	MouseAdapter mouseAdapter=new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(e.getComponent()!=pnlWhiteBorad){
			selectPnl=(JPanel)e.getSource();
			sX=e.getX();
			sY=e.getY();
			System.out.println("Pressed obj" );
			}

			//super.mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(selectPnl==null)
				return;

			pnlWhiteBorad.scrollRectToVisible(selectPnl.getBounds());
			selectPnl=null;
			System.out.println("Released obj");
			scrollPane.revalidate();
			//super.mouseReleased(e);
		}

		public void mouseDragged(MouseEvent e) {
			if(selectPnl==e.getSource()){
				int x= e.getXOnScreen()-selectPnl.getParent().getLocationOnScreen().x-sX;
				int y= e.getYOnScreen()-selectPnl.getParent().getLocationOnScreen().y-sY;
				if(x<0)x=0;
				if(y<0)y=0;
				
				System.out.println("x:"+x+" y:"+y );
				selectPnl.setLocation(x, y);
				pnlWhiteBoradResize();
				
			}
			
			super.mouseDragged(e);
		};
	};
	
	void pnlWhiteBoradResize(){
		double maxX=1;
		double maxY=1;
		for (int i=0;i<pnlWhiteBorad.getComponentCount();i++) {
			JPanel p=(JPanel)pnlWhiteBorad.getComponent(i);
			if(maxX<p.getBounds().getMaxX())
				maxX=p.getBounds().getMaxX();
			if(maxY<p.getBounds().getMaxY())
				maxY=p.getBounds().getMaxY();
			
		}
		pnlWhiteBorad.setPreferredSize(new Dimension((int)maxX+6,(int)maxY+6));
		pnlWhiteBorad.repaint();
		scrollPane.revalidate();
	}
	
	
	class MovePanel extends JPanel{
		public  MovePanel(int width,int height,Color c){
			setBackground(c);
			setSize(width,height);
			addMouseListener(mouseAdapter);
			addMouseMotionListener(mouseAdapter);
		}
	}
}

