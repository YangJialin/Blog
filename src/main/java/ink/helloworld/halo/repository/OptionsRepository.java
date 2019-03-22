package ink.helloworld.halo.repository;

import ink.helloworld.halo.model.domain.Options;
import ink.helloworld.halo.repository.base.BaseRepository;

/**
 * <pre>
 *     系统设置持久层
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2017/11/14
 */
public interface OptionsRepository extends BaseRepository<Options, String> {

    /**
     * 根据key查询单个option
     *
     * @param key key
     * @return Options
     */
    Options findOptionsByOptionName(String key);
}
