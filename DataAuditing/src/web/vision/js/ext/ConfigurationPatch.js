var ConfigurationPatch = {
	extensionPoints : {
		css : [ '/vision/css/accordion.css' ],
		catalogElementTypes : {
			'WORKFLOW' : '${ProcessDefinition}'
		},
		CatalogTree : {
			exportChooser : {
				checkableTypes : [ 'WORKFLOW' ],
				typeCanAddReference : [ 'WORKFLOW' ]
			},
			copyableTypes : {
				'WORKFLOW' : true
			},
			dragDropHandler : [ {
				className : 'smartbi.flow.handler.DragDropHandler'
			} ],
			BusinessViewCatalogTree_DenyTypes : [ 'WORKFLOW' ]
		},
		Metadata : {
			MResTreeNodeChooser : {
				analysisNodeType : {
					WORKFLOW : "${WorkFlow}"
				}
			}
		},
		Macro : {
			MacroIDETreePopupMenuHandler : {
				analysisNodeTypes : {
					'WORKFLOW' : '${WorkFlow}'
				},
				filterTypes : [ 'WORKFLOW' ]
			},
			SimpleReportRuleTree : {
				filterTypes : [ "WORKFLOW" ]
			},
			simpleReportRule : {
				filterTypes : [ 'WORKFLOW' ],
				analysisTypes : [ 'WORKFLOW' ]
			},
			MacroExecutor : [ {
				resourceType : "WORKFLOW",
				className : "smartbi.flow.FlowMacroExecutor"
			} ]
		},
		SuperviseTree : {
			handlers : [ {
				className : 'smartbi.flow.handler.SpreadsheetReportDataAuditingHandler'
			} ]
		}
	}
};