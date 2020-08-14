package com.codingos.shirojwt.service;

import java.util.Set;

public interface ShiroService {

	Set<String> listPermissions(String username);
	
	Set<String> listRoles(String username);

}
