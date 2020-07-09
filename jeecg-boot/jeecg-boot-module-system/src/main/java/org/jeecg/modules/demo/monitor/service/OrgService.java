package org.jeecg.modules.demo.monitor.service;
import java.util.List;
import java.util.Map;

public interface OrgService {

    List<Map<String, Object>> findOrgUserTree(int pid);

}
