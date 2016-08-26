package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Table(name="FOLDER")
class FilesFolder extends Nameable {

    @ManyToOne @DescriptionsList
    FilesFolder parent // parent, not filesFolder, to test a case

    @OneToMany(cascade=CascadeType.ALL, mappedBy="parent")
    Collection<FilesFolder> subfolders

    @OneToMany(mappedBy="folder")
    Collection<File> files
	
	@PreRemove
	private void detachFiles() {
		files.each() { it.folder = null }
	}    

}