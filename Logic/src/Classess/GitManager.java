package Classess;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import generated.*;


import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.server.ExportException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class GitManager {

    //members
    private Repository GITRepository;
    private String userName;

    //    private class diffLogClass {
    private LinkedList<Path> updatedFiles = new LinkedList<>();
    private LinkedList<Path> createdFiles = new LinkedList<>();
    private LinkedList<Path> deletedFiles = new LinkedList<>();
    // }

    Map<String, Commit> commitTempMap;
    Map<String, Folder.Component> folderTempMap;
    Map<String, Folder.Component> blobTempMap;


    //get\set
    public void setGITRepository(Repository GITRepository) { this.GITRepository = GITRepository; }

    public LinkedList<Path> getUpdatedFiles() {
        return updatedFiles;
    }

    public LinkedList<Path> getCreatedFiles() {
        return createdFiles;
    }

    public LinkedList<Path> getDeletedFiles() {
        return deletedFiles;
    }

    public Repository getGITRepository() {
        return GITRepository;
    }

    public String getUserName() {
        return userName;
    }

    public void updateNewUserNameInLogic(String NewUserName) {
        userName = NewUserName;
    }


    static String generateSHA1FromString(String str) {
        return org.apache.commons.codec.digest.DigestUtils.sha1Hex(str);
    }


    //methods
    public void ExecuteCommit(String description, Boolean isCreateZip) throws Exception {  //V
        Path ObjectPath = Paths.get(GITRepository.getRepositoryPath().toString() + "\\.magit\\Objects");
        Path BranchesPath = Paths.get(GITRepository.getRepositoryPath().toString() + "\\.magit\\Branches");
        String headBranch = readTextFile(BranchesPath + "\\Head");
        String prevCommitSHA1 = readTextFile(BranchesPath + "\\" + headBranch);//לזה נעשה אןזיפ וגם לקובץ שהשם שלו הוא הsha1 שכתוב פה
        //Date
        //String creationDate = GitManager.
        // getDate();

        Folder newFolder = GenerateFolderFromWC(GITRepository.getRepositoryPath());// ייצג את הספרייה הראשית
        Folder oldFolder = GITRepository.getHeadBranch().getPointedCommit().getRootFolder();
        if (!generateSHA1FromString(newFolder.getFolderContentString()).equals(generateSHA1FromString(oldFolder.getFolderContentString()))) {
            createShaAndZipForNewCommit(newFolder, oldFolder, isCreateZip, GITRepository.getRepositoryPath());


            if (isCreateZip) {
                Commit c = new Commit(description, userName);
                String anotherPrev = GITRepository.getHeadBranch().getPointedCommit().getSHA1PreveiousCommit();//האם יש עוד אבא
                if (anotherPrev != null) {
                    c.setSHA1anotherPreveiousCommit(anotherPrev);
                }
                GITRepository.getHeadBranch().setPointedCommit(c); //creation
                GITRepository.getHeadBranch().getPointedCommit().setSHA1PreveiousCommit(prevCommitSHA1); //setting old commits sha1
                GITRepository.getHeadBranch().getPointedCommit().setRootFolder(newFolder); //setting old commit
                GITRepository.getHeadBranch().getPointedCommit().setRootFolderSHA1(generateSHA1FromString(newFolder.getFolderContentString()));
                GITRepository.getHeadBranch().getPointedCommit().setCommitFileContentToSHA(); //
                GITRepository.getCommitList().put(GITRepository.getHeadBranch().getPointedCommit().getSHA(), GITRepository.getHeadBranch().getPointedCommit()); //adding to commits list of the current reposetory
                createFile(GITRepository.getHeadBranch().getBranchName(), GITRepository.getHeadBranch().getPointedCommit().getSHA(), BranchesPath, new Date().getTime());
                GITRepository.getHeadBranch().setPointedCommitSHA1(c.getSHA());
                createZipFile(ObjectPath, generateSHA1FromString(newFolder.getFolderContentString()), newFolder.getFolderContentString());

                createFileInMagit(GITRepository.getHeadBranch().getPointedCommit(), GITRepository.getRepositoryPath());


                createFileInMagit(GITRepository.getHeadBranch().getPointedCommit(), GITRepository.getRepositoryPath());

            }
        }
    }

    private void createShaAndZipForNewCommit(Folder newFolder, Folder oldFolder, Boolean isCreateZip, Path path) throws IOException { //V
        ArrayList<Folder.Component> newComponents = new ArrayList<>();
        ArrayList<Folder.Component> oldComponents = new ArrayList<>();
        Path objectPath = Paths.get(GITRepository.getRepositoryPath().toString() + "\\.magit\\Objects");
        int oldd = 0;
        int neww = 0;

        if ((oldFolder != null) && (newFolder != null)) {
            oldComponents = oldFolder.getComponents();
            newComponents = newFolder.getComponents();
            if (!oldComponents.isEmpty() && !newComponents.isEmpty()) {

// indexes of the component in the lists
                while (oldd < oldComponents.size() && neww < newComponents.size()) { // while two folders are not empty
                    if (oldComponents.get(oldd).getComponentName().equals(newComponents.get(neww).getComponentName())) { // if names are the same
                        if (oldComponents.get(oldd).getComponentSHA1().equals(newComponents.get(neww).getComponentSHA1())) { //if sha1 is the same
                            //point old object
                            newComponents.set(neww, oldComponents.get(oldd)); // if nothing changed, point at the original tree
                            neww++;
                            oldd++;
                        } else if (oldComponents.get(oldd).getComponentType().equals(newComponents.get(neww).getComponentType())) { //different sha1, updated file
                            if (oldComponents.get(oldd).getComponentType().equals(FolderType.Folder)) {
                                Folder newf = (Folder) newComponents.get(neww).getDirectObject();
                                Folder oldf = (Folder) oldComponents.get(oldd).getDirectObject();

                                createShaAndZipForNewCommit(newf, oldf, isCreateZip, Paths.get(path.toString() + "\\" + oldComponents.get(oldd).getComponentName()));
                                String newSHA = generateSHA1FromString(newf.getFolderContentString());
                                String oldSHA = generateSHA1FromString(oldf.getFolderContentString());

                                if (newSHA.equals(oldSHA)) { //if sha1 is the same
                                    newComponents.set(neww, oldComponents.get(oldd)); // if nothing changed, point at the original tree
                                }
                                if (isCreateZip == Boolean.TRUE) {
                                    createZipFile(objectPath, generateSHA1FromString(newf.getFolderContentString()), newf.getFolderContentString());
                                }
                                neww++;
                                oldd++;
                            } else {
                                //both blob - updated
                                if (isCreateZip == Boolean.TRUE) {
                                    File f = new File(objectPath.toString() + "\\" + newComponents.get(neww).getComponentSHA1() + ".zip");
                                    if (!f.exists()) {
                                        Blob b = (Blob) newComponents.get(neww).getDirectObject();
                                        createZipFile(objectPath, newComponents.get(neww).getComponentSHA1(), b.getContent());
                                        //add updated file zip
                                    }
                                }
                                //add to path
                                this.updatedFiles.add(Paths.get(path.toString() + "\\" + newComponents.get(neww).getComponentName()));
                                neww++;
                                oldd++;
                            }
                        }
                    } else {
                        int result = newComponents.get(neww).getComponentName().compareTo(oldComponents.get(oldd).getComponentName());
                        if (result > 0) {
                            //file was deleted from old
                            //add to list
                            if (oldComponents.get(oldd).getComponentType().equals(FolderType.Folder)) {
                                Folder f = (Folder) oldComponents.get(oldd).getDirectObject();

                                createShaAndZipForNewCommit(null, f, isCreateZip, Paths.get(path.toString() + "\\" + oldComponents.get(oldd).getComponentName()));
                            }
                            this.deletedFiles.add(Paths.get(path.toString() + "\\" + oldComponents.get(oldd).getComponentName()));
                            oldd++;

                        } else {
                            //new file was added
                            //add new zip
                            //createZipFile(path,newComponents.get(neww).getComponentSHA1(),newComponents.get(neww).);

                            //add to list
                            if (newComponents.get(neww).getComponentType().equals(FolderType.Blob)) {
                                if (isCreateZip == Boolean.TRUE) {
                                    File f = new File(objectPath.toString() + "\\" + newComponents.get(neww).getComponentSHA1() + ".zip");
                                    if (!f.exists()) {
                                        Blob b = (Blob) newComponents.get(neww).getDirectObject();
                                        createZipFile(objectPath, newComponents.get(neww).getComponentSHA1(), b.getContent());
                                    }
                                }
                            } else {
                                Folder f = (Folder) newComponents.get(neww).getDirectObject();

                                //Folder f = new Folder(newComponents.get(neww));
                                createShaAndZipForNewCommit(f, null, isCreateZip, Paths.get(path.toString() + "\\" + newComponents.get(neww).getComponentName()));
                                if (isCreateZip == Boolean.TRUE) {

                                    createZipFile(objectPath, generateSHA1FromString(f.getFolderContentString()), f.getFolderContentString());
                                }
                            }
                            this.createdFiles.add(Paths.get(path.toString() + "\\" + newComponents.get(neww).getComponentName()));
                            neww++;
                        }
                    }
                }
            }
        }

        if (oldFolder != null) {
            oldComponents = oldFolder.getComponents();
            while (oldd < oldComponents.size()) {
                if (oldComponents.get(oldd).getComponentType().equals(FolderType.Folder)) {
                    Folder f = (Folder) oldComponents.get(oldd).getDirectObject();

                    //Folder f = new Folder(newComponents.get(neww));
                    createShaAndZipForNewCommit(null, f, isCreateZip, Paths.get(path.toString() + "\\" + oldComponents.get(oldd).getComponentName()));
                }
                this.deletedFiles.add(Paths.get(path.toString() + "\\" + oldComponents.get(oldd).getComponentName()));
                oldd++;
            }
        }

        if (newFolder != null) {
            newComponents = newFolder.getComponents();
            while (neww < newComponents.size()) {
                if (newComponents.get(neww).getComponentType().equals(FolderType.Blob)) {
                    if (isCreateZip == Boolean.TRUE) {
                        File f = new File(objectPath.toString() + "\\" + newComponents.get(neww).getComponentSHA1() + ".zip");
                        if (!f.exists()) {
                            Blob b = (Blob) newComponents.get(neww).getDirectObject();
                            createZipFile(objectPath, newComponents.get(neww).getComponentSHA1(), b.getContent());
                        }
                    }
                } else {
                    Folder f = (Folder) newComponents.get(neww).getDirectObject();

                    //Folder f = new Folder(newComponents.get(neww).getDirectObject().);
                    createShaAndZipForNewCommit(f, null, isCreateZip, Paths.get(path.toString() + "\\" + newComponents.get(neww).getComponentName()));
                    if (isCreateZip == Boolean.TRUE) {

                        createZipFile(objectPath, generateSHA1FromString(f.getFolderContentString()), f.getFolderContentString());
                    }
                }
                this.createdFiles.add(Paths.get(path.toString() + "\\" + newComponents.get(neww).getComponentName()));
                neww++;
            }
        }
    }

    private Folder GenerateFolderFromWC(Path currentPath)  {
        File[] allFileComponents = currentPath.toFile().listFiles();
        String sh1Hex = "";
        String fileContent = "";
        String objectsPath = currentPath + "\\Objects";
        Folder currentFolder = new Folder();

        for (File f : allFileComponents) {
            if (!f.getName().equals(".magit")) {
                if (!f.isDirectory()) {
                    fileContent = readTextFile(f.toString());
                    sh1Hex = generateSHA1FromString((fileContent));
                    //לוגית יוצרת את האובייקט שהוא קומפוננט שמתאר בלוב
                    Folder.Component newComponent = new Folder.Component(f.getName(), sh1Hex, FolderType.Blob, userName, getDateFromObject(f.lastModified()));
                    newComponent.setDirectObject(new Blob(fileContent));
                    currentFolder.getComponents().add(newComponent);

                } else {
                    Folder folder = GenerateFolderFromWC(Paths.get(f.getPath()));
                    sh1Hex = generateSHA1FromString(folder.stringComponentsToString());

                    Folder.Component newComponent = new Folder.Component(f.getName(), sh1Hex, FolderType.Folder, userName, getDateFromObject(f.lastModified()));
                    newComponent.setDirectObject(new Folder(folder.getComponents()));
                    currentFolder.getComponents().add(newComponent);
                    Collections.sort(currentFolder.getComponents());
                }
            }
        }
        Collections.sort(currentFolder.getComponents());
        return currentFolder;
    }

    public void CreatBranch(String newBranchName) throws IOException {
        Path pathOfNewFile = Paths.get(getGITRepository().getRepositoryPath().toString() + "\\" + ".magit\\branches\\");
        String nameOfBranch = readTextFile(getGITRepository().getRepositoryPath().toString() + "\\" + ".magit\\branches\\Head");//name of main branch
        String sha1OfCurrCommit = readTextFile(getGITRepository().getRepositoryPath().toString() + "\\" + ".magit\\branches\\" + nameOfBranch);//sha1 of main commit
        createFile(newBranchName, sha1OfCurrCommit, pathOfNewFile, new Date().getTime());// a file created in branches

        Branch newBranch = new Branch(newBranchName, GITRepository.getHeadBranch().getPointedCommit().getSHA());
        newBranch.setPointedCommit(GITRepository.getHeadBranch().getPointedCommit());//creating and initialising

        GITRepository.getBranches().add(newBranch);//adding to logic//not good

    }

    public void DeleteBranch(String FileName) throws Exception { //V
        if (FileName.equals(readTextFile(GITRepository.getRepositoryPath().toString() + "\\" + ".magit\\branches\\Head")))
            throw new Exception();
        File file = new File(GITRepository.getRepositoryPath().toString() + "\\" + ".magit\\branches\\" + FileName);
        file.delete();//// erasing it physically

        Branch branch = GITRepository.getBranchByName(FileName);
        GITRepository.getBranches().remove(branch);// erasing it logically

    }

    public void createEmptyRepositoryFolders(String repPath, String repName) throws Exception {//V
        if (repPath.substring(repPath.length() - 1) != "/") {
            repPath += "\\";
        }

        new File(repPath + repName + "\\.magit\\objects").mkdirs();
        new File(repPath + repName + "\\.magit\\branches").mkdirs();
        Path workingPath = Paths.get(repPath + repName + "\\");
        this.GITRepository = (new Repository(workingPath, new Branch("Master")));
        GITRepository.getHeadBranch().setPointedCommit(new Commit());
        //GITRepository.getHeadBranch().getPointedCommit().setRootfolder(workingPath.toString());
        GITRepository.getHeadBranch().getPointedCommit().setCommitFileContentToSHA();
        GITRepository.getHeadBranch().setPointedCommitSHA1(GITRepository.getHeadBranch().getPointedCommit().getSHA());
        //Create commit file

        createFileInMagit(GITRepository.getHeadBranch().getPointedCommit(), workingPath);//commit
        createFileInMagit(GITRepository.getHeadBranch(), workingPath);

        createFile("Head", "Master", Paths.get(repPath + repName + "\\.magit\\branches"), new Date().getTime());

        GITRepository.getBranchByName("Master").setPointedCommit(GITRepository.getHeadBranch().getPointedCommit());

//            //create origcommit
        Folder folder = GenerateFolderFromWC(GITRepository.getRepositoryPath());
        GITRepository.getHeadBranch().getPointedCommit().setRootFolder(folder);
        this.userName = "Administrator";
        GITRepository.getHeadBranch().getPointedCommit().setRootFolder(folder);
    }

    private boolean isFileMagit(String repPath) {
        return Files.exists(Paths.get(repPath));
    }

    public void switchRepository(Path newRepPath) throws IOException, IllegalArgumentException //V
    {

        File f = Paths.get(newRepPath.toString() + "\\.magit\\branches\\Head").toFile();
        String content = readTextFile(newRepPath + "\\.magit\\branches\\" + f.getName());
        String name = readTextFile(newRepPath + "\\.magit\\branches\\" + content);
        this.GITRepository = new Repository(newRepPath);

        this.GITRepository.getRepositorysBranchesObjects();
        GITRepository.Switch(newRepPath);
        GITRepository.setHeadBranch(GITRepository.getBranchByName(content));


        getCommitForBranches(newRepPath);


    }


    public boolean doesPathExist(String path) {
        return Files.exists(Paths.get(path));
    }


    /*
        public void switchRepository(Path newRepPath)
            throws ExceptionInInitializerError, UnsupportedOperationException, IllegalArgumentException, IOException {
        Path checkIfMagit = Paths.get(newRepPath + "\\.magit");
        if (Files.exists(newRepPath)) {
            if (Files.exists(checkIfMagit)) {

                File f = Paths.get(newRepPath.toString() + "\\.magit\\branches\\Head").toFile();
                String content = readTextFile(newRepPath + "\\.magit\\branches\\" + f.getName());
                String name = readTextFile(newRepPath + "\\.magit\\branches\\" + content);
                this.GITRepository = new Repository(newRepPath);

                this.GITRepository.getRepositorysBranchesObjecets();
                GITRepository.Switch(newRepPath);
                GITRepository.setHeadBranch(GITRepository.getBranchByName(content));


                getCommitForBranches(newRepPath);
            } else throw new ExceptionInInitializerError();//exeption forG not being magit

        } else throw new IllegalArgumentException();//exception for not existing

    }
     */

    public void getCommitForBranches(Path newRepPath) throws IOException, IllegalArgumentException { //V
        Folder folder = null;
        String commitContent = null;
        for (Branch b : GITRepository.getBranches()) {
            Path commitPath = Paths.get(newRepPath + "\\.magit\\objects\\" + b.getPointedCommitSHA1() + ".zip");
            try {
                commitContent = extractZipFile(commitPath);
            } catch (IOException e) {
                throw new IOException();
            }// opening zip file failed
            BufferedReader br = new BufferedReader(new StringReader(commitContent));
            ArrayList<String> st = new ArrayList<>();
            String a;
            int i = 0;
            while ((a = br.readLine()) != null) {
                if (a.equals("null")) {
                    st.add(i, null);
                } else {
                    st.add(i, a);
                }
                i++;
            }
            Commit newCommit = new Commit(st);

            b.setPointedCommit(newCommit);
            //GITRepository.getRepositoryName() = ךהחליף שם של רפוסיטורי
            //לא יצרנו קומיט שההד יצביע עליו כי אין צורך
            try {
                folder = generateFolderFromCommitObject(newCommit.getRootFolderSHA1());
            } catch (IOException er) {
                throw new IllegalArgumentException();
            }// was unable to generateFolderFromCommitObject
            b.getPointedCommit().setRootFolder(folder);
            newCommit.setCommitFileContentToSHA();
            br.close();
            GITRepository.getCommitList().put(newCommit.getSHA(), newCommit);
        }
    }

    public Folder generateFolderFromCommitObject(String rootFolderName) throws IOException {//V
        Path ObjectPath = Paths.get(GITRepository.getRepositoryPath().toString() + "\\.magit\\Objects");
        String folderContent = extractZipFile(Paths.get(ObjectPath + "\\" + rootFolderName + ".zip"));
        Folder currentFolder = new Folder();

        currentFolder.setComponents(currentFolder.setComponentsFromString(folderContent));

        for (Folder.Component c : currentFolder.getComponents()) {

            if (c.getComponentType().equals(FolderType.Blob)) {
                String blocContent = extractZipFile(Paths.get(ObjectPath + "\\" + c.getComponentSHA1() + ".zip"));
                Blob b = new Blob(blocContent);
                c.setDirectObject(b);
            } else {
                Folder folder = generateFolderFromCommitObject(c.getComponentSHA1());
                Collections.sort(folder.getComponents());
                c.setDirectObject(folder);
            }
        }
        return currentFolder;
    }


    private static void createFileInMagit(Object obj, Path path) throws Exception {//V
        Path magitPath = Paths.get(path.toString() + "\\.magit");
        Path objectsPath = Paths.get(magitPath.toString() + "\\objects");
        Path branchesPath = Paths.get(magitPath.toString() + "\\branches");

        if (obj instanceof Commit) {
            createCommitZip((Commit) obj, objectsPath);
        } else if (obj instanceof Branch) {
            Branch branch = (Branch) obj;
            createFile(branch.getBranchName(), branch.getPointedCommit().getSHA(), branchesPath, new Date().getTime());
        } else if (obj instanceof Folder) {
            createFolderZip((Folder) obj, objectsPath);
        } else if (obj instanceof Blob) {
            createBlobZip((Blob) obj, objectsPath);
        } else throw new Exception();

    }

    private static void createCommitZip(Commit commit, Path path) throws IOException {//V

        createZipFile(path, commit.getSHA(), commit.getSHAContent());
    }


    private static void createFolderZip(Folder folder, Path path) throws IOException {//V
        String content = folder.stringComponentsToString();
        String SHA = generateSHA1FromString(content);

        createZipFile(path, SHA, content);
    }

    private static void createBlobZip(Blob blob, Path path) throws IOException {//V
        String content = blob.getContent();
        String SHA = generateSHA1FromString(content);

        createZipFile(path, SHA, content);
    }

    public static String extractZipFile(Path path) throws IOException {//V
        ZipFile zip = new ZipFile(path.toString());
        ZipEntry entry = zip.entries().nextElement();
        StringBuilder out = getTxtFiles(zip.getInputStream(entry));
        return out.toString();
    }

    private static StringBuilder getTxtFiles(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            reader.close();
        }
        return out;
    }

    private static void createZipFile(Path path, String fileName, String fileContent) throws IOException {
        File f = new File(path + "\\" + fileName + ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
        ZipEntry e = new ZipEntry(fileName);
        out.putNextEntry(e);

        byte[] data = fileContent.getBytes();
        out.write(data, 0, data.length);
        out.closeEntry();
        out.close();
    }

    public static void createFile(String fileName, String fileContent, Path path, long date) { // gets a name for new file,what to right inside, where to put it
        Writer out = null;

        File file = new File(path + "\\" + fileName);
        try {
            out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file)));
            out.write(fileContent);
            out.close();
            file.setLastModified(date);
        } catch (IOException e) {
            e.fillInStackTrace();
        }

    }

    public String getAllBranches() {
        StringBuilder toReturn = new StringBuilder();
        for (Branch b : GITRepository.getBranches()) {
            if(GITRepository.getHeadBranch().getBranchName().equals(b.getBranchName()))
            {
                toReturn.append("Head");
                toReturn.append(System.lineSeparator());

            }
            toReturn.append("Branch name: ");
            toReturn.append(b.getBranchName());
            toReturn.append(System.lineSeparator());
            toReturn.append(b.getPointedCommitSHA1());
            toReturn.append(System.lineSeparator());
            toReturn.append(b.getPointedCommit().getDescription());
            toReturn.append(System.lineSeparator());
        }
        return toReturn.toString();
    }


    public String ShowHistoryActiveBranch() throws Exception {//V
        String sha1OfMainBranch = GITRepository.getHeadBranch().getPointedCommit().getSHA();
        return ShowHistoryActiveBranchRec(sha1OfMainBranch);
    }


    public String ShowHistoryActiveBranchRec(String sha1OfMainBranch) throws Exception {//V

        StringBuilder sb = new StringBuilder();
        Commit commit = GITRepository.getCommitList().get(sha1OfMainBranch);
        if (commit == null) {
            //get commit from the files
            commit = getCommitFromSha1UsingFiles(GITRepository.getRepositoryPath().toString(), sha1OfMainBranch);
            //add it to the list of commits (only a part of its fields are there
            GITRepository.getCommitList().put(sha1OfMainBranch, commit);
        }
        //either way now have a commit in my hands

        sb.append(commit.getSHA());
        sb.append(System.lineSeparator());
        sb.append(commit.getDescription());
        sb.append(System.lineSeparator());
        sb.append(commit.getCreationDate());//date
        sb.append(System.lineSeparator());
        sb.append(commit.getChanger());
        sb.append(System.lineSeparator());
        if (commit.getSHA1PreveiousCommit() == null)// difault commit
        {
            return sb.toString();
        }
        //
        return sb.toString() + System.lineSeparator() + ShowHistoryActiveBranchRec(commit.getSHA1PreveiousCommit());
    }


    public Commit getCommitFromSha1UsingFiles(String path, String sha1) throws Exception {//V

        //read the file in folder objects that its name is sha1
        //take all i need to a commit
        // return it
        Path commitPath = Paths.get(path.toString() + "\\.magit\\objects\\" + sha1 + ".zip");
        String commitContent = extractZipFile(commitPath);
        BufferedReader br = new BufferedReader(new StringReader(commitContent));
        ArrayList<String> st = new ArrayList<>();
        String a;
        int i = 0;
        while ((a = br.readLine()) != null) {
            if (a.equals("null")) {
                st.add(i, null);
            } else {
                st.add(i, a);
            }
            i++;
        }

        StringBuilder sha1ContentToNewCommit = new StringBuilder();
        int m = 0;
           /* while(!st.isEmpty())
            {

                sha1ContentToNewCommit.append(st.get(m));
                sha1ContentToNewCommit.append(System.lineSeparator());
                m++;
            }
*/
        Commit newCommit = new Commit(st);
        newCommit.setSHAContent(commitContent);
        return newCommit;
    }


    public static String readTextFile(String filePath) { //V
        File f = new File(filePath);
        StringBuilder content = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(f.getPath()), StandardCharsets.UTF_8)) {
            stream.forEach(s -> content.append(s).append("\n"));
            if (content.length() > 0) {
                content.deleteCharAt(content.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return content.toString();
    }
//    {
//        String returnValue = "";
//        String line;
//        FileReader file;
//
//        file = new FileReader(filePath);
//
//
//        BufferedReader reader = new BufferedReader(file);
//
//        while ((line = reader.readLine()) != null) {
//            returnValue += line;
//            returnValue+='\n';
//        }
//        returnValue.
//        reader.close();
//        return returnValue;
//    }

    public static String getDateFromObject(Object date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        if (date != null) {
            return dateFormat.format(date);
        }
        return dateFormat.format(new Date());
    }

    public static long getDateFromString(String date) throws Exception {
        long returnedDate;
//        Date date1=new SimpleDateFormat("dd.MM.YYYY - hh:mm:ss:sss").parse(date);//////////////ענביתתתתתתת
//        return date1;
//long fj = date.to
        DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        try{returnedDate = sdf.parse(date).getTime();}
        catch(ParseException e){throw new Exception("Invalid date structure");}
        return  returnedDate;
    }

    @Override
    public String toString() {
        String separator = System.lineSeparator() + "*" + System.lineSeparator();
        StringBuilder sb = new StringBuilder();

        sb.append("All the updated files:");
        sb.append(separator);
        for (Path updatedFilePath : updatedFiles) {
            sb.append(updatedFilePath.toString());
            sb.append(System.lineSeparator());
        }

        sb.append("All the created files:");
        sb.append(separator);
        for (Path addedFilePath : createdFiles) {
            sb.append(addedFilePath.toString());
            sb.append(System.lineSeparator());
        }

        sb.append("All the deleted files:");
        sb.append(separator);
        for (Path deletedFilePath : deletedFiles) {
            sb.append(deletedFilePath.toString());
            sb.append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());

        return sb.toString();
    }

    public void executeCheckout(String branchName) throws Exception {
        Branch b = GITRepository.getBranchByName(branchName);
        //add question to user

        GITRepository.setHeadBranch(b);
        deleteFilesInFolder(GITRepository.getRepositoryPath().toFile());
        Commit c = GITRepository.getHeadBranch().getPointedCommit();

        //
        String fileName= getGITRepository().getRepositoryPath()+"\\.magit\\branches\\head";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        //DELETE THE OLD NAME INSIDE
        writer.write(branchName);

        writer.close();

        createFilesInWCFromCommitObject(c.getRootFolder(), GITRepository.getRepositoryPath());
    }

    public void deleteFilesInFolder(File mainFile) {
        File[] allFileComponents = mainFile.listFiles();
        for (File f : allFileComponents) {
            if (!f.getName().equals(".magit")) {
                if (f.isDirectory()) {
                    deleteFilesInFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        mainFile.delete();
    }

    public void createFilesInWCFromCommitObject(Folder rootFolder, Path pathForFile) throws Exception {
        //
        for (Folder.Component c : rootFolder.getComponents()) {

            if (c.getComponentType().equals(FolderType.Blob)) {
                Blob b = (Blob) c.getDirectObject();
                createFile(c.getComponentName(), b.getContent(), pathForFile,getDateFromString(c.getLastUpdateDate()));/////////////gbch,,,,,
            } else {
                new File(pathForFile.toString() + "\\" + c.getComponentName()).mkdirs();
                Folder f = (Folder) c.getDirectObject();
                createFilesInWCFromCommitObject(f, Paths.get(pathForFile.toString() + "\\" + c.getComponentName()));
                File file =Paths.get(pathForFile.toString() + "\\" + c.getComponentName()).toFile();
                file.setLastModified(getDateFromString(c.getLastUpdateDate()));
            }
        }

    }

    //inbar, need to debug

    public String showFilesOfCommit() throws IOException {
        Commit commit = GITRepository.getHeadBranch().getPointedCommit();
        //build a folder that represents the commit
        Folder folder = generateFolderFromCommitObject(commit.getRootFolderSHA1());
        return showFilesOfCommitRec(folder, "");
    }


    //commitFolder
    public String showFilesOfCommitRec(Folder rootFolder, String toPrint) {

        StringBuilder builder = new StringBuilder();
        builder.append(toPrint);
        for (Folder.Component c : rootFolder.getComponents()) {

            if (c.getComponentType().equals(FolderType.Blob)) {
                builder.append(c.getComponentsStringFromComponent());
                //return builder.toString();
                //add blob component to string, return;
            } else {

                builder.append(c.getComponentsStringFromComponent());
                builder.append(System.lineSeparator());
                builder.append(c.getComponentName());
                builder.append(" items:");
                builder.append(System.lineSeparator());
                builder.append(showFilesOfCommitRec((Folder) c.getDirectObject(), toPrint));

            }
            if(rootFolder.getComponents().size() != 1) {
                builder.append(System.lineSeparator());
            }

        }
        if(rootFolder.getComponents().size() != 1) {

            builder.append(System.lineSeparator());
        }
        return builder.toString();

    }

//    public static File getFileFromSHA1(String ShA1, Path path) {
//        Path objectsPath = Paths.get(path.toString() + "\\objects");
//        File f = Paths.get(objectsPath + ShA1 + ".zip").toFile();
//        return f;
//
//    }
//    public static String generateSHA1FromFile(File file) {
//        String str = file.toString();
//        return generateSHA1FromString(str);
//    }

    //    public void ImportRepFromXML() {
//
//    }
//
//    public void ShowFilesOfCurrCommit() {
//
//    }

    public void ImportRepositoryFromXML(Boolean isCreateFiles, String xmlPath) throws Exception {//V
        if(isCreateFiles)
        {
            MagitRepository oldRepository;
            try{oldRepository= Repository.loadFromXml(xmlPath);}
            catch(Exception e) {throw new Exception("Unable to load from xml");}
            GITRepository = new Repository(Paths.get(oldRepository.getLocation()));
            convertOldRepoToNew(oldRepository);

            if (Files.exists(Paths.get(oldRepository.getLocation())))//כבר קיים רפוזטורי כזה בעל אותו השם באותו המקום
            {
                throw new IOException(oldRepository.getLocation());//go ask question, with the location, c:\repo1
            }
        }
        createMagitFiles();
        createFilesInWCFromCommitObject(GITRepository.getHeadBranch().getPointedCommit().getRootFolder(), GITRepository.getRepositoryPath());
        this.userName = GITRepository.getHeadBranch().getPointedCommit().getChanger();
        this.blobTempMap.clear();
        this.folderTempMap.clear();
        this.commitTempMap.clear();
    }

    public void convertOldRepoToNew(MagitRepository oldRepository)throws Exception {

        GITRepository.insertMembersToNewRepository(oldRepository);
        blobTempMap = Folder.getAllBlobsToMap(oldRepository.getMagitBlobs());
        folderTempMap = Folder.getAllFoldersToMap(oldRepository.getMagitFolders());

        Folder.createListOfComponents(folderTempMap, blobTempMap, oldRepository.getMagitFolders());//create list of component to each folder
        setSHA1ToFolders();


        commitTempMap = Commit.getAllCommitsToMap(oldRepository.getMagitCommits(), folderTempMap);
        updateAllSHA1(oldRepository.getMagitCommits());//update prev and prevprev and cur SHA1
        GITRepository.addCommitsToRepositoryMAp(commitTempMap);//add all commit to comitmap in repository

        GITRepository.setBranches(Branch.getAllBranchesToMap(oldRepository.getMagitBranches(), commitTempMap));    //add all branched to branches list in repository

        if (GITRepository.getBranchByName(oldRepository.getMagitBranches().getHead()) != null) {
            GITRepository.setHeadBranch(GITRepository.getBranchByName(oldRepository.getMagitBranches().getHead()));//set head
//            createMagitFiles();

        } else {
            //return head branch does not exist
            throw new Exception("Head branch does not exist");
        }
    }



    public void updateAllSHA1(MagitCommits oldList)//update prev and prevprev
    {
        List<MagitSingleCommit> oldlist = oldList.getMagitSingleCommit();
        for (MagitSingleCommit c : oldlist) {
            updateCurrentSHA(c, oldList);
        }
    }

    public void updateCurrentSHA(MagitSingleCommit c, MagitCommits oldList) {
        if(commitTempMap.get(c.getId()).getSHA() ==null) {
            if (c.getPrecedingCommits() == null) {
                Commit com = commitTempMap.get(c.getId());
                com.setCommitFileContentToSHA();
                return;
            } else if (c.getPrecedingCommits().getPrecedingCommit().isEmpty()) {
                Commit com = commitTempMap.get(c.getId());
                com.setCommitFileContentToSHA();
                return;
            } else if ((commitTempMap.get(c.getPrecedingCommits().getPrecedingCommit().get(0).getId()).getSHA() != null)) {
                commitTempMap.get((c.getId())).setSHA1PreveiousCommit(commitTempMap.get(c.getPrecedingCommits().getPrecedingCommit().get(0).getId()).getSHA());
                commitTempMap.get(c.getId()).setCommitFileContentToSHA();
                return;
            }

            MagitSingleCommit commit = getMagitCommit(c.getPrecedingCommits().getPrecedingCommit().get(0).getId(), oldList);
            updateCurrentSHA(commit, oldList);
            Commit com = commitTempMap.get(c.getId());
            com.setSHA1PreveiousCommit(commitTempMap.get(c.getPrecedingCommits().getPrecedingCommit().get(0).getId()).getSHA());
            com.setCommitFileContentToSHA();
        }
    }

    public MagitSingleCommit getMagitCommit(String ID, MagitCommits oldList) {
        List<MagitSingleCommit> oldlist = oldList.getMagitSingleCommit();
        for (MagitSingleCommit c : oldlist) {
            if (c.getId().equals(ID)) {
                return c;
            }
        }
        return null;
    }

    public void setSHA1ToFolders() {
        Iterator entries = folderTempMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            Folder.Component c = (Folder.Component) thisEntry.getValue();
            Folder f = (Folder) c.getDirectObject();
            c.setSha1(createShaToFolderRec(f));
//            for(Folder.Component com:f.getComponents())
//            {
//                if(com.getComponentType().equals(FolderType.Folder))
//                {
//                    if (com.getComponentSHA1() == null) {
//                        setSHA1ToFolders();
//                    }
//                }
//            }
//            Collections.sort(f.getComponents());
//            c.setSha1(generateSHA1FromString(f.getFolderContentString()));
        }

    }
    public  String createShaToFolderRec(Folder f)
    {
        for(Folder.Component com:f.getComponents())
        {
            if(com.getComponentType().equals(FolderType.Folder))
            {
                if (com.getComponentSHA1() == null) {
                    com.setSha1(createShaToFolderRec((Folder)com.getDirectObject()));
                }
            }
        }
        Collections.sort(f.getComponents());
        return generateSHA1FromString(f.getFolderContentString());
    }



    public void createMagitFiles() throws Exception { //V
        new File(GITRepository.getRepositoryPath() + "\\.magit\\objects").mkdirs();
        new File(GITRepository.getRepositoryPath() + "\\.magit\\branches").mkdirs();

        try{createBranchesFiles();}
        catch(Exception e) {throw new Exception("Could not creat branches folder");}
        try{createObjectFiles();}
        catch(Exception e) {throw new Exception("Could not creat objects folder");}
    }

    public void createBranchesFiles() throws Exception {//V
        Path BranchesPath = Paths.get(GITRepository.getRepositoryPath().toString() + "\\.magit\\Branches");
        for (Branch b : GITRepository.getBranches()) {
            createFileInMagit(b, GITRepository.getRepositoryPath());
        }
        createFile("Head", GITRepository.getHeadBranch().getBranchName(), BranchesPath,new Date().getTime());
    }

    public void createObjectFiles() throws Exception{//V
        createBlobs();
        createFolders();
        createCommits();
    }

    public void createBlobs() throws Exception {//V

        Iterator entries = blobTempMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            Folder.Component c = (Folder.Component) thisEntry.getValue();
            Blob b = (Blob) c.getDirectObject();
            createFileInMagit(b,GITRepository.getRepositoryPath());
        }

    }

    public void createFolders() throws Exception {//V

        Iterator entries = folderTempMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            Folder.Component c = (Folder.Component) thisEntry.getValue();
            Folder f = (Folder) c.getDirectObject();
            createFileInMagit(f, GITRepository.getRepositoryPath());
        }
    }

    public void createCommits() throws Exception {//V

        Iterator entries = commitTempMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            Commit c = (Commit) thisEntry.getValue();
            createFileInMagit(c,GITRepository.getRepositoryPath());
        }
    }
    public void updateFile(String newSha1) throws Exception//אמור להחליף את השא1 בתוך הקובץ
    {
        String fileName= getGITRepository().getRepositoryPath()+"\\.magit\\branches\\"+getGITRepository().getHeadBranch().getBranchName();//c:\repo1\\.magit\\branches\\mainBranchName
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(newSha1);

        writer.close();
    }
//    public void checkValidation(MagitRepository oldRepository)
//    {
//        isFileXML();//1
//        isTwoIdentifiedID(oldRepository);//2
//        isBlobOrFolderRelateToFolder();//3+4+5
//        isRootFolder();//6
//        isBranchCommitExist();//7
//        isHeadBranchExist();//8
//
//
//    }
//    public void isFileXML()//1
//    {
//
//    }
//    public void isTwoIdentifiedID(MagitRepository repo)//2
//    {
//        Iterator entries = commitMap.entrySet().iterator();
//        while (entries.hasNext()) {
//            Map.Entry thisEntry = (Map.Entry) entries.next();
//            Commit c = (Commit) thisEntry.getValue();
//        }
//    }
//    public void  isBlobOrFolderRelateToFolder()//3+4+5
//    {
//
//    }
//    public void isRootFolder()//6
//    {
//
//    }
//    public void  isBranchCommitExist()//7
//    {
//
//    }
//    public void isHeadBranchExist()//8
//    {
//
//    }
}

//לתת אפשרות לעשות סוויץ רפוזטורי מתוך כלום
//אם עושים סוויצ רפוזטורי פעולה 11 לא עובדת, יכול להיות בגלל 2 סיבות: או שאין קישור בין קומיט לאבא שלו באובייקט עצמו, או שבסווית רפוזטורי לא מעדכנות את ההד להצביע על הקומיט הנחוץעל הקומיט הנחוץ