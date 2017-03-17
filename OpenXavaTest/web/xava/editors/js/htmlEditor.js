openxava.addEditorInitFunction(function() {
	var config = { 
		language: openxava.language,
		uiColor: '#F5F5F5'
	};
	$('.ox-ckeditor').ckeditor(config);
	
	var simpleConfig = { 
		language: openxava.language,
		uiColor: '#F5F5F5',
		toolbarGroups: [
			{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] },
			{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
			{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
			{ name: 'forms', groups: [ 'forms' ] },
			{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
			{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
			{ name: 'links', groups: [ 'links' ] },
			{ name: 'insert', groups: [ 'insert' ] },
			{ name: 'styles', groups: [ 'styles' ] },
			{ name: 'colors', groups: [ 'colors' ] },
			{ name: 'tools', groups: [ 'tools' ] },
			{ name: 'others', groups: [ 'others' ] },
			{ name: 'about', groups: [ 'about' ] }
		],
		removeButtons: 'Save,Templates,Cut,NewPage,Preview,Print,Copy,Paste,PasteText,PasteFromWord,Find,Replace,SelectAll,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,CreateDiv,Language,Anchor,Flash,PageBreak,Iframe,ShowBlocks,About,Undo,Redo,Subscript,Superscript,BidiLtr,BidiRtl,SpecialChar,Styles,Format,Font,FontSize'
	};
	$('.ox-simple-ckeditor').ckeditor(simpleConfig);
});


openxava.addEditorDestroyFunction(function() {
	for (var instance in CKEDITOR.instances) {
		CKEDITOR.instances[instance].destroy(false); // Needs to be false, otherwise calculated properties in the same view reset editor content
	}
});





