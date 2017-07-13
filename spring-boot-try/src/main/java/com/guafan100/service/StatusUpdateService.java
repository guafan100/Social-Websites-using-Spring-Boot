package com.guafan100.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.guafan100.model.StatusUpdate;
import com.guafan100.model.StatusUpdateDao;

@Service
public class StatusUpdateService {
	
	private final static int PAGESIZE = 10;
	
	@Autowired
	private StatusUpdateDao dao;
	
	public void save(StatusUpdate statusUpdate) {
		dao.save(statusUpdate);
	}
	
	public StatusUpdate getLatest() {
		return dao.findFirstByOrderByAddedDesc();
	}
	
	public Page<StatusUpdate> getPage(int pageNumber) {
		PageRequest request = new PageRequest(pageNumber-1, PAGESIZE, Sort.Direction.DESC, "added");
		
		return dao.findAll(request);
	}

	public void delete(Long id) {
		dao.delete(id);		
	}

	public StatusUpdate get(Long id) {
		return dao.findOne(id);
	}
}
