package cmd;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class MsgBox 
{
	private static long seconds=0;
	static final String HTML_TEXT = "<html><div style='font-family: Segoe UI; text-align: center; font-size: 14px;'>";
	private static final String WINDOW_TITLE = "Backup Progress Report";
	static JFrame frame = new JFrame(WINDOW_TITLE);
	static JLabel label, resultsLabel;
	public static void setMsgBox() 
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		label = new JLabel();
		resultsLabel = new JLabel(HTML_TEXT+"Files: "+Main.files+"<br>Folders: "+Main.folders+"<br>Size: "+Main.getSizeInMultipleOfBytes());
		JLabel timeLabel = new JLabel(HTML_TEXT+"Time elapsed: "+seconds+"s");
		Timer timer = new Timer(1000, e -> {
			++seconds;
			if (seconds>=3600) timeLabel.setText(HTML_TEXT+"Time elapsed: "+(seconds/3600)+"h"+((seconds/60)%60)+"m"+(seconds%3600)+"s");
			else if (seconds>=60) timeLabel.setText(HTML_TEXT+"Time elapsed: "+(seconds/60)+"m"+(seconds%60)+"s");
			else timeLabel.setText(HTML_TEXT+"Time elapsed: "+seconds+"s");
		});
		timer.start();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER); 
	
        panel.add(Box.createVerticalGlue()); //top margin
		panel.add(label);
		panel.add(new JLabel(" ")); //empty space
		panel.add(timeLabel);
		panel.add(resultsLabel);
		panel.add(Box.createVerticalGlue()); //bottom margin
		
		frame.add(panel);
		frame.setSize(1280,256);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void setMsgBoxError(String errorMsg)
	{
		JOptionPane.showMessageDialog(null, HTML_TEXT+errorMsg, WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
	}
	public static void setMsgBoxSuccess(double time)
	{
		String timeString=null; 
		if (seconds>=3600) timeString=String.format("%d hours, %d minutes and %.3f seconds", (int)time/3600, (int)(time/60)%60, time);
		else if (seconds>=60) timeString=String.format("%d minutes and %.3f seconds", (int)time%60, time);
		else timeString=String.format("%.3f seconds", time);
		String results=HTML_TEXT+Main.files+" files & "+Main.folders+" folders worth "+
		Main.getSizeInMultipleOfBytes()+" backed up successfully in "+timeString+"!";
		JOptionPane.showMessageDialog(null, results, WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
	}
}