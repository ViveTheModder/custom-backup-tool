package task;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

public class Task 
{
	private static final String OS = System.getProperty("os.name");
	private static final String TOOL_TITLE = "backup.jar";
	private static final String[] WEEKDAYS = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
	static boolean[] cronEntryFlags = new boolean[5];
	static int bakFreq=1, weekday=0;
	static int[] startTimeOptions = {0,0,0};
	static String schedType="";
	static String[] cronEntries = new String[5];
	public static void makeBatchFile(String task, String command, String currDir) throws IOException
	{
		File batch = new File(currDir+"\\config\\scheduler.bat");
		FileWriter writer = new FileWriter(batch);
		writer.write(task+" /F");
		writer.close();
		
		batch = new File(currDir+"\\config\\task.bat");
		writer = new FileWriter(batch);
		String changeDir = "cd /d "+'"'+currDir+'"'+"\n";
		writer.write(changeDir+"\n"+command);
		writer.close();
	}
	public static void runBatchFile(String currDir) throws InterruptedException, IOException
	{
		ProcessBuilder builder = new ProcessBuilder(currDir+"\\config\\scheduler.bat");
		Process proc = builder.start();
		proc.waitFor();
	}
	public static void setErrorsLog(Exception e)
	{
		final String TIME_FORMAT = "dd-MM-yy-hh-mm-ss";
		File errorLog = new File("errors.log");
		try {
			FileWriter logWriter = new FileWriter(errorLog,true);
			logWriter.append(new SimpleDateFormat(TIME_FORMAT).format(new Date())+":\n"+e.getMessage()+"\n");
			logWriter.close();
			Desktop.getDesktop().open(errorLog);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	public static void setScheduledTask()
	{
		if (OS.contains("Win"))
		{
			try {
				String finalJavaPath = "start /b javaw.exe";
				String currDir = new File(".").getCanonicalPath();
				String schedJar = currDir+'\\'+TOOL_TITLE;
				String command = finalJavaPath+" -jar"+' '+schedJar;
				//important arguments for scheduled task creation
				String task = "schtasks.exe /CREATE /TN "+"\"ScheduledBackupTask\""+
				" /TR "+currDir+"\\config\\task.bat"+" /SC "+schedType+" /ST ";
				//add leading zeroes for /ST argument to work as intended
				String[] extraZeroes = {"","",""};
				for (int i=0; i<startTimeOptions.length; i++)
				{
					if (startTimeOptions[i]>=10) continue;
					extraZeroes[i]="0";
				}
				task+=extraZeroes[0]+startTimeOptions[0]+":"+extraZeroes[1]+startTimeOptions[1]+":"+extraZeroes[2]+startTimeOptions[2];
				//add /MO argument (mandatory)
				task+=" /MO "+bakFreq;
				//add /D argument if weekday is provided (optional)
				if (weekday!=0) task+=" /D "+WEEKDAYS[weekday-1];
				makeBatchFile(task,command,currDir);
				runBatchFile(currDir);
				JOptionPane.showMessageDialog(null, Scheduler.HTML_TEXT+"Backup task has been scheduled successfully!", 
				Scheduler.WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException | InterruptedException e) {
				setErrorsLog(e);
			}
		}
		else if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))
		{
			try {
				String finalJavaPath = "javaw.exe";
				String currDir = new File(".").getCanonicalPath();
				String schedJar = currDir+'/'+TOOL_TITLE;
				String command = finalJavaPath+" -jar"+' '+schedJar;
				String cron="", cronName="/backup";
				for (int i=0; i<cronEntries.length; i++)
					cron+=cronEntries[i]+" ";
				cron+=command;
				//write cron task to crontabs
				String crontabs = "/var/spool/cron/crontabs";
				File crontabsRef = new File(crontabs);
				if (!crontabsRef.exists()) crontabsRef.mkdir();
				PrintWriter writer = new PrintWriter(crontabs+cronName);
				writer.println(cron); writer.close();
				JOptionPane.showMessageDialog(null, Scheduler.HTML_TEXT+"Backup task has been scheduled successfully!<br>"+cron, 
				Scheduler.WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
				//run crontabs (user must run jar as superuser for this to work)
				ProcessBuilder builder = new ProcessBuilder("crontab",crontabs+cronName);
				builder.redirectErrorStream(true);
				Process proc = builder.start();
				proc.waitFor();
			} 
			catch (IOException | InterruptedException e) {
				setErrorsLog(e);
			}
		}
	}
}