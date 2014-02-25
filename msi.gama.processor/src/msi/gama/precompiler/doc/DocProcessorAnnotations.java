package msi.gama.precompiler.doc;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic.Kind;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.doc.utils.TypeConverter;
import msi.gama.precompiler.doc.utils.XMLElements;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DocProcessorAnnotations {
	
	public static org.w3c.dom.Element getDocElt(doc docAnnot, Document doc, Messager mes, String eltName, TypeConverter tc, ExecutableElement e){ 
		return DocProcessorAnnotations.getDocElt(docAnnot, doc, null, mes, eltName, tc, e);
	}
	
	public static org.w3c.dom.Element getDocElt(doc[] docAnnotTab, Document doc, Messager mes, String eltName, TypeConverter tc, ExecutableElement e){ // e.getSimpleName() 
		if(docAnnotTab == null || docAnnotTab.length == 0){
			return DocProcessorAnnotations.getDocElt(null, doc, null, mes, eltName, tc, e);
		} else {
			return DocProcessorAnnotations.getDocElt(docAnnotTab[0], doc, null, mes, eltName, tc, e);
		}
	}
	
	public static org.w3c.dom.Element getDocElt(doc docAnnot, Document doc, org.w3c.dom.Element docElement, Messager mes, String eltName, TypeConverter tc, ExecutableElement e){ // e.getSimpleName() 
		org.w3c.dom.Element docElt = docElement;

		if ( docAnnot == null ) {
			mes.printMessage(Kind.ERROR, "The element __" + eltName + "__ is not documented.");
		} else {	
			if(docElt == null){
				docElt = doc.createElement("documentation");
			} 
	
			// Parse result
			String value = docAnnot.value();
			boolean masterDoc = docAnnot.masterDoc();
			if(value != ""){
				if(docElt.getElementsByTagName("result").getLength() != 0){
					org.w3c.dom.Element resultElt = (org.w3c.dom.Element) docElt.getElementsByTagName("result").item(0);
					if(("true".equals(resultElt.getAttribute("masterDoc")) && masterDoc) || (!("true".equals(resultElt.getAttribute("masterDoc"))) && !masterDoc)){ 
						resultElt.setTextContent(resultElt.getTextContent() + value);							
					} else if (!resultElt.hasAttribute("masterDoc") && masterDoc) {
						resultElt.setTextContent(value);			
						resultElt.setAttribute("masterDoc", "true");
					} 
					// eltResult.setTextContent(eltResult.getTextContent() + value);
					//	docElt.getElementsByTagName("result").item(0)
					//		.setTextContent(docElt.getElementsByTagName("result").item(0).getTextContent() + value);					
				} else {
					org.w3c.dom.Element resultElt = doc.createElement("result");
					resultElt.setTextContent(value);
					if(masterDoc) {resultElt.setAttribute("masterDoc", "true");	}				
					docElt.appendChild(resultElt);
				}
			}
			
			// Parse comment
			String comment = docAnnot.comment();
			if(!"".equals(comment)){
				if(docElt.getElementsByTagName("comment").getLength() != 0){
					docElt.getElementsByTagName("comment").item(0)
						.setTextContent(docElt.getElementsByTagName("comment").item(0).getTextContent() + comment);					
				} else {
					org.w3c.dom.Element commentElt = doc.createElement("comment");
					commentElt.setTextContent(comment);
					docElt.appendChild(commentElt);
				}
			}
	
//			// Parse specialCases
//			org.w3c.dom.Element specialCasesElt;
//			if(docElt.getElementsByTagName("specialCases").getLength() != 0){
//				specialCasesElt = (org.w3c.dom.Element) docElt.getElementsByTagName("specialCases").item(0);				
//			} else {
//				specialCasesElt = doc.createElement("specialCases");
//			}
//			for ( String cases : docAnnot.special_cases() ) {
//				if ( !"".equals(cases) ) {
//					org.w3c.dom.Element caseElt = doc.createElement("case");
//					caseElt.setAttribute("item", cases);
//					specialCasesElt.appendChild(caseElt);
//				}
//			}
//			if(docAnnot.special_cases().length != 0) {docElt.appendChild(specialCasesElt);}
	
			// Examples are now integrated into usages
//			// Parse examples
//			org.w3c.dom.Element examplesElt;
//			if(docElt.getElementsByTagName("examples").getLength() != 0){
//				examplesElt = (org.w3c.dom.Element) docElt.getElementsByTagName("examples").item(0);				
//			} else {
//				examplesElt = doc.createElement("examples");
//			}	
//			for ( String example : docAnnot.examples() ) {
//				org.w3c.dom.Element exampleElt = doc.createElement("example");
//				exampleElt.setAttribute("code", example);
//				examplesElt.appendChild(exampleElt);
//			}
//			if(docAnnot.examples().length != 0) {docElt.appendChild(examplesElt);}
			
			// Parse: seeAlso
			org.w3c.dom.Element seeAlsoElt;
			if(docElt.getElementsByTagName("seeAlso").getLength() != 0){
				seeAlsoElt = (org.w3c.dom.Element) docElt.getElementsByTagName("seeAlso").item(0);				
			} else {
				seeAlsoElt = doc.createElement("seeAlso");
			}	
			for ( String see : docAnnot.see() ) {
				NodeList nLSee = seeAlsoElt.getElementsByTagName("see");
				int i = 0;
				boolean seeAlreadyInserted = false;
				while(i < nLSee.getLength() && !seeAlreadyInserted){
					if(((org.w3c.dom.Element) nLSee.item(i)).getAttribute("id").equals(see) ){
						seeAlreadyInserted = true;
					}
					i++;
				}
				if(!seeAlreadyInserted){
					org.w3c.dom.Element seesElt = doc.createElement("see");
					seesElt.setAttribute("id", see);
					seeAlsoElt.appendChild(seesElt);
				}
			}
			if(docAnnot.see().length != 0) {docElt.appendChild(seeAlsoElt);}
			
			// Parse: usages
			// FIXME: should replace specialCases & examples
			
			org.w3c.dom.Element usagesElt;
			org.w3c.dom.Element usagesExampleElt;
			org.w3c.dom.Element usagesNoExampleElt;
			if(docElt.getElementsByTagName(XMLElements.USAGES).getLength() != 0){
				usagesElt = (org.w3c.dom.Element) docElt.getElementsByTagName(XMLElements.USAGES).item(0);				
			} else {
				usagesElt = doc.createElement(XMLElements.USAGES);
			}	
			if(docElt.getElementsByTagName("usagesExamples").getLength() != 0){
				usagesExampleElt = (org.w3c.dom.Element) docElt.getElementsByTagName("usagesExamples").item(0);				
			} else {
				usagesExampleElt = doc.createElement("usagesExamples");
			}	
			if(docElt.getElementsByTagName("usagesNoExample").getLength() != 0){
				usagesNoExampleElt = (org.w3c.dom.Element) docElt.getElementsByTagName("usagesNoExample").item(0);				
			} else {
				usagesNoExampleElt = doc.createElement("usagesNoExample");
			}			
			int numberOfUsages = 0;			
			int numberOfUsagesWithExamplesOnly = 0;
			int numberOfUsagesWithoutExample = 0;
			for ( usage usage : docAnnot.usages() ) {
				org.w3c.dom.Element usageElt = doc.createElement(XMLElements.USAGE);			
				
				// Among the usages, we consider the ones without value
				if ("".equals(usage.value())) {
					numberOfUsagesWithExamplesOnly++;

					org.w3c.dom.Element examplesUsageElt = DocProcessorAnnotations.getExamplesElt(usage.examples(), doc, e, tc);
					usageElt.appendChild(examplesUsageElt);
					usagesExampleElt.appendChild(usageElt);				
				} 
				// Among the usages, we consider the ones with only the value				
				else if(usage.examples().length == 0){
					numberOfUsagesWithoutExample++;
					
					usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
					usagesNoExampleElt.appendChild(usageElt);						
				}
				// Otherwise, when we have both value and examples
				else {
					numberOfUsages++;	
					
					usageElt.setAttribute(XMLElements.ATT_USAGE_DESC, usage.value());
					org.w3c.dom.Element examplesUsageElt = DocProcessorAnnotations.getExamplesElt(usage.examples(), doc, e, tc);
					usageElt.appendChild(examplesUsageElt);
					usagesElt.appendChild(usageElt);					
				}
			}
			// Let's continue with examples and special cases
			//  - special cases are equivalent to usage without examples
			//  - examples are equivalent to usage with only examples
			// Parse examples	
			if (docAnnot.examples().length != 0) {
				org.w3c.dom.Element usageExElt = doc.createElement(XMLElements.USAGE);
				org.w3c.dom.Element examplesElt = doc.createElement(XMLElements.EXAMPLES);	
				
				for ( String example : docAnnot.examples() ) {
					org.w3c.dom.Element exampleElt = doc.createElement(XMLElements.EXAMPLE);
					exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_CODE, example);
					exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TEST_ONLY, "false");
					exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_EXECUTABLE, "true");
					
					examplesElt.appendChild(exampleElt);	

					numberOfUsagesWithExamplesOnly++;				
				}
				usageExElt.appendChild(examplesElt);
				usagesExampleElt.appendChild(usageExElt);				
			}
							
			
			// Parse specialCases
			for ( String cases : docAnnot.special_cases() ) {
				if ( !"".equals(cases) ) {
					org.w3c.dom.Element caseElt = doc.createElement(XMLElements.USAGE);
					caseElt.setAttribute(XMLElements.ATT_USAGE_DESC, cases);
					usagesNoExampleElt.appendChild(caseElt);
					numberOfUsagesWithoutExample++;
				}
			}
			
			if(numberOfUsagesWithExamplesOnly != 0) {docElt.appendChild(usagesExampleElt);}
			if(numberOfUsagesWithoutExample != 0) {docElt.appendChild(usagesNoExampleElt);}
			if(numberOfUsages != 0) {docElt.appendChild(usagesElt);}
			
