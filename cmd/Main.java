package cmd;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main 
{
	private static File[] dirsArray = new File[2];
	private static final String TIME_FORMAT = "dd-MM-yy-hh-mm-ss";
	static long bytes=0, files=0, folders=0;
	static String currPath, errorMsg;
	public static boolean isDirsFileValid(File dirs)
	{
		String[] dirType = {"Input ","Output "};
		Scanner sc=null;
		try 
		{
			sc = new Scanner(dirs);
			for (int i=0; i<4; i++)
			{
				if (sc.hasNextLine())
				{
					String input = sc.nextLine();
					if (i%2==0)
					{
						if (!input.contains(dirType[i/2]+"Directory")) 
						{
							errorMsg="Invalid format! dirs.txt must contain 1 input directory & 1 output directory."; 
							sc.close(); return false;
						}
					}
					else
					{
						File dir = new File(input);
						if (!dir.isDirectory()) 
						{
							errorMsg="Invalid directories found in dirs.txt!"; 
							sc.close(); return false;
						}
						if (i==1) dirsArray[0]=dir;
						if (i==3) dirsArray[1]=dir;
					}
				}
				else
				{
					errorMsg="Invalid format! dirs.txt must contain exactly 4 lines of text, where even ones must be directories."; 
					sc.close(); return false;
				}
			}
			sc.close();
		} 
		catch (FileNotFoundException e) 
		{
			errorMsg="dirs.txt is missing!"; return false;		
		}
		return true;
	}
	public static String getSizeInMultipleOfBytes()
	{
		double newSize=0;
		long GB = 1073741824;
		long MB = 1048576;
		long TB = 1099511627776L;
		String unit=" ";
		if (bytes>=TB) 
		{
			newSize=(double)bytes/TB; unit+="TB";
		}
		else if (bytes>=GB) 
		{
			newSize=(double)bytes/GB; unit+="GB";
		}
		else if (bytes>=MB) 
		{
			newSize=(double)bytes/MB; unit+="MB";
		}
		else if (bytes>=1024)
		{
			newSize=(double)bytes/1024; unit+="KB";
		}
		else unit+="B";
		if (bytes>=1024) return String.format("%.3f", newSize)+unit;
		else return bytes+unit;
	}
	public static void backup(File src, File dst)
	{
		if (src.isDirectory())
		{
			dst.mkdirs(); //make folders and subfolders in case source has them
			folders++;
			//check for subfolders
			String[] list = src.list();
			if (list!=null)
			{
				for (String name: list)
				{
					File newSrc = new File(src,name);
					File newDst = new File(dst,name);
					if (Files.isReadable(src.toPath())) backup(newSrc,newDst);
				}
			}
		}
		else if (src.isFile()) //copy files
		{
			files++; bytes+=src.length();
			currPath = src.toPath().toString();
			MsgBox.label.setText(MsgBox.HTML_TEXT+"Working on:<br>"+currPath);
			try 
			{
				dst.getParentFile().mkdirs(); //I put this just to be 100% sure
				boolean canBeRenamed = src.renameTo(src);
				if (!canBeRenamed) //and here is a cheap way of checking if a file is in use or not
				{
					String os = System.getProperty("os.name");
					if (os.contains("Win"))
					{
						String srcFileName = src.getName();
						String srcFolder = src.toPath().toString().replace(srcFileName, "");
						String dstFolder = dst.toPath().toString().replace(srcFileName, "");
						//remove slashes at end of folder name via regex
						srcFolder = srcFolder.replaceFirst(".$","");
						dstFolder = dstFolder.replaceFirst(".$", "");
						//add quotes before and after
						srcFileName='"'+srcFileName+'"';
						srcFolder='"'+srcFolder+'"'; dstFolder='"'+dstFolder+'"';
						//execute robocopy command
						ProcessBuilder builder = new ProcessBuilder("robocopy",srcFolder,dstFolder,srcFileName);
						builder.redirectErrorStream(true); //actual life saver
						Process proc = builder.start();
						proc.waitFor();
					}
				}
				else Files.copy(src.toPath(), dst.toPath());
			} 
			catch (IOException | SecurityException | InterruptedException e) 
			{
				//I wrote this because I find it far more favorable than e.printStackTrace();
				File errorLog = new File("errors.log");
				try {
					FileWriter logWriter = new FileWriter(errorLog,true);
					if (errorLog.exists()) errorLog.delete();
					logWriter.append(e.getMessage());
					logWriter.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		}
		MsgBox.resultsLabel.setText(MsgBox.HTML_TEXT+"Files: "+files+"<br>Folders: "+folders+"<br>Size: "+Main.getSizeInMultipleOfBytes());
	}
	public static void main(String[] args) throws IOException 
	{
		String currWorkDir = new File(".").getCanonicalPath();
		File dirs = new File(currWorkDir+"\\config\\dirs.txt");
		if (isDirsFileValid(dirs)) 
		{
			MsgBox.setMsgBox();
			Date d = new Date();
			String backupFolder = dirsArray[1]+"\\"+dirsArray[0].getName()+"-BACKUP-"+new SimpleDateFormat(TIME_FORMAT).format(d); 
			dirsArray[1] = new File(backupFolder);
			dirsArray[1].mkdir(); //make backup folder
			long start = System.currentTimeMillis();
			backup(dirsArray[0],dirsArray[1]);
			long finish = System.currentTimeMillis();
			double time = (finish-start)/(double)1000;
			MsgBox.frame.setVisible(false);
			MsgBox.setMsgBoxSuccess(time);
			System.exit(0);
		}
		else 
		{
			MsgBox.setMsgBoxError(errorMsg);
			System.exit(1);
		}
	}
}