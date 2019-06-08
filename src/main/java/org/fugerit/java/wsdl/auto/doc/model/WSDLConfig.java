package org.fugerit.java.wsdl.auto.doc.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.predic8.schema.Choice;
import com.predic8.schema.ComplexContent;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Derivation;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.Sequence;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;

import groovy.xml.QName;

public class WSDLConfig {

	private static Logger logger = LoggerFactory.getLogger( WSDLConfig.class );
	
	public static WSDLModel configure( Definitions wsdlDsf ) {
		WSDLModel wsdlModel = new WSDLModel( wsdlDsf );
		populateBase( wsdlModel );
		populateTree( wsdlModel );
		return wsdlModel;
	}

	private static void populateBase( WSDLModel wsdlModel ) {
		// parse operations
		List<Operation> operations = wsdlModel.getWsdlDefs().getOperations();
		Iterator<Operation> itOperations = operations.iterator();
		while ( itOperations.hasNext() ) {
			Operation op = itOperations.next();
			wsdlModel.getOperations().add( new OperationModel( op ) );
		}
		// parse types
		for (Schema schema : wsdlModel.getWsdlDefs().getSchemas()) {
			for (SimpleType type : schema.getSimpleTypes()) {
				TypeModel typeModel = new TypeModel(type);
				wsdlModel.getTypes().add(typeModel);
			}
			for (ComplexType type : schema.getComplexTypes()) {
				TypeModel typeModel = new TypeModel(type);
				wsdlModel.getTypes().add(typeModel);
			}
			for ( Element e : schema.getAllElements() ) {
				ComplexType type = (ComplexType)e.getEmbeddedType();
				ElementModel elementModel = new ElementModel( e , new TypeModel(type), null );
				wsdlModel.getElements().add( elementModel );
			}
		}
		
	}
	
	private static void populateTree( WSDLModel wsdlModel ) {
		Iterator<OperationModel> opIt = wsdlModel.getOperations().iterator();
		while ( opIt.hasNext() ) {
			OperationModel currentOp = opIt.next();
			Element inputElement = wsdlModel.lookup( currentOp.getInput().getMessagePrefixedName() );
			ComplexType inputType = (ComplexType) inputElement.getEmbeddedType();
			ElementModel inputModel = new ElementModel( inputElement , new TypeModel( inputType ), null );
			logger.info( "**********************************" );
			logger.info( "**** INPUT                      **" );
			logger.info( "**********************************" );
			populateElement(wsdlModel, inputModel, 0 );
			Element outputElement = wsdlModel.lookup( currentOp.getOuInput().getMessagePrefixedName() );
			ComplexType outputType = (ComplexType) outputElement.getEmbeddedType();
			ElementModel outputModel = new ElementModel( outputElement , new TypeModel( outputType ), null );
			logger.info( "**********************************" );
			logger.info( "**** OUTPUT                     **" );
			logger.info( "**********************************" );
			populateElement( wsdlModel, outputModel, 0 );
		}
	}
	
	private static void populateElement( WSDLModel wsdlModel, ElementModel parent, int level ) {
		TypeModel type = parent.getType();
		if ( type != null ) {
			if ( type.isSimple() ) {
				wsdlModel.addToUsedTypes( type  );
			} else {
				ComplexType complexType = type.getComplex();
				SchemaComponent model = complexType.getModel();
				logger.debug( "CT : "+model.getClass().getName()+" -> "+complexType );
				List<SchemaComponent> parts = null;
				if ( model instanceof Choice ) {
					Choice choice = (Choice) model;
					parts = choice.getParticles();
				} else  if ( model instanceof Sequence ) {
					Sequence sequence = (Sequence) model;
					parts = sequence.getParticles();
				} else  if ( model instanceof ComplexContent ) {
					ComplexContent content = (ComplexContent) model;
					Derivation derivation = content.getDerivation();
					Sequence sequence = (Sequence) derivation.getModel();
					parts = sequence.getParticles();
					
				}
				Iterator<SchemaComponent> componenets = parts.iterator();
				while ( componenets.hasNext() ) {
					SchemaComponent comp = componenets.next();
					//logger.debug( comp.getClass().getName() );
					List<Element> elements = null;
					if ( comp instanceof Choice ) {
						Choice choice = (Choice) comp;
						elements = choice.getElements();
					} else if ( comp instanceof Sequence ) {
						Sequence sequence = (Sequence) comp;
						elements = sequence.getElements();
					} else  if ( comp instanceof Element ) {
						elements = new ArrayList<Element>();
						elements.add( (Element) comp );
					}
					for (Element e : elements) {
						String typeKey = null;
						TypeDefinition kidType = e.getEmbeddedType();
						if ( kidType != null ) {
							typeKey = WSDLUtils.createKey( kidType.getNamespaceUri() , kidType.getName() );
						} else if ( e.getType() != null ) {
							QName typeName = e.getType();
							typeKey = WSDLUtils.createKey( typeName.getNamespaceURI(), typeName.getLocalPart() );
						}
						TypeModel kidTypeModel = null;
						if ( typeKey != null ) {
							kidTypeModel = wsdlModel.getTypes().get( typeKey );
						} else  if ( e.getRef() != null ) {
							QName typeName = e.getRef();
							String elementKey = WSDLUtils.createKey( typeName.getNamespaceURI(), typeName.getLocalPart() );
							ElementModel ref = wsdlModel.getElements().get( elementKey );
							kidTypeModel = ref.getType();
						}
						ElementModel kid = new ElementModel( e , kidTypeModel, parent );
						wsdlModel.getElementMap().put( kid.getFullPath() , kid );
						parent.getChildren().add( kid );
						//logger.debug( kid.getFullPath() );
						populateElement( wsdlModel, kid, level+1 );
					}	
				}
				
				
			}
		}
	}
	
}
