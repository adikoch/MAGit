package UI;

import Classess.*;
import Classess.Commit;
import Classess.Folder;
import Classess.GitManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.out;

public class Runner {
    private Scanner scanInput = new Scanner(System.in);
    private GitManager manager = new GitManager();
    //private LinkedList<Commit> CommitsList= new LinkedList<>();


    public void run() {
        boolean isNumber = true, isValid = true;
        int result = 0;
        MainMenu menu = new MainMenu();
        String userInput = "0";
        while (!isValid || !userInput.equals("14")) {
            if (isValid) {
                out.println(System.lineSeparator());
                menu.show();
            }
            userInput = scanInput.nextLine();
            try {
                result = Integer.parseInt(userInput);
                isNumber = true;
            } catch (NumberFormatException e) {
                isNumber = false;
            }
            isValid = isNumber && !isOutOfRange(1, 14, result);

            if (isValid) {

                try {
                    sendToOption(result);
                } catch (Exception e) {
                    out.println("Please enter a valid input!");
                }
            } else out.println("Please enter a valid input!");
        }
    }


    private void sendToOption(int userInput) {
        switch (userInput) {
            case (1):
                UpdateUsername();//validation V
                break;

            case (2):
                ImportRepFromXML();
                break;

            case (3):
                SwitchRepository(false, null);//validation V
                break;

            case (4):
                ShowFilesOfCurrCommit();
                break;

            case (5):
                ShowStatus();
                break;

            case (6):
                Commit();
                break;

            case (7):
                ShowAllBranches();
                break;

            case (8):
                CreatBranch();// יווצא קובץ חדש בתיקייה branches, שבתוכו יש את הsha1 שכרגע מוצבע ע"י הhead הנוכחי,וגם הקומיט הנוכחי יהיה כמוהו, ושהhead הנוכחי עכשיו יצביע על הbranch שעכשיו יצרתי, להוסיף את הbranch לוגית ברפוזטורי
                break;

            case (9):
                DeleteBranch();
                break;

            case (10):
                CheckOut();
                break;

            case (11):
                ShowHistoryOfActiveBranch();
                break;

            case (12):
                createEmptyRepository();//validation V
                break;

            case (13):
                //  SwitchPointingOfHeadBranch();

            case (14):
                out.println("Goodbye");
                break;


        }


    }

    private void ShowHistoryOfActiveBranch() {
        if (manager.getGITRepository() == null) {
            out.println("There is no reposetory defined, therefor no active branch defined");
            return;
        }
        try {
            String historyOfActiveBranch = manager.ShowHistoryActiveBranch();
            out.println(historyOfActiveBranch);
        } catch (Exception e) {
            out.println("Opening zip file failed");
        }
    }

    private void UpdateUsername() {
        if (manager.getGITRepository() != null) {

            System.out.println("Please enter the new username:");
            String NewUserName = scanInput.nextLine();
            manager.updateNewUserNameInLogic(NewUserName);
        } else {
            out.println("There is no repository defined! no changes occurred");
        }
    }


    private void Commit() {
        if (manager.getGITRepository() == null) {
            out.println("There is no reposetory defined, commit cannot be applied");
            return;
        }

        System.out.println("Please enter description for the commit");
        Scanner sc = new Scanner(System.in);
        String description = sc.nextLine();
        try {
            manager.ExecuteCommit(description, true);
        } catch (Exception e) {
            out.println("Commit Failed! Unable to create zip files");
        }
        out.println("Deleted Files's Paths:" + manager.getDeletedFiles().toString());
        out.println("Added Files's Paths:" + manager.getCreatedFiles().toString());
        out.println("Updated Files's Paths:" + manager.getUpdatedFiles().toString());
        manager.getCreatedFiles().clear();
        manager.getDeletedFiles().clear();
        manager.getUpdatedFiles().clear();
        //CommitsList.add((newCommit));

    }


    private void CreatBranch() {
        if (manager.getGITRepository() == null) {
            out.println("There is no repository defined, cannot creat new branch");
            return;
        }
        String newBranchName;
        boolean isValid = false;
        Scanner sc = new Scanner(System.in);
        out.println("Please enter the name of the new branch");
        newBranchName = sc.nextLine();//getting the name

        while (!isValid) {
            for (Branch b : manager.getGITRepository().getBranches())                //checking if exist already
            {
                if (b.getBranchName().toLowerCase().equals(newBranchName.toLowerCase())) {
                    isValid = false;
                    out.println("Branch name already exist, please enter a different name");
                    newBranchName = sc.nextLine();//getting the name
                } else isValid = true;
            }
        }//valid:
        try {
            manager.CreatBranch(newBranchName);
        } catch (IOException e) {
            out.println("Reading text file failed");
        }
    }


