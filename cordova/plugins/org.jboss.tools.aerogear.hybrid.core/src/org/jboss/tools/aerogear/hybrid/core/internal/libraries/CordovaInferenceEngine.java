package org.jboss.tools.aerogear.hybrid.core.internal.libraries;

import org.eclipse.wst.jsdt.core.ast.IAssignment;
import org.eclipse.wst.jsdt.core.ast.IExpression;
import org.eclipse.wst.jsdt.core.ast.IFunctionCall;
import org.eclipse.wst.jsdt.core.ast.IStringLiteral;
import org.eclipse.wst.jsdt.core.infer.InferEngine;
import org.eclipse.wst.jsdt.core.infer.InferredType;
import org.eclipse.wst.jsdt.internal.compiler.ast.CompilationUnitDeclaration;

public class CordovaInferenceEngine extends InferEngine {
	
	private CompilationUnitDeclaration unit;
	
	
	@Override
	public void setCompilationUnit(
			CompilationUnitDeclaration scriptFileDeclaration) {
		this.unit = scriptFileDeclaration;
		super.setCompilationUnit(scriptFileDeclaration);
	}
	
	@Override
	public boolean visit(IFunctionCall functionCall) {
		char[] sChars = functionCall.getSelector();
		if(sChars != null && (new String(sChars)).equals("define") 
				&& functionCall.getArguments()[0] instanceof IStringLiteral){
			IStringLiteral arg0  = (IStringLiteral) functionCall.getArguments()[0];
			InferredType type = unit.findInferredType(arg0.source());
			if(type == null){
				type = addType(arg0.source(), true);
				type.updatePositions(functionCall.sourceStart(), functionCall.sourceEnd());
			}
			pushContext();
			
			return true;
		}
		
		
		return super.visit(functionCall);
	}
	
	@SuppressWarnings("restriction")
	@Override
	public boolean visit(IAssignment assignment) {
		IExpression assignmentExpr = assignment.getExpression();
		if( assignmentExpr instanceof IFunctionCall ){
			IFunctionCall fCall = (IFunctionCall) assignmentExpr;
			if(fCall.getSelector() != null && new String(fCall.getSelector()).equals("require")){
				if( fCall.getArguments()[0] instanceof IStringLiteral){
					IStringLiteral objName = (IStringLiteral)fCall.getArguments()[0];
					InferredType type = unit.findInferredType(objName.source());
					if(type == null ){
						type  = addType(objName.source());
					}
					assignment.setInferredType(type);
				}
			}
		}
		
		return super.visit(assignment);
	}


}
