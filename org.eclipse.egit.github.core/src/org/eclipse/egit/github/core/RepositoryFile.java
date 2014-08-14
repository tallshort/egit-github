package org.eclipse.egit.github.core;

import java.io.Serializable;

/**
 * File in a repository
 */
public class RepositoryFile implements Serializable {

	private static final long serialVersionUID = 2730895557747613239L;

	private RepositoryContents content;
	private Commit commit;

	public RepositoryContents getContent() {
		return content;
	}

	public void setContent(RepositoryContents content) {
		this.content = content;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}
}
