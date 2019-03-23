package ink.helloworld.halo.service;

import ink.helloworld.halo.model.domain.Logs;
import ink.helloworld.halo.model.domain.Visits;
import ink.helloworld.halo.service.base.CrudService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <pre>
 *     访问监控逻辑接口
 * </pre>
 *
 * @author : Gallin
 * @date : 2019/3/22
 */
public interface VisitsService extends CrudService<Visits, Long> {

    /**
     * 保存记录
     *
     * @param visitIp   visitIp
     * @param visitBrowser visitBrowser
     * @param url    url
     */
    void save(String visitIp, String visitBrowser,String visitOs,String url,String visitUserAgent);

    /**
     * 查询最新的五条记录
     *
     * @return List
     */
    List<Visits> findVisitsLatest();
}
