package org.egov.bpa.transaction.entity;

import java.util.ArrayList;
import java.util.List;

public class FileData {
	
	
	private List<FileDetails> fileDetails;

    public FileData() {
    	fileDetails = new ArrayList<>();
    }

	public List<FileDetails> getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(List<FileDetails> fileDetails) {
		this.fileDetails = fileDetails;
	}	

}
