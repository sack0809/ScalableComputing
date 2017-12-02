

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 * Snippet which shows how to use RevWalk and TreeWalk to read the contents
 * of a specific file from a specific commit.
 *
 * @author dominik.stadler at gmx.at
 */
public class CloneRemoteRepository {
  
	private static final String REMOTE_URL = "https://github.com/sack0809/ScalableComputing.git";
	
	
    public static void main(String[] args) throws IOException , GitAPIException{
    	
    	 try (Git result = Git.cloneRepository()
                 .setURI(REMOTE_URL)
                 .setDirectory(new File("/Users/playsafe/Desktop/Java/Assignment2/src/repo"))
                  .setCloneAllBranches(true)
                  
                 .call()) {
 	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
 	        System.out.println("Having repository: " + result.getRepository().getDirectory());
 	        
         }
    	
    	
    	
    	FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder
                .setGitDir(new File("/Users/playsafe/Desktop/Java/Assignment2/src/repo/.git")).readEnvironment()
                .findGitDir().build();

        listRepositoryContents(repository);

        repository.close();
    	
    	}

	@SuppressWarnings("deprecation")
	private static void listRepositoryContents(Repository repository) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException, GitAPIException {
		// TODO Auto-generated method stub
		@SuppressWarnings("deprecation")
		Git git = new Git(repository);
		Ref head = repository.getRef("HEAD");

        // a RevWalk allows to walk over commits based on some filtering that is defined
        RevWalk walk = new RevWalk(repository);

        
        List<Ref> branches = git.branchList().call();
        for (Ref branch : branches) {
            String branchName = branch.getName();

            System.out.println("Commits of branch: " + branch.getName());
            System.out.println("-------------------------------------");

            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit commit : commits) {
                boolean foundInThisBranch = false;

                RevCommit targetCommit = walk.parseCommit(repository.resolve(
                        commit.getName()));
                
                for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
                    if (e.getKey().startsWith(Constants.R_HEADS)) {
                        if (walk.isMergedInto(targetCommit, walk.parseCommit(
                                e.getValue().getObjectId()))) {
                            String foundInBranch = e.getValue().getName();
                            if (branchName.equals(foundInBranch)) {
                                foundInThisBranch = true;
                                RevTree tree = targetCommit.getTree();
                                TreeWalk treeWalk = new TreeWalk(repository);
                                treeWalk.addTree(tree);
                                treeWalk.setRecursive(false);
                                while (treeWalk.next()) {
                                    System.out.println("found: " + treeWalk.getPathString());
                                }
                                break;
                            }
                        }
                    }
                }

                if (foundInThisBranch) {
                    System.out.println(commit.getName());
                    //System.out.println(commit.getAuthorIdent().getName());
                    //System.out.println(new Date(commit.getCommitTime()));
                    //System.out.println(commit.getFullMessage());
                }
            }
       

        
        
    
        
       
    }
	}
    
}
        