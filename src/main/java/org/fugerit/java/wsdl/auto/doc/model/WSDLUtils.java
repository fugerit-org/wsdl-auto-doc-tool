package org.fugerit.java.wsdl.auto.doc.model;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSDLUtils {

	private static Logger logger = LoggerFactory.getLogger( WSDLConfig.class );
	
	public static String createKey( String ns, String name ) {
		String key = name;
		if ( ns != null ) {
			key = "{"+ns+"}"+name;
		}
		return key;
	}
	
	
	public static void printElementTree( ElementModel model , String rootPath, boolean addDoc ) {
		String printElement = model.getFullPath();
		if ( rootPath != null && printElement.startsWith( rootPath ) ) {
			printElement = printElement.substring( rootPath.length() );
		}
		if ( addDoc ) {
			printElement = printElement+" ["+model.getDocumentation().getFullDocumentationText()+"]";
		}
		System.out.println( printElement );
		Iterator<ElementModel> it = model.getChildren().iterator();
		while ( it.hasNext() ) {
			printElementTree( it.next(), rootPath, addDoc );
		}
	}
	
}
