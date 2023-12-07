package com.s8.core.arch.magnesium.databases.repository.store;

import com.s8.core.io.json.JSON_Field;
import com.s8.core.io.json.JSON_Type;



@JSON_Type(name = "RepositoryStore")
public class RepoMgStoreMetadata {


	@JSON_Field(name = "rootPathname") 
	public String rootPathname;


}