//			public static @interface usages {
//				String value() default "";
//				String[] examples() default {};}
			
		}
		return docElt;
	}

	public static org.w3c.dom.Element getExamplesElt(example[] examples, Document doc, ExecutableElement e, TypeConverter tc){ 
		org.w3c.dom.Element examplesElt = doc.createElement(XMLElements.EXAMPLES);			
		for ( example example : examples ) { 
			examplesElt.appendChild(getExampleElt(example, doc, e, tc));
		}
		return examplesElt;		
	}	
	
	public static org.w3c.dom.Element getExampleElt(example example, Document doc, ExecutableElement e, TypeConverter tc){ 
		org.w3c.dom.Element exampleElt = doc.createElement(XMLElements.EXAMPLE);			
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_CODE, example.value());
		if(!"".equals(example.var())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_VAR, example.var());}
		if(!"".equals(example.equals())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_EQUALS, example.equals());}
		if(!"".equals(example.isNot())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_NOT, example.isNot());}
		if(!"".equals(example.raises())) {exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_RAISES, example.raises());}
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_TEST_ONLY, ""+example.isTestOnly());
		exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_IS_EXECUTABLE, ""+example.isExecutable());
		if (e != null){
			exampleElt.setAttribute(XMLElements.ATT_EXAMPLE_TYPE, tc.getProperType(e.getReturnType().toString()));
		}

		return exampleElt;
