package org.fugerit.java.wsdl.auto.doc.model;

import java.util.List;

import com.predic8.schema.Annotation;
import com.predic8.schema.Documentation;

public class DocumentationModel {

	private Annotation annotation;

	protected DocumentationModel(Annotation annotation) {
		super();
		this.annotation = annotation;
	}

	public Annotation getAnnotation() {
		return annotation;
	}
	
	public String getFullDocumentationText() {
		StringBuffer buffer = new StringBuffer();
		if ( this.getAnnotation() != null ) {
			List<Documentation> docs = this.getAnnotation().getDocumentations();
			for ( int k=0; k<docs.size(); k++ ) {
				Documentation current = docs.get( k );
				buffer.append( current.getContent().trim() );
				buffer.append( " " );
			}
		}
		return buffer.toString().trim();
	}
	
	
}
