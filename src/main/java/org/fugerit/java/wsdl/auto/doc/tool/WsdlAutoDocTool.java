package org.fugerit.java.wsdl.auto.doc.tool;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;

import org.fugerit.java.core.cli.ArgUtils;
import org.fugerit.java.wsdl.auto.doc.model.ElementModel;
import org.fugerit.java.wsdl.auto.doc.model.WSDLConfig;
import org.fugerit.java.wsdl.auto.doc.model.WSDLModel;
import org.fugerit.java.wsdl.auto.doc.model.WSDLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

public class WsdlAutoDocTool {

	private static Logger logger = LoggerFactory.getLogger( WsdlAutoDocTool.class );
	
	public static final String ARG_WSDL_INPUT = "wsdl-input";
	
	public static final String ARG_PRINT_FROM = "print-from";
	
	public static final String ARG_OUTPUT_HTML = "output-html";
	
	public static void main( String[] args ) {
		try {

			Properties params = ArgUtils.getArgs( args );
			String wsdlInput = params.getProperty( ARG_WSDL_INPUT );
			logger.info( "param "+ARG_WSDL_INPUT+" -> "+wsdlInput );
			WSDLParser parser = new WSDLParser();
			Definitions defs = parser.parse( wsdlInput );			
			WSDLModel wsdlModel = WSDLConfig.configure( defs );
			String printFrom = params.getProperty( ARG_PRINT_FROM );
			if ( printFrom != null ) {
				ElementModel elementModel = wsdlModel.getElementMap().get( printFrom );	
				if ( elementModel != null ) {
					WSDLUtils.printElementTree( elementModel, printFrom, true );		
				} else {
					logger.info( "Path not found : "+printFrom );
				}
			}
			String outputHtml = params.getProperty( ARG_OUTPUT_HTML );
			if ( outputHtml != null ) {
				throw new Exception( ARG_OUTPUT_HTML+" not yet implemented" );
			}

		} catch ( Exception e ) {
			logger.error( "Error "+e, e );
		}
	}

	private static String prepareMinOccurs( String val ) {
		String res = val;
		if ( val == null ) {
			val = "1";
		}
		return res;
	}
	
	private static String prepareMaxOccurs( String val ) {
		String res = prepareMinOccurs(val);
		if ( "unbounded".equalsIgnoreCase( val ) ) {
			res = null;
		} else {
			
		}
		return res;
	}
	
	public static void createScript( PrintWriter writer, Properties gruppi, ElementModel base, ElementModel model , String rootPath ) throws Exception {
		String printElement = model.getFullPath();
		if ( rootPath != null && printElement.startsWith( rootPath ) ) {
			printElement = printElement.substring( rootPath.length() );
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append( " INSERT INTO DEC_ELEMENTO_DATI " );
		buffer.append( " ( ID, DESCRIZIONE, ORDINAMENTO, DATAINIZIOVALIDITA, DATAFINEVALIDITA, NOMELEMENTO, NOTE, MINOCCURS, MAXOCCURS, IDGRUPPO, STATO ) " );
		buffer.append( " VALUES( " );
		buffer.append( " SEQ_DEC_ELEMENTO_DATI.NEXTVAL, '" );
		buffer.append( printElement );
		buffer.append( "', 0, TO_DATE( '2019-06-01', 'YYYY-MM-DD' ), TO_DATE( '9999-12-31', 'YYYY-MM-DD' ), '" );
		buffer.append( model.getPathName() );
		String[] note = printElement.substring( 1 ).split( "/" );
		StringBuffer noteBuffer = new StringBuffer();
		for ( int k=note.length-1; k>=0; k-- ) {
			noteBuffer.append( note[k] );
			noteBuffer.append( ", " );
		}
		buffer.append( "', '"+noteBuffer+"', " );
		buffer.append( prepareMinOccurs( model.getElement().getMinOccurs() ) );
		buffer.append( " , " );
		buffer.append( prepareMaxOccurs( model.getElement().getMaxOccurs() ) );
		buffer.append( " , " );
		String baseName = base.getPathName();
		if ( "altraLingua".equalsIgnoreCase( baseName ) ) {
			baseName = "residenza";
		}
		String idGruppo = gruppi.getProperty( baseName );
		if ( idGruppo == null ) {
			logger.info( "Escludi gruppo : "+baseName );
		} else {
			buffer.append( idGruppo );
			buffer.append( " , 0 " );
			buffer.append( " );" );
			writer.println( buffer.toString() );			
			Iterator<ElementModel> it = model.getChildren().iterator();
			while ( it.hasNext() ) {
				createScript( writer, gruppi, base, it.next(), rootPath );
			}
		}
		
	}
	
}