//		public static @interface example {
//			String value() default "";
//			String var() default "";			
//			String equals() default "";
//			String isnot() default "";
//			String raises() default "";	
//			boolean isTestOnly() default false;		
// 			boolean isExecutable() default true;		
//		}		
	}		
	
	
	public static org.w3c.dom.Element getActionElt(action actionAnnot, Document doc, Messager mes, Element e, TypeConverter tc){
		if((!(e instanceof ExecutableElement)) || (actionAnnot == null)){
			return null;
		}
		
		ExecutableElement eltMethod = (ExecutableElement) e;
		org.w3c.dom.Element actionElt = doc.createElement("action");
		actionElt.setAttribute("name", actionAnnot.name());
		actionElt.setAttribute("returnType", tc.getProperType(eltMethod.getReturnType().toString())); 

		org.w3c.dom.Element argsElt = doc.createElement("args");
		for (arg eltArg : actionAnnot.args()){
			org.w3c.dom.Element argElt = doc.createElement("arg");
			argElt.setAttribute("name", eltArg.name());
			
			String tabType = "";
			for(int i = 0; i < eltArg.type().length ; i++){
				// tabType = tabType + ((i < eltArg.type().length - 1) ? typeStringFromIType.get(eltArg.type()[i]) + "," : typeStringFromIType.get(eltArg.type()[i]));
				tabType = tabType + ((i < eltArg.type().length - 1) ? tc.getTypeString(eltArg.type()[i]) + "," : tc.getTypeString(eltArg.type()[i]));
			}
			argElt.setAttribute("type", tabType);
			argElt.setAttribute("optional", ""+eltArg.optional());
			org.w3c.dom.Element docEltArg = 
					DocProcessorAnnotations.getDocElt(eltArg.doc(), doc, mes, "Arg " + eltArg.name() + " from " + eltMethod.getSimpleName(), tc, null);
			if(docEltArg != null){
				argElt.appendChild(docEltArg);
			}
			
			argsElt.appendChild(argElt);
		}
		actionElt.appendChild(argsElt);		
		
		org.w3c.dom.Element docEltAction = DocProcessorAnnotations.getDocElt(actionAnnot.doc(), doc, mes, eltMethod.getSimpleName().toString(), tc, null);
		if(docEltAction != null){
			actionElt.appendChild(docEltAction);
		}
		
		return actionElt;
	}
	
	public static org.w3c.dom.Element getFacetsElt(facets facetsAnnot, Document doc, Messager mes, String statName, TypeConverter tc){
		if(facetsAnnot == null){
			return null;
		}
		
		org.w3c.dom.Element facetsElt = doc.createElement("facets");

		for ( facet f : facetsAnnot.value() ) {
			org.w3c.dom.Element facetElt = doc.createElement("facet");
			facetElt.setAttribute("name", f.name());
			// TODO : check several types
			facetElt.setAttribute("type", String.valueOf(f.type()[0]));
			facetElt.setAttribute("optional", "" + f.optional());
			facetElt.setAttribute("omissible",
				f.name().equals(facetsAnnot.omissible()) ? "true" : "false");
			org.w3c.dom.Element docFacetElt = 
					DocProcessorAnnotations.getDocElt(f.doc(), doc, mes, "Facet " + f.name() + " from Statement" + statName, tc, null);
			if(docFacetElt != null){
				facetElt.appendChild(docFacetElt);
			}
			
			facetsElt.appendChild(facetElt);
		}
		return facetsElt;
	}
	
	public static org.w3c.dom.Element getInsideElt(inside insideAnnot, Document doc){
		if(insideAnnot == null){
			return null;
		}
		
		org.w3c.dom.Element insideElt = doc.createElement("inside");
		
		org.w3c.dom.Element symbolsElt = doc.createElement("symbols");
		for(String sym : insideAnnot.symbols()){
			org.w3c.dom.Element symElt = doc.createElement("symbol");
			symElt.setTextContent(sym);
			symbolsElt.appendChild(symElt);
		}
		insideElt.appendChild(symbolsElt);

		org.w3c.dom.Element kindsElt = doc.createElement("kinds");
		for(int kind : insideAnnot.kinds()){
			org.w3c.dom.Element kindElt = doc.createElement("kind");
			kindElt.setTextContent(""+kind);
			kindsElt.appendChild(kindElt);
		}
		insideElt.appendChild(kindsElt);

		
		return insideElt;
	}

	public static org.w3c.dom.Element getOperatorElement(final org.w3c.dom.Element operators, final String eltName) {
			NodeList nL = operators.getElementsByTagName("operator");
			int i = 0;
			boolean found = false;
			while (!found && i < nL.getLength()) {
				org.w3c.dom.Element elt = (org.w3c.dom.Element) nL.item(i);
				if ( eltName.equals(elt.getAttribute("id")) ) { return elt; }
				i++;
			}
			return null;
		}
}