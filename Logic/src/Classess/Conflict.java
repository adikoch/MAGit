package Classess;

import java.awt.image.ByteLookupTable;

public class Conflict {

    public String getConflictName() {
        return conflictName;
    }

    String conflictName;
    //    Folder folderOfFile;
    Blob our;
    Blob their;
    String pathInFolder;
    Blob father;

    public Conflict(String conflict, Folder.Component ourIn, Folder.Component theirIn, Folder.Component fatherIn) {
        conflictName = conflict;
        our = (Blob) ourIn.getDirectObject();
        their = (Blob) theirIn.getDirectObject();
        father = (Blob) fatherIn.getDirectObject();
//pathInFolder = pathInFolderIn;
    }
}

