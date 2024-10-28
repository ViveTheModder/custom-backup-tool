# custom-backup-tool
A program in Java that makes backups (for all OS's) and schedules them (only on Windows atm).

# Usage/Breakdown
For the program to work, it needs an input directory and an output directory, which it takes from ``dirs.txt``, stored in the ``config`` folder.

So yes, the program can only back up one folder (and its files & subfolders), although support to back up multiple folders can be added in the future.

To schedule the backup procedure, again, **on Windows**, just run ``scheduler.jar`` and you will be greeted with this interface:
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

By the way, for the task to work as it should, I had to make the program create a ``scheduler.bat`` file, which is what the scheduler runs, and a ``task.bat`` file, which will actually run ``backup.jar`` in the right directory.

Running it straight from Task Scheduler caused problems, because it assumed the ``dirs.txt`` was stored in ``C:\Windows\System32``... *sigh*.
