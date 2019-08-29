package Classess;


import java.util.Map;

public enum MergeType {
    A("001000"), B("010000"), C("011000"), D("011001"),
    E("100000"), F("101000"), G("101010"),
    H("110000"), I("110100"), J("111000"), K("111001"),
    L("111010"), M("111100"), N("111111");

    private String situationString;

    MergeType(String name) {
        situationString = name;
    }

    Folder.Component decideFile(Map<Conflict,Folder> conflictMap, Folder.Component ourF, Folder.Component theirF, Folder f) {
        switch (this) {
            case A:
            case M:
            case G:
            case D:
                return null;
            case B:
            case K:
                return theirF;
            case C:
            case F:
            case H:
            case J:
            {
                Conflict c = new Conflict(ourF.getComponentName(),ourF,theirF);
                conflictMap.put(c,f);
                return  null;
            }
            case E:
            case L:
            case I:
                return ourF;


        }
        return null;
    }
}

