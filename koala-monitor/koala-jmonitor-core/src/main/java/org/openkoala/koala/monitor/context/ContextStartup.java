package org.openkoala.koala.monitor.context;

import java.util.Map;

import org.openkoala.koala.monitor.config.PersistManager;
import org.openkoala.koala.monitor.core.RuntimeContext;
import org.openkoala.koala.monitor.exception.ContextInitialException;
import org.openkoala.koala.monitor.jwebap.NodeDef;

/**
 * 监控Context启动器
 * @author Administrator
 *
 */
public class ContextStartup {

	/**
	 * 启动监控contentext
	 * @param configPath
	 * @param serverInfos
	 */
	public static void startup(String configPath,Map<String, String> serverInfos)  {
		NodeDef config = null;
		PersistManager defManager=new PersistManager(configPath);
		
		try {
			config = defManager.get();
		} catch (Exception e) {
			throw new ContextInitialException("",e);
		}
		

		RuntimeContext context = RuntimeContext.registerContext(config,defManager);
		context.registerServerInfos(serverInfos);
		context.startup();
	}
}