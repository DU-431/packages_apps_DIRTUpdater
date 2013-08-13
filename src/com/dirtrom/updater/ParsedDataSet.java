package com.dirtrom.updater;

public class ParsedDataSet {
	
	private String buildNumber;
	private String link;
    private String extractedString = null;
    private int extractedInt = 0;

    public String getExtractedString() {
            return extractedString;
    }
    public void setExtractedString(String extractedString) {
            this.extractedString = extractedString;
    }

    public int getExtractedInt() {
            return extractedInt;
    }
    public void setExtractedInt(int extractedInt) {
            this.extractedInt = extractedInt;
    }
   
    public String toString(){
            return "ExtractedString = " + this.extractedString
                            + "nExtractedInt = " + this.extractedInt;
    }
    
    public void setBuildNumber (String BuildNumber) {
    	this.buildNumber = BuildNumber;
    }
    
    public void setLink (String link) {
    	this.link = link;
    }
    
    public String getBuildNumber() {
    	return this.buildNumber;
    }
    
    public String getLink() {
    	return this.link;
    }
}