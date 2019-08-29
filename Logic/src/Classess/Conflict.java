package Classess;

import java.awt.image.ByteLookupTable;

public class Conflict {

    String conflictName;
//    Folder folderOfFile;
    Blob our;
    Blob their;
    String pathInFolder;

    public  Conflict(String conflict, Folder.Component ourIn, Folder.Component theirIn)
    {
conflictName = conflict;
our = (Blob)ourIn.getDirectObject();
their = (Blob)theirIn.getDirectObject();
//pathInFolder = pathInFolderIn;
    }

}
