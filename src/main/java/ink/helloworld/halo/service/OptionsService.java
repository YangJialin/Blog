package ink.helloworld.halo.service;

import ink.helloworld.halo.model.domain.Options;
import ink.helloworld.halo.service.base.CrudService;

import java.util.Map;

/**
 * <pre>
 *     系统设置业务逻辑接口
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2017/11/14
 */
public interface OptionsService extends CrudService<Options, String> {

    /**
     * 保存单个设置选项
     *
     * @param key   key
     * @param value value
     */
    void saveOption(String key, String value);

    /**
     * 保存多个设置选项
     *
     * @param options options
     */
    void saveOptions(Map<String, String> options);

    /**
     * 获取所有设置选项
     *
     * @return Map
     */
    Map<String, String> findAllOptions();

    /**
     * 根据key查询单个设置
     *
     * @param key key
     * @return String
     */
    String findOneOption(String key);
}
