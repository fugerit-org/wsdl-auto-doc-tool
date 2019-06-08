package org.fugerit.java.wsdl.auto.doc.model;

import org.fugerit.java.core.util.collection.KeyObject;

import com.predic8.wsdl.Input;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Output;

public class OperationModel implements KeyObject<String> {

	private Operation operation;

	public OperationModel(Operation operation) {
		super();
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}
	
	public String getFullDocumentationText() {
		String res = "";
		if ( operation.getDocumentation() != null && operation.getDocumentation().getContent() != null ) {
			res = operation.getDocumentation().getContent().trim();
		}
		return res;
	}
	
	public String getName() {
		return this.getOperation().getName();
	}

	@Override
	public String getKey() {
		return this.getName();
	}
	
	public Input getInput() {
		return this.getOperation().getInput();
	}
	
	public Output getOuInput() {
		return this.getOperation().getOutput();
	}
	
}
