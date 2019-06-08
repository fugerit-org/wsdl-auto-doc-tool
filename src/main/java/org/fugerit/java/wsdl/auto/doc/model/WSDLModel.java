package org.fugerit.java.wsdl.auto.doc.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fugerit.java.core.util.collection.ListMapStringKey;

import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.wsdl.Definitions;
import com.predic8.xml.util.PrefixedName;

public class WSDLModel {

	private Definitions wsdlDefs;
	
	private ListMapStringKey<TypeModel> types;
	
	private ListMapStringKey<ElementModel> elements;
	
	private ListMapStringKey<TypeModel> usedTypes;
	
	private ListMapStringKey<OperationModel> operations;
	
	private Map<String, ElementModel> elementMap;
	
	public WSDLModel( Definitions wsdlDefs ) {
		this.wsdlDefs = wsdlDefs;
		this.types = new ListMapStringKey<TypeModel>();
		this.usedTypes = new ListMapStringKey<TypeModel>();
		this.operations = new ListMapStringKey<OperationModel>();
		this.elements = new ListMapStringKey<ElementModel>();
		this.elementMap = new HashMap<String, ElementModel>();
	}

	public Definitions getWsdlDefs() {
		return wsdlDefs;
	}

	public ListMapStringKey<OperationModel> getOperations() {
		return operations;
	}

	public ListMapStringKey<TypeModel> getTypes() {
		return types;
	}
	
	public void addToUsedTypes( TypeModel type ) {
		if ( !this.getUsedTypes().getMap().containsKey( type.getKey() ) ) {
			this.getUsedTypes().add( type );
		}
	}
	
	public ListMapStringKey<TypeModel> getUsedTypes() {
		return usedTypes;
	}

	public ListMapStringKey<ElementModel> getElements() {
		return elements;
	}
	
	public Map<String, ElementModel> getElementMap() {
		return elementMap;
	}
	
	public Element lookup( PrefixedName pn ) {
		Element res = null;
		List<Schema> schemaList = this.getWsdlDefs().getSchemas();
		Iterator<Schema> schemaIt = schemaList.iterator();
		while ( schemaIt.hasNext() && res == null ) {
			try {
				Schema current = schemaIt.next();
				res = current.getElement( pn.getLocalName() );
			} catch (Exception e) {
			}
		}
		return res;
	}
	
}
