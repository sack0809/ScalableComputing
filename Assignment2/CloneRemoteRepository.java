

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;



public class CloneRemoteRepository {

    private static final String REMOTE_URL = "https://github.com/sack0809/ScalableComputing.git";

    public static void main(String[] args) throws IOException, GitAPIException {
       /* // prepare a new folder for the cloned repository
        File localPath = File.createTempFile("/Users/playsafe/Desktop/Java/Assignment2/","");
     //File localPath = new File("/Users/playsafe/Desktop/Java/");
        if(!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);*/
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(new File("/Users/playsafe/Desktop/Java/Assignment2/src/repo"))
                 .setCloneAllBranches(true)
                 
                .call()) {
	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
	        System.out.println("Having repository: " + result.getRepository().getDirectory());
	        
        }
	        Git.open(new File("//Users/playsafe/Desktop/Java/Assignment2/src/repo/.git"))
	        .checkout();
	        Repository repo = new FileRepository("//Users/playsafe/Desktop/Java/Assignment2/src/repo/.git");
	        Git git = new Git(repo);
	        RevWalk walk = new RevWalk(repo);

	        List<Ref> branches = git.branchList().call();
	        for (Ref branch : branches) {
	            String branchName = branch.getName();

	            System.out.println("Commits of branch: " + branch.getName());
	            System.out.println("-------------------------------------");

	            Iterable<RevCommit> commits = git.log().all().call();

	            for (RevCommit commit : commits) {
	                boolean foundInThisBranch = false;

	                RevCommit targetCommit = walk.parseCommit(repo.resolve(
	                        commit.getName()));
	                for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
	                    if (e.getKey().startsWith(Constants.R_HEADS)) {
	                        if (walk.isMergedInto(targetCommit, walk.parseCommit(
	                                e.getValue().getObjectId()))) {
	                            String foundInBranch = e.getValue().getName();
	                            if (branchName.equals(foundInBranch)) {
	                                foundInThisBranch = true;
	                                break;
	                            }
	                        }
	                    }
	                }

	                if (foundInThisBranch) {
	                    System.out.println(commit.getName());
	                    System.out.println(commit.getAuthorIdent().getName());
	                    System.out.println(new Date(commit.getCommitTime()));
	                    System.out.println(commit.getFullMessage());
	                }
	            }
	       

	        
	        
        }
    }
}
