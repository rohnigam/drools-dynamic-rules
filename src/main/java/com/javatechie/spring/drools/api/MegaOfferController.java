package com.javatechie.spring.drools.api;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MegaOfferController {
//	@Autowired
//	private KieSession session;

	@PostMapping("/order")
	public Order orderNow(@RequestBody Order order) {


//		session.insert(order);
//		session.fireAllRules();

		KieSession customSession = getCustomSessionForClient(order.getClientType());

		customSession.insert(order);
		customSession.fireAllRules();
		return order;
	}

	private KieSession getCustomSessionForClient(String clientType) {
		String filename = getRulesFileForClientType(clientType);

		KieServices kieServices = KieServices.Factory.get();
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
//		kieFileSystem.write(ResourceFactory.newClassPathResource(filename));
		kieFileSystem.write(ResourceFactory.newFileResource(filename));
		System.out.println("Container created...");
		getKieRepository(kieServices);
		KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
		kb.buildAll();
		KieModule kieModule = kb.getKieModule();
		KieContainer kContainer = kieServices.newKieContainer(kieModule.getReleaseId());
		KieSession customSession = kContainer.newKieSession();

		return customSession;
	}

	private String getRulesFileForClientType(String clientType) {
		String filename = "";

		try {
			InputStream getLocalJsonFile = new FileInputStream(
				"/Users/rohitashwanigam/repos/mine/drools-dynamic-rules/rulesConfigForClient.json");
			HashMap<String,String> rulesConfigForClient = new ObjectMapper().readValue(getLocalJsonFile, HashMap.class);
			if (rulesConfigForClient.containsKey(clientType)) {
				filename = rulesConfigForClient.get(clientType);
			} else {
				filename = rulesConfigForClient.get("default");
			}
		} catch (Exception ex) {
			System.out.println(ex);
		}

		return filename;
	}

	private void getKieRepository(KieServices kieServices) {
		final KieRepository kieRepository = kieServices.getRepository();
		kieRepository.addKieModule(new KieModule() {
			public ReleaseId getReleaseId() {
				return kieRepository.getDefaultReleaseId();
			}
		});
	}

}
