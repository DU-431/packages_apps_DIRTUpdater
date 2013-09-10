package com.du.updater;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Message;
import android.util.Log;
 
@SuppressWarnings("unused")
public class Handlers extends DefaultHandler{
 
        // ===========================================================
        // Fields
        // ===========================================================
       
        
		private boolean in_outertag = false;
        private boolean in_device = false;
        private boolean in_build = false;
        private boolean in_link = false;
        private String buildNumber;
        private String fileLocation;
        private String deviceName;
        private String hwDevice;
       
        private ParsedDataSet myParsedDataSet = new ParsedDataSet();
 
        // ===========================================================
        // Getter & Setter
        // ===========================================================
 
        public ParsedDataSet getParsedData() {
                return this.myParsedDataSet;
        }
        
        public void setHWDevice(String hwDevice) {
        	this.hwDevice = hwDevice;
        }
 
        // ===========================================================
        // Methods
        // ===========================================================
        @Override
        public void startDocument() throws SAXException {
                this.myParsedDataSet = new ParsedDataSet();
        }
 
        @Override
        public void endDocument() throws SAXException {
                // Nothing to do
        }
 
        /** Gets be called on opening tags like:
         * <tag>
         * Can provide attribute(s), when xml was like:
         * <tag attribute="attributeValue">*/
        @Override
        public void startElement(String namespaceURI, String localName,
                        String qName, Attributes atts) throws SAXException {
                if (localName.equals("DU")) {
                        this.in_outertag = true;
                }else if (localName.equals("Device")) {
                		
                		deviceName = atts.getValue("id");
                		//if (hwDevice.equals(deviceName)) {
                			this.in_device = true;
                			this.in_build = true;
                            buildNumber = atts.getValue("buildnumber");
                            myParsedDataSet.setBuildNumber(buildNumber);
                            this.in_link = true;
                            fileLocation = atts.getValue("link");
                            myParsedDataSet.setLink(fileLocation);
                		//}
                		
                }
        }
       
        /** Gets be called on closing tags like:
         * </tag> */
        @Override
        public void endElement(String namespaceURI, String localName, String qName)
                        throws SAXException {
                if (localName.equals("DU")) {
                        this.in_outertag = false;
                }else if (localName.equals("Device")) {
                        this.in_device = false;
                }else if (localName.equals("tagwithnumber")) {
                        // Nothing to do here
                }
        }
       
        /** Gets be called on the following structure:
         * <tag>characters</tag> */
        public void characters(String ch, int start, int length) {
        if(this.in_build){
        	myParsedDataSet.setBuildNumber(new String(ch));
        }
        if(this.in_link){
        	myParsedDataSet.setLink(new String(ch));
        }
    }
}
