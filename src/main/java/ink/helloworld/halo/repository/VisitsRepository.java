package ink.helloworld.halo.repository;

import ink.helloworld.halo.model.domain.Logs;
import ink.helloworld.halo.model.domain.Visits;
import ink.helloworld.halo.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * <pre>
 *     访问监控持久层
 * </pre>
 *
 * @author : Gallin
 * @date : 2018/3/22
 */
public interface VisitsRepository extends BaseRepository<Visits, Long> {

    /**
     * 查询最新的五条数据
     *
     * @return List
     */
    @Query(value = "SELECT * FROM halo_visits ORDER BY visit_time DESC LIMIT 5", nativeQuery = true)
    List<Visits> findTopFive();
}
