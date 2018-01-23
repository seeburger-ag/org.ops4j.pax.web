/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.extender.war.internal.model;

public class WebAppCookieConfig {

	private String domain;
	private Boolean httpOnly;
	private Integer maxAge;
	private String name;
	private String path;
	private Boolean secure;

	/**
	 * 
	 * @param domain
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * 
	 * @param httpOnly
	 */
	public void setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	/**
	 * 
	 * @param maxAge
	 */
	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public void setSecure(Boolean secure) {
		this.secure = secure;
	}

	/**
	 * 
	 * @return
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getHttpOnly() {
		return httpOnly;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getMaxAge() {
		return maxAge;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getSecure() {
		return secure;
	}

}
