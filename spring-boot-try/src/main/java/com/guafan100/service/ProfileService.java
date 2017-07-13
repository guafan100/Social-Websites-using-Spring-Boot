package com.guafan100.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guafan100.model.Profile;
import com.guafan100.model.ProfileDao;
import com.guafan100.model.SiteUser;

@Service
public class ProfileService {

	@Autowired
	private ProfileDao profileDao;

	public void save(Profile profile) {
		profileDao.save(profile);
	}

	public Profile getUserProfile(SiteUser user) {
		return profileDao.findByUser(user);
	}
}
