package com.athaydes.spockframework.report.internal

import com.athaydes.spockframework.report.SpockReportExtension
import groovy.xml.MarkupBuilder

/**
 *
 * User: Renato
 */
abstract class AbstractHtmlCreator<T> {

	String css
	String outputDir
	KnowsWhenAndWhoRanTest whenAndWho = new KnowsWhenAndWhoRanTest()

	void setCss( String css ) {
		if ( !css || css.trim().empty ) return
		def cssResource = this.class.getResource( "/$css" )
		if ( cssResource )
			try {
				this.@css = cssResource.text
			} catch ( e ) {
				println "${this.class.name}: Failed to set CSS file to $css: $e"
			}
		else
			println "${this.class.name}: The CSS file does not exist: ${css}"
	}

	File createReportsDir( ) {
		def reportsDir = new File( outputDir )
		reportsDir.mkdirs()
		reportsDir
	}

	String reportFor( T data ) {
		def writer = new StringWriter()
		def builder = new MarkupBuilder( new IndentPrinter( new PrintWriter( writer ), "" ) )
		builder.expandEmptyElements = true
		builder.html {
			head {
				if ( css ) style css
			}
			body {
				h2 reportHeader( data )
				hr()
				writeSummary( builder, data )
				writeDetails( builder, data )
				hr()
				writeFooter( builder )
			}
		}
		'<!DOCTYPE html>' + writer.toString()
	}

	protected void writeFooter( MarkupBuilder builder ) {
		builder.div( 'class': 'footer' ) {
			mkp.yieldUnescaped(
					"Generated by <a href='${SpockReportExtension.PROJECT_URL}'>Athaydes Spock Reports</a>" )
		}
	}

	protected double successRate( int total, int reproved ) {
		( total > 0 ? ( total - reproved ) / total : 1.0 )
	}

	abstract protected String reportHeader( T data )

	abstract protected void writeSummary( MarkupBuilder builder, T data )

	abstract protected void writeDetails( MarkupBuilder builder, T data )

}
