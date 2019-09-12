package Classess;

import java.awt.image.ByteLookupTable;

public class Conflict {

    public String getConflictName() {
        return conflictName;
    }

    String conflictName;
    //    Folder folderOfFile;
    Folder.Component our;
    Folder.Component their;
    String pathInFolder;
    Folder.Component father;

    public Folder.Component getOur() {
        return our;
    }

    public Folder.Component getTheir() {
        return their;
    }

    public Folder.Component getFather() {
        return father;
    }

    public Conflict(String conflict, Folder.Component ourIn, Folder.Component theirIn, Folder.Component fatherIn) {
        conflictName = conflict;
        our =  ourIn;
        their =  theirIn;
        father = fatherIn;
//pathInFolder = pathInFolderIn;
    }
}

