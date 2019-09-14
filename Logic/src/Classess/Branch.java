package Classess;


import generated.MagitBranches;
import generated.MagitSingleBranch;

import java.util.*;

public class Branch {

        //members
        private String branchName;
        private Commit pointedCommit;
        private  String pointedCommitSHA1="";
        private Boolean isRemoteTrackingBranch;
        private Boolean isRemoteBranch;

        //con
        Branch(String name)
        {
            branchName = name;
        }
        Branch(String name, String commitSHA1,boolean isRemote,boolean isTracking)
        {
            branchName = name;
            pointedCommitSHA1 = commitSHA1;
            isRemoteBranch = isRemote;
            isRemoteTrackingBranch = isTracking;
        }


        //set\get

        public String getPointedCommitSHA1() { return pointedCommitSHA1; }
        void setPointedCommitSHA1(String pointedCommit) { pointedCommitSHA1 = pointedCommit; }

        public String getBranchName() { return branchName; }

        public Commit getPointedCommit() { return pointedCommit; }
        public void setPointedCommit(Commit newC) { pointedCommit=newC; }


        //methods
        public static HashSet<Branch> getAllBranchesToMap(MagitBranches branches, Map<String, Commit> commits) throws Exception
        {
            HashSet<Branch> newbranches = new HashSet<>();
            List<MagitSingleBranch> brancheslist = branches.getMagitSingleBranch();
            for(MagitSingleBranch c: brancheslist)
            {
                Branch b = new Branch(c.getName());
                if(commits.containsKey(c.getPointedCommit().getId())) {
                    b.setPointedCommit(commits.get(c.getPointedCommit().getId()));
                    newbranches.add(b);
                    b.setPointedCommitSHA1(commits.get(c.getPointedCommit().getId()).getSHA());
                }
                else
                {
                    //return commit does not exist in xml
                    throw new Exception("Commit does not exist in the xml");

                }
            }
            return newbranches;
        }
    }