    private void SwitchRepository(boolean isFromXml, Path pathFromXml) {//לטפל באנטרים ורווחים
        boolean isValid = false;
        Scanner sc = new Scanner(System.in);
        String repPath = null;
        Path newRepPath = null;

//        if (manager.getGITRepository() == null) {
//            if(!isFromXml)
//            {
//                createEmptyRepository();
//                return;
//            }
//        }
        if (!isFromXml) {
            out.println("Enter the path for the new repository: ");

            while (!isValid) {

                while (repPath == null || !Files.exists(newRepPath)) {
                    if (repPath != null) {
                        out.println("The wanted path does not exist, please try again");
                    }
                    repPath = sc.nextLine();
                    newRepPath = Paths.get(repPath);
                }
                String checkIfmagit = repPath + "\\" + ".magit";
                if (!Files.exists(Paths.get(checkIfmagit))) {
                    //the path exist but not magit
                    out.println("The wanted path is not a part of the magit system, please try again");
                    repPath = null;
///////////////מסתמכת על זה שהפאט שיש בתוך הקריאה של האקסמל הוא בסדר
                } else isValid = true;
            }

            //here the path i have exist, and is a part of the magit system
            //
            try {
                manager.switchRepository(newRepPath);
            } catch (IOException e) {
                out.println("opening zip file failed");
            } catch (IllegalArgumentException e) {
                out.println("was unable to generate folder from commit object");
            }// what to say
        } else {
            try {
                manager.switchRepository(pathFromXml);
            } catch (IOException e) {
                out.println("opening zip file failed");
            } catch (IllegalArgumentException e) {
                out.println("was unable to generate folder from commit object");
            }// what to say
        }


    }


    private void createEmptyRepository() {//לטפל באנטרים ורווחים
        Scanner sc = new Scanner(System.in);
        String repPath = null;
        String repName = null;

        out.println("Enter the path for the new repository: ");
        boolean pathIsExist = false;
        boolean nameIsExist = true;


        while (repPath == null || !pathIsExist) {

            if (repPath != null) {
                out.println("The wanted path does not exist, please try again");
            }
            repPath = sc.nextLine();
            try {
                pathIsExist = Files.exists(Paths.get(repPath));
            } catch (Exception e) {
                out.println("There was a problem reaching the path");
            }
            if (repPath.equals(""))
                pathIsExist = false;
        }

        out.println("Choose a name for the new repository: ");
        while (repName == null || nameIsExist) {
            if (repName != null) {
                out.println("The wanted name already exist, please try again");
            }
            repName = sc.nextLine();
            try {
                nameIsExist = Files.exists(Paths.get(repPath + "\\" + repName));
            } catch (Exception e) {
                out.println("There was a problem reaching the path");
            }
            if (repName.equals(""))
                repName = null;
        }

        try {
            manager.createEmptyRepositoryFolders(repPath, repName);
        } catch (Exception e) {
            out.println("File creation failed, nothing changed");
        }
    }


    void ShowStatus() {
        if (manager.getGITRepository() != null) {
            out.println("Repository's Name:" + manager.getGITRepository().getRepositoryName());
            out.println("Repository's Path:" + manager.getGITRepository().getRepositoryPath().toString());
            out.println("Repository's User:" + manager.getUserName());
            try {
                manager.ExecuteCommit("", false);
                out.println("Deleted Files's Paths:" + manager.getDeletedFiles());
                out.println("Added Files's Paths:" + manager.getCreatedFiles());
                out.println("Updated Files's Paths:" + manager.getUpdatedFiles());
                manager.getCreatedFiles().clear();
                manager.getDeletedFiles().clear();
                manager.getUpdatedFiles().clear();
            } catch (Exception e) {
                out.println("Show Status Failed! Unable to create files");
            }
        } else {
            out.println("There is no repository defined, no status to show");
        }


    }


    void ShowAllBranches() {
        if (manager.getGITRepository() == null) {
            out.println("There is no reposetory defined, no branches to show");
            return;
        }
        String allBranches = manager.getAllBranches();
        out.println(allBranches);
    }

