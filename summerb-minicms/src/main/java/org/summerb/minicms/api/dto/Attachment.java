/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.minicms.api.dto;

import java.io.InputStream;

import org.summerb.easycrud.api.dto.HasAutoincrementId;
import org.summerb.utils.DtoBase;
import org.summerb.utils.objectcopy.Clonnable;

public class Attachment implements DtoBase, HasAutoincrementId, Clonnable<Attachment> {
	private static final long serialVersionUID = 6611992873465286245L;

	public static final String FN_NAME = "name";
	public static final int FN_NAME_MAXSIZE = 200;
	public static final String FN_ARTICLE_ID = "articleId";
	public static final String FN_CONTENTS = "contents";
	public static final String FN_SIZE = "size";

	private Long id;
	private long articleId;
	private String name;
	private long size;
	private InputStream contents;

	@Override
	public Attachment clone() {
		Attachment ret = new Attachment();
		ret.setId(id);
		ret.setArticleId(articleId);
		ret.setName(name);
		ret.setSize(size);
		ret.setContents(contents);
		return ret;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public InputStream getContents() {
		return contents;
	}

	public void setContents(InputStream content) {
		this.contents = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
