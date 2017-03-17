package org.openxava.test.actions

import javax.persistence.*

import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.data.*

import org.openxava.actions.*
import org.openxava.jpa.*;
import org.openxava.test.model.*

/**
 * To print a datasheet of Movie concatenating simple reports. <p>
 * 
 * @author Jeromy Altuna
 */
class MovieReportAction extends JasperConcatReportBaseAction {
	
	Movie movie	
	JRDataSource[] dataSources
	
	@Override
	protected JRDataSource[] getDataSources() throws Exception {
		if(dataSources == null) {
			Collection images = getImages getMovie().photographs
			dataSources = [ 
				new JRBeanCollectionDataSource([images[0]]),
				new JRBeanCollectionDataSource([null]),
				new JRBeanCollectionDataSource(images[1..-1])
			]			
		}
		dataSources
	}

	@Override
	protected String[] getJRXMLs() throws Exception {
		"Images.jrxml:Film.jrxml:Images.jrxml".split ":"
	}
	
	@Override
	protected Map getParameters(int i) throws Exception {
		switch (i) {
			case 1:
				def parameters = [
					"title"    : getMovie().title, 
					"director" : getMovie().director,
					"writers"  : getMovie().writers,
					"starring" : getMovie().starring
				]
				return parameters
		}
		return null
	}
	
	Movie getMovie() {		
		if(movie == null) {
			movie = Movie.findById(getView().getValue("id"))
		}
		movie
	}
	
	Collection getImages(String galleryImage) {
		Query query = XPersistence.getManager().createQuery "from GalleryImage where galleryOid=:galleryOid"
		query.setParameter "galleryOid", galleryImage
		query.getResultList()
	}
}