    void DeleteBranch() {
        if (manager.getGITRepository() == null) {
            out.println("There is no reposetory defined, no branches to delete");
            return;
        }
        out.println("Please enter the name of the branch to delete");
        Scanner sc = new Scanner(System.in);
        String branchName = sc.nextLine();

        try {
            manager.DeleteBranch(branchName);
        } catch (Exception e) {
            out.println("Erasing the head branch is not a valid action, no changes occurred");
        }

    }

    private static boolean isOutOfRange(int min, int max, int val) {
        if (val >= min && val <= max)
            return false;
        return true;
    }

    private void CheckOut() {
        if (manager.getGITRepository() == null) {
            out.println("There is no reposetory defined, cannot check out");
            return;
        }
        out.println("Please enter the name of the branch to move over to");
        Scanner sc = new Scanner(System.in);

        String branchName = sc.nextLine();
        if (manager.getGITRepository().getBranchByName(branchName) != null) {
            try {
                manager.ExecuteCommit("", false);
                if (manager.getDeletedFiles().size() != 0 ||
                        manager.getUpdatedFiles().size() != 0 ||
                        manager.getCreatedFiles().size() != 0) {
                    out.println("There are unsaved changes in the WC. would you like to save it before checkout? (yes/no");
                    String toCommit = sc.nextLine();
                    if (toCommit.toLowerCase().equals("yes".toLowerCase())) {
                        try {
                            manager.ExecuteCommit("commit before checkout to " + sc.toString() + "Branch", true);
                        } catch (Exception e) {
                            out.println("Unable to create zip file");
                        }
                    }
                }
                manager.executeCheckout(branchName);
                manager.getCreatedFiles().clear();
                manager.getDeletedFiles().clear();
                manager.getUpdatedFiles().clear();
            } catch (Exception e) {
                out.println("Unable to create zip file");
            }
        }
        out.println("Branch does not exist!");
    }

    private void ShowFilesOfCurrCommit() {
        if (manager.getGITRepository() == null) {
            out.println("There is no repository defined, no files to show");
            return;
        }
        try {
            String s = manager.showFilesOfCommit();
            out.println(s);
        } catch (Exception e) {
            out.println("Unable to generate folder from commit object");
        }
    }


