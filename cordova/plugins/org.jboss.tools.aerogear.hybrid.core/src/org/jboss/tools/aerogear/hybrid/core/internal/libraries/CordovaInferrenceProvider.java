package org.jboss.tools.aerogear.hybrid.core.internal.libraries;

import org.eclipse.wst.jsdt.core.infer.IInferEngine;
import org.eclipse.wst.jsdt.core.infer.IInferenceFile;
import org.eclipse.wst.jsdt.core.infer.InferrenceProvider;
import org.eclipse.wst.jsdt.core.infer.RefactoringSupport;
import org.eclipse.wst.jsdt.core.infer.ResolutionConfiguration;

public class CordovaInferrenceProvider implements InferrenceProvider {
	public static final String ID="org.jboss.tools.aerogear.hybrid.core.CordovaInferenceProvider";

	public CordovaInferrenceProvider() {
	}

	@Override
	public IInferEngine getInferEngine() {
		CordovaInferEngine2 engine = new CordovaInferEngine2();
		engine.inferenceProvider = this;
		return engine;
	}

	@Override
	public int applysTo(IInferenceFile scriptFile) {
		return MAYBE_THIS; 
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public ResolutionConfiguration getResolutionConfiguration() {
		return new ResolutionConfiguration();
	}

	@Override
	public RefactoringSupport getRefactoringSupport() {
		// TODO Auto-generated method stub
		return null;
	}

}
