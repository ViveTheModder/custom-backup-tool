# custom-backup-tool
A program in Java that makes backups and schedules them, for Windows and Unix.

The user can customize what directories to include/exclude, as well as include/exclude hidden & Operating System files & folders (for Windows).

# Usage/Breakdown
For the program to work, it needs at least one input directory and one output directory, which it takes from ``dirs.txt``, stored in the ``config`` folder.

To schedule the backup procedure, just run ``scheduler.jar`` (**on Unix, as superuser, from the terminal:** ``sudo java -jar scheduler.jar``) 
and you will be greeted with this interface:

## Backup Scheduler on Windows
![image](https://github.com/user-attachments/assets/89eb75e1-0961-44d7-9d52-920b49faa597)

If the **WEEKLY** or **MONTHLY** option is chosen, the user can enter the **day of the week (Monday-Sunday)** during which the backup task will run.

![image](https://github.com/user-attachments/assets/ef53a4e0-39da-4f0e-95b8-72619dee20fc)

As for the **Backup Frequency**, that represents how often your files will be backed up.

In this example, the backup task will run **everyday at 12:20 PM**.

That's right, the start time must be in **military time (24-hour clock)**.

![image](https://github.com/user-attachments/assets/57be09b7-897e-4d52-ac41-a532b3176c37)

After the chosen backup options are validated, the user can open **Task Scheduler** and see that **their task has been added**.

![image](https://github.com/user-attachments/assets/a94c6364-4a8d-46fe-971f-b3ada77dcd39)
![image](https://github.com/user-attachments/assets/eaacb8e5-bba2-4ab3-a1b3-3ca7e7c43eaa)

In case they have entered options they no longer like, they can simply run ``scheduler.jar`` again and overwrite the previous task with a new one.

## Backup Scheduler on Unix

## Backup Procedure
Once ``backup.jar`` runs, it will show the following:
* What file it is currently copying;
* The time elapsed;
* The number of files it has copied;
* The number of folders it has copied;
* The current file size of the backup.

![image](https://github.com/user-attachments/assets/5ecb9202-42bc-450b-9e6f-92812ba47b37)
![image](https://github.com/user-attachments/assets/ed1cf505-0b69-44a8-ad20-c5a9201fd27a)