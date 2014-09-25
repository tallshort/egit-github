/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.service;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_CONTENTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_README;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryFile;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.RequestException;

import com.google.gson.reflect.TypeToken;

/**
 * Service for accessing repository contents
 *
 * @see <a href="http://developer.github.com/v3/repos/contents">GitHub contents
 *      API documentation</a>
 */
public class ContentsService extends GitHubService {

	/**
	 * Create contents service
	 */
	public ContentsService() {
		super();
	}

	/**
	 * Create contents service
	 *
	 * @param client
	 */
	public ContentsService(final GitHubClient client) {
		super(client);
	}

	/**
	 * Get repository README
	 *
	 * @param repository
	 * @return README
	 * @throws Exception
	 */
	public RepositoryContents getReadme(IRepositoryIdProvider repository)
			throws Exception {
		return getReadme(repository, null);
	}

	/**
	 * Get repository README
	 *
	 * @param repository
	 * @param ref
	 * @return README
	 * @throws IOException
	 */
	public RepositoryContents getReadme(IRepositoryIdProvider repository,
			String ref) throws IOException {
		String id = getId(repository);

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_README);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		if (ref != null && ref.length() > 0)
			request.setParams(Collections.singletonMap("ref", ref));
		request.setType(RepositoryContents.class);
		return (RepositoryContents) client.get(request).getBody();
	}

	/**
	 * Get contents at the root of the given repository on master branch
	 *
	 * @param repository
	 * @return list of contents at root
	 * @throws IOException
	 */
	public List<RepositoryContents> getContents(IRepositoryIdProvider repository)
			throws IOException {
		return getContents(repository, null);
	}

	/**
	 * Get contents at path in the given repository on master branch
	 *
	 * @param repository
	 * @param path
	 * @return list of contents at path
	 * @throws IOException
	 */
	public List<RepositoryContents> getContents(
			IRepositoryIdProvider repository, String path) throws IOException {
		return getContents(repository, path, null);
	}

	/**
	 * Get contents of path at reference in given repository
	 * <p>
	 * For file paths this will return a list with one entry corresponding to
	 * the file contents at the given path
	 *
	 * @param repository
	 * @param path
	 * @param ref
	 * @return list of contents at path
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public List<RepositoryContents> getContents(
			IRepositoryIdProvider repository, String path, String ref)
			throws IOException {
		String id = getId(repository);

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_CONTENTS);
		if (path != null && path.length() > 0) {
			if (path.charAt(0) != '/')
				uri.append('/');
			uri.append(path);
		}
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(RepositoryContents.class);
		request.setArrayType(new TypeToken<List<RepositoryContents>>() {
		}.getType());
		if (ref != null && ref.length() > 0)
			request.setParams(Collections.singletonMap("ref", ref));

		Object body = client.get(request).getBody();
		if (body instanceof RepositoryContents)
			return Collections.singletonList((RepositoryContents) body);
		else
			return (List<RepositoryContents>) body;
	}

	/**
	 * Check if contents at path exist in the given repository
	 *
	 * @param repository
	 * @param path
	 * @return true if contents at path exist
	 * @throws IOException
	 */
	public boolean exists(IRepositoryIdProvider repository, String path) throws IOException {
		return exists(repository, path, null);
	}

	/**
	 * Check if contents at path exist in the given repository
	 *
	 * @param repository
	 * @param path
	 * @param ref
	 * @return true if contents at path exist
	 * @throws IOException
	 */
	public boolean exists(IRepositoryIdProvider repository, String path, String ref) throws IOException {
		try {
			return getContents(repository, path, ref).size() != 0;
		} catch (RequestException e) {
			if (e.getMessage().equals("Not Found (404)")) {
				return false;
			} else {
				throw e;
			}
		}
	}

	/**
	 * Create file in given repository on master branch
	 *
	 * @param repository
	 * @param file
	 * @return file
	 * @throws IOException
	 */
	public RepositoryFile createFile(IRepositoryIdProvider repository, RepositoryContents file) throws IOException {
		return saveFile(repository, file);
	}

	/**
	 * Create file in given repository on given branch
	 *
	 * @param repository
	 * @param file
	 * @return file
	 * @throws IOException
	 */
	public RepositoryFile createFile(IRepositoryIdProvider repository, RepositoryContents file, String branch) throws IOException {
		return saveFile(repository, file, branch);
	}

	/**
	 * Update file in given repository on master branch
	 *
	 * @param repository
	 * @param file
	 * @return file
	 * @throws IOException
	 */
	public RepositoryFile updateFile(IRepositoryIdProvider repository, RepositoryContents file) throws IOException {
		return saveFile(repository, file);
	}

	/**
	 * Create file in given repository on given branch
	 *
	 * @param repository
	 * @param file
	 * @return file
	 * @throws IOException
	 */
	public RepositoryFile updateFile(IRepositoryIdProvider repository, RepositoryContents file, String branch) throws IOException {
		return saveFile(repository, file, branch);
	}

	/**
	 * Delete file in given repository on master branch
	 *
	 * @param repository
	 * @param file
	 * @throws IOException
	 */
	public void deleteFile(IRepositoryIdProvider repository, RepositoryContents file) throws IOException {
		deleteFile(repository, file, null);
	}

	/**
	 * Delete file in given repository on given branch
	 *
	 * @param repository
	 * @param file
	 * @throws IOException
	 */
	public void deleteFile(IRepositoryIdProvider repository, RepositoryContents file, String branch) throws IOException {
		String id = getId(repository);
		String uri = createFileURI(file, id);
		Map<String, String> paramMap = buildFileParamMap(file, branch);
		try {
			client.delete(uri, paramMap);
		} catch (RequestException e) {
			if (e.getStatus() != HttpURLConnection.HTTP_OK) { // ignore Status: 200 OK
				throw e;
			}
		}
	}

	private RepositoryFile saveFile(IRepositoryIdProvider repository, RepositoryContents file) throws IOException {
		return updateFile(repository, file, null);
	}

	private RepositoryFile saveFile(IRepositoryIdProvider repository, RepositoryContents file, String branch) throws IOException {
		String id = getId(repository);
		String uri = createFileURI(file, id);
		Map<String, String> paramMap = buildFileParamMap(file, branch);
		return client.put(uri, paramMap, RepositoryFile.class);
	}

	private String createFileURI(RepositoryContents file, String id) {
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(id);
		uri.append(SEGMENT_CONTENTS);
		uri.append('/').append(file.getPath());
		return uri.toString();
	}

	private Map<String, String> buildFileParamMap(RepositoryContents file, String branch) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (file.getSha() != null) {
			paramMap.put("sha", file.getSha());
			if (file.getContent() != null) {
				paramMap.put("content", file.getContent());
				paramMap.put("message", "update file " +  file.getName());
			} else {
				paramMap.put("message", "delete file " +  file.getName());
			}
		} else {
			paramMap.put("content", file.getContent());
			paramMap.put("message", "create file " +  file.getName());
		}
		if (branch != null) {
			paramMap.put("branch", branch);
		}
		return paramMap;
	}
}
