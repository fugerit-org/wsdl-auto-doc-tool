package org.fugerit.java.wsdl.auto.doc.model;

import org.fugerit.java.core.util.collection.KeyObject;

import com.predic8.schema.ComplexType;
import com.predic8.schema.SimpleType;

public class TypeModel implements KeyObject<String>  {

	private ComplexType complex = null;
	
	private SimpleType simple = null;
	
	private String ns;
	
	private String name;
	
	private DocumentationModel documentation;

	public TypeModel(ComplexType type) {
		super();
		this.complex = type;
		this.ns = type.getNamespaceUri();
		this.name = type.getName();
		this.documentation = new DocumentationModel( type.getAnnotation() );
	}

	public TypeModel(SimpleType type) {
		super();
		this.simple = type;
		this.ns = type.getNamespaceUri();
		this.name = type.getName();
		this.documentation = new DocumentationModel( type.getAnnotation() );
	}

	public ComplexType getComplex() {
		return complex;
	}

	public SimpleType getSimple() {
		return simple;
	}

	public boolean isComplex() {
		return this.getComplex() != null;
	}
	
	public boolean isSimple() {
		return this.getSimple() != null;
	}

	public String getNs() {
		return ns;
	}

	public String getName() {
		return name;
	}

	public DocumentationModel getDocumentation() {
		return documentation;
	}

	@Override
	public String getKey() {
		return WSDLUtils.createKey( this.getNs() , this.getName() );
	}
	
	
}
