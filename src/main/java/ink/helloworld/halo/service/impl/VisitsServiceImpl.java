package ink.helloworld.halo.service.impl;

import cn.hutool.extra.servlet.ServletUtil;
import ink.helloworld.halo.model.domain.Logs;
import ink.helloworld.halo.model.domain.Visits;
import ink.helloworld.halo.repository.LogsRepository;
import ink.helloworld.halo.repository.VisitsRepository;
import ink.helloworld.halo.service.LogsService;
import ink.helloworld.halo.service.VisitsService;
import ink.helloworld.halo.service.base.AbstractCrudService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <pre>
 *     访问监控逻辑接口实现类
 * </pre>
 *
 * @author : Gallin
 * @date : 2019/3/22
 */
@Service
public class VisitsServiceImpl extends AbstractCrudService<Visits, Long> implements VisitsService {

    private final VisitsRepository visitsRepository;

    public VisitsServiceImpl(VisitsRepository visitsRepository) {
        super(visitsRepository);
        this.visitsRepository = visitsRepository;
    }


    /**
     * 保存日志
     *
     * @param visitIp   visitIp
     * @param visitUserAgent visitUserAgent
     */
    @Override
    public void save(String visitIp, String visitBrowser,String visitOs,String url,String visitUserAgent) {
        final Visits visits = new Visits();
        visits.setVisitIp(visitIp);
        visits.setVisitBrowser(visitBrowser);
        visits.setVisitOs(visitOs);
        visits.setUrl(url);
        visits.setVisitUserAgent(visitUserAgent);
        visitsRepository.save(visits);
    }

    /**
     * 查询最新的五条日志
     *
     * @return List
     */
    @Override
    public List<Visits> findVisitsLatest() {
        return visitsRepository.findTopFive();
    }
}
