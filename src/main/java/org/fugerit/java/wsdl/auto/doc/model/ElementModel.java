package org.fugerit.java.wsdl.auto.doc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fugerit.java.core.util.collection.KeyObject;

import com.predic8.schema.Element;

public class ElementModel implements KeyObject<String> {

	private Element element;
	
	private TypeModel type;
	
	private ElementModel parent;
	
	private List<ElementModel> children;
	
	private DocumentationModel documentation;
	
	public ElementModel( Element element, TypeModel type, ElementModel parent )  {
		this.children = new ArrayList<ElementModel>();
		this.element = element;
		this.type = type;
		this.parent = parent;
		this.documentation = new DocumentationModel( element.getAnnotation() );
	}

	public Element getElement() {
		return element;
	}

	public TypeModel getType() {
		return type;
	}

	public ElementModel getParent() {
		return parent;
	}

	public List<ElementModel> getChildren() {
		return children;
	}
	
	public DocumentationModel getDocumentation() {
		return documentation;
	}

	public String getPathName() {
		String pathName = this.getElement().getName();
		if ( pathName == null ) {
			pathName = this.getElement().getRefValue();
		}
		return pathName;
	}
	
	public String getFullPath() {
		StringBuffer buffer = new StringBuffer();
		List<ElementModel> path = this.getElementPath();
		buffer.append( path.get( 0 ).getPathName() );
		for ( int k=1; k<path.size(); k++ ) {
			buffer.append( "/" );	
			buffer.append( path.get( k ).getPathName() );	
		}
 		return buffer.toString();
	}
	
	public List<ElementModel> getElementPath() {
		List<ElementModel> elementPath = new ArrayList<ElementModel>();
		ElementModel current = this;
		while ( current != null ) {
			elementPath.add( current );
			current = current.getParent();
		}
		Collections.reverse( elementPath );
		return elementPath;
	}
	
	@Override
	public String getKey() {
		return WSDLUtils.createKey( this.getElement().getNamespaceUri() , this.getPathName() );
	}

}