    public void ImportRepFromXML() {


        boolean isValid = false;
        boolean isExist = true;
        Scanner sc = new Scanner(System.in);
        String xmlPath = null;
        Path newXmlPath = null;


        out.println("Please enter the path to import xml from: ");
        while (!isValid) {
            if (!isExist) {
                try {
                    isExist = Files.exists(newXmlPath);
                } catch (SecurityException e) {
                    out.println("The wanted path does not exist, please try again");
                }
            }
            while (xmlPath == null || !isExist) {
                if (xmlPath != null) {
                    out.println("The wanted path does not exist, please try again");
                }
                xmlPath = sc.nextLine();
                if (!xmlPath.substring(xmlPath.length() - 4).equals(".xml")) {
                    xmlPath += "\\.xml";
                }

                try {
                    newXmlPath = Paths.get(xmlPath);
                }//return?
                catch (InvalidPathException e) {
                    isValid = false;
                    xmlPath = null;
                    out.println("The wanted path does not exist, please try again");
                }
            }
            String checkIfxml = xmlPath;
            try {
                isExist = Files.exists(Paths.get(checkIfxml));
            } catch (InvalidPathException e) {
                isExist = false;
                xmlPath = null;
                out.println("The wanted path is not an xml file, please try again");
            }
            if (!isExist) {
                //the path exist but not xml
                out.println("The wanted path is not an xml file, please try again");
                xmlPath = null;

            } else isValid = true;
        }//i have a valid path from the user, can call ImportRepositoryFromXML with xmlPath
        try {
            manager.ImportRepositoryFromXML(true, xmlPath);
        } catch (IOException e) {//קיים כבר רפוזטורי עם אותו שם באותה התיקייה שקיבלנו מהאקסמל
            //רוצה לשאול מה אתה רוצה לעשות
            //ask all the questions //
            // רוצה לקרוא לסוויצ רפוזטורי עם מה שחזר מהאקספשן// after catch, meaning the user want to over write the existing repo meaning sending to ImportRepositoryFromXML with e.
            boolean isValidOS = true;
            Scanner scanner = new Scanner(System.in);
            String userInput = null;

            out.println("There is already a reposetory with the same name at the wanted location");
            out.println("Please enter O to over write the existing, S to switch to the existing one");

            while (userInput == null || !isValidOS) {
                if (userInput != null) {
                    out.println("Invalid input, please try again");
                }
                userInput = scanner.nextLine();
                if (userInput.toLowerCase().equals("s")) // switch to the existing repo
                {//להוסיף את החלק שבודק האם מגיט מהסוויצ רפוזטורי
                    manager.setGITRepository(null);
                    SwitchRepository(true, Paths.get(e.getMessage()));//רוצה לשלוח עם התיקייה כלומר c:\repo1
                    isValidOS = true;
                } else if (userInput.toLowerCase().equals("o")) { // overwrite the existing
                    isValidOS = true;

                    manager.deleteFilesInFolder(new File(e.getMessage()));
                    manager.deleteFilesInFolder(new File(e.getMessage() + "\\.magit"));
                    try {
                        Files.delete((Paths.get(e.getMessage())));

                    }// delete the existing, prepering for loading , need the path of the folder to erase(c:\repo1)
                    catch (IOException e3) {
                        out.println("Could not delete the old reposetory, check if it is open some where else");
                    }//ואיך שהוא לחזור לתפריט הראשי
                    /////
                    try {
                        manager.ImportRepositoryFromXML(false, xmlPath);
                    } catch (Exception e4) {
                        out.println("Could not import from xml");
                    }

                } else//not o not s
                {
                    isValidOS = false;
                }
            }
        }
        //over write= במקום מה שיש בתיקייה תהיה את התיקייה החדשה
        //switch=לשלוח לסוויץ המקורית עם הפאט שקיבלתי בתוך managerReposetory
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


        //here have the path of the xml, before importing reposetory from xml want to check if i already have a reposetory in the wc or not
        //if i have a reposetory, ask if the user would like to change to the new reposetory or not.
        //                                                                                          then do everything above
        //                                                                                          else dont do nothing
        //אם כבר יש לי תיקייה בשם הזה בתוך ה
        //

    }
}
//    public void SwitchPointingOfHeadBranch()
//    {
//
//        Boolean isExist=false;
//        Scanner sc= new Scanner(System.in);
//        String userInput=null;
//        Path pathToSearchZip=null;
//        Commit newCommit;
//        Folder folderOfCommit;
//
//        while(userInput==null||!isExist)
//        {
//            if(userInput==null)
//            {
//                out.println("Please enter the sha1 you would like to point by the head branch");
//            }
//            userInput=sc.nextLine();
//            pathToSearchZip=Paths.get(manager.getGITRepository().getRepositoryPath().toString()+"\\.magit\\objects\\"+userInput+".zip");//c:\rep\.magit\objects
//            isExist=Files.exists(pathToSearchZip);//isExist true=> לבדוק אם זה באמת זיפ של קומיט או של בלוב או פולדר
//        }//sha1 takin
//
//        try {newCommit= manager.getCommitFromSha1UsingFiles(manager.getGITRepository().getRepositoryPath().toString(), userInput);}
//        catch (Exception e) {
//            out.println(e.getMessage());
//            return;}
//        try {newCommit.setRootFolder(manager.generateFolderFromCommitObject(newCommit.getRootFolderSHA1()));}
//        catch(Exception e) {out.println(e.getMessage());}
//
//        //checking if there are open changes in the WC
//        try {
//            manager.ExecuteCommit("", false);
//            if (manager.getDeletedFiles().size() != 0 ||
//                    manager.getUpdatedFiles().size() != 0 ||
//                    manager.getCreatedFiles().size() != 0) {
//                out.println("There are unsaved changes in the WC. would you like to save it before checkout? (yes/no");
//                String toCommit = sc.nextLine();
//                if (toCommit.toLowerCase().equals("yes".toLowerCase())) {
//                    try{
//                        manager.ExecuteCommit("commit before checkout to " + sc.toString() + "Branch", true);}
//                    catch(Exception er) {
//                        out.println("Unable to create zip file");
//                    }
//                }
//            }
//
//            //text file update:
//            manager.updateFile(userInput);
//            manager.getGITRepository().getHeadBranch().setPointedCommit(newCommit);
//
//
//            manager.executeCheckout(manager.getGITRepository().getHeadBranch().getBranchName());
//            manager.getCreatedFiles().clear();
//            manager.getDeletedFiles().clear();
//            manager.getUpdatedFiles().clear();
//        } catch (Exception er) {
//            out.println("Unable to create zip file");
//        }
//
////        try {manager.switchPointingOfHeadBranch(userInput);}//with the wanted sha1, after checking if there are open changes and deciding what to do
////        catch (Exception err) {
////            out.println(err.getMessage());
////            return;}
//        ShowFilesOfCurrCommit();
//
//    }
//}