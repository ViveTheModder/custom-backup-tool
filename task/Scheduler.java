package task;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Scheduler 
{
	static final String HTML_TEXT = "<html><div style='font-family: Segoe UI; text-align: left; font-size: 14px;'>";
	static final String WINDOW_TITLE = "Backup Configuration";
	private static final int[] START_TIME_MAX = {23,59,59};
	private static final int[] TIME_UNIT_MAX = {23,365,52,12};
	private static final String[] START_TIME_UNITS = {"hours","minutes","seconds"};
	private static final String OS = System.getProperty("os.name");
	static boolean hasWeekDayOption=false;
	static int checkboxID=-1, currMax=-1, schedID=-1, valueOfN;
	static JFrame frame = new JFrame(WINDOW_TITLE);
	static JTextField nUnit = null;
	static String timeUnit="UNITS";
	public static void setConfigGUIForWin()
	{
		final String[] RADIO_BTN_TEXT = {"HOURLY","DAILY","WEEKLY","MONTHLY"};
		final String[] TIME_UNITS = {"HOURS","DAYS","WEEKS","MONTHS"};
		JPanel panel = new JPanel();
		JLabel schedLabel = new JLabel(HTML_TEXT+"Backup Schedule:");
		JLabel modLabel = new JLabel(HTML_TEXT+"Backup Frequency<br>(EVERY N "+timeUnit+"):");
		JLabel startTimeLabel = new JLabel(HTML_TEXT+"Backup Start Time:");
		JLabel weekDayLabel = new JLabel(" ");
		JTextField modField = new JTextField();
		JTextField weekDayField = new JTextField();
		JButton configBtn = new JButton(HTML_TEXT+"Schedule Backup");
		
		schedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		modLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		startTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		modField.setAlignmentX(Component.CENTER_ALIGNMENT);
		configBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		modField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(schedLabel);
        
        ButtonGroup btnGrp = new ButtonGroup();
        JRadioButton[] radBtns = new JRadioButton[RADIO_BTN_TEXT.length];
		for (int i=0; i<RADIO_BTN_TEXT.length; i++)
		{
			final int constant=i;
			radBtns[i] = new JRadioButton(HTML_TEXT+RADIO_BTN_TEXT[i]);
			radBtns[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			radBtns[i].addActionListener(new ActionListener() {
			    @Override
			    public void actionPerformed(ActionEvent e) {
			        schedID = constant;
			    	Task.schedType = RADIO_BTN_TEXT[constant];
			        timeUnit = TIME_UNITS[constant];
			        modLabel.setText(HTML_TEXT+"Backup Frequency<br>(EVERY N "+timeUnit+"):");
			        if (schedID==2 || schedID==3) 
			        {
			        	hasWeekDayOption=true;
			        	weekDayLabel.setText(HTML_TEXT+"<br>Day of Week (1-7): ");
			        	weekDayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			        	weekDayField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			        	panel.add(weekDayLabel);
			        	panel.add(weekDayField);
			        }
			        else
			        {
			        	hasWeekDayOption=false;
			        	panel.remove(weekDayLabel);
			        	panel.remove(weekDayField);
			        }
			    }
			});
			btnGrp.add(radBtns[i]);
			panel.add(radBtns[i]);
		}
		Box startTimeBox = Box.createHorizontalBox();
		JTextField[] startTimeFields = new JTextField[START_TIME_MAX.length];
		for (int i=0; i<START_TIME_MAX.length; i++)
		{
			startTimeFields[i] = new JTextField();
			startTimeFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
			startTimeBox.add(startTimeFields[i]);
		}
		configBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String bakFreqText = modField.getText();
				String[] startTimeText = new String[START_TIME_MAX.length];
				for (int i=0; i<START_TIME_MAX.length; i++)
					startTimeText[i] = startTimeFields[i].getText();
				String weekDayText = weekDayField.getText();
				validateConfigOptions(bakFreqText,startTimeText,weekDayText);
			}
		});
		panel.add(modLabel);
		panel.add(modField);
		panel.add(new JLabel(" "));
		panel.add(startTimeLabel);
		panel.add(startTimeBox);
		panel.add(new JLabel(" "));
		panel.add(configBtn);
		frame.setLayout(new GridBagLayout());
		frame.add(panel);
		frame.setSize(512,512);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void setConfigGUIForUnix()
	{
		JButton configBtn = new JButton(HTML_TEXT+"Schedule");
		JCheckBox[] checkboxes = new JCheckBox[5];
		JPanel panel = new JPanel();
		JLabel timeLabel = new JLabel(HTML_TEXT+"Time Units: ");
		JLabel modLabel = new JLabel(HTML_TEXT+"Apply Modifiers: ");
		JLabel[] labels = new JLabel[10];
		JRadioButton[] radioBtns = new JRadioButton[4];
		JTextField[] units = new JTextField[5];
		int[] unitCharLimits = {1,1,1,1,0}, unitMin = {0,0,1,1,0}, unitMax = {59,23,31,12,6};
		String[] modifiers = {"Even Units","Odd Units","Every N Units","At N Units"};
		String[] tooltips = {"Minutes","Hours","Day of Month","Months","Day of Week"};
		
		configBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		configBtn.setMaximumSize(new Dimension(140,56));
		configBtn.setMinimumSize(new Dimension(140,56));
		configBtn.setPreferredSize(new Dimension(140,56));
		configBtn.setToolTipText(HTML_TEXT+"For validation purposes, if the units have higher values than normal, they will be set to their respective maximum values.");
		
		Box labelBox = Box.createHorizontalBox();
		for (int i=0; i<units.length; i++)
		{
			labels[i] = new JLabel((HTML_TEXT+"("+(i+1)+")"));
			labels[i].setToolTipText(HTML_TEXT+tooltips[i]);
			labelBox.add(labels[i]);
		}
		Box labelBoxCpy = Box.createHorizontalBox();
		for (int i=0; i<units.length; i++)
		{
			labels[i] = new JLabel((HTML_TEXT+"("+(i+1)+")"));
			labels[i].setToolTipText(HTML_TEXT+tooltips[i]);
			labelBoxCpy.add(labels[i]);
		}
		
		Box textFieldBox = Box.createHorizontalBox();
		for (int i=0; i<units.length; i++)
		{
			units[i] = new JTextField();
			units[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
			units[i].setHorizontalAlignment(JTextField.CENTER);
			
			final int index = i;
			units[i].addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e)
				{
					char ch = e.getKeyChar();
					String text = units[index].getText();
					if (text.length()>unitCharLimits[index])
					{
						if (!(ch==KeyEvent.VK_DELETE || ch==KeyEvent.VK_BACK_SPACE))
							e.consume();
					}
					if (!(ch>='0' && ch<='9')) e.consume();
				}
			});
			textFieldBox.add(units[i]);
		}
		Box boxOfCheckboxes = Box.createHorizontalBox();
		for (int i=0; i<units.length; i++)
		{
			final int constant = i;
			checkboxes[i] = new JCheckBox();
			checkboxes[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					checkboxID = constant;
					if (checkboxes[constant].isSelected()) Task.cronEntryFlags[checkboxID]=true;
					else Task.cronEntryFlags[checkboxID]=false;
				}});
			boxOfCheckboxes.add(checkboxes[i]);
			boxOfCheckboxes.add(new JLabel("  "));
		}
		timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		modLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		textFieldBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(timeLabel);
		panel.add(labelBox);
		panel.add(textFieldBox);
		panel.add(new JLabel(" "));
		panel.add(modLabel);
		panel.add(labelBoxCpy);
		panel.add(boxOfCheckboxes);
		panel.add(new JLabel(" "));
		ButtonGroup btnGrp = new ButtonGroup();
		
		Box nBox = Box.createHorizontalBox();
		JLabel emptyLabel = new JLabel(" "), nLabel = new JLabel(HTML_TEXT+"Value of N: ");
		nUnit = new JTextField();

		for (int i=0; i<4; i++)
		{
			Box modBox = Box.createHorizontalBox();
			labels[i+5] = new JLabel(HTML_TEXT+modifiers[i]);
			radioBtns[i] = new JRadioButton();
			final int constant = i;
			radioBtns[i].addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					schedID = constant;
					nUnit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
					nUnit.setHorizontalAlignment(JTextField.CENTER);
					nUnit.addKeyListener(new KeyAdapter() {
						public void keyTyped(KeyEvent e)
						{
							char ch = e.getKeyChar();
							String text = nUnit.getText();
							if (text.length()>1)
							{
								if (!(ch==KeyEvent.VK_DELETE || ch==KeyEvent.VK_BACK_SPACE))
									e.consume();
							}
							if (!(ch>='0' && ch<='9')) e.consume();
						}
					});
					if (constant==2 || constant==3) 
					{
						nBox.add(nLabel); nBox.add(nUnit);
						panel.add(emptyLabel); panel.add(nBox);
					}
					else 
					{
						nBox.removeAll();
						panel.remove(emptyLabel); panel.remove(nBox);
					}
					frame.revalidate();
				}
			});
			btnGrp.add(radioBtns[i]);
			modBox.add(labels[i+5]);
			modBox.add(radioBtns[i]);
			panel.add(modBox);
		}
		configBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				frame.setVisible(false);
				System.out.println("Cron Entries (* = Every):");
				for (int i=0; i<5; i++)
				{
					int val=0;
					String[] modifierSyntax = {"*/2","1-MAX/2","*/N","N"};
					String text = units[i].getText();
					String nUnitText = nUnit.getText();
					currMax=unitMax[i];
					if (!text.equals(""))
					{	
						val = Integer.parseInt(text);
						if (val>unitMax[i]) val=unitMax[i]; //handle overflow
						else if (val<unitMin[i]) val=unitMin[i]; //handle underflow
					}
					if (Task.cronEntryFlags[i]==true)
					{
						if (schedID==-1) Task.cronEntries[i]="*";
						else Task.cronEntries[i] = modifierSyntax[schedID];
						if (!nUnitText.equals(""))
						{
							int nUnitVal = Integer.parseInt(nUnitText);
							if (nUnitVal>unitMax[i]) nUnitVal=unitMax[i]; //handle overflow
							else if (nUnitVal<unitMin[i]) nUnitVal=unitMin[i]; //handle underflow
							Task.cronEntries[i] = Task.cronEntries[i].replace("N", nUnitVal+"");
						}
						else Task.cronEntries[i] = Task.cronEntries[i].replace("N", unitMin[i]+""); //set default value to minimum
						if (currMax!=-1) 
							Task.cronEntries[i] = Task.cronEntries[i].replace("MAX", currMax+"");
					}
					else 
					{
						if (!text.equals("")) Task.cronEntries[i]=val+"";
						else Task.cronEntries[i]="*";
					}
					System.out.println(Task.cronEntries[i]+" "+tooltips[i]);
				}
				Task.setScheduledTask(); System.exit(0);
			}
		});
		
		panel.add(new JLabel(" "));
		panel.add(configBtn);
		frame.setLayout(new GridBagLayout());
		frame.add(panel);
		frame.setSize(512,512);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void validateConfigOptions(String bakFreqText, String[] startTimeText, String weekDayText)
	{
		boolean isBakFreqValid=false, isStartTimeValid=false, isWeekDayValid=false;
		boolean[] validStartTimeVals = new boolean[startTimeText.length];
		String errorMsg="";
		if (Task.schedType.equals("")) errorMsg+="Select a backup schedule!<br>";
		//validate schedule modifier
		if (bakFreqText.length()==0) errorMsg+="Please enter a backup frequency!<br>";
		else if (!bakFreqText.matches("[0-9]+")) errorMsg+="Backup frequency must only contain integers!<br>";
		else
		{
			Task.bakFreq = Integer.parseInt(bakFreqText);
			if (schedID==-1);
			else if (Task.bakFreq>0 && Task.bakFreq<=TIME_UNIT_MAX[schedID]) isBakFreqValid=true;
			else errorMsg+="Enter a valid backup frequency, no more than "+TIME_UNIT_MAX[schedID]+"!<br>";
		}
		//validate start time values
		for (int i=0; i<startTimeText.length; i++)
		{
			if (startTimeText[i].length()==0) Task.startTimeOptions[i]=0; //default value
			else if (!startTimeText[i].matches("[0-9]+")) errorMsg+="Start time "+START_TIME_UNITS[i]+" must only contain integers!<br>";
			else
			{
				int startTimeVal = Integer.parseInt(startTimeText[i]);
				if (startTimeVal>=0 && startTimeVal<=START_TIME_MAX[i]) 
				{
					validStartTimeVals[i]=true; Task.startTimeOptions[i]=startTimeVal;
				}
				else errorMsg+="Start time "+START_TIME_UNITS[i]+" must not exceed "+START_TIME_MAX[i]+" "+START_TIME_UNITS[i]+"!<br>";
			}
		}
		for (int i=0; i<startTimeText.length; i++) //set boolean values for start time values
		{
			if (validStartTimeVals[i]) isStartTimeValid=true;
			else 
			{
				isStartTimeValid=false; break;
			}
		}
		if (hasWeekDayOption) //validate weekday value
		{
			if (weekDayText.length()==0) errorMsg+="Please enter a day of the week as a numerical value!<br>";
			else if (!weekDayText.matches("[0-9]+")) errorMsg+="Day of the week must be a numerical value!<br>";
			else
			{
				int day = Integer.parseInt(weekDayText);
				if (day<=0 && day>7) errorMsg+="Day of the week must be between 1 and 7!<br>";
				else 
				{
					isWeekDayValid=true; Task.weekday=day;
				}
			}
		}
		if (isBakFreqValid && isStartTimeValid)
		{
			if (hasWeekDayOption) 
			{
				if (isWeekDayValid) errorMsg="";
			}
			else errorMsg="";
		}
		if (!errorMsg.equals("")) //display error message if not empty
		{
			JOptionPane.showMessageDialog(null, HTML_TEXT+errorMsg, WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
			return; //end validation method after error message
		}
		else //proceed with scheduled task creation if all cases are valid
		{
			frame.setVisible(false); 
			JOptionPane.showMessageDialog(null, HTML_TEXT+"Backup task has been scheduled successfully!", WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
			Task.setScheduledTask(); System.exit(0);
		}
	}
	public static void main(String[] args) 
	{
		if (OS.contains("Win")) setConfigGUIForWin();
		else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) setConfigGUIForUnix();
		else JOptionPane.showMessageDialog(null, HTML_TEXT+"Support has not been added yet for this OS ("+OS+")!", WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
	}
}