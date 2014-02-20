package org.jboss.tools.aerogear.hybrid.core.engine;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.internal.util.ConfigJSon;
import org.jboss.tools.aerogear.hybrid.engine.internal.cordova.CordovaEngineProvider;

public class HybridMobileEngineManager {
	
	private final HybridProject project;
	
	public HybridMobileEngineManager(HybridProject project){
		this.project = project;
	}

	public HybridMobileEngine getActiveEngine(){
		try{
			ConfigJSon configJson = ConfigJSon.readConfigJson(project.getProject());
			if(configJson == null || configJson.getEngineId() == null ){
				HybridCore.log(IStatus.WARNING, "No engine information on the config.json, this is OK if using an old project. Falling back to default engine.",null );
				return defaultEngine();
			}
			if(CordovaEngineProvider.CORDOVA_ENGINE_ID.equals(configJson.getEngineId())){
				CordovaEngineProvider engineProvider = new CordovaEngineProvider();
				HybridMobileEngine engine = engineProvider.getEngine(configJson.getEngineId(),configJson.getEngineVersion());
				return engine;
			}
			
		} catch (CoreException e) {
			HybridCore.log(IStatus.WARNING, "No existing engines can be created", e);
		}
		HybridCore.log(IStatus.WARNING, "Could not determine the engine used, falling back to default engine", null);
		return defaultEngine();
	}

	private static HybridMobileEngine defaultEngine() {
        CordovaEngineProvider engineProvider = new CordovaEngineProvider();
        HybridMobileEngine engine =  engineProvider.createEngine(CordovaEngineProvider.CORDOVA_ENGINE_ID,"3.1.0");
        return engine;
	}

	public void updateEngine(HybridMobileEngine engine) throws CoreException{
		ConfigJSon configJSon = ConfigJSon.readConfigJson(project.getProject());
		if(configJSon == null) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "config.json does not exist or can not be parsed. Updating the hybrid mobile engine for the project " + project.getProject().getName() +" failed."));
		}
		configJSon.setEngineInfo(engine);
		configJSon.persist(project.getProject());
	}

}
