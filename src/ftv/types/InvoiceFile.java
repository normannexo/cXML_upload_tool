package ftv.types;

import java.io.File;

public class InvoiceFile {
	private File file;
	private File responseFile;
	private String status;
	private String response;
	private boolean sent;
	private boolean selected;
	
	public InvoiceFile(File file) {
		this.file = file;
	
		this.sent = false;
		this.selected = false;
		this.status = "";
		this.response = "";
		
	}
	
	public File getFile() {
		return file;
	}
	
	public String getStatus() {
		return status;
	}
	
	public String getResponse() {
		return this.response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public boolean isSent() {
		return this.sent;
	}
	
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	
	public void setStatus(String newstatus) {
		this.status = newstatus;
	}
	
	public String getName() {
		return file.getName();
	}
	
	public void setResponseFile(File rfile) {
		this.responseFile = rfile;
	}
	
	public File getResponseFile() {
		return this.responseFile;
	}
	
	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean value) {
		this.selected = (boolean) value;
		
	}
	

}
